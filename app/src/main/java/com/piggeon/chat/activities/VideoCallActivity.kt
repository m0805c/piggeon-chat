package com.piggeon.chat.activities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.piggeon.chat.R
import com.piggeon.chat.databinding.ActivityVideoCallBinding
import com.piggeon.chat.util.toast
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Actividad 8: videollamada.
 * Muestra la vista previa de la cámara con CameraX y controles para silenciar,
 * apagar la cámara y terminar. El estado pasa por: llamando -> conectado -> finalizado.
 * Nota: sin Firebase no hay transmisión real; el micro y la cámara se controlan en local.
 */
class VideoCallActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CONTACT_NAME = "extra_contact_name"
    }

    private enum class CallState { CALLING, CONNECTED, ENDED }

    private lateinit var binding: ActivityVideoCallBinding
    private var callState = CallState.CALLING
    private var micOn = true
    private var cameraOn = true
    private var cameraProvider: ProcessCameraProvider? = null

    // Pide permisos de cámara y micrófono; si la cámara se concede, arranca la vista previa
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        if (results[Manifest.permission.CAMERA] == true) {
            startCamera()
        } else {
            toast(R.string.permission_denied)
            binding.tvCameraOff.visibility = View.VISIBLE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoCallBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val contactName = intent.getStringExtra(EXTRA_CONTACT_NAME).orEmpty()
        binding.tvContactName.text = getString(R.string.call_with, contactName)

        binding.btnMute.setOnClickListener { toggleMic() }
        binding.btnCamera.setOnClickListener { toggleCamera() }
        binding.btnEndCall.setOnClickListener { endCall() }

        // Simula que el contacto contesta a los 2 segundos
        setCallState(CallState.CALLING)
        lifecycleScope.launch {
            delay(2000)
            if (callState == CallState.CALLING) setCallState(CallState.CONNECTED)
        }

        requestPermissionsAndStartCamera()
    }

    private fun requestPermissionsAndStartCamera() {
        val cameraGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_GRANTED
        if (cameraGranted) {
            startCamera()
        } else {
            permissionLauncher.launch(arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO))
        }
    }

    /** Inicia CameraX y obtiene el proveedor de cámara. */
    private fun startCamera() {
        val future = ProcessCameraProvider.getInstance(this)
        future.addListener({
            cameraProvider = future.get()
            bindCamera()
        }, ContextCompat.getMainExecutor(this))
    }

    /** Vincula la vista previa de la cámara frontal, o la quita si la cámara está apagada. */
    private fun bindCamera() {
        binding.tvCameraOff.visibility = if (cameraOn) View.GONE else View.VISIBLE
        val provider = cameraProvider ?: return

        provider.unbindAll()
        if (!cameraOn) return

        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(binding.previewView.surfaceProvider)
        }
        provider.bindToLifecycle(this, CameraSelector.DEFAULT_FRONT_CAMERA, preview)
    }

    private fun toggleMic() {
        micOn = !micOn
        binding.btnMute.text = getString(if (micOn) R.string.btn_mute else R.string.btn_unmute)
    }

    private fun toggleCamera() {
        cameraOn = !cameraOn
        binding.btnCamera.text = getString(if (cameraOn) R.string.btn_camera_off else R.string.btn_camera_on)
        bindCamera()
    }

    private fun endCall() {
        setCallState(CallState.ENDED)
        lifecycleScope.launch {
            delay(1000) // Deja ver el estado "Finalizado" un momento
            finish()
        }
    }

    /** Actualiza el texto de estado: llamando, conectado o finalizado. */
    private fun setCallState(newState: CallState) {
        callState = newState
        binding.tvStatus.text = getString(
            when (newState) {
                CallState.CALLING -> R.string.call_calling
                CallState.CONNECTED -> R.string.call_connected
                CallState.ENDED -> R.string.call_ended
            }
        )
    }
}
