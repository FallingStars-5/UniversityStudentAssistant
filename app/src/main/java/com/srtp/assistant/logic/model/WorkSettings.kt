package com.srtp.assistant.logic.model

data class WorkSettings(
    var isRemind:Int=1,       //是否启用作业提交提醒功能，1表示启用，0表示关闭
    var remindDayTime:Int = 1,//剩余多长时间时发送提交作业提醒消息
    var showingWays:Int = 0   //展示作业类型，0表示只显示未完成作业，1表示只显示已完成作业，2表示显示所有作业
)
