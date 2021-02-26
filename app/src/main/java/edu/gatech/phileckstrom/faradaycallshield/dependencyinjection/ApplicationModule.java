package edu.gatech.phileckstrom.faradaycallshield.dependencyinjection;

import android.app.Application;

import dagger.Module;
import dagger.Provides;
import edu.gatech.phileckstrom.faradaycallshield.misc.FaradayCallshieldApplication;

//Required DI Module
@Module
public class ApplicationModule {
    public final FaradayCallshieldApplication application;
    public ApplicationModule(FaradayCallshieldApplication application) {
        this.application = application;
    }

    @Provides
    FaradayCallshieldApplication provideRoomDemoApplication(){
        return application;
    }

    @Provides
    Application provideApplication(){
        return application;
    }
}
