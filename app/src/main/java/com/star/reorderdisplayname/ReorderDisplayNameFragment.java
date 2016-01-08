package com.star.reorderdisplayname;


import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;

import java.util.ArrayList;
import java.util.List;

public class ReorderDisplayNameFragment extends Fragment {

    private static final String TAG = "ReorderDisplayNameFragment";

    private RecyclerView mDisplayNameRecyclerView;
    private DisplayNameAdapter mDisplayNameAdapter;

    private SwitchCompat mSwitchCompat;

    private Button mQueryButton;
    private Button mUpdateButton;

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

        mSwitchCompat = (SwitchCompat) view.findViewById(R.id.select_reject_all_switch);
        mSwitchCompat.setClickable(mDisplayNameAdapter != null);
        mSwitchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                boolean checked = mSwitchCompat.isChecked();
                List<Boolean> displayNamesChecked = mDisplayNameAdapter.getDisplayNamesChecked();
                for (int i = 0; i < displayNamesChecked.size(); i++) {
                    displayNamesChecked.set(i, checked);
                }
                mDisplayNameAdapter.notifyDataSetChanged();

                mCheckedDisplayNames.clear();
                if (checked) {
                    mCheckedDisplayNames.addAll(mDisplayNameAdapter.getDisplayNames());
                }
            }
        });

        mQueryButton = (Button) view.findViewById(R.id.query_button);
        mQueryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new QueryContactsTask().execute();
            }
        });

        mUpdateButton = (Button) view.findViewById(R.id.update_button);
        mUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UpdateContactsTask().execute(mCheckedDisplayNames);
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

        public void bindDisplayName(String displayName, boolean checked) {
            mDisplayName = displayName;
            mChecked = checked;
            mDisplayNameCheckedTextView.setText(mDisplayName);
            mDisplayNameCheckedTextView.setChecked(mChecked);

            mDisplayNameCheckedTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setChecked(!mDisplayNameCheckedTextView.isChecked());
                }
            });
        }

        private void setChecked(boolean checked) {
            mDisplayNameAdapter.getDisplayNamesChecked().set(getAdapterPosition(), checked);
            mDisplayNameAdapter.notifyItemChanged(getAdapterPosition());

            if (checked) {
                if (!mCheckedDisplayNames.contains(mDisplayName)) {
                    mCheckedDisplayNames.add(mDisplayName);
                }
            } else {
                mCheckedDisplayNames.remove(mDisplayName);
            }
        }
    }

    private class DisplayNameAdapter extends RecyclerView.Adapter<DisplayNameHolder> {

        private List<String> mDisplayNames;
        private List<Boolean> mDisplayNamesChecked;

        public DisplayNameAdapter(List<String> displayNames, List<Boolean> displayNamesChecked) {
            mDisplayNames = displayNames;
            mDisplayNamesChecked = displayNamesChecked;
        }

        public List<String> getDisplayNames() {
            return mDisplayNames;
        }

        public List<Boolean> getDisplayNamesChecked() {
            return mDisplayNamesChecked;
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
            boolean displayNameChecked = mDisplayNamesChecked.get(position);
            holder.bindDisplayName(displayName, displayNameChecked);
        }

        @Override
        public int getItemCount() {
            return mDisplayNames.size();
        }
    }

    private class QueryContactsTask extends AsyncTask<Void, Void, Void> {

        private List<String> mQueryDisplayNames = new ArrayList<>();
        private List<Boolean> mQueryDisplayNamesChecked = new ArrayList<>();

        @Override
        protected Void doInBackground(Void... params) {

            mQueryDisplayNames.clear();
            mQueryDisplayNamesChecked.clear();

            Uri contactsUri = ContactsContract.Contacts.CONTENT_URI;
            String[] columns = new String[] {
                    ContactsContract.Contacts.DISPLAY_NAME
            };

            Cursor contactsCursor = getActivity().getContentResolver().query(
                    contactsUri, columns, null, null, null
            );

            if (contactsCursor == null) {
                return null;
            }

            try {
                while (contactsCursor.moveToNext()) {
                    String displayName = contactsCursor.getString(
                            contactsCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                    if (displayName.contains(" ")) {
                        mQueryDisplayNames.add(displayName);
                        mQueryDisplayNamesChecked.add(false);
                    }

                }
            } finally {
                contactsCursor.close();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mDisplayNameAdapter = new DisplayNameAdapter(
                    mQueryDisplayNames, mQueryDisplayNamesChecked);
            mDisplayNameRecyclerView.setAdapter(mDisplayNameAdapter);
            mSwitchCompat.setClickable(mDisplayNameAdapter != null);
        }
    }

    private class UpdateContactsTask extends AsyncTask<List<String>, String, Void> {

        private List<String> mUpdateDisplayNames = new ArrayList<>();

        @Override
        protected Void doInBackground(List<String>... params) {

            if (params == null) {
                return null;
            }

            mUpdateDisplayNames = params[0];

            for (String displayName : mUpdateDisplayNames) {
                Uri dataUri = ContactsContract.Data.CONTENT_URI;
                String[] columns = new String[] {
                        ContactsContract.CommonDataKinds.StructuredName.CONTACT_ID,
                        ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
                        ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME,
                        ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME
                };

                String whereClause = ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME
                        + " = ?" + " AND "
                        + ContactsContract.Data.MIMETYPE
                        + " = ?";
                String[] whereArgs = new String[] {
                        displayName,
                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
                };

                Cursor dataCursor = getActivity().getContentResolver().query(
                        dataUri, columns, whereClause, whereArgs, null
                );

                if (dataCursor == null) {
                    return null;
                }

                try {
                    while (dataCursor.moveToNext()) {
                        String contactId = dataCursor.getString(dataCursor.getColumnIndex(
                                ContactsContract.CommonDataKinds.StructuredName.CONTACT_ID));
                        String givenName = dataCursor.getString(dataCursor.getColumnIndex(
                                ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME));
                        String familyName = dataCursor.getString(dataCursor.getColumnIndex(
                                ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME));
                        String middleName = dataCursor.getString(dataCursor.getColumnIndex(
                                ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME));

                        givenName = TextUtils.isEmpty(givenName) ? "" : givenName;
                        familyName = TextUtils.isEmpty(familyName) ? "" : familyName;
                        middleName = TextUtils.isEmpty(middleName) ? "" : middleName;

                        ArrayList<ContentProviderOperation> contentProviderOperationList =
                                new ArrayList<>();

                        whereClause = ContactsContract.CommonDataKinds.StructuredName.CONTACT_ID
                                + " = ?" + " AND "
                                + ContactsContract.Data.MIMETYPE
                                + " = ?";
                        whereArgs = new String[] {
                                contactId,
                                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
                        };

                        String newDisplayName = familyName + middleName + givenName;
                        newDisplayName = newDisplayName.replace(" ", "");
                        Log.d(TAG, "familyName = " + familyName + " middleName = " + middleName +
                        " givenName = " + givenName + " newDisplayName = " + newDisplayName);
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(
                                ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME,
                                newDisplayName);
                        contentValues.put(
                                ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
                                "");
                        contentValues.put(
                                ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME,
                                "");

                        contentProviderOperationList.add(ContentProviderOperation
                                        .newUpdate(dataUri)
                                        .withSelection(whereClause, whereArgs)
                                        .withValues(contentValues)
                                        .build()
                        );

                        try {
                            getActivity().getContentResolver().applyBatch(
                                    ContactsContract.AUTHORITY, contentProviderOperationList);
                            publishProgress(displayName, newDisplayName);

                        } catch (RemoteException e) {
                            e.printStackTrace();
                        } catch (OperationApplicationException e) {
                            e.printStackTrace();
                        }

                    }
                } finally {
                    dataCursor.close();
                }
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            String displayName = values[0];
            String newDisplayName = values[1];

            List<String> displayNames = mDisplayNameAdapter.getDisplayNames();
            int position = displayNames.indexOf(displayName);

            displayNames.set(position, newDisplayName);
            mDisplayNameAdapter.notifyItemChanged(position);
        }
    }
}
