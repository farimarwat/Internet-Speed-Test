package pk.farimarwat.speedtest.models

import androidx.annotation.Keep

@Keep
data class ServersResponse(
    var provider:STProvider?,
    var servers:List<STServer>?
)
