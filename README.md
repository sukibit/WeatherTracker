# Weather Tracker App

Aplicación de clima con pronóstico diario usando Jetpack Compose y arquitectura MVI.

## Quick Start

### Compilar y Ejecutar

1. Clonar el proyecto
```bash
git clone <repo>
cd weather-tracker
```

2. Configurar API key
```bash
# En local.properties (raíz del proyecto)
OPENWEATHER_API_KEY=tu_api_key_aqui
```

3. Compilar y ejecutar
```bash
./gradlew installDebug
```

---

## Arquitectura

### Estructura de Módulos (Actual)
```
app/                          # Punto de acceso
feature/
  └── weather/               # Feature aislado
      ├── presentation/      # UI + ViewModels (MVI)
      │   ├── contract/      # Event, State, Effect
      │   ├── viewmodel/     # WeatherListViewModel, WeatherDetailViewModel
      │   └── ui/            # Screens y Components
      ├── domain/            # Use Cases (lógica de negocio)
      │   └── usecase/       # RefreshWeatherUseCase
      └── data/              # Repository + Data Sources
          ├── repository/    # WeatherRepository
          ├── datasource/    # API, Local DB
          └── model/         # DTOs
core/                         # Módulo compartido
  ├── ui/                    # Componentes, Temas, Dimens
  └── network/               # Retrofit, OkHttp, Config
```

---

## Mejoras Futuras de Arquitectura

Se propone crear nuevos módulos para mejorar la escalabilidad:

```
├── app
├── core
├── ui
├── feature
├── domain              # Modelos y use cases genéricos/transversales
├── data                # Implementaciones de Repository
└── framework           # Implementaciones de datos (Retrofit, Room, etc.)
```

Concepto:
* `:domain` y `:data` contienen Kotlin puro, sin dependencias de SDKs externos.
* Los use cases genéricos y transversales a toda la app se alojan en `:domain`.
* Cada `:feature` puede tener sus propios use cases específicos.
* `:framework` contiene todas las implementaciones concretas (Retrofit, Room, etc.).

Beneficio para KMP:
Si en el futuro migramos a Kotlin Multiplatform, solo necesitaríamos modificar las dependencias en `:framework`. Los módulos `:domain`, `:data` y `:ui` (que pueden compartir módulo con presentación) permanecerían intactos y reutilizables.

---

## Decisiones Técnicas

### 1. **MVI vs MVVM**
- **Elegido: MVI (Model-View-Intent)**
- **Unidireccional**: Flujo predecible de datos (Intent → ViewModel → State → UI)
- **Testeable**: Cada componente tiene responsabilidad única
- **Debugging**: Fácil rastrear el flujo de eventos
- **State Management**: Estado centralizado e inmutable

```
Event (User Action) → ViewModel → State (UI Update) → Composable
```

### 2. **Retrofit vs Ktor**
- **Elegido: Retrofit**
- Simple y maduro
- Integración con Hilt directa
- OkHttp interceptors bien soportados
- Community grande

### 3. **Hilt vs Koin**
- **Elegido: Hilt**
- Compile-time safety (detección de errores antes de ejecutar)
- Mejor performance (no reflection en runtime)
- Integración nativa con Jetpack
- Google official

### 4. **Jetpack Compose vs XML**
- **Elegido: Compose**
- Menos boilerplate
- Preview en tiempo real
- Accesibilidad mejorada
- Material Design 3 nativo

### 5. **State Management: Flow vs LiveData**
- **Elegido: Flow (StateFlow)**
- Coroutines nativas
- Cold stream (eficiente)
- Mejor para Compose

### 6. **Estrategia de Caché: SSOT (Single Source of Truth)**
- **Elegido: Room como SSOT con Flow reactivo**
- **Flujo de datos:**
    1. Usuario abre app → ViewModel solicita datos con `getWeatherForecast()`
    2. `getWeatherForecast()` devuelve `Flow<List<Weather>>` desde Room
    3. ViewModel se suscribe al Flow y emite estado de Carga
    4. App muestra datos locales si existen (cache-first)
    5. En paralelo, `refreshWeather()` llama a API y actualiza Room
    6. Room emite nuevos datos → UI se actualiza automáticamente
- **Ventaja:** UI siempre reactiva a cambios en BD, sin actualizaciones manuales
- **Robustez:** Si la API falla, la app sigue mostrando datos en caché
- **Garantía:** Un único origen de verdad (Room), evita inconsistencias

---

## Accesibilidad

### WCAG AA+ Compliance
- ✓ Contraste mínimo 4.5:1 (texto sobre fondo)
- ✓ Touch targets 48dp mínimo
- ✓ Semantics en componentes interactivos
- ✓ Descripciones en iconos

### Implementación
```kotlin
Icon(
    imageVector = Icons.Default.Warning,
    contentDescription = stringResource(R.string.error_icon_description),
    tint = MaterialTheme.colorScheme.error
)
```