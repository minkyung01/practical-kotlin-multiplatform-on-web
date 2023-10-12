package todoapp.domain

/**
 * 할 일 검증기 인터페이스
 *
 * @author springrunner.kr@gmail.com
 */
object TodoValidator {

    fun validate(text: String) {
        // text가 비었을 경우에는 validation 실패
        if (text.isBlank()) throw TodoExceptions.creation("todo text is must not be null or empty")
    }
}
