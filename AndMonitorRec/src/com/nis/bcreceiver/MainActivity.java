package com.nis.bcreceiver;


import com.nis.bcreceiver.PushMessCache.MessageData;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Contacts.Intents;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

public class MainActivity extends Activity {

	public static final String TAG = "BCReceiver";
	private LinearLayout rootLayout;
	private Button accesscBt;
	private Button accesscStartNo;
	private Button ClearViews;
	private PushMessCache  pushIns;
	private Intent upservice;
	private static MainActivity activity = null;
	private static HttpReq req = new HttpReq();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		activity = this;
		AppLog.init();
		pushIns = new PushMessCache();
		
		setContentView(R.layout.activity_main);
		
		upservice = new Intent(this, NeNotificationService.class); 
		
		rootLayout = (LinearLayout) findViewById(R.id.root_layout);
		accesscBt = (Button)findViewById(R.id.buttonAssesc);
		accesscBt.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
				startActivityForResult(intent, 0);
				
			}
		});
		accesscStartNo = (Button)findViewById(R.id.buttonStartNofi);
		accesscStartNo.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//registerBroadcast();
				updateServiceStatus(true);
			}
		});
		
		ClearViews = (Button)findViewById(R.id.buttonClearView);
		ClearViews.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				rootLayout.removeAllViews();
				NotificationManager nm = (NotificationManager) MainActivity.this.getSystemService(Context.NOTIFICATION_SERVICE);
				
			}
		});
		
		updateServiceStatus(true);
		//Intent service=new Intent(this, CatServices.class); 
		//startService(service);
		//test();
		//req.StartRequest("http://p.3g.163.com/nc/last.do");
		
	}
	
	private void test()
	{
		MessageData data = pushIns.new MessageData();
		data.isChina = true;
		data.packageName = "com.netease.newsreader.activity";
		data.timeText = "2015/4/11";
		data.title = "国内成品油价3连降后首次涨价";
		data.message = "今日24时起，国内成品油价迎年内三连降后首次涨价，93号汽油上涨0.09元/升、0号柴油上涨0.10元/升。";
		
		pushIns.sendMess(this, data);
	}
	
	private void updateServiceStatus(boolean start)
	{
		boolean bRunning = util.isServiceRunning(this, "com.nis.bcreceiver.NeNotificationService");
		
		if (start && !bRunning) {
			this.startService(upservice);
		} else if(!start && bRunning) {
			this.stopService(upservice);
		}
		bRunning = util.isServiceRunning(this, "com.nis.bcreceiver.NeNotificationService");
		
		AppLog.i("updateServiceStatus ctrl[ " + start + "] result running:" + bRunning);
		
	}
	
	private NotifyDataReceiver  receiver = null;
	private void registerBroadcast() {
		receiver = new NotifyDataReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(".NeNotificationService");
		Intent it = this.registerReceiver(receiver, filter);
		
		AppLog.i( "Broadcast registered.........:" + it);
	}
	
	
	public void testIDFA()
	{
		
	}
	
	private void EnumGroupViews(View v1, MessageData data)
	{
		if(v1 instanceof ViewGroup)
		{
			//Log.i(TAG, "FrameLayout in");
			ViewGroup lav = (ViewGroup)v1;
			int lcCnt = lav.getChildCount();
			for(int i = 0; i < lcCnt; i++)
			{
				View c1 = lav.getChildAt(i);
				if(c1 instanceof ViewGroup)
					EnumGroupViews(c1, data);
				else if(c1 instanceof TextView)
				{
					TextView txt = (TextView)c1;
					String str = txt.getText().toString().trim();
					if(str.length() > 0)
					{
						pushIns.addMess(txt.getId(), data, str);
					}
					
					AppLog.i( "TextView id:"+ txt.getId() + ".text:" + str);
				}else
				{
					AppLog.w("2 other layout:" + c1.toString());
					
				}
			}
		}
		else {
			AppLog.w("1 other layout:" + v1.toString());
		}
	}
	
	private void addToUi(RemoteViews remoteView, String packName) {
		//rootLayout.addView(remoteView);
		try {
			View v1 = remoteView.apply(this, rootLayout);
			//AppLog.i("remoteview:" + v1.toString());
			MessageData data = pushIns.new MessageData();
			
			data.packageName = packName;
			EnumGroupViews(v1, data);
			if(rootLayout.getChildCount() > 100)
			{
				AppLog.i("remove 50 views in child!");
				rootLayout.removeViews(0, 50);
				
			}
			/*
			if(packName.equals("com.netease.newsreader.activity"))
			{
				data.docID = req.getdocID(data.message);
				AppLog.i("docID:" + data.docID);
			}
			*/
			rootLayout.addView(v1);
			data.isChina = PackName.isChina(packName);
			pushIns.sendMess(this, data);
		} catch (Exception e) {
			AppLog.e("addToUi excep",e);
		}
		
	}
	
	
	public static void notifyReceive(String packageName, Notification notification)
	{
		PendingIntent nit = notification.contentIntent;
		
		//Parcel data = Parcel.obtain();
		//nit.writeToParcel(data, 0);
		
		//nit.getIntentSender().writeToParcel(data, 0);
		
		AppLog.i("onReceive packageName: " + packageName);
		/*
		if(packageName.equals("com.netease.newsreader.activity"))
		{
			req.StartRequest("http://p.3g.163.com/nc/last.do");
		}
		*/
		if (notification != null) {
			
			RemoteViews remoteV = notification.contentView;
		
			if (remoteV==null) {
				AppLog.e("remoteView is: null" );
			} else {
				if(activity != null)
					activity.addToUi(remoteV, packageName);
				else
					AppLog.e("MainActivity is null");
				
			}
		}
	}
	
	public class NotifyDataReceiver extends BroadcastReceiver{
		 
		@Override
		public void onReceive(Context context, Intent intent) {
			//AppLog.i("Receiver got msg in onReceive()...");
 
			Parcelable notifyParcelable = intent.getParcelableExtra("NotifyData");
			String packageName = intent.getStringExtra("packageName");
			AppLog.i("onReceive packageName: " + packageName);
			if (notifyParcelable != null) {
 
				Notification notification = (Notification) notifyParcelable;
				//Log.i("tickerText: " + notification.tickerText);
					
				RemoteViews remoteV = notification.contentView;
				PendingIntent nit = notification.contentIntent;
				//AppLog.i("PendingIntent:" + nit.toString());
				//IntentSender se = nit.getIntentSender();
				//int code = 101;
				try {
					/*
					nit.send(code, new PendingIntent.OnFinished() {
						
						@Override
						public void onSendFinished(PendingIntent pendingIntent, Intent intent,
								int resultCode, String resultData, Bundle resultExtras) {
							if(resultData != null)
								AppLog.i("onSendFinished resultCode :" + resultCode + " .resultData:" + resultData);
							if(resultExtras != null)
								AppLog.i("onSendFinished resultExtras:" + resultExtras.toString());
							if(intent != null)
								AppLog.i("onSendFinished intent:" + intent.toString());													
						}
					}, null);*/
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (remoteV==null) {
					AppLog.e("remoteView is: null" );
				} else {
					//showNotify("remoteView is: not null" );
					
					addToUi(remoteV, packageName);
				}
 
				 
			}
 
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		//if(receiver != null)
		//	this.unregisterReceiver(receiver);
		super.onDestroy();
		updateServiceStatus(false);
	}
	
	/*
	 
	 int datasize = data.dataSize();
		AppLog.i("paracel datasize:" + datasize);
		int i = 0;
		while (i < datasize) {  
        	data.setDataPosition(i);  
        	String url = data.readString();
        	if(url != null)
        	{
        		AppLog.i(i+"=" + url);
        	}
        
        	
        	
        	
        	i++;
        }
		
		try {
			nit.send(101, new PendingIntent.OnFinished() {
				
				@Override
				public void onSendFinished(PendingIntent pendingIntent, Intent intent,
						int resultCode, String resultData, Bundle resultExtras) {
					if(resultData != null)
						AppLog.i("onSendFinished resultCode :" + resultCode + " .resultData:" + resultData);
					if(resultExtras != null)
						AppLog.i("onSendFinished resultExtras:" + resultExtras.getString("id"));
					if(intent != null)
					{
						Bundle localBundle4 = intent.getBundleExtra("param_news");
						if(localBundle4 != null)
						{
							String   str2 = localBundle4.getString("docid");
							AppLog.i("docid send =" + str2);
						}
					}
							
					
					
				}
			}, null);
		} catch (CanceledException e) {
			e.printStackTrace();
		}
        
	 * */
}
