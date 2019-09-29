package edu.gatech.phileckstrom.faradaycallshield.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import edu.gatech.phileckstrom.faradaycallshield.ui.fragment.ActivityLogFragment;
import edu.gatech.phileckstrom.faradaycallshield.ui.fragment.BlackListFragment;
import edu.gatech.phileckstrom.faradaycallshield.ui.fragment.MessageFragment;
import edu.gatech.phileckstrom.faradaycallshield.ui.fragment.RulesFragment;


public class MainPagerAdapter extends FragmentStatePagerAdapter {
    private int num;

    public MainPagerAdapter(FragmentManager fm, int num) {
        super(fm);
        this.num = num;
    }

    @Override
    public Fragment getItem(int position) {

        // Do NOT try to save references to the Fragments in getItem(),
        // because getItem() is not always called. If the Fragment
        // was already created then it will be retrieved from the FragmentManger
        // and not here (i.e. getItem() won't be called again).

        switch (position) {
            case 0:
                return new BlackListFragment();
            case 1:
                return new MessageFragment();
            case 2:
                return new ActivityLogFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return num;
    }
}
