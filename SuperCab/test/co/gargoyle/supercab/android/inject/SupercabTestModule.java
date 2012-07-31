package co.gargoyle.supercab.android.inject;

import roboguice.util.Ln;
import android.util.Log;
import co.gargoyle.supercab.android.activities.FareDetailActivity;

import com.google.inject.AbstractModule;

public class SupercabTestModule extends AbstractModule {

  @Override 
  protected void configure() {
//    Context context = Robolectric.application;
    
    bind(FareDetailActivity.class);    
    //bind(PhoneUtils.class).toInstance(new MockPhoneUtils(context));
//    bind(PhoneUtils.class).to(MockPhoneUtils.class);
    
    //bind(CKSingleton.class);
//    bind(CKSingleton.class).toInstance(new CKSingleton(context));
    //requestStaticInjection( CKSingleton.class );

//    bind(Counter.class).in(Scopes.SINGLETON);
//    bind(Date.class).toProvider(FakeDateProvider.class);
//    bind(Ln.BaseConfig.class).toInstance(new RobolectricLoggerConfig());
  }

  static class RobolectricLoggerConfig extends Ln.BaseConfig {
    public RobolectricLoggerConfig() {
      super();
//      this.packageName = "robo";
      this.minimumLogLevel = Log.VERBOSE;
//      this.scope = "ROBO";
    }
  }
}
