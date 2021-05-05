package com.srtp.assistant.ui.campus.showingCampus

import androidx.lifecycle.ViewModel
import com.srtp.assistant.logic.Repository
import com.srtp.assistant.logic.model.CampusAddress

class ShowingCampusViewModel:ViewModel() {

    fun addCampusAddress(campusAddress: CampusAddress) = Repository.addCampusAddress(campusAddress)

    fun findCampusAddressById(id:Long) = Repository.findCampusAddressById(id)

    fun findCampusAddressBySortName(sortName: String) = Repository.findCampusAddressBySortName(sortName)

    fun findAllCampusAddressName() = Repository.findAllCampusAddressName()

    fun updateCampusAddressById(campusAddress: CampusAddress) = Repository.updateCampusAddressById(campusAddress)

    fun deleteCampusAddressById(id:Long) = Repository.deleteCampusAddressById(id)

    fun deleteCampusAddressBySortName(sortName:String) = Repository.deleteCampusAddressBySortName(sortName)
}