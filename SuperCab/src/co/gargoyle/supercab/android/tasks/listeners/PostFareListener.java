package co.gargoyle.supercab.android.tasks.listeners;

import com.google.common.base.Optional;

public interface PostFareListener {

  public void completed(Optional<Long> fareId);

  public void handleError(Throwable exception);

}
