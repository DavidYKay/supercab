package co.gargoyle.supercab.android.tasks.listeners;

import co.gargoyle.supercab.android.model.UserModel;

import com.google.common.base.Optional;


public interface GetUserListener {

  public void completed(Optional<UserModel> user);

  public void handleError(Throwable throwable);

}
