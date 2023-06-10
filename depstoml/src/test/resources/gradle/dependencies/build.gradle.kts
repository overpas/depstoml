plugins {
    alias(Dependencies.Plugins.ksp)
}

dependencies {
    implementation(Dependencies.Kotlin.gradlePlugin)
    implementation(Dependencies.Kotlin.dateTime)
    implementation(Dependencies.Kotlin.Coroutines.core)
    implementation(Dependencies.Log.napier)
    testImplementation(Dependencies.Kotlin.Coroutines.test)
}
