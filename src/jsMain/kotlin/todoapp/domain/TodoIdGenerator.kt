package todoapp.domain

import todoapp.util.UUID

// js에서의 실체화된 코드용
// js에서는 개발자가 작성한 UUID 활용
actual interface TodoIdGenerator {
    actual fun generateId() = TodoId(UUID.randomUUID().toString().lowercase())

}