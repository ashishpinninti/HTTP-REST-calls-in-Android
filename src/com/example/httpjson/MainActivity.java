package com.example.httpjson;

import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.httpjson.AppEngineClient.Request;

import java.net.HttpURLConnection;
import java.nio.charset.Charset;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

public class MainActivity extends Activity {

	private String file = "myjsonfile";
	
	
	
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

	}
	
	
	
	public static final String PREFS_NAME = "MyPrefsFile";
	
	public void saveToInternalFile(String file,String body){
    	
	       
		FileOutputStream fOut = null;
		try {
			fOut = openFileOutput(file, Context.MODE_PRIVATE);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			fOut.write(("Hello SPRINT :)  " + body).getBytes());

			fOut.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	       
	    }
	
	public String getTokenandSave()
	{
		try {
					AppEngineClient aec1 = new AppEngineClient();
					
					String s = aec1.getTokenFromServer();
					boolean b = saveTokenPref(s);
					
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return getTokenFromLocal();
	}
	
    public boolean saveTokenPref(String value){
    	
       
       SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
       SharedPreferences.Editor editor = settings.edit();
       editor.putString("token", value);
       Toast.makeText(getBaseContext(), "Token saved", Toast.LENGTH_SHORT)
		.show();
       return editor.commit();
       
    }
    
    public String getTokenFromLocal()
    {
    	String s = null;
    	
    	SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        s = settings.getString("token", "NO LOCAL TOKEN");
    	
    	return s;
    }
    
    public boolean isTokenPresent()
	{
		boolean flag = false;
		
		flag = !(getTokenFromLocal().equals("NO LOCAL TOKEN"));
		
		
		return flag;
	}

    public void retryWithNewToken() {

    	try {

			URL uri = new URL("https://rtonames.sprintlabs.io/beacons");

			Map<String, List<String>> headers = new HashMap<String, List<String>>();

			String key = "X-Auth-Token";
//			List<String> value = Arrays
//					.asList("eyJhbGciOiJIUzI1NiIsInR5cGUiOiJKV1QifQ.eyJpc3MiOiJ3aG9kYXQiLCJpYXQiOjE0MDI1MTQ2NjUsInN1YiI6IjYyNjM5OTdhLTljOGUtNDM2MC1iYmRmLTI4MGUyOTVmNjY1MSIsImV4cCI6MTQwMjUxODI2NSwiYXVkIjpbIndob2RhdCJdfQ.ho65rIYaJLbucXq5D12UxvgCJQUtPo7lk2UOvVrqVqE");
			String token = null;
			
			
				token = getTokenandSave();
				while(getTokenFromLocal().equals("NO LOCAL TOKEN"))
				{
					
				}
				
				Toast.makeText(getBaseContext(), "RETRY LOGIN", Toast.LENGTH_SHORT)
				.show();
				
				token = getTokenFromLocal();
			
				
			List<String> value = Arrays
					.asList(token);

			headers.put(key, value);
			
			
			AppEngineClient aec1 = new AppEngineClient();
			Request request = aec1.new GET(uri, headers);

			AsyncTask<Request, Void, Response> obj = new anotherthread().execute(request);


		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }

    // Gets the taken using get request
    
	public void savetofile(View v) {
		try {

			URL uri = new URL("https://rtonames.sprintlabs.io/beacons");

			Map<String, List<String>> headers = new HashMap<String, List<String>>();

			String key = "X-Auth-Token";
//			List<String> value = Arrays
//					.asList("eyJhbGciOiJIUzI1NiIsInR5cGUiOiJKV1QifQ.eyJpc3MiOiJ3aG9kYXQiLCJpYXQiOjE0MDI1MTQ2NjUsInN1YiI6IjYyNjM5OTdhLTljOGUtNDM2MC1iYmRmLTI4MGUyOTVmNjY1MSIsImV4cCI6MTQwMjUxODI2NSwiYXVkIjpbIndob2RhdCJdfQ.ho65rIYaJLbucXq5D12UxvgCJQUtPo7lk2UOvVrqVqE");
			String token = null;
			if(isTokenPresent()){
				token = getTokenFromLocal();
				Toast.makeText(getBaseContext(), "Using LOCAL", Toast.LENGTH_SHORT)
				.show();
			}
			else
			{
				token = getTokenandSave();
				while(getTokenFromLocal().equals("NO LOCAL TOKEN"))
				{
					
				}
				
				Toast.makeText(getBaseContext(), "FIRST TIME LOGIN", Toast.LENGTH_SHORT)
				.show();
				
				token = getTokenFromLocal();
			}
				
			List<String> value = Arrays
					.asList(token);

			headers.put(key, value);
			
			
			AppEngineClient aec1 = new AppEngineClient();
			//Request request =  aec1.get(uri, headers);
			Request request = aec1.new GET(uri, headers);

			// Networking operations should be done using background threads (One way is Async task)
			
			AsyncTask<Request, Void, Response> obj = new anotherthread().execute(request);


		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void anotherThreadCallback() {
		Log.d("TESTING","Calling anotherThreadCallback");
		Toast.makeText(getBaseContext(), "file saved", Toast.LENGTH_SHORT)
				.show();
	}

	class anotherthread extends AsyncTask<Request, Void, Response> {

		private Exception exception;

		protected Response doInBackground(Request... requests) {
			try {

				Response response = AppEngineClient.getOrPost(requests[0]);

				return response;
			} catch (Exception e) {
				this.exception = e;
				return null;
			}
		}

		protected void onPostExecute(Response response) {
			// TODO: check this.exception
			// TODO: do something with the feed
			
			// 
			
			if(response.status == 401)
			{
				Toast.makeText(getBaseContext(), "401 error! generating new token....", Toast.LENGTH_SHORT)
				.show();
				retryWithNewToken();
				return;
			}
			
//			if(response == null)
//			{
//				Toast.makeText(getBaseContext(), "401 error! generating new token....", Toast.LENGTH_SHORT)
//				.show();
//				retryWithNewToken();
//				return;
//			}
			

			String body = null;
			try {
				body = new String(response.body, "UTF-8");
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			JSONObject tokenjson = AppEngineClient.getJSONObject(body);
			
			boolean isnull = (tokenjson == null);
			
			if(!isnull && tokenjson.has("token"))
			{
				try {
					String s = tokenjson.getString("token");
					boolean b = saveTokenPref(s);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return;
			}
			

			// String body = response.body.toString();
			
			
			
			saveToInternalFile(file,body);

			
			
			anotherThreadCallback();

		}
	}
	
	

	public String readfromfile(View v) {
		String temp = "";
		try {
			FileInputStream fin = openFileInput(file);
			int c;

			while ((c = fin.read()) != -1) {
				temp = temp + Character.toString((char) c);
			}
			Toast.makeText(getBaseContext(), "file read" + temp,
					Toast.LENGTH_SHORT).show();

			return temp;

		} catch (Exception e) {

		}

		return temp;
	}

	public void executeGet(View v) {
		try {
			Toast.makeText(getBaseContext(), "GET command ", Toast.LENGTH_SHORT)
					.show();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	// Posts the json data 
	
	public void executePost(View v) {
		try {
			
			
			URL uri = new URL("https://rtonames.sprintlabs.io/beacons/1/endpoints");

			Map<String, List<String>> headers = new HashMap<String, List<String>>();
			
			String jsonstring = "{ \"id\": \"9\",\"service\": \"security\",\"uri\": \"http://example.com/test\"}";
			
			
			
			//byte[] body = jsonstring.getBytes(Charset.forName("UTF-8"));
			byte[] body = jsonstring.getBytes();

			String key = "X-Auth-Token";
			List<String> value = Arrays
					.asList("eyJhbGciOiJIUzI1NiIsInR5cGUiOiJKV1QifQ.eyJpc3MiOiJ3aG9kYXQiLCJpYXQiOjE0MDI0MDg3NTgsInN1YiI6IjYyNjM5OTdhLTljOGUtNDM2MC1iYmRmLTI4MGUyOTVmNjY1MSIsImV4cCI6MTQwMjQxMjM1OCwiYXVkIjpbIndob2RhdCJdfQ.ITh0RxuueEJxi4gHqvpUXKhEpSd4l6aXzjMg7GK7Vv0");

			headers.put(key, value);
			
			
			AppEngineClient aec1 = new AppEngineClient();
			Request request = aec1.new POST(uri, headers,body);

			new anotherthread().execute(request);
			
			
			
			
			
			Toast.makeText(getBaseContext(), "POST command ",
					Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void executePut(View v) {
		
		// Please make use of put method in AppEngineClient directly. Or
		// Make Request object (PUT object) and changes put method to take request object as parameter.
		
		try {
			Toast.makeText(getBaseContext(), "PUT command ", Toast.LENGTH_SHORT)
					.show();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void executeDelete(View v) {
		
		// Please make use of delete method in AppEngineClient directly. Or
				// Make Request object (DELETE object) and changes delete method to take request object as parameter.
		try {
			Toast.makeText(getBaseContext(), "DELETE command ",
					Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
}
