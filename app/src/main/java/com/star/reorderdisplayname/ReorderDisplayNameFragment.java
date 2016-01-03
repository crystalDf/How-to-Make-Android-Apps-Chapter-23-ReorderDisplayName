package com.star.reorderdisplayname;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;

import java.util.ArrayList;
import java.util.List;

public class ReorderDisplayNameFragment extends Fragment {

    private RecyclerView mDisplayNameRecyclerView;
    private DisplayNameAdapter mDisplayNameAdapter;

    private SwitchCompat mSwitchCompat;

    private Button mQueryButton;
    private Button mUpdateButton;

    private List<String> mQueryDisplayNames = new ArrayList<>();
    private List<String> mCheckedDisplayNames = new ArrayList<>();

    public static ReorderDisplayNameFragment newInstance() {
        return new ReorderDisplayNameFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reorder_display_name, container, false);

        mDisplayNameRecyclerView = (RecyclerView)
                view.findViewById(R.id.display_name_recycler_view);
        mDisplayNameRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mQueryDisplayNames.add("haha");
        mQueryDisplayNames.add("hehe");

        mDisplayNameAdapter = new DisplayNameAdapter(mQueryDisplayNames);

        mDisplayNameRecyclerView.setAdapter(mDisplayNameAdapter);

        mSwitchCompat = (SwitchCompat) view.findViewById(R.id.select_reject_all_switch);
        mSwitchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                boolean checked = mSwitchCompat.isChecked();
                for (int i = 0; i < mDisplayNameAdapter.getItemCount(); i++) {
                    DisplayNameHolder holder = (DisplayNameHolder)
                            mDisplayNameRecyclerView.findViewHolderForAdapterPosition(i);
                    holder.setChecked(checked);
                }
            }
        });

        mQueryButton = (Button) view.findViewById(R.id.query_button);
        mQueryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mUpdateButton = (Button) view.findViewById(R.id.update_button);
        mUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return view;
    }

    private class DisplayNameHolder extends RecyclerView.ViewHolder {

        private CheckedTextView mDisplayNameCheckedTextView;
        private String mDisplayName;
        private boolean mChecked;

        public DisplayNameHolder(View itemView) {
            super(itemView);

            mDisplayNameCheckedTextView = (CheckedTextView) itemView;
        }

        public void bindDisplayName(String displayName) {
            mDisplayName = displayName;
            mChecked = false;
            mDisplayNameCheckedTextView.setText(mDisplayName);
            mDisplayNameCheckedTextView.setChecked(mChecked);

            mDisplayNameCheckedTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setChecked(!mDisplayNameCheckedTextView.isChecked());
                }
            });
        }

        public void setChecked(boolean checked) {
            mDisplayNameCheckedTextView.setChecked(checked);
            if (checked) {
                mCheckedDisplayNames.add(mDisplayName);
            } else {
                mCheckedDisplayNames.remove(mDisplayName);
            }
        }
    }

    private class DisplayNameAdapter extends RecyclerView.Adapter<DisplayNameHolder> {

        private List<String> mDisplayNames;

        public DisplayNameAdapter(List<String> displayNames) {
            mDisplayNames = displayNames;
        }

        @Override
        public DisplayNameHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(
                    android.R.layout.simple_list_item_multiple_choice, parent, false);

            return new DisplayNameHolder(view);
        }

        @Override
        public void onBindViewHolder(DisplayNameHolder holder, int position) {
            String displayName = mDisplayNames.get(position);
            holder.bindDisplayName(displayName);
        }

        @Override
        public int getItemCount() {
            return mDisplayNames.size();
        }
    }
}
