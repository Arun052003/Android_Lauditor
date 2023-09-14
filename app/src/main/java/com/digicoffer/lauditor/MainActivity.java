package com.digicoffer.lauditor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.digicoffer.lauditor.Calendar.Meetings;
import com.digicoffer.lauditor.Calendar.Models.Event_Details_DO;
import com.digicoffer.lauditor.Calendar.MonthlyCalendar;
import com.digicoffer.lauditor.Calendar.WeeklyCalendar;
import com.digicoffer.lauditor.Chat.Chat;
import com.digicoffer.lauditor.ClientRelationships.ClientRelationship;
import com.digicoffer.lauditor.Dashboard.Dashboard;
import com.digicoffer.lauditor.Documents.Documents;
import com.digicoffer.lauditor.Groups.Groups;
import com.digicoffer.lauditor.Matter.Matter;
import com.digicoffer.lauditor.Members.Members;
import com.digicoffer.lauditor.Notifications.Notifications;
import com.digicoffer.lauditor.TimeSheets.TimeSheets;
import com.digicoffer.lauditor.common.Constants;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MonthlyCalendar.EventDetailsListener, WeeklyCalendar.EventDetailsListener, View.OnClickListener {
    ExtendedFloatingActionButton mAddFab;
    ImageView iv_logo_dashboard;
    ImageButton iv_open_menu, iv_close_menu;
    private NewModel viewModel;
    TextView tv_pageName;
    ActionBarDrawerToggle dtoggle;
    Animation fabOpen, fabClose, rotateForward, rotateBackward;
    DrawerLayout dLayout;
    AppBarLayout appbar;
    ImageView iv_digilogo;
    ImageView iv_Drawer;
    TextView tv_headerName, tv_digilogo, tv_header_firm_name;
    DrawerLayout navigationDrawer;
    FloatingActionButton fab_relationships, fab_documents, fab_timesheet, fab_matter, fab_more;
    TextView tv_relations, tv_documents, tv_timesheet, tv_matter, tv_more;
    public androidx.appcompat.widget.LinearLayoutCompat ll_bottom_menu;
    Boolean isAllFabsVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
//            mAddFab = findViewById(R.id.fb_menu);
            fabOpen = AnimationUtils.loadAnimation
                    (this, R.anim.fab_open);
            fabClose = AnimationUtils.loadAnimation
                    (this, R.anim.fab_close);
            rotateForward = AnimationUtils.loadAnimation
                    (this, R.anim.rotate_forward);
            rotateBackward = AnimationUtils.loadAnimation
                    (this, R.anim.rotate_backward);
            iv_open_menu = findViewById(R.id.iv_up_arrow);
            iv_close_menu = findViewById(R.id.iv_down_arrow);
            fab_relationships = findViewById(R.id.fb_relationships);
            fab_relationships.setVisibility(View.GONE);
            fab_documents = findViewById(R.id.fb_documents);
            fab_documents.setVisibility(View.GONE);
            iv_logo_dashboard = findViewById(R.id.logo_dashboard);
            ll_bottom_menu = findViewById(R.id.ll_bottom_menu);
            fab_matter = findViewById(R.id.fb_matter);
            tv_pageName = findViewById(R.id.page_name);
            appbar = (AppBarLayout) findViewById(R.id.appbar);
            appbar.setVisibility(View.VISIBLE);


            fab_matter.setVisibility(View.GONE);
            fab_timesheet = findViewById(R.id.fb_timesheets);
            fab_timesheet.setVisibility(View.GONE);
            fab_more = findViewById(R.id.fb_more);
            fab_more.setVisibility(View.GONE);
            viewModel = new ViewModelProvider(this).get(NewModel.class);
            viewModel.getselectedItem().observe(this, item -> {
                tv_pageName.setText(item);
            });
            iv_Drawer = (ImageView) findViewById(R.id.menu);
            ll_bottom_menu.setVisibility(View.VISIBLE);
            iv_Drawer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!dLayout.isDrawerOpen(Gravity.START)) {

                        dLayout.openDrawer(Gravity.START);
                        ll_bottom_menu.setVisibility(View.GONE);
                    } else {
                        dLayout.closeDrawer(Gravity.END);
                        ll_bottom_menu.setVisibility(View.VISIBLE);
                    }
                }
            });
//            dLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//            navigationDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

//            tv_relations = findViewById(R.id.tv_relationships);
//            tv_relations.setVisibility(View.GONE);
//            tv_documents = findViewById(R.id.tv_documents);
//            tv_documents.setVisibility(View.GONE);
//            tv_timesheet = findViewById(R.id.tv_timesheet);
//            tv_timesheet.setVisibility(View.GONE);
//            tv_matter = findViewById(R.id.tv_matter);
//            tv_matter.setVisibility(View.GONE);
//            tv_more = findViewById(R.id.tv_more);
//            tv_more.setVisibility(View.GONE);
            Fragment fragment = new Dashboard();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.id_framelayout, fragment);
            ft.commit();
            isAllFabsVisible = false;
            setNavigationDrawer();
            fab_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment fragment = new Meetings();
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.id_framelayout, fragment);
                    ft.addToBackStack("current_fragment").commit();
//                    ft.commit();
                    closeMenu();
                }
            });
            fab_matter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment = new Matter();
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.id_framelayout, fragment);
                    ft.addToBackStack("current_fragment").commit();
//                    ft.commit();
                    closeMenu();
                }
            });
            fab_timesheet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment fragment1 = new TimeSheets();
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.id_framelayout, fragment1);
                    ft.addToBackStack("current_fragment").commit();
//                    ft.commit();
                    closeMenu();
                }
            });
            fab_documents.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment fragment1 = new Documents();
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.id_framelayout, fragment1);
                    ft.addToBackStack("current_fragment").commit();
//                    ft.commit();
                    closeMenu();
                }
            });
            fab_relationships.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment fragment1 = new ClientRelationship();
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.id_framelayout, fragment1);
                    ft.addToBackStack("current_fragment").commit();
//                    ft.commit();
                    closeMenu();
                }
            });
            iv_logo_dashboard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment fragment = new Dashboard();
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.id_framelayout, fragment);
                    ft.addToBackStack("current_fragment").commit();
//                    ft.commit();
                }
            });
            iv_open_menu.setOnClickListener(new View.OnClickListener
                    () {
                @Override
                public void onClick(View view) {
                    try {
                        openMenu();
                    } catch (Exception e) {
                        Log.e("Error", "Error" + e.getMessage());
                        e.printStackTrace();
                    }
                }
            });

            iv_close_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        closeMenu();

                    } catch (Exception e) {
                        Log.e("Error", "Error" + e.getMessage());
                        e.printStackTrace();
                    }
                }
            });

        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
//        Sup
    }

    private void setNavigationDrawer() {

        dLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        navigationDrawer.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        // initiate a DrawerLayout
        final NavigationView navView = (NavigationView) findViewById(R.id.navigation);
        Menu nav_Menu = navView.getMenu();
        MenuItem version = nav_Menu.findItem(R.id.Version);
        version.setTitle("Version" + "(" + "v" + Constants.VERSION + ")");
        View header = navView.getHeaderView(0);
        tv_headerName = (TextView) header.findViewById(R.id.tv_headerName);
        tv_headerName.setText(Constants.NAME);
        tv_header_firm_name = (TextView) header.findViewById(R.id.tv_firm_name);
        tv_header_firm_name.setText(Constants.FIRM_NAME);
        iv_digilogo = (ImageView) header.findViewById(R.id.tv_digicofferlogo_header);
        iv_digilogo.setAlpha(127);
        iv_digilogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dashboard fragment_d = new Dashboard();
                FragmentManager fragmentManager3 = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction3 = fragmentManager3.beginTransaction();
                fragmentTransaction3.replace(R.id.id_framelayout, fragment_d);
                fragmentTransaction3.addToBackStack(null);
                fragmentTransaction3.commit();
                navigationDrawer.closeDrawers();
                ll_bottom_menu.setVisibility(View.VISIBLE);

            }
        });


        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                Fragment frag = null;
                Activity activity = null;
//                fb_chat.hide();
                // create a Fragment Object
                int itemId = menuItem.getItemId();
                if (itemId == R.id.Dashboard) {
                    ll_bottom_menu.setVisibility(View.VISIBLE);
                    frag = new Dashboard();

                }else if(itemId == R.id.notifications){
                    ll_bottom_menu.setVisibility(View.VISIBLE);
                    frag = new Notifications();
                }else if(itemId == R.id.matter){
                    ll_bottom_menu.setVisibility(View.VISIBLE);
                    frag = new Matter();
                }else if(itemId == R.id.documents){
                    ll_bottom_menu.setVisibility(View.VISIBLE);
                    frag = new Documents();
                }else if(itemId == R.id.calendar){
                    ll_bottom_menu.setVisibility(View.VISIBLE);
                    frag = new Meetings();
                }else if(itemId == R.id.relationships){
                    ll_bottom_menu.setVisibility(View.VISIBLE);
                    frag = new ClientRelationship();
                }else if(itemId == R.id.groups){
                    ll_bottom_menu.setVisibility(View.VISIBLE);
                    frag = new Groups();
                }else if(itemId == R.id.timesheets){
                    ll_bottom_menu.setVisibility(View.VISIBLE);
                    frag = new TimeSheets();
                }else if(itemId == R.id.members){
                    ll_bottom_menu.setVisibility(View.VISIBLE);
                    frag = new Members();
                }else if(itemId == R.id.chat){
                    ll_bottom_menu.setVisibility(View.VISIBLE);
                    frag = new Chat();
                }
//                else if (itemId == R.id.CredentialDocuments) {
//                    frag = new Credential_Docs();
//                    FragmentManager fragmentManager = getSupportFragmentManager();
//                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                    fragmentTransaction.replace(R.id.id_framelayout, frag);
//                    fragmentTransaction.addToBackStack(null);
//                    fragmentTransaction.commit();
//                } else if (itemId == R.id.ClientRelationships) {
//                    frag = new Client_Relationships();
//                    FragmentManager fragmentManager = getSupportFragmentManager();
//                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                    fragmentTransaction.replace(R.id.id_framelayout, frag);
//                    fragmentTransaction.addToBackStack(null);
//                    fragmentTransaction.commit();
//                } else if (itemId == R.id.ProfessionalDocuments) {
//                    frag = new Professional_Docs();
//                    FragmentManager fragmentManager = getSupportFragmentManager();
//                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                    fragmentTransaction.replace(R.id.id_framelayout, frag);
//                    fragmentTransaction.addToBackStack(null);
//                    fragmentTransaction.commit();
//
//                } else if (itemId == R.id.relationship_contacts) {
//                    frag = new Relationship_Contacts();
//                    FragmentManager fragmentManager = getSupportFragmentManager();
//                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                    fragmentTransaction.replace(R.id.id_framelayout, frag);
//                    fragmentTransaction.addToBackStack(null);
//                    fragmentTransaction.commit();
//                } else if (itemId == R.id.team_members) {
//                    frag = new Team_Members();
//                    FragmentManager fragmentManager = getSupportFragmentManager();
//                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                    fragmentTransaction.replace(R.id.id_framelayout, frag);
//                    fragmentTransaction.addToBackStack(null);
//                    fragmentTransaction.commit();
//                } else if (itemId == R.id.Notifications) {
//                    frag = new Notifications();
//                    FragmentManager fragmentManager = getSupportFragmentManager();
//                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                    fragmentTransaction.replace(R.id.id_framelayout, frag);
//                    fragmentTransaction.addToBackStack(null);
//                    fragmentTransaction.commit();
//                } else if (itemId == R.id.Reminders) {
//                    frag = new Reminders();
//                    FragmentManager fragmentManager = getSupportFragmentManager();
//                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                    fragmentTransaction.replace(R.id.id_framelayout, frag);
//                    fragmentTransaction.addToBackStack(null);
//                    fragmentTransaction.commit();
//                }

//                else if (itemId == R.id.Logout) {
//
//                    logout();
//                }


                if (frag != null) {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.id_framelayout, frag);
                    transaction.commit();
                    dLayout.closeDrawers();
                    return true;
                }
                return true;
            }
        });
    }

    private void closeMenu() {
        iv_open_menu.setVisibility(View.VISIBLE);
        iv_close_menu.setVisibility(View.GONE);
//            mAddFab.setIcon(ContextCompat.getDrawable(MainActivity.this, R.drawable.down_arrow));
        fab_relationships.setVisibility(View.GONE);
        fab_relationships.startAnimation(fabClose);
        fab_documents.setVisibility(View.GONE);
        fab_documents.startAnimation(fabClose);
        fab_matter.setVisibility(View.GONE);
        fab_matter.startAnimation(fabClose);
        fab_timesheet.setVisibility(View.GONE);
        fab_timesheet.startAnimation(fabClose);
        fab_more.setVisibility(View.GONE);
        fab_more.startAnimation(fabClose);
    }

    private void openMenu() {
        iv_close_menu.setVisibility(View.VISIBLE);
        iv_open_menu.setVisibility(View.GONE);
//            mAddFab.setIcon(ContextCompat.getDrawable(MainActivity.this, R.drawable.up_arrow));
//                    mAddFab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_full_sad));
        fab_relationships.setVisibility(View.VISIBLE);
        fab_relationships.startAnimation(fabOpen);
        fab_documents.setVisibility(View.VISIBLE);
        fab_documents.startAnimation(fabOpen);
        fab_matter.setVisibility(View.VISIBLE);
        fab_matter.startAnimation(fabOpen);
        fab_timesheet.setVisibility(View.VISIBLE);
        fab_timesheet.startAnimation(fabOpen);
        fab_more.setVisibility(View.VISIBLE);
        fab_more.startAnimation(fabOpen);
    }

    private void animateFab() {

//        LinearLayout.LayoutParams lp = new
//                LinearLayout.LayoutParams(tv_relations.getWidth(),tv_relations.getHeight());
//        lp.setMargins(0,0,165,15);
        if (!isAllFabsVisible) {
//            iv_open_menu.startAnimation(rotateForward);

//            tv_more.startAnimation(fabOpen);
//                    mAddFab.extend();
            isAllFabsVisible = true;
        } else {
//            iv_open_menu.startAnimation(rotateBackward);

        }

    }


    private void logout() {
        new AlertDialog.Builder(this)
                .setTitle("Alert")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//                        prefs.edit().remove("current_fragment");
////                        SharedPreferences crend_Prefs = getSharedPreferences(Constants.BIOMETIRCSharedPrefsKey, MODE_PRIVATE);
//                        crend_Prefs.edit().putString(Constants.TOKEN_key,"");
//                        crend_Prefs.edit().putString(Constants.PK_key,"");
//                        crend_Prefs.edit().apply();
//
//                        FragmentManager fragmentManager = getSupportFragmentManager();
//                        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                        finish();

                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void onEventDetailsPassed(ArrayList<Event_Details_DO> event_details_list, String calendar_Type) {

    }
    @Override
    public void onBackPressed() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        closeSubMenusFab();
        prefs.edit().remove("current_fragment").apply();
//                .putString("current_fragment", getSupportFragmentManager().getFragments().get(0).getClass().getSimpleName()).apply();
//        if (getSupportFragmentManager().getFragments().get(0) instanceof MessagesList)
//            fb_chat.hide();
//        else
//            fb_chat.hide();
        appbar.setVisibility(View.VISIBLE);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (getSupportFragmentManager().getFragments().get(0) instanceof Dashboard)
                logout();
            else
                super.onBackPressed();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (dtoggle.onOptionsItemSelected(item)) {
            ll_bottom_menu.setVisibility(View.VISIBLE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu:

                break;
        }
    }
}