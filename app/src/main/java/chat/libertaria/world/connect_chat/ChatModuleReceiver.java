package chat.libertaria.world.connect_chat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.libertaria.world.profile_server.CantSendMessageException;
import org.libertaria.world.profile_server.ProfileInformation;
import org.libertaria.world.profile_server.engine.app_services.BaseMsg;
import org.libertaria.world.services.chat.msg.ChatMsg;
import org.libertaria.world.services.chat.msg.ChatMsgTypes;
import org.libertaria.world.services.interfaces.PairingModule;
import org.libertaria.world.services.interfaces.ProfilesModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.ref.WeakReference;

import chat.libertaria.world.connect_chat.chat.WaitingChatActivity;
import chat.libertaria.world.connect_chat.chat.contact_list.ChatContactActivity;
import world.libertaria.sdk.android.client.ClientServiceConnectHelper;
import world.libertaria.sdk.android.client.ConnectClientService;
import world.libertaria.sdk.android.client.ConnectReceiver;
import world.libertaria.sdk.android.client.InitListener;
import world.libertaria.sdk.android.client.LocalConnection;
import world.libertaria.shared.library.global.ModuleObject;
import world.libertaria.shared.library.global.socket.LocalSocketSession;
import world.libertaria.shared.library.global.socket.SessionHandler;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static chat.libertaria.world.connect_chat.ChatApp.INTENT_CHAT_ACCEPTED_BROADCAST;
import static chat.libertaria.world.connect_chat.ChatApp.INTENT_CHAT_REFUSED_BROADCAST;
import static chat.libertaria.world.connect_chat.ChatApp.INTENT_CHAT_TEXT_BROADCAST;
import static chat.libertaria.world.connect_chat.ChatApp.INTENT_CHAT_TEXT_RECEIVED;
import static chat.libertaria.world.connect_chat.ChatApp.PREFS_NAME;
import static chat.libertaria.world.connect_chat.chat.WaitingChatActivity.REMOTE_PROFILE_PUB_KEY;
import static world.libertaria.shared.library.global.client.IntentBroadcastConstants.ACTION_IOP_SERVICE_CONNECTED;
import static world.libertaria.shared.library.services.chat.ChatIntentsConstants.ACTION_ON_CHAT_CONNECTED;
import static world.libertaria.shared.library.services.chat.ChatIntentsConstants.ACTION_ON_CHAT_DISCONNECTED;
import static world.libertaria.shared.library.services.chat.ChatIntentsConstants.ACTION_ON_CHAT_MSG_RECEIVED;
import static world.libertaria.shared.library.services.chat.ChatIntentsConstants.ACTION_OPEN_CHAT_APP;
import static world.libertaria.shared.library.services.chat.ChatIntentsConstants.EXTRA_INTENT_CHAT_MSG;
import static world.libertaria.shared.library.services.chat.ChatIntentsConstants.EXTRA_INTENT_DETAIL;
import static world.libertaria.shared.library.services.chat.ChatIntentsConstants.EXTRA_INTENT_IS_LOCAL_CREATOR;
import static world.libertaria.shared.library.services.chat.ChatIntentsConstants.EXTRA_INTENT_LOCAL_PROFILE;
import static world.libertaria.shared.library.services.chat.ChatIntentsConstants.EXTRA_INTENT_REMOTE_PROFILE;

/**
 * Created by furszy on 7/20/17.
 */

public class ChatModuleReceiver extends ConnectReceiver {

    private Logger log = LoggerFactory.getLogger(ChatModuleReceiver.class);
    private NotificationManager notificationManager;
    private AppConf appConf;

    public ChatModuleReceiver() {

    }

    /*@Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        log.info("chat module receiver");
        try {
            if (action.equals(ACTION_ON_CHAT_CONNECTED)) {
                String localPk = intent.getStringExtra(EXTRA_INTENT_LOCAL_PROFILE);
                String remotePk = intent.getStringExtra(EXTRA_INTENT_REMOTE_PROFILE);
                boolean isLocalCreator = intent.getBooleanExtra(EXTRA_INTENT_IS_LOCAL_CREATOR, false);
                onChatConnected(context,localPk, remotePk, isLocalCreator);
            } else if (action.equals(ACTION_ON_CHAT_DISCONNECTED)) {
                String remotePk = intent.getStringExtra(EXTRA_INTENT_REMOTE_PROFILE);
                String reason = intent.getStringExtra(EXTRA_INTENT_DETAIL);
                onChatDisconnected(remotePk, reason);
            } else if (action.equals(ACTION_ON_CHAT_MSG_RECEIVED)) {
                String remotePk = intent.getStringExtra(EXTRA_INTENT_REMOTE_PROFILE);
                BaseMsg baseMsg = (BaseMsg) intent.getSerializableExtra(EXTRA_INTENT_CHAT_MSG);
                onMsgReceived(remotePk, baseMsg);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }*/

    @Override
    public void onConnectReceive(Context context, Intent intent) {
        if (appConf==null)
            appConf = new AppConf(context.getSharedPreferences(PREFS_NAME, 0));
        String action = intent.getAction();
        log.info("chat module receiver");
        try {
            if (action.equals(ACTION_ON_CHAT_CONNECTED)) {
                String localPk = intent.getStringExtra(EXTRA_INTENT_LOCAL_PROFILE);
                String remotePk = intent.getStringExtra(EXTRA_INTENT_REMOTE_PROFILE);
                boolean isLocalCreator = intent.getBooleanExtra(EXTRA_INTENT_IS_LOCAL_CREATOR, false);
                onChatConnected(context,localPk, remotePk, isLocalCreator);
            } else if (action.equals(ACTION_ON_CHAT_DISCONNECTED)) {
                String remotePk = intent.getStringExtra(EXTRA_INTENT_REMOTE_PROFILE);
                String reason = intent.getStringExtra(EXTRA_INTENT_DETAIL);
                onChatDisconnected(remotePk, reason);
            } else if (action.equals(ACTION_ON_CHAT_MSG_RECEIVED)) {
                String remotePk = intent.getStringExtra(EXTRA_INTENT_REMOTE_PROFILE);
                BaseMsg baseMsg = (BaseMsg) intent.getSerializableExtra(EXTRA_INTENT_CHAT_MSG);
                onMsgReceived(remotePk, baseMsg);
            } else if(action.equals(ACTION_OPEN_CHAT_APP)){
                String localPk = intent.getStringExtra(EXTRA_INTENT_LOCAL_PROFILE);
                String remotePk = intent.getStringExtra(EXTRA_INTENT_REMOTE_PROFILE);
                openChatApp(context,localPk,remotePk);
            }
        }catch (Exception e){
            e.printStackTrace();
            log.info("Exception "+e.getMessage());
        }
    }

    private void openChatApp(Context context,String localPk, String remotePk) {
        log.info("open chat: ");
        Intent intent = new Intent(context, ChatContactActivity.class);
        intent.putExtra(REMOTE_PROFILE_PUB_KEY,remotePk);
        context.startActivity(intent);
    }


    public void onChatConnected(Context context,String localProfilePubKey, String remoteProfilePubKey, boolean isLocalCreator) {
        log.info("on chat connected: " + remoteProfilePubKey);
        setNotificationManager(context);
        ProfileInformation remoteProflie = null;
        ProfilesModule profilesModule = getProfilesModule();
        String selectedProfPk = appConf.getSelectedProfPubKey();
        remoteProflie = profilesModule.getKnownProfile(selectedProfPk, remoteProfilePubKey);
        if (remoteProflie != null) {
            Intent intent = new Intent(context, WaitingChatActivity.class);
            intent.putExtra(REMOTE_PROFILE_PUB_KEY, remoteProfilePubKey);
            intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
            if (isLocalCreator) {
                //intent.putExtra(WaitingChatActivity.IS_CALLING, false);
                //app.startActivity(intent);
            } else {
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
                // todo: null pointer found.
                String name = remoteProflie.getName();
                Notification not = new Notification.Builder(context)
                        .setContentTitle("Hey, chat notification received")
                        .setContentText(name + " want to chat with you!")
                        .setSmallIcon(R.drawable.ic_open_chat)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .build();
                notificationManager.notify(43, not);
            }
        } else {
            log.error("Chat notification arrive without know the profile, remote pub key " + remoteProfilePubKey);
        }
    }

    private void setNotificationManager(Context context) {
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void onChatDisconnected(String remotePk, String reason) {
        log.info("on chat disconnected: " + remotePk);
        ChatApp app = ChatApp.getInstance();
        Intent intent = new Intent(ACTION_ON_CHAT_DISCONNECTED);
        intent.putExtra(REMOTE_PROFILE_PUB_KEY,remotePk);
        intent.putExtra(EXTRA_INTENT_DETAIL,reason);
        app.getBroadcastManager().sendBroadcast(intent);
    }

    public void onMsgReceived(String remotePubKey, BaseMsg msg) {
        log.info("on chat msg received: " + remotePubKey);
        ChatApp app = ChatApp.getInstance();
        Intent intent = new Intent();
        intent.putExtra(REMOTE_PROFILE_PUB_KEY, remotePubKey);
        switch (ChatMsgTypes.valueOf(msg.getType())) {
            case CHAT_ACCEPTED:
                intent.setAction(INTENT_CHAT_ACCEPTED_BROADCAST);
                break;
            case CHAT_REFUSED:
                intent.setAction(INTENT_CHAT_REFUSED_BROADCAST);
                break;
            case TEXT:
                intent.putExtra(INTENT_CHAT_TEXT_RECEIVED, ((ChatMsg) msg).getText());
                intent.setAction(INTENT_CHAT_TEXT_BROADCAST);
                break;
        }
        app.getBroadcastManager().sendBroadcast(intent);
    }

}
