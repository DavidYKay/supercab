package co.gargoyle.supercab.android.inject;


import org.junit.runners.model.InitializationError;

import roboguice.RoboGuice;
import roboguice.inject.ContextScope;
import android.app.Application;

import com.google.inject.Injector;
import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.RobolectricTestRunner;

public class InjectedTestRunner extends RobolectricTestRunner {

    public InjectedTestRunner(Class<?> testClass) throws InitializationError {
      super(testClass);
    }

    @Override public void prepareTest(Object test) {
      Application sampleApplication = Robolectric.application;
      Injector injector = RoboGuice.getInjector(sampleApplication);
      ContextScope scope = injector.getInstance(ContextScope.class);
      scope.enter(sampleApplication);
      injector.injectMembers(test);
    }

    @Override 
    protected Application createApplication() {
      //        Collect application = (Collect)super.createApplication();
      Application application = super.createApplication();

//      application.setModule(new RobolectricSampleTestModule());
      return application;
    }

//    @Override public void prepareTest(Object test) {
//        SampleRoboApplication application = (SampleRoboApplication) Robolectric.application;
//
//        //This project's application does not extend GuiceInjectableApplication therefore we need to enter the ContextScope manually.
//        Injector injector = application.getInjector();
//        ContextScope scope = injector.getInstance(ContextScope.class);
//        scope.enter(application);
//
//        injector.injectMembers(test);
//    }
}
