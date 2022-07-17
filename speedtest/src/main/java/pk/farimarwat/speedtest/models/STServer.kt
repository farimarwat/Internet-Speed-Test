package pk.farimarwat.speedtest.models

data class STServer(
    val url:String,
    val lat:String,
    val lon:String,
    val name:String,
    val sponsor:String
){
    var distance:Int = 0
}
