package com.hlz.meizi.service;

import android.app.IntentService;
import android.content.Intent;

import com.hlz.meizi.utils.MUtils;

public class StartDownDatasService extends IntentService {
	public StartDownDatasService() {
		super("DownDatasService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		MUtils.downloadImage(getApplicationContext());
	}

}
