package com.nis.bcreceiver;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;

public class BCBroadCastReceiver extends BroadcastReceiver {

	public static String TAG = "BCBroadCastReceiver";
	

	@Override
	public void onReceive(Context context, Intent intent) 
	{
		/*
		try {
			
			String sAct = intent.getAction();
			Log.d(TAG, "BCBroadCastReceiver onReceive action = " + sAct);
			Bundle bundle = intent.getExtras();
			if(sAct.equals("com.turner.et.B.POST_BREAKING_NEWS_NOTIFICATION"))
			{
				String sHead = (String)bundle.get("HEADLINE");
				final String sMessage = (String)bundle.get("MESSAGE");
				Log.d(TAG, "CNN receive head:" + sHead + ".message:" + sMessage);
				
				//util.threadSend(context, "CNN", sMessage, null, false);
			}else if(sAct.equals("net.processone.push.INCOMING_NOTIFICATION"))
			{
				//sendTargetUrl("BBC", "to see BBC APP");
				
				//Parcelable a = (Parcelable)bundle.get("notification");
				//String str = a.toString();
				//Log.d(TAG, "BBC receiv notify:" + str);
				
			}else
			{
				
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		*/
	}

}
