package todoapp.web

import io.ktor.client.request.*
import mu.KLogging
import org.springframework.web.reactive.function.server.*
import todoapp.application.TodoCleanup
import todoapp.application.TodoFind
import todoapp.application.TodoModification
import todoapp.application.TodoRegistry
import todoapp.domain.TodoId
import todoapp.web.command.WriteTodoCommand
import todoapp.web.validation.WriteTodoCommandValidator
import todoapp.web.validation.process
import java.net.URI

/**
 * 할 일 관리 라우터
 *
 * @author springrunner.kr@gmail.com
 */
class TodoRouter(
    val find: TodoFind,
    val registry: TodoRegistry,
    val modification: TodoModification,
    val cleanup: TodoCleanup
): RouterFunction<ServerResponse> {

    private val validator = WriteTodoCommandValidator()

    private val delegate = coRouter { // 코틀린의 coroutine 기반 router - 비동미
        "/api/todos".nest {
            GET("") {
                ok().bodyValueAndAwait(find.all())
            }

            GET("/{id}") { request ->
                val id = TodoId(request.pathVariable("id"))
                ok().bodyValueAndAwait(find.byId(id))
            }

            POST("") { request ->
                val command = request.awaitBody<WriteTodoCommand>().apply { // WriteTodoCommand 객체로 가져옴
                    validator.process(target = this)
                }

                registry.register(command.text).let {
                    created(URI.create("/api/todos/$it")).bodyValueAndAwait(it)
                }
            }

            PUT("/{id}") { request ->
                val id = TodoId(request.pathVariable("id"))
                val command = request.awaitBody<WriteTodoCommand>().apply {
                    validator.process(target = this)
                }

                modification.modify(id, command.text, command.completed)
                ok().bodyValueAndAwait(find.byId(id))
            }

            DELETE("/{id}") { request ->
                // 아래 로직을 이렇게도 작성할 수 있음
                // cleanup.clear(TodoId(request.pathVariable("id")))

                val id = TodoId(request.pathVariable("id"))

                cleanup.clear(id)
                ok().buildAndAwait() // response: Unit (응답 값이 없음을 의미)
            }

            POST("/clear-completed") {
                cleanup.clearAllCompleted()
                ok().buildAndAwait()
            }
        }
    }

    override fun route(request: ServerRequest) = delegate.route(request)

    companion object : KLogging()
}
