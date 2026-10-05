# Piggeon Chat

App de mensajería y videollamadas para Android, escrita en Kotlin con XML y Room. Personalizada con colores morado y blanco y totalmente en español.

## Funciones

| Actividad | Qué hace | Dónde está |
|---|---|---|
| 1 y 2 | Crear usuario y confirmar contraseña | `activities/RegisterActivity.kt` |
| 3 | Verificación de correo por código | `activities/VerifyEmailActivity.kt` |
| 4 | Inicio y cierre de sesión | `activities/LoginActivity.kt`, `HomeActivity.kt` |
| 5 | Videollamada accesible desde Chat e Inicio | `activities/VideoCallActivity.kt`, `ChatActivity.kt` |
| 6 | Roles de usuario (el primero registrado es administrador) | `models/User.kt`, `HomeActivity.kt` |
| 7 | Lista de chats, mensajes con hora, no leídos | `ChatActivity.kt`, `adapters/` |
| 8 | Videollamada con cámara y controles | `activities/VideoCallActivity.kt` |

## Estructura

```
app/src/main/java/com/piggeon/chat/
├── activities/   Pantallas
├── adapters/     RecyclerView (chats y mensajes)
├── models/       User, Conversation, Message (entidades de Room)
├── data/         Room (AppDatabase, DAOs), ChatRepository, SessionManager
└── util/         Toast y hash de contraseñas
app/src/main/res/
├── layout/       XML de las pantallas
├── values/       Textos en español, colores (morado/blanco) y tema
```

## Cómo abrirlo

1. Abrir la carpeta del proyecto en Android Studio (Ladybug o más reciente).
2. Esperar a que Gradle sincronice.
3. Ejecutar en un emulador o dispositivo con Android 7.0 (API 24) o superior.

## Pendiente

- **Firebase**: la app funciona en local (Room). Cuando se conecte Firebase, se cambiarán
  las llamadas de `ChatRepository` y el registro/login para usar Firebase Auth y Firestore.
- **Verificación de correo**: en modo demo el código se muestra en pantalla.
- **Videollamada**: se ve la cámara y los estados, pero no hay transmisión real hasta tener señalización (Firebase).
