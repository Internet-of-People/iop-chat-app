package chat.libertaria.world.connect_chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import chat.libertaria.world.connect_chat.chat.contact_list.ChatContactActivity;

import static chat.libertaria.world.connect_chat.R.id.text;

/**
 * Created by furszy on 8/10/17.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int OPTION_ADD_NODE = 300;

    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private Spinner spinner;
    private int[] layouts;
    private Button btnNext, btn_open_app;
    private TextView txt_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.tutorial_activity);

        spinner = (Spinner)findViewById(R.id.spinner);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        btnNext = (Button) findViewById(R.id.btn_next);

        txt_title = (TextView) findViewById(R.id.tutorial_title);

        btn_open_app = (Button) findViewById(R.id.btn_open_app);

        layouts = new int[]{
                R.layout.tutorial_slide1,
                R.layout.tutorial_slide2,
                R.layout.tutorial_slide3};

        viewPagerAdapter = new ViewPagerAdapter();
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
            launchHomeScreen();
        }
    }


    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {

            if (position == 0 ) {
                btnNext.setText(getString(R.string.next));
                txt_title.setText(getString(R.string.welcome));
            }

            else if ( position == 1) {
                btnNext.setText(getString(R.string.select_profile));
                txt_title.setText(getString(R.string.select_profile_title));
            }

            else {
                btnNext.setText(getString(R.string.start));
                txt_title.setText(getString(R.string.start_chatting));
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

    public class ViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;


        public ViewPagerAdapter() {

        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            return view;

        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }

}
