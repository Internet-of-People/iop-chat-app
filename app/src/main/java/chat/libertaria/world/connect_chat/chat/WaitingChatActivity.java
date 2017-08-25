package chat.libertaria.world.connect_chat.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import org.libertaria.world.profile_server.ProfileInformation;
import org.libertaria.world.profile_server.client.AppServiceCallNotAvailableException;
import org.libertaria.world.profile_server.engine.futures.BaseMsgFuture;
import org.libertaria.world.profile_server.engine.futures.MsgListenerFuture;
import org.libertaria.world.profile_server.engine.listeners.ProfSerMsgListener;
import org.libertaria.world.services.chat.ChatCallAlreadyOpenException;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import chat.libertaria.world.connect_chat.ChatApp;
import chat.libertaria.world.connect_chat.R;
import chat.libertaria.world.connect_chat.base.BaseActivity;
import chat.libertaria.world.connect_chat.base.dialogs.DialogListener;
import chat.libertaria.world.connect_chat.base.dialogs.SimpleTextDialog;
import chat.libertaria.world.connect_chat.utils.DialogsUtil;
import de.hdodenhof.circleimageview.CircleImageView;
import pl.droidsonroids.gif.GifImageView;

import static chat.libertaria.world.connect_chat.ChatApp.INTENT_CHAT_REFUSED_BROADCAST;
import static world.libertaria.shared.library.services.chat.ChatIntentsConstants.ACTION_ON_CHAT_DISCONNECTED;
import static world.libertaria.shared.library.services.chat.ChatIntentsConstants.EXTRA_INTENT_DETAIL;

/**
 * Created by Neoperol on 7/3/17.
 */

public class WaitingChatActivity extends BaseActivity implements View.OnClickListener {

    public static final String REMOTE_PROFILE_PUB_KEY = "remote_prof_pub";
    public static final String IS_CALLING = "is_calling";

    /** Call timeout in minutes */
    private static final long CALL_TIMEOUT = 1;
    private SimpleTextDialog errorDialog;
    private GifImageView loadingAnimation;
    private View root;
    private TextView txt_name;
    private CircleImageView img_profile;
    private ProgressBar progressBar;
    private TextView txt_title;
    private ProfileInformation profileInformation;
    private String remotePk;
    private boolean isCalling;
    private ExecutorService executors;
    private AtomicBoolean flag = new AtomicBoolean(false);

    private ScheduledExecutorService scheduledCallTimeout;

    private AtomicBoolean isCalled = new AtomicBoolean(false);

    private BroadcastReceiver chatReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ChatApp.INTENT_CHAT_ACCEPTED_BROADCAST)){
                Intent intent1 = new Intent(WaitingChatActivity.this,ChatActivity.class);
                intent1.putExtra(REMOTE_PROFILE_PUB_KEY,intent.getStringExtra(REMOTE_PROFILE_PUB_KEY));
                startActivity(intent1);
                finish();
            }else if(action.equals(INTENT_CHAT_REFUSED_BROADCAST)){
                Toast.makeText(WaitingChatActivity.this,"Chat refused.",Toast.LENGTH_LONG).show();
                onBackPressed();
            }else if (action.equals(ACTION_ON_CHAT_DISCONNECTED)){
                String remotePubKey = intent.getStringExtra(REMOTE_PROFILE_PUB_KEY);
                String reason = intent.getStringExtra(EXTRA_INTENT_DETAIL);
                if (remotePk.equals(remotePubKey)){
                    Toast.makeText(WaitingChatActivity.this,"Chat disconnected",Toast.LENGTH_LONG).show();
                    onBackPressed();
                }
            }
        }
    };

    @Override
    protected void onCreateView(Bundle savedInstanceState, ViewGroup container) {
        super.onCreateView(savedInstanceState, container);
        root = getLayoutInflater().inflate(R.layout.incoming_message, container);
        img_profile = (CircleImageView) root.findViewById(R.id.profile_image);
        txt_name = (TextView) root.findViewById(R.id.txt_name);
        progressBar = (ProgressBar) root.findViewById(R.id.progressBar);
        txt_title = (TextView) root.findViewById(R.id.txt_title);
        remotePk = getIntent().getStringExtra(REMOTE_PROFILE_PUB_KEY);
        if (remotePk==null) throw new IllegalStateException("remote profile key null");
        isCalling = getIntent().hasExtra(IS_CALLING);
        if (isCalling){
            root.findViewById(R.id.single_cancel_container).setVisibility(View.VISIBLE);
            root.findViewById(R.id.btn_cancel_chat_alone).setOnClickListener(this);
            root.findViewById(R.id.container_btns).setVisibility(View.GONE);
            // prepare timer..
            scheduleCallTimeout();
        }else {
            root.findViewById(R.id.loading_animation).setVisibility(View.GONE);
            root.findViewById(R.id.single_cancel_container).setVisibility(View.GONE);
            root.findViewById(R.id.btn_open_chat).setOnClickListener(this);
            root.findViewById(R.id.btn_cancel_chat).setOnClickListener(this);
        }
    }

    private void call() {
        if (!isCalled.getAndSet(true)) {
            if (flag.compareAndSet(false, true)) {
                Log.i("CHAT","sending chat request..");
                Toast.makeText(this, "Sending chat request..", Toast.LENGTH_SHORT).show();
                if (executors == null)
                    executors = Executors.newSingleThreadExecutor();
                executors.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            MsgListenerFuture<Boolean> readyListener = new MsgListenerFuture<>();
                            readyListener.setListener(new BaseMsgFuture.Listener<Boolean>() {
                                @Override
                                public void onAction(int messageId, Boolean object) {
                                    flag.set(false);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(WaitingChatActivity.this, "Chat request sent", Toast.LENGTH_LONG).show();
                                        }
                                    });

                                }

                                @Override
                                public void onFail(int messageId, final int status, final String statusDetail) {
                                    Log.e("APP", "fail chat request: " + statusDetail + ", id: " + messageId);
                                    flag.set(false);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (errorDialog==null) {
                                                errorDialog = DialogsUtil.buildSimpleTextDialog(
                                                        WaitingChatActivity.this,
                                                        getString(R.string.chat_request_fail_title),
                                                        getString(R.string.chat_request_fail, statusDetail)

                                                );
                                                errorDialog.setListener(new DialogListener() {
                                                    @Override
                                                    public void cancel(boolean isActionCompleted) {
                                                        if (WaitingChatActivity.this.isDestroyed() || !WaitingChatActivity.this.isFinishing())
                                                            return;
                                                        onBackPressed();
                                                    }
                                                });
                                            }else {
                                                errorDialog.setBody(getString(R.string.chat_request_fail, statusDetail));
                                            }
                                            errorDialog.show(getFragmentManager(),"chat_fail_dialog");
                                        }
                                    });
                                }
                            });

                            chatModule.requestChat(selectedProfPubKey, profileInformation, readyListener);
                        } catch (ChatCallAlreadyOpenException e) {
                            e.printStackTrace();
                            // chat call already open
                            // first send the acceptance
                            Log.i("CHAT","chat already open");
                            try {
                                chatModule.acceptChatRequest(selectedProfPubKey, profileInformation.getHexPublicKey(), new ProfSerMsgListener<Boolean>() {
                                    @Override
                                    public void onMessageReceive(int messageId, Boolean message) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                flag.set(false);
                                                // let's go to the chat again
                                                Intent intent1 = new Intent(WaitingChatActivity.this, ChatActivity.class);
                                                intent1.putExtra(REMOTE_PROFILE_PUB_KEY, profileInformation.getHexPublicKey());
                                                startActivity(intent1);
                                            }
                                        });
                                    }

                                    @Override
                                    public void onMsgFail(int messageId, int statusValue, final String details) {
                                        Log.e("TAG", "chat connection fail " + details);
                                        flag.set(false);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(WaitingChatActivity.this, "Chat connection fail\n" + details, Toast.LENGTH_LONG).show();
                                                onBackPressed();
                                            }
                                        });
                                    }

                                    @Override
                                    public String getMessageName() {
                                        return "accept_chat_request";
                                    }
                                });

                            } catch (AppServiceCallNotAvailableException e1) {
                                e1.printStackTrace();
                                flag.set(false);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(WaitingChatActivity.this, "Remote profile calling you.., closing the connection\nPlease try again", Toast.LENGTH_LONG).show();
                                        onBackPressed();
                                    }
                                });

                            } catch (final Exception e1) {
                                e1.printStackTrace();
                                flag.set(false);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        onBackPressed();
                                        Toast.makeText(WaitingChatActivity.this, "Chat connection fail\n" + e1.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    onBackPressed();
                                    Toast.makeText(WaitingChatActivity.this, "Chat call fail\nplease try again later", Toast.LENGTH_LONG).show();
                                }
                            });
                            flag.set(false);
                        }
                    }
                });
            }
        }
    }

    private void scheduleCallTimeout() {
        scheduledCallTimeout = Executors.newSingleThreadScheduledExecutor();
        scheduledCallTimeout.schedule(new Runnable() {
            @Override
            public void run() {
                executors.submit(new Runnable() {
                    @Override
                    public void run() {
                        refuseChat();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(WaitingChatActivity.this,profileInformation.getName()+" doesn't answer..",Toast.LENGTH_LONG).show();
                                onBackPressed();
                            }
                        });
                    }
                });
            }
        },CALL_TIMEOUT, TimeUnit.MINUTES);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            localBroadcastManager.registerReceiver(chatReceiver, new IntentFilter(ChatApp.INTENT_CHAT_ACCEPTED_BROADCAST));
            localBroadcastManager.registerReceiver(chatReceiver, new IntentFilter(ChatApp.INTENT_CHAT_REFUSED_BROADCAST));
            localBroadcastManager.registerReceiver(chatReceiver, new IntentFilter(ACTION_ON_CHAT_DISCONNECTED));
            if (!isCalling) {
                if (executors == null)
                    executors = Executors.newSingleThreadExecutor();
                executors.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (selectedProfPubKey != null && remotePk != null) {
                                if (!chatModule.isChatActive(selectedProfPubKey, remotePk)) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(WaitingChatActivity.this, "Chat not active anymore", Toast.LENGTH_LONG).show();
                                            onBackPressed();
                                        }
                                    });

                                }
                            } else
                                Log.e("WaitingChat", "profile pub key is null");
                        } catch (Exception e) {
                            e.printStackTrace();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(WaitingChatActivity.this, "Chat not active anymore", Toast.LENGTH_LONG).show();
                                    onBackPressed();
                                }
                            });

                        }
                    }
                });
            }
            profileInformation = profilesModule.getKnownProfile(selectedProfPubKey, remotePk);
            txt_name.setText(profileInformation.getName());
            if (profileInformation.getImg() != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(profileInformation.getImg(), 0, profileInformation.getImg().length);
                img_profile.setImageBitmap(bitmap);
            }
            if (isCalling) {
                txt_title.setText("Waiting for " + profileInformation.getName() + " response...");
                call();
            } else {
                txt_title.setText("Call from " + profileInformation.getName());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        localBroadcastManager.unregisterReceiver(chatReceiver);
        if (executors!=null){
            executors.shutdownNow();
            executors = null;
        }
        if (scheduledCallTimeout!=null){
            scheduledCallTimeout.shutdownNow();
        }
        finish();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_open_chat){
            progressBar.setVisibility(View.VISIBLE);
            acceptChatRequest();
        }else if (id == R.id.btn_cancel_chat || id == R.id.btn_cancel_chat_alone){
            // here i have to close the connection refusing the call..
            if (executors==null){
                executors = Executors.newSingleThreadExecutor();
            }
            executors.submit(new Runnable() {
                @Override
                public void run() {
                    refuseChat();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onBackPressed();
                        }
                    });
                }
            });
        }
    }

    private void refuseChat(){
        try{
            chatModule.refuseChatRequest(selectedProfPubKey,profileInformation.getHexPublicKey());
        }catch (Exception e){
            e.printStackTrace();
            // do nothing..
        }
    }

    private void acceptChatRequest() {
        if (executors==null)
            executors = Executors.newSingleThreadExecutor();
        executors.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    // send the ok to the other side
                    MsgListenerFuture<Boolean> future = new MsgListenerFuture<>();
                    future.setListener(new BaseMsgFuture.Listener<Boolean>() {
                        @Override
                        public void onAction(int messageId, Boolean object) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(WaitingChatActivity.this, ChatActivity.class);
                                    intent.putExtra(REMOTE_PROFILE_PUB_KEY, profileInformation.getHexPublicKey());
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        }

                        @Override
                        public void onFail(int messageId, int status, final String statusDetail) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(WaitingChatActivity.this, "Chat connection fail\n" + statusDetail, Toast.LENGTH_LONG).show();
                                    onBackPressed();
                                }
                            });
                        }
                    });
                    chatModule.acceptChatRequest(selectedProfPubKey,profileInformation.getHexPublicKey(), future);

                    /*Intent intent = new Intent(WaitingChatActivity.this,ChatActivity.class);
                    intent.putExtra(REMOTE_PROFILE_PUB_KEY,remotePk);
                    startActivity(intent);*/

                } catch (AppServiceCallNotAvailableException e){
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(WaitingChatActivity.this,"Connection is not longer available",Toast.LENGTH_LONG).show();
                            onBackPressed();
                        }
                    });

                } catch (final Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(WaitingChatActivity.this,"Chat connection fail\n"+e.getMessage(),Toast.LENGTH_LONG).show();
                            onBackPressed();
                        }
                    });
                }
            }
        });
    }
}
