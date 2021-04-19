package com.srtp.assistant.logic.dao

import android.content.Context
import androidx.core.content.edit
import com.srtp.assistant.logic.model.CourseSettings
import com.srtp.assistant.logic.model.CourseTime
import com.srtp.assistant.logic.model.MyTime
import com.google.gson.Gson
import com.srtp.assistant.logic.model.getClassTime

object CourseSettingDao {

    /**
     * 初始化课程表信息
     */
    private fun initCourseSettings():CourseSettings{
        val courseSettings = CourseSettings()
        courseSettings.startDate = "未设置"
        courseSettings.lessonTimeList = ArrayList()
        for (i in 0 until courseSettings.courseSumNumber){

            getClassTime(i+1).startTime.split(":")[0].toInt()

            val t1 = MyTime(getClassTime(i+1).startTime.split(":")[0].toInt(),
                            getClassTime(i+1).startTime.split(":")[1].toInt())

            val t2 = MyTime(getClassTime(i+1).endTime.split(":")[0].toInt(),
                            getClassTime(i+1).endTime.split(":")[1].toInt())

            val course = CourseTime(t1,t2)

            (courseSettings.lessonTimeList as ArrayList<CourseTime>).add(course)
        }
        return courseSettings
    }

    /**
     * 保存课程设置数据
     * @param context:Context
     * @param courseSettings:CourseSettings
     */
    fun saveCourseSettings(context:Context,courseSettings: CourseSettings){
        context.getSharedPreferences("assistant",Context.MODE_PRIVATE).edit {
            putString("courseSettings",Gson().toJson(courseSettings))
        }
    }

    /**
     * 获取已保存的课程设置数据
     * @param context:Context
     */
    fun getSavedCourseSettings(context: Context):CourseSettings{

        if (!isCourseSettingsSaved(context)){  //如果为保存，则先进行初始化
            saveCourseSettings(context,initCourseSettings())
        }

        val courseSettingsJson = context.getSharedPreferences("assistant",Context.MODE_PRIVATE).getString("courseSettings","")
        return Gson().fromJson(courseSettingsJson,CourseSettings::class.java)
    }

    /**
     * 判断课程设置数据是否已保存
     * @param context:Context
     */
    private fun isCourseSettingsSaved(context: Context):Boolean{
        return context.getSharedPreferences("assistant",Context.MODE_PRIVATE).contains("courseSettings")
    }
}