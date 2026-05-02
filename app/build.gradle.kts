plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

/*
Configuración principal del módulo Android.

Define parámetros como versión de SDK, compatibilidad, nombre del paquete y configuración de compilación.
*/

android {
    namespace = "com.example.simpletodolist"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.simpletodolist"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

/*
Dependencias del proyecto.

Aquí se incluyen las librerías necesarias para el funcionamiento de la aplicación:
- Jetpack Compose para la interfaz de usuario.
- Lifecycle y Activity Compose para integración con Android.
- Navigation Compose para manejo de pantallas.
- Retrofit para comunicación con API REST.
- Gson Converter para convertir JSON a objetos Kotlin automáticamente.
*/

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Navegación entre pantallas usando Jetpack Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.7.7")

    /*Librería Retrofit: Usada para conectarse a API REST de manera sencilla
    Peticiones HTTP (GET, POST, etc.)
    Convierte a JSON los objetos de Kotlin de manera automática
    */

    implementation("com.squareup.retrofit2:retrofit:2.9.0")

    // Conversor JSON -> Kotlin usando Gson
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

}