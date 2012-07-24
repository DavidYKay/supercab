package co.gargoyle.supercab.android.tasks;

import com.google.common.base.Optional;

public interface GetUserListener {

  public void completed(Boolean success);

  public void handleError(Throwable throwable);

}
