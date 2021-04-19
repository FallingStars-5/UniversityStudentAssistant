package com.srtp.assistant.ui.work

import android.content.Context
import androidx.lifecycle.ViewModel
import com.srtp.assistant.logic.Repository
import com.srtp.assistant.logic.model.WorkSettings

class WorkViewModel:ViewModel() {

    var workSetting = WorkSettings()

    var workList = Repository.findAllNotCompletedWork()

    fun showAllWork(){
        workList = Repository.findAllWork()
    }

    fun getSavedWorkSettings(context: Context) = Repository.getSavedWorkSettings(context)

    fun saveWorkSettings(context: Context){
        Repository.saveWorkSettings(context,workSetting)
    }

    fun showCompletedWork(){
        workList = Repository.findAllCompletedWork()
    }

    fun showNotCompletedWork(){
        workList = Repository.findAllNotCompletedWork()
    }

    fun showOverTimeAndNotCompletedWork(){
        workList = Repository.findAllOvertimeNotCompletedWork()
    }
}