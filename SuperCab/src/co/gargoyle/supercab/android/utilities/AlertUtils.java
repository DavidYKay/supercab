package co.gargoyle.supercab.android.utilities;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import co.gargoyle.supercab.android.R;

import com.google.inject.Inject;

public class AlertUtils {

  private Context mContext;

  @Inject
  public AlertUtils(Context context) {
    mContext = context;
  }

  public AlertDialog createAlertDialog(int title, int message) {
    return createAlertDialog(
        mContext.getString(title),
        mContext.getString(message)
        );
  }

  public AlertDialog createAlertDialog(String title, String message) {
    return createAlertDialog(title, message, false);
  }

  public AlertDialog createAlertDialog(String title, String message,
      final boolean shouldExit) {
//    Collect.getInstance().getActivityLogger()
//        .logAction(this, "createAlertDialog", "show");
    AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
    alertDialog.setTitle(title);
    alertDialog.setMessage(message);
    DialogInterface.OnClickListener quitListener = new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int i) {
        switch (i) {
        case DialogInterface.BUTTON1: // ok
          // just close the dialog
          // successful download, so quit
          if (shouldExit) {
//            finish();
          }
          break;
        }
      }
    };
    alertDialog.setCancelable(false);
    alertDialog.setButton(mContext.getString(R.string.ok), quitListener);
    alertDialog.setIcon(android.R.drawable.ic_dialog_info);

    alertDialog.show();
    return alertDialog;
  }

}
