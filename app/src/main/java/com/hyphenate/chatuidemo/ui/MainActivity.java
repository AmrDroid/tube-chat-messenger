package com.hyphenate.chatuidemo.ui;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.RateThisApp;
import com.hyphenate.Setting;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chatuidemo.CallFragment;
import com.hyphenate.chatuidemo.Constant;
import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.apply.ApplyActivity;
import com.hyphenate.chatuidemo.chat.ConversationListFragment;
import com.hyphenate.chatuidemo.chatroom.ChatRoomListActivity;
import com.hyphenate.chatuidemo.group.GroupChangeListener;
import com.hyphenate.chatuidemo.group.GroupListActivity;
import com.hyphenate.chatuidemo.group.InviteMembersActivity;
import com.hyphenate.chatuidemo.group.NewGroupActivity;
import com.hyphenate.chatuidemo.group.PublicGroupsListActivity;
import com.hyphenate.chatuidemo.runtimepermissions.PermissionsManager;
import com.hyphenate.chatuidemo.runtimepermissions.PermissionsResultAction;
import com.hyphenate.chatuidemo.settings.AccountActivity;
import com.hyphenate.chatuidemo.settings.BlackListActivity;
import com.hyphenate.chatuidemo.settings.SettingsFragment;
import com.hyphenate.chatuidemo.sign.SignInActivity;
import com.hyphenate.chatuidemo.user.AddContactsActivity;
import com.hyphenate.chatuidemo.user.ContactListFragment;
import com.hyphenate.chatuidemo.user.ContactsChangeListener;
import com.hyphenate.util.EMLog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by wei on 2016/9/27.
 * The main activity of demo app
 */
public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener{
    private static final String TAG = MainActivity.class.getSimpleName();
    @BindView(R.id.tab_layout) TabLayout mTabLayout;
    @BindView(R.id.view_pager) ViewPager mViewPager;


    private DrawerLayout drawer;
    private FloatingActionButton fab;
    private RelativeLayout relative_main;
    private ImageView img_page_start;

    private static boolean isShowPageStart = true;
    private final int MESSAGE_SHOW_DRAWER_LAYOUT = 0x001;
    private final int MESSAGE_SHOW_START_PAGE = 0x002;







    private int mCurrentPageIndex = 0;

    private ConversationListFragment mConversationListFragment;
    private ContactListFragment mContactListFragment;
    private SettingsFragment mSettingsFragment;
    private CallFragment mCallFragment;

    private DefaultContactsChangeListener mContactListener;
    private DefaultGroupChangeListener mGroupListener;
    private InterstitialAd mInterstitialAd;
    private void requestNewInterstitial() {
        AdRequest adRequest =new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);
    }
    private AdView mAdView;


    @Override protected void onCreate(Bundle savedInstanceState) {
        // Set default setting values
        //PreferenceManager.setDefaultValues(this, R.xml.preferences_default, false);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.em_activity_main);




        MobileAds.initialize(this, "ca-app-pub-8197945977485556~7027742045");

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-8197945977485556/7825818399");

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
             //   beginPlayingGame();
            }
        });
        requestNewInterstitial();


        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
            Log.i("MYADD","DONE");
        } else {
Log.i("MYADD","NOTLOADED");        }








        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);








        android.preference.PreferenceManager.setDefaultValues(this, R.xml.preferences_default,
                false);

        getSupportActionBar().setTitle("الدردشات");

        mContactListener = new DefaultContactsChangeListener();
        mGroupListener = new DefaultGroupChangeListener();

        ButterKnife.bind(this);

        // runtime permission for android 6.0, just require all permissions here for simple
        if(Build.VERSION.SDK_INT >= 23) {
            requestPermissions();
        }

        //setup viewpager
        setupViewPager();
        //setup tabLayout with viewpager
        setupTabLayout();






//        try {
//            PreferenceManager.setDefaultValues(this, R.xml.preferences_settings, false);
//        } catch (Exception e) {
//        }

//        SharedPreferences sharedPreferences = getSharedPreferences("app", MODE_PRIVATE);
//
//        if (isShowPageStart) {
//            relative_main.setVisibility(View.VISIBLE);
//            Glide.with(MainActivity.this).load(R.drawable.ic_launcher_big).into(img_page_start);
//            if (sharedPreferences.getBoolean("isFirst", true)) {
//                mHandler.sendEmptyMessageDelayed(MESSAGE_SHOW_START_PAGE, 2000);
//            } else {
//                mHandler.sendEmptyMessageDelayed(MESSAGE_SHOW_START_PAGE, 1000);
//            }
//            isShowPageStart = false;
//        }
//
//        if (sharedPreferences.getBoolean("isFirst", true)) {
//            mHandler.sendEmptyMessageDelayed(MESSAGE_SHOW_DRAWER_LAYOUT, 2500);
//        }






        // Set custom criteria (optional)
        RateThisApp.init(new RateThisApp.Config(3, 5));

        // Set callback (optional)
        RateThisApp.setCallback(new RateThisApp.Callback() {
            @Override
            public void onYesClicked() {
                //    Toast.makeText(MainActivity.this, "Yes event", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNoClicked() {
                //  Toast.makeText(MainActivity.this, "No event", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelClicked() {
                //Toast.makeText(MainActivity.this, "Cancel event", Toast.LENGTH_SHORT).show();
            }
        });

        /*
        // Set custom title and message
        RateThisApp.Config config = new RateThisApp.Config(3, 5);
        config.setTitle(R.string.hello_world);
        config.setMessage(R.string.hello_world);
        RateThisApp.init(config);
        */

        //        Button button1 = (Button) findViewById(R.id.button1);
//        button1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Show rating dialog explicitly.
//                RateThisApp.showRateDialog(MainActivity.this);
//            }
//        });
//
//        Button button2 = (Button) findViewById(R.id.button2);
//        button2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Show rating dialog explicitly.
//            }
//        });
//
//        Button button3 = (Button) findViewById(R.id.button3);
//        button3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                RateThisApp.stopRateDialog(MainActivity.this);
//            }
//        });
        RateThisApp.showRateDialog(MainActivity.this, R.style.MyAlertDialogStyle2);


        // Monitor launch times and interval from installation
        RateThisApp.onCreate(this);
        // Show a dialog if criteria is satisfied
        RateThisApp.showRateDialogIfNeeded(this);



















    }
    Drawable drawable;
    ImageView imageView_nav_header;
    View headerView;
    private void setupViewPager() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

         headerView = navigationView.getHeaderView(0);


        if (AccountActivity.photo != null)
        {
            drawable = new BitmapDrawable(getResources(), AccountActivity.photo);

            imageView_nav_header = (ImageView) headerView.findViewById(R.id.imageView_nav_header);
            imageView_nav_header.setImageDrawable(drawable);
        }


        LinearLayout nav_header = (LinearLayout) headerView.findViewById(R.id.nav_header);
        nav_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//                startActivity(intent);
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }
        });





        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        mContactListFragment = ContactListFragment.newInstance();
        mConversationListFragment = ConversationListFragment.newInstance();
        mSettingsFragment = SettingsFragment.newInstance();
        mCallFragment=CallFragment.newInstance();
        //add fragments to adapter
        adapter.addFragment(mConversationListFragment, getString(R.string.title_chats));

        adapter.addFragment(mContactListFragment, getString(R.string.title_contacts));

        adapter.addFragment(mSettingsFragment, "الاعدادات");
adapter.addFragment(mCallFragment,"مكالمات");
        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override public void onPageSelected(int position) {
                mCurrentPageIndex = position;
                Toolbar toolbar = getActionBarToolbar();
                toolbar.setTitle(adapter.getPageTitle(position));
                toolbar.getMenu().clear();
                if (position == 0) { //Contacts
                    toolbar.inflateMenu(R.menu.em_conversations_menu);
                } else if (position == 1) { //Chats
                    toolbar.inflateMenu(R.menu.em_contacts_menu);
                    mTabLayout.getTabAt(0).getCustomView().findViewById(R.id.img_tab_item);
                }
                if (position != 2) {
                    setSearchViewQueryListener();
                }
            }

            @Override public void onPageScrolled(int position, float positionOffset,
                    int positionOffsetPixels) {
            }

            @Override public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void setupTabLayout() {
        mTabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.tab_indicator));
        mTabLayout.setupWithViewPager(mViewPager);
        //        mTabLayout.setSelectedTabIndicatorHeight(R.dimen.tab_indicator_height);
        for (int i = 0; i < 4; i++) {
            View customTab = LayoutInflater.from(this).inflate(R.layout.em_tab_layout_item, null);
            ImageView imageView = (ImageView) customTab.findViewById(R.id.img_tab_item);
            if (i == 0) {
                imageView.setImageDrawable(
                        getResources().getDrawable(R.drawable.em_tab_chats_selector));
            } else if (i == 1) {
                imageView.setImageDrawable(
                        getResources().getDrawable(R.drawable.em_tab_contacts_selector));

            } else if (i == 2) {
                imageView.setImageDrawable(
                        getResources().getDrawable(R.drawable.em_ic_tab_settings));

            }

            else {
                imageView.setImageDrawable(
                        getResources().getDrawable(R.drawable.em_ic_action_voice_call));
            }
            //set the custom tabview
            mTabLayout.getTabAt(i).setCustomView(customTab);
        }
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        //add the action buttons to toolbar
        Toolbar toolbar = getActionBarToolbar();
        if (mViewPager.getCurrentItem() == 0) {
            toolbar.inflateMenu(R.menu.em_conversations_menu);
            setSearchViewQueryListener();
        } else if (mViewPager.getCurrentItem() == 1) {
            toolbar.inflateMenu(R.menu.em_contacts_menu);
            setSearchViewQueryListener();

        }
        toolbar.setOnMenuItemClickListener(new ToolBarMenuItemClickListener());
        return true;
    }
    public static Bitmap ph;
    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        // / if (AccountActivity.photo != null)



        {


if(AccountActivity.photo!=null) {
    ph = AccountActivity.photo;
    drawable = new BitmapDrawable(getResources(), ph);

    imageView_nav_header = (ImageView) findViewById(R.id.imageView_nav_header);
    imageView_nav_header.setImageDrawable(drawable);
}


        }
        Bitmap a= loadImageFromStorage("imageDir/am.png");
        if(a!=null)
        {
            drawable = new BitmapDrawable(getResources(), a);
            imageView_nav_header.setImageDrawable(drawable);
        }

    }










    private Bitmap loadImageFromStorage(String path)
    {

        try {
            File f=new File(path, "am.png");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
          //  ImageView img=(ImageView)findViewById(R.id.imgPicker);
          return  b;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;

    }




    private void setSearchViewQueryListener() {
        Toolbar toolbar = getActionBarToolbar();

        SearchView searchView;
        if (mCurrentPageIndex == 0) {
            searchView = (SearchView) MenuItemCompat.getActionView(
                    toolbar.getMenu().findItem(R.id.menu_conversations_search));
            // search conversations list
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override public boolean onQueryTextSubmit(String query) {
                    mConversationListFragment.filter(query);
                    return true;
                }

                @Override public boolean onQueryTextChange(String newText) {
                    mConversationListFragment.filter(newText);
                    return true;
                }
            });

               } else if (mCurrentPageIndex == 1) {
            searchView = (SearchView) toolbar.getMenu().findItem(R.id.menu_search).getActionView();
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override public boolean onQueryTextSubmit(String query) {
                    return true;
                }

                @Override public boolean onQueryTextChange(String newText) {
                    mContactListFragment.filter(newText);
                    return true;
                }
            });

        }
    }

    /**
     * Toolbar menu item onclick listener
     */
    private class ToolBarMenuItemClickListener implements Toolbar.OnMenuItemClickListener {

        @Override public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {

                case R.id.menu_create_group:

                    startActivity(new Intent(MainActivity.this, InviteMembersActivity.class));
                    break;

                case R.id.menu_public_groups:
                    startActivity(new Intent(MainActivity.this, PublicGroupsListActivity.class));
                    break;
                case R.id.menu_add_contacts:
                    startActivity(new Intent(MainActivity.this, AddContactsActivity.class));
                    break;
            }

            return false;
        }
    }

    /**
     * message listener
     */
    EMMessageListener mMessageListener = new EMMessageListener() {
        @Override public void onMessageReceived(List<EMMessage> list) {
            Fragment fragment = null;
            //display unread tips
            if (EMClient.getInstance().chatManager().getUnreadMessageCount() > 0) {
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        getTabUnreadStatusView(1).setVisibility(View.VISIBLE);
                    }
                });
            }
            //refresh ConversationListFragment
            fragment = ((PagerAdapter) mViewPager.getAdapter()).getItem(1);
            ((ConversationListFragment) fragment).refresh();
        }

        @Override public void onCmdMessageReceived(List<EMMessage> list) {
        }

        @Override public void onMessageRead(List<EMMessage> list) {
        }

        @Override public void onMessageDelivered(List<EMMessage> list) {
        }

        @Override public void onMessageChanged(EMMessage emMessage, Object o) {
        }
    };

    private ImageView getTabUnreadStatusView(int index) {
        View tabView = mTabLayout.getTabAt(index).getCustomView();
        ImageView unreadStatusView = (ImageView) tabView.findViewById(R.id.img_unread_status);
        return unreadStatusView;
    }

    @TargetApi(23)
    private void requestPermissions() {
        PermissionsManager.getInstance().requestAllManifestPermissionsIfNecessary(this, new PermissionsResultAction() {
            @Override
            public void onGranted() {
                //				Toast.makeText(MainActivity.this, "All permissions have been granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDenied(String permission) {
                //Toast.makeText(MainActivity.this, "Permission " + permission + " has been denied", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
        requestNewInterstitial();


        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
            Log.i("MYADD","DONE");
        } else {
            Log.i("MYADD","NOTLOADED");        }
    }


    @Override protected void onResume() {
        super.onResume();
        //register message listener
        if (mAdView != null) {
            mAdView.resume();
        }
        requestNewInterstitial();


        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
            Log.i("MYADD","DONE");
        } else {
            Log.i("MYADD","NOTLOADED");        }


        EMClient.getInstance().chatManager().addMessageListener(mMessageListener);
        EMClient.getInstance().contactManager().setContactListener(mContactListener);
        EMClient.getInstance().groupManager().addGroupChangeListener(mGroupListener);

        // Check that you are logged in
        if (EMClient.getInstance().isLoggedInBefore()) {
            // Load the group into memory
            EMClient.getInstance().groupManager().loadAllGroups();
            // Load all mConversation into memory
            EMClient.getInstance().chatManager().loadAllConversations();

            //register message listener
            EMClient.getInstance().chatManager().addMessageListener(mMessageListener);
            EMClient.getInstance().contactManager().setContactListener(mContactListener);
            EMClient.getInstance().groupManager().addGroupChangeListener(mGroupListener);

            updateUnreadMsgLabel();
            refreshApply();
            //refreshContacts();
        }
    }

    @Override protected void onStop() {
        super.onStop();
        //unregister message listener on stop
        EMClient.getInstance().chatManager().removeMessageListener(mMessageListener);
        EMClient.getInstance().contactManager().removeContactListener(mContactListener);
        EMClient.getInstance().groupManager().removeGroupChangeListener(mGroupListener);
    }

    @Override protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getBooleanExtra(Constant.ACCOUNT_CONFLICT, false) && !isConflictDialogShow) {
            showConflictDialog();
        }
    }

    @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
        //will not finish when back key is down
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void updateUnreadMsgLabel() {
        if (EMClient.getInstance().chatManager().getUnreadMessageCount() > 0) {
            getTabUnreadStatusView(1).setVisibility(View.VISIBLE);
        } else {
            getTabUnreadStatusView(1).setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Fragment pager adapter
     */
    private class PagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override public int getCount() {
            return mFragments.size();
        }

        @Override public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }

    private void refreshApply() {
        runOnUiThread(new Runnable() {
            @Override public void run() {
                EMConversation conversation = EMClient.getInstance()
                        .chatManager()
                        .getConversation(Constant.CONVERSATION_NAME_APPLY,
                                EMConversation.EMConversationType.Chat, true);
                if (conversation.getUnreadMsgCount() > 0) {
                    getTabUnreadStatusView(0).setVisibility(View.VISIBLE);
                } else {
                    getTabUnreadStatusView(0).setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    /**
     * refresh the contacts view
     */
    private void refreshContacts() {
        runOnUiThread(new Runnable() {
            @Override public void run() {
                //refresh ContactListFragment
                Fragment fragment = ((PagerAdapter) mViewPager.getAdapter()).getItem(0);
                ((ContactListFragment) fragment).refresh();
            }
        });
    }

    private void refreshConversation() {
        runOnUiThread(new Runnable() {
            @Override public void run() {
                Fragment fragment = ((PagerAdapter) mViewPager.getAdapter()).getItem(1);
                ((ConversationListFragment) fragment).refresh();
            }
        });
    }

    //private boolean isConflict;
    private boolean isConflictDialogShow;

    /**
     * show the dialog when user logged into another device
     */
    private void showConflictDialog() {
        isConflictDialogShow = true;
        DemoHelper.getInstance().signOut(false, null);
        String st = getResources().getString(R.string.Logoff_notification);
        if (!isFinishing()) {
            // clear up global variables
            try {
                AlertDialog.Builder conflictBuilder = new AlertDialog.Builder(this);
                conflictBuilder.setTitle(st);
                conflictBuilder.setMessage(R.string.connect_conflict);
                conflictBuilder.setPositiveButton(R.string.common_ok,
                        new DialogInterface.OnClickListener() {

                            @Override public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
                conflictBuilder.setCancelable(false);
                conflictBuilder.show();
                //isConflict = true;
            } catch (Exception e) {
                EMLog.e(TAG, "---------conflictBuilder error" + e.getMessage());
            }
        }
    }

    private class DefaultContactsChangeListener extends ContactsChangeListener {
        @Override public void onContactAdded(String username) {
            refreshContacts();
        }

        @Override public void onContactDeleted(String username) {
            refreshContacts();
        }

        @Override public void onContactInvited(String username, String reason) {
            refreshApply();
            refreshContacts();
        }

        @Override public void onFriendRequestAccepted(String username) {
            refreshApply();
            refreshContacts();
        }

        @Override public void onFriendRequestDeclined(String username) {
            refreshApply();
            refreshContacts();
        }
    }

    private class DefaultGroupChangeListener extends GroupChangeListener {
        @Override public void onInvitationReceived(String s, String s1, String s2, String s3) {
            refreshApply();
            refreshContacts();
        }

        @Override public void onRequestToJoinReceived(String s, String s1, String s2, String s3) {
            refreshApply();
            refreshContacts();
        }

        @Override public void onRequestToJoinAccepted(String s, String s1, String s2) {
            refreshApply();
            refreshContacts();
        }

        @Override public void onRequestToJoinDeclined(String s, String s1, String s2, String s3) {
            refreshApply();
            refreshContacts();
        }

        @Override public void onInvitationAccepted(String s, String s1, String s2) {
            refreshApply();
            refreshContacts();
        }



        @Override public void onInvitationDeclined(String s, String s1, String s2) {
            refreshApply();
            refreshContacts();
        }

        @Override public void onUserRemoved(String s, String s1) {
            refreshApply();
            refreshContacts();
            refreshConversation();
        }

        @Override public void onGroupDestroyed(String s, String s1) {
            refreshApply();
            refreshContacts();
            refreshConversation();
        }

        @Override public void onAutoAcceptInvitationFromGroup(String s, String s1, String s2) {
            refreshApply();
            refreshContacts();
            refreshConversation();
            runOnUiThread(new Runnable() {
                @Override public void run() {
                    updateUnreadMsgLabel();
                }
            });
        }
    }

    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
    }










    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_SHOW_DRAWER_LAYOUT:
                    drawer.openDrawer(GravityCompat.START);
                    SharedPreferences sharedPreferences = getSharedPreferences("app", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isFirst", false);
                    editor.apply();
                    break;

                case MESSAGE_SHOW_START_PAGE:
                    AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
                    alphaAnimation.setDuration(300);
                    alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            relative_main.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    relative_main.startAnimation(alphaAnimation);
                    break;
            }
        }
    };


























    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_menu_main_1:
//                Intent intent = new Intent(this, AboutActivity.class);
//                startActivity(intent);
//                break;
//        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Intent intent = new Intent();

        switch (item.getItemId()) {
            case R.id.nav_recycler_and_swipe_refresh:
                intent.setClass(this, AccountActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_scrolling:
                try {
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    String shareBody = "Tube Chat Messenger now On Google Play";
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    startActivity(Intent.createChooser(sharingIntent, "Share via"));

                } catch(Exception e) {
                    //e.toString();
                }

                break;

            case R.id.nav_full_screen:
                intent.setClass(this, GroupListActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_bottom_navigation:
                intent.setClass(this, ChatRoomListActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_settings:
                intent.setClass(this, NewGroupActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_about:
                intent.setClass(this, PublicGroupsListActivity.class);
                startActivity(intent);
                break;


            case R.id.nav_donate:
                intent.setClass(this, ApplyActivity.class);
                startActivity(intent);

                break;

            case R.id.black:
                intent.setClass(this,BlackListActivity.class);
                startActivity(intent);

                break;



            case R.id.nav_color:
                final ProgressDialog dialog = new ProgressDialog(this);
                dialog.setMessage(this.getString(R.string.em_hint_loading));
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();

                DemoHelper.getInstance().signOut(true, new EMCallBack() {
                    @Override public void onSuccess() {
                        Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }

                    @Override public void onError(final int i, final String s) {
                        runOnUiThread(new Runnable() {
                            @Override public void run() {
                                dialog.dismiss();
                                Toast.makeText(MainActivity.this, "فشل تسجيل الخروج تاكد من اتصالك بالانترنت",
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    @Override public void onProgress(int i, String s) {

                    }
                });
                break;

//
//            case R.id.nav_color:
//                if (checkAppInstalled(Constant.MATERIAL_DESIGN_COLOR_PACKAGE)) {
//                    intent = getPackageManager().getLaunchIntentForPackage(Constant.MATERIAL_DESIGN_COLOR_PACKAGE);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(intent);
//                } else {
//                    intent.setData(Uri.parse(Constant.MATERIAL_DESIGN_COLOR_URL));
//                    intent.setAction(Intent.ACTION_VIEW);
//                    startActivity(intent);
//                }
           //     break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private boolean checkAppInstalled(String packageName) {
        try {
            getPackageManager().getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }


    @Override
    protected void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }

        super.onDestroy();

    }
}
