package com.srtp.assistant.logic.model

/**
 * 学校常用地址信息存储
 */
private val campus = mapOf(
    1 to CampusAddress("常用工具","文档表格下载","http://jwc.swjtu.edu.cn/vatuu/WebAction?setAction=fileList&viewType=web&selectType=all",1),

    2 to CampusAddress("常用官方网站","交大图书馆","http://www.lib.swjtu.edu.cn/",1),
    3 to CampusAddress("常用官方网站","交大教务","http://jwc.swjtu.edu.cn/index.html",1),
    4 to CampusAddress("常用官方网站","交大官网","https://www.swjtu.edu.cn/",1),
    5 to CampusAddress("常用官方网站","学院","https://www.swjtu.edu.cn/xysz_/xy.htm",1),
    6 to CampusAddress("常用官方网站","研究院","https://www.swjtu.edu.cn/xysz_/yjy.htm",1),

    7 to CampusAddress("交大概况","总体介绍","https://www.swjtu.edu.cn/xxgk2.htm#ztjs",0),
    8 to CampusAddress("交大概况","现任领导","https://www.swjtu.edu.cn/xxgk2.htm#xrld",0),
    9 to CampusAddress("交大概况","机构设置","https://www.swjtu.edu.cn/xxgk2.htm#jgsz",0),
    10 to CampusAddress("交大概况","校史文化","https://www.swjtu.edu.cn/xxgk2.htm#xswh",0),
    11 to CampusAddress("交大概况","校园地图","https://gis.swjtu.edu.cn/#/",0),

    12 to CampusAddress("人才培养","本科生教育","http://jwc.swjtu.edu.cn/index.html",0),
    13 to CampusAddress("人才培养","研究生教育","https://gsnews.swjtu.edu.cn/",0),
    14 to CampusAddress("人才培养","成人教育","https://www.xnjd.cn/",0),
    15 to CampusAddress("人才培养","国际教育","https://sie.swjtu.edu.cn/",0),
    16 to CampusAddress("人才培养","网络教育","https://www.xnjd.cn/",0),

    17 to CampusAddress("科学研究","科研基地","https://www.swjtu.edu.cn/kxyj1.htm#kyjd",0),
    18 to CampusAddress("科学研究","科研成果","https://www.swjtu.edu.cn/kxyj1.htm#kycg",0),
    19 to CampusAddress("科学研究","科研团队","https://www.swjtu.edu.cn/kxyj1.htm#kytd",0),

    20 to CampusAddress("交流合作","国际合作","https://en.swjtu.edu.cn/GLOBAL.htm",0),
    21 to CampusAddress("交流合作","国内合作","https://hlc.swjtu.edu.cn/index.htm",0),

    22 to CampusAddress("校园生活","原创文化","https://news.swjtu.edu.cn/",0),
    23 to CampusAddress("校园生活","教工活动","https://xgh.swjtu.edu.cn/index.htm",0),
    24 to CampusAddress("校园生活","学生活动","http://xg.swjtu.edu.cn/web/",0),
    25 to CampusAddress("校园生活","心理健康","https://xinli.swjtu.edu.cn/",0),
    26 to CampusAddress("校园生活","体育锻炼","http://sports.swjtu.edu.cn/",0),
    27 to CampusAddress("校园生活","交大之声","https://gbt.swjtu.edu.cn/",0),
    28 to CampusAddress("校园生活","交大校报","https://news.swjtu.edu.cn/epaper/index.shtml",0),
    29 to CampusAddress("校园生活","扬华校园","https://news.swjtu.edu.cn/showlist-94.shtml",0),
    30 to CampusAddress("校园生活","交大人物","https://news.swjtu.edu.cn/showlist-95.shtml",0),
    31 to CampusAddress("校园生活","交大通知","https://news.swjtu.edu.cn/showlist-91.shtml",0),
    32 to CampusAddress("校园生活","交大视频","https://video.swjtu.edu.cn/",0),
    33 to CampusAddress("校园生活","文化交大","https://news.swjtu.edu.cn/showlist-100.shtml",0),

    34 to CampusAddress("招生就业","本科生招生","https://zhaosheng.swjtu.edu.cn/",0),
    35 to CampusAddress("招生就业","研究生招生","http://yz.swjtu.edu.cn/web/index.html",0),
    36 to CampusAddress("招生就业","国外招生","http://fad.swjtu.edu.cn/",0),
    37 to CampusAddress("招生就业","国际教育学院","https://sie.swjtu.edu.cn/",0),

    38 to CampusAddress("常用官方网站","交大就业网","https://jiuye.swjtu.edu.cn/eweb/jygl/index.so",1),
    39 to CampusAddress("常用官方网站","西南交通大学人事处","https://rsc.swjtu.edu.cn/rszp/zpxx.htm",0),
    39 to CampusAddress("常用官方网站","党史学习教育网","https://dsxxjy.swjtu.edu.cn/index.htm",0),
    40 to CampusAddress("常用工具","信息化与网络管理处","https://inc.swjtu.edu.cn/wxwl.htm",1),

    41 to CampusAddress("交大图书馆","概况","http://www.lib.swjtu.edu.cn/home/ServiceDetail/1015",1),
    42 to CampusAddress("交大图书馆","思政云课堂","https://sz.bjadks.com/",1),
    43 to CampusAddress("交大图书馆","党建工作","http://www.lib.swjtu.edu.cn/home/service/0/5194",1),
    44 to CampusAddress("交大图书馆","图书资源","http://www.lib.swjtu.edu.cn/databaseguide",1),
    45 to CampusAddress("交大图书馆","新生入馆攻略","http://www.lib.swjtu.edu.cn/home/ServiceDetail/5632",1),
    46 to CampusAddress("交大图书馆","找书指引","http://www.lib.swjtu.edu.cn/home/ServiceDetail/5247",1),
    47 to CampusAddress("交大图书馆","借阅服务","http://www.lib.swjtu.edu.cn/home/ServiceDetail/5252",1),
    48 to CampusAddress("交大图书馆","移动图书馆","http://m.5read.com/244",1),
    49 to CampusAddress("交大图书馆","科研服务","http://www.lib.swjtu.edu.cn/databaseguide",1),
    50 to CampusAddress("交大图书馆","影像展播","http://www.lib.swjtu.edu.cn/home/ServiceDetail/5636",1),
    51 to CampusAddress("交大图书馆","经典阅读","http://www.lib.swjtu.edu.cn/home/service/0/1114",1),
    52 to CampusAddress("交大图书馆","其他服务","http://www.lib.swjtu.edu.cn/home/service/0/1125",1),
    53 to CampusAddress("交大图书馆","离校专栏","http://www.lib.swjtu.edu.cn/Home/ServiceDetail/5905",1),
    54 to CampusAddress("交大图书馆","网站地图","http://www.lib.swjtu.edu.cn/home/servicemap",1),

    55 to CampusAddress("常用工具","交大校历","http://jwc.swjtu.edu.cn/web/date.html",1),

    56 to CampusAddress("常用工具","课程安排","http://jwc.swjtu.edu.cn/vatuu/CourseAction?setAction=queryCourseList&selectTableType=ThisTerm",1),
    57 to CampusAddress("常用工具","期末考试安排","http://jwc.swjtu.edu.cn/vatuu/TestInfoAction?setAction=TestArrange&selectTableType=Test",1),
    58 to CampusAddress("常用工具","空闲教室查询","http://jwc.swjtu.edu.cn/vatuu/CourseAction?setAction=classroomQuery",1),
    59 to CampusAddress("常用工具","四六成绩查询","http://cet.neea.edu.cn/",1),
    60 to CampusAddress("常用工具","维修水电及运输服务中心","https://wxsdys.swjtu.edu.cn/",1),
    61 to CampusAddress("常用工具","物业服务中心","https://wyfwzx.swjtu.edu.cn/",1),
    62 to CampusAddress("常用工具","计划财务处","https://cwc.swjtu.edu.cn/",1),
    63 to CampusAddress("常用工具","实验教学平台","http://dean.vatuu.com/experiment/index.html?pageLink=index",1),


    64 to CampusAddress("交大教务","各科室简介","http://jwc.swjtu.edu.cn/vatuu/WebAction?setAction=commonList&viewType=view&infoType=detail&infoIndex=22251884ACC79046",1),
    65 to CampusAddress("交大教务","教务办事流程","http://jwc.swjtu.edu.cn/vatuu/WebAction?setAction=commonList&viewType=view&infoType=detail&infoIndex=881A8F30F8EA0FAC",1),
    66 to CampusAddress("交大教务","教务办公电话","http://jwc.swjtu.edu.cn/vatuu/WebAction?setAction=commonList&viewType=view&infoType=detail&infoIndex=36A0F9DAB7403C19",1),
    67 to CampusAddress("交大教务","教学管理制度","http://jwc.swjtu.edu.cn/vatuu/WebAction?setAction=ruleList&viewType=web&selectType=all",1),
    68 to CampusAddress("交大教务","新闻媒体报道","http://jwc.swjtu.edu.cn/vatuu/WebAction?setAction=newsList&viewType=secondStyle&selectType=smallType&keyWord=8E3746FC429F906F&newsType=all",1),
    69 to CampusAddress("交大教务","教学动态","http://jwc.swjtu.edu.cn/vatuu/WebAction?setAction=newsList&viewType=secondStyle&selectType=bigType&bigTypeId=FB2D32BEAD066637&newsType=yes",1),
    70 to CampusAddress("交大教务","教学运行","http://jwc.swjtu.edu.cn/vatuu/WebAction?setAction=newsList&viewType=secondStyle&selectType=bigType&bigTypeId=0E4BF4D36E232918&newsType=yes",1),
    71 to CampusAddress("交大教务","实践教学","http://jwc.swjtu.edu.cn/vatuu/WebAction?setAction=newsList&viewType=secondStyle&selectType=bigType&bigTypeId=42ED96EE3E089C19&newsType=yes",1),
    72 to CampusAddress("交大教务","网上办事","https://jiaowu.swjtu.edu.cn/vatuu/WebSite?setAction=index",1),

    73 to CampusAddress("常用工具","学历学位信息查询","http://jwc.swjtu.edu.cn/vatuu/PublicInfoQueryAction?setAction=queryGraduate",1),
    74 to CampusAddress("常用工具","培养方案信息查询","http://jwc.swjtu.edu.cn/vatuu/PublicInfoQueryAction?setAction=queryProgram",1),







    
)


/**
 * 提供访问地址的公共接口函数
 */
fun getCampusAddress(i:Int): CampusAddress {
    return campus[i]?: (campus[1] ?: error(""))
}