package com.srtp.assistant.logic.model

import org.litepal.crud.LitePalSupport

data class Work(
    var courseName:String = "",         //该作业的课程名称
    var content:String = "",            //作业文本内容
    var PicturesOnePath:String = "",    //图片1的路径
    var PicturesTwoPath: String="",     //图片2的路径
    var PicturesThreePath: String="",   //图片3的路径
    var time:Long=0,                    //截至提交日期，用时间戳表示
    var isCompleted:Int=0,              //作业是否完成，1表示已完成，0表示未完成
    var isReminded:Int=0,               //该作业是否已发送提醒交作业消息
    var firstSaveTimeStamp:Long = 0     //首次保存该作业项的时间戳

    ):LitePalSupport(){
        var id: Long = 0    //主键，值自增
    }


