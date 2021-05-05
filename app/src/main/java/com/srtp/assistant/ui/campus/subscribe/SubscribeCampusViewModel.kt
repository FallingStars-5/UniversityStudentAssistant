package com.srtp.assistant.ui.campus.subscribe

import androidx.lifecycle.ViewModel
import com.srtp.assistant.logic.Repository
import com.srtp.assistant.logic.model.CampusAddress

class SubscribeCampusViewModel:ViewModel() {

    fun addCampusAddress(campusAddress: CampusAddress) = Repository.addCampusAddress(campusAddress)

    fun findCampusAddressById(id:Long) = Repository.findCampusAddressById(id)

    fun findCampusAddressBySortName(sortName: String) = Repository.findCampusAddressBySortName(sortName)

    fun findAllCampusAddressName() = Repository.findAllCampusAddressName()

    fun updateCampusAddressById(campusAddress: CampusAddress){
        val c = CampusAddress()
        c.sortName = campusAddress.sortName
        c.address = campusAddress.address
        c.name = campusAddress.name
        c.isSubscribe = campusAddress.isSubscribe
        Repository.deleteCampusAddressById(campusAddress.id)
        Repository.addCampusAddress(c)
    }

    fun deleteCampusAddressById(id:Long) = Repository.deleteCampusAddressById(id)

    fun deleteCampusAddressBySortName(sortName:String) = Repository.deleteCampusAddressBySortName(sortName)
}