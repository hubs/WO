package com.hlz.meizi.entity;

public class AppUpdateInfo {

	/**
	 * 获取新版的版本号:version code
	 */
	public int getVersionCode(){
		return 1;
	}

	/**
	 * 获取新版的版本名字:version name
	 */
	public String getVersionName(){
		return "撸V1";
	}

	/**
	 * 获取升级提示:update tips
	 */
	public String getUpdateTips(){
		return "撸妹有升级版本啦,赶快升级体验一下吧。";
	}

	/**
	 * 获取新版本的Apk下载地址:url
	 */
	public String getUrl(){
		return "";
	}
}
