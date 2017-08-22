package chat.libertaria.world.connect_chat.utils;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.ContextCompat;

import chat.libertaria.world.connect_chat.R;
import chat.libertaria.world.connect_chat.base.dialogs.SimpleTextDialog;
import chat.libertaria.world.connect_chat.base.dialogs.SimpleTwoButtonsDialog;

/**
 * Created by Neoperol on 8/21/17.
 */

public class DialogsUtil {

    public static SimpleTextDialog buildSimpleTextDialog(Context context, String title, String body){
        final SimpleTextDialog dialog = SimpleTextDialog.newInstance();
        dialog.setTitle(title);
        dialog.setBody(body);
        dialog.setOkBtnBackgroundColor(context.getResources().getColor(R.color.mainPurple,null));
        dialog.setOkBtnTextColor(Color.WHITE);
        dialog.setRootBackgroundRes(R.drawable.dialog_bg);
        return dialog;
    }

    public static SimpleTwoButtonsDialog buildSimpleTwoBtnsDialog(Context context, int titleRes, int bodyRes, SimpleTwoButtonsDialog.SimpleTwoBtnsDialogListener simpleTwoBtnsDialogListener){
        return buildSimpleTwoBtnsDialog(context,context.getString(titleRes),context.getString(bodyRes),simpleTwoBtnsDialogListener);
    }

    public static SimpleTwoButtonsDialog buildSimpleTwoBtnsDialog(Context context, String title, String body, SimpleTwoButtonsDialog.SimpleTwoBtnsDialogListener simpleTwoBtnsDialogListener){
        final SimpleTwoButtonsDialog dialog = SimpleTwoButtonsDialog.newInstance(context);
        dialog.setTitle(title);
        dialog.setTitleColor(Color.BLACK);
        dialog.setBody(body);
        dialog.setBodyColor(Color.BLACK);
        dialog.setListener(simpleTwoBtnsDialogListener);
        dialog.setContainerBtnsBackgroundColor(Color.WHITE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            dialog.setRightBtnBackgroundColor(context.getResources().getColor(R.color.mainPurple, null));
        }else {
            dialog.setRightBtnBackgroundColor(ContextCompat.getColor(context,R.color.mainPurple));
        }
        dialog.setLeftBtnTextColor(Color.BLACK);
        dialog.setRightBtnTextColor(Color.WHITE);
        dialog.setRootBackgroundRes(R.drawable.dialog_bg);
        return dialog;
    }
}
