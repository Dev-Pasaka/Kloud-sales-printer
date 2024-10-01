package utils

import java.nio.file.Paths

object OSDirectory {
    fun getDocumentsDirectory(): String {
        val userHome = System.getProperty("user.home")
        val documentsPath: String = when {
            // On Windows, the Documents folder is usually found in the user's home directory
            System.getProperty("os.name").contains("Windows") -> Paths.get(userHome, "Documents").toString()

            // On macOS, it's also inside the user's home directory
            System.getProperty("os.name").contains("Mac") -> Paths.get(userHome, "Documents").toString()

            // On Linux, it's generally located under the user's home directory too
            else -> Paths.get(userHome, "Documents").toString()
        }
        println("Documents path: $documentsPath")
        return documentsPath
    }
}

fun main(){
    println(OSDirectory.getDocumentsDirectory())
}