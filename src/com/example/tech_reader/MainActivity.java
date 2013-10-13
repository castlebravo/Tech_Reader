package com.example.tech_reader;

import com.example.tech_reader.R;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;


/**
 * MainActivity - main activity for the Tech Reader app.
 * 
 * @author Chris McCarty
 */
public class MainActivity extends Activity
{
	private RelativeLayout 		settingsPage_;
	private EditText 			filterField_;
	private Button 				clearButton_,
								showButton_,
								retrieveButton_;
	private CheckBox 			checkBox_giz_,
								checkBox_lh_,
								checkBox_k_;
	private TextView 			sliderLabel_;
	private SeekBar				slider_;

	
	private RelativeLayout 		articlePage_;
	private ExpandableListView 	expListView_;
	static  ArticleListAdapter 	adapter_;
	private Button 				hideButton_,
								gotoButton_;

	
	static Context 				context_;
	static int 					articlesToRetrieve_;

	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// initialize the settings page
		settingsPage_ 	= (RelativeLayout) findViewById(R.id.settings_page);
		retrieveButton_ = (Button) findViewById(R.id.btn_execute);
		clearButton_ 	= (Button) findViewById(R.id.clr_articles);
		showButton_ 	= (Button) findViewById(R.id.show_articles);
		filterField_ 	= (EditText) findViewById(R.id.filters);
		checkBox_giz_ 	= (CheckBox) findViewById(R.id.checkBox_giz);
		checkBox_lh_ 	= (CheckBox) findViewById(R.id.checkBox_lh);
		checkBox_k_ 	= (CheckBox) findViewById(R.id.checkBox_k);
		sliderLabel_ 	= (TextView) findViewById(R.id.slider_label);
		slider_ 		= (SeekBar) findViewById(R.id.slider);
	
		// initialize the article page
		articlePage_ 	= (RelativeLayout) findViewById(R.id.article_page);
		expListView_ 	= (ExpandableListView) findViewById(R.id.articleList);
		gotoButton_ 	= (Button) findViewById(R.id.btn_goto);
		hideButton_ 	= (Button) findViewById(R.id.btn_remove);
		adapter_ 		= new ArticleListAdapter();
		
		adapter_.setExpandableListView(expListView_);
		expListView_.setAdapter(adapter_);

		// set static helper variables
		context_ 			= getApplicationContext();
		articlesToRetrieve_ = 5;

		
		// sets the relevant slider listener
		slider_.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
		{
			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2){
				// updates the text label to reflect the position
				if(arg1 == 1)
					sliderLabel_.setText("Retrieve 1 article from each site");
				else
					sliderLabel_.setText("Retrieve " + Integer.toString(arg1) 
						+ " articles from the selected sites");
				// sets the static variable equal to the position
				articlesToRetrieve_ = arg1;
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar){}
			@Override
			public void onStopTrackingTouch(SeekBar seekBar){}			
		});
		
		
		// sets the relevant expandable list view listeners
		expListView_.setOnGroupExpandListener(new OnGroupExpandListener()
		{
			@Override
			public void onGroupExpand(int groupIndex){
				// selects the opened article
				adapter_.setSelectedArticle(groupIndex);	
				hideArticleButtons(false);
			}
        });
		expListView_.setOnGroupCollapseListener(new OnGroupCollapseListener()
		{
			@Override
			public void onGroupCollapse(int arg0){
				// deselects the closed article
				adapter_.deselectArticle();
				hideArticleButtons(true);
			}
        });
		
		
		// sets the clear article list button listener
		clearButton_.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View view){
				adapter_.clearArticleList();
	    		Toast.makeText(getApplicationContext(), "Article List Cleared", Toast.LENGTH_SHORT).show();
			}
		});
		
		
		// sets the show hidden articles button listener
		showButton_.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View view){
				adapter_.showHiddenArticles();
	    		Toast.makeText(getApplicationContext(), "Hidden Articles Shown", Toast.LENGTH_SHORT).show();
			}
		});
		
		
		// sets the hide article button listener
		hideButton_.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View view){
				// hides the selected article
				adapter_.hideSelectedArticle();
				// toast an empty article list message
				if(adapter_.getGroupCount() == 0)
	    			toastNoArticles();
			}
		});
		
		
		// sets the view full article button listener
		gotoButton_.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View view){
				// opens the selected article in the default browser
				startActivity(new Intent(Intent.ACTION_VIEW, 
						Uri.parse(adapter_.getSelectedURL())));
			}
		});

		
		// sets the retrieve button listener
		retrieveButton_.setOnClickListener(new OnClickListener()
		{
			@SuppressLint("NewApi")
			@Override
			public void onClick(View v){
				final NetworkTask networkTask = new NetworkTask();
				boolean atLeastOneChecked = false;
				
				// passes the user specified search filters
				String[] params = {filterField_.getText().toString(),"","",""};
				
				// determine the sites to pull articles from
				if(checkBox_giz_.isChecked()){
					params[1] = "GIZMODO";
					atLeastOneChecked = true;
				}
				if(checkBox_lh_.isChecked()){
					params[2] = "LIFEHACKER";
					atLeastOneChecked = true;
				}
				if(checkBox_k_.isChecked()){
					params[3] = "KOTAKU";
					atLeastOneChecked = true;
				}
				
				// as long as at least one site is checked
				if(atLeastOneChecked){
					// execute the network task
					if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
						networkTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
					else
						networkTask.execute(params);
					
					// swap to the article page
					hideSettingsPage(true);
					hideArticlePage(false);
				}else
					// toast an error message
					Toast.makeText(getApplicationContext(), "You must select at least one site", 
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	
	@SuppressLint("NewApi")
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(R.menu.main, menu);
		
		// hides the app name on the action bar
		getActionBar().setDisplayShowTitleEnabled(false);
		
		// configures the page swap button
		MenuItem btnItem = menu.findItem(R.id.swap_pages_btn);
		Button swap = (Button) MenuItemCompat.getActionView(btnItem);
		swap.setText(" Swap Pages");
		swap.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_refresh, 0, 0, 0);
		
		
		// sets the swap button listener
		swap.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View view){
				if(settingsPage_.getVisibility() == View.VISIBLE){
		    		hideSettingsPage(true);
		    		hideArticlePage(false);
		    		if(adapter_.getGroupCount() == 0)
		    			toastNoArticles();
		    	}else{
		    		hideArticlePage(true);
			    	hideSettingsPage(false);
		    	}
			}
		});
	    return super.onCreateOptionsMenu(menu);
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		// the settings option takes the user to the settings page
	    if(item.getItemId() == R.id.action_settings){
	    	hideArticlePage(true);
	    	hideSettingsPage(false);
	        return true;
	    }else
	    	return super.onOptionsItemSelected(item);
	}
	
	
	
	// HELPER FUNCTIONS=================================================
	
	// control visibility of the 2 article buttons on the article page
	private void hideArticleButtons(boolean hide){
		if(hide){
			hideButton_.setVisibility(View.GONE);
			gotoButton_.setVisibility(View.GONE);
		}else{
			hideButton_.setVisibility(View.VISIBLE);
			gotoButton_.setVisibility(View.VISIBLE);
		}
	}
	
	// control visibility of the entire settings page
	private void hideSettingsPage(boolean hide){
		if(hide)
			settingsPage_.setVisibility(View.GONE);
		else
			settingsPage_.setVisibility(View.VISIBLE);
	}
	
	// control visibility of the entire article page
	private void hideArticlePage(boolean hide){
		if(hide){
			adapter_.closeAllArticles();
			articlePage_.setVisibility(View.GONE);
		}else{
			articlePage_.setVisibility(View.VISIBLE);
			hideArticleButtons(true);
		}
	}
	
	// toasts an empty article list message
	private void toastNoArticles(){
		Toast.makeText(getApplicationContext(), "You currently have no articles to view! " +
			"Try swapping pages and hitting the button labeled \"Retrieve Articles\" or " +
			"\"Show Hidden Articles\".", Toast.LENGTH_LONG).show();
	}
}
