package com.os.bakingapp.adapters;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.os.bakingapp.fragments.StepFragment;
import com.os.bakingapp.models.Step;

import java.util.List;

/**
 * Created by Omar on 14-May-18 7:35 PM.
 */
public class StepsPagerAdapter extends FragmentStatePagerAdapter {
    private List<Step> steps;

    public StepsPagerAdapter(FragmentManager fm, List<Step> steps) {
        super(fm);
        this.steps = steps;
    }

    @Override
    public StepFragment getItem(int position) {
        return StepFragment.newInstance(steps.get(position));
    }

    @Override
    public int getCount() {
        return steps.size();
    }
}
