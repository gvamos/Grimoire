package com.palookaville.atester;

import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.os.Build;

public class LauncherActivity extends Activity {
	
	AndroidBaseApi baseApi;
	Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment()).commit();
        }
        context = this;
        baseApi = new AndroidBaseApi(context);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container,
                    false);
            return rootView;
        }
    }

    public void buttonPushedIntentFired(View v) {

        final String ANGRY_BIRDS = "com.rovio.angrybirdsstarwarsii.ads";
        final String SWIFTKEY = "com.touchtype.swiftkey";

        startActivityOrGoToMarket(ANGRY_BIRDS);
    }
    
    String dateString(Long l, String title){
    	return "" + (new Date(l)).toString() + " " + title;
    }
    
    public void buttonDisplayTimestamps(View v) {
    	String dirDateString = dateString(baseApi.getAppDirInstallationTime(),"App Dir Date");
    	String pkgInstallDateString = dateString(baseApi.getPackageInstallationTime(),"Pkg Install Date");
    	String pkgUpdateDateString = dateString(baseApi.getPackageUpdateTime(),"Pkg Update Date");
    	String dateFrame = dirDateString + "\n" + pkgInstallDateString + "\n" + pkgUpdateDateString;
        Toast.makeText(getApplicationContext(),dateFrame, Toast.LENGTH_LONG).show();
    }
    
    public void buttonTouchDirDate(View v){
    	baseApi.touchFile();
    	String dirDateString = dateString(baseApi.getAppDirInstallationTime(),"App Dir Date");
        Toast.makeText(getApplicationContext(),dirDateString, Toast.LENGTH_LONG).show();  	
    }

    private void startActivityOrGoToMarket(String appIdentifier) {
        Log.i("asdf", "looking to go to app: " + appIdentifier);
        Intent sendIntent;
        PackageManager manager = getPackageManager();
        try {
            sendIntent = manager.getLaunchIntentForPackage(appIdentifier);
            if (sendIntent == null) {
                Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appIdentifier));
                List<ResolveInfo> resolveInfos = manager.queryIntentActivities(marketIntent, 0);
                for (ResolveInfo resolveInfo : resolveInfos) {
                    Log.i("resolver packageName", ""+resolveInfo.resolvePackageName);
                    Log.i("resolver toString", ""+resolveInfo);

                }
    //                if (marketIntent.resolveActivity(manager) != null) {
                if (resolveInfos.size() > 0) {
                    startActivity(marketIntent);
                } else {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appIdentifier)));
                }
            } else {
                Log.i("ddd", "action is " + sendIntent.getAction());
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        "This is my text to send.");
                sendIntent.setType("text/plain");
                if (sendIntent == null)
                    throw new PackageManager.NameNotFoundException();
                sendIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                startActivity(sendIntent);
                Toast.makeText(getApplicationContext(),
                        "buttonPushedIntentFired ran", Toast.LENGTH_SHORT)
                        .show();
            }
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(getApplicationContext(), "Launch failed",
                    Toast.LENGTH_SHORT).show();
        }
    }

}
