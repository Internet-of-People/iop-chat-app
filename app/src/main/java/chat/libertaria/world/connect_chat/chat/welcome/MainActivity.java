package chat.libertaria.world.connect_chat.chat.welcome;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import chat.libertaria.world.connect_chat.R;
import chat.libertaria.world.connect_chat.base.BaseActivity;
import chat.libertaria.world.connect_chat.chat.contact_list.ChatContactActivity;
import chat.libertaria.world.connect_chat.utils.CustomFontTextView;

import static chat.libertaria.world.connect_chat.R.id.text;

/**
 * Created by furszy on 8/10/17.
 */

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static final int OPTION_ADD_NODE = 300;

    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private Spinner spinner;
    private int[] layouts;
    private Button btnNext, btn_open_app;
    private CustomFontTextView txt_title;


    @Override
    protected void onCreateView(Bundle savedInstanceState, ViewGroup container) {
        getLayoutInflater().inflate(R.layout.tutorial_activity,container,true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().hide();
        spinner = (Spinner)findViewById(R.id.spinner);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        btnNext = (Button) findViewById(R.id.btn_next);

        txt_title = (CustomFontTextView) findViewById(R.id.tutorial_title);

        btn_open_app = (Button) findViewById(R.id.btn_open_app);

        layouts = new int[]{
                R.layout.tutorial_slide1,
                R.layout.tutorial_slide2,
                R.layout.tutorial_slide3};

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new WelcomeScreenFragment());
        viewPagerAdapter.addFragment(new SelectProfileFragment());
        viewPagerAdapter.addFragment(new StartFragment());
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);


    }


    public  void btnNextClick(View v)
    {
        int current = getItem(1);
        if (current < layouts.length) {
            // move to next screen
            viewPager.setCurrentItem(current);
        } else {
            // save profile and init app
            app.getAppConf().setAppInit();
            launchHomeScreen();
        }
    }


    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            try {
                if (position == 0) {
                    btnNext.setText(getString(R.string.next));
                    txt_title.setText(getString(R.string.welcome));
                } else if (position == 1) {
                    btnNext.setText(getString(R.string.select_profile));
                    txt_title.setText(getString(R.string.select_profile_title));
                } else {
                    if (app.getSelectedProfilePubKey() == null) {
                        // try to get the profile
                        String profPubKey = ((SelectProfileFragment) viewPagerAdapter.getItem(1)).getSelectedProfileKey();
                        if (profPubKey!=null){
                            app.setSelectedProfile(profPubKey);
                        }else {
                            viewPager.setCurrentItem(1);
                            Toast.makeText(MainActivity.this, "Please choose a profile to continue", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                    btnNext.setText(getString(R.string.start));
                    txt_title.setText(getString(R.string.start_chatting));
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };




    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    private void launchHomeScreen() {
        startActivity(new Intent(this, ChatContactActivity.class));
        finish();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();

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

        public void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }
    }

    public static class WelcomeScreenFragment extends Fragment{
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.tutorial_slide1,container,false);
        }
    }

    public static class StartFragment extends Fragment{
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.tutorial_slide3,container,false);
        }
    }


}
