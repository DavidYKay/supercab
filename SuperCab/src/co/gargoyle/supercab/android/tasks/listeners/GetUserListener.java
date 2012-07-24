package co.gargoyle.supercab.android.tasks.listeners;

import co.gargoyle.supercab.android.model.UserProfile;

import com.google.common.base.Optional;


public interface GetUserListener {

  public void completed(Optional<UserProfile> user);

  public void handleError(Throwable throwable);

}
