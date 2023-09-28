package com.aarogyaforworkers.aarogyaFDC.Tracky

import android.os.Parcel
import android.os.Parcelable
import com.qn.device.out.QNIndicateConfig

class QnConfig : Parcelable {
    var showUserName: Boolean = true
    var showBmi: Boolean = true
    var showBone: Boolean = true
    var showFat: Boolean = true
    var showMuscle: Boolean = true
    var showWater: Boolean = true
    var showHeartRate: Boolean = true
    var showWeather: Boolean = true
    var weightExtend: Boolean = true
    var showVoice: Boolean = true

    constructor() {
    }

    constructor(parcel: Parcel) {
        showUserName = parcel.readByte() != 0.toByte()
        showBmi = parcel.readByte() != 0.toByte()
        showBone = parcel.readByte() != 0.toByte()
        showFat = parcel.readByte() != 0.toByte()
        showMuscle = parcel.readByte() != 0.toByte()
        showWater = parcel.readByte() != 0.toByte()
        showHeartRate = parcel.readByte() != 0.toByte()
        showWeather = parcel.readByte() != 0.toByte()
        weightExtend = parcel.readByte() != 0.toByte()
        showVoice = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte(if (showUserName) 1 else 0)
        parcel.writeByte(if (showBmi) 1 else 0)
        parcel.writeByte(if (showBone) 1 else 0)
        parcel.writeByte(if (showFat) 1 else 0)
        parcel.writeByte(if (showMuscle) 1 else 0)
        parcel.writeByte(if (showWater) 1 else 0)
        parcel.writeByte(if (showHeartRate) 1 else 0)
        parcel.writeByte(if (showWeather) 1 else 0)
        parcel.writeByte(if (weightExtend) 1 else 0)
        parcel.writeByte(if (showVoice) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<QNIndicateConfig> {
        override fun createFromParcel(parcel: Parcel): QNIndicateConfig {
            return QNIndicateConfig()
        }

        override fun newArray(size: Int): Array<QNIndicateConfig?> {
            return arrayOfNulls(size)
        }
    }
}
