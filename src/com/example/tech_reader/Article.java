package com.example.tech_reader;


/**
 * Article - a data structure used to organize and pass a collection of strings 
 * representing a generic article.
 * 
 * @author Chris McCarty
 */
public class Article
{
	private String 	title_,
					summary_,
					date_,
					source_,
					URL_;
	
	public Article(String t, String s, String d, String src, String url){
		title_ = t;
		summary_ = s;
		date_ = d;
		source_ = src;
		URL_ = url;
	}
	
	public String getTitle(){
		return title_;
	}
	public String getSummary(){
		return summary_;
	}
	public String getDate(){
		return date_;
	}
	public String getSource(){
		return source_;
	}
	public String getURL(){
		return URL_;
	}
}
