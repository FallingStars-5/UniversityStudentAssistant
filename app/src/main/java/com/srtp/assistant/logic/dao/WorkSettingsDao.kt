package com.srtp.assistant.logic.dao

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import com.srtp.assistant.logic.model.WorkSettings

object WorkSettingsDao {

    /**保存作业设置数据
     * @param context:Context
     * @param workSettings:WorkSettings
     */
    fun saveWorkSettings(context: Context,workSettings: WorkSettings){
        context.getSharedPreferences("assistant", Context.MODE_PRIVATE).edit {
            putString("workSettings", Gson().toJson(workSettings))
        }
    }

    /**获取已保存的作业设置数据
     * @param context:Context
     * @return WorkSettings对象
     */
    fun getSavedWorkSettings(context: Context):WorkSettings{

        if (!isWorkSettingsSaved(context)){
            saveWorkSettings(context,WorkSettings())
        }

        val workSettingsJson = context.
        getSharedPreferences("assistant", Context.MODE_PRIVATE).getString("workSettings","")
        return Gson().fromJson(workSettingsJson,WorkSettings::class.java)
    }

    /**判断是否已保存
     * @return Boolean
     */
    fun isWorkSettingsSaved(context: Context) = context.
    getSharedPreferences("assistant", Context.MODE_PRIVATE).contains("workSettings")
}