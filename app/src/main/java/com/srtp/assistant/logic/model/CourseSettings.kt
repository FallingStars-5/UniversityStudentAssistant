package com.srtp.assistant.logic.model

data class CourseSettings(
    var showingLessonTable:String = "default",//正在展示的课程表名，支持多张课程表
    var lessonTimeList:List<CourseTime>?=null,//上课时间表
    var weekSum: Int=21,                      //每学期总周数
    var background:String="",                 //保存课程表背景图片的路径，初始为空,使用drawable下的默认图片为背景
    var backgroundType: Int = 1,              //1表示图片背景，0表示纯色背景
    var sameClassBetweenTime:Int = 5,         //同一讲中课间休息时间
    var differentClassBetweenTime:Int = 15,   //不同讲中课间休息时间
    var classTime:Int=45,                     //每节课的上课时间
    var isRemindClass:Int = 1,                //是否启用上课提醒功能，1表示启用，0表示关闭
    var isShowWeekend:Int = 1,                //是否在课表中展示周末，1表示展示，0表示不展示
    var remindMinuteTime:Int = 20,            //距上课多少分钟时发送提醒消息
    var courseSumNumber:Int = 15,             //每天课程的总节数
    var nowWeek:Int=1,                        //当前周，默认为1，设置开学时间之后自动调整
    var startDate:String = "")        //开学日期，用于确定当前周

data class CourseTime(var start:MyTime,var end:MyTime) //表示课程开始时间和结束时间

data class MyTime(var hour:Int,var minute:Int){  //表示某个时间的小时和分钟

    /**
     * 重载加法运算符，进行两个时间的加法
     */
    operator fun plus(other: MyTime):MyTime{
        val result = MyTime(0,0)
        if (other.minute+minute>=60){
            result.minute = other.minute+minute-60
            result.hour += 1
        }
        else{
            result.minute = other.minute+minute
        }

        result.hour += other.hour+hour

        if (result.hour>=24){
            result.hour -= 24
        }

        return result
    }

    /**
     * 重载减法运算符，当被减数小于减数时返回0，0
     */
    operator fun minus(other:MyTime):MyTime?{
        val result = MyTime(0,0)
        if (minute <other.minute){
            result.minute = minute + 60 - other.minute
            hour--
        }else{
            result.minute = minute - other.minute
        }
        if (hour<other.hour){
            return null
        }
        result.hour = hour - other.hour
        return result
    }
}
