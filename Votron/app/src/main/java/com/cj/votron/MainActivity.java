package com.cj.votron;
/**********************************************************
 *
 * @author gvamos
 * Todo:
 *   - How can I tell what sort of a JSON object am I holding (List, Hash,...?)
 *   - Do I need a unique parse process for each data type? (Voter, Election, Candidate...?)
 *
 **********************************************************/
import java.util.ArrayList;
import java.util.List;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends Activity {


    ListView ballotListView ;
    ArrayAdapter<String> ballotListAdapter ;

	Spinner electionsSpinner;
	List<String> nameList;
    ArrayAdapter<String> electionsArrayAdapter;
	
	Spinner voterSpinner;
	List<String> voterList;
    ArrayAdapter<String>voterListAdapter;
	
	Button btnConfig;
    Button btnBallot;

    protected void onResume(){
        super.onResume();

        Util.dbg("DBG MainActivity.java");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setup();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

    	getMenuInflater().inflate(R.menu.actions, menu);
    	return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        System.out.println("DBG: onOptionsItemSelected:" + id);
        Intent intent = null;
        switch (id) {
            case R.id.action_settings:
                break;
            case R.id.refresh:
                refreshElections();
                refreshVoters();
                break;
            case R.id.voters:
            	System.out.println("DBG: Voters Activity");
            	intent = new Intent(this, VotersActivity.class);
              break;
            case R.id.elections:
            	intent = new Intent(this, ElectionsActivity.class);
              break;
            case R.id.debug:
            	intent = new Intent(this, DebugActivity.class);
              break;
            default:
            	Log.i(this.getClass().getName(),":onOptionsItemSelected default:" + id);
                return super.onOptionsItemSelected(item);
        }
        if (null != intent){
        	System.out.println("DBG: intent action=" + intent.getAction());
        	startActivity(intent);
        }
        return true;
    }


    /**************************************************************
     *
     * Setup
     *
     ***************************************************************/
    private void setup(){

        electionsSpinner = (Spinner) findViewById(R.id.electionSpinner);
        voterSpinner = (Spinner) findViewById(R.id.voterSpinner);

        btnConfig = (Button) findViewById(R.id.btnConfig);
        btnBallot = (Button) findViewById(R.id.btnBallot);
        ballotListView = (ListView) findViewById(R.id.BallotListView);

        addListenerOnButton();
        addListenerOnSpinnerItemSelection();
    }

    public void fetchBallot(View v){
        if (electionsSpinner == null || electionsSpinner.getSelectedItem() == null){
            Toast.makeText(MainActivity.this, "You must chose an election", Toast.LENGTH_SHORT
            ).show();
            return;
        }
        if (voterSpinner == null || voterSpinner.getSelectedItem() == null){
            Toast.makeText(MainActivity.this, "You must chose a voter", Toast.LENGTH_SHORT
            ).show();
            return;
        }

        String election = (String)electionsSpinner.getSelectedItem();
        String voter = (String)voterSpinner.getSelectedItem();

        CandidateUpdater candidateUpdater = new CandidateUpdater(this, ballotListView);
        candidateUpdater.fetch(Config.SERVER + "/" + Config.ELECTIONS + "/" + election + "/" + Config.CANDIDATES);
    }

    public void refreshElections(){
        NameListUpdater electionsUpdater = new NameListUpdater(this, electionsSpinner);
        electionsUpdater.fetch(Config.SERVER + "/" + Config.ELECTIONS);
    }

    public void refreshVoters(){
        NameListUpdater voterUpdater = new NameListUpdater(this, voterSpinner);
        voterUpdater.fetch(Config.SERVER + "/" + Config.VOTERS);
    }


    //get the selected dropdown list value
    public void addListenerOnButton() {

        electionsSpinner = (Spinner) findViewById(R.id.electionSpinner);
        voterSpinner = (Spinner) findViewById(R.id.voterSpinner);


        btnConfig.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Toast.makeText(MainActivity.this,
                        "OnClickListener : " +
                                "\nElectionSpinner 1 : " + String.valueOf(electionsSpinner.getSelectedItem()) +
                                "\nVoterSpinner 2 : " + String.valueOf(voterSpinner.getSelectedItem()),
                        Toast.LENGTH_SHORT
                ).show();
            }

        });
    }

    public void addListenerOnSpinnerItemSelection(){

        electionsSpinner = (Spinner) findViewById(R.id.electionSpinner);
        electionsSpinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());

    }


    class NameListUpdater implements Callback{

        HttpAgent httpAgent;
        ArrayAdapter<String> arrayAdapter;
        Context context;
        Spinner spinner;

        NameListUpdater(Context context, Spinner spinner){
            this.context = context;
            this.spinner = spinner;
            httpAgent = new HttpAgent(context);
        }

        void fetch(String url){
            httpAgent.fetch(url,this,"Fetch " + url);
        }

        @Override
        public void ok(String jsonData) {
            nameList = new ArrayList<String>();
            try {
                JSONArray scriptItems = new JSONArray(jsonData);
                for(int i =0; i<scriptItems.length(); i++){
                    JSONObject obj = scriptItems.getJSONObject(i);
                    nameList.add(obj.getString("name"));
                }
                arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, nameList);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(arrayAdapter);

            } catch (Exception e){
                throw new RuntimeException(e);
            }
        }

        @Override
        public void fail(String s) {
            throw new RuntimeException("fail called with " +s);
        }
    }

    class CandidateUpdater implements Callback{

        HttpAgent httpAgent;
        ArrayAdapter<String> arrayAdapter;
        Context context;
        ListView listView;

        CandidateUpdater(Context context, ListView listView){
            this.context = context;
            this.listView = listView;
            httpAgent = new HttpAgent(context);
        }

        void fetch(String url){
            httpAgent.fetch(url,this,"Fetch " + url);
        }

        @Override
        public void ok(String jsonData) {
            nameList = new ArrayList<String>();
            try {
                JSONArray scriptItems = new JSONArray(jsonData);
                for(int i =0; i<scriptItems.length(); i++){
                    JSONObject obj = scriptItems.getJSONObject(i);
                    nameList.add(obj.getString("name"));
                }
                arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, nameList);
                listView.setAdapter(arrayAdapter);

            } catch (Exception e){
                throw new RuntimeException(e);
            }
        }

        @Override
        public void fail(String s) {
            throw new RuntimeException("fail called with " +s);
        }
    }
}
