package edu.gatech.phileckstrom.faradaycallshield.misc;

import android.app.Application;

import edu.gatech.phileckstrom.faradaycallshield.dependencyinjection.ApplicationComponent;
import edu.gatech.phileckstrom.faradaycallshield.dependencyinjection.ApplicationModule;
import edu.gatech.phileckstrom.faradaycallshield.dependencyinjection.DaggerApplicationComponent;
import edu.gatech.phileckstrom.faradaycallshield.dependencyinjection.RoomModule;


public class FaradayCallshieldApplication extends Application {
    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        applicationComponent = DaggerApplicationComponent
                .builder()
                .applicationModule(new ApplicationModule(this))
                .roomModule(new RoomModule(this)).build();

    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }
}
