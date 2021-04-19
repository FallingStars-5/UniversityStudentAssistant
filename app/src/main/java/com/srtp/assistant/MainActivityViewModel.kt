package com.srtp.assistant

import android.content.Context
import androidx.lifecycle.ViewModel
import com.srtp.assistant.logic.Repository
import com.srtp.assistant.logic.model.Course
import com.srtp.assistant.logic.model.CourseSettings
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivityViewModel:ViewModel() {

    private val calendar: Calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)+1
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)
    val week = calendar.get(Calendar.DAY_OF_WEEK)
    var courseSettings = CourseSettings()
    companion object{
        var nextCourseNumber = 0
    }

    /**
     * 获得作业设置数据
     */
    fun getSavedWorkSettings(context: Context) = Repository.getSavedWorkSettings(context)

    /**
     * 获取未完成作业列表
     */
    fun findAllNotCompletedWork() = Repository.findAllNotCompletedWork()

    /**
     * 保存课程设置
     */
    fun saveCourseSettings(context: Context){
        Repository.saveCourseSettings(context,courseSettings)
    }

    /**查找当前正在上课的课程
     * @param context:Context
     */
    fun nowCourseList(context: Context):List<Course>{
        val list = ArrayList<Course>()
        val df = SimpleDateFormat("HH:mm", Locale.CHINA)//hh表示12小时制，HH表示24小时制
        courseSettings = AssistantApplication.getSavedCourseSettings(context)
        val listCourse = Repository.findAllCourseByWeek(courseSettings.showingLessonTable,courseSettings.nowWeek)
        try {
            for (i in listCourse.indices){
                val classTime = AssistantApplication.getClassTime(context,listCourse[i])
                val now = df.parse(df.format(Date()))!! //当前时间
                val beginTime = df.parse(classTime.startTime)!!
                val endTime = df.parse(classTime.endTime)!!
                if (AssistantApplication.belongCalendar(now,beginTime,endTime)&&listCourse[i].week==AssistantApplication.getWeek(week)){
                    list.add(listCourse[i])
                    nextCourseNumber = i+1
                    break
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
        return list
    }


    /**查找下一讲课程，并以字符串形式返回
     * @param context:Context
     */
    fun nextCourseList(context: Context):String{
        var s = "暂无课程"
        val listCourse = Repository.findAllCourseByWeek(courseSettings.showingLessonTable,courseSettings.nowWeek) //本周所有课程
        var nextCourse = Course()
        var nextCourseAddDay = -1
        val realNowWeek = AssistantApplication.getWeek(week)
        courseSettings = AssistantApplication.getSavedCourseSettings(context)
        val df = SimpleDateFormat("HH:mm", Locale.CHINA)//hh表示12小时制，HH表示24小时制

        if (listCourse.isNotEmpty()) { //在本周课程内查找离当前时间最近的下一讲课程
            for (i in listCourse) {

                if (i.week >= realNowWeek) {   //查询星期大于等于当前星期时进行判断
                    if (i.week == realNowWeek) {  //今天之内的课程
                        val strBeginTime = AssistantApplication.getClassTime(context,i).startTime
                        val now = df.parse(df.format(Date()))!! //当前时间
                        val beginTime = df.parse(strBeginTime)!!
                        if (AssistantApplication.beforeCalendar(now, beginTime)) {
                            nextCourse = i
                            nextCourseAddDay = 0
                            break
                        }
                    } else if (i.week > realNowWeek) {
                        nextCourse = i
                        nextCourseAddDay = i.week - realNowWeek
                        break
                    }
                }
            }
        }
        if (nextCourseAddDay == -1) { //在本周之后的周内查找离当前时间最近的下一讲课程
            for (j in courseSettings.nowWeek+1..courseSettings.weekSum) {
                val l = Repository.findAllCourseByWeek(courseSettings.showingLessonTable,j)
                if (l.isNotEmpty()) {
                    nextCourse = l[0]
                    nextCourseAddDay =
                        (j - courseSettings.nowWeek) * 7 + nextCourse.week - realNowWeek
                    break
                }
            }
        }

        if (nextCourseAddDay!=-1){ //即找到了下一讲课程
            val strTime = AssistantApplication.timeAddDays("$year-$month-$day",nextCourseAddDay)
            val str = strTime.split("-")

            val classTime = AssistantApplication.getClassTime(context,nextCourse)

            val time = "${classTime.startTime}-${classTime.endTime}"

            s = "${nextCourse.name} | ${nextCourse.classRoom} | ${str[1]}-${str[2]} | ${AssistantApplication.getStringWeekByRealWeek(nextCourse.week)} | $time"
        }
        return s
    }
}