package todoapp.ui.welcome

import csstype.*
import emotion.react.css
import react.FC
import react.Props
import react.dom.html.InputType
import react.dom.html.ReactHTML
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.section
import react.useState

external interface WelcomePageProps : Props { // react(= 외부)가 이 props를 활용할 것이기 때문에 external 키워드 활용
    var name: String? // nullable
}

val WelcomePage = FC<WelcomePageProps> { // FC: functional component
    val (name, setName) = useState(it.name ?: "SpringRunner") // it.name으로 넘어온 값이 null이라면, 기본값인 SpringRunner 활용 >> hook

    section {
        css {
            position = Position.absolute
            top = 30.pct
            transform = translate(0.px, (-50).pct)
        }
        div {
            css {
                paddingBottom = 8.px
                fontSize = 20.px
                fontWeight = FontWeight.bold
            }
            // +"Hello, ${it.name}" // 1. props 활용해서 외부에서 값 주입받을 수 있도록
            +"Hello, $name" // 2. hook에서 값을 설정할 수 있도록
        }
        input {
            type = InputType.text
            value = name
            onChange = { event ->
                setName(event.currentTarget.value)
            }
        }
    }
}
