package com.cj.votron;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.view.View.OnClickListener;

public class DebugActivity extends Activity {


	public static String displayBuffer = "Move along.  Nothing to see here.";
	EditText debugText;
	Config config;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_debug);

		initialize();
	}


	void initialize(){

		debugText = (EditText)findViewById(R.id.debugDisplay);
		config = Config.getInstance();
	}


	public void dbg1Pressed(View view){
		System.out.println("Pressed DBG1");
		ServerLink.getInstance().getElectionQuery(Config.VOTERS, this);
		debugText.setText(displayBuffer);
	}
	
	public void dbg2Pressed(View view){
		System.out.println("Pressed DBG2");
		String dbpq = "select*%7Bdbpedia%3ALos_Angeles+rdfs%3Alabel+%3Flabel%7D";
		ServerLink.getInstance().getDbpediaQuery(dbpq, this);
	}

	public void dbg3Pressed(View view){
		System.out.println("Pressed DBG3");
		String result = config.debug3("baz", DebugActivity.this);
		debugText.setText(result);
	}
	
	public void display(String msg){
		System.out.println("DBG display");
		System.out.println(msg);
		debugText.setText(msg);
		System.out.println("DBG display over");
		
	}
}