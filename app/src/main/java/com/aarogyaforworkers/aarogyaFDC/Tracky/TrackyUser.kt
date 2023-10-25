package com.aarogyaforworkers.aarogyaFDC.Tracky

import android.os.Parcel
import android.os.Parcelable
import com.qn.device.constant.UserGoal
import com.qn.device.constant.UserShape
import com.qn.device.out.QNIndicateConfig
import java.util.Date

data class TrackyUser(
    var userId: String? = null,
    var height: Int = 0,
    var gender: String? = null,
    var birthDay: Date? = null,
    var athleteType: Int = 0,
    var choseShape: UserShape? = null,
    var choseGoal: UserGoal? = null,
    var clothesWeight: Double = 0.0,
    var qnIndicateConfig: QNIndicateConfig? = null
)
