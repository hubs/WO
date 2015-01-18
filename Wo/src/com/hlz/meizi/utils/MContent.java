package com.hlz.meizi.utils;

public class MContent {

	//最后加载时间
	public static final String T_LAST_TIME					=	 "last_time";
	
	//服务器数据时间(用于加载远程数据使用的add_Time)
	public static final String T_SERVICE_TIME				=	 "service_time_";
	public static final String T_LAST_SERVICE_ID			=	 "last_service_id_";
	
	//每次加载的数量
	public static final int T_PAGE_SIZE							=	15;
	public static final int T_SERVICE_PAGE_SIZE			=	30;// 每次去下载30张
	public static final int T_MAX_HEIGHT						=  560;//图片最大高度
	
	//第一次加载代码时使用
	public static final String T_TODAY							= "today";
	public static final String T_FIRST_LOAD					=	"first_load";
	public static final String T_FIRST_INSTRALL				=	"first_install";
	public static final String T_SEND_BASE					=	"base_send";//基础数据是否发送
	public static final String T_PHONE							=	"phone";
	public static final String T_MAC								=	"mac";
	public static final String T_IMSI								=	"imsi";
	public static final String T_PROVIDERSNAME   		=	"providerName";
	public static final String T_SDK								=	"sdk";//系统版本号
	public static final String T_SDK_NAME					=	"release";//系统版本号名
	public static final String T_UID								=	"uid";
	public static final String T_USERNAME					=	"username";
	public static final String T_TOKEN							="HiAndroid";
	public static final String T_LOCAL_DATAS_TOKEN 	="local_token";
	
	public static  String T_TYPE									="ALL";
	public static  String T_TITLE									="妹子图";
	public static String T_TITLE_OK								="妹子图";
	public static String T_SD_FILENAME						="HiMeizi";
	//左边分类名称
	public static  final String T_TYPE_ALL						="查看全部";
	public static final String T_TYPE_YW						="尤物/诱惑";
	public static final String T_TYPE_QC						="清纯美女/自拍";
	public static final String T_TYPE_MT						="美腿/美臀";
	public static final String T_TYPE_DX						="大胸";
	
	public static final int T_ONE_HOUR						=60*1000*60;//定时任务(一小时)
	public static final int T_FIVE_MINUTE						=60*1000*5;//5分钟
	public static final String T_DEFAULT_TIME				="2014-08-01";
	
	public static final String T_ALERT_RECEIVE				="com.meizi.utils.alarm";
	public static final String T_RECEIVE_BOOT				="android.intent.action.BOOT_COMPLETED";
	
	public static final String T_TABLE_INFO					="table_info";
	
	//提交地址
	public static final String T_URL_BASE_INFO				="http://www.vocome.com/Android/baseInfo";
	public static final String T_URL_FEEDBACK				="http://www.vocome.com/Android/feedBack";
	public static final String T_URL_REMOTE				="http://www.vocome.com/Android/loadDatas";
	public static final String T_URL_SEND						="http://www.vocome.com/Android/sendToClientFeedBack";
	public static final String T_DOWN_PATH					="http://vocome.537389d89a40a.d01.nanoyun.com/";	
	public static final String T_URL_LOG						="http://www.vocome.com/Android/logInfo";
	
	//反馈上传
	public static final String T_FEEDBACK_CONTENT		="content";
	public static final String T_FEEDBACK_QQINFO		="qqinfo";
	public static final String T_FEEDBACK_UID				="uid";
	public static final String T_ADDTIME						="add_time";
	public static final String T_FEEDBACK_TOKEN			="token";
	public static final String T_MARKID						="markId";
	public static final String T_OTHER_TYPE					="type";
	public static final String T_SERVICE_ID					="serviceId";
	public static final String T_PAGESIZE						="pagesize";
	//SQL
	public static final String UPDATE_BY_LOCAL			="update table_info set loadNums=loadNums+1 ,isNews=1 where id in (%s)";
	public static final String UPDATE_BY_LOCAL_2			="update table_info set loadNums=loadNums+1 ,isNews=1 ,token='%s' where id in (%s)";
	public static final String UPDATE_ISDOWN				="update table_info set isDown=1 where id in (%s)";
	public static final String UPDATE_SELECT_DOWN    ="select * from "+MContent.T_TABLE_INFO+" where isDown=0 and status=0 order by id asc limit "+MContent.T_PAGE_SIZE;
	public static final String UPDATE_DOWN_WHERE	=" update "+MContent.T_TABLE_INFO+" set isDown=1 where imageUrl='%s'";
	public static final String UPDATE_SELECT_TYPE		="select type,count(id) num from table_info where status=0 group by type";
	public static final String SQL_HIDE							="update "+MContent.T_TABLE_INFO+" set status=1 where imageUrl='%s'";
	public static final String SQL_FEEDLIST					="select * from table_feedback where isSend=1 and replayTime>0 order by id desc";
	
	
	
	//AZero.db
	public static final String T_DB_NAME						="de9ef18abc6d3c3b4261189e42";
	public static final String T_DB_NAME_OK				="AZero.db";
	//HiMeizi.zip
	public static final String T_IMAGE_NAME				="ie9ef18abc6d3c3b4261189e42";
	
	
	public static final String T_TYPE_XGMM					=	"性感美女";
	public static final String T_TYPE_GIF						=	"GIF";
	
	
	//fragment
	public static final String T_FRAG_FEED					=	"feedBack";
	public static final String T_FRAG_MENU					=	"menu";
	public static final String T_FRAG_CONTENT			=	"content";
	public static final String T_FRAG_AD						=	"ad";
	
	
}
