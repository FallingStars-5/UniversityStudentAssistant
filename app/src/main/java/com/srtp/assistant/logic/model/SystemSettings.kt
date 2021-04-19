package com.srtp.assistant.logic.model

data class SystemSettings(
    var isvibrates:Int = 1,   //是否允许响铃，0表示不允许，1表示允许，默认不允许
    var isRing:Int = 0        //是否允许震动，0表示不允许，1表示允许，默认不允许
    )
