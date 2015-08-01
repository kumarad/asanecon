package com.techan.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.crashlytics.android.Crashlytics;
import com.techan.R;
import com.techan.activities.dialogs.AddPortfolio;
import com.techan.activities.drawer.DrawerMenuAddItem;
import com.techan.activities.drawer.DrawerMenuGoldItem;
import com.techan.activities.drawer.DrawerMenuItem;
import com.techan.activities.drawer.DrawerMenuListAdapter;
import com.techan.activities.drawer.DrawerSubMenuItem;
import com.techan.activities.drawer.IDrawerActivity;
import com.techan.activities.drawer.IDrawerMenuItem;
import com.techan.activities.fragments.StockListFragment;
import com.techan.profile.Portfolio;
import com.techan.profile.ProfileManager;

import io.fabric.sdk.android.Fabric;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeActivity extends Activity implements IDrawerActivity {
    public static String PORTFOLIO = "PORTFOLIO";
    public static String APP_START_UP = "APP_START_UP";
    public static String ALL_STOCKS = "All stocks";
    public static String ADD_PORTFOLIO = "Add portfolio";
    private DrawerLayout drawerLayout;
    private ListView drawerListView;
    private ActionBarDrawerToggle drawerToggle;
    private boolean isDrawerOpen = false;
    private ActionBar actionBar;
    private boolean resetDrawer = false;
    private String actionBarTitle = null;
    private List<IDrawerMenuItem> menuItems = new ArrayList<>();

    private boolean drawerSetup = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        setContentView(R.layout.stock_home);

        actionBar = getActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        loadDrawerItems();
        setupDrawer();
        displayFragment(0, true);
        drawerSetup = true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!drawerSetup) {
            loadDrawerItems();
            setupDrawer();
            displayFragment(0, true);
        } else {
            drawerSetup = false;
        }
    }

    private void loadDrawerItems() {
        menuItems.clear();
        menuItems.add(new DrawerMenuItem(ALL_STOCKS));

        if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean(SettingsActivity.ENABLE_GOLD_TRACKER, false)) {
            menuItems.add(new DrawerMenuGoldItem());
        }

        menuItems.add(new DrawerMenuAddItem(ADD_PORTFOLIO));
        Map<String, Portfolio> portfolios = ProfileManager.getPortfolios(this);
        for(String curPortfolioName : portfolios.keySet()) {
            menuItems.add(new DrawerSubMenuItem(curPortfolioName));
        }
    }

    private void setupDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.homeDrawer);

        drawerListView = (ListView) findViewById(R.id.homeLeftDrawerList);
        final DrawerMenuListAdapter adapter = new DrawerMenuListAdapter(this, menuItems);
        drawerListView.setAdapter(adapter);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_drawer, R.string.blank, R.string.blank) {
            @Override
            public void onDrawerStateChanged(int newState) {
                if(newState == DrawerLayout.STATE_SETTLING) {
                    if(!drawerLayout.isDrawerOpen(Gravity.LEFT)) {
                        if(resetDrawer) {
                            resetDrawer = false;
                            loadDrawerItems();
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if(slideOffset > .55 && !isDrawerOpen) {
                    onDrawerOpened(drawerView);
                    isDrawerOpen = true;
                    invalidateOptionsMenu();

                    actionBarTitle = actionBar.getTitle().toString();
                    actionBar.setTitle(null);
                } else if(slideOffset < .45 && isDrawerOpen){
                    onDrawerClosed(drawerView);
                    isDrawerOpen = false;
                    invalidateOptionsMenu();
                    actionBar.setTitle(actionBarTitle);
                }
            }
        };

        drawerLayout.setDrawerListener(drawerToggle);

        drawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                displayFragment(position, false);
            }
        });
    }

    private void displayFragment(int position, boolean appStartup) {
        if(menuItems.get(position).getText() == null) {
            return;
        }
        
        if(menuItems.get(position).getText().equals(ADD_PORTFOLIO)) {
            AddPortfolio.create(this);
        } else {
            StockListFragment fragment = new StockListFragment();
            fragment.setParentActivity(this);
            String portfolioName = menuItems.get(position).getText();

            actionBar.setTitle(portfolioName);
            actionBarTitle = portfolioName;

            Bundle bundle = new Bundle();
            bundle.putString(PORTFOLIO, portfolioName);
            bundle.putBoolean(APP_START_UP, appStartup);
            fragment.setArguments(bundle);

            FragmentManager fragmentManager = getFragmentManager();
            android.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.homeFrameContainer, fragment);
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            transaction.commit();
        }

        drawerLayout.closeDrawer(drawerListView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        if(isDrawerOpen) {
            menu.clear();
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggle
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public void resetDrawer() {
        resetDrawer = true;
    }
}
