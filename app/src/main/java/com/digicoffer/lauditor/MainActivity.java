package com.digicoffer.lauditor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.digicoffer.lauditor.AuditTrails.AuditTrails;
import com.digicoffer.lauditor.Calendar.Meetings;
import com.digicoffer.lauditor.Calendar.Models.Event_Details_DO;
import com.digicoffer.lauditor.Calendar.MonthlyCalendar;
import com.digicoffer.lauditor.Calendar.WeeklyCalendar;
import com.digicoffer.lauditor.Chat.Chat;
import com.digicoffer.lauditor.ClientRelationships.ClientRelationship;
import com.digicoffer.lauditor.Dashboard.DahboardModels.MenuModels;
import com.digicoffer.lauditor.Dashboard.Dashboard;
import com.digicoffer.lauditor.Documents.Documents;
import com.digicoffer.lauditor.Groups.Groups;
import com.digicoffer.lauditor.LoginActivity.LoginActivity;
import com.digicoffer.lauditor.LoginActivity.biometric_page;
import com.digicoffer.lauditor.Matter.Matter;
import com.digicoffer.lauditor.Members.Members;
import com.digicoffer.lauditor.Notifications.Notifications;
import com.digicoffer.lauditor.TimeSheets.TimeSheets;
import com.digicoffer.lauditor.common.Constants;
import com.digicoffer.lauditor.email.Email;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MonthlyCalendar.EventDetailsListener, WeeklyCalendar.EventDetailsListener, View.OnClickListener {
    private static final int RC_SIGN_IN = 9001;
    ExtendedFloatingActionButton mAddFab;
    AppBarLayout header_layout;
    ImageView iv_logo_dashboard;
    TextView person_icon;
    Email email;
    ArrayList<MenuModels> menuList = new ArrayList<>();
    RecyclerView recyclerView;
    //    ImageButton iv_open_menu, iv_close_menu;
    private NewModel viewModel;
    private static final int AUTH_REQUEST_CODE = 1001;

    // Create a boolean flag to track if authentication is in progress
    private boolean isAuthInProgress = false;
    TextView tv_pageName;
    ActionBarDrawerToggle dtoggle;
    private BeginSignInRequest signUpRequest;
    //    Animation fabOpen, fabClose, rotateForward, rotateBackward;
    DrawerLayout dLayout;
    AppBarLayout appbar;
    ImageView iv_digilogo;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    MenuItem email_sm;

    ImageView iv_Drawer;
    ImageView menu_open;
    SignInClient oneTapClient;


    FloatingActionMenu center_menu;
    com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton actionButton;
    TextView tv_headerName, tv_digilogo, tv_header_firm_name;
    DrawerLayout navigationDrawer;
    //    FloatingActionButton fab_relationships, fab_documents, fab_timesheet, fab_matter, fab_more;
//    TextView tv_relations, tv_documents, tv_timesheet, tv_matter, tv_more;
    ImageView matters_bm, timesheets_bm, relationships_bm, groups_bm, team_members_bm, audit_bm, more_bm, firm_profile_bm, documents_bm;
    Menu nav_Menu;
    MenuItem team_member_sm, audits_sm, meetings_sm, messages_sm, notifications_sm, logout_sm;
    NavigationView navView;
    String token_id;
    int itemId;
    private static final int REQ_ONE_TAP = 2;
    private boolean showOneTapUI = true;
    private SignInClient OneTapClient;
    public androidx.appcompat.widget.LinearLayoutCompat ll_bottom_menu;
    Boolean isAllFabsVisible;
    GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        ll_bottom_menu = findViewById(R.id.ll_bottom_menu);

        menu_open = new ImageView(this);
        menu_open.setPadding(0, -30, 0, -30);
        menu_open.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu_icon_img));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(360, 170);
        params.setMargins(0, -10, 0, -40);
        actionButton = new com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton.Builder(this)
                .setContentView(menu_open)
                .setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu_desing))
                .setLayoutParams(new com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton.LayoutParams(params))
                .setPosition(com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton.POSITION_BOTTOM_CENTER)
                .build();

        // Initialize each menu item.
        matters_bm = new ImageView(this);
        timesheets_bm = new ImageView(this);
        documents_bm = new ImageView(this);
        relationships_bm = new ImageView(this);
        groups_bm = new ImageView(this);
        team_members_bm = new ImageView(this);
        firm_profile_bm = new ImageView(this);
        audit_bm = new ImageView(this);
        more_bm = new ImageView(this);

        matters_bm.setPadding(10, 10, 10, 10);
        timesheets_bm.setPadding(10, 10, 10, 10);
        documents_bm.setPadding(10, 10, 10, 10);
        relationships_bm.setPadding(15, 10, 15, 10);
        groups_bm.setPadding(10, 10, 10, 10);
        firm_profile_bm.setPadding(10, 10, 10, 10);
        more_bm.setPadding(10, 10, 10, 10);
        team_members_bm.setPadding(20, 5, 20, 5);

        // Set Icon for each menu item
        hide_un_chosen_menu();

        // Creating menu items which are also Floating Action Buttons
        SubActionButton.Builder rLSubBuilder = new SubActionButton.Builder(this);
        if (Constants.ROLE.equals("SU")) { //Restrict the User as per the Role.
            center_menu = new FloatingActionMenu.Builder(this)
                    .addSubActionView(rLSubBuilder.setContentView(matters_bm).setLayoutParams(new FrameLayout.LayoutParams(200, 200)).build())
                    .addSubActionView(rLSubBuilder.setContentView(timesheets_bm).build())
                    .addSubActionView(rLSubBuilder.setContentView(documents_bm).build())
                    .addSubActionView(rLSubBuilder.setContentView(relationships_bm).build())
                    .addSubActionView(rLSubBuilder.setContentView(groups_bm).build())
                    .attachTo(actionButton)
                    .setStartAngle(190)
                    .setEndAngle(350)
//                    .setRadius(280)
                    .build();
        } else if (Constants.ROLE.equals("GH")) {
            center_menu = new FloatingActionMenu.Builder(this)
                    .addSubActionView(rLSubBuilder.setContentView(matters_bm).setLayoutParams(new FrameLayout.LayoutParams(200, 200)).build())
                    .addSubActionView(rLSubBuilder.setContentView(timesheets_bm).build())
                    .addSubActionView(rLSubBuilder.setContentView(documents_bm).build())
                    .addSubActionView(rLSubBuilder.setContentView(relationships_bm).build())
                    .addSubActionView(rLSubBuilder.setContentView(more_bm).build())
                    .attachTo(actionButton)
                    .setStartAngle(190)
                    .setEndAngle(350)
//                    .setRadius(280)
                    .build();
        } else if (Constants.ROLE.equals("AAM")) {
            center_menu = new FloatingActionMenu.Builder(this)
                    .addSubActionView(rLSubBuilder.setContentView(timesheets_bm).setLayoutParams(new FrameLayout.LayoutParams(200, 200)).build())
                    .addSubActionView(rLSubBuilder.setContentView(groups_bm).build())
                    .addSubActionView(rLSubBuilder.setContentView(team_members_bm).build())
                    .addSubActionView(rLSubBuilder.setContentView(audit_bm).build())
                    .addSubActionView(rLSubBuilder.setContentView(firm_profile_bm).build())
                    .attachTo(actionButton)
                    .setStartAngle(190)
                    .setEndAngle(350)
//                    .setRadius(280)
                    .build();
        } else {
            center_menu = new FloatingActionMenu.Builder(this)
                    .addSubActionView(rLSubBuilder.setContentView(matters_bm).setLayoutParams(new FrameLayout.LayoutParams(200, 200)).build())
                    .addSubActionView(rLSubBuilder.setContentView(timesheets_bm).build())
                    .addSubActionView(rLSubBuilder.setContentView(documents_bm).build())
                    .addSubActionView(rLSubBuilder.setContentView(relationships_bm).build())
                    .addSubActionView(rLSubBuilder.setContentView(more_bm).build())
                    .attachTo(actionButton)
                    .setStartAngle(190)
                    .setEndAngle(350)
//                    .setRadius(280)
                    .build();
        }
        center_menu.setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener() {
            @Override
            public void onMenuOpened(FloatingActionMenu floatingActionMenu) {
                dLayout.close();
                menu_open.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu_down_icon));
            }

            @Override
            public void onMenuClosed(FloatingActionMenu floatingActionMenu) {
                menu_open.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu_icon_img));
            }
        });
        // OnClickListeners for each menu item
        matters_bm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fab_menu(new Matter());
                hide_un_chosen_menu();
                selected_menu();
                matters_bm.setImageDrawable(getDrawable(R.drawable.matter_white));
                matters_bm.setBackground(getDrawable(R.drawable.circular_button_background));
            }
        });

        timesheets_bm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fab_menu(new TimeSheets());
                hide_un_chosen_menu();
                selected_menu();
                timesheets_bm.setBackground(getDrawable(R.drawable.circular_button_background));
                timesheets_bm.setImageDrawable(getDrawable(R.drawable.timesheets_white));
            }
        });

        documents_bm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fab_menu(new Documents());
                hide_un_chosen_menu();
                selected_menu();
                documents_bm.setBackground(getDrawable(R.drawable.circular_button_background));
                documents_bm.setImageDrawable(getDrawable(R.drawable.documents_white));
            }
        });

        relationships_bm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fab_menu(new ClientRelationship());
                hide_un_chosen_menu();
                selected_menu();
                relationships_bm.setBackground(getDrawable(R.drawable.circular_button_background));
                relationships_bm.setImageDrawable(getDrawable(R.drawable.relationship_white));
            }
        });
        groups_bm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fab_menu(new Groups());
                hide_un_chosen_menu();
                selected_menu();
                groups_bm.setBackground(getDrawable(R.drawable.circular_button_background));
                groups_bm.setImageDrawable(getDrawable(R.drawable.groups_white));
            }
        });
        team_members_bm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fab_menu(new Members());
                hide_un_chosen_menu();
                selected_menu();
                team_members_bm.setBackground(getDrawable(R.drawable.circular_button_background));
                team_members_bm.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.team_member_white));
            }
        });
        firm_profile_bm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fab_menu(new Dashboard());
                hide_un_chosen_menu();
                selected_menu();
                firm_profile_bm.setBackground(getDrawable(R.drawable.circular_button_background));
                firm_profile_bm.setImageDrawable(getDrawable(R.drawable.firm_profile_white));
            }
        });
        audit_bm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fab_menu(new AuditTrails());
                hide_un_chosen_menu();
                selected_menu();
                audit_bm.setBackground(getDrawable(R.drawable.circular_button_background));
                audit_bm.setImageDrawable(getDrawable(R.drawable.audit_white));
            }
        });
        more_bm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dLayout.openDrawer(GravityCompat.START);
                center_menu.close(true);
                hide_un_chosen_menu();
//                selected_menu();
                more_bm.setBackground(getDrawable(R.drawable.circular_button_background));
                more_bm.setImageDrawable(getDrawable(R.drawable.more_white));
            }
        });

        try {
//            mAddFab = findViewById(R.id.fb_menu);
//            fabOpen = AnimationUtils.loadAnimation
//                    (this, R.anim.fab_open);
//            fabClose = AnimationUtils.loadAnimation
//                    (this, R.anim.fab_close);
//            rotateForward = AnimationUtils.loadAnimation
//                    (this, R.anim.rotate_forward);
//            rotateBackward = AnimationUtils.loadAnimation
//                    (this, R.anim.rotate_backward);


//            iv_open_menu = findViewById(R.id.iv_up_arrow);
//            iv_close_menu = findViewById(R.id.iv_down_arrow);
//            fab_relationships = findViewById(R.id.fb_relationships);
//            fab_relationships.setVisibility(View.GONE);
//            fab_documents = findViewById(R.id.fb_documents);
//            fab_documents.setVisibility(View.GONE);
//            ll_bottom_menu = findViewById(R.id.ll_bottom_menu);
//            fab_matter = findViewById(R.id.fb_matter);
//            fab_matter.setVisibility(View.GONE);
//            fab_timesheet = findViewById(R.id.fb_timesheets);
//            fab_timesheet.setVisibility(View.GONE);
//            fab_more = findViewById(R.id.fb_more);
//            fab_more.setVisibility(View.GONE);

            tv_pageName = findViewById(R.id.page_name);
            iv_logo_dashboard = findViewById(R.id.logo_dashboard);
            appbar = (AppBarLayout) findViewById(R.id.appbar);
            appbar.setVisibility(View.VISIBLE);
            viewModel = new ViewModelProvider(this).get(NewModel.class);
            viewModel.getselectedItem().observe(this, item -> {
                tv_pageName.setText(item);
            });
            iv_Drawer = (ImageView) findViewById(R.id.menu);
//            ll_bottom_menu.setVisibility(View.GONE);
            actionButton.setVisibility(View.VISIBLE);
            iv_Drawer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!dLayout.isDrawerOpen(GravityCompat.START)) {
                        dLayout.openDrawer(GravityCompat.START);
                        center_menu.close(true);
//                        ll_bottom_menu.setVisibility(View.GONE);
                    } else {
                        dLayout.close();
//                        // ll_bottom_menu.setVisibility(View.VISIBLE);
                    }
                }
            });
            iv_logo_dashboard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hide_un_chosen_menu();
                    selected_menu();
                    fab_menu(new Dashboard());
//                    ft.commit();
                }
            });
            Fragment fragment = new Dashboard();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.id_framelayout, fragment);
            ft.commit();
            isAllFabsVisible = false;
            setNavigationDrawer();

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
//            fab_more.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Fragment fragment = new Meetings();
//                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//                    ft.replace(R.id.id_framelayout, fragment);
//                    ft.addToBackStack("current_fragment").commit();
////                    ft.commit();
//                    closeMenu();
//                }
//            });
//            fab_matter.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Fragment fragment = new Matter();
//                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//                    ft.replace(R.id.id_framelayout, fragment);
//                    ft.addToBackStack("current_fragment").commit();
////                    ft.commit();
//                    closeMenu();
//                }
//            });
//            fab_timesheet.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Fragment fragment1 = new TimeSheets();
//                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//                    ft.replace(R.id.id_framelayout, fragment1);
//                    ft.addToBackStack("current_fragment").commit();
////                    ft.commit();
//                    closeMenu();
//                }
//            });
//            fab_documents.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Fragment fragment1 = new Documents();
//                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//                    ft.replace(R.id.id_framelayout, fragment1);
//                    ft.addToBackStack("current_fragment").commit();
////                    ft.commit();
//                    closeMenu();
//                }
//            });
//            fab_relationships.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Fragment fragment1 = new ClientRelationship();
//                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//                    ft.replace(R.id.id_framelayout, fragment1);
//                    ft.addToBackStack("current_fragment").commit();
////                    ft.commit();
//                    closeMenu();
//                }
//            });
//            iv_open_menu.setOnClickListener(new View.OnClickListener
//                    () {
//                @Override
//                public void onClick(View view) {
//                    try {
//                        openMenu();
//                    } catch (Exception e) {
//                        Log.e("Error", "Error" + e.getMessage());
//                        e.printStackTrace();
//                    }
//                }
//            });
//            iv_close_menu.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    try {
//                        closeMenu();
//
//                    } catch (Exception e) {
//                        Log.e("Error", "Error" + e.getMessage());
//                        e.printStackTrace();
//                    }
//                }
//            });
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

//    private void loadRecyclerview() {
//        menuList.add(new MenuModels("Matters", R.drawable.matter));
//        menuList.add(new MenuModels("Documents", R.drawable.document));
//        menuList.add(new MenuModels("Relationships", R.drawable.relationship));
//        menuList.add(new MenuModels("Timesheets", R.drawable.timesheet));
//        menuList.add(new MenuModels("Meetings", R.drawable.meeting_new));
//        menuList.add(new MenuModels("Emails", R.drawable.email_icon_dashboard));
//        menuList.add(new MenuModels("Messages", R.drawable.client_chat_icon));
//        menuList.add(new MenuModels("Notifications", R.drawable.notification_icon));
//        menuList.add(new MenuModels("Audits", R.drawable.audit_new));
//        menuList.add(new MenuModels("Groups", R.drawable.groups_icon));
//        menuList.add(new MenuModels("Invoice", R.drawable.invoice_icon));
//        recyclerView = findViewById(R.id.rv_menu);
//        Animation animation = AnimationUtils.loadAnimation(this, R.anim.rotate);
//        recyclerView.startAnimation(animation);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
//        MenuAdapter menuAdapter = new MenuAdapter(menuList);
//        recyclerView.setAdapter(menuAdapter);
//    }

    private void setNavigationDrawer() {
        dLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        navigationDrawer.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // initiate a DrawerLayout
        navView = (NavigationView) findViewById(R.id.navigation);
        nav_Menu = navView.getMenu();
//        MenuItem version = nav_Menu.findItem(R.id.Version);
//        version.setTitle("Version" + "(" + "v" + Constants.VERSION + ")");
        View header = navView.getHeaderView(0);
        header_layout = (AppBarLayout) header.findViewById(R.id.header_layout);
        header_layout.setVisibility(View.GONE);
//        tv_headerName = (TextView) header.findViewById(R.id.tv_headerName);
//        tv_headerName.setText(Constants.NAME);
        person_icon = (TextView) findViewById(R.id.person_icon);
        if (!Constants.NAME.equals("")) {
            String person_name = Constants.NAME.substring(0, 2);
            person_icon.setText(person_name);
        }
//        tv_header_firm_name = (TextView) header.findViewById(R.id.tv_firm_name);
//        tv_header_firm_name.setText(Constants.FIRM_NAME);
        iv_digilogo = (ImageView) header.findViewById(R.id.tv_digicofferlogo_header);
        iv_digilogo.setAlpha(127);
        iv_digilogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide_un_chosen_menu();
                selected_menu();
                Dashboard fragment_d = new Dashboard();
                fab_menu(fragment_d);
                // ll_bottom_menu.setVisibility(View.VISIBLE);
            }
        });
        //Initialize Menu Items..
        team_member_sm = nav_Menu.findItem(R.id.members);
        audits_sm = nav_Menu.findItem(R.id.audit);
        meetings_sm = nav_Menu.findItem(R.id.calendar);
        messages_sm = nav_Menu.findItem(R.id.chat);
        notifications_sm = nav_Menu.findItem(R.id.notifications);
        email_sm = nav_Menu.findItem(R.id.email);
        logout_sm = nav_Menu.findItem(R.id.logout);

        //Side Menu Items Text Color changes.
        selected_menu();

        //Restrict the Menu Items in Side Menu as per their Role.
        if (Constants.ROLE.equals("AAM")) {
            hide_menu_items();
            nav_Menu.findItem(R.id.calendar).setVisible(true);//meetings menu
            nav_Menu.findItem(R.id.chat).setVisible(true);//messages menu
            nav_Menu.findItem(R.id.notifications).setVisible(true);
        } else if (Constants.ROLE.equals("SU")) {
            hide_menu_items();
            nav_Menu.findItem(R.id.members).setVisible(true);
            nav_Menu.findItem(R.id.audit).setVisible(true);
            nav_Menu.findItem(R.id.calendar).setVisible(true);//meeting menu
            nav_Menu.findItem(R.id.chat).setVisible(true);//messages menu
            nav_Menu.findItem(R.id.notifications).setVisible(true);
            nav_Menu.findItem(R.id.email).setVisible(true);
        } else if (Constants.ROLE.equals("GH")) {
            hide_menu_items();
            nav_Menu.findItem(R.id.audit).setVisible(true);
            nav_Menu.findItem(R.id.calendar).setVisible(true);//meeting menu
            nav_Menu.findItem(R.id.chat).setVisible(true);//messages menu
            nav_Menu.findItem(R.id.notifications).setVisible(true);
            nav_Menu.findItem(R.id.email).setVisible(true);
        } else {
            hide_menu_items();
            nav_Menu.findItem(R.id.calendar).setVisible(true);//meeting menu
            nav_Menu.findItem(R.id.chat).setVisible(true);//messages menu
            nav_Menu.findItem(R.id.notifications).setVisible(true);
            nav_Menu.findItem(R.id.email).setVisible(true);
        }

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            /**
             * @noinspection InstantiationOfUtilityClass
             */
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                hide_un_chosen_menu();
                selected_menu();
                Fragment frag = null; // create a Fragment Object
                Activity activity = null;
                itemId = menuItem.getItemId();
                if (itemId == R.id.Dashboard) {
                    frag = new Dashboard();
                } else if (itemId == R.id.members) {
                    //1st option
                    SpannableString spannableString1 = new SpannableString(team_member_sm.getTitle());
                    spannableString1.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getApplicationContext(), R.color.green_count_color)), 0, spannableString1.length(), 0);
                    team_member_sm.setTitle(spannableString1);
                    frag = new Members();
                } else if (itemId == R.id.audit) {
                    //2nd option
                    SpannableString spannableString1 = new SpannableString(audits_sm.getTitle());
                    spannableString1.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getApplicationContext(), R.color.green_count_color)), 0, spannableString1.length(), 0);
                    audits_sm.setTitle(spannableString1);
                    frag = new AuditTrails();
                } else if (itemId == R.id.calendar) {
                    //3rd option
                    SpannableString spannableString1 = new SpannableString(meetings_sm.getTitle());
                    spannableString1.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getApplicationContext(), R.color.green_count_color)), 0, spannableString1.length(), 0);
                    meetings_sm.setTitle(spannableString1);
                    frag = new Meetings();
                } else if (itemId == R.id.chat) {
                    //4th option
                    SpannableString spannableString1 = new SpannableString(messages_sm.getTitle());
                    spannableString1.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getApplicationContext(), R.color.green_count_color)), 0, spannableString1.length(), 0);
                    messages_sm.setTitle(spannableString1);
                    frag = new Chat();
                } else if (itemId == R.id.notifications) {
                    //5th option
                    SpannableString spannableString1 = new SpannableString(notifications_sm.getTitle());
                    spannableString1.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getApplicationContext(), R.color.green_count_color)), 0, spannableString1.length(), 0);
                    notifications_sm.setTitle(spannableString1);
                    frag = new Notifications();
                } else if (itemId == R.id.email) {
//                    if (!isAuthInProgress) {
//
//                        isAuthInProgress = true;
//
//
//                        String url = "https://accounts.google.com/o/oauth2/v2/auth/oauthchooseaccount?access_type=offline&scope=https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuser.emails.read%20https%3A%2F%2Fmail.google.com%2F&state=ZXlKMGVYQWlPaUpLVjFRaUxDSmhiR2NpT2lKSVV6STFOaUo5LmV5SjFhV1FpT2lKQlEwSXhNRUUyUWpVelJqbEVNamRFSWl3aVlXUnRhVzRpT21aaGJITmxMQ0p3YkdGdUlqb2liR0YxWkdsMGIzSWlMQ0p5YjJ4bElqb2lVMVVpTENKdVlXMWxJam9pVTI5MWJtUmhjbmxoSUVSTVJpQlRWU0lzSW5WelpYSmZhV1FpT2lJMk0ySm1aRGxpTjJFeFpHSTNNakJtTW1Rek9HUTBaRElpTENKbGVIQWlPakUzTURrd01qZ3lPVGw5LlUtdjIyWjhSQUVfcmNWQ05ZZEpvdjFOQ2ttM3hsVWVHd0hvdHc0bXY1QWc%3D&response_type=code&client_id=1074057396282-o970s9o11m4g3dla4g0lokc5k4e7o8g5.apps.googleusercontent.com&redirect_uri=https%3A%2F%2Fdev.utils.mail.digicoffer.com%2Foauth2callback&service=lso&o2v=2&theme=glif&flowName=GeneralOAuthFlow";
//
//
//                        Intent intent = new Intent(Intent.ACTION_VIEW);
//                        intent.setData(Uri.parse(url));
//                        startActivityForResult(intent, AUTH_REQUEST_CODE);
//
//
//                    }


                    SpannableString spannableString1 = new SpannableString(email_sm.getTitle());
                    spannableString1.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getApplicationContext(), R.color.green_count_color)), 0, spannableString1.length(), 0);
                    email_sm.setTitle(spannableString1);
                    frag = new Email();
                }


                if (itemId == R.id.logout) {
                    confirmLogout();
                } else if (itemId == R.id.matter) {
                    frag = new Matter();
                } else if (itemId == R.id.documents) {
                    frag = new Documents();
                } else if (itemId == R.id.relationships) {
                    frag = new ClientRelationship();
                } else if (itemId == R.id.groups) {
                    frag = new Groups();
                } else if (itemId == R.id.timesheets) {
                    frag = new TimeSheets();
                } else if (itemId == R.id.invoices) {
                    frag = new Dashboard();
                } else if (itemId == R.id.firm_profile) {
                    frag = new Dashboard();
                } else if (itemId == R.id.Version) {
                    frag = new Dashboard();
                }

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

    //  @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == AUTH_REQUEST_CODE) {
//
//            isAuthInProgress = false;
//
//
//            if (resultCode == RESULT_OK)
//                email.callLegalMatter();
//            else {
//
//
//            }
//        }
//    }


    // Method to fetch emails after successful authentication
    public class GMAILLABLEL extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Void... voids) {
            try {
                String apiUrl = Constants.EMAIL_BASE_URL + "gmail/label/" + token_id + "?labelid=INBOX";

                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    return new JSONObject(response.toString());
                } else {
                    throw new IOException("Error: " + responseCode);
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            if (jsonObject != null) {

                try {
                    JSONObject data = jsonObject.getJSONObject("data");
                    String id = data.getString("id");
                    String type = data.getString("type");
                    int messagesTotal = data.getInt("messagesTotal");
                    int messagesUnread = data.getInt("messagesUnread");
                    int threadsTotal = data.getInt("threadsTotal");
                    int threadsUnread = data.getInt("threadsUnread");


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                // Handle null response
            }
        }
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
                confirmLogout();
            else
                super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (dtoggle.onOptionsItemSelected(item)) {
            actionButton.setVisibility(View.VISIBLE);
            // ll_bottom_menu.setVisibility(View.VISIBLE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void confirmLogout() {
        Log.d("is_biometric", "" + Constants.is_biometric);
        if (Constants.is_biometric) {
            Intent intent = new Intent(MainActivity.this, biometric_page.class);
            startActivity(intent);
        } else {
            performLogout();
        }
    }

    private void performLogout() {
        // Clear SharedPreferences or any other session data here
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor1 = sharedPreferences.edit();
        editor1.clear();
        editor1.apply();
        SharedPreferences sharedPreferences_bio = getSharedPreferences("BIO", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences_bio.edit();
        editor.clear();
        editor.apply();
        Constants.is_biometric = false;
        // Optionally, redirect the user to the LoginActivity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish(); // To close the MainActivity and remove it from the back stack
    }

    private void fab_menu(Fragment fragment) { //Navigation of Bottom Menu
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.id_framelayout, fragment);
        ft.addToBackStack("current_fragment").commit();
        center_menu.close(true);
        dLayout.closeDrawers();
        menu_open.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu_icon_img));
    }

    private void selected_menu() { //Setting the Text color for the each Menu Item in Side Menu.
        SpannableString spannableString1 = new SpannableString(team_member_sm.getTitle());
        spannableString1.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.black)), 0, spannableString1.length(), 0);
        team_member_sm.setTitle(spannableString1);
        SpannableString spannableString2 = new SpannableString(audits_sm.getTitle());
        spannableString2.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.black)), 0, spannableString2.length(), 0);
        audits_sm.setTitle(spannableString2);
        SpannableString spannableString3 = new SpannableString(meetings_sm.getTitle());
        spannableString3.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.black)), 0, spannableString3.length(), 0);
        meetings_sm.setTitle(spannableString3);
        SpannableString spannableString4 = new SpannableString(messages_sm.getTitle());
        spannableString4.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.black)), 0, spannableString4.length(), 0);
        messages_sm.setTitle(spannableString4);
        SpannableString spannableString5 = new SpannableString(notifications_sm.getTitle());
        spannableString5.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.black)), 0, spannableString5.length(), 0);
        notifications_sm.setTitle(spannableString5);
        SpannableString spannableString6 = new SpannableString(email_sm.getTitle());
        spannableString6.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.black)), 0, spannableString6.length(), 0);
        email_sm.setTitle(spannableString6);
    }

    private void hide_menu_items() { //Hide the Menu Items in Side Menu....
        nav_Menu.findItem(R.id.Dashboard).setVisible(false);
        nav_Menu.findItem(R.id.matter).setVisible(false);
        nav_Menu.findItem(R.id.documents).setVisible(false);
        nav_Menu.findItem(R.id.relationships).setVisible(false);
        nav_Menu.findItem(R.id.email).setVisible(false);
        nav_Menu.findItem(R.id.invoices).setVisible(false);
        nav_Menu.findItem(R.id.chat).setVisible(false);
        nav_Menu.findItem(R.id.calendar).setVisible(false);
        nav_Menu.findItem(R.id.notifications).setVisible(false);
        nav_Menu.findItem(R.id.Version).setVisible(false);
        //....
        nav_Menu.findItem(R.id.timesheets).setVisible(false);
        nav_Menu.findItem(R.id.groups).setVisible(false);
        nav_Menu.findItem(R.id.members).setVisible(false);
        nav_Menu.findItem(R.id.audit).setVisible(false);
        nav_Menu.findItem(R.id.firm_profile).setVisible(false);
    }

    private void hide_un_chosen_menu() {
        //Bottom Menu Backgrounds
        matters_bm.setBackground(getDrawable(com.applandeo.materialcalendarview.R.drawable.background_transparent));
        timesheets_bm.setBackground(getDrawable(com.applandeo.materialcalendarview.R.drawable.background_transparent));
        documents_bm.setBackground(getDrawable(com.applandeo.materialcalendarview.R.drawable.background_transparent));
        relationships_bm.setBackground(getDrawable(com.applandeo.materialcalendarview.R.drawable.background_transparent));
        groups_bm.setBackground(getDrawable(com.applandeo.materialcalendarview.R.drawable.background_transparent));
        team_members_bm.setBackground(getDrawable(com.applandeo.materialcalendarview.R.drawable.background_transparent));
        firm_profile_bm.setBackground(getDrawable(com.applandeo.materialcalendarview.R.drawable.background_transparent));
        audit_bm.setBackground(getDrawable(com.applandeo.materialcalendarview.R.drawable.background_transparent));
        more_bm.setBackground(getDrawable(com.applandeo.materialcalendarview.R.drawable.background_transparent));

        //Bottom Menu Icons
        matters_bm.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.matters_menu_icon));
        timesheets_bm.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.timesheets_menu_icon));
        documents_bm.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.documents_menu_icon));
        relationships_bm.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.relationship_menu_icon));
        groups_bm.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.group_menu_icon));
        team_members_bm.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.teammember_menu_icon));
        firm_profile_bm.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.firm_profile_menu_icon));
        audit_bm.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.audit_menu_icon));
        more_bm.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.more_menu_icon));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu:
                break;
        }
    }
}