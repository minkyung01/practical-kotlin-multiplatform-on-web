package todoapp.application

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import org.w3c.dom.url.URLSearchParams
import todoapp.application.support.InMemoryTodoManager
import todoapp.domain.RandomTodoIdGenerator
import todoapp.domain.UUIDTodoIdGenerator
import todoapp.serializer.Serializers
import todoapp.web.client.HttpClientTodoManager

/**
 * @author springrunner.kr@gmail.com
 */
object TodoManagerFactory {

    fun create(params: URLSearchParams) = when(params.get("mode")) { // mode에 따라 서로 다른 manager를 사용하도록
        "remote" -> UseCases.of( // mode = remote인 경우 - http://localhost:8080/?mode=remote
            HttpClientTodoManager(
                httpClient = HttpClient(io.ktor.client.engine.js.Js) {
                    install(ContentNegotiation) {
                        json(Serializers.JSON)
                    }
                    expectSuccess = true
                }
            )
        )
        else -> UseCases.of(
            InMemoryTodoManager(
                // todoIdGenerator = RandomTodoIdGenerator()
                todoIdGenerator = UUIDTodoIdGenerator() // uuid 활용하도록 변경
            )
        )
    }

    data class UseCases(
        val find: TodoFind,
        val registry: TodoRegistry,
        val modification: TodoModification,
        val cleanup: TodoCleanup
    ) {

        companion object {

            fun of(manager: InMemoryTodoManager) = UseCases(
                find = manager,
                registry = manager,
                modification = manager,
                cleanup = manager
            )

            fun of(manager: HttpClientTodoManager) = UseCases(
                find = manager,
                registry = manager,
                modification = manager,
                cleanup = manager
            )
        }
    }
}
