package com.srtp.assistant.ui.work

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Message
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModelProvider
import com.srtp.assistant.AssistantApplication
import com.srtp.assistant.R
import com.srtp.assistant.logic.Repository
import com.srtp.assistant.logic.model.Work
import com.srtp.assistant.ui.work.workDetail.WorkDetailActivity
import java.util.*
import kotlin.collections.ArrayList

@Suppress("SameParameterValue", "DEPRECATION")
class WorkRemindService:Service() {

    private val handle = @SuppressLint("HandlerLeak")
    object : Handler() {
        @SuppressLint("HandlerLeak")
        @RequiresApi(Build.VERSION_CODES.N)
        override fun handleMessage(@SuppressLint("HandlerLeak") msg: Message) {
            msg.let {
                val work = msg.obj
                sendWorkMessage(work as Work,msg.arg1)
            }
        }
    }
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        /**
        - 异步线程
         */
        Thread {

            val msg = Message.obtain()
            val remindTime = Repository.getSavedWorkSettings(this).remindDayTime //天

            val workList = Repository.findAllNotCompletedAndNotRemindWork() as ArrayList
            while (workList.isNotEmpty()){
                val date = Date(System.currentTimeMillis())
                val time = ((workList[0].time - date.time) / (1000*60*60*24)).toInt()
                if (workList[0].isReminded==0&&(time<remindTime)){
                    msg.obj = workList[0]
                    msg.arg1 = time
                    workList[0].isReminded = 1
                    Repository.updateWorkById(workList[0])
                    //返回主线程
                    handle.sendMessage(msg)
                    break
                }
                workList.removeAt(0)
            }


        }.start()

        return super.onStartCommand(intent, flags, startId)
    }

    //设置大图标
    private fun createNotificationChannel(
        channelID: String,
        channelNAME: String,
        level: Int
    ): String? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(channelID, channelNAME, level)
            manager.createNotificationChannel(channel)
            channelID
        } else {
            null
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun sendWorkMessage(item: Work, restTime: Int) {
        val str: String = if (restTime > 0) {
            "主人！您的 ${item.courseName} 作业还有 $restTime 天就截止了，请尽快完成，以免错过哦(点击我查看作业内容)"
        } else {
            "主人！您的 ${item.courseName} 作业已截止，请尽快补做(点击我查看作业内容)"
        }
        val intent = Intent(this, WorkDetailActivity::class.java)
        intent.putExtra("work_id", item.firstSaveTimeStamp)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        val channelId = createNotificationChannel(
            "my_channel_ID",
            "my_channel_NAME",
            NotificationManager.IMPORTANCE_HIGH
        )
        val notification = NotificationCompat.Builder(
            this.applicationContext,
            channelId!!
        )
            .setContentTitle("小助作业提交提醒")
            .setContentText(str)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(100, notification.build())
    }
}