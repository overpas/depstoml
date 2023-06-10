plugins {
    alias(Deps.Plugins.ksp)
}

dependencies {
    implementation(Deps.Kotlin.gradlePlugin)
    implementation(Deps.Kotlin.dateTime)
    implementation(Deps.Kotlin.Coroutines.core)
    implementation(Deps.Log.napier)
    testImplementation(Deps.Kotlin.Coroutines.test)
}
