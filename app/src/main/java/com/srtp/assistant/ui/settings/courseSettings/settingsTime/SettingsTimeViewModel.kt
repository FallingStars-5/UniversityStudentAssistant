package com.srtp.assistant.ui.settings.courseSettings.settingsTime

import android.content.Context
import androidx.lifecycle.ViewModel
import com.srtp.assistant.AssistantApplication
import com.srtp.assistant.logic.Repository
import com.srtp.assistant.logic.model.CourseSettings

class SettingsTimeViewModel:ViewModel() {

    var courseSettings = CourseSettings()

    fun getSavedCourseSettings(context: Context): CourseSettings {
            return AssistantApplication.getSavedCourseSettings(context)
    }
    fun saveCourseSettings(context: Context){
        Repository.saveCourseSettings(context,courseSettings)
    }
}