package pk.farimarwat.speedtest.network

sealed class ApiStatus{
    data class Success(val list:List<Any>): ApiStatus()
    object Loading : ApiStatus()
    object Empty: ApiStatus()
    data class Error(val error:String): ApiStatus()
}
