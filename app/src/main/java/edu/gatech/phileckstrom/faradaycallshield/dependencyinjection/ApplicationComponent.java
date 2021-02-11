

package edu.gatech.phileckstrom.faradaycallshield.dependencyinjection;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Component;
import edu.gatech.phileckstrom.faradaycallshield.MainActivity;
import edu.gatech.phileckstrom.faradaycallshield.receivers.CallReceiver;
import edu.gatech.phileckstrom.faradaycallshield.ui.dialog.AddBlacklistEntryDialogFragment;
import edu.gatech.phileckstrom.faradaycallshield.ui.fragment.BlackListFragment;
import edu.gatech.phileckstrom.faradaycallshield.ui.fragment.ActivityLogFragment;
import edu.gatech.phileckstrom.faradaycallshield.ui.fragment.MessageFragment;
import edu.gatech.phileckstrom.faradaycallshield.ui.fragment.RulesFragment;

//Main component for dependency injection
@Singleton
@Component(modules = {ApplicationModule.class, RoomModule.class})
public interface ApplicationComponent {

    void inject(BlackListFragment blackListFragment);
    void inject(MessageFragment messageFragment);
    void inject(RulesFragment rulesFragment);
    void inject(AddBlacklistEntryDialogFragment addBlacklistEntryDialogFragment);
    void inject(CallReceiver receiver);
    void inject(ActivityLogFragment logFragment);
    void inject(MainActivity mainActivity);


    Application application();

}
