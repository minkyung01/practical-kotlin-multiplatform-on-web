package todoapp.domain

import java.util.UUID

// jvm에서의 실체화된 코드
// jvm에서는 java.util에서 제공하는 UUID 활용
actual interface TodoIdGenerator {
    actual fun generateId() = TodoId(UUID.randomUUID().toString().lowercase())

}