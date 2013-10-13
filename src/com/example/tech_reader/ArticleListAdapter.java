package com.example.tech_reader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import com.example.tech_reader.R;
import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;


/**
 * ArticleListAdapter - a class extending BaseExpandableListAdapter, designed to
 * display a dynamic list of articles. This class has many methods for interacting
 * with the list of articles at any given time, and is thread safe.
 * 
 * @author Chris McCarty
 */
public class ArticleListAdapter extends BaseExpandableListAdapter
{
	private ArrayList<ArticleCollapsed> articles_,
										hiddenArticles_;
	private ExpandableListView expListView_;
	private ArticleCollapsed selectedArticle = null;
	private SimpleDateFormat dateParser_;

	
	@SuppressLint("SimpleDateFormat")
	public ArticleListAdapter(){
		articles_ = new ArrayList<ArticleCollapsed>();
		hiddenArticles_ = new ArrayList<ArticleCollapsed>();
		dateParser_ = new SimpleDateFormat("MM/dd/yy hh:mma");
	}
	

	public void setExpandableListView(ExpandableListView elv){
		expListView_ = elv;
	}
	
	
	// adds an article to the list and inserts it chronologically
	public synchronized void addArticle(Article a){
		if(!containsArticle(a)){
			// formats the Article for the list
			ArticleExpanded t = new ArticleExpanded();
			t.setSummary(a.getSummary());
			t.setDate(a.getDate());
			t.setSource(a.getSource());
			t.setURL(a.getURL());
			
			ArticleCollapsed temp = new ArticleCollapsed();
			temp.setTitle(a.getTitle());
			temp.setExpandedArticle(t);
			
			articles_.add(findChronologicalIndex(a), temp);
			notifyDataSetChanged();
		}
	}
	
	
	// removes an article from the list
	public synchronized void removeArticle(String title){
		for(int i = 0; i < articles_.size(); ++i){
			if(articles_.get(i).getTitle().equals(title)){
				articles_.remove(i);
				notifyDataSetChanged();
				break;
			}
		}
	}
	
	
	// set the selected article
	public synchronized void setSelectedArticle(int article_index){
		if(articles_.indexOf(selectedArticle) != article_index){
			expListView_.collapseGroup(articles_.indexOf(selectedArticle));
			selectedArticle = articles_.get(article_index);
		}
	}
	
	
	// deselects the currently selected article
	public synchronized void deselectArticle(){
		selectedArticle = null;
	}
	
	
	// hides the currently selected article
	public synchronized void hideSelectedArticle(){
		if(selectedArticle != null){
			removeArticle(selectedArticle.getTitle());
			hiddenArticles_.add(selectedArticle);
			selectedArticle = null;
			closeAllArticles();
		}
	}
	
	
	// clears the entire article list
	public synchronized void clearArticleList(){
		articles_.clear();
		selectedArticle = null;
		notifyDataSetChanged();
	}
	
	
	// shows all the hidden articles
	public synchronized void showHiddenArticles(){
		ArrayList<ArticleCollapsed> t = new ArrayList<ArticleCollapsed>();
		
		// copies the hidden articles to a temporary array list
		for(int i = 0; i < hiddenArticles_.size(); ++i){
			t.add(hiddenArticles_.get(i));
		}
		
		hiddenArticles_.clear();
		Article a;
		
		// loops through the temporary array list and adds the previously hidden articles
		for(int i = 0; i < t.size(); ++i){
			a = new Article(t.get(i).getTitle(),t.get(i).getExpandedArticle().getSummary(), 
				t.get(i).getExpandedArticle().getDate(), t.get(i).getExpandedArticle().getSource(), 
				t.get(i).getExpandedArticle().getURL());
			addArticle(a);
		}
	}
	
	
	// gets the url of the currently selected article
	public synchronized String getSelectedURL(){
		if(selectedArticle != null)
			return selectedArticle.getExpandedArticle().getURL();
		else
			return "";		
	}
	
	
	// collapses any open articles
	public synchronized void closeAllArticles(){
		for(int i = 0; i < articles_.size(); ++i){
			expListView_.collapseGroup(i);
		}
	}
	
	
	//OVERRIDEN METHODS====================================================================
	
	// handles the binding of expanded article objects to view widgets
	@Override
	public synchronized View getChildView(int groupPosition, int childPosition, boolean isLastChild,
			View view, ViewGroup parent){
		
		// gets the expanded article object that is being referenced
		ArticleExpanded exp = (ArticleExpanded) getChild(groupPosition, childPosition);
		
		// inflates the relevant layout
		if (view == null){
			LayoutInflater infalInflater = (LayoutInflater) 
				MainActivity.context_.getSystemService(MainActivity.context_.LAYOUT_INFLATER_SERVICE);
			view = infalInflater.inflate(R.layout.article_expanded, null);
		}
		
		// sets the body text
		TextView tv = (TextView) view.findViewById(R.id.summary);		
		tv.setText(setExpandedViewText(exp));
		return view;
	}

	
	// handles binding of collapsed article object to view widgets
	@Override
	public synchronized View getGroupView(int groupPosition, boolean isLastChild, View view,
			ViewGroup parent){
		
		// gets the collapsed article object that is being referenced
		ArticleCollapsed col = (ArticleCollapsed) getGroup(groupPosition);
		
		// inflates the relevant layout
		if (view == null){
			LayoutInflater inf = (LayoutInflater) 
				MainActivity.context_.getSystemService(MainActivity.context_.LAYOUT_INFLATER_SERVICE);
			view = inf.inflate(R.layout.article_collapsed, null);
		}
		
		// sets the header text
		TextView tv = (TextView) view.findViewById(R.id.title);
		tv.setText(col.getTitle());
		return view;
	}
	
	
	@Override
	public synchronized Object getChild(int groupPosition, int childPosition){
		return articles_.get(groupPosition).getExpandedArticle();
	}
	@Override
	public synchronized long getChildId(int groupPosition, int childPosition){
		return childPosition;
	}
	@Override
	public synchronized int getChildrenCount(int groupPosition){
		return 1;
	}
	@Override
	public synchronized Object getGroup(int groupPosition){
		return articles_.get(groupPosition);
	}
	@Override
	public synchronized int getGroupCount(){
		return articles_.size();
	}
	@Override
	public synchronized long getGroupId(int groupPosition){
		return groupPosition;
	}
	@Override
	public synchronized boolean hasStableIds(){
		return true;
	}
	@Override
	public synchronized boolean isChildSelectable(int a0, int a1){
		return true;
	}
	
	
	//HELPER FUNCTIONS=========================================================
	
	// creates a formatted string to be displayed in the article body
	private synchronized String setExpandedViewText(ArticleExpanded a){
		String toReturn = a.getSummary() + "\n";
		
		if(!a.getDate().equals("")){
			toReturn += ("\nDate " + a.getDate());
		}
		if(!a.getSource().equals(""))
			toReturn += ("\nSource: " + a.getSource());
		
		return toReturn;
	}
	
	
	// checks to see if the article list already contains the article
	private synchronized boolean containsArticle(Article a){
		// checks all the hidden articles
		for(int i = 0; i < hiddenArticles_.size(); ++i){
			if(hiddenArticles_.get(i).getTitle().equals(a.getTitle()))
				return true;
		}
		// checks all the articles
		for(int i = 0; i < articles_.size(); ++i){
			if(articles_.get(i).getTitle().equals(a.getTitle()))
				return true;
		}
		return false;
	}
	
	
	// finds the chronological index for a new article
	private synchronized int findChronologicalIndex(Article a){	
		Date ArticleToAddDate;
		Date viewedArticleDate;
		
		try {
			ArticleToAddDate = dateParser_.parse(a.getDate());
			// loops through all the articles and parses their dates
			for(int i = 0; i < articles_.size(); ++i){		
				viewedArticleDate = dateParser_.parse(
						articles_.get(i).getExpandedArticle().getDate());
				
				// if an article is found after the new one, insert the new article here
				if(ArticleToAddDate.after(viewedArticleDate))
					return i;
			}
		}catch(ParseException e){
			Log.e("ArticleListAdapter", e.toString());
		}
		// if no index was found, insert at the end
		return articles_.size();
	}
}