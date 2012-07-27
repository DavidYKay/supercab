package co.gargoyle.supercab.android.tasks.listeners;

import co.gargoyle.supercab.android.model.Fare;

import com.google.common.base.Optional;

public interface GetFareListener {

  public void completed(Optional<Fare> fares);

  public void handleError(Throwable throwable);
  
  public void unauthorized();

}
