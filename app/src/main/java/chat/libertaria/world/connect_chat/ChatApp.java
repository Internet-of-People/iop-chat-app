package chat.libertaria.world.connect_chat;


import android.app.NotificationManager;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.snappydb.SnappydbException;

import org.libertaria.world.profile_server.ProfileInformation;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

import chat.libertaria.world.connect_chat.chat.store.RemoteProfilesStore;
import world.libertaria.sdk.android.client.ConnectApp;
import world.libertaria.sdk.android.client.ConnectClientService;

/**
 * Created by furszy on 8/2/17.
 */

public class ChatApp extends ConnectApp {

    public static final String INTENT_SERVICE_CONNECTED = "service_connected";
    public static final String INTENT_PROFILE_NOT_EXIST_ON_THE_PLATFORM = "profile_not_exist_on_the_platform";

    public static final String INTENT_ACTION_PROFILE_CONNECTED = "profile_connected";
    public static final String INTENT_ACTION_PROFILE_CHECK_IN_FAIL = "profile_check_in_fail";
    public static final String INTENT_ACTION_PROFILE_DISCONNECTED = "profile_disconnected";

    public static final String INTENT_EXTRA_ERROR_DETAIL = "error_detail";

    public static final String INTENT_CHAT_ACCEPTED_BROADCAST = "chat_accepted";
    public static final String INTENT_CHAT_REFUSED_BROADCAST = "chat_refused";
    public static final String INTENT_CHAT_TEXT_BROADCAST = "chat_text";
    public static final String INTENT_CHAT_TEXT_RECEIVED = "text";

    /**
     * Preferences
     */
    public static final String PREFS_NAME = "app_prefs";

    private NotificationManager notificationManager;
    private RemoteProfilesStore remoteProfilesStore;

    private static volatile ChatApp instance;

    /**
     * Pub key of the selected profile
     */
    private String selectedProfilePubKey;
    private AppConf appConf;
    private AtomicBoolean existProfile = new AtomicBoolean(true);

    public static ChatApp getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        appConf = new AppConf(getSharedPreferences(PREFS_NAME, 0));
        selectedProfilePubKey = appConf.getSelectedProfPubKey();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        try {
            remoteProfilesStore = new RemoteProfilesStore(getDirPrivateMode("remotes_profiles_store").getAbsolutePath());
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
    }

    public File getDirPrivateMode(String name) {
        return getDir(name, MODE_PRIVATE);
    }

    @Override
    protected void onConnectClientServiceBind(ConnectClientService clientService) {
        super.onConnectClientServiceBind(clientService);
        Log.i("ChatApp", "onConnectClientServiceBind");

        if (isClientServiceBound() && isConnectedToPlatform()) {
            try {
                // check if the profile is the same that we have saved here or the user have to select one of the list.
                ProfileInformation selectedProfile = getProfilesModule().getProfile(appConf.getSelectedProfPubKey());
                if (selectedProfile == null) {
                    existProfile.set(false);
                } else {
                    existProfile.set(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            Intent intent = new Intent(INTENT_SERVICE_CONNECTED);
            broadcastManager.sendBroadcast(intent);
        }
    }

    @Override
    protected void onConnectClientServiceUnbind() {
        super.onConnectClientServiceUnbind();
    }


    public String getSelectedProfilePubKey() {
        return selectedProfilePubKey;
    }

    public void cancelChatNotifications() {
        notificationManager.cancel(43);
    }

    public LocalBroadcastManager getBroadcastManager() {
        return broadcastManager;
    }

    public NotificationManager getNotificationManager() {
        return notificationManager;
    }

    public void setSelectedProfile(String selectedProfile) {
        // check if profile exists
        if (getProfilesModule().getProfile(selectedProfile) != null) {
            existProfile.set(true);
        }
        this.selectedProfilePubKey = selectedProfile;
        appConf.setSelectedProfPubKey(selectedProfile);
    }

    public AppConf getAppConf() {
        return appConf;
    }

    public RemoteProfilesStore getRemoteProfilesStore() {
        return remoteProfilesStore;
    }

    public boolean getExistProfile() {
        return existProfile.get();
    }
}
