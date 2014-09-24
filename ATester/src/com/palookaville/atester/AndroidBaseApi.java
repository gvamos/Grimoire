package com.palookaville.atester;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class AndroidBaseApi {
	
	Context context;
	
	public AndroidBaseApi(Context context){
		this.context = context;
	}
	
	public void touchFile(){
		String fileName = "touch";
		ApplicationInfo appInfo;
		PackageManager pm = context.getPackageManager();
       
		try {
			appInfo = pm.getApplicationInfo(context.getPackageName(), 0);
			String appDir = appInfo.sourceDir;
			String touchPath = appDir + File.pathSeparator + fileName;
			PrintWriter fileWriter= new PrintWriter(touchPath, "UTF-8");
			fileWriter.println((new Date()).toString());
			fileWriter.close();

		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	};
	
    public Long getPackageInstallationTime() {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            return pi.firstInstallTime;
        } catch (PackageManager.NameNotFoundException e) {
            return -1L;
        }
    }
    
    public Long getPackageUpdateTime() {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            return pi.lastUpdateTime;
        } catch (PackageManager.NameNotFoundException e) {
            return -1L;
        }
    }
    
    public Long getAppDirInstallationTime() {
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo appInfo = pm.getApplicationInfo(context.getPackageName(), 0);
            String appFile = appInfo.sourceDir;
            return new File(appFile).lastModified();
        } catch (PackageManager.NameNotFoundException e) {
            return -1L;
        }
    }
}
