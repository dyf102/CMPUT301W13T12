package ca.ualberta.c301w13t12recipes.view;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import ca.ualberta.c301w13t12recipes.R;
import ca.ualberta.c301w13t12recipes.controller.DatabaseController;
import ca.ualberta.c301w13t12recipes.controller.RecipeAdapter;
import ca.ualberta.c301w13t12recipes.controller.WebSearch;
import ca.ualberta.c301w13t12recipes.model.Recipe;

/**
 * Users are able to search recipes both online and offline
 * 
 * @author YWUEI DUAN
 * 
 */
public class SearchActivity extends Activity {
	// declare the view components
	private MultiAutoCompleteTextView keyword_edittext;
	private ImageButton search_imagebutton;
	private ListView result_listview;
	private String keyword;
	private ImageButton optionMenu;
	private PopupMenu popupMenu;
	// searchModeSwitch = 0 local search
	private int searchModeSwitch;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		// initial the view components
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
		// set thread policy
		setUp();
		prepareAutoCompleteText();
		// testWeb();

		search_imagebutton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Log.v("button test", "" + searchModeSwitch);
				keyword = keyword_edittext.getText().toString();
				if (keyword.length() > 0) {
					if (searchModeSwitch == 1)
						refreshListview("web_" + keyword);
					else
						refreshListview(keyword);
					/*
					 * result_listview = (ListView)
					 * findViewById(R.id.searchResult_listView); RecipeAdapter
					 * adapter = new RecipeAdapter(); ListAdapter la =
					 * adapter.getAdapter(arg0.getContext(), keyword);
					 * result_listview.setAdapter(la);
					 */
				}
			}
		});
		result_listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				String key = keyword_edittext.getEditableText().toString();
				if (key.length() > 0) {
					jumpToAddViewDetailRecipeActivity(pos, key);
				} else
					jumpToAddViewDetailRecipeActivity(pos, "");
			}
		});

		optionMenu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				showPopup(v);
				popupMenu
						.setOnMenuItemClickListener(new OnMenuItemClickListener() {

							@Override
							public boolean onMenuItemClick(MenuItem item) {
								switch (item.getItemId()) {
								case R.id.pop_online_search_mode:
									searchModeSwitch = 1;
									return true;

								case R.id.pop_search_offline_mode:
									searchModeSwitch = 0;
									return true;
								default:
									return false;
								}

							}
						});

			}
		});

	}

	private void showPopup(View v) {
		popupMenu = new PopupMenu(this, v);
		MenuInflater inflater = popupMenu.getMenuInflater();
		inflater.inflate(R.menu.search_mode_switch, popupMenu.getMenu());
		popupMenu.show();
	}

	/**
	 * Set up the view
	 */
	public void setUp() {
		keyword_edittext = (MultiAutoCompleteTextView) findViewById(R.id.keyword_autoCompleteTextView1);
		result_listview = (ListView) findViewById(R.id.searchResult_listView);
		search_imagebutton = (ImageButton) findViewById(R.id.search_imageButton1);
		optionMenu = (ImageButton) findViewById(R.id.search_option_menu);
		searchModeSwitch = 0;
	}

	/**
	 * Jump to the class and send the recipe by intent
	 * 
	 * @param the
	 *            index from listview index
	 */
	private void jumpToAddViewDetailRecipeActivity(int index, String key) {
		Intent intent = new Intent(SearchActivity.this,
				ViewDetailedRecipeActivity.class);
		ArrayList<Recipe> recipeList = new ArrayList<Recipe>();
		if (searchModeSwitch == 1) {
			Recipe recipe = new Recipe();
			try {
				recipe = (new WebSearch()).searchRecipes(key, this).get(index);
			} catch (Exception e) {
				e.printStackTrace();
			}

			Bundle bundle = new Bundle();
			bundle.putSerializable("WEB_RECIPE", recipe);
			intent.putExtras(bundle);
			startActivity(intent);
			return;
		} else {
			recipeList = (new DatabaseController(this)).getDB()
					.searchRecipebyKeyword(key);
			;
		}
		Recipe recipe = recipeList.get(index);
		Bundle bundle = new Bundle();
		bundle.putSerializable("LOCAL_RECIPE", recipe);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	/**
	 * To prepare the multiCompleteText
	 */
	private void prepareAutoCompleteText() {
		List<String> autoCompleteList = (new DatabaseController(this))
				.getNameList();
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, autoCompleteList);
		keyword_edittext.setAdapter(adapter);
		keyword_edittext
				.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
	}

	private void refreshListview(String key) {
		result_listview = (ListView) findViewById(R.id.searchResult_listView);
		RecipeAdapter adapter = new RecipeAdapter();
		ListAdapter la = adapter.getAdapter(this, key, null);
		result_listview.setAdapter(la);
	}

}
