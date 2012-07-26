package co.gargoyle.supercab.android.tasks.listeners;

import com.google.common.base.Optional;

public interface PostFareListener {

  public void completed(Optional<String> fareId);

  public void handleError(Throwable exception);

}
