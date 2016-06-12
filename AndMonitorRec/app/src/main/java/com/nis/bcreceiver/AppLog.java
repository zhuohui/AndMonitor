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
	

    public final static int LEVEL_ERROR = 0;
    public final static int LEVEL_WARN = 1;
    public final static int LEVEL_INFO = 2;
    public final static int LEVEL_DEBUG = 3;
    public final static int LEVEL_VERBOSE = 4;
    public static int log_level			= LEVEL_VERBOSE;


    
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
    

   

  
    public static void error(String tag, String msg)
    {
        if(log_level >= LEVEL_ERROR)
        {
            Log.e(tag, msg);

        }
    }
  
       
   
    
    public static void warn(String tag, String msg)
    {
        if(log_level >= LEVEL_WARN)
        {
            Log.w(tag, msg);

        }
    }
    
  
    
    public static void info(String tag, String msg)
    {
        if(log_level >= LEVEL_INFO)
        {
            Log.i(tag, msg);

        }
    }
    
  
    public static void debug(String tag, String msg)
    {
        if(log_level >= LEVEL_DEBUG)
        {
            Log.d(tag, msg);

        }
    }
    
    public static void verbose(String tag, String msg)
    {
        if(log_level >= LEVEL_VERBOSE)
        {
            Log.v(tag, msg);

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
            
            
        }
    }
    
    public static void d(String msg)
    {
    	debug(TAG, msg);
    }
    

}
