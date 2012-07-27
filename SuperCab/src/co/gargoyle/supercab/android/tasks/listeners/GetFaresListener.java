package co.gargoyle.supercab.android.tasks.listeners;

import java.util.List;

import co.gargoyle.supercab.android.model.Fare;

public interface GetFaresListener {

  public void completed(List<Fare> fares);

  public void handleError(Throwable throwable);

}
