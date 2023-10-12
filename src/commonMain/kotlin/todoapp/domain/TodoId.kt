package todoapp.domain

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * 할 일 일련번호
 *
 * @author springrunner.kr@gmail.com
 */
@kotlinx.serialization.Serializable(with = TodoId.TodoIdSerializer::class)
data class TodoId(val value: String) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return when(other) {
            is TodoId -> value == other.value
            is String -> value == other
            else -> false
        }
    }
    override fun hashCode() = value.hashCode()
    override fun toString() = value

    internal object TodoIdSerializer : KSerializer<TodoId> {
        override val descriptor = PrimitiveSerialDescriptor("TodoId", PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder): TodoId {
            return TodoId(decoder.decodeString())
        }

        override fun serialize(encoder: Encoder, value: TodoId) {
            encoder.encodeString(value.toString())
        }
    }
}

expect interface TodoIdGenerator {
    // expect 키워드를 활용해 플랫폼 추상화 매커니즘을 활용할 것임을 선언
    // 개별 플랫폼 별로 실체화된 코드가 따로 존재함을, 코틀린 컴파일러에게 알려주는 것
    // common 모듈 내에는 실체화된 코드 존재하지 않게 됨

    open fun generateId(): TodoId
}

class UUIDTodoIdGenerator : TodoIdGenerator

class RandomTodoIdGenerator: TodoIdGenerator { // 36자리의 random한 문자열 생성

    private val CHARACTERS : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    override fun generateId() = (1..36)
        .map { CHARACTERS[kotlin.random.Random.nextInt(0, CHARACTERS.size)] }
        .joinToString("")
        .let { TodoId(it) }
}
