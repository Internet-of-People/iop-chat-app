package chat.libertaria.world.connect_chat.base;

import android.os.Bundle;
import android.support.annotation.Nullable;

import org.libertaria.world.services.chat.ChatModule;
import org.libertaria.world.services.interfaces.PairingModule;
import org.libertaria.world.services.interfaces.ProfilesModule;

import chat.libertaria.world.connect_chat.ChatApp;
import tech.furszy.ui.lib.base.RecyclerFragment;

/**
 * Created by furszy on 8/10/17.
 */

public abstract class BaseAppRecyclerFragment<T> extends RecyclerFragment<T> {

    protected ChatApp app;
    protected String selectedProfilePubKey;
    protected PairingModule pairingModule;
    protected ChatModule chatModule;
    protected ProfilesModule profilesModule;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadBasics();
    }


    public void loadBasics() {
        app = ChatApp.getInstance();
        pairingModule = app.getPairingModule();
        chatModule = app.getChatModule();
        profilesModule = app.getProfilesModule();
        selectedProfilePubKey = app.getSelectedProfilePubKey();
    }
}