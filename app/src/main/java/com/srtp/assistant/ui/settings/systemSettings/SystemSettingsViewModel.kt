package com.srtp.assistant.ui.settings.systemSettings

import android.content.Context
import androidx.lifecycle.ViewModel
import com.srtp.assistant.logic.Repository
import com.srtp.assistant.logic.model.SystemSettings

class SystemSettingsViewModel:ViewModel() {

    var systemSettings = SystemSettings()

    fun getSavedSystemSettings(context: Context): SystemSettings {
        if (Repository.isSystemInfoSaved(context)){
            return Repository.getSavedSystemInfo(context)
        }
        return SystemSettings()
    }
    fun saveSystemSettings(context: Context){
        Repository.saveSystemInfo(context,systemSettings)
    }
}