package todoapp.application.support

import kotlinx.datetime.LocalDateTime
import mu.KotlinLogging
import todoapp.application.TodoCleanup
import todoapp.application.TodoFind
import todoapp.application.TodoModification
import todoapp.application.TodoRegistry
import todoapp.domain.Todo
import todoapp.domain.TodoId
import todoapp.domain.TodoIdGenerator
import todoapp.domain.Todos
import kotlin.math.log

/**
 * 메모리 기반 할 일 관리 컴포넌트
 *
 * @author springrunner.kr@gmail.com
 */
// application의 핵심 로직은 모두 common 모듈에 작성됨
// client 모듈에서는, common 모듈에 작성된 로직을 기반으로 사용자와 상호작용
class InMemoryTodoManager(
    private val todoIdGenerator: TodoIdGenerator
): TodoFind, TodoRegistry, TodoModification, TodoCleanup {
    // in memory manager는 메모리 기반이기 때문에, 새로고침 하게 되면 데이터가 초기화됨

    private val todos = mutableListOf(
        Todo(id = todoIdGenerator.generateId(), text = "Task One", createdDate = LocalDateTime.parse("2016-02-15T10:00:00")),
        Todo(id = todoIdGenerator.generateId(), text = "Task Two", createdDate = LocalDateTime.parse("2016-02-15T10:00:10"))
    )
    private val logger = KotlinLogging.logger("todoapp.application.support.InMemoryTodoManager")

    override suspend fun all(): Todos = Todos(todos.sortedBy { it.createdDate })

    override suspend fun byId(id: TodoId): Todo = loadTodoById(id)

    override suspend fun register(text: String): TodoId {
        return Todo.create(text = text, idGenerator = todoIdGenerator).apply {
            todos.add(this)
            logger.info { "Registered todo (id: ${id})" }
        }.id
    }

    override suspend fun modify(id: TodoId, text: String, completed: Boolean) {
        loadTodoById(id).update(text = text, completed = completed).run {
            todos.remove(this)
            todos.add(this)
            logger.info { "Modified todo (id: $id)" }
        }
    }

    override suspend fun clear(id: TodoId) {
        loadTodoById(id).run {
            todos.remove(this)
            logger.info { "Cleared todo (id: $id)" }
        }
    }

    override suspend fun clearAllCompleted() {
        val activeTodos = todos.filter { !it.completed }
        todos.clear()
        todos.addAll(activeTodos)
        logger.info { "Cleared completed todos" }
    }

    private fun loadTodoById(id: TodoId) = todos.firstOrNull { it.id == id } ?: error("Not Found Todo (id: $id)")
}
