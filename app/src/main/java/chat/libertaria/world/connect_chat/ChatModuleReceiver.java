package chat.libertaria.world.connect_chat;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.libertaria.world.profile_server.ProfileInformation;
import org.libertaria.world.profile_server.engine.app_services.BaseMsg;
import org.libertaria.world.services.chat.msg.ChatMsg;
import org.libertaria.world.services.chat.msg.ChatMsgTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chat.libertaria.world.connect_chat.chat.WaitingChatActivity;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static chat.libertaria.world.connect_chat.ChatApp.INTENT_CHAT_ACCEPTED_BROADCAST;
import static chat.libertaria.world.connect_chat.ChatApp.INTENT_CHAT_REFUSED_BROADCAST;
import static chat.libertaria.world.connect_chat.ChatApp.INTENT_CHAT_TEXT_BROADCAST;
import static chat.libertaria.world.connect_chat.ChatApp.INTENT_CHAT_TEXT_RECEIVED;
import static chat.libertaria.world.connect_chat.chat.WaitingChatActivity.REMOTE_PROFILE_PUB_KEY;
import static world.libertaria.shared.library.services.chat.ChatIntentsConstants.ACTION_ON_CHAT_CONNECTED;
import static world.libertaria.shared.library.services.chat.ChatIntentsConstants.ACTION_ON_CHAT_DISCONNECTED;
import static world.libertaria.shared.library.services.chat.ChatIntentsConstants.ACTION_ON_CHAT_MSG_RECEIVED;
import static world.libertaria.shared.library.services.chat.ChatIntentsConstants.EXTRA_INTENT_CHAT_MSG;
import static world.libertaria.shared.library.services.chat.ChatIntentsConstants.EXTRA_INTENT_DETAIL;
import static world.libertaria.shared.library.services.chat.ChatIntentsConstants.EXTRA_INTENT_IS_LOCAL_CREATOR;
import static world.libertaria.shared.library.services.chat.ChatIntentsConstants.EXTRA_INTENT_LOCAL_PROFILE;
import static world.libertaria.shared.library.services.chat.ChatIntentsConstants.EXTRA_INTENT_REMOTE_PROFILE;

/**
 * Created by furszy on 7/20/17.
 */

public class ChatModuleReceiver extends BroadcastReceiver {

    private Logger log = LoggerFactory.getLogger(ChatModuleReceiver.class);

    public ChatModuleReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        log.info("chat module receiver");
        try {
            if (action.equals(ACTION_ON_CHAT_CONNECTED)) {
                String localPk = intent.getStringExtra(EXTRA_INTENT_LOCAL_PROFILE);
                String remotePk = intent.getStringExtra(EXTRA_INTENT_REMOTE_PROFILE);
                boolean isLocalCreator = intent.getBooleanExtra(EXTRA_INTENT_IS_LOCAL_CREATOR, false);
                onChatConnected(localPk, remotePk, isLocalCreator);
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
    }

    public void onChatConnected(String localProfilePubKey, String remoteProfilePubKey, boolean isLocalCreator) {
        log.info("on chat connected: " + remoteProfilePubKey);
        ChatApp app = ChatApp.getInstance();
        ProfileInformation remoteProflie = app.getProfilesModule().getKnownProfile(app.getSelectedProfilePubKey(),remoteProfilePubKey);
        if (remoteProflie != null) {
            // todo: negro acá abrí la vista de incoming para aceptar el request..
            Intent intent = new Intent(app, WaitingChatActivity.class);
            intent.putExtra(REMOTE_PROFILE_PUB_KEY, remoteProfilePubKey);
            intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
            if (isLocalCreator) {
                intent.putExtra(WaitingChatActivity.IS_CALLING, false);
                app.startActivity(intent);
            } else {
                PendingIntent pendingIntent = PendingIntent.getActivity(app, 0, intent, 0);
                // todo: null pointer found.
                String name = remoteProflie.getName();
                Notification not = new Notification.Builder(app)
                        .setContentTitle("Hey, chat notification received")
                        .setContentText(name + " want to chat with you!")
                        .setSmallIcon(R.drawable.ic_open_chat)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .build();
                app.getNotificationManager().notify(43, not);
            }
        } else {
            log.error("Chat notification arrive without know the profile, remote pub key " + remoteProfilePubKey);
        }
    }

    public void onChatDisconnected(String remotePk, String reason) {
        log.info("on chat disconnected: " + remotePk);
        ChatApp app = ChatApp.getInstance();
        Intent intent = new Intent();
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
