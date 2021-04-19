package com.srtp.assistant.ui.settings.courseSettings

import android.content.Context
import androidx.lifecycle.ViewModel
import com.srtp.assistant.logic.Repository
import com.srtp.assistant.logic.model.CourseSettings
import java.util.*

class CourseSettingsViewModel:ViewModel() {

    var courseSettings = CourseSettings()

    private val calendar: Calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)+1
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)
    val week = calendar.get(Calendar.DAY_OF_WEEK)

    fun saveCourseSettings(context: Context){
        Repository.saveCourseSettings(context,courseSettings)
    }
}