package com.srtp.assistant.ui.settings.workSettings

import android.content.Context
import androidx.lifecycle.ViewModel
import com.srtp.assistant.logic.Repository
import com.srtp.assistant.logic.model.WorkSettings

class WorkSettingsViewModel:ViewModel() {

    var workSettings = WorkSettings()

    fun getSavedWorkSettings(context: Context): WorkSettings {
        if (Repository.isWorkSettingsSaved(context)){
            return Repository.getSavedWorkSettings(context)
        }
        return WorkSettings()
    }
    fun saveWorkSettings(context: Context){
        Repository.saveWorkSettings(context,workSettings)
    }
}