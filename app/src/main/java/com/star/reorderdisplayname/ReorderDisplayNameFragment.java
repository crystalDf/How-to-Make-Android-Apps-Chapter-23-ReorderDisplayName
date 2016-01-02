package com.star.reorderdisplayname;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import java.util.ArrayList;
import java.util.List;

public class ReorderDisplayNameFragment extends Fragment {

    private RecyclerView mDisplayNameRecyclerView;

    private List<String> queryDisplayNames = new ArrayList<>();

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

        queryDisplayNames.add("haha");
        queryDisplayNames.add("hehe");

        mDisplayNameRecyclerView.setAdapter(new DisplayNameAdapter(queryDisplayNames));

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
