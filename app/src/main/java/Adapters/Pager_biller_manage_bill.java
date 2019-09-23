package Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.teamcipher.mrfinman.mrfinmanfinal.Biller.manage_tab_1_pending;
import com.teamcipher.mrfinman.mrfinmanfinal.Biller.manage_tab_2_paid;
import com.teamcipher.mrfinman.mrfinmanfinal.Biller.manage_tab_3_overdue;

public class Pager_biller_manage_bill extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public Pager_biller_manage_bill(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int i) {

        switch (i) {
            case 0:
                manage_tab_1_pending tab1 = new manage_tab_1_pending();
                return tab1;
            case 1:
                manage_tab_2_paid tab2 = new manage_tab_2_paid();
                return tab2;
            case 2:
                manage_tab_3_overdue tab3 = new manage_tab_3_overdue();
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
