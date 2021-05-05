package com.srtp.assistant.logic.dao

import com.srtp.assistant.logic.model.CampusAddress
import org.litepal.LitePal
import org.litepal.extension.delete
import org.litepal.extension.deleteAll
import org.litepal.extension.find

object CampusAddressDao {

    /**增加一条网址记录
     * @param campusAddress :CampusAddress
     */
    fun addCampusAddress(campusAddress: CampusAddress){
        campusAddress.save()
    }


    /**通过id查找一条记录并返回
     * @param id:Long
     * @return CampusAddress对象
     */
    fun findCampusAddressById(id:Long) = LitePal.find<CampusAddress>(id)

    /**通过类别名查找所有的记录并返回
     * @param sortName:String
     * @return CampusAddress列表
     */
    fun findCampusAddressBySortName(sortName: String) = LitePal.where("sortName = ?", sortName).find<CampusAddress>()


    /**查找所有的分类名称并返回
     * @return String类型列表
     */
    fun findAllCampusAddressName():List<String>{
        val list = LitePal.where("").find<CampusAddress>()
        val sortNameSet = HashSet<String>()
        for (i in list){
            sortNameSet.add(i.sortName)
        }
        return ArrayList<String>(sortNameSet)
    }

    /**修改一条记录
     * @param campusAddress:CampusAddress
     */
    fun updateCampusAddressById(campusAddress: CampusAddress){
        campusAddress.update(campusAddress.id)
    }

    /**通过id删除一条记录
     * @param id:Long
     */
    fun deleteCampusAddressById(id:Long){
        LitePal.delete<CampusAddress>(id)
    }

    /**通过类别名删掉该类别所有的记录
     * @param sortName:String
     */
    fun deleteCampusAddressBySortName(sortName:String){
        LitePal.deleteAll<CampusAddress>("sortName = ?", sortName)
    }
}