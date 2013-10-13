package com.example.tech_reader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;


/**
 * NetworkTask - a class extending AsyncTask which takes sites and search filters as 
 * parameters, and makes the relevant http get requests for retrieving articles. Raw
 * html pages are retrieved, and then passed as parameters to ParsingTasks to be post
 * processed and displayed. 
 * 
 * @author Chris McCarty
 */
public class NetworkTask extends AsyncTask<String, String, Void>
{
	String 				filters_;
	ArrayList<String> 	source_;
	int 				src_index_ = 0;
	int 				retrieve_index_ = 0;
	
	
	@Override
	protected void onPreExecute(){
		// toast a start message
		Toast.makeText(MainActivity.context_, "Retrieving Articles", Toast.LENGTH_LONG).show();
	}

	
	@Override
	protected Void doInBackground(String... params){
		source_ = new ArrayList<String>();
		// replace any spaces with + for searching
		filters_ = params[0].replace(' ', '+');
		
		// makes each server query according to the passed parameters
		for(int i = 1; i < params.length; ++i){
			if(!params[i].equals("")){
				// add the tag of the site to the list
				source_.add(params[i]);
				// do the network request and pass the resulting
				// raw html to the UI thread as a string
				publishProgress(getRawHTML());
			}
		}
		return null;
	}
	
	
	@Override
	protected void onProgressUpdate(String... progress){
		// instantiates a new ParsingTask in the UI thread
		ParsingTask htmlParser = new ParsingTask(source_.get(src_index_));
		src_index_++;
		// executes the new ParsingTask with the raw html as a parameter
		htmlParser.execute(progress);
    }
	
	// do nothing on post execute
	@Override
	protected void onPostExecute(Void v){}
	
	
	//HELPER FUNCTIONS====================================================
	// makes the http get request and returns a string representing the retrieved
	// html page
	private String getRawHTML(){
		String 			html = "NONE",
						baseURL = "";
		HttpGet 		request;
		HttpClient 		client = new DefaultHttpClient();
	    HttpResponse 	response;
	    StringBuilder 	builder;
	    BufferedReader 	inputreader;
	    
	    // set the base URL depending on the parameters
	    if(source_.get(retrieve_index_).equals("GIZMODO"))
	    	baseURL = "http://gizmodo.com/";
	    else if(source_.get(retrieve_index_).equals("LIFEHACKER"))
	    	baseURL = "http://lifehacker.com/";
	    else if(source_.get(retrieve_index_).equals("KOTAKU"))
	    	baseURL = "http://kotaku.com/";
	    
	    // if there are no filters, use the home page
	    if(filters_.equals(""))
	    	request = new HttpGet(baseURL);
	    // else, use the search page
	    else
	    	request = new HttpGet(baseURL + "search?q=" + filters_);
	    
		try {
			response = client.execute(request);
		    HttpEntity entity = response.getEntity();

		    if(entity != null){
		    	builder = new StringBuilder();
		        InputStream inputstream = entity.getContent();
		        inputreader = new BufferedReader(new InputStreamReader(inputstream));

		        // start retrieving and building the html string
		        String line = null;
		        try {
		            while((line = inputreader.readLine()) != null){
		                builder.append(line + "\n");
		            }
		        }catch(Exception e){
		        	Log.e("NetworkTaskException", "readLine(): " + e.toString());
		        }finally{
		            try {
		            	inputstream.close();
		            }catch(Exception e){
		            	Log.e("NetworkTaskException", "close(): " + e.toString());
		            }
		        }
		        html = builder.toString();
		    }
		}catch(Exception e){
			Log.e("NetworkTaskException", "http: " + e.toString());
		}
		retrieve_index_++;
		// return the raw html string
		return html;
	}
}