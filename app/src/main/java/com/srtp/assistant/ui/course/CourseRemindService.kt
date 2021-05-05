package com.srtp.assistant.ui.course

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
import com.srtp.assistant.AssistantApplication
import com.srtp.assistant.R
import com.srtp.assistant.logic.Repository
import com.srtp.assistant.logic.model.Course
import com.srtp.assistant.logic.model.Work
import java.util.*
import kotlin.collections.ArrayList

@Suppress("SameParameterValue", "DEPRECATION")
class CourseRemindService : Service() {

    private val handle = @SuppressLint("HandlerLeak")
    object : Handler() {
        @SuppressLint("HandlerLeak")
        @RequiresApi(Build.VERSION_CODES.N)
        override fun handleMessage(@SuppressLint("HandlerLeak") msg: Message) {
            msg.let {
                val course = msg.obj
                sendCourseMessage(course as Course,msg.arg1)
            }
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        /**
        - 异步线程
         */
        Thread {

            val msg = Message.obtain()  ///!!!!!!!!!!!!!!!
            val courseSettings = AssistantApplication.getSavedCourseSettings(this)
            val remindTime = courseSettings.remindMinuteTime //天

            val courseList = Repository.findAllCourse(courseSettings.showingLessonTable) as ArrayList
            while (courseList.isNotEmpty()){
                val date = Date(System.currentTimeMillis())
                val time = ((courseList[0].sectionStart - date.time) / (1000*60)).toInt()
                if (time<remindTime&&time>=remindTime-1){
                    msg.obj = courseList[0]
                    msg.arg1 = time
                    //返回主线程
                    handle.sendMessage(msg)
                    break
                }
                courseList.removeAt(0)
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
    private fun sendCourseMessage(item: Course, restTime: Int) {
        val str = "主人！您的 ${item.name} 课程还有 ${restTime - 1} 分钟就上课了，记得准时上课哦！"
        val intent = Intent(this, CourseActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        val channelId = createNotificationChannel(
            "my_course_service",
            "course_remind_service",
            NotificationManager.IMPORTANCE_HIGH
        )
        val notification = NotificationCompat.Builder(
            this.applicationContext,
            channelId!!
        )
            .setContentTitle("小助上课提醒")
            .setContentText(str)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(105, notification.build())
    }

}
