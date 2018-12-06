import org.apache.commons.collections4.ListUtils
import java.security.SecureRandom
import java.time.Duration
import java.time.Instant
import java.util.*
import java.util.stream.Stream
import kotlin.experimental.and
import kotlin.random.Random
import kotlin.streams.toList

val charPool : List<Char> = ('a'..'z').toList()
val TOTAL_NAMES_TO_GENERATE = 300000
val DIVIDER = 150
val NAME_LENGTH = 7
val random = SecureRandom()
val bytes = ByteArray(NAME_LENGTH)

fun List<String>.toParticipant() =
        this
            .map { name: String ->
                Participant(name, Random.nextInt(0, 30))
            }

fun main(args: Array<String>) {
    val start = Instant.now()
    val names = generateNames(TOTAL_NAMES_TO_GENERATE)
    val names2 = generateNames(TOTAL_NAMES_TO_GENERATE)
    val names3 = generateNames(TOTAL_NAMES_TO_GENERATE)
    val end = Instant.now()

    println("Generating names lasted: ${Duration.between(start, end).seconds} seconds")

    testArrayConcatWithKotlinFilter(names, names2, names3)
    testNativeArrayJoinWithKotlinFilter(names, names2, names3)
    testAll(generateNames(TOTAL_NAMES_TO_GENERATE * 3))
}

/**
 * Tests filtering all names without joining arrays
 */
fun testAll(names: ArrayList<String?>) {
    var start = Instant.now()
    names
        .toSet()
        .filterNotNull()
        .toParticipant()
        .sortedWith(compareBy({ it.name }, { it.age }))

    var end = Instant.now()
    print("Tests all names with sorting", Duration.between(start, end).toMillis())

    start = Instant.now()
    names
        .toSet()
        .filterNotNull()
        .toParticipant()

    end = Instant.now()
    print("Tests all names without sorting", Duration.between(start, end).toMillis())
    println("----")
}

fun testNativeArrayJoinWithKotlinFilter(names: ArrayList<String?>, names2: ArrayList<String?>, names3: ArrayList<String?>) {
    var start = Instant.now()
    Arrays.asList(names, names2, names3)
        .flatten()
        .toSet()
        .filterNotNull()
        .toParticipant()
        .sortedWith(compareBy({ it.name }, { it.age }))

    var end = Instant.now()

    print("Join arrays with asList with sorting", Duration.between(start, end).toMillis())

    start = Instant.now()
    Arrays.asList(names, names2, names3)
        .flatten()
        .toSet()
        .filterNotNull()
        .toParticipant()

    end = Instant.now()

    print("Join arrays with asList without sorting", Duration.between(start, end).toMillis())
    println("----")
}

fun testArrayConcatWithKotlinFilter(names: List<String?>, names2: List<String?>, names3: List<String?>) {
    var start = Instant.now()
    (names + names2 + names3)
        .toSet()
        .filterNotNull()
        .toParticipant()
        .sortedWith(compareBy({ it.name }, { it.age }))

    var end = Instant.now()

    print("Concat arrays with + operand with sorting", Duration.between(start, end).toMillis())
    start = Instant.now()
    (names + names2 + names3)
        .toSet()
        .filterNotNull()
        .toParticipant()

    end = Instant.now()

    print("Concat arrays with + operand without sorting", Duration.between(start, end).toMillis())
    println("----")
}

fun print(invocation: String, duration: Long) {
    println("$invocation lasted $duration milliseconds")
}

fun generateNames(totalElements: Int): ArrayList<String?> {
    val names = ArrayList<String?>()
    for (i in totalElements downTo 0) {
        if (i % DIVIDER == 0) {
            names.add(null)
        } else {
            random.nextBytes(bytes)
            val name = (0..bytes.size - 1)
                .map { i -> charPool.get((bytes[i] and 0xFF.toByte() and (charPool.size - 1).toByte()).toInt()) }
                .joinToString("")
            names.add(name)
        }
    }

    return names
}
