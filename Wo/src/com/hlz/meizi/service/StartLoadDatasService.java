package com.hlz.meizi.service;

import android.app.IntentService;
import android.content.Intent;

import com.hlz.meizi.utils.MUtils;

public class StartLoadDatasService extends IntentService {

	public StartLoadDatasService() {
		super("LoadDatasService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
		MUtils.getRemoteDatas(getApplicationContext());
	}
}
