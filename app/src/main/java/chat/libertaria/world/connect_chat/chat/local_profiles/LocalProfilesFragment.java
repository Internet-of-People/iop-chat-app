package chat.libertaria.world.connect_chat.chat.local_profiles;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import org.libertaria.world.profile_server.ProfileInformation;

import java.util.List;

import chat.libertaria.world.connect_chat.R;
import chat.libertaria.world.connect_chat.base.BaseAppRecyclerFragment;
import chat.libertaria.world.connect_chat.chat.contact_list.ChatContactActivity;
import tech.furszy.ui.lib.base.adapter.BaseAdapter;
import tech.furszy.ui.lib.base.adapter.BaseViewHolder;
import tech.furszy.ui.lib.base.adapter.RecyclerListItemListeners;

/**
 * Created by furszy on 8/10/17.
 */

public class LocalProfilesFragment extends BaseAppRecyclerFragment<ProfileInformation> {

    @Override
    protected List<ProfileInformation> onLoading() {
        if (profilesModule != null)
            return profilesModule.getLocalProfiles();
        return null;
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        refresh();
    }

    @Override
    protected BaseAdapter<ProfileInformation, ? extends ProfileHolder> initAdapter() {
        BaseAdapter<ProfileInformation, ProfileHolder> baseAdapter = new BaseAdapter<ProfileInformation, ProfileHolder>(getActivity()) {
            @Override
            protected ProfileHolder createHolder(View view, int type) {
                return new ProfileHolder(view, type);
            }

            @Override
            protected int getCardViewResource(int i) {
                return R.layout.my_contacts_row;
            }

            @Override
            protected void bindHolder(ProfileHolder profileHolder, ProfileInformation profileInformation, int position) {
                profileHolder.txt_name.setText(profileInformation.getName());
            }
        };
        baseAdapter.setListEventListener(new RecyclerListItemListeners<ProfileInformation>() {
            @Override
            public void onItemClickListener(ProfileInformation o, int i) {
                app.setSelectedProfile(o.getHexPublicKey());
                startActivity(new Intent(getActivity(), ChatContactActivity.class));
            }

            @Override
            public void onLongItemClickListener(ProfileInformation o, int i) {

            }
        });
        return baseAdapter;
    }

    private class ProfileHolder extends BaseViewHolder {

        TextView txt_name;

        protected ProfileHolder(View itemView, int holderType) {
            super(itemView, holderType);
            txt_name = itemView.findViewById(R.id.txt_name);
        }
    }
}
