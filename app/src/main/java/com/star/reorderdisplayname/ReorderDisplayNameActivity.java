package com.star.reorderdisplayname;

import android.support.v4.app.Fragment;

public class ReorderDisplayNameActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return ReorderDisplayNameFragment.newInstance();
    }

}
