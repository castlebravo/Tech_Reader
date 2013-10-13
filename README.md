Tech_Reader
===========

This application will pull between one and thirty articles from Gizmodo.com, Lifehacker.com, and Kotaku.com.
These sites can be toggled individually using the three checkboxes. Search filters can also be specified using the field 
at the top of the page, which will go into the search bar for each website and filter the articles retrieved. Once you 
have selected your parameters, you can retrieve your articles, which show up chronologically in an alternate page. The 
two pages can be swapped between at any time using the “Swap Page” button in the action bar. In viewing your articles, 
you can select any title to have the summary pop up below it. In doing do, two additional buttons will be displayed. 
These two buttons allow you to hide the selected article, and to view the full article in your default browser. Hidden 
articles can be reset by hitting the “Show Hidden Articles” button on the main page. The article list can be cleared at 
any time using the “Clear Article List” button on the same page.

This app was developed as a tool to aid in searching and viewing articles. The articles from all three sites are 
listed chronologically in one feed, allowing you to keep up with new articles as they’re published.

The app is one activity, with two types of AsyncTask. The first, NetworkTask, handles all the http requests and 
retrieves an entire raw html page. The second, ParsingTask, is used to process the retrieved raw html. Each time the task 
completes an article, it pushes it to the UI thread for display, dynamically updating the feed. These articles are 
displayed using a modified BaseExpandableListAdapter. The ArticleListAdapter class interacts with an ExpandableListView 
to provide the desired interface and display. In this custom view, each article is comprised of one group and one item. 
The group acts as the title, and once clicked, the item shows up and displays the body for that particular article.
