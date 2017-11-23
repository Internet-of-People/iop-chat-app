package chat.libertaria.world.connect_chat.chat.contact_list;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import chat.libertaria.world.connect_chat.R;
import chat.libertaria.world.connect_chat.base.BaseActivity;
import chat.libertaria.world.connect_chat.chat.settings.ChangeProfileActivity;
import world.libertaria.shared.library.util.OpenApplicationsUtil;

/**
 * Created by Neoperol on 7/3/17.
 */

public class ChatContactActivity extends BaseActivity {

    private static final int OPTION_ADD_CONTACT = 0;
    private static final int OPTION_SELECTED_PROFILE = 1;
    private static final int OPTION_CHANGE_PROFILE = 2;
    private static final int DEFAULT_GROUP = 0;


    private View root;
    private ViewPager viewPager;
    private Menu menu;

    private boolean showMenu;

    @Override
    protected void onCreateView(Bundle savedInstanceState, ViewGroup container) {
        super.onCreateView(savedInstanceState, container);
        root = getLayoutInflater().inflate(R.layout.chat_contacts_activity, container);
        setTitle("Contact chat");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2053A1")));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#2053A1"));
        }
        viewPager = (ViewPager) root.findViewById(R.id.viewpager);
        setupViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ContactsFragment(), "Contacts");
        viewPager.setAdapter(adapter);
    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        this.menu = menu;
        MenuItem menuAdd = menu.add(DEFAULT_GROUP, OPTION_ADD_CONTACT, Menu.NONE, R.string.add_contact).setIcon(R.drawable.ic_add_acontact);
        menuAdd.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        MenuItem menuMyProfile = menu.add(DEFAULT_GROUP, OPTION_SELECTED_PROFILE, Menu.NONE, R.string.my_profile);
        menuMyProfile.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        MenuItem menuOptions = menu.add(DEFAULT_GROUP, OPTION_CHANGE_PROFILE, Menu.NONE, R.string.change_profile);
        menuOptions.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case OPTION_ADD_CONTACT:
                // Add contacts
                OpenApplicationsUtil.openSendRequestScreen(this);
                return true;
            case OPTION_SELECTED_PROFILE:
                OpenApplicationsUtil.openMyProfileScreen(this, selectedProfPubKey);
                break;
            case OPTION_CHANGE_PROFILE:
                // Open settings
                Intent myIntent = new Intent(this, ChangeProfileActivity.class);
                startActivity(myIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
    }

    @Override
    protected void onServiceDisconnected() {
        super.onServiceDisconnected();
    }

    private void menuVisible(boolean visible) {
        if (menu != null) {
            menu.setGroupVisible(DEFAULT_GROUP, visible);
        }
    }
}
