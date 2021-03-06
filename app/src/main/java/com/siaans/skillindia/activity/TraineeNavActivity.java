package com.siaans.skillindia.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import com.siaans.skillindia.ExDialog;
import com.siaans.skillindia.LoginActivity;
import com.siaans.skillindia.NewPassActivity;
import com.siaans.skillindia.R;
import com.siaans.skillindia.fragment.AttendanceFragment;
import com.siaans.skillindia.fragment.CertificateFragment;
import com.siaans.skillindia.fragment.HomeFragment;
import com.siaans.skillindia.fragment.JobFragment;
import com.siaans.skillindia.fragment.NewsFragment;
import com.siaans.skillindia.fragment.ProfileFragment;
import com.siaans.skillindia.fragment.ReviewsFragment;
import com.siaans.skillindia.fragment.WebinarsFragment;
import com.siaans.skillindia.other.CircleTransform;

import org.json.JSONException;
import org.json.JSONObject;

public class TraineeNavActivity extends AppCompatActivity {
    public SharedPreferences.Editor loginPrefsEditor;
    public  SharedPreferences loginPreferences;
    private String saveLogin,Type,email;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private ImageView imgNavHeaderBg;// imgProfile;
    private TextView txtName, txtWebsite;
    private Toolbar toolbar;
    private FloatingActionButton fab;


    public static int navItemIndex = 0;

    private static final String TAG_NEWS = "news";
    private static final String TAG_WEBINAR="webinar";
    private static final String TAG_JOB="job";
    private static final String TAG_CERTIFICATE="certificate";
    private static final String TAG_PROFILE="profile";
    public static String CURRENT_TAG = TAG_PROFILE;

    private static final String urlNavHeaderBg = "https://api.androidhive.info/images/nav-menu-header-bg.jpg";
    //private static final String urlProfileImg = "https://lh3.googleusercontent.com/eCtE_G34M9ygdkmOpYvCag1vBARCmZwnVS6rS5t4JLzJ6QgQSBquM0nuTsCpLhYbKljoyS-txg";

    private String[] activityTitles;

    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainee_nav);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mHandler = new Handler();
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        navHeader = navigationView.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(R.id.name);
        txtWebsite = (TextView) navHeader.findViewById(R.id.website);
        imgNavHeaderBg = (ImageView) navHeader.findViewById(R.id.img_header_bg);
        //imgProfile = (ImageView) navHeader.findViewById(R.id.img_profile);
        activityTitles = getResources().getStringArray(R.array.nav_item_trainee_activity_titles);
        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();
        Type=loginPreferences.getString("lgn","");
        saveLogin = loginPreferences.getString("Trainee","");
        email=loginPreferences.getString("username","");
        Log.d("'", "onCreate: "+saveLogin);

//        Intent i = getIntent();
//        b = i.getExtras();
//        String s=b.getString(
//                "json"
//        );

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogue();
            }
        });

        loadNavHeader();
        setUpNavigationView();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_PROFILE;
            loadHomeFragment();
        }

    }
    public void openDialogue(){
        ExDialog exDialog=new ExDialog();
        exDialog.show(getSupportFragmentManager(),"example dialog");
    }
//    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    private void loadNavHeader() {
        // name, website

        JSONObject jo= null;
        try {
            jo = new JSONObject(saveLogin);
            txtName.setText(jo.getString("name"));
            txtWebsite.setText(jo.getString("emailid"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // loading header background image
        Glide.with(this).load(urlNavHeaderBg)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgNavHeaderBg);

        // Loading profile image
//        Glide.with(this).load(urlProfileImg)
//                .crossFade()
//                .thumbnail(0.5f)
//                .bitmapTransform(new CircleTransform(this))
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .into(imgProfile);

        // showing dot next to notifications label
        navigationView.getMenu().getItem(1).setActionView(R.layout.menu_dot);
    }
    private void loadHomeFragment() {
        selectNavMenu();
        setToolbarTitle();

        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();

            // show or hide the fab button
            toggleFab();
            return;
        }
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                if(fragment!=null) {
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                            android.R.anim.fade_out);
                    fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                    fragmentTransaction.commitAllowingStateLoss();
                }
            }
        };
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }
        toggleFab();
        drawer.closeDrawers();
        invalidateOptionsMenu();
    }
    private Fragment getHomeFragment() {
        Bundle b=new Bundle();
        switch (navItemIndex) {
            case 0:
                // home
                ProfileFragment profileFragment = new ProfileFragment();
                   b.putString("json",saveLogin);
                   b.putString("type",Type);
                profileFragment.setArguments(b);
                return profileFragment;
            case 1:
                NewsFragment newsFragment = new NewsFragment();
                return newsFragment;
            case 2:
                WebinarsFragment webinarsFragment = new WebinarsFragment();
                return webinarsFragment;
            case 3:
                JobFragment jobFragment = new JobFragment();
                return jobFragment;
            case 4:
                CertificateFragment certificatesFragment= new CertificateFragment();
                return certificatesFragment;
            default:
                ProfileFragment profileFragment1 = new ProfileFragment();
                b.putString("json",saveLogin);
                profileFragment1.setArguments(b);
                return profileFragment1;
        }
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void setUpNavigationView() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_Profile:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_PROFILE;
                        break;
                    case R.id.nav_news:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_NEWS;
                        break;
                    case R.id.nav_Webinars:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_WEBINAR;
                        break;
                    case R.id.nav_job:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_JOB;
                    case R.id.nav_Certificates:
                        navItemIndex = 4;
                        CURRENT_TAG = TAG_CERTIFICATE;
                        break;

                    default:
                        navItemIndex = 0;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                loadHomeFragment();

                return true;
            }
        });

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawer.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }
        if (shouldLoadHomeFragOnBackPress) {
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_PROFILE;
                loadHomeFragment();
                return;
            }
        }

        super.onBackPressed();
    }


    private void toggleFab() {
        if (navItemIndex == 0)
            fab.show();
        else
            fab.hide();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (navItemIndex == 0) {
            getMenuInflater().inflate(R.menu.main, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            SharedPreferences preferences =getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("saveLogin", false);
            editor.putString("username", "");
            editor.putString("password", "");
            editor.putString("lgn","");
            editor.putString("Trainee","");
            editor.clear();
            editor.commit();
            finish();

            Toast.makeText(getApplicationContext(), "Logout user!", Toast.LENGTH_LONG).show();
            Intent intent=new Intent(TraineeNavActivity.this, NavigationActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
