package com.nis.bcreceiver;

import java.util.Date;
import java.util.Vector;

import android.content.Context;

public class PushMessCache {
	
	public final static int UNKNOWN_INDEX = 0;
	public final static int TITLE_INDEX = 1;
	public final static int TIME_INDEX = 2;
	public final static int TEXT_INDEX = 3;
	
	public class MessageData
	{
		public String packageName;
		public String timeText;
		public String title;
		public String message;
		public Date date;
		public boolean isChina;
		public String docID;
		
		public MessageData()
		{
			date = new Date();
			message = "";
			timeText = "";
			title = "";
			packageName = "";
			isChina = false;
			docID = "";
		}
		
		public void print()
		{
			StringBuffer str = new StringBuffer(packageName);
			if(title.length() > 0)
			{
				str.append(".title:");
				str.append(title);
			}
			if(timeText.length() > 0)
			{
				str.append(".timeText:");
				str.append(timeText);
			}
			str.append(".message:");
			str.append(message);
			
			str.append(".Date:");
			str.append(date.toLocaleString());
			
			AppLog.i(str.toString());
			
			
		}
	}
	
	public Vector<MessageData> messVec = new Vector<MessageData>();
	
	public void addMess(int label_Id, MessageData data, String txt)
	{
		int id_i = PackName.checkID(data.packageName, label_Id);
		
		switch (id_i) 
		{
		case UNKNOWN_INDEX:
			//AppLog.w("UNKNOWN_INDEX for txt:" + txt + ".pack:"+data.packageName + ". label_id" + label_Id);
			break;
		case TITLE_INDEX:
			data.title = txt;
			break;
		case TIME_INDEX:
			data.timeText = txt;
			break;
		case TEXT_INDEX:
			data.message = txt;
			break;
		default:
			break;
		}
	}
	
	private boolean news_exist(MessageData data)
	{
		boolean bIn = false;
		for(MessageData item : messVec)
		{
			if(item.packageName.equals(data.packageName))
			{
				if(item.message.equals(data.message))
				{
					bIn = true;
					break;
				}
			}
		}
		if(!bIn)
		{
			if(messVec.size() >= 100)
			{
				AppLog.w("messVec size 100 clear.");
				messVec.clear();
			}
			messVec.add(data);
		}
		return bIn;
	}
	
	public boolean sendMess(Context cxt, MessageData data)
	{
		//message为空不发
		if(data.message.length() <= 0)
			return false;
		if(!news_exist(data))
		{
			AppLog.i("need to send message:");
			data.print();
			util.threadSend(cxt, PackName.getNewsSource(data.packageName), data.message, data.title, data.isChina, data.docID);
			return true;
		}else
		{
			//已经存在了
			AppLog.w("Message is exist in vector mess:");
			data.print();
			return false;
		}
	}
	
	
}
