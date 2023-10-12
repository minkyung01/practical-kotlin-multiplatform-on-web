package todoapp.util

/**
 * @author springrunner.kr@gmail.com
 */
value class UUID private constructor(private val value: String) {

    override fun toString() = value

    companion object {

        fun randomUUID() = UUID(external.uuid.v4().lowercase()) // js는 uuid를 기본적으로 제공해주고 있지 않기 때문에 외부 라이브러리를 활용함
    }
}
