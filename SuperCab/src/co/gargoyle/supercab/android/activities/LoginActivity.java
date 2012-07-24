package co.gargoyle.supercab.android.activities;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import co.gargoyle.supercab.android.R;
import co.gargoyle.supercab.android.model.UserCredentials;
import co.gargoyle.supercab.android.tasks.GetUserTask;
import co.gargoyle.supercab.android.tasks.listeners.GetUserListener;
import co.gargoyle.supercab.android.utilities.AlertUtils;
import co.gargoyle.supercab.android.utilities.PreferenceUtils;

import com.google.inject.Inject;

public class LoginActivity extends RoboActivity {

  private static final String TAG = "LoginActivity";

  private static final int PROGRESS_DIALOG = 1;
  private static final int AUTH_FAILED_DIALOG = 2;

  private String mAlertMsg;
  private boolean mAlertShowing = false;

  protected AlertDialog mAlertDialog;
  protected ProgressDialog mProgressDialog;

  @Inject protected AlertUtils mAlertUtils;
  @Inject protected PreferenceUtils mPreferenceUtils;

  @InjectView(R.id.edit_username) EditText mEditUsername;
  @InjectView(R.id.edit_password) EditText mEditPassword;

  ////////////////////////////////////////////////////////////
  // Activity Overrides
  ////////////////////////////////////////////////////////////

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (isLoggedIn()) {
      proceedToApp();
    } else {
      setContentView(R.layout.login);

      mAlertMsg = getString(R.string.please_wait);

      mEditPassword.setOnEditorActionListener(mPasswordEnterListener);
    }
  }

  @Override
  protected Dialog onCreateDialog(int id) {
    switch (id) {
    case PROGRESS_DIALOG:
      mProgressDialog = new ProgressDialog(this);
      DialogInterface.OnClickListener loadingButtonListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          dialog.dismiss();
          // End the tasks
        }
      };
      mProgressDialog.setTitle(getString(R.string.downloading_data));
      mProgressDialog.setMessage(mAlertMsg);
      mProgressDialog.setIcon(android.R.drawable.ic_dialog_info);
      mProgressDialog.setIndeterminate(true);
      mProgressDialog.setCancelable(false);
      mProgressDialog.setButton(getString(R.string.cancel),
          loadingButtonListener);
      return mProgressDialog;
    case AUTH_FAILED_DIALOG:
      return mAlertUtils.createAlertDialog(
          R.string.auth_failed,
          R.string.please_correct_username
          );
    }
    return null;
  }

  ////////////////////////////////////////////////////////////
  // UI Methods
  ////////////////////////////////////////////////////////////

  public void onLoginButtonClicked(View view) {
    // Kabloey
    Log.v(TAG, "BOOM");
    attemptLogin();
  }
  
  public void onRegisterButtonClicked(View view) {
    // Kabloey
    Log.v(TAG, "BOOM");
    
    startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    finish();
  }

  TextView.OnEditorActionListener mPasswordEnterListener = new TextView.OnEditorActionListener() {
    public boolean onEditorAction(TextView exampleView, int actionId, KeyEvent event) {
      if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_DOWN) {
        // match this behavior to your 'Send' (or Confirm) button
        attemptLogin();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEditPassword.getWindowToken(), 0);
        return true;
      }
      return false;
    }
  };
  

  ////////////////////////////////////////////////////////////
  // Login
  ////////////////////////////////////////////////////////////

  /**
   * Attempts login with fixed username / password EditTexts
   */
  private void attemptLogin() {
    showDialog(PROGRESS_DIALOG);
    beginNetworkLogin(getCredentialsFromUi());
  }
  
  private UserCredentials getCredentialsFromUi() {
    String username = mEditUsername.getText().toString();
    String password = mEditPassword.getText().toString();

    return new UserCredentials(username, password);
  }

  private void beginNetworkLogin(final UserCredentials credentials) {
    GetUserTask task = new GetUserTask(new GetUserListener() {

      @Override
      public void handleError(Throwable throwable) {
        mProgressDialog.dismiss();
        goBlooey(throwable);
      }

      @Override
      public void completed(Boolean success) {
        mProgressDialog.dismiss();
        saveCredentialsAndProceedToApp(credentials);
      }
    });

    task.execute(credentials);
  }

  private boolean isLoggedIn() {
    return mPreferenceUtils.hasCredentials();
  }

  ////////////////////////////////////////////////////////////
  // Login Complete
  ////////////////////////////////////////////////////////////

  private void proceedToApp() {
    // TODO: Detect if driver
    startActivity(new Intent(LoginActivity.this, HailActivity.class));
    finish();
  }

  private void saveCredentialsAndProceedToApp(UserCredentials credentials) {
    mPreferenceUtils.saveCredentials(credentials);

    // finish login and proceed
    proceedToApp();
  }

  ////////////////////////////////////////////////////////////
  // Utils
  ////////////////////////////////////////////////////////////

  void goBlooey(Throwable t) {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);

    builder.setTitle("Exception!").setMessage(t.toString()).setPositiveButton("OK", null).show();
  }

}
