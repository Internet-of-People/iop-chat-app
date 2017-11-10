package chat.libertaria.world.connect_chat.base;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;

import java.util.List;

/**
 * Created by furszy on 8/13/17.
 */

public class OpenConnectUtil {

    public static void openSendRequestScreen(Context context) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(
                "org.furszy",
                "org.furszy.contacts.ui.send_request.SendRequestActivity"));
        startIntent(context, intent);
    }

    public static void openMyProfileScreen(Context context, String selectedProfPubKey) {
        // todo: add profile pub key to extras..
        launchContacts(context);
    }

    public static void openConnectAppTutorialScreen(Context context) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(
                "org.furszy",
                "org.furszy.contacts.ui.send_request.SendRequestActivity"));
        boolean isIntentSafe = checkIfConnectServiceExist(context, intent);
        if (isIntentSafe) {
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "Not supported operation", Toast.LENGTH_LONG).show();
        }
    }

    public static void startIntent(Context context, Intent intent) {
        boolean isIntentSafe = checkIfConnectServiceExist(context, intent);
        if (isIntentSafe) {
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "Not supported operation", Toast.LENGTH_LONG).show();
        }
    }

    public static void launchContacts(Context context) {
        String packageName = "org.furszy";
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (intent != null) {
            // We found the activity now start the activity
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "Not supported operation", Toast.LENGTH_LONG).show();
        }

//        Intent intent = new Intent(ContactsAppIntents.ACTION_OPEN_ACTIVITY);
//        intent.setPackage(packageName);
//        intent.putExtra(ContactsAppIntents.INTENT_ACTIVITY_NAME,activityType);
//        boolean isIntentSafe = checkIfConnectServiceExist(context,intent);
//        if (isIntentSafe) {
//            context.sendBroadcast(intent);
//        }else {
//            Toast.makeText(context,"Not supported operation",Toast.LENGTH_LONG).show();
//        }
    }

    public static boolean checkIfConnectServiceExist(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        List activities = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return activities.size() > 0;
    }

}
