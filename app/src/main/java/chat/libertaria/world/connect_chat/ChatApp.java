package chat.libertaria.world.connect_chat;


import android.app.Application;
import android.app.NotificationManager;
import android.support.v4.content.LocalBroadcastManager;

import org.libertaria.world.services.EnabledServices;
import org.libertaria.world.services.chat.ChatModule;
import org.libertaria.world.services.interfaces.PairingModule;
import org.libertaria.world.services.interfaces.ProfilesModule;

import world.libertaria.sdk.android.client.ConnectApp;

/**
 * Created by furszy on 8/2/17.
 */

public class ChatApp extends ConnectApp{

    public static final String INTENT_ACTION_PROFILE_CONNECTED = "profile_connected";
    public static final String INTENT_ACTION_PROFILE_CHECK_IN_FAIL= "profile_check_in_fail";
    public static final String INTENT_ACTION_PROFILE_DISCONNECTED = "profile_disconnected";

    public static final String INTENT_EXTRA_ERROR_DETAIL = "error_detail";

    public static final String INTENT_CHAT_ACCEPTED_BROADCAST = "chat_accepted";
    public static final String INTENT_CHAT_REFUSED_BROADCAST = "chat_refused";
    public static final String INTENT_CHAT_TEXT_BROADCAST = "chat_text";
    public static final String INTENT_CHAT_TEXT_RECEIVED = "text";

    /** Preferences */
    private static final String PREFS_NAME = "app_prefs";

    private NotificationManager notificationManager;

    private static volatile ChatApp instance;

    private ChatModule chatModule;
    private PairingModule pairingModule;
    private ProfilesModule profilesModule;
    /** Pub key of the selected profile */
    private String selectedProfilePubKey;
    private AppConf appConf;
    private ChatModuleReceiver chatModuleReceiver = new ChatModuleReceiver();

    public static ChatApp getInstance(){
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        appConf = new AppConf(getSharedPreferences(PREFS_NAME, 0));
        selectedProfilePubKey = appConf.getSelectedProfPubKey();

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    protected void onConnectClientServiceBind() {
        super.onConnectClientServiceBind();
        chatModule = (ChatModule) getModule(EnabledServices.CHAT);
        pairingModule = (PairingModule) getModule(EnabledServices.PROFILE_PAIRING);
        profilesModule = (ProfilesModule) getModule(EnabledServices.PROFILE_DATA);
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
}
