package chat.libertaria.world.connect_chat.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;

import org.libertaria.world.services.chat.ChatModule;
import org.libertaria.world.services.interfaces.PairingModule;
import org.libertaria.world.services.interfaces.ProfilesModule;

import chat.libertaria.world.connect_chat.ChatApp;
import tech.furszy.ui.lib.base.RecyclerFragment;

import static chat.libertaria.world.connect_chat.ChatApp.INTENT_SERVICE_CONNECTED;

/**
 * Created by furszy on 8/10/17.
 */

public abstract class BaseAppRecyclerFragment<T> extends RecyclerFragment<T> {

    protected ChatApp app;
    protected String selectedProfilePubKey;
    protected PairingModule pairingModule;
    protected ChatModule chatModule;
    protected ProfilesModule profilesModule;

    private BroadcastReceiver appReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(INTENT_SERVICE_CONNECTED)){
                loadBasics();
                onServiceConnected();
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadBasics();
        localBroadcastManager.registerReceiver(appReceiver,new IntentFilter(INTENT_SERVICE_CONNECTED));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(appReceiver);
    }

    public void loadBasics() {
        app = ChatApp.getInstance();
        pairingModule = app.getPairingModule();
        chatModule = app.getChatModule();
        profilesModule = app.getProfilesModule();
        selectedProfilePubKey = app.getSelectedProfilePubKey();
    }

    /**
     * Method to override
     */
    protected void onServiceConnected(){

    }
}