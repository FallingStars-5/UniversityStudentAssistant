package com.srtp.assistant.ui.work.workDetail

import androidx.lifecycle.ViewModel
import com.srtp.assistant.AssistantApplication
import com.srtp.assistant.logic.Repository
import com.srtp.assistant.logic.model.Work
import java.util.*

class WorkDetailViewModel:ViewModel() {

    private val calendar: Calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)+1
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)
    var firstSaveTimeStamp:Long = 0
    var date = "" //初始值存入当前已保存的日期字符串
    var time = "" //初始值存入当前已保存的时间字符串

    var work = Work()

    fun deleteWork(){
        Repository.deleteAllWorkByFirstSaveTimeStamp(firstSaveTimeStamp)
    }

    fun findWorkByFirstSaveTime(time:Long):Work?{
        val work = Repository.findWorkByFirstSaveTime(time)
        if (work!=null){
            val strTimeList = AssistantApplication.getStrTime(work.time).split(" ")
            this.date = strTimeList[0]
            this.time = strTimeList[1]
        }
        return work
    }

    /**
     * 修改作业
     */
    fun updateWork(){
        if (date==""){
            date = "$year-$month-$day"
        }
        if (time==""){
            time = "$hour:$minute"
        }
        work.time = AssistantApplication.getTime("$date $time")
        deleteWork()
        work.firstSaveTimeStamp = firstSaveTimeStamp

        val w = Work()

        w.firstSaveTimeStamp = work.firstSaveTimeStamp
        w.content = work.content
        w.courseName = work.courseName
        w.isReminded = work.isReminded
        w.isCompleted = work.isCompleted
        w.time = work.time
        w.PicturesOnePath = work.PicturesOnePath
        w.PicturesTwoPath = work.PicturesTwoPath
        w.PicturesThreePath = work.PicturesThreePath

        //Repository.updateWorkById(work)
        Repository.addWork(w)
    }

}