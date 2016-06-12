package com.nis.bcreceiver;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class CatServices extends Service {


	public static Context ctx = null;



	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		ctx = this.getBaseContext();



	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
	}



	@Override
	public void onDestroy() {
		super.onDestroy();

	}


}
