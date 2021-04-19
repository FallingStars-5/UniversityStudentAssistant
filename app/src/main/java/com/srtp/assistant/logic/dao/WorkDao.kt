package com.srtp.assistant.logic.dao

import com.srtp.assistant.logic.model.Work
import org.litepal.LitePal
import org.litepal.extension.delete
import org.litepal.extension.deleteAll
import org.litepal.extension.find
import java.util.*


object WorkDao {

    /**增加一条作业记录
     * @param work: Work
     */
    fun addWork(work: Work){
        work.save()
    }

    /**根据id修改一条作业记录
     * @param work:Work
     */
    fun updateWorkById(work: Work){
        work.update(work.id)
    }

    /**根据id查询一条作业记录并返回
     * @param id:Long
     */
    fun findWorkById(id:Long) = LitePal.find<Work>(id)

    /**查询所有未完成作业并返回列表
     * @return 返回Work类对象的List列表
     */
    fun findAllNotCompletedWork() = LitePal.where("isCompleted = ?", "0").order("time").find<Work>()

    /**查询所有未完成且尚未提醒的作业并返回列表
     * @return 返回Work类对象的List列表
     */
    fun findAllNotCompletedAndNotRemindWork() =
        LitePal.where("isCompleted = ? and isReminded = ?", "0","0").order("time").find<Work>()


    /**通过保存的时间戳来获取作业项
     *
     *  @return 返回Work类对象
     */
    fun findWorkByFirstSaveTime(time:Long):Work?{
        val list = LitePal.where("firstSaveTimeStamp = ?","$time").find<Work>()
        if (list.isNotEmpty()){
            return list[0]
        }
        return null
    }


    /**查询所有已完成作业并返回列表
     * @return 返回Work类对象的List列表
     */
    fun findAllCompletedWork() = LitePal.where("isCompleted = ?", "1").order("time").find<Work>()

    /**查询所有作业并返回列表
     * @return 返回Work类对象的List列表
     */
    fun findAllWork() = LitePal.where("").order("time").find<Work>()

    /**查询所有超过提交日期并且未完成的作业并返回列表
     * @return 返回Work类对象的List列表
     */
    fun findAllOvertimeNotCompletedWork():List<Work>{
        val nowTime = Date(System.currentTimeMillis()).time
        return LitePal.where("time < ? and isCompleted = ?","$nowTime","0").find()
    }

    /**
     * 根据id删除一条作业记录
     */
    fun deleteWorkById(id:Long){
        LitePal.delete<Work>(id)
    }

    /**
     * 删除所有已完成作业
     */
    fun deleteAllCompletedWork(){
        LitePal.deleteAll<Work>("isCompleted = ?","1")
    }

    /**
     * 删除超时且已完成作业
     */
    fun deleteAllOvertimeAndCompletedWork(){
        val nowTime = Date(System.currentTimeMillis()).time
        LitePal.deleteAll<Work>("isCompleted = ? and time < ?", "1","$nowTime")
    }

    /**
     * 根据第一次保存的时间戳删除作业项
     */
    fun deleteAllWorkByFirstSaveTimeStamp(time:Long){
        LitePal.deleteAll<Work>("firstSaveTimeStamp = ?","$time")
    }

    /**
     * 删除超过指定天数并且已完成的作业
     */
    fun deleteAllBeforeDaysAndCompletedWork(days: Long){
        val nowTime = Date(System.currentTimeMillis()).time
        val timeLength = nowTime - days*24*60*60*1000
        LitePal.deleteAll<Work>("time < ? and isCompleted = ?","$timeLength","1")
    }

}