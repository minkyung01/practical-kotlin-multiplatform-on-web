package todoapp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.support.beans
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.http.codec.json.KotlinSerializationJsonDecoder
import org.springframework.http.codec.json.KotlinSerializationJsonEncoder
import org.springframework.web.reactive.config.WebFluxConfigurer
import todoapp.application.DefaultTodoManager
import todoapp.application.support.TransactionalTodoManager
import todoapp.domain.RandomTodoIdGenerator
import todoapp.domain.UUIDTodoIdGenerator
import todoapp.serializer.Serializers
import todoapp.web.IndexRouter
import todoapp.web.TodoRouter

@SpringBootApplication
class TodoServerApplication

/**
 * 서버 애플리케이션 진입점(entry point)
 *
 * @author springrunner.kr@gmail.com
 */
fun main(args: Array<String>) {
    val applicationBeans = beans {
        // 2개의 bean 선언
        bean {
            DefaultTodoManager(
                // todoIdGenerator = RandomTodoIdGenerator(),
                todoIdGenerator = UUIDTodoIdGenerator(), // uuid 활용하도록 변경
                todoRepository = ref()
            )
        }
        bean(isPrimary = true) { // spring 기술이 적용된 manager가 먼저 사용되도록 isPrimary = true 설정
            val todoManager = ref<DefaultTodoManager>()
            TransactionalTodoManager(
                find = todoManager,
                registry = todoManager,
                modification = todoManager,
                cleanup = todoManager
            )
        }
    }
    val webBeans = beans {
        bean {
            object : WebFluxConfigurer {
                override fun configureHttpMessageCodecs(configurer: ServerCodecConfigurer) {
                    configurer.defaultCodecs().apply {
                        kotlinSerializationJsonEncoder(KotlinSerializationJsonEncoder(Serializers.JSON))
                        kotlinSerializationJsonDecoder(KotlinSerializationJsonDecoder(Serializers.JSON))
                    }
                }
            }
        }
        bean { IndexRouter() }
        bean { TodoRouter(find = ref(), registry = ref(), modification = ref(), cleanup = ref()) }
    }

    runApplication<TodoServerApplication>(*args) {
        addInitializers(applicationBeans, webBeans)
    }
}
