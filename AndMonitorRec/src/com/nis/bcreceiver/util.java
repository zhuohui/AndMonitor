package com.nis.bcreceiver;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;

public class util 
{
	public static String TAG = "BCReceiver";
	public static String preMessage = "";
	
	public static final String InURL_TEST = "http://10.240.139.223:8081/ROOT/apppush";
	public static final String CNURL_REQ = "http://cont.3g.163.com/apppush";//国内
	public static final String InterURL_REQ = "http://cont.3g.163.com/emgnews";//国外
	public static boolean sendTargetUrl(String head, String message,String title, boolean isChina, String docID)
    {
		//测试url
		String sBaseUrl = "http://10.240.136.189:8081/ROOT/emgnews";
    	
    	if(isChina)
    		sBaseUrl = CNURL_REQ;
    	else
    		sBaseUrl = InterURL_REQ;
    	
    	try {
    		StringBuffer strBuffer = new StringBuffer(sBaseUrl);
    		strBuffer.append("?head=");
    		strBuffer.append(URLEncoder.encode(head, "UTF-8"));
    		if(title != null)
    		{
    			strBuffer.append("&title=");
        		strBuffer.append(URLEncoder.encode(title, "UTF-8"));
    		}
    		if(docID != null && docID.length() > 0)
    		{
    			strBuffer.append("&docID=");
        		strBuffer.append(URLEncoder.encode(docID, "UTF-8"));
    		}
    		strBuffer.append("&msg=");
    		strBuffer.append(URLEncoder.encode(message, "UTF-8"));//(Base64.encodeToString(message.getBytes(), 0));
    		
    		String sURl = strBuffer.toString();
    		
    		String sURl2 = sURl.trim();//去掉最后的空格，否则会报illegal character
    		String newURL = sURl2.replaceAll("\\\n", "");
    		AppLog.d("sendTargetUrl sUrl = " + newURL);
    		DefaultHttpClient localDefaultHttpClient = new DefaultHttpClient();
    		//HttpPost localHttpPost = new HttpPost(newURL);
    		HttpGet localHttpget = new HttpGet(newURL);
			HttpResponse localHttpResponse = localDefaultHttpClient.execute(localHttpget);
			
			int status = localHttpResponse.getStatusLine().getStatusCode();
			String str = localHttpResponse.getStatusLine().getReasonPhrase();
			
			if (status == 400)
			{
				AppLog.d("Failed to get to cont.3g.163.com.");
				
				return false;
			}
    	    AppLog.d("get cont.3g.163.com , got back: " + status + " " + str);
    	    
		} catch (Exception e) {
			AppLog.e("sendUrl exception:" + e.getMessage(), e);
			return false;
		}
    	return true;
    }
	
	protected static boolean CheckMessageSended(Context ctx, String head, String message)
	{
		
		preMessage = readMessage(ctx, head);
			
		
		AppLog.d("CheckMessageSended pre="+preMessage);
		AppLog.d("CheckMessageSended now="+message);
		//判断两者是否相等
		return preMessage.equals(message);
	}
	
	public synchronized static void threadSend(Context ctx,final String head, final String message,
			final String title, final boolean isChina)
	{
		threadSend(ctx, head, message, title, isChina, null);
	}
	
	public synchronized static void threadSend(Context ctx,final String head, final String message,
			final String title, final boolean isChina, final String docID)
	{
		
		if(CheckMessageSended(ctx, head, message))
		{
			AppLog.w( message + ".message equal, not need to send!");
			return;
		}
		
		saveLastMessage(ctx, head, message);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				sendTargetUrl(head, message, title, isChina, docID);
				
			}
		}).start();
	}
	
	public static String getURLContents(String url)
	{
		try {
			HttpGet urlGet = new HttpGet(url);
			urlGet.setHeader("Accept", "text/json");
			
			HttpResponse httpResponse = new DefaultHttpClient().execute(urlGet);
			int status = httpResponse.getStatusLine().getStatusCode();
			if(status ==  200)
			{
				String result = EntityUtils.toString(httpResponse.getEntity());  
				//  去掉返回结果中的"\r"字符，	否则会在结果字符串后面显示一个小方格  
				String breaknews = result.replaceAll("\r", "");
				return breaknews;
			}else
			{
				AppLog.w("getURLContents error, status = " + status);
			}
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.e("getURLContents error.", e);
		}
		return null;
		
	}
	
	
	public static String readMessage(Context ctx, String title)
	{
		String mess = "";
		if(ctx != null)
		{
			
			SharedPreferences sp = ctx.getSharedPreferences("news_sp", Context.MODE_PRIVATE);
			mess = sp.getString(title, "");
			//Log.d(MainActivity.TAG, "readMessage = " + mess);
		}else
		{
			AppLog.w("readMessage, Context is null!");
		}
		
		return mess;
	}
	
	public synchronized static boolean saveLastMessage(Context ctx, String title, String message)
	{
		if(ctx != null)
		{
			SharedPreferences sp = ctx.getSharedPreferences("news_sp", Context.MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putString(title, message);
			boolean b1 = editor.commit();
			AppLog.d("saveLastMessage " + title + ".result = " + b1);
			return b1;
		}else
		{
			AppLog.w("saveLastMessage, Context is null!");
			return false;
		}
	    
	}
	
	public static Date getNowDate()
	{
		Date localDate1 = new Date();
		//SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		//String date = sDateFormat.format(localDate1);
		//Log.d(MainActivity.TAG, "localdate=" + localDate1);
		return localDate1;
	}
	
	public static Date parseDateString2(String paramStr) 
	{
		//2014-10-09T21:54:04Z
		String str_t = paramStr.replaceAll("Z", "");
		String date_str = str_t.replaceAll("T", " ");
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date date = new Date();
		try {
			date = fmt.parse(date_str);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date;
	}
	
	public static Date parseDateString(String paramStr) {
		String[] arrayOfString1;
		String[] arrayOfString2;
		//替换掉可能存在的Z
		String date_str = paramStr.replaceAll("Z", "");
		try {
			int i = date_str.indexOf("T");
			if (i == -1) {
				i = date_str.indexOf(" ");
				if (i == -1)
					return new Date();
			}
			String str1 = date_str.substring(0, i);
			String str2 = date_str.substring(i + 1);
					
			int j = str2.indexOf(".");
			if (j > 0)
				str2 = str2.substring(0, j);
			arrayOfString1 = str1.split("-");
			arrayOfString2 = str2.split(":");
			if ((arrayOfString1.length != 3) || (arrayOfString2.length != 3)) {
				Date localDate1 = new Date();
				return localDate1;
			}
		} catch (Exception localException) {
			return new Date();
		}
		int k = Integer.valueOf(arrayOfString1[0]).intValue();
		int m = -1 + Integer.valueOf(arrayOfString1[1]).intValue();
		int n = Integer.valueOf(arrayOfString1[2]).intValue();
		int i1 = Integer.valueOf(arrayOfString2[0]).intValue();
		int i2 = Integer.valueOf(arrayOfString2[1]).intValue();
		int i3 = Integer.valueOf(arrayOfString2[2]).intValue();
		
		Calendar localCalendar = Calendar.getInstance();
		localCalendar.setTimeZone(TimeZone.getTimeZone("UTC"));
		localCalendar.set(1, k);
		localCalendar.set(2, m);
		localCalendar.set(5, n);
		localCalendar.set(11, i1);
		localCalendar.set(12, i2);
		localCalendar.set(13, i3);
		localCalendar.set(14, 0);
		Date localDate2 = localCalendar.getTime();
		return localDate2;
	}
	public static boolean isServiceRunning(Context mContext, String className) {
		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		
		List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(100);
		if (serviceList.size() == 0) {
			return false;
		}
		for (int i = 0; i < serviceList.size(); i++) {
			if (serviceList.get(i).service.getClassName().equals(className) == true) {
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}

}
