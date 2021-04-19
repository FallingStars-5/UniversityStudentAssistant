package com.srtp.assistant.ui.work.addWork

import android.app.Dialog
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.lifecycle.ViewModel
import com.srtp.assistant.AssistantApplication
import com.srtp.assistant.logic.Repository
import com.srtp.assistant.logic.model.Work
import java.util.*

open class AddWorkViewModel:ViewModel() {
    var addingWork = Work()
    var date: String  = ""
    var time:String = ""
    var dia: Dialog? = null

    private val calendar: Calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)+1
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)
    var uriTempFile: Uri? = null


    /**通过课表名查找课程记录并返回列表
     * @param context:AddWorkActivity
     */
    fun getCourseNameList(context: AddWorkActivity)  =
        Repository.findCourseNameByTable(AssistantApplication.getSavedCourseSettings(context).showingLessonTable)

    //用于保存拍照图片的uri
    var mCameraUri: Uri? = null

    // 用于保存图片的文件路径，Android 10以下使用图片路径访问图片
    var mCameraImagePath: String? = null

    // 是否是Android 10以上手机
    val isAndroidQ = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    val choosePhoto = 2

    fun saveWork(){
        if (date==""){
            date = "$year-$month-$day"
        }
        if (time==""){
            time = "$hour:$minute"
        }
        addingWork.time = AssistantApplication.getTime("$date $time")
        //Log.e("dan",addingWork.time.toString())
        addingWork.firstSaveTimeStamp = System.currentTimeMillis()
        Log.e("dan","添加作业时保存的时间戳：${addingWork.firstSaveTimeStamp}")
        Repository.addWork(addingWork)
    }
}