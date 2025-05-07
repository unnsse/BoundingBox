import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class CreateShellScriptTask : DefaultTask() {
    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @TaskAction
    fun createScript() {
        val file = outputFile.get().asFile
        file.writeText(
            """
            |#!/bin/sh
            |DIR="$(cd "$(dirname "$0")" && pwd)"
            |exec java -jar "${'$'}DIR/bounding-box.jar" "${'$'}@"
            """.trimMargin()
        )
        file.setExecutable(true)
        logger.lifecycle("Shell script created at: ${file.absolutePath}")
    }
}