package chat.libertaria.world.connect_chat;


import android.app.Application;

import org.libertaria.world.services.EnabledServices;
import org.libertaria.world.services.chat.ChatModule;
import org.libertaria.world.services.interfaces.PairingModule;
import org.libertaria.world.services.interfaces.ProfilesModule;

import world.libertaria.sdk.android.client.ConnectApp;

/**
 * Created by furszy on 8/2/17.
 */

public class ChatApp extends ConnectApp{

    private static volatile ChatApp instance;

    private ChatModule chatModule;
    private PairingModule pairingModule;
    private ProfilesModule profilesModule;

    public static ChatApp getInstance(){
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
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


}
