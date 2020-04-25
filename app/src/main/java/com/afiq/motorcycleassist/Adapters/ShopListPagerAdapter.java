package com.afiq.motorcycleassist.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.afiq.motorcycleassist.Fragments.ApprovedShopFragment;
import com.afiq.motorcycleassist.Fragments.PendingShopFragment;

public class ShopListPagerAdapter extends FragmentStatePagerAdapter {

    int mNumOfTabs;

    public ShopListPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.mNumOfTabs = NumOfTabs;
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                ApprovedShopFragment tab1 = new ApprovedShopFragment();
                return tab1;
            case 1:
                PendingShopFragment tab2 = new PendingShopFragment();
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
