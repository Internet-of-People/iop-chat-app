package chat.libertaria.world.connect_chat.chat.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import chat.libertaria.world.connect_chat.R;
import chat.libertaria.world.connect_chat.base.BaseActivity;

/**
 * Created by Neoperol on 8/15/17.
 */

public class ChangeProfile extends BaseActivity {
    private static final int OPTIONS_SAVE = 1;
    private Spinner spinner;
    private TextView txt_no_profile;
    private Button btn_open_app;
    private RelativeLayout separator;

    @Override
    protected void onCreateView(Bundle savedInstanceState, ViewGroup container) {
        getLayoutInflater().inflate(R.layout.tutorial_slide2, container);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Change Profile");

        btn_open_app = (Button) findViewById(R.id.btn_open_app);
        btn_open_app.setVisibility(View.GONE);
        txt_no_profile = (TextView) findViewById(R.id.no_profile);
        txt_no_profile.setVisibility(View.GONE);
        separator = (RelativeLayout) findViewById(R.id.separator);
        separator.setVisibility(View.GONE);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.add(0,OPTIONS_SAVE,0,R.string.save);
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == OPTIONS_SAVE){
            //Save
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
