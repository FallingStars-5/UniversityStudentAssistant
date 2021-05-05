package com.srtp.assistant.logic

import android.content.Context
import com.srtp.assistant.logic.dao.*
import com.srtp.assistant.logic.model.*

object Repository {

    /**
     * 实现作业数据增删改查接口
     */
    fun addWork(work:Work) = WorkDao.addWork(work)

    fun updateWorkById(work:Work) = WorkDao.updateWorkById(work)

    fun findWorkById(id:Long) = WorkDao.findWorkById(id)

    fun findAllWork() = WorkDao.findAllWork()

    fun findAllNotCompletedWork() = WorkDao.findAllNotCompletedWork()

    fun findAllNotCompletedAndNotRemindWork() = WorkDao.findAllNotCompletedAndNotRemindWork()

    fun findAllCompletedWork() = WorkDao.findAllCompletedWork()

    fun findAllOvertimeNotCompletedWork() = WorkDao.findAllOvertimeNotCompletedWork()

    fun findWorkByFirstSaveTime(time:Long) = WorkDao.findWorkByFirstSaveTime(time)

    fun deleteWorkById(id:Long) = WorkDao.deleteWorkById(id)

    fun deleteAllWorkByFirstSaveTimeStamp(time:Long) = WorkDao.deleteAllWorkByFirstSaveTimeStamp(time)

    fun deleteAllCompletedWork() = WorkDao.deleteAllCompletedWork()

    fun deleteAllOvertimeAndCompletedWork() = WorkDao.deleteAllOvertimeAndCompletedWork()

    fun deleteAllBeforeDaysAndCompletedWork(days:Long) = WorkDao.deleteAllBeforeDaysAndCompletedWork(days)


    /**
     * 实现课程数据增删查改接口
     */
    fun addCourse(course: Course) = CourseDao.addCourse(course)

    fun findCourseByStartSectionAndWeek(courseTableName:String,startSection: Int, week: Int) =
                        CourseDao.findCourseByStartSectionAndWeek(courseTableName,startSection,week)

    fun updateCourseById(course:Course) = CourseDao.updateCourseById(course)

    fun findCourseById(id: Long) = CourseDao.findCourseById(id)

    fun deleteCourseById(id:Long) = CourseDao.deleteCourseById(id)

    fun deleteLessonTableByTableName(tableName:String) =
                            CourseDao.deleteLessonTableByTableName(tableName)

    fun findCourseNameByTable(courseTableName:String) = CourseDao.findCourseNameByTable(courseTableName)

    fun findCourseBySectionAndWeek(courseTableName:String,section:Int,week:Int) =
                            CourseDao.findCourseBySectionAndWeek(courseTableName,section, week)

    fun findAllCourse(courseTableName:String) = CourseDao.findAllCourse(courseTableName)

    fun findAllCourseByWeek(courseTableName:String,week: Int) =
                            CourseDao.findAllCourseByWeek(courseTableName,week)

    fun deleteCourseByStartSectionAndWeek(courseTableName:String,startSection: Int, week: Int) =
                            CourseDao.deleteCourseByStartSectionAndWeek(courseTableName,startSection, week)


    /**
     * 实现实时校园接口
     */
    fun addCampusAddress(campusAddress: CampusAddress) = CampusAddressDao.addCampusAddress(campusAddress)

    fun findCampusAddressById(id:Long) = CampusAddressDao.findCampusAddressById(id)

    fun findCampusAddressBySortName(sortName: String) = CampusAddressDao.findCampusAddressBySortName(sortName)

    fun findAllCampusAddressName() = CampusAddressDao.findAllCampusAddressName()

    fun updateCampusAddressById(campusAddress: CampusAddress) = CampusAddressDao.updateCampusAddressById(campusAddress)

    fun deleteCampusAddressById(id:Long) = CampusAddressDao.deleteCampusAddressById(id)

    fun deleteCampusAddressBySortName(sortName:String) = CampusAddressDao.deleteCampusAddressBySortName(sortName)

    /**
     * 实现作业设置接口
     */
    fun saveWorkSettings(context: Context,workSettings: WorkSettings) = WorkSettingsDao.saveWorkSettings(context,workSettings)

    fun getSavedWorkSettings(context: Context) = WorkSettingsDao.getSavedWorkSettings(context)

    fun isWorkSettingsSaved(context: Context) = WorkSettingsDao.isWorkSettingsSaved(context)

    /**
     * 实现课程设置接口
     */
    fun saveCourseSettings(context: Context,courseSettings: CourseSettings) = CourseSettingDao.saveCourseSettings(context,courseSettings)

    fun getSavedCourseSettings(context: Context) = CourseSettingDao.getSavedCourseSettings(context)




    /**
     * 实现系统设置接口
     */
    fun saveSystemInfo(context: Context,systemSettings: SystemSettings) = SystemInfoDao.saveSystemInfo(context,systemSettings)

    fun getSavedSystemInfo(context: Context) = SystemInfoDao.getSavedSystemInfo(context)

    fun isSystemInfoSaved(context: Context) = SystemInfoDao.isSystemInfoSaved(context)



}