

package edu.gatech.phileckstrom.faradaycallshield.misc;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;


public abstract class BaseActivity extends AppCompatActivity {

    public static void addFragmentToActivity (FragmentManager fragmentManager,
                                              Fragment fragment,
                                              int frameId,
                                              String tag) {

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(frameId, fragment, tag);
        transaction.commit();
    }
}
