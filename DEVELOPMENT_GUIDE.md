# 📱 SoftFocus Mobile - Guía de Desarrollo

> Guía  sobre la arquitectura, estructura y convenciones del proyecto SoftFocus Mobile.

---

## 📚 Tabla de Contenidos

1. [Navegación y Rutas](#-navegación-y-rutas)
2. [Constantes de API](#-constantes-de-api)
3. [Temas y Estilos](#-temas-y-estilos)
4. [Tipos de Usuario](#-tipos-de-usuario)
5. [Arquitectura del Proyecto](#-arquitectura-del-proyecto)
6. [Buenas Prácticas](#-buenas-prácticas)

---

## 🗺️ Navegación y Rutas

### Estructura Modular

La navegación está dividida en **7 archivos** organizados por tipo de usuario y responsabilidad:

```
core/navigation/
├── AppNavigation.kt          ← Orquestador principal (66 líneas)
├── Route.kt                  ← Definición de todas las rutas
├── AuthNavigation.kt         ← Rutas de autenticación (pre-login)
├── SharedNavigation.kt       ← Rutas compartidas (post-login)
├── GeneralNavigation.kt      ← Rutas específicas de usuarios GENERAL
├── PatientNavigation.kt      ← Rutas específicas de usuarios PATIENT
├── PsychologistNavigation.kt ← Rutas específicas de usuarios PSYCHOLOGIST
└── AdminNavigation.kt        ← Rutas específicas de usuarios ADMIN
```

---

### 1. AppNavigation.kt (Orquestador Principal)

**Responsabilidad:** Coordinar todas las navegaciones según el tipo de usuario.

```kotlin
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val userSession = remember { UserSession(context) }
    val currentUser = userSession.getUser()

    NavHost(
        navController = navController,
        startDestination = Route.Splash.path
    ) {
        // Rutas de autenticación (disponibles para todos)
        authNavigation(navController, context)

        // Rutas compartidas (General, Patient, Psychologist)
        sharedNavigation(navController, context)

        // Rutas específicas por tipo de usuario
        when (currentUser?.userType) {
            UserType.GENERAL -> generalNavigation(navController, context)
            UserType.PATIENT -> {
                generalNavigation(navController, context)
                patientNavigation(navController, context)
            }
            UserType.PSYCHOLOGIST -> psychologistNavigation(navController, context)
            UserType.ADMIN -> adminNavigation(navController, context)
            else -> { /* Sin rutas adicionales */ }
        }
    }
}
```

**⚠️ NO modifiques este archivo** a menos que necesites cambiar la lógica de orquestación.

---

### 2. Route.kt (Definición de Rutas)

**Responsabilidad:** Definir todas las rutas disponibles en la app.

```kotlin
sealed class Route(val path: String) {
    // Auth routes
    data object Splash : Route("splash")
    data object Login : Route("login")
    data object Register : Route("register")

    // Main app routes
    data object Home : Route("home")
    data object Profile : Route("profile")
    data object Notifications : Route("notifications")

    // AI routes
    data object AIWelcome : Route("ai_welcome")
    data object AIChat : Route("ai_chat_screen/{initialMessage}?sessionId={sessionId}") {
        fun createRoute(initialMessage: String? = null, sessionId: String? = null): String {
            // Lógica de construcción de ruta
        }
    }

    // Admin routes
    data object AdminUsers : Route("admin_users")
}
```

#### ¿Cómo agregar una nueva ruta?

```kotlin
// 1. Define la ruta en Route.kt
sealed class Route(val path: String) {
    // ... otras rutas
    data object MyNewScreen : Route("my_new_screen")
}

// 2. Agrégala al archivo de navegación correspondiente
// Ejemplo: Si es para Psychologist → PsychologistNavigation.kt
fun NavGraphBuilder.psychologistNavigation(
    navController: NavHostController,
    context: Context
) {
    composable(Route.MyNewScreen.path) {
        MyNewScreen(
            onNavigateBack = { navController.popBackStack() }
        )
    }
}
```

---

### 3. AuthNavigation.kt (Pre-Login)

**Contenido:** Rutas accesibles ANTES de iniciar sesión.

```
✅ Rutas incluidas:
├── Splash
├── Login
├── Register (normal y OAuth)
└── AccountReview (para psicólogos pendientes)
```

**Cuándo modificar:**
- Agregar nuevas pantallas de autenticación (ej: Forgot Password, Reset Password)
- Modificar flujo de login/registro

---

### 4. SharedNavigation.kt (Post-Login Compartidas)

**Contenido:** Rutas accesibles por **General, Patient y Psychologist** después de login.

```
✅ Rutas incluidas:
├── Home (muestra diferente contenido según userType)
├── Profile
├── Notifications
├── NotificationPreferences
├── AIWelcome
└── AIChat
```

**Ejemplo de ruta con contenido diferente por usuario:**

```kotlin
composable(Route.Home.path) {
    val userSession = remember { UserSession(context) }
    val currentUser = userSession.getUser()

    when (currentUser?.userType) {
        UserType.PSYCHOLOGIST -> {
            Scaffold(bottomBar = { PsychologistBottomNav(navController) }) {
                PsychologistHomeScreen(...)
            }
        }
        UserType.GENERAL, UserType.PATIENT -> {
            if (isPatient) PatientHomeScreen() else GeneralHomeScreen()
        }
    }
}
```

---

### 5. GeneralNavigation.kt (Solo usuario GENERAL)

**Contenido:** Rutas exclusivas para usuarios tipo GENERAL (sin psicólogo).

```
✅ Rutas actuales:
└── ConnectPsychologist

📋 Futuras rutas sugeridas:
├── FindPsychologist (buscar psicólogos disponibles)
├── WellnessResources (recursos de bienestar)
└── SelfCareExercises (ejercicios de autocuidado)
```

**Cuándo modificar:**
- Agregar funcionalidades exclusivas para usuarios sin psicólogo asignado

---

### 6. PatientNavigation.kt (Solo PATIENT)

**Contenido:** Rutas exclusivas para usuarios tipo PATIENT (con psicólogo asignado).

```
📋 Actualmente vacío, pero listo para:
├── TherapySessions (ver sesiones con psicólogo)
├── MyPsychologist (perfil del psicólogo asignado)
├── AssignedExercises (ejercicios asignados por el psicólogo)
├── SessionNotes (notas de sesiones)
└── ProgressTracking (seguimiento de progreso)
```

**⚠️ Importante:** Los pacientes TAMBIÉN tienen acceso a rutas de `GeneralNavigation` (pueden cambiar de psicólogo).

---

### 7. PsychologistNavigation.kt (Solo PSYCHOLOGIST)

**Contenido:** Rutas exclusivas para usuarios tipo PSYCHOLOGIST.

```
📋 Actualmente vacío, pero listo para:
├── MyPatients (lista de pacientes asignados)
├── PatientDetail (detalle de un paciente específico)
├── AssignExercise (asignar ejercicios a pacientes)
├── SessionNotes (crear/editar notas de sesiones)
├── Analytics (estadísticas y progreso de pacientes)
└── Schedule (gestión de horarios)
```

---

### 8. AdminNavigation.kt (Solo ADMIN)

**Contenido:** Rutas exclusivas para usuarios tipo ADMIN.

```
✅ Rutas actuales:
├── AdminUsers (gestión de usuarios)
└── VerifyPsychologist (verificación de psicólogos)

📋 Futuras rutas sugeridas:
├── SystemSettings (configuración del sistema)
├── Analytics (estadísticas generales)
└── Reports (reportes y auditoría)
```


## 🌐 Constantes de API

### Ubicación

```
core/networking/ApiConstants.kt
```

### Propósito

Centralizar TODAS las URLs de los endpoints del backend en un solo lugar para:
- ✅ Facilitar el mantenimiento
- ✅ Evitar URLs hardcodeadas
- ✅ Reducir errores de tipeo
- ✅ Cambiar URLs desde un solo lugar

---

### Estructura

```kotlin
object ApiConstants {
    const val BASE_URL = "http://98.90.172.251:5000/api/v1/"

    // Auth endpoints
    object Auth {
        const val LOGIN = "auth/login"
        const val REGISTER = "auth/register"
        const val REGISTER_GENERAL = "auth/register/general"
        const val REGISTER_PSYCHOLOGIST = "auth/register/psychologist"
        const val SOCIAL_LOGIN = "auth/social-login"
        const val OAUTH = "auth/oauth"
        const val OAUTH_VERIFY = "auth/oauth/verify"
        const val OAUTH_COMPLETE_REGISTRATION = "auth/oauth/complete-registration"
    }

    // User endpoints
    object Users {
        const val BASE = "users"
        const val PROFILE = "users/profile"
        const val BY_ID = "users/{id}"
        const val VERIFY_PSYCHOLOGIST = "users/{id}/verify"
        const val CHANGE_STATUS = "users/{id}/status"
        const val PSYCHOLOGIST_INVITATION_CODE = "users/psychologist/invitation-code"

        fun getById(id: String) = BY_ID.replace("{id}", id)
        fun verifyPsychologist(id: String) = VERIFY_PSYCHOLOGIST.replace("{id}", id)
        fun changeStatus(id: String) = CHANGE_STATUS.replace("{id}", id)
    }

    // Therapy endpoints
    object Therapy {
        const val MY_RELATIONSHIP = "therapy/my-relationship"
        const val CONNECT = "therapy/connect"
    }

    // AI endpoints
    object AI {
        const val CHAT_MESSAGE = "ai/chat/message"
        const val CHAT_USAGE = "ai/chat/usage"
        const val CHAT_SESSIONS = "ai/chat/sessions"
        const val CHAT_SESSION_MESSAGES = "ai/chat/sessions/{sessionId}/messages"
        const val EMOTION_ANALYZE = "ai/emotion/analyze"
        const val EMOTION_USAGE = "ai/emotion/usage"

        fun getChatSessionMessages(sessionId: String) =
            CHAT_SESSION_MESSAGES.replace("{sessionId}", sessionId)
    }

    // Notification endpoints
    object Notifications {
        const val BASE = "notifications"
        const val BY_USER_ID = "notifications/{userId}"
        const val DETAIL = "notifications/detail/{notificationId}"
        const val MARK_AS_READ = "notifications/{notificationId}/read"
        const val MARK_ALL_READ = "notifications/read-all"
        const val DELETE = "notifications/{notificationId}"
        const val UNREAD_COUNT = "notifications/unread-count"

        fun getByUserId(userId: String) = BY_USER_ID.replace("{userId}", userId)
        fun getDetail(notificationId: String) = DETAIL.replace("{notificationId}", notificationId)
        fun markAsRead(notificationId: String) = MARK_AS_READ.replace("{notificationId}", notificationId)
        fun delete(notificationId: String) = DELETE.replace("{notificationId}", notificationId)
    }

    // Preferences endpoints
    object Preferences {
        const val BASE = "preferences"
        const val RESET = "preferences/reset"
    }
}
```

---

### ¿Cómo usar las constantes?

#### ❌ INCORRECTO (Hardcoded)

```kotlin
interface UserService {
    @GET("users/profile")  // ❌ URL hardcodeada
    suspend fun getProfile(): ProfileDto
}
```

#### ✅ CORRECTO (Usando constantes)

```kotlin
import com.softfocus.core.networking.ApiConstants

interface UserService {
    @GET(ApiConstants.Users.PROFILE)  // ✅ Usando constante
    suspend fun getProfile(): ProfileDto
}
```

---

### ¿Cómo agregar nuevos endpoints?

```kotlin
// 1. Agrega el endpoint en ApiConstants.kt
object ApiConstants {
    // ... código existente

    object MyNewFeature {
        const val BASE = "my-feature"
        const val GET_DATA = "my-feature/data"
        const val UPDATE_DATA = "my-feature/data/{id}"

        fun updateData(id: String) = UPDATE_DATA.replace("{id}", id)
    }
}

// 2. Úsalo en tu servicio Retrofit
interface MyFeatureService {
    @GET(ApiConstants.MyNewFeature.GET_DATA)
    suspend fun getData(): DataDto

    @PUT(ApiConstants.MyNewFeature.UPDATE_DATA)
    suspend fun updateData(
        @Path("id") id: String,
        @Body data: UpdateDataDto
    ): ResponseDto
}
```

---

### Endpoints con parámetros dinámicos

Para endpoints con `{id}`, `{userId}`, etc., usa **helper functions**:

```kotlin
object Users {
    const val BY_ID = "users/{id}"

    // Helper function para reemplazar el parámetro
    fun getById(id: String) = BY_ID.replace("{id}", id)
}

// Uso:
@GET(ApiConstants.Users.BY_ID)
suspend fun getUserById(@Path("id") userId: String): UserDto
```

---

## 🎨 Temas y Estilos

### Ubicación

```
ui/theme/
├── Color.kt        ← Definición de colores
├── Type.kt         ← Definición de tipografías
└── Theme.kt        ← Tema principal
```

---

### 1. Color.kt - Paleta de Colores

**Responsabilidad:** Definir TODOS los colores usados en la app.

```kotlin
package com.softfocus.ui.theme

import androidx.compose.ui.graphics.Color

// Colores principales
val Green29 = Color(0xFF295F29)
val Green6B = Color(0xFF6B8E6F)
val Green8A = Color(0xFF8AAE7C)
val YellowE8 = Color(0xFFE8C547)

// Colores de estado
val ErrorRed = Color(0xFFD32F2F)
val SuccessGreen = Color(0xFF388E3C)
val WarningOrange = Color(0xFFF57C00)

// Grises
val Gray50 = Color(0xFF9E9E9E)
val Gray80 = Color(0xFFCCCCCC)
val Gray90 = Color(0xFFE0E0E0)

// Backgrounds
val BackgroundLight = Color(0xFFFAFAFA)
val BackgroundDark = Color(0xFF121212)
```

#### ¿Cómo usar colores?
Primero busca el codigo color del figma pones ctrl f en el archivo de color y vas a ver con q  nombre esta, si no esta crealo. Lo mismo con types en el figma ves con q tipo de letra esta 
```kotlin
import com.softfocus.ui.theme.Green29
import com.softfocus.ui.theme.YellowE8

@Composable
fun MyButton() {
    Button(
        colors = ButtonDefaults.buttonColors(
            containerColor = Green29  // ✅ Usando color definido
        )
    ) {
        Text("Click me")
    }
}
```

#### ¿Cómo agregar nuevos colores?

```kotlin
// 1. Define el color en Color.kt
val MyNewColor = Color(0xFF123456)

// 2. Úsalo en tus composables
import com.softfocus.ui.theme.MyNewColor

Box(
    modifier = Modifier.background(MyNewColor)
)
```

**⚠️ NUNCA uses `Color(0xFF...)` directamente en composables.** Siempre define el color en `Color.kt` primero.

---

### 2. Type.kt - Tipografías

**Responsabilidad:** Definir todas las fuentes y estilos de texto.

```kotlin
package com.softfocus.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.softfocus.R

// Fuentes personalizadas
val CrimsonText = FontFamily(
    Font(R.font.crimson_text_regular, FontWeight.Normal),
    Font(R.font.crimson_text_semibold, FontWeight.SemiBold),
    Font(R.font.crimson_text_bold, FontWeight.Bold)
)

val SourceSans = FontFamily(
    Font(R.font.source_sans_pro_regular, FontWeight.Normal),
    Font(R.font.source_sans_pro_semibold, FontWeight.SemiBold)
)

// Estilos de texto predefinidos
val CrimsonBold = TextStyle(
    fontFamily = CrimsonText,
    fontWeight = FontWeight.Bold,
    fontSize = 24.sp
)

val CrimsonSemiBold = TextStyle(
    fontFamily = CrimsonText,
    fontWeight = FontWeight.SemiBold,
    fontSize = 20.sp
)

val SourceSansRegular = TextStyle(
    fontFamily = SourceSans,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp
)

val SourceSansSemiBold = TextStyle(
    fontFamily = SourceSans,
    fontWeight = FontWeight.SemiBold,
    fontSize = 16.sp
)
```

#### ¿Cómo usar tipografías?

```kotlin
import com.softfocus.ui.theme.CrimsonBold
import com.softfocus.ui.theme.SourceSansRegular

@Composable
fun MyScreen() {
    Column {
        Text(
            text = "Título Principal",
            style = CrimsonBold  // ✅ Usando estilo predefinido
        )

        Text(
            text = "Descripción del contenido",
            style = SourceSansRegular  // ✅ Usando estilo predefinido
        )
    }
}
```

#### ¿Cómo agregar nuevas tipografías?

```kotlin
// 1. Agrega el archivo .ttf en res/font/

// 2. Define la fuente en Type.kt
val MyNewFont = FontFamily(
    Font(R.font.my_new_font_regular, FontWeight.Normal),
    Font(R.font.my_new_font_bold, FontWeight.Bold)
)

// 3. Crea estilos con la nueva fuente
val MyNewFontStyle = TextStyle(
    fontFamily = MyNewFont,
    fontWeight = FontWeight.Normal,
    fontSize = 18.sp
)

// 4. Úsala en tu composable
Text(
    text = "Nuevo estilo",
    style = MyNewFontStyle
)
```

---

### 3. Theme.kt - Tema Principal

**Responsabilidad:** Configurar el tema Material Design de la app.

```kotlin
@Composable
fun SoftFocusMobileTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = Green29,
            secondary = Green6B,
            tertiary = YellowE8
        )
    } else {
        lightColorScheme(
            primary = Green29,
            secondary = Green6B,
            tertiary = YellowE8
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
```

**⚠️ Generalmente NO necesitas modificar este archivo.**

---

## 👥 Tipos de Usuario

### Ubicación

```
features/auth/domain/models/UserType.kt
```

### Definición

```kotlin
enum class UserType {
    GENERAL,      // Usuario sin psicólogo asignado
    PATIENT,      // Usuario con psicólogo asignado
    PSYCHOLOGIST, // Psicólogo profesional
    ADMIN         // Administrador del sistema
}
```

---

### ¿Cómo verificar el tipo de usuario?

```kotlin
import com.softfocus.core.data.local.UserSession
import com.softfocus.features.auth.domain.models.UserType

@Composable
fun MyScreen() {
    val context = LocalContext.current
    val userSession = remember { UserSession(context) }
    val currentUser = userSession.getUser()

    when (currentUser?.userType) {
        UserType.GENERAL -> {
            // Mostrar contenido para usuario general
        }
        UserType.PATIENT -> {
            // Mostrar contenido para paciente
        }
        UserType.PSYCHOLOGIST -> {
            // Mostrar contenido para psicólogo
        }
        UserType.ADMIN -> {
            // Mostrar contenido para admin
        }
        else -> {
            // Usuario no autenticado
        }
    }
}
```

---

## 🏗️ Arquitectura del Proyecto

### Clean Architecture

El proyecto sigue **Clean Architecture** con las siguientes capas:

```
features/
└── [feature-name]/
    ├── data/
    │   ├── models/          ← DTOs (Data Transfer Objects)
    │   ├── remote/          ← Retrofit services
    │   └── repositories/    ← Implementación de repositorios
    ├── domain/
    │   ├── models/          ← Entidades de dominio
    │   ├── repositories/    ← Interfaces de repositorios
    │   └── usecases/        ← Casos de uso (opcional)
    └── presentation/
        ├── viewmodels/      ← ViewModels
        ├── screens/         ← Pantallas Composable
        ├── components/      ← Componentes reutilizables
        └── di/              ← Dependency Injection
```

---

### Ejemplo: Feature de Notificaciones

```
features/notifications/
├── data/
│   ├── models/
│   │   ├── request/
│   │   │   └── UpdatePreferencesRequestDto.kt
│   │   └── response/
│   │       ├── NotificationListResponseDto.kt
│   │       └── NotificationResponseDto.kt
│   ├── remote/
│   │   └── NotificationService.kt  ← Retrofit interface
│   └── repositories/
│       └── NotificationRepositoryImpl.kt
├── domain/
│   ├── models/
│   │   └── Notification.kt  ← Domain entity
│   └── repositories/
│       └── NotificationRepository.kt  ← Interface
└── presentation/
    ├── di/
    │   └── NotificationPresentationModule.kt
    ├── list/
    │   ├── NotificationsScreen.kt
    │   ├── NotificationsViewModel.kt
    │   └── components/
    │       └── NotificationItem.kt
    └── preferences/
        ├── NotificationPreferencesScreen.kt
        └── NotificationPreferencesViewModel.kt
```

---

http://98.90.172.251:5000/swagger/index.html

● Usuarios de Prueba - Credenciales

👤 Usuarios Generales (5)

1. Laura Gomez
   - Email: patient1@test.com                                                                                                                                                                                                      
   - Password: Patient123!
2. Carlos Martinez
   - Email: patient2@test.com                                                                                                                                                                                                      
   - Password: Patient123!
3. Ana Garcia
   - Email: patient3@test.com                                                                                                                                                                                                      
   - Password: Patient123!
4. Luis Torres
   - Email: patient4@test.com                                                                                                                                                                                                      
   - Password: Patient123!
5. Maria Lopes
   - Email: patient5@test.com                                                                                                                                                                                                      
   - Password: Patient123!
> 
  ---
HJBMDNV0
👨‍⚕ Psicólogos Verificados (3)

1. Dra. Patricia Sanchez
   - Email: psychologist1@test.com                                                                                                                                                                                                 
   - Password: Psy123!
2. Dr. Ramiro Miranda Loza
   - Email: psychologist2@test.com                                                                                                                                                                                                 
   - Password: Psy123!
3. Dra. Sofia Ramirez
   - Email: psychologist3@test.com                                                                                                                                                                                                 
   - Password: Psy123!
     
  ---
👑 Admin

- Admin SoftFocus
    - Email: admin@softfocus.com
    - Password: Admin123!

