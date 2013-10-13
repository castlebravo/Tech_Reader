package com.example.tech_reader;


/**
 * ArticleCollapsed - a data structure used to organize and pass a collection of objects 
 * representing the header for an article within an expandable list view.
 * 
 * @author Chris McCarty
 */
public class ArticleCollapsed
{
	private String 			title_ = "";
	private ArticleExpanded expanded_;
	private boolean 		hidden_ = false;
	
	public String getTitle(){
		return title_;
	}
	public void setTitle(String name){
		title_ = name;
	}
	
	public ArticleExpanded getExpandedArticle(){
		return expanded_;
	}
	public void setExpandedArticle(ArticleExpanded ae){
		expanded_ = ae;
	}
	
	public boolean isHidden(){
		return hidden_;
	}
	public void setHidden(boolean b){
		hidden_ = b;
	}
}
