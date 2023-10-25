package com.aarogyaforworkers.aarogyaFDC

class Common {

    val login = LoginFlowTest.getInstance()

    companion object{
        val shared = Common()
    }
}