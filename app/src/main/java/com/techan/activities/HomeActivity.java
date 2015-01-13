package com.techan.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.techan.R;
import com.techan.activities.dialogs.AddPortfolio;
import com.techan.activities.fragments.StockListFragment;
import com.techan.profile.ProfileManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class HomeActivity extends Activity {
    public static String PORTFOLIO = "PORTFOLIO";

    private DrawerLayout drawerLayout;
    private ListView drawerListView;
    private ActionBarDrawerToggle drawerToggle;
    private boolean isDrawerOpen = false;
    private List<String> portfolios = new ArrayList<>();
    private ActionBar actionBar;
    private boolean resetDrawer = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.stock_home);

        actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        loadDrawerItems();
        setupDrawer();
        displayFragment(0);
    }

    private void loadDrawerItems() {
        Set<String> portfolios = ProfileManager.getPortfolios(this);
        this.portfolios.clear();
        this.portfolios.add("All");
        this.portfolios.addAll(portfolios);
        this.portfolios.add("Add Portfolio");
    }

    private void setupDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.homeDrawer);

        drawerListView = (ListView) findViewById(R.id.homeLeftDrawerList);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, portfolios);
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
                } else if(slideOffset < .45 && isDrawerOpen){
                    onDrawerClosed(drawerView);
                    isDrawerOpen = false;
                    invalidateOptionsMenu();
                }
            }
        };

        drawerLayout.setDrawerListener(drawerToggle);

        drawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                displayFragment(position);
            }
        });
    }

    private void displayFragment(int position) {
        if(portfolios.get(position).equals("Add Portfolio")) {
            AddPortfolio.create(this);
        } else {
            StockListFragment fragment = new StockListFragment();

            Bundle bundle = new Bundle();
            bundle.putString(PORTFOLIO, portfolios.get(position));
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

    public void resetDrawer() {
        resetDrawer = true;
    }
}
