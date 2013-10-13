package com.example.tech_reader;


/**
 * ArticleExpanded - a data structure used to organize and pass a collection of objects 
 * representing the body for an article within an expandable list view.
 * 
 * @author Chris McCarty
 */
public class ArticleExpanded
{
	private String 	summary_ = "",
					date_ = "",
					source_ = "",
					URL_ = "";
	
	public String getSummary(){
		return summary_;
	}
	public void setSummary(String Name){
		this.summary_ = Name;
	}

	public String getDate(){
		return date_;
	}
	public void setDate(String date){
		this.date_ = date;
	}
	
	public String getSource(){
		return source_;
	}
	public void setSource(String src){
		this.source_ = src;
	}
	
	public String getURL(){
		return URL_;
	}
	public void setURL(String url){
		this.URL_ = url;
	}
}

