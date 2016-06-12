package com.nis.bcreceiver;


import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;


import android.os.AsyncTask;


public class HttpReq extends AsyncTask<String, Integer, String>{

	private static HashMap<String, String>docIDs = new HashMap<String, String>(); 
	
	private static String gmsg = "";
	private static String gdocID = "";
	private static Object waitObj = new Object();
	
	
	public static void wait(int timeout)
	{
		synchronized (waitObj) {
			try {
				waitObj.wait(timeout);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void add(String msg, String docID)
	{
		synchronized (docIDs) 
		{
			if(docIDs.size() >= 10)
			{
				docIDs.clear();
				if(gmsg.length() > 0)
				{
					docIDs.put(gmsg, gdocID);
				}
			}
			if(!docIDs.containsKey(msg))
			{
				docIDs.put(msg, docID);
			}
			gmsg = msg;
			gdocID = docID;
		}
		synchronized (waitObj) {
			waitObj.notify();
		}
	}
	
	public static String getdocID(String msg)
	{
		wait(2000);
		synchronized (docIDs) 
		{
			if(docIDs.containsKey(msg))
			{
				return (String)docIDs.get(msg);
			}else
			{
				return null;
			}
		}
		
	}
	
	public HttpReq() {
		
	}
	
	public void StartRequest(String url)
	{
		this.execute(url);
		
	}
	
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		
	}

	@Override
	protected String doInBackground(String... arg0) {
		
		String retUrl = "";
		try {
			HttpGet httpRequest = new HttpGet(arg0[0]);
			httpRequest.setHeader("Accept", "text/json");
			HttpClient httpClient = new DefaultHttpClient();
			
			HttpResponse response = httpClient.execute(httpRequest);
			int statusCode = response.getStatusLine().getStatusCode();
			if(statusCode == 200)
			{
				retUrl = EntityUtils.toString(
						response.getEntity(), HTTP.UTF_8);
				AppLog.i("reqUrl:" + retUrl);
				if(retUrl != null && retUrl.length() > 0)
				{
					JSONArray jarray = new JSONArray(retUrl);
					for(int i = 0; i < jarray.length(); i++)
					{
						JSONObject obj = jarray.getJSONObject(i);
						String docID = obj.getString("id").replace("doc_", "");
						String msg = obj.getString("c");
						add(msg, docID);
						AppLog.i("docid:" + docID + ".msg:" + msg);
					}
				
				}
				
			}else
			{
				AppLog.w("reqUrl status failed, statuscode:" + statusCode);
			}
		} catch (Exception e) {
			AppLog.e("reqURL exception:" + e.getLocalizedMessage(), e);
		}
		return retUrl;
	}

}
