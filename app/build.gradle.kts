import java.util.Properties //  Para poder leer propiedades desde local.properties donde guarda la API key

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

val propiedadesLocales = Properties()
val archivoPropiedadesLocales = rootProject.file("local.properties") // creo un objeto para leer esas propiedades y obtener el archivo

if (archivoPropiedadesLocales.exists()) { // comprueba que local properties exista y carga su contenido
    propiedadesLocales.load(
        archivoPropiedadesLocales.inputStream()
    )
}

val claveOpenRouter =
    propiedadesLocales.getProperty("OPENROUTER_API_KEY", "") ///  obtiene especificamente esa API

android {
    namespace = "edu.unicauca.aplimovil.studyhub_application"

    compileSdk {
        version = release(37)
    }

    defaultConfig { // hace que esa clave este dispoinble dentro de la aplicacion mediante buildConfig
        applicationId = "edu.unicauca.aplimovil.studyhub_application"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            "String",
            "OPENROUTER_API_KEY",
            "\"$claveOpenRouter\""
        )
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)

    ksp(libs.androidx.room.compiler)

    implementation(
        "androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7"
    )

    implementation(
        platform(libs.androidx.compose.bom)
    )

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.navigation.compose)

    testImplementation(libs.junit)

    androidTestImplementation(
        platform(libs.androidx.compose.bom)
    )

    androidTestImplementation(
        libs.androidx.compose.ui.test.junit4
    )

    androidTestImplementation(
        libs.androidx.espresso.core
    )

    androidTestImplementation(
        libs.androidx.junit
    )

    debugImplementation(
        libs.androidx.compose.ui.test.manifest
    )

    debugImplementation(
        libs.androidx.compose.ui.tooling
    )
}