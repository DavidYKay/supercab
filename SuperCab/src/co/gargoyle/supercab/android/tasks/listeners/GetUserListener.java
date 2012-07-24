package co.gargoyle.supercab.android.tasks.listeners;


public interface GetUserListener {

  public void completed(Boolean success);

  public void handleError(Throwable throwable);

}
