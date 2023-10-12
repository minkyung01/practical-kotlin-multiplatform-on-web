package todoapp.web

import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router

/**
 * 인덱스 페이지 라우터
 *
 * @author springrunner.kr@gmail.com
 */
class IndexRouter : RouterFunction<ServerResponse> {

    private val delegate = router {
        accept(MediaType.TEXT_HTML).nest { // 클라이언트가 TEXT_HTML을 컨텐츠로 요청했을 때, 처리할 내용
            GET("/") {// 요청의 method/url 정보
                ok().contentType(MediaType.TEXT_HTML).bodyValue(indexHtml) // 요청 처리의 결과를 response로
            }
        }
    }
    private val indexHtml = buildString {
        appendLine("<!DOCTYPE html>")
        appendHTML(xhtmlCompatible = true).html {
            head {
                title("KMP • TodoMVC")
                meta(charset = "utf-8")
                link(href = "/webjars/todomvc-common/1.0.5/base.css", rel = "stylesheet")
                link(href = "/webjars/todomvc-app-css/2.4.1/index.css", rel = "stylesheet")
            }
            body {
                // <div id="root"></div>
                // <script src="/main.js"></script>
                //// 아래는 위 html 코드를 dsl을 활용해 작성한 것
                div {
                    id = "root"
                }
                script(src = "/main.js") {}
            }
        }
    }

    override fun route(request: ServerRequest) = delegate.route(request)
}
