package chat.libertaria.world.connect_chat.chat.init;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import chat.libertaria.world.connect_chat.ChatApp;
import chat.libertaria.world.connect_chat.chat.contact_list.ChatContactActivity;
import chat.libertaria.world.connect_chat.chat.welcome.MainActivity;

/**
 * Created by furszy on 8/18/17.
 */

public class InitActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(ChatApp.getInstance().getAppConf().getAppInit()){
            startActivity(new Intent(this, ChatContactActivity.class));
        }else {
            startActivity(new Intent(this, MainActivity.class));
        }
        finish();

    }
}
