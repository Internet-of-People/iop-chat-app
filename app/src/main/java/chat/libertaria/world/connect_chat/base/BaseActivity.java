package chat.libertaria.world.connect_chat.base;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.libertaria.world.services.chat.ChatModule;
import org.libertaria.world.services.interfaces.PairingModule;
import org.libertaria.world.services.interfaces.ProfilesModule;

import chat.libertaria.world.connect_chat.ChatApp;
import chat.libertaria.world.connect_chat.R;

import static chat.libertaria.world.connect_chat.ChatApp.INTENT_ACTION_PROFILE_CHECK_IN_FAIL;
import static chat.libertaria.world.connect_chat.ChatApp.INTENT_ACTION_PROFILE_CONNECTED;
import static chat.libertaria.world.connect_chat.ChatApp.INTENT_ACTION_PROFILE_DISCONNECTED;
import static chat.libertaria.world.connect_chat.ChatApp.INTENT_EXTRA_ERROR_DETAIL;

/**
 * Created by furszy on 6/5/17.
 */

public class BaseActivity extends AppCompatActivity {

    public static final String NOTIF_DIALOG_EVENT = "nde";

    protected String selectedProfPubKey;

    protected PairingModule pairingModule;
    protected ChatModule chatModule;
    protected ProfilesModule profilesModule;
    protected ChatApp app;

    protected LocalBroadcastManager localBroadcastManager;
    protected NotificationManager notificationManager;
    private NotifReceiver notifReceiver;

    protected Toolbar toolbar;
    protected FrameLayout childContainer;
    protected LinearLayout btnReload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            this.localBroadcastManager = LocalBroadcastManager.getInstance(this);
            this.notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            this.notifReceiver = new NotifReceiver();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            setContentView(R.layout.activity_base);
            loadBasics();
            init();
            // onCreateChildMethod
            onCreateView(savedInstanceState, childContainer);

            //Layout reload
            btnReload = (LinearLayout) findViewById(R.id.btnReload);
            btnReload.setVisibility(LinearLayout.GONE);
            btnReload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // nothing yet
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    protected void loadBasics(){
        app = ChatApp.getInstance();
        pairingModule = app.getPairingModule();
        chatModule = app.getChatModule();
        profilesModule = app.getProfilesModule();
        selectedProfPubKey = app.getSelectedProfilePubKey();
    }

    private void init(){
        childContainer = (FrameLayout) findViewById(R.id.content);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    /**
     * Empty method to override.
     *
     * @param savedInstanceState
     */
    protected void onCreateView(Bundle savedInstanceState, ViewGroup container){

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBasics();
        localBroadcastManager.registerReceiver(notifReceiver, new IntentFilter(NOTIF_DIALOG_EVENT));
        localBroadcastManager.registerReceiver(notifReceiver, new IntentFilter(INTENT_ACTION_PROFILE_DISCONNECTED));
        localBroadcastManager.registerReceiver(notifReceiver,new IntentFilter(INTENT_ACTION_PROFILE_CHECK_IN_FAIL));
        localBroadcastManager.registerReceiver(notifReceiver,new IntentFilter(INTENT_ACTION_PROFILE_CONNECTED));
    }

    @Override
    protected void onStop() {
        try {
            super.onStop();
            localBroadcastManager.unregisterReceiver(notifReceiver);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private class NotifReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("BaseActivity","onReceive");
            String action = intent.getAction();
            if (action.equals(INTENT_ACTION_PROFILE_CONNECTED)){
                hideConnectionLoose();
            }else if (action.equals(INTENT_ACTION_PROFILE_DISCONNECTED)){
                showConnectionLoose();
            }else if(action.equals(INTENT_ACTION_PROFILE_CHECK_IN_FAIL)){
                // todo: here i should add some error handling and use the "detail" field...
                String detail = intent.getStringExtra(INTENT_EXTRA_ERROR_DETAIL);
                Log.e("BaseActivity","check in fail: "+detail);
                Toast.makeText(BaseActivity.this,"Check in fail, "+detail,Toast.LENGTH_LONG).show();
                showConnectionLoose();
            }
        }
    }

    protected boolean checkPermission(String permission) {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),permission);
        return result == PackageManager.PERMISSION_GRANTED;
    }


    public void hideConnectionLoose(){
        btnReload.setVisibility(View.GONE);
    }

    public void showConnectionLoose(){
        btnReload.setVisibility(View.VISIBLE);
    }


}