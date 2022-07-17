package pk.farimarwat.speedtest.models

const val TESTTYPE_DOWNLOAD = "download"
const val TESTTYPE_UPLOAD = "upload"
sealed class TestingStatus{
    object Idle: TestingStatus()
    data class Testing(val testing:Boolean, val testtype:String): TestingStatus()
    data class Error(val error:String): TestingStatus()
    object Canceled: TestingStatus()
    data class Finished(val testtype:String): TestingStatus()
}
