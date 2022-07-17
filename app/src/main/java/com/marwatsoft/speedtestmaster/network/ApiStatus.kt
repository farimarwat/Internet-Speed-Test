package com.marwatsoft.speedtestmaster.network

sealed class ApiStatus{
    data class Success(val list:List<Any>):ApiStatus()
    object Loading : ApiStatus()
    data class Error(val error:String): ApiStatus()
}
