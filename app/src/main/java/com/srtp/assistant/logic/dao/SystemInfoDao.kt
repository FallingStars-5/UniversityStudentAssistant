package com.srtp.assistant.logic.dao

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import com.srtp.assistant.logic.model.SystemSettings

object SystemInfoDao {

    /**保存系统设置信息
     * @param context:Context
     * @param systemSettings: SystemSettings
     */
    fun saveSystemInfo(context: Context,systemSettings: SystemSettings){
        context.
        getSharedPreferences("assistant", Context.MODE_PRIVATE).edit {
            putString("systemSettings", Gson().toJson(systemSettings))
        }
    }

    /**获取已保存的系统设置数据
     * @param context:Context
     * @return SystemSettings对象
     */
    fun getSavedSystemInfo(context: Context):SystemSettings{
        val systemInfoJson = context.
        getSharedPreferences("assistant", Context.MODE_PRIVATE).getString("systemSettings","")
        return Gson().fromJson(systemInfoJson,SystemSettings::class.java)
    }

    /**是否已保存
     * @param context:Context
     * @return Boolean
     */
    fun isSystemInfoSaved(context: Context) = context.
        getSharedPreferences("assistant", Context.MODE_PRIVATE).contains("systemSettings")
}