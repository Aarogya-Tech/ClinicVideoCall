package com.aarogyaforworkers.aarogyaFDC.Tracky

import android.os.Parcel
import android.os.Parcelable

data class DConfig(
    var onlyScreenOn: Boolean = false,
    var allowDuplicates: Boolean = false,
    var duration: Int = 0,
    var enhanceBleBroadcast: Boolean = false,
    var unit: Int = 0,
    var heightUnit: Int = 0,
    var scanOutTime: Long = 6000,
    var connectOutTime: Long = 6000
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readInt(),
        parcel.readByte() != 0.toByte(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readLong(),
        parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte(if (onlyScreenOn) 1 else 0)
        parcel.writeByte(if (allowDuplicates) 1 else 0)
        parcel.writeInt(duration)
        parcel.writeByte(if (enhanceBleBroadcast) 1 else 0)
        parcel.writeInt(unit)
        parcel.writeInt(heightUnit)
        parcel.writeLong(scanOutTime)
        parcel.writeLong(connectOutTime)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DConfig> {
        override fun createFromParcel(parcel: Parcel): DConfig {
            return DConfig()
        }

        override fun newArray(size: Int): Array<DConfig?> {
            return arrayOfNulls(size)
        }
    }
}

