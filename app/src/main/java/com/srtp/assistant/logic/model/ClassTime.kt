package com.srtp.assistant.logic.model

data class ClassTime(var startTime:String, var endTime:String)

    //用map来进行从上课时间到节数之间的映射，以便于查询
    private val time = mapOf(
        1 to ClassTime("08:00", "08:45"),
        2 to ClassTime("08:50", "09:35"),
        3 to ClassTime("09:50", "10:35"),
        4 to ClassTime("10:40", "11:25"),
        5 to ClassTime("11:30", "12:15"),
        6 to ClassTime("14:00", "14:45"),
        7 to ClassTime("14:50", "15:35"),
        8 to ClassTime("15:50", "16:35"),
        9 to ClassTime("16:40", "17:25"),
        10 to ClassTime("17:30", "18:15"),
        11 to ClassTime("19:30", "20:15"),
        12 to ClassTime("20:20", "21:05"),
        13 to ClassTime("21:10", "21:55"),
        14 to ClassTime("22:10", "22:55"),
        15 to ClassTime("23:00", "23:45")
    )

/**
 * 通过节数获取上课时间
 */
fun getClassTime(i: Int): ClassTime {
        return time[i] ?: (time[1] ?: error(""))
    }
