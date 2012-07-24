package co.gargoyle.supercab.android.tasks.listeners;

public interface PostUserListener {
  
  public void completed(Boolean success);

  public void handleError(Throwable throwable);

}
