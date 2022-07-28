package pk.farimarwat.speedtest.models

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.Keep

@Keep
data class STProvider(
    val isp: String?,
    val providername: String?,
    val lat: String?,
    val lon: String?
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(isp)
        parcel.writeString(providername)
        parcel.writeString(lat)
        parcel.writeString(lon)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<STProvider> {
        override fun createFromParcel(parcel: Parcel): STProvider {
            return STProvider(parcel)
        }

        override fun newArray(size: Int): Array<STProvider?> {
            return arrayOfNulls(size)
        }
    }
}