package by.overpass.depstoml

import java.io.File
import java.nio.file.Files
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.com.intellij.psi.PsiManager
import org.jetbrains.kotlin.com.intellij.testFramework.LightVirtualFile
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.psi.KtFile

private val project by lazy {
    KotlinCoreEnvironment.createForProduction(
        Disposer.newDisposable(),
        CompilerConfiguration(),
        EnvironmentConfigFiles.JVM_CONFIG_FILES,
    ).project
}

fun File.asKotlinFile(): KtFile? {
    val path = toPath()
    val filename = name
    val codeString = Files.readString(path)
    return PsiManager.getInstance(project)
        .findFile(
            LightVirtualFile(filename, KotlinFileType.INSTANCE, codeString)
        ) as? KtFile
}
