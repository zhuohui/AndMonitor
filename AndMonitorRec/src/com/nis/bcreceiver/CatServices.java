package com.nis.bcreceiver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class CatServices extends Service {

	public static final String TAG2 = "BCReceiverLOG";
	public static Context ctx = null;
	public static void analyze_log()
	{
  
		Log.d(TAG2, "analyze_log 7");
		
		Process logcatProcess = null;
        BufferedReader bufferedReader = null;
        try {
                /** »ñÈ¡ÏµÍ³logcatÈÕÖ¾ÐÅÏ¢ */
                //Ïàµ±ÓÚÔÚÃüÁîÐÐÔËÐÐ  logcat -s dalvikm ,  -s±íÊ¾¹ýÂË£¬ µÚÈý¸ö²ÎÊý±íÊ¾¹ýÂËµÄÌõ¼þ¡£Èç¹ûÃ»ÓÐµÚÈý¸ö²ÎÊý£¬Êý×é³¤¶È2£¬¿Ï¶¨Ò²ÊÇ¿ÉÒÔµÄ¡£ÏÂÃæÓÐlogcatµÄÊ¹      ÓÃ·½·¨
                String[] running=new String[]{ "logcat","-s","*:V" };
                logcatProcess = Runtime.getRuntime().exec(running);
                                
                bufferedReader = new BufferedReader(new InputStreamReader(
                                logcatProcess.getInputStream()));

                String line;
                //É¸Ñ¡ÐèÒªµÄ×Ö´®
                final String strFilter="Exception";
                final String gcm_key = "onReceive: com.google.android.c2dm.intent.RECEIVE";
                final String bbc_news_key = "data: {\"content\":\"BBC News";//data: {"content":"BBC News
                final String bbc_news_data = "data: ";
                final String cnn_new_key = "CNN receive head:Breaking News.message:";
                final String bbc_resume_key = "onResume : android.intent.action.MAIN";
                boolean b_gcm_get = false;
                Log.d(TAG2, "analyze_log 4"+ "_" + b_gcm_get);
                while ((line = bufferedReader.readLine()) != null) 
                {
                        //¶Á³öÃ¿ÐÐlogÐÅÏ¢
                        //System.out.println("¼àÌý:" + line);
                        int idx = -1;
                        String new_message = "";
                        String new_head = "";
                		if((idx = line.indexOf(cnn_new_key)) >= 0)
                		{
                			new_head = "CNN";
                			idx = idx + cnn_new_key.length();
                			
                		}else if((idx = line.indexOf(bbc_news_key)) >= 0)
                		{
                			
                			new_head = "BBC";
                			idx = idx + bbc_news_data.length();//bbc_news_key.length();
                			//int index2 = line.indexOf(subString, idx);
                		}
                		else if (line.indexOf(gcm_key) >= 0) 
                        {
                             Log.e(TAG2, "Got gcm push!");
                             b_gcm_get = true;
                        }else if(line.indexOf(bbc_resume_key) >= 0)
                        {
                        	//System.out.println("get excep yes!");
                        	System.out.println("¼àÌýBBC resume:");
                        }
                		
                		if(idx >= 0)
                		{
                			new_message = line.substring(idx);
                			Log.d(TAG2, "head = "+ new_head + "_" + b_gcm_get +".message=" + new_message);
                			util.threadSend(ctx, new_head, new_message, null, false);
                			b_gcm_get = false;
                		}
                }

        } catch (Exception e) {
        	  Log.e(TAG2, "exception : " + e.getLocalizedMessage());
                e.printStackTrace();
        }
        Log.w(TAG2, "analyze_log return!");
	}
	
	public static boolean DateOutlets(String date)
	{
		Date dto = util.parseDateString2(date);
		Date dtn = util.getNowDate();
		long diff = dtn.getTime() - dto.getTime();
		diff = diff / 1000;
		diff = diff / 3600;
		//Log.d(MainActivity.TAG, "diff:" + dtn + "-" + dto + "=" + diff);
		if(diff <= 16)
			return false;
		else
			return true;
		
	}
	
	public static boolean isAPNewsMess(String id, String text, String date)
	{
		if(DateOutlets(date))
			return false;
		final String APDT = "APdt";
		final String APTX = "APtext";
		
		String prevDt = util.readMessage(ctx,APDT);
		String prevtext = util.readMessage(ctx, APTX);
		
		if(prevDt.equals(date) && prevtext.equals(text))
		{
			//ÒÑ¾­·¢ËÍ¹ýÁË£¬²»ÐèÒªÔÙ´Î·¢ËÍ
			return false;
		}else
		{
			//±£´æÐÂµÄ
			util.saveLastMessage(ctx, APDT, date);
			util.saveLastMessage(ctx, APTX, text);
			return true;
		}
	}
	
	public static boolean isReutersNewsMess(String text, String date)
	{
		if(DateOutlets(date))
			return false;
		final String REUDT = "Reuterdt";
		final String REUTX = "Reutertext";
		
		String prevDt = util.readMessage(ctx,REUDT);
		String prevtext = util.readMessage(ctx, REUTX);
		
		if(prevDt.equals(date) && prevtext.equals(text))
		{
			//ÒÑ¾­·¢ËÍ¹ýÁË£¬²»ÐèÒªÔÙ´Î·¢ËÍ
			return false;
		}else
		{
			//±£´æÐÂµÄ
			util.saveLastMessage(ctx, REUDT, date);
			util.saveLastMessage(ctx, REUTX, text);
			return true;
		}
	}

	
	public static void CheckingNews()
	{
		Log.w(TAG2, "Checking AP created!");
		boolean fixed = true;
		String break_list = "http://services.apmobileapps.com/rest/v1/appush/getbreakinglist";
		String break_list_reuters = "http://apiservice.reuters.com/api/feed/channelitems?channel=breakingviews&count=10&edition=US&topstorycount=1&enablesymbol=false&noBylineInBody=true&deviceid=46bef647e7419882&format=json&devicecode=NXWPU&appcode=HZTBH&versioncode=1.1&apikey=f1d077a09fb75232ca2f258e23ea0896";
		//String break_item = "http://services.apmobileapps.com/rest/v1/appush/getstories?breakingnewsid=%s"; 
		String prev_text = "";
		String prev_date = "";
		
		String prev_text_r = "";
		String prev_date_r = "";
		try {
			while(fixed)
			{
				//Checking AP
				String news = util.getURLContents(break_list);
				if(news != null)
				{
					//Log.d(TAG2, "CheckingAP = " + news);
					JSONArray jsonArray = new JSONObject(news).getJSONArray("breakingnewsitem");
		            if (jsonArray != null) 
		            {
		                for (int length = jsonArray.length(), i = 0; i < length; ++i) {
		                    final JSONObject jsonObject = jsonArray.getJSONObject(i);
		                    if (jsonObject != null) {
		                        String id = jsonObject.optString("apbreakingnewsid");
		                        String tp = jsonObject.optString("apalerttype");
		                        String tx = jsonObject.optString("apbreakingnewstext");
		                        String dt = jsonObject.optString("apbreakingnewscreationdate");
		                        
		                        if (id.length() != 0 && tx.length() != 0 && dt.length() != 0 ) 
		                        {
		                        	
		                        	if(isAPNewsMess(id, tx, dt))
		                        	{
		                        		Log.d(TAG2, "find new id = "+ id + ".message = " + tx + ".date=" +dt );
		                        		if(prev_text.equals(tx) && prev_date.equals(dt))
		                        		{
		                        			Log.w(TAG2, "AP memory prev text and date equal, not need to send!");
		                        		}else
		                        		{
		                        			prev_text = tx;
		                        			prev_date = dt;
		                        			util.threadSend(ctx, "AP", tx, null, false);
		                        			
		                        		}
		                        	}
		                            break;
		                        }
		                    }//jsonobject != null
		                }//for
		            }//jsonArray != null
				}
				//Checking reuters
				String news_reuters = util.getURLContents(break_list_reuters);
				if(news_reuters != null)
				{
					//Log.d(TAG2, "Checking reuters = " + news_reuters);
					JSONArray jsonArray = new JSONObject(news_reuters).getJSONArray("topItems");
		            if (jsonArray != null) 
		            {
		            	for (int length = jsonArray.length(), i = 0; i < length; ++i) {
		                    final JSONObject jsonObject = jsonArray.getJSONObject(i);
		                    if (jsonObject != null) 
		                    {
		                    	 String stitle = jsonObject.optString("title");
			                     String slink = jsonObject.optString("link");
			                     String sdt = jsonObject.optString("published");
			                     String stx = jsonObject.optString("description");
			                     if(stitle.length() > 0 && sdt.length() > 0 && stx.length() > 0)
			                     {
			                    	 if(isReutersNewsMess(stx, sdt))
			                    	 {
			                    		 Log.d(TAG2, "reuters find new title = "+ stitle + ".message = " + stx + ".date=" +sdt );
			                        	 if(prev_text_r.equals(stx) && prev_date_r.equals(sdt))
			                        	{
			                        		Log.w(TAG2, "reuters memory prev text and date equal, not need to send!");
			                        	}else
			                        	{
			                        		prev_text_r = stx;
			                        		prev_date_r = sdt;
			                        		
			                        		String json_str = "{\"title\": \"" + stitle + "\", \"link\":\""+slink+"\",\"description\":\"" +stx + "\"}";
			                        		
			                        		util.threadSend(ctx, "Reuters", json_str, null, false);
			                        			
			                        	}
			                    	 }
			                    	 break;
			                     }
			                     
		                    }
		            	}
		            }
				}
				
				Thread.sleep(20000);//ÑÓ³Ù20Ãë
			}//while
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		Log.w(TAG2, "Checking AP return!");
	}
	
	
	
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		ctx = this.getBaseContext();
		Log.i(TAG2, "ºóÌ¨·þÎñ±»´´½¨");
		new Thread(new Runnable() {
			@Override
			public void run() {
				analyze_log();
				
			}
		}).start();
		
		/*
		new Thread(new Runnable() {
			@Override
			public void run() {
				CheckingNews();
				
			}
		}).start();
		*/
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
		Log.i(TAG2, "ºóÌ¨·þÎñ±»Æô¶¯");
		
		//Log.e(TAG2, "ºóÌ¨·þÎñ·µ»Ø");
	}
	
	
	
	@Override
	public void onDestroy() {
		Log.e(TAG2, "ºóÌ¨·þÎñdestroy");
		super.onDestroy();
		
	}
	
	
}
