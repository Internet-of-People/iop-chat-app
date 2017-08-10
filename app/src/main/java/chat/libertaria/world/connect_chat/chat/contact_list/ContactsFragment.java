package chat.libertaria.world.connect_chat.chat.contact_list;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.libertaria.world.profile_server.ProfileInformation;

import chat.libertaria.world.connect_chat.R;
import chat.libertaria.world.connect_chat.base.BaseAppRecyclerFragment;
import tech.furszy.ui.lib.base.adapter.BaseAdapter;
import tech.furszy.ui.lib.base.adapter.FermatListItemListeners;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;



public class ContactsFragment extends BaseAppRecyclerFragment<ProfileInformation> {

    private static final Logger log = LoggerFactory.getLogger(ContactsFragment.class);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        setEmptyText(getResources().getString(R.string.empty_contact));
        setEmptyTextColor(Color.parseColor("#4d4d4d"));
        //setEmptyView(R.drawable.img_contact_empty);
        return view;
    }

    @Override
    protected BaseAdapter initAdapter() {
        return new ProfileAdapter(getActivity(),new FermatListItemListeners<ProfileInformation>() {
            @Override
            public void onItemClickListener(ProfileInformation data, int position) {
                // todo: launch activity
                /*Intent intent1 = new Intent(getActivity(), ProfileInformationActivity.class);
                intent1.putExtra(INTENT_EXTRA_PROF_KEY, data.getPublicKey());
                getActivity().startActivity(intent1);*/
            }

            @Override
            public void onLongItemClickListener(ProfileInformation data, int position) {

            }
        });
    }

    @Override
    protected List onLoading() {
        try {
            if (profilesModule!=null)
                return profilesModule.getKnownProfiles(selectedProfilePubKey);
            else {
                loadBasics();
                TimeUnit.SECONDS.sleep(1);
                onLoading();
            }
        }catch (Exception e){
            log.info("onLoading",e);
        }
        return null;
    }


}