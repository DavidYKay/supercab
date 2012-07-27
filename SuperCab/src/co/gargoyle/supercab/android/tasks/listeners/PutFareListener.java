package co.gargoyle.supercab.android.tasks.listeners;

import co.gargoyle.supercab.android.model.Fare;

import com.google.common.base.Optional;

public interface PutFareListener {

  public void completed(Optional<Fare> fare);

  public void handleError(Throwable exception);

}
