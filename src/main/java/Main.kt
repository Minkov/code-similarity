import com.zhixiangli.code.similarity.CodeSimilarity
import com.zhixiangli.code.similarity.strategy.CosineSimilarity
import java.io.File

fun getFileNames(folder: String): List<String> {
    return File(folder)
            .walk()
            .map { it.toString() }
            .filter { it.endsWith("cs") }
            .toList()
}

fun main(args: Array<String>) {
    val map = HashMap<String, ArrayList<String>>().withDefault { arrayListOf() }

    for (file in getFileNames("/Users/doncho/repos/dmoj-env/site-env/sources/cs-advanced")) {
        val firstIndex = file.lastIndexOf('/')
        val lastIndex = file.lastIndexOf('-')
        val key = file.substring(firstIndex + 1, lastIndex)
        if (!map.containsKey(key)) {
            map[key] = arrayListOf()
        }

        map[key]!!.add(file)
    }

    map.forEach { problem_name, solutionPaths ->
        val solutions = solutionPaths.map {
            File(it).inputStream()
                    .bufferedReader()
                    .use { it.readText() }
        }

        for ((i, solution1) in solutions.withIndex()) {
            var j = i + 1
            val username1 = extractUsername(solutionPaths[i])

            while (j < solutions.size) {
                val solution2 = solutions[j]
                val username2 = extractUsername(solutionPaths[j])
                j++

                if (username1 == username2) {
                    continue
                }

                val cosineSimilarity = CodeSimilarity(CosineSimilarity())
                val codeSimilarity = CodeSimilarity()

                val resultCosineSimilarity = cosineSimilarity.get(solution1, solution2)
                val resultCodeSimilarity = codeSimilarity.get(solution1, solution2)
                println("${problem_name}|${username1}|${username2}|${resultCodeSimilarity}|${resultCosineSimilarity}")
            }
        }
    }
}

fun extractUsername(path: String): String {
    val lastIndex = path.lastIndexOf('/')
    val firstIndex = path.lastIndexOf('/', lastIndex - 1)
    return path.substring(firstIndex + 1, lastIndex)
}
