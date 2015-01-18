package com.hlz.meizi.entity;

import com.azero.annotation.sqlite.Id;
import com.azero.annotation.sqlite.Property;
import com.azero.annotation.sqlite.Table;

//用户反馈表
@Table(name = "table_feedback")
public class Feedback {

	@Id
	private int id;

	// 反馈内容
	@Property
	private String content;

	// 联系方式
	@Property
	private String contact;

	// 反馈时间
	@Property
	private String addTime;

	// 反馈用户ID(保留)
	@Property
	private String uid;

	@Property
	private String markid;

	@Property(defaultValue = "0")
	private int isSend;

	@Property
	private String replayName;

	@Property
	private String replayContent;
	// 回复时间
	@Property
	private String replayTime;

	public int getIsSend() {
		return isSend;
	}

	public void setIsSend(int isSend) {
		this.isSend = isSend;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public String getMarkid() {
		return markid;
	}

	public void setMarkid(String markid) {
		this.markid = markid;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getAddTime() {
		return addTime;
	}

	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getReplayName() {
		return replayName;
	}

	public void setReplayName(String replayName) {
		this.replayName = replayName;
	}

	public String getReplayContent() {
		return replayContent;
	}

	public void setReplayContent(String replayContent) {
		this.replayContent = replayContent;
	}

	public String getReplayTime() {
		return replayTime;
	}

	public void setReplayTime(String replayTime) {
		this.replayTime = replayTime;
	}

}
