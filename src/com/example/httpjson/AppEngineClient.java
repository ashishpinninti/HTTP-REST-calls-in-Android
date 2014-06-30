package com.example.httpjson;

/*
 * Copyright 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
//package com.textuality.aerc;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

//import com.example.httpjson.MainActivity.anotherthread;


import android.accounts.Account;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

/**
 * Performs GET and POST operations against a Google App Engine app, authenticating with
 *  a Google account on the device.
 */
public class AppEngineClient {

    //private Authenticator mAuthenticator;
    //private Context mContext;
    private static String mErrorMessage;



    /**
     * Performs an HTTP GET request.  The request is performed inline and this method must not
     *  be called from the UI thread.
     *  
     * @param uri The URI you're sending the GET to
     * @param headers Any extra HTTP headers you want to send; may be null
     * @return a Response structure containing the status, headers, and body. Returns null if the request 
     *   could not be launched due to an IO error or authentication failure; in which case use errorMessage()
     *   to retrieve diagnostic information.
     */
    public static Response get(URL uri, Map<String, List<String>> headers) {
    	AppEngineClient aec = new AppEngineClient();
        GET get = aec.new GET(uri, headers);
        return getOrPost(get);
    }

    /**       
     * Performs an HTTP POST request.  The request is performed inline and this method must not
     *  be called from the UI thread.
     *  
     * @param uri The URI you're sending the POST to
     * @param headers Any extra HTTP headers you want to send; may be null
     * @param body The request body to transmit
     * @return a Response structure containing the status, headers, and body. Returns null if the request 
     *   could not be launched due to an IO error or authentication failure; in which case use errorMessage()
     *   to retrieve diagnostic information.
     */
    public Response post(URL uri, Map<String, List<String>> headers, byte[] body) {
        POST post = new POST(uri, headers, body);
        return getOrPost(post);
    }
    
    
    
    public void put(URL uri, Map<String, List<String>> headers, byte[] body)
    {
    	PUT put = new PUT(uri, headers, body);
    	
    	URL url = null;
		try {
			url = new URL("http://www.example.com/resource");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	HttpURLConnection httpCon = null;
		try {
			httpCon = (HttpURLConnection) url.openConnection();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	try {
			httpCon.setDoOutput(true);
			httpCon.setRequestMethod("PUT");
			OutputStreamWriter out = new OutputStreamWriter(
			    httpCon.getOutputStream());
			out.write("Resource content from PUT!!! ");
			out.close();
			httpCon.getInputStream();
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void delete(URL uri, Map<String, List<String>> headers)
    {
    	
    	URL url = null;
    	try {
    	    url = new URL("http://localhost:8080/deleteservice");
    	} catch (MalformedURLException exception) {
    	    exception.printStackTrace();
    	}
    	HttpURLConnection httpURLConnection = null;
    	try {
    	    httpURLConnection = (HttpURLConnection) url.openConnection();
    	    httpURLConnection.setRequestProperty("Content-Type",
    	                "application/x-www-form-urlencoded");
    	    httpURLConnection.setRequestMethod("DELETE");
    	    System.out.println("DELETE response code..! "+httpURLConnection.getResponseCode());
    	} catch (IOException exception) {
    	    exception.printStackTrace();
    	} finally {         
    	    if (httpURLConnection != null) {
    	        httpURLConnection.disconnect();
    	    }
    	} 
    }
    
    

    /**
     * Provides an error message should a get() or post() return null.
     * @return the message
     */
    public String errorMessage() {
        return mErrorMessage;
    }

    public  static Response getOrPost(Request request) {
        mErrorMessage = null;
        HttpURLConnection conn = null;
        Response response = null;
        try {
            conn = (HttpURLConnection) request.uri.openConnection();
//            if (!mAuthenticator.authenticate(conn)) {
//                mErrorMessage = str(R.string.aerc_authentication_failed) + ": " + mAuthenticator.errorMessage();
//            } else 
            {
                if (request.headers != null) {
                    for (String header : request.headers.keySet()) {
                        for (String value : request.headers.get(header)) {
                            conn.addRequestProperty(header, value);
                        }
                    }
                }
                if (request instanceof POST) {
                    byte[] payload = ((POST) request).body; 
                    String s = new String(payload, "UTF-8");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.setRequestMethod("POST");
                    
                    JSONObject jsonobj = getJSONObject(s);
                    
                    conn.setRequestProperty("Content-Type", "application/json; charset=utf8");

                 // ...

                 OutputStream os = conn.getOutputStream();
                 os.write(jsonobj.toString().getBytes("UTF-8"));
                 os.close();
                    
                    
                    
                    
//                    conn.setFixedLengthStreamingMode(payload.length);
//                    conn.getOutputStream().write(payload);
                    int status = conn.getResponseCode();
                    if (status / 100 != 2)
                        response = new Response(status, new Hashtable<String, List<String>>(), conn.getResponseMessage().getBytes());
                }
                if (response == null) {
                	int a = conn.getResponseCode();
                	if(a == 401){
                		response = new Response(a, conn.getHeaderFields(), new byte[]{});
                	}
                	InputStream a1 = conn.getErrorStream();
                    BufferedInputStream in = new BufferedInputStream(conn.getInputStream());
                    
                    byte[] body = readStream(in);
                    
                    response = new Response(conn.getResponseCode(), conn.getHeaderFields(), body);
                 //   List<String> a = conn.getHeaderFields().get("aa");
                }
            }
        } catch (IOException e) {
            e.printStackTrace(System.err);
            mErrorMessage = ((request instanceof POST) ? "POST " : "GET ") +
                    str(R.string.aerc_failed) + ": " + e.getLocalizedMessage();
        } finally {
            if (conn != null)
                conn.disconnect();
        }
        return response;
    }

    // request structs
    public  class Request {
        public URL uri;
        public Map<String, List<String>> headers;
        public Request(URL uri, Map<String, List<String>> headers) {
            this.uri = uri; this.headers = headers;
        }
    }
    public class POST extends Request {
        public byte[] body;
        public POST(URL uri, Map<String, List<String>> headers, byte[] body) {
            super(uri, headers);
            this.body = body; 
        }
    }
    public class GET extends Request {
        public GET(URL uri, Map<String, List<String>> headers) {
            super(uri, headers);
        }
    }
    public class PUT extends Request {
        public byte[] body;
        public PUT(URL uri, Map<String, List<String>> headers, byte[] body) {
            super(uri, headers);
            this.body = body; 
        }
    }
    public class DELETE extends Request {
        public DELETE(URL uri, Map<String, List<String>> headers) {
            super(uri, headers);
        }
    }

    // utilities
    private static byte[] readStream(InputStream in) 
            throws IOException {
        byte[] buf = new byte[1024];
        int count = 0;
        ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
        while ((count = in.read(buf)) != -1) 
            out.write(buf, 0, count);
        return out.toByteArray();
    }

    private static String str(int id) {
        //return mContext.getString(id);
    	return "str method placeholder string!";
    }
    
    public static UUID getUUID()
    {        
       return UUID.randomUUID();
    }
    
    
    
    
    
    // generates JSON object from given string
    
    public static JSONObject getJSONObject(String s)
    {
    	
    	JSONObject jsonObj = null;
    	
    	//s = s.replace('"', '\"');
		try {
			jsonObj = new JSONObject(s);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return jsonObj;
    }
    
    
    
    
    public String getTokenFromServer()
	{
    	String s = null;
		try {

			URL uri = new URL("https://rtonames.sprintlabs.io/token");

			Map<String, List<String>> headers = new HashMap<String, List<String>>();

			String key = "Authorization";
			String authStr = "6eece178-9fcf-45b7-ab50-08b8066daabe:e494a4e72b6f7c7672b74d311cbabf2672be8c24c9496554077936097195a5ac69b6acd11eb09a1ae07a40d2e7f0348d";
			String encoding = Base64.encodeToString(authStr.getBytes(), Base64.DEFAULT);
			encoding = encoding.replace("\n", "").replace("\r", "");
			
			System.out.println(encoding);
			List<String> value = Arrays
					.asList("Basic "+encoding);

			headers.put(key, value);
			
			
			AppEngineClient aec1 = new AppEngineClient();
			Request request = aec1.new GET(uri, headers);

			AsyncTask<Request, Void, Response> obj = new asyncclass().execute(request);
			
			Response response = obj.get();
			
			obj.cancel(true);
			
			obj = null;
			
			
			String body = null;
			try {
				body = new String(response.body, "UTF-8");
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			JSONObject tokenjson = AppEngineClient.getJSONObject(body);
			
			if(tokenjson.has("token"))
			{
				try {
					s = tokenjson.getString("token");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			
			}
			

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return s;
	}
    

   
    
    class asyncclass extends AsyncTask<Request, Void, Response> {

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

			

		}
	}
    
    
}