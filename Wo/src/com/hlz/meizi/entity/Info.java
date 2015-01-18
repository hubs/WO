package com.hlz.meizi.entity;

import com.azero.annotation.sqlite.Id;
import com.azero.annotation.sqlite.Property;
import com.azero.annotation.sqlite.Table;

//瀑布流表
@Table(name = "table_info")
public class Info {

	@Id
	private int id;

	@Property
	private String imageUrl;// 图片网络地址

	@Property(defaultValue = "0")
	private int loadNums;

	@Property(defaultValue = "0")
	private int clickNums;// 点击次数
	@Property(defaultValue="0")
	private int status;//状态(1:无效,0,有效)

	@Property
	private int noSendClick;// 未发送到服务端的点击数

	@Property(defaultValue = "0")
	private int isNews;

	@Property
	private String token;// 每次加载时都进行排除

	@Property
	private int serviceId;// 服务端ID

	@Property
	private String serviceAddTime;// 服务端时间

	@Property
	private String type;// 分类

	@Property(defaultValue="0")
	private int isDown;// 是否下载图片,在Service中更新

	public int getId() {
		return id;
	}

	public int getIsDown() {
		return isDown;
	}

	public void setIsDown(int isDown) {
		this.isDown = isDown;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public int getIsNews() {
		return isNews;
	}

	public void setIsNews(int isNews) {
		this.isNews = isNews;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public int getLoadNums() {
		return loadNums;
	}

	public void setLoadNums(int loadNums) {
		this.loadNums = loadNums;
	}

	public int getClickNums() {
		return clickNums;
	}

	public void setClickNums(int clickNums) {
		this.clickNums = clickNums;
	}

	public int getNoSendClick() {
		return noSendClick;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setNoSendClick(int noSendClick) {
		this.noSendClick = noSendClick;
	}

	public int getServiceId() {
		return serviceId;
	}

	public void setServiceId(int serviceId) {
		this.serviceId = serviceId;
	}

	public String getServiceAddTime() {
		return serviceAddTime;
	}

	public void setServiceAddTime(String serviceAddTime) {
		this.serviceAddTime = serviceAddTime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
