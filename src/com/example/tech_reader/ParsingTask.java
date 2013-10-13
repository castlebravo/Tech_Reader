package com.example.tech_reader;

import android.os.AsyncTask;


/**
 * ParsingTask - a class extending AsyncTask which takes a raw html page in the form 
 * of a string as a parameter, and then parses it for individual articles. These articles
 * are pushed to the UI thread and displayed incrementally as they are parsed through the
 * publishProgress() and onProgressUpdate() methods.
 * 
 * @author Chris McCarty
 */
public class ParsingTask extends AsyncTask<String, Article, Void>
{
	private int maxArticles_ = 100;
	String 		substring_ = "",
				source_ = "";
	boolean 	is_parsing = true;

	
	public ParsingTask(String s){
		source_ = s;
	}
	

	@Override
	protected Void doInBackground(String... params){	
		Article a;
		maxArticles_ = MainActivity.articlesToRetrieve_;
		// copies the raw html page
		substring_ = params[0];
		
		// loops n times where n is the number of articles to retrieve
		for(int i = 0; i < maxArticles_ && is_parsing; ++i){
			a = getNextArticle();
			// if an article was retrieved, push it to the UI thread 
			// and display it
			if(is_parsing)
				publishProgress(a);
			else
				break;
		}
		return null;
	}
	
	
	@Override
	protected void onProgressUpdate(Article... progress){
		for(int i = 0; i < progress.length; ++i){
			// use the expandable list adapter to display the article in the list
			MainActivity.adapter_.addArticle(progress[i]);
		}
    }
	
	// do nothing on post execute
	@Override
	protected void onPostExecute(Void v){}
	
	
	
	//HELPER FUNCTIONS========================================================
	// looks at the remaining html and pulls out the next article
	public Article getNextArticle()
	{
		Article a;
		String 	title_ = "Not connected to the internet.",
				summary_ = "Not connected to the internet.",
				date_ = "Not connected to the internet.",
				url_ = "Not connected to the internet.",
				url_substring = "";
		int 	i_start = 0,
				i_end = 0,
				url_start = 0,
				url_end = 0;
		
		// starts at the first index of an article
		i_start = substring_.indexOf("data-id=\"\">");
		
		// if the index is valid
		if(i_start != -1 && is_parsing == true){
			// pull out the url prior to the article index
			url_substring = substring_.substring(0, i_start);
			url_end = url_substring.lastIndexOf('"');
			url_start = url_substring.lastIndexOf("<a href=\"");
			// saves the url
			url_ = url_substring.substring(url_start + 9, url_end);
			
			// cut to the start of the title
			substring_ = substring_.substring(i_start + 11);
			
			// find the end of the title and copy it
			i_end = substring_.indexOf("</a></h1></header>");
			title_ = cleanString(substring_.substring(0, i_end));
			
			// cut to the end of the title
			substring_ = substring_.substring(i_end + 18);
			
			// find the start of the summary
			i_start = substring_.indexOf("<p class=\"first-text\">");
			
			// cut to the start of the summary
			substring_ = substring_.substring(i_start + 22);
			
			// find the end of the summary and copy it
			i_end = substring_.indexOf("<span class=\"read-more mbn hide-for-billboard\">");
			summary_ = cleanString(substring_.substring(0, i_end));
			
			// cut to the end of the summary
			substring_ = substring_.substring(i_end + 47);
			
			// find the start of the date
			i_start = substring_.indexOf("<span class=\"show-on-hover\">");
			
			// cut to the start of the date
			substring_ = substring_.substring(i_start + 28);
			
			// find the end of the date and copy it
			i_end = substring_.indexOf('<');
			date_ = substring_.substring(0, i_end);
			
			// cut to the end of the date
			substring_ = substring_.substring(i_end + 1);
		
			
			// replace html escape character sequences with individual characters
			title_ = title_.replace("&#39;", "'");
			summary_ = summary_.replace("&#39;", "'");
			
			title_ = title_.replace("&quot;", "\"");
			summary_ = summary_.replace("&quot;", "\"");
			
			title_ = title_.replace("&amp;", "&");
			summary_ = summary_.replace("&amp;", "&");
		}
		else
			is_parsing = false;
		
		a = new Article(title_, summary_, date_, source_, url_);
		return a;
	}
	
	
	// used to remove any html tags from strings
	private String cleanString(String text){
		String 	toReturn = text,
				before,
				after;
		
		// while the string still contains tags
		while(toReturn.contains("<")){
			int i_start = toReturn.indexOf("<"),
				i_end = toReturn.substring(i_start + 1).indexOf(">");
			
			before = toReturn.substring(0, i_start);
			after = toReturn.substring(i_start + i_end + 2, toReturn.length());
			toReturn = before + after;
		}
		// return the clean string
		return toReturn;
	}
}
