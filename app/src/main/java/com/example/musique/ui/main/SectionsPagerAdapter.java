package com.example.musique.ui.main;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.musique.view.FragmentAlbums;
import com.example.musique.view.FragmentTracks;
import com.example.musique.R;

import org.jetbrains.annotations.NotNull;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tracks, R.string.albums};
    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @NotNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        switch (position) {
            case 1:
                fragment = new FragmentAlbums();
                break;

            case 0:
            default:
                fragment = new FragmentTracks();
                break;
        }
        return fragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 2;
    }
}