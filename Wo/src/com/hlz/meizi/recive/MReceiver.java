package com.hlz.meizi.recive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.azero.utils.MNetUtil;
import com.hlz.meizi.utils.MContent;
import com.hlz.meizi.utils.MUtils;

public class MReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if(MNetUtil.isWifiConnected(context)){
			if(MContent.T_ALERT_RECEIVE.equals(intent.getAction())){
				MUtils.startServiceDown(context);
			}else if(MContent.T_RECEIVE_BOOT.equals(intent.getAction())){
				MUtils.startServiceDown(context);			
			}
		}
	}
	
//	  Intent sayHelloIntent=new Intent(context,SayHello.class);
//	   sayHelloIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//	   context.startActivity(sayHelloIntent);
}
