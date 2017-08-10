package chat.libertaria.world.connect_chat.chat.contact_list;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.view.View;

import org.libertaria.world.profile_server.ProfileInformation;

import chat.libertaria.world.connect_chat.R;
import tech.furszy.ui.lib.base.adapter.BaseAdapter;
import tech.furszy.ui.lib.base.adapter.FermatListItemListeners;

/**
 * Created by mati on 03/03/17.
 */
public class ProfileAdapter extends BaseAdapter<ProfileInformation, ProfileHolder> {


    public ProfileAdapter(final Activity context, FermatListItemListeners<ProfileInformation> fermatListItemListeners) {
        super(context);
        setFermatListEventListener(fermatListItemListeners);

    }

    @Override
    protected ProfileHolder createHolder(View itemView, int type) {
        return new ProfileHolder(itemView, type);
    }

    @Override
    protected int getCardViewResource(int type) {
        return R.layout.my_contacts_row;
    }

    @Override
    protected void bindHolder(final ProfileHolder holder, final ProfileInformation data, int position) {
        holder.txt_name.setText(data.getName());
        if (data.getImg()!=null && data.getImg().length>1)
            holder.img.setImageBitmap(BitmapFactory.decodeByteArray(data.getImg(),0,data.getImg().length));
    }
}
