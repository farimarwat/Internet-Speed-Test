package pk.farimarwat.speedtest.models

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.Keep

@Keep
data class STServer(
    val url: String?,
    val lat: String?,
    val lon: String?,
    val name: String?,
    val sponsor: String?
):Parcelable{
    var distance:Int = 0

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
        distance = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(url)
        parcel.writeString(lat)
        parcel.writeString(lon)
        parcel.writeString(name)
        parcel.writeString(sponsor)
        parcel.writeInt(distance)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<STServer> {
        override fun createFromParcel(parcel: Parcel): STServer {
            return STServer(parcel)
        }

        override fun newArray(size: Int): Array<STServer?> {
            return arrayOfNulls(size)
        }
    }
}
