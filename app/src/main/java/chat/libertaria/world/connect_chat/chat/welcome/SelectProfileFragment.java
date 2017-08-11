package chat.libertaria.world.connect_chat.chat.welcome;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import org.libertaria.world.profile_server.ProfileInformation;

import java.util.ArrayList;
import java.util.List;

import chat.libertaria.world.connect_chat.R;
import chat.libertaria.world.connect_chat.base.BaseAppFragment;
import chat.libertaria.world.connect_chat.base.BaseAppRecyclerFragment;
import chat.libertaria.world.connect_chat.chat.contact_list.ProfileHolder;

/**
 * Created by furszy on 8/11/17.
 */

public class SelectProfileFragment extends BaseAppFragment {

    private View root;
    private Spinner spinner;
    private Button btn_open_app;
    private ArrayAdapter<ProfileInformation> adapter;
    private List<ProfileInformation> list = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.tutorial_slide2,container,false);
        spinner = root.findViewById(R.id.spinner);
        btn_open_app = root.findViewById(R.id.btn_open_app);
        btn_open_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        adapter = new ArrayAdapter<ProfileInformation>(getActivity(),android.R.layout.simple_spinner_item){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

                View row = convertView;
                ProfileHolder holder = null;

                if(row == null) {
                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    row = inflater.inflate(R.layout.my_contacts_row, parent, false);
                    holder = new ProfileHolder(row,position);
                    row.setTag(holder);
                }
                else {
                    holder = (ProfileHolder) row.getTag();
                }

                ProfileInformation profileInformation = list.get(position);
                holder.txt_name.setText(profileInformation.getName());
                //holder.imgIcon.setImageResource(weather.icon);

                return row;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View row = convertView;
                ProfileHolder holder = null;

                if(row == null) {
                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    row = inflater.inflate(R.layout.my_contacts_row, parent, false);
                    holder = new ProfileHolder(row,position);
                    row.setTag(holder);
                }
                else {
                    holder = (ProfileHolder) row.getTag();
                }

                ProfileInformation profileInformation = list.get(position);
                holder.txt_name.setText(profileInformation.getName());
                //holder.imgIcon.setImageResource(weather.icon);

                return row;
            }
        };
        adapter.setDropDownViewResource(R.layout.my_contacts_row);
        spinner.setAdapter(adapter);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (profilesModule!=null) {
            load();
        }
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        load();
    }

    private void load() {
        list = profilesModule.getLocalProfiles();
        adapter.clear();
        adapter.addAll(list);
        adapter.notifyDataSetChanged();
    }

    public String getSelectedProfileKey() {
        return ((ProfileInformation) spinner.getSelectedItem()).getHexPublicKey();
    }
}
