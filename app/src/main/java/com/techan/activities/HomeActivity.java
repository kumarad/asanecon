package com.techan.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.crashlytics.android.Crashlytics;
import com.squareup.otto.Subscribe;
import com.techan.R;
import com.techan.activities.dialogs.AddPortfolio;
import com.techan.activities.drawer.DrawerHeaderItem;
import com.techan.activities.drawer.DrawerMenuAddItem;
import com.techan.activities.drawer.DrawerMenuGoldItem;
import com.techan.activities.drawer.DrawerMenuItem;
import com.techan.activities.drawer.DrawerMenuListAdapter;
import com.techan.activities.drawer.DrawerSubMenuItem;
import com.techan.activities.drawer.IDrawerActivity;
import com.techan.activities.drawer.IDrawerMenuItem;
import com.techan.activities.fragments.GoldFragment;
import com.techan.activities.fragments.StockListFragment;
import com.techan.profile.Portfolio;
import com.techan.profile.ProfileManager;

import io.fabric.sdk.android.Fabric;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity implements IDrawerActivity {
    public static String PORTFOLIO = "PORTFOLIO";
    public static String APP_START_UP = "APP_START_UP";
    public static String ALL_STOCKS = "All stocks";
    public static String ADD_PORTFOLIO = "Portfolios";
    public static String GOLD = "Gold";
    private DrawerLayout drawerLayout;
    private ListView drawerListView;
    private ActionBarDrawerToggle drawerToggle;
    private boolean isDrawerOpen = false;
    private Toolbar actionBar;
    private boolean resetDrawer = false;
    private String actionBarTitle = null;
    private List<IDrawerMenuItem> menuItems = new ArrayList<>();

    private boolean drawerSetup = false;
    private int currentPosition = 0;
    private final static String CURRENT_POSITION = "CURRENT_POSITION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        setContentView(R.layout.stock_home);


        actionBar = (Toolbar)findViewById(R.id.asaneconToolbar);
        setSupportActionBar(actionBar);

        if(savedInstanceState != null) {
            currentPosition = savedInstanceState.getInt(CURRENT_POSITION);
        } else {
            // The asanecon banner is at position 0.
            currentPosition = 1;
        }

        loadDrawerItems();
        setupDrawer();
        displayFragment(currentPosition, true);
        drawerSetup = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        BusService.getInstance().register(this);

        if(!drawerSetup) {
            loadDrawerItems();
            setupDrawer();
        } else {
            drawerSetup = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        BusService.getInstance().unregister(this);
    }

    @Subscribe
    public void refreshComplete(StockListFragment.RefreshCompleteEvent event) {
        loadDrawerItems();
    }

    private void loadDrawerItems() {
        menuItems.clear();
        menuItems.add(new DrawerHeaderItem());
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

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, actionBar, R.string.blank, R.string.blank) {
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
        currentPosition = position;
        if(menuItems.get(position).getText() == null) {
            return;
        }

        if(menuItems.get(position).getText().equals(ADD_PORTFOLIO)) {
            AddPortfolio.create(this);
        } else {
            Fragment fragment;
            if (menuItems.get(position).getText().equals(GOLD)) {
                fragment = new GoldFragment();
                getSupportActionBar().setTitle(" " + GOLD);
                actionBarTitle = GOLD;
            } else {
                StockListFragment stockListFragment = new StockListFragment();
                stockListFragment.setParentActivity(this);
                String portfolioName = menuItems.get(position).getText();

                getSupportActionBar().setTitle(" " + portfolioName);
                actionBarTitle = portfolioName;

                Bundle bundle = new Bundle();
                bundle.putString(PORTFOLIO, portfolioName);
                bundle.putBoolean(APP_START_UP, appStartup);
                stockListFragment.setArguments(bundle);

                fragment = stockListFragment;
            }

            FragmentManager fragmentManager = getFragmentManager();
            android.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.homeFrameContainer, fragment);
            //transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
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


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_POSITION, currentPosition);
    }
}
