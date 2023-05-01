# Mapas y Geolocalización: Regreso a casa

La aplicación de Mapas y Rutas es una aplicación para Android que utiliza la biblioteca OSMDroid para mostrar mapas de OpenStreetMap y la API de OpenRouteService para calcular rutas entre la ubicación actual del dispositivo y un marcador seleccionado en el mapa. Además, la aplicación incluye un botón flotante para centrar el mapa en la ubicación actual del dispositivo.

## Características

- Visualización de mapas de OpenStreetMap utilizando OSMDroid
- Obtención y visualización de la ubicación actual del dispositivo
- Agregar marcadores en el mapa al tocar la pantalla
- Cálculo y visualización de rutas utilizando la API de OpenRouteService
- Botón flotante para centrar el mapa en la ubicación actual del dispositivo
- Ventana de información del marcador en la parte superior de la pantalla

## Requisitos

- Android Studio
- Kotlin
- OSMDroid
- OpenRouteService API
- Retrofit
- Gson
- Material Design Components

## Instalación

1. Clonar el repositorio: `git clone https://github.com/your-username/MapasRutasApp.git`
2. Importar el proyecto en Android Studio: `File` > `Open` > Seleccionar la carpeta del proyecto clonado
3. Sincronizar Gradle: Android Studio debería sincronizar automáticamente los archivos Gradle. Si no lo hace, selecciona `File` > `Sync Project with Gradle Files`
4. Obtener una API Key de OpenRouteService: Visita https://openrouteservice.org/sign-up/ para registrarte y obtener una clave de API.
5. Agregar la API Key al archivo `local.properties`: En el archivo `local.properties` del proyecto, agrega la siguiente línea: `openRouteServiceApiKey = TU_API_KEY`, reemplazando `TU_API_KEY` con la clave de API obtenida en el paso 4.
6. Ejecutar la aplicación: Conecta un dispositivo Android o inicia un emulador de Android y selecciona `Run` > `Run 'app'`.

## Uso

1. Inicia la aplicación en tu dispositivo o emulador de Android.
2. Espera a que la aplicación muestre el mapa y la ubicación actual del dispositivo.
3. Toca en cualquier lugar del mapa para agregar un marcador en esa ubicación.
4. La aplicación calculará y mostrará la ruta entre la ubicación actual del dispositivo y el marcador.
5. La información básica sobre la ubicación del marcador, como la latitud y la longitud, se mostrará en una ventana en la parte superior de la pantalla.
6. Utiliza el botón flotante en la parte inferior derecha de la pantalla para centrar el mapa en la ubicación actual del dispositivo.


## Licencia

Esta aplicación está bajo la licencia MIT. Consulte el archivo [LICENSE](LICENSE

🐱‍💻
