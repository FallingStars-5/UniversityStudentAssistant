package com.srtp.assistant

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.os.Environment.getExternalStoragePublicDirectory
import android.util.Log
import com.srtp.assistant.logic.Repository
import com.srtp.assistant.logic.model.*
import com.yalantis.ucrop.UCrop
import org.litepal.LitePal
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.lang.Exception
import java.nio.channels.Channel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class AssistantApplication: Application() {
    companion object{

        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context

        @SuppressLint("SimpleDateFormat")
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm")

        /**
         * 字符串转时间戳
         */
        fun getTime(timeString: String): Long {
            try {

                return sdf.parse(timeString)!!.time

            }catch (e: ParseException){
                e.printStackTrace()
            }
            return -1
        }

        /**
         * 时间戳转字符串
         */
        fun getStrTime(l: Long?): String = sdf.format(l)

        /**
         * 计算给定时间与当前时间的日期之差，返回天、小时、分钟
         * 参数time是 "yyyy-MM-dd HH:mm:ss" 类型字符串
         * 该方法返回一个int类型的List列表，列表项中依次为时、分
         */
        fun getDifferTime(strTime: String): String {
            var s = "已超时"
            try {
                val date = Date(System.currentTimeMillis())
                val times = sdf.parse(strTime)!!.time - date.time
                if(times<1000*60) return s
                val days = times / (1000*60*60*24)
                val hours = times / (1000*60*60) - days*24
                val minutes = times / (1000*60) - days*24*60 - hours*60
                s = when {
                    days>0 -> {
                        "$days 天 $hours 小时"
                    }
                    hours>0 -> {
                        "$hours 小时"
                    }
                    else -> {
                        ""
                    }
                }

            }catch (e: Exception){
                e.printStackTrace()
            }
            return s
        }

        /**
         * 判断时间是否在时间段内
         * @param nowTime
         * @param beginTime
         * @param endTime
         * @return
         */

        fun belongCalendar(nowTime:Date,beginTime:Date,endTime:Date):Boolean{
            val date = Calendar.getInstance()
            date.time = nowTime

            val begin = Calendar.getInstance()
            begin.time = beginTime

            val end = Calendar.getInstance()
            end.time = endTime

            return date.after(begin)&&date.before(end)
        }


        /**
         * 判断时间是否小于某个时间
         * @param nowTime
         * @param beginTime
         * @return
         */
        fun beforeCalendar(nowTime:Date,beginTime:Date):Boolean{
            val date = Calendar.getInstance()
            date.time = nowTime

            val begin = Calendar.getInstance()
            begin.time = beginTime

            return date.before(begin)
        }

        /**
         * 得到Int型的星期
         */
        fun getWeek(week:Int) :Int{
            if (week==1) return 7
            return week-1
        }

        /**
         * 将小于10的数转换字符串时前面补零
         */
        fun formatStrNumber(i:Int):String{
            if (i<10){
                return "0$i"
            }
            return "$i"
        }

        /**
         * 得到课程设置的数据
         */
        fun getSavedCourseSettings(context: Context): CourseSettings {
            val calendar: Calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)+1
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)
            val week = calendar.get(Calendar.DAY_OF_WEEK)
            //判断当前周
            val s = Repository.getSavedCourseSettings(context)
            if(s.startDate!=""){
                val w = getDayOfWeek(s.startDate)
                //val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
                try {
                    //将开学日期转换为本周的星期一的日期
                    if (w!=2) {
                        if (w == 1) {
                            s.startDate = timeAddDays(s.startDate, -6)
                        } else {
                            s.startDate = timeAddDays(s.startDate, -w + 2)
                        }
                    }

                    //将当前日期转换为当前周的星期一
                    val w1 = week
                    var nowWeekMonday = "$year-$month-$day"
                    if (w1!=2){
                        nowWeekMonday = if (w1==1){
                            timeAddDays(nowWeekMonday,-6)
                        }else{
                            timeAddDays(nowWeekMonday,-w1+2)
                        }
                    }
                    //Log.e("dan","本周周一日期：$nowWeekMonday")
                    var k = 1
                    val start = s.startDate

                    //Log.e("dan","开学周一日期：$start")

                    val format = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)

                    //Log.e("dan","周总数：${s.weekSum}")

                    val nowWeekMondayStamp = format.parse(nowWeekMonday)!!.time/(1000*60*60*24)  //转换为天数
                    val startStamp = format.parse(start)!!.time / (1000*60*60*24)  //转换为天数

                    val nowWeek = ((nowWeekMondayStamp - startStamp)/7).toInt()

                    if (nowWeek<1||nowWeek>s.weekSum){
                        s.nowWeek = 0
                    }else{
                        s.nowWeek = nowWeek+1
                    }
                    //s.nowWeek = k
                    Repository.saveCourseSettings(context,s)

                }catch (e: Exception){
                    e.printStackTrace()
                }
            }else{
                s.nowWeek = -1
                Repository.saveCourseSettings(context,s)
            }
            return Repository.getSavedCourseSettings(context)
        }

        /*
        日期加指定天数，得到新的日期
         */
        fun timeAddDays(strDate:String,days:Int):String{

            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
            try {
                val date = sdf.parse(strDate)
                val rightNow = Calendar.getInstance()
                rightNow.time = date
                rightNow.add(Calendar.DAY_OF_YEAR,days)
                return sdf.format(rightNow.time)
            }catch (e:Exception){
                e.printStackTrace()
            }
            return ""
        }

        /*
        *返回某个日期的星期
         */
        fun getDayOfWeek(dateTime:String):Int{
            val cal = Calendar.getInstance()
            if (dateTime == ""){
                cal.time = Date(System.currentTimeMillis())
            }else{
                val sdf = SimpleDateFormat("yyyy-MM-dd",Locale.CHINA)
                var date:Date?
                try {
                    date = sdf.parse(dateTime)
                }catch (e:Exception){
                    date = null
                    e.printStackTrace()
                }
                if (date!=null){
                    cal.time = Date(date.time)
                }
            }
            return cal.get(Calendar.DAY_OF_WEEK)
        }

        /*
        得到ClassTime内型的上课时间
         */
        fun getClassTime(context: Context,course: Course):ClassTime{

            val courseSettings = getSavedCourseSettings(context)
            var startTimeH = "${courseSettings.lessonTimeList?.get(course.sectionStart-1)?.start?.hour!!}"
            if (startTimeH.toInt()<10){
                startTimeH = "0$startTimeH"
            }
            var startTimeM = "${courseSettings.lessonTimeList?.get(course.sectionStart-1)?.start?.minute!!}"
            if (startTimeM.toInt()<10){
                startTimeM = "0$startTimeM"
            }

            var endTimeH = "${courseSettings.lessonTimeList?.get(course.sectionStart+course.sectionNum-2)?.end?.hour!!}"
            if (endTimeH.toInt()<10){
                endTimeH = "0$endTimeH"
            }
            var endTimeM = "${courseSettings.lessonTimeList?.get(course.sectionStart+course.sectionNum-2)?.end?.minute!!}"
            if (endTimeM.toInt()<10){
                endTimeM = "0$endTimeM"
            }

            return ClassTime("$startTimeH:$startTimeM","$endTimeH:$endTimeM")
        }

        /**
         * 得到String类型的星期
         */
        fun getStringWeek(week:Int):String{
            return when(week){
                1 -> "星期日"
                2 -> "星期一"
                3 -> "星期二"
                4 -> "星期三"
                5 -> "星期四"
                6 -> "星期五"
                7 -> "星期六"
                else -> ""
            }
        }

        /**
         * 得到String类型的星期
         */
        fun getStringWeekByRealWeek(week:Int):String{
            return when(week){
                1 -> "星期一"
                2 -> "星期二"
                3 -> "星期三"
                4 -> "星期四"
                5 -> "星期五"
                6 -> "星期六"
                7 -> "星期日"
                else -> ""
            }
        }


        /**保存图片到本地并返回其路径
         *
         */
        fun savePictureToLocal(data: Intent?):String {
            val croppedFileUri = data?.let { UCrop.getOutput(it) }
            //获取默认的下载目录
            val downloadsDirectoryPath =
                getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
            val filename = croppedFileUri?.let {
                String.format(
                    "%d_%s",
                    Calendar.getInstance().timeInMillis,
                    it.lastPathSegment
                )
            }
            val saveFile = File(downloadsDirectoryPath, filename!!)
            //保存下载的图片
            var filePath = ""
            lateinit var inStream: FileInputStream
            lateinit var outStream: FileOutputStream
            lateinit var inChannel: Channel
            lateinit var outChannel: Channel
            try {
                inStream = FileInputStream(File(croppedFileUri.path!!))
                outStream = FileOutputStream(saveFile)
                inChannel = inStream.channel
                outChannel = outStream.channel
                inChannel.transferTo(0, inChannel.size(), outChannel)
                filePath = saveFile.absolutePath
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                try {
                    outChannel.close()
                    outStream.close()
                    inChannel.close()
                    inStream.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            return filePath
        }
    }

    override fun onCreate() {
        super.onCreate()
        LitePal.initialize(this)
        context = applicationContext
    }
}