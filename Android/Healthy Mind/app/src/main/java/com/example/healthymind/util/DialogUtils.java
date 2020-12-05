package com.example.healthymind.util;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class DialogUtils {

    public static String showDialogFragment(FragmentManager fragmentManager, DialogFragment dialogFragment,
                                            String fragmentTag, boolean onlyIfNotDuplicate) {

        // If only showing non duplicates dialogs, make sure the fragment isn't already in the manager
        boolean doesFragmentExist = fragmentManager.findFragmentByTag(fragmentTag) != null;
        if (!(onlyIfNotDuplicate && doesFragmentExist)) {
            dialogFragment.show(fragmentManager, fragmentTag);
        }

        return fragmentTag;
    }

    public static String showDialogFragment(FragmentManager fragmentManager, DialogFragment dialogFragment,
                                            boolean onlyIfNotDuplicate) {
        return showDialogFragment(fragmentManager, dialogFragment, generateFragmentTag(dialogFragment), onlyIfNotDuplicate);
    }

    public static String showDialogFragment(FragmentManager fragmentManager, DialogFragment dialogFragment,
                                            String fragmentTag) {
        return showDialogFragment(fragmentManager, dialogFragment, fragmentTag, true);
    }

    public static String showDialogFragment(FragmentManager fragmentManager, DialogFragment dialogFragment) {
        return showDialogFragment(fragmentManager, dialogFragment, generateFragmentTag(dialogFragment));
    }

    private static String generateFragmentTag(Fragment fragment) {
        return fragment.getClass().getName();
    }
}
