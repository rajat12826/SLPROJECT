package com.example.attendancemanager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class TabAdapter extends FragmentStateAdapter {

    public TabAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new AttendanceFragment();
            case 1:
                return new NotesFragment();
            case 2:
                return new ProfileFragment();
            default:
                return new AttendanceFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
