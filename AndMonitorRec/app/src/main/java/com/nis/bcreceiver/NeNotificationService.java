package com.nis.bcreceiver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.lang.Integer;
import java.lang.reflect.Field;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Notification;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.RemoteViews;


public class NeNotificationService extends AccessibilityService {

	private static String qqpimsecure = "com.tencent.qqpimsecure";
	//public static String TAG = "NeNotificationService";
	
	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		
	    if (event.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) 
	    {
	    	if(event.getPackageName().equals(qqpimsecure))
	    	{
	    		
	    	}else{
	    		//AppLog.i("notification toStr():" + event.toString());
	    		//AppLog.i("notification getText : " + event.getText());
	    		
	    		Parcelable data = event.getParcelableData();
	    		
				if (data instanceof Notification) {

					// Log.i(TAG, "Recieved notification");
										
					Notification notification = (Notification) data;
					
					//analyzeNotify(notification);
					//AppLog.i("ticker: " + notification.tickerText);
					
					// Log.i(TAG, "contentview: " + notification.contentView));
					///*
					Intent intent = new Intent();
					intent.putExtra("NotifyData", notification);
					intent.putExtra("packageName", event.getPackageName());
					//AppLog.i("notification getPackageName: " + event.getPackageName());
					intent.setAction(".NeNotificationService");
					//sendBroadcast(intent);
					MainActivity.notifyReceive((String)event.getPackageName(), notification);
					//*/
					

				}
			}
	    }else
	    {
	    	//Log.i(TAG, "other event : " + event.getEventType() + " .package:"  + event.getPackageName() + " .text:" + event.getText());
	    }
	    
	}
	
	private void analyzeNotify(Notification notification)
	{
		RemoteViews views = notification.contentView;
		Class secretClass = views.getClass();

		try {
		    Map<Integer, String> text = new HashMap<Integer,String>();

		    Field outerField = secretClass.getDeclaredField("mActions");
		    outerField.setAccessible(true);
		    ArrayList<Object> actions = (ArrayList<Object>) outerField.get(views);

		    for (Object action : actions) {
		    	AppLog.i("analyzeNotify action:" + action.toString());
		        Field innerFields[] = action.getClass().getDeclaredFields();
		        Field innerFieldsSuper[] = action.getClass().getSuperclass().getDeclaredFields();
		        
		        Object value = null;
		        Integer type = null;
		        Integer viewId = null;
		        for (Field field : innerFields) {
		            field.setAccessible(true);
		            //AppLog.i("analyzeNotify innerFields :" + field.toString());
		            if (field.getName().equals("value")) {
		                value = field.get(action);
		            } else if (field.getName().equals("type")) {
		                type = field.getInt(action);
		            }else if(field.getName().equals("URI"))
		            {
		            	
		            	//AppLog.i("analyzeNotify innerFields URI :" + uri);
		            	
		            }else
		            {
		            	//Object obj = (Object)field.get(action);
		            	//AppLog.i("analyzeNotify innerFields obj :" + obj);
		            	
		            }
		        }
		        for (Field field : innerFieldsSuper) {
		            field.setAccessible(true);
		            //AppLog.i("analyzeNotify innerFieldsSuper :" + field.toString());
		            if (field.getName().equals("viewId")) {
		                viewId = field.getInt(action);
		            }
		        }

		        if (value != null && type != null && viewId != null && (type == 9 || type == 10)) {
		            text.put(viewId, value.toString());
		        }
		    }

		    AppLog.i("analyzeNotify title is: " + text.get(16908310));
		    AppLog.i("analyzeNotify info is: " + text.get(16909082));
		    AppLog.i("analyzeNotify text is: " + text.get(16908358));
		} catch (Exception e) {
		    e.printStackTrace();
		}
	}
	
	@Override
	protected void onServiceConnected() {
		AppLog.d("onServiceConnected");
	    
	    AccessibilityServiceInfo info = new AccessibilityServiceInfo();
	    info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED |
				AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED | 
				AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED;
	    info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
	    info.notificationTimeout = 100;
	    setServiceInfo(info);
	}

	@Override
	public void onInterrupt() {
		AppLog.d( "onInterrupt");

	}

}
