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
import android.widget.Toast;

import chat.libertaria.world.connect_chat.R;
import chat.libertaria.world.connect_chat.base.BaseActivity;
import chat.libertaria.world.connect_chat.chat.welcome.MainActivity;
import chat.libertaria.world.connect_chat.chat.welcome.SelectProfileFragment;

/**
 * Created by Neoperol on 8/15/17.
 */

public class ChangeProfileActivity extends BaseActivity {
    private static final int OPTIONS_SAVE = 1;

    private SelectProfileFragment selectProfileFragment;

    @Override
    protected void onCreateView(Bundle savedInstanceState, ViewGroup container) {
        getLayoutInflater().inflate(R.layout.change_profile_main, container);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Change Profile");
        selectProfileFragment = (SelectProfileFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_select_profiles);
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
            if(checkSelectedProfile()){
                onBackPressed();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean checkSelectedProfile() {
        // try to get the profile
        String profPubKey = selectProfileFragment.getSelectedProfileKey();
        if (profPubKey!=null){
            app.setSelectedProfile(profPubKey);
            return true;
        }else {
            Toast.makeText(this, "Please choose a profile to continue", Toast.LENGTH_LONG).show();
            return false;
        }
    }
}
