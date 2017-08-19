package chat.libertaria.world.connect_chat.base;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;

import java.util.List;

import chat.libertaria.world.connect_chat.chat.contact_list.ChatContactActivity;
import world.libertaria.shared.library.contacts.ContactsAppIntents;
import world.libertaria.shared.library.global.client.IntentBroadcastConstants;

/**
 * Created by furszy on 8/13/17.
 */

public class OpenConnectUtil {

    public static void openSendRequestScreen(Context context){
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(
                "org.furszy",
                "org.furszy.contacts.ui.send_request.SendRequestActivity"));
        startIntent(context,intent);
    }

    public static void openMyProfileScreen(Context context, String selectedProfPubKey) {
        // todo: add profile pub key to extras..
        //intent.putExtra(PROF_PUB_KEY)
        startActivity(context,ContactsAppIntents.INTENT_EXTRA_ACTIVITY_TYPE_MY_PROFILE);
    }

    public static void openConnectAppTutorialScreen(Context context){
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(
                "org.furszy",
                "org.furszy.contacts.ui.send_request.SendRequestActivity"));
        boolean isIntentSafe = checkIfConnectServiceExist(context,intent);
        if (isIntentSafe) {
            context.startActivity(intent);
        }else {
            Toast.makeText(context,"Not supported operation",Toast.LENGTH_LONG).show();
        }
    }

    public static void startIntent(Context context,Intent intent){
        boolean isIntentSafe = checkIfConnectServiceExist(context,intent);
        if (isIntentSafe) {
            context.startActivity(intent);
        }else {
            Toast.makeText(context,"Not supported operation",Toast.LENGTH_LONG).show();
        }
    }

    public static void startActivity(Context context,String activityType){
        Intent intent = new Intent(ContactsAppIntents.ACTION_OPEN_ACTIVITY);
        intent.setPackage("org.furszy");
        intent.putExtra(ContactsAppIntents.INTENT_ACTIVITY_NAME,activityType);
        //boolean isIntentSafe = checkIfConnectServiceExist(context,intent);
        //if (isIntentSafe) {
            context.sendBroadcast(intent);
        //}else {
        //    Toast.makeText(context,"Not supported operation",Toast.LENGTH_LONG).show();
        //}
    }

    public static boolean checkIfConnectServiceExist(Context context,Intent intent){
        PackageManager packageManager = context.getPackageManager();
        List activities = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return activities.size() > 0;
    }

}
