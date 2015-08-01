package com.example.qrticket;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.location.Location;
import android.location.LocationManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.Settings;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends Activity implements OnClickListener {

	
	private Button scanBtn,payBtn;
	private TextView formatTxt, contentTxt,srcTxt,destTxt,srcVal,destVal,stageTxt;
	String fare;
    AppLocationService appLocationService;
    private Spinner spinner1;
    private Spinner spinner2;
    private String[] st;
    private String passStr;
    public static final String MIME_TEXT_PLAIN = "text/plain";
    

	public static final String TAG = "NfcDemo";
	 
    private TextView mTextView;
    private NfcAdapter mNfcAdapter;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		appLocationService = new AppLocationService(
                MainActivity.this);
		//instantiate UI items
		scanBtn = (Button)findViewById(R.id.scan_button);
		fare="";
		srcTxt=(TextView)findViewById(R.id.textView1);
		destTxt=(TextView)findViewById(R.id.textView2);
		srcVal=(TextView)findViewById(R.id.textView3);
		payBtn=(Button)findViewById(R.id.button1);
		payBtn.setOnClickListener(this);
		scanBtn.setOnClickListener(this);
		getLocation();
	mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		 
        if (mNfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
           // finish();
            return;
 
        }
     
        if (!mNfcAdapter.isEnabled()) {
            mTextView.setText("NFC is disabled.");
        } else {
            
        }
         
        handleIntent(getIntent());
	
		
		
		
	}
	private void handleIntent(Intent intent) {
        // TODO: handle Intent
		String action = intent.getAction();
	    if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
	         
	        String type = intent.getType();
	        if (MIME_TEXT_PLAIN.equals(type)) {
	 
	            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
	            new NdefReaderTask().execute(tag);
	             
	        } else {
	            Log.d(TAG, "Wrong mime type: " + type);
	        }
	    } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
	         
	        // In case we would still use the Tech Discovered Intent
	        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
	        String[] techList = tag.getTechList();
	        String searchedTech = Ndef.class.getName();
	         
	        for (String tech : techList) {
	            if (searchedTech.equals(tech)) {
	                new NdefReaderTask().execute(tag);
	                break;
	            }
	        }
	    }

    }

	private class NdefReaderTask extends AsyncTask<Tag, Void, String> {
		 
	    @Override
	    protected String doInBackground(Tag... params) {
	        Tag tag = params[0];
	         
	        Ndef ndef = Ndef.get(tag);
	        if (ndef == null) {
	            // NDEF is not supported by this Tag. 
	            return null;
	        }
	 
	        NdefMessage ndefMessage = ndef.getCachedNdefMessage();
	 
	        NdefRecord[] records = ndefMessage.getRecords();
	        for (NdefRecord ndefRecord : records) {
	            if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
	                try {
	                    return readText(ndefRecord);
	                } catch (UnsupportedEncodingException e) {
	                    Log.e(TAG, "Unsupported Encoding", e);
	                }
	            }
	        }
	 
	        return null;
	    }
	     
	    private String readText(NdefRecord record) throws UnsupportedEncodingException {
	        /*
	         * See NFC forum specification for "Text Record Type Definition" at 3.2.1 
	         * 
	         * http://www.nfc-forum.org/specs/
	         * 
	         * bit_7 defines encoding
	         * bit_6 reserved for future use, must be 0
	         * bit_5..0 length of IANA language code
	         */
	 
	        byte[] payload = record.getPayload();
	 
	        // Get the Text Encoding
	        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
	 
	        // Get the Language Code
	        int languageCodeLength = payload[0] & 0063;
	         
	        // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
	        // e.g. "en"
	         
	        // Get the Text
	        return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
	    }
	     
	    @Override
	    protected void onPostExecute(String result) {
	        if (result != null) {
	            //mTextView.setText("Read content: " + result);
	        	passStr=result;
	        	getStageList("100");
	        }
	    }
	}



	public void onClick(View v){
		//check for scan button
		if(v.getId()==R.id.scan_button){
			//instantiate ZXing integration class
			IntentIntegrator scanIntegrator = new IntentIntegrator(this);
			//start scanning
			scanIntegrator.initiateScan();
		}
		else if(v.getId()==R.id.button1)
		{
			
			spinner2 = (Spinner) findViewById(R.id.spinner2);

			Toast.makeText(getApplicationContext(),
					"OnClickListener : " + 
			                
			                "\nSpinner 2 : "+ String.valueOf(spinner2.getSelectedItem()),
						Toast.LENGTH_SHORT).show();

			
            String result = null;
			Toast toast = Toast.makeText(getApplicationContext(), 
					"Confirm", Toast.LENGTH_SHORT);
			toast.show();
			Intent toNextPage = new Intent(MainActivity.this,
                    PaymentActivity.class); 
			if(spinner2.getSelectedItem()!=null){
			toNextPage.putExtra("point", srcVal.getText()+"-"+spinner2.getSelectedItem().toString());
			//toNextPage.putExtra("point", "Mahadevpura"+"-"+"Bagmane WTC");
				startActivity(toNextPage);
			}
			
		}
		}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		//retrieve result of scanning - instantiate ZXing object
		IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		//check we have a valid result
		if (scanningResult != null) {
			//get content from Intent Result
			String scanContent = scanningResult.getContents();
			//get format name of data scanned
			String scanFormat = scanningResult.getFormatName();
			String[] str=scanContent.split(" ");
			//output to UI
			Toast toast = Toast.makeText(getApplicationContext(), 
					""+scanContent, Toast.LENGTH_SHORT);
			toast.show();
			
			getStageList(str[0]);
			//formatTxt.setText("FORMAT: "+scanFormat);
			//contentTxt.setText("CONTENT: "+scanContent);
			//srcVal.setText(str[0]);
			//destVal.setText(str[0]);
			//fare=str[1];
			
		}
		else{
			//invalid scan data or scan canceled
			Toast toast = Toast.makeText(getApplicationContext(), 
					"No scan data received!", Toast.LENGTH_SHORT);
			toast.show();
			
		}
	}
	
	public void getStageList(String stage)
	{
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

		StrictMode.setThreadPolicy(policy); 
		HttpPost httppost;
        StringBuffer buffer;
        
        HttpClient httpclient;
        ProgressDialog dialog = null;
    	 try{            
             
    		 String link = "http://dpk3593.net84.net/qrticket/getData.php?id="+stage;
             System.out.println(link);
             URL url = new URL(link);
             HttpClient client = new DefaultHttpClient();
             HttpGet request = new HttpGet();
             request.setURI(new URI(link));
             HttpResponse response = client.execute(request);
             BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

               StringBuffer sb = new StringBuffer("");
               String line="";
               
               while ((line = in.readLine()) != null) {
                  sb.append(line);
                  break;
                }
                in.close();
                System.out.println(sb.toString());
                st=sb.toString().split("-");
                addItemsOnSpinner2();
         }catch(Exception e){
            
        	 e.printStackTrace();
         }
    	
		
		
		
	}
	public void addItemsOnSpinner2() {
		 
		spinner2 = (Spinner) findViewById(R.id.spinner2);
		List<String> list = new ArrayList<String>();
		for(int i=0;i<st.length;i++){
		list.add(st[i]);
		}
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
			android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner2.setAdapter(dataAdapter);
	  }
	 public void getLocation()
	    {
		 Toast.makeText(getApplicationContext(), "Retrieving GPS Location", Toast.LENGTH_LONG).show();
		  Location location = appLocationService
                  .getLocation(LocationManager.GPS_PROVIDER);

          //you can hard-code the lat & long if you have issues with getting it
          //remove the below if-condition and use the following couple of lines
          //double latitude = 37.422005;
          //double longitude = -122.084095

          if (location != null) {
        	
              double latitude = location.getLatitude();
              double longitude = location.getLongitude();
              LocationAddress locationAddress = new LocationAddress();
              locationAddress.getAddressFromLocation(latitude, longitude,
                      getApplicationContext(), new GeocoderHandler());
          } else {
              showSettingsAlert();
          }
	    }
	 public void showSettingsAlert() {
	        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
	                MainActivity.this);
	        alertDialog.setTitle("SETTINGS");
	        alertDialog.setMessage("Enable Location Provider! Go to settings menu?");
	        alertDialog.setPositiveButton("Settings",
	                new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int which) {
	                        Intent intent = new Intent(
	                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	                        MainActivity.this.startActivity(intent);
	                    }
	                });
	        alertDialog.setNegativeButton("Cancel",
	                new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int which) {
	                        dialog.cancel();
	                    }
	                });
	        alertDialog.show();
	    }
	  private class GeocoderHandler extends Handler {
	        @Override
	        public void handleMessage(Message message) {
	            String locationAddress;
	            switch (message.what) {
	                case 1:
	                    Bundle bundle = message.getData();
	                    locationAddress = bundle.getString("address");
	                    break;
	                default:
	                    locationAddress = null;
	            }
	            //tvAddress.setText(locationAddress);
	            srcVal.setText(locationAddress);
	            if(locationAddress.isEmpty())
	            	srcVal.setText("Bagmane WTC");
	            //Toast.makeText(getApplicationContext(),"Check Internet Connection", Toast.LENGTH_LONG).show();
	            }
	    }


    


}




