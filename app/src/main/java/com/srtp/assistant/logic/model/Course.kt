package com.srtp.assistant.logic.model

import org.litepal.crud.LitePalSupport

data class Course(
    var name:String="",                     //课程名
    var classRoom:String="",                //上课教室地点
    var teacher:String="",                  //上课教师姓名
    var sectionStart:Int=0,                 //该课程的开始节
    var sectionNum:Int = 3,                   //该课程有几节课
    var week:Int = 1,                       //星期，1，2，3，4，5，6，7分别表示星期一到星期日
    var classWeekNum:String="",             //该课程有课的周，第i位的字符表示第i+1周是否有课：1表示有课，0表示没有
    var credits:Int = 2,                    //学分
    var color:Int=0,                        //课程项的背景颜色
    var remark:String="",                   //备注
    var lessonTableName:String = "default", //课程表名称，支持创建多张课程表，添加课程时默认课表名为default

        ) :LitePalSupport(){                //支持LitePal进行数据库操作

    var id:Long = 0                         //定义在数据类的内部，表示值自增加

}