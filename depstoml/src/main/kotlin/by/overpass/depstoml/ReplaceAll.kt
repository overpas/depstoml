package by.overpass.depstoml

import java.nio.file.Files
import java.nio.file.Path

fun Path.replaceAll(replacements: Map<String, String>) {
    var fileString = Files.readString(this)
    if (replacements.keys.none(fileString::contains)) {
        return
    }
    for ((old, new) in replacements) {
        fileString = fileString.replace(old, new)
    }
    Files.writeString(this, fileString)
}
