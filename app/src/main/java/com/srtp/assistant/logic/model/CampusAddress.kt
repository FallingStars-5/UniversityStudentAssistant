package com.srtp.assistant.logic.model

import org.litepal.crud.LitePalSupport

data class CampusAddress(
    var sortName:String = "",           //类别名称
    var name:String = "",                //网站名称
    var address:String = "",             //网址或id地址
    var isSubscribe:Int = 0              //是否订阅
    ):LitePalSupport(){

        var id:Long = 0                  //定义在数据类的内部，表示值自增加
    }

