package com.example.qrticket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import com.google.zxing.integration.android.IntentIntegrator;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class PaymentActivity extends Activity implements android.view.View.OnClickListener {

    protected static String API_KEY = "61361e6f-cfa1-47e7-89df-94d0a091ae76";

    
    private String[] str;
    
	protected static String SHARED_SECRET = "272186eb-166d-4059-87a0-33472a0d3a1a";
   private static String SERVICE_URI = "https://sandbox.api.visa.com/cva/cf/AccountLookup?apiKey=" + API_KEY;

    private static String SERVICE_URI_OCT = "https://sandbox.api.visa.com/pm/ft/OriginalCreditTransactions?apiKey=" + API_KEY;
    private Button payBtn;
    private TextView locationTxt,srcTxt,destTxt,fareTxt;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_payment);
		//locationTxt=(TextView)findViewById(R.id.textView1);
		srcTxt=(TextView)findViewById(R.id.textView1);
		destTxt=(TextView)findViewById(R.id.textView2);
		fareTxt=(TextView)findViewById(R.id.textView3);
		String data=getIntent().getExtras().getString("point");
		str=data.split("-");
		srcTxt.setText(str[0]);
		destTxt.setText(str[1]);
		fareTxt.setText("");
		getQRData();
		payBtn=(Button)findViewById(R.id.button1);
		payBtn.setOnClickListener(this);
	}
	public void onClick(View v){
		//check for scan button
		if(v.getId()==R.id.button1){
			Toast.makeText(getApplicationContext(), "Payment Initiated", Toast.LENGTH_LONG).show();
			//Driver.execute_OriginalCreditTransactions(getApplicationContext());
			OctTrans oct=new OctTrans();
			
			oct.doTrans(getApplicationContext());
			Button b=(Button)findViewById(R.id.button1);
			b.setEnabled(false);
			if(OctTrans.flagSuccess==1)
			{
				sendMhuail();
			}
		}
	}
	public void getQRData()
    {
    	
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

		StrictMode.setThreadPolicy(policy); 
		HttpPost httppost;
        StringBuffer buffer;
        
        HttpClient httpclient;
        ProgressDialog dialog = null;
    	 try{            
             
    		 String link = "http://dpk3593.net84.net/qrticket/fareCalculate.php?src="+str[0].replace(" ", "%20")+"&dest="+str[1].replace(" ", "%20");
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
                String[] st=sb.toString().split("-");
               fareTxt.setText(st[0]);
               
         }catch(Exception e){
            
        	 e.printStackTrace();
         }
    	
    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.payment, menu);
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
/*	class OnlineCreditTransactionService extends AsyncTask<String, String, String> {

	    
		private String responseCode;
	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        
	        
	    }

	   
	    @Override
	    protected String doInBackground(String... f_url) {
	        int count;
	        String result = null;
	        try {
	        	JSONObject json = new JSONObject();
                json.put("SystemsTraceAuditNumber", "350420");
                json.put("RetrievalReferenceNumber", "401010350420");
                json.put("DateAndTimeLocalTransaction", "2021-10-26T21:32:52");
                json.put("AcquiringBin", "409999");
                json.put("AcquirerCountryCode", "101");
                json.put("SenderReference", "");
                json.put("SenderAccountNumber", "1234567890123456");
                json.put("SenderCountryCode", "USA");
                json.put("TransactionCurrency", "USD");
                json.put("SenderName", "John Smith");
                json.put("SenderAddress", "44 Market St.");
                json.put("SenderCity", "San Francisco");
                json.put("SenderStateCode", "CA");
                json.put("RecipientCardPrimaryAccountNumber", "4957030420210454");
                json.put("Amount", "112.00");
                json.put("BusinessApplicationID", "AA");
                json.put("MerchantCategoryCode", "6012");
                json.put("TransactionIdentifier", 234234322342343L);
                json.put("SourceOfFunds", "03");

                JSONObject cardAcceptorObj = new JSONObject();

                cardAcceptorObj.put("Name", "John Smith");
                cardAcceptorObj.put("TerminalId", "13655392");
                cardAcceptorObj.put("IdCode", "VMT200911026070");

                JSONObject addressObj = new JSONObject();
                addressObj.put("State", "CA");
                addressObj.put("County", "081");
                addressObj.put("Country", "USA");
                addressObj.put("ZipCode", "94105");

                cardAcceptorObj.put("Address", addressObj);

                json.put("CardAcceptor", cardAcceptorObj);

                // Instantiate the custom HttpClient
                DefaultHttpClient httpClient = new MyHttpClient(getApplicationContext());
                httpClient.getParams().setParameter("rejectUnauthorized", false);
                System.out.println(httpClient.getParams()+"--------------------------------------------");
                HttpPost request = new HttpPost(SERVICE_URI_OCT);
                request.setHeader("Accept", "application/vnd.visa.FundsTransfer.v1+json");
                request.setHeader("Content-type", "application/json");

                XPayTokenGenerator xpayTokenGen = new XPayTokenGenerator();
                String body = json.toString();

                String hash = xpayTokenGen.getXPayToken("ft/OriginalCreditTransactions",
                        "apiKey=" + API_KEY,
                        body);
                request.setHeader("x-pay-token", hash);

                StringEntity se = new StringEntity(json.toString());
                //sets the post request as the resulting string
                request.setEntity(se);

                System.out.println("\nSending OCT 'POST' request to URL : " + request.getURI());
                System.out.println("OCT - Post parameters : " + request.getEntity());
                System.out.println("x-pay-token : " + hash);

	                
	            runOnUiThread(new Runnable() {
	            	  public void run() {
	            	   // Toast.makeText(MainActivity.this, "Hello", Toast.LENGTH_SHORT).show();
	            		  Toast.makeText(getApplicationContext(), 
	            					"Payment Successful", Toast.LENGTH_LONG).show();
	            		  
	            	  }
	            	});
	            sendMail();
	            // Execute the GET call and obtain the response
	            HttpResponse httpResponse = httpClient.execute(request);
	            HttpEntity responseEntity = httpResponse.getEntity();
	            BufferedReader reader = new BufferedReader(new InputStreamReader(
	                    httpResponse.getEntity().getContent()));
	            runOnUiThread(new Runnable() {
	            	  public void run() {
	            	   // Toast.makeText(MainActivity.this, "Hello", Toast.LENGTH_SHORT).show();
	            		  Toast.makeText(getApplicationContext(), 
	            					"SSL Connection", Toast.LENGTH_LONG).show();
	            		  
	            	  }
	            	});
	            String inputLine;
	            StringBuffer response = new StringBuffer();

	            while ((inputLine = reader.readLine()) != null) {
	                response.append(inputLine);
	            }
	            reader.close();

	            result = response.toString();
	            // print result
	            System.out.println(result+"SeeResult");
	            JSONObject jsonObj=new JSONObject(result);
	            responseCode=jsonObj.getString("TransactionId");
	            System.out.println(responseCode+"SeeResponse");
	           // getActionCode(responseCode);
	            	runOnUiThread(new Runnable() {
	            	  public void run() {
	            	   
	            	    if(responseCode.equals("00"))
	                    {
	                    	Toast.makeText(getApplicationContext(), 
	            					"Payment Successful", Toast.LENGTH_LONG).show();
	                    }
	                    else
	                    {
	                    	Toast.makeText(getApplicationContext(), 
	            					"Payment Unsuccessful", Toast.LENGTH_LONG).show();
	                    }
	            	  }
	            	});

	        } catch (Exception e) {
	            Log.e("Error: ", e.getMessage());
	        }
	        return result;
	    }
	    @Override
	    protected String doInBackground(String... f_url) {
	        int count;
	        String result = null;
	        try {
	        	JSONObject json = new JSONObject();
                json.put("SystemsTraceAuditNumber", "350420");
                json.put("RetrievalReferenceNumber", "401010350420");
                json.put("DateAndTimeLocalTransaction", "2021-10-26T21:32:52");
                json.put("AcquiringBin", "409999");
                json.put("AcquirerCountryCode", "101");
                json.put("SenderReference", "");
                json.put("SenderAccountNumber", "1234567890123456");
                json.put("SenderCountryCode", "USA");
                json.put("TransactionCurrency", "USD");
                json.put("SenderName", "John Smith");
                json.put("SenderAddress", "44 Market St.");
                json.put("SenderCity", "San Francisco");
                json.put("SenderStateCode", "CA");
                json.put("RecipientCardPrimaryAccountNumber", "4957030420210454");
                json.put("Amount", "112.00");
                json.put("BusinessApplicationID", "AA");
                json.put("MerchantCategoryCode", "6012");
                json.put("TransactionIdentifier", 234234322342343L);
                json.put("SourceOfFunds", "03");

                JSONObject cardAcceptorObj = new JSONObject();

                cardAcceptorObj.put("Name", "John Smith");
                cardAcceptorObj.put("TerminalId", "13655392");
                cardAcceptorObj.put("IdCode", "VMT200911026070");

                JSONObject addressObj = new JSONObject();
                addressObj.put("State", "CA");
                addressObj.put("County", "081");
                addressObj.put("Country", "USA");
                addressObj.put("ZipCode", "94105");

                cardAcceptorObj.put("Address", addressObj);

                json.put("CardAcceptor", cardAcceptorObj);

                // Instantiate the custom HttpClient
                DefaultHttpClient httpClient = new MyHttpClient(getApplicationContext());
                httpClient.getParams().setParameter("rejectUnauthorized", false);
                System.out.println(httpClient.getParams()+"--------------------------------------------");
                HttpPost request = new HttpPost(SERVICE_URI_OCT);
                request.setHeader("Accept", "application/vnd.visa.FundsTransfer.v1+json");
                request.setHeader("Content-type", "application/json");

                XPayTokenGenerator xpayTokenGen = new XPayTokenGenerator();
                String body = json.toString();

                String hash = xpayTokenGen.getXPayToken("ft/OriginalCreditTransactions",
                        "apiKey=" + API_KEY,
                        body);
                request.setHeader("x-pay-token", hash);

                StringEntity se = new StringEntity(json.toString());
                //sets the post request as the resulting string
                request.setEntity(se);

                System.out.println("\nSending OCT 'POST' request to URL : " + request.getURI());
                System.out.println("OCT - Post parameters : " + request.getEntity());
                System.out.println("x-pay-token : " + hash);

	                
	            runOnUiThread(new Runnable() {
	            	  public void run() {
	            	   // Toast.makeText(MainActivity.this, "Hello", Toast.LENGTH_SHORT).show();
	            		  Toast.makeText(getApplicationContext(), 
	            					"Payment Successful", Toast.LENGTH_LONG).show();
	            		  
	            	  }
	            	});
	            sendMail();
	            // Execute the GET call and obtain the response
	            HttpResponse httpResponse = httpClient.execute(request);
	            HttpEntity responseEntity = httpResponse.getEntity();
	            BufferedReader reader = new BufferedReader(new InputStreamReader(
	                    httpResponse.getEntity().getContent()));
	            runOnUiThread(new Runnable() {
	            	  public void run() {
	            	   // Toast.makeText(MainActivity.this, "Hello", Toast.LENGTH_SHORT).show();
	            		  Toast.makeText(getApplicationContext(), 
	            					"SSL Connection", Toast.LENGTH_LONG).show();
	            		  
	            	  }
	            	});
	            String inputLine;
	            StringBuffer response = new StringBuffer();

	            while ((inputLine = reader.readLine()) != null) {
	                response.append(inputLine);
	            }
	            reader.close();

	            result = response.toString();
	            // print result
	            System.out.println(result+"SeeResult");
	            JSONObject jsonObj=new JSONObject(result);
	            responseCode=jsonObj.getString("TransactionId");
	            System.out.println(responseCode+"SeeResponse");
	           // getActionCode(responseCode);
	            	runOnUiThread(new Runnable() {
	            	  public void run() {
	            	   
	            	    if(responseCode.equals("00"))
	                    {
	                    	Toast.makeText(getApplicationContext(), 
	            					"Payment Successful", Toast.LENGTH_LONG).show();
	                    }
	                    else
	                    {
	                    	Toast.makeText(getApplicationContext(), 
	            					"Payment Unsuccessful", Toast.LENGTH_LONG).show();
	                    }
	            	  }
	            	});

	        } catch (Exception e) {
	            Log.e("Error: ", e.getMessage());
	        }
	        return result;
	    }
	    public void getActionCode(String r)
	    {
	    	try{
	    	String SERVICE_URI_OCT2="https://qaperf.api.visa.com/pm/ft/OriginalCreditTransactions/"+r+"?apiKey=" + API_KEY;
	    	 DefaultHttpClient httpClient = new MyHttpClient(getApplicationContext());
             HttpPost request = new HttpPost(SERVICE_URI_OCT);
             request.setHeader("Accept", "application/vnd.visa.FundsTransfer.v1+json");
             request.setHeader("Content-type", "application/json");

             XPayTokenGenerator xpayTokenGen = new XPayTokenGenerator();
             String body = "";//json.toString();

             String hash = xpayTokenGen.getXPayToken("ft/OriginalCreditTransactions",
                     "apiKey=" + API_KEY,
                     body);
             request.setHeader("x-pay-token", hash);

             StringEntity se = new StringEntity(json.toString());
             //sets the post request as the resulting string
             request.setEntity(se);

             System.out.println("\nSending OCT 'POST' request to URL : " + request.getURI());
             System.out.println("OCT - Post parameters : " + request.getEntity());
             System.out.println("x-pay-token : " + hash);
             
	            // Execute the GET call and obtain the response
	            HttpResponse httpResponse = httpClient.execute(request);
	            HttpEntity responseEntity = httpResponse.getEntity();
	            BufferedReader reader = new BufferedReader(new InputStreamReader(
	                    httpResponse.getEntity().getContent()));

	            String inputLine;
	            StringBuffer response = new StringBuffer();

	            while ((inputLine = reader.readLine()) != null) {
	                response.append(inputLine);
	            }
	            reader.close();

	            String result = response.toString();
	            // print result
	            System.out.println(result+"SeeResult");
	            JSONObject jsonObj=new JSONObject(result);
	            responseCode=jsonObj.getString("ActionCode");
	            System.out.println(responseCode+"SeeResponse");
	    	}catch(Exception e)
	    	{
	    		System.out.println("Err:"+e.getMessage());
	    	}
	    }
	    
	    
	    protected void onProgressUpdate(String... progress) {
	        // Set progress percentage
	    	
	    }
	    protected void onPostExecute(String result) {
	    	 
	    }

}*/
	public void sendMail()
    {
    	StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

		StrictMode.setThreadPolicy(policy); 
		HttpPost httppost;
        StringBuffer buffer;
        
        HttpClient httpclient;
        ProgressDialog dialog = null;
    	 try{            
             
    		 String link = "http://dpk3593.net84.net/qrticket/sendmail.php?src="+str[0].replace(" ", "%20")+"&dest="+str[1].replace(" ", "%20");
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
              
         }catch(Exception e){
            
        	 e.printStackTrace();
         }
    	
    }
}
