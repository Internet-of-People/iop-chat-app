package chat.libertaria.world.connect_chat.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;

import org.libertaria.world.services.chat.ChatModule;
import org.libertaria.world.services.interfaces.PairingModule;
import org.libertaria.world.services.interfaces.ProfilesModule;

import chat.libertaria.world.connect_chat.ChatApp;

import static chat.libertaria.world.connect_chat.ChatApp.INTENT_SERVICE_CONNECTED;

/**
 * Created by furszy on 8/11/17.
 */

public class BaseAppFragment extends Fragment {

    protected ChatApp app;
    protected String selectedProfilePubKey;
    protected PairingModule pairingModule;
    protected ChatModule chatModule;
    protected ProfilesModule profilesModule;
    private LocalBroadcastManager localBroadcastManager;

    private BroadcastReceiver appReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("New intent received! " + intent);
            if (intent.getAction().equals(INTENT_SERVICE_CONNECTED)) {
                loadBasics();
                onServiceConnected();
            }
        }
    };


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadBasics();
        localBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
        getActivity().registerReceiver(appReceiver, new IntentFilter(INTENT_SERVICE_CONNECTED));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(appReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadBasics();
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
    protected void onServiceConnected() {

    }
}
