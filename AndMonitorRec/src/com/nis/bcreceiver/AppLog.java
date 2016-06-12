package com.nis.bcreceiver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;


import android.os.Environment;
import android.util.Log;


public class AppLog
{
	public final static String TAG= "BC_REC";
	public final static String Enter = "\r\n";
	public final static String Tab3 = "\t\t\t";
	
	public static interface LogListener
	{
		public void getLog(String msg);
	}
    public final static int LEVEL_ERROR = 0;
    public final static int LEVEL_WARN = 1;
    public final static int LEVEL_INFO = 2;
    public final static int LEVEL_DEBUG = 3;
    public final static int LEVEL_VERBOSE = 4;
    public static int log_level			= LEVEL_VERBOSE;
    public static int write_log_level	= LEVEL_VERBOSE;
    
    public static String log_file_path = Environment.getExternalStorageDirectory().getPath()+File.separator+"ntes_ad_log.txt";
    static  LogListener listener=null;
    
    private final static long LOG_MAXSIZE = 1024 * 1024 * 2;
    
    public static String getOutPath()
    {
    	String path = "";
    	if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
			path = Environment.getExternalStorageDirectory().getPath()+File.separator+"netease"+File.separator+"BCreceiver"+File.separator;
		else
			path = "mnt\\";
    	File file = new File(path);
		if(!file.exists())
			file.mkdirs();
    	return path;
    }
    
    public static void init()
    {
    	
    	log_file_path = getOutPath() +"BCreceiver_log.txt";
    	wrtieToFile(Enter + Enter, LEVEL_WARN);
    	i("log file path:" + log_file_path);
    }
   
   public static void setLogListener(LogListener aListener)
   {
	   listener = aListener;
   }
  
    public static void error(String tag, String msg)
    {
        if(log_level >= LEVEL_ERROR)
        {
            Log.e(tag, msg);
            wrtieToFile("e :"+msg, LEVEL_ERROR);
        }
    }
  
       
   
    
    public static void warn(String tag, String msg)
    {
        if(log_level >= LEVEL_WARN)
        {
            Log.w(tag, msg);
           	wrtieToFile("w :"+msg, LEVEL_WARN);
        }
    }
    
  
    
    public static void info(String tag, String msg)
    {
        if(log_level >= LEVEL_INFO)
        {
            Log.i(tag, msg);
            wrtieToFile("i :"+msg, LEVEL_INFO);
        }
    }
    
  
    public static void debug(String tag, String msg)
    {
        if(log_level >= LEVEL_DEBUG)
        {
            Log.d(tag, msg);
            wrtieToFile("d :"+msg, LEVEL_DEBUG);
        }
    }
    
    public static void verbose(String tag, String msg)
    {
        if(log_level >= LEVEL_VERBOSE)
        {
            Log.v(tag, msg);
            wrtieToFile("v :"+msg, LEVEL_VERBOSE);
        }
    }
  
    public static void i(String msg)
    {
    	info(TAG, msg);
    }
    
    public static void w(String msg)
    {
    	warn(TAG, msg);
    }
    
    public static void e(String msg)
    {
 	   error(TAG, msg);
    }
    
    public static void e(String msg, Throwable tr)
    {
    	if(log_level >= LEVEL_ERROR)
        {
            Log.e(TAG, msg, tr);
            
            StackTraceElement [] trlist = tr.getStackTrace();
            
            String message = msg + Enter;
            
            message += Tab3 + tr.toString() + Enter;
            
            for(StackTraceElement el : trlist)
            {
            	message += Tab3 + el.toString() + Enter;
            }
            wrtieToFile("e :"+message, LEVEL_ERROR);
            
            
        }
    }
    
    public static void d(String msg)
    {
    	debug(TAG, msg);
    }
    
    private static void checklogFileSize()
    {
    	try {
    		File pf = new File(log_file_path);
    		if(pf.exists())
    		{
    			long fsize = pf.length();
    			if(fsize >= LOG_MAXSIZE)
    			{
    				
    				RandomAccessFile rafile = new RandomAccessFile(pf, "rw");
    				int halfsize = (int)( fsize / 2) - 16;
    				rafile.seek(halfsize);
    				byte [] r_bytes = new byte[halfsize + 64];
    				
    				int nReaded = rafile.read(r_bytes);
    				rafile.close();
    				
    				
    				FileOutputStream fout = new FileOutputStream(pf);
    				fout.write(r_bytes, 0, nReaded);
    				fout.close();
    			}
    		}
    		   		
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    }
    
    private synchronized static void wrtieToFile(String msg, int level)
    {
      /*  Message m=new Message();m.obj=d.toGMTString().substring(12,21)+" "+msg;
         handle.sendMessage(m);*/
    	if(write_log_level < level)
    		return;
    	
    	checklogFileSize();
         SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
         Date date = new Date();
         String time= sdf.format(date);
         
         if(listener!=null)
        	 listener.getLog(time+" "+msg);
         
         try {
				FileOutputStream out=new FileOutputStream(log_file_path,true);
				
				
				out.write((time+" "+msg + Enter).getBytes("utf-8"));
				
				if(out!=null)
					out.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
         catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
    }
}
