import java.io.File
import java.nio.file.Paths
import kotlin.streams.toList
import kotlin.system.exitProcess

fun grep(keyword: String, file: File) : List<String> {
    val lines = file.readLines()
    println("${Thread.currentThread()}")

    return lines
            .withIndex()
            .filter { it.value.contains(keyword) }
            .map { "${file.name} ${it.index} : ${it.value}" }
}

fun findKeyword(keyword: String, files : List<File>) : List<String> {
    return files.flatMap {
        val result = grep(keyword, it)
        result
    }
}


fun findKeywordParallel(keyword: String, files : List<File>) : List<String> {
    return files.parallelStream()
            .flatMap {
                grep(keyword, it).stream()
            }.toList()
}

fun findFilesRecursively(relativePath: String): List<File> {
    val currentFile = File(relativePath)
    if (currentFile.exists()) {
        val files = currentFile.walk()
        return files.filter { !it.isDirectory }.toList()
    } else {
        println("file not exists : $relativePath")
        exitProcess(1)
    }
}

fun main() {
    val input = readLine()
    val commands = input?.split(" ")

    if (commands?.size != 3) {
        println("dgrep must have 2 parameters")
        exitProcess(1)
    }

    val keyword = commands[1]
    val path = Paths.get("").toAbsolutePath().toString()
    val relativePath = "${path}/${commands[2]}"

    val start = System.currentTimeMillis()
    val files = findFilesRecursively(relativePath)
    val result = findKeyword(keyword, files)

    println(result)

    val end = System.currentTimeMillis()

    println("non parallel spend time : ${end - start}")

    val parallelStart = System.currentTimeMillis()

    val parallelFiles = findFilesRecursively(relativePath)
    val parallelResult = findKeywordParallel(keyword, parallelFiles)

    println(parallelResult)

    val parallelEnd = System.currentTimeMillis()

    println("non parallel spend time : ${parallelEnd - parallelStart}")}