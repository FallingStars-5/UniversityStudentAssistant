package com.srtp.assistant.ui.course

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.srtp.assistant.logic.Repository
import com.srtp.assistant.logic.model.Course
import com.srtp.assistant.logic.model.CourseSettings
import java.lang.StringBuilder
import java.util.*
import kotlin.collections.ArrayList

class CourseViewModel:ViewModel() {

    var course = Course()
    var weekList = StringBuilder("000000000000000000000")
    var listCheckedWeek = ArrayList<Int>()

    //获取当前系统时间的时、分、年、月、日、星期
    private val calendar: Calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)+1
    val day = calendar.get(Calendar.DAY_OF_MONTH)
//    val hour = calendar.get(Calendar.HOUR_OF_DAY)
//    val minute = calendar.get(Calendar.MINUTE)
    val week = calendar.get(Calendar.DAY_OF_WEEK)

    val choosePhoto = 2

    var uriTempFile: Uri? = null
    var courseSettings = CourseSettings()

    /**
     * 保存课程
     */
    fun saveCourse(){
        course.classWeekNum = weekList.toString() //保存上课周
        Repository.addCourse(course)
        course = Course()
    }

    /**保存课程设置
     * @param context:Context
     */
    fun saveCourseSettings(context: Context){
        Repository.saveCourseSettings(context,courseSettings)
    }


    /**
     * 修改课程
     */
    fun updateCourse(){
        Repository.deleteCourseById(course.id)
        Repository.addCourse(course)
        //Repository.updateCourseById(course)
        course = Course()
    }

    /**是否需要修改课程
     * @param course_1:Course
     */
    fun isNeedChange(course_1: Course):Boolean{
        course.classWeekNum = weekList.toString() //保存上课周
        return course_1!=course
    }

    /**根据开始节、课表名和星期删除课程
     * @param courseTableName:String
     * @param startSection:Int
     * @param week:Int
     */
    fun deleteCourseByStartSectionAndWeek(courseTableName:String,startSection: Int, week: Int) =
        Repository.deleteCourseByStartSectionAndWeek(courseTableName,startSection,week)

    /**通过课表名、开始节和星期查找课程
     * @param courseTableName:String
     * @param startSection:Int
     * @param week:Int
     */
    fun courseListWeekAndSection(courseTableName:String,startSection: Int, week: Int) =
        Repository.findCourseByStartSectionAndWeek(courseTableName,startSection, week)

    /**
     * 通过课表名删除一张课表中的所有课程
     */
    fun deleteAllCourseByTable(tableName:String) = Repository.deleteLessonTableByTableName(tableName)
}