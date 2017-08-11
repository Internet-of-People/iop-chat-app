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

/**
 * Created by Neoperol on 7/3/17.
 */

public class ChatContactActivity extends BaseActivity {
    private View root;
    private ViewPager viewPager;
    @Override
    protected void onCreateView(Bundle savedInstanceState, ViewGroup container) {
        root = getLayoutInflater().inflate(R.layout.chat_contacts_activity, container);
        setTitle("Contact chat");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#21619C")));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#21619C"));
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
        MenuItem menuAdd = menu.add(0, 0, Menu.NONE, R.string.next).setIcon(R.drawable.ic_add_acontact);
        menuAdd.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        MenuItem menuOptions = menu.add(0, 1, Menu.NONE, R.string.next).setIcon(R.drawable.ic_options);
        menuOptions.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case 0:
                // Add contacts
                return true;
            case 1:
                // Open settings
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



}
