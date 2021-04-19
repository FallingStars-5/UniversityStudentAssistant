package com.srtp.assistant.logic.dao

import com.srtp.assistant.logic.model.Course
import org.litepal.LitePal
import org.litepal.extension.delete
import org.litepal.extension.deleteAll
import org.litepal.extension.find
import java.lang.Exception
import kotlin.collections.ArrayList

object CourseDao {

    /**
     * 增加一条课表记录
     * @param course:Course 待保存的课程
     */
    fun addCourse(course: Course){
        course.save()
    }

    /**
     * 根据id修改一条作业记录
     * @param course Course类型，表示待修改的课程
     */
    fun updateCourseById(course:Course){
        course.update(course.id)
    }

    /**
     * 根据id查询一条课程记录并返回
     * @param id : Long
     * @return Course 返回一条课程记录
     */
    fun findCourseById(id: Long) = LitePal.find<Course>(id)

    /**
     * 根据指定节和星期查询当前课程并返回
     * @param courseTableName:String 表示课程表的名称
     * @param section:Int 表示开始节
     * @param week:Int 表示星期
     * @return 返回满足条件的所有课程
     */
    fun findCourseBySectionAndWeek(courseTableName:String,section:Int,week:Int) =
        LitePal.where("sectionStart <= ? and and week = ? and lessonTableName = ?",
                                    "$section","$week",courseTableName).find<Course>()


    /**
     * 查找一张课程表内所有课程并根据星期和开始的节数排序
     * @param courseTableName:String 表示课程表的名称
     */
    fun findAllCourse(courseTableName:String):List<Course>{
        val list = ArrayList<Course>()
        for (i in 1..7){
            list.addAll(
                LitePal.where("week = ? and lessonTableName = ?","$i", courseTableName)
                    .order("sectionStart").find())
        }
        return list
    }

    //fun findCourses() = LitePal.findAll<Course>()

    /**
     * 查找指定周所有有课的课程并返回
     * @param courseTableName:String 表示课程表名称
     * @param week:Int 表示第几周
     */
    fun findAllCourseByWeek(courseTableName:String,week: Int):List<Course>{
        val list = ArrayList<Course>()
        val allCourse = findAllCourse(courseTableName)
        try {
            for (i in allCourse){
                val s = StringBuffer(i.classWeekNum)
                if (s[week-1]=='1'){
                    list.add(i)
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
        return list
    }


    /**
     * 获取课表中所有的课程名称
     * @param courseTableName:String 表示课程表名称
     * @return List<String> 返回所有课程的名称
     */
    fun findCourseNameByTable(courseTableName:String):ArrayList<String>{

        val list1 =  LitePal.where("lessonTableName = ?", courseTableName).find<Course>()
        val list = ArrayList<String>()
        for (i in list1){
            list.add(i.name)
        }
        return list
    }


    /**
     * 通过开始节和星期查找课程并返回列表
     * @param courseTableName:String 表示课程表名称
     * @param startSection: Int 表示开始节
     * @param week:Int 星期
     */
    fun findCourseByStartSectionAndWeek(courseTableName:String,startSection: Int, week: Int) =
        LitePal.where("sectionStart = ? and week = ? and lessonTableName = ?","$startSection","$week",courseTableName).find<Course>()


    /**
     * 在指定课表中删除开始节和星期满足条件的课程记录
     *
     */
    fun deleteCourseByStartSectionAndWeek(courseTableName:String,startSection: Int, week: Int){
        LitePal.deleteAll(Course::class.java,"sectionStart = ? and week = ? and lessonTableName = ?","$startSection","$week",courseTableName)
    }

    /**
     * 根据id删除一条课程记录
     */
    fun deleteCourseById(id:Long){
        LitePal.delete<Course>(id)
    }

    /**
     * 根据课表名称删除一整张课表
     * @param courseTableName:String 表示课程表名称
     */
    fun deleteLessonTableByTableName(courseTableName:String){
        LitePal.deleteAll<Course>("lessonTableName = ?",courseTableName)
    }
}
