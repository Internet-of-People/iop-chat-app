package chat.libertaria.world.connect_chat;

import android.content.SharedPreferences;

import world.libertaria.shared.library.ui.Configurations;

/**
 * Created by furszy on 8/10/17.
 */

public class AppConf extends Configurations{

    public static String PREF_APP_PK = "selected_prof_pub_key";

    public AppConf(SharedPreferences prefs) {
        super(prefs);
    }

    public String getSelectedProfPubKey() {
        return getString(PREF_APP_PK,null);
    }

    public void setSelectedProfPubKey(String selectedProfPubKey){
        save(PREF_APP_PK,selectedProfPubKey);
    }
}
