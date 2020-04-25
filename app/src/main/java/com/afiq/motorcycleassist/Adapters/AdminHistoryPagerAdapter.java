package com.afiq.motorcycleassist.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.afiq.motorcycleassist.Fragments.GSRHistoryFragment;
import com.afiq.motorcycleassist.Fragments.RequestHistoryFragment;

public class AdminHistoryPagerAdapter extends FragmentStatePagerAdapter {

    int mNumOfTabs;

    public AdminHistoryPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.mNumOfTabs = NumOfTabs;
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                GSRHistoryFragment tab1 = new GSRHistoryFragment();
                return tab1;
            case 1:
                RequestHistoryFragment tab2 = new RequestHistoryFragment();
                return tab2;
            /*case 2:
                RescuerHistoryFragment tab3 = new RescuerHistoryFragment();
                return tab3;*/
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
