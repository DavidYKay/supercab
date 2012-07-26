package co.gargoyle.supercab.android.activities;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import co.gargoyle.supercab.android.R;
import co.gargoyle.supercab.android.exceptions.FormIncompleteException;
import co.gargoyle.supercab.android.exceptions.PasswordsDontMatchException;
import co.gargoyle.supercab.android.model.UserModel;
import co.gargoyle.supercab.android.tasks.RegisterTask;
import co.gargoyle.supercab.android.tasks.listeners.PostUserListener;
import co.gargoyle.supercab.android.utilities.AlertUtils;
import co.gargoyle.supercab.android.utilities.StringUtils;

import com.google.common.base.Optional;
import com.google.inject.Inject;

//public class RegisterActivity extends LoginActivity {
public class RegisterActivity extends RoboActivity {

  private static final String TAG = "RegisterActivity";

  private static final int PROGRESS_DIALOG = 1;
  private static final int AUTH_FAILED_DIALOG = 2;

  protected ProgressDialog mProgressDialog;

  private Optional<RegisterTask> mRegisterTask = Optional.absent();
  private String mAlertMsg;

  @InjectView(R.id.edit_username) EditText mEditUsername;
  @InjectView(R.id.edit_phone_number) EditText mEditPhoneNumber;
  @InjectView(R.id.edit_password) EditText mEditPassword;
  @InjectView(R.id.confirm_password) EditText mConfirmPassword;

  @InjectView(R.id.edit_last_name) EditText mEditLastName;
  @InjectView(R.id.edit_first_name) EditText mEditFirstName;

  @Inject protected AlertUtils mAlertUtils;
  //@Inject protected PreferenceUtils mPreferenceUtils;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.register);

    mAlertMsg = getString(R.string.please_wait);

//    mEditLastName.setOnEditorActionListener(mPasswordEnterListener);
  }

  ////////////////////////////////////////////////////////////
  // UI Methods
  ////////////////////////////////////////////////////////////

  public void onRegisterButtonClicked(View view) {
    // Kabloey
    Log.v(TAG, "BOOM");
    attemptRegister();
  }

  private UserModel getProfileFromUi() throws PasswordsDontMatchException, FormIncompleteException {
    String password1 =  mEditPassword.getText().toString();
    String password2 =  mConfirmPassword.getText().toString();

    if (!password1.equals(password2)) {
      throw new PasswordsDontMatchException("Your passwords don't match!");
    }

    UserModel user = new UserModel();

    String password =  password1;
    if (StringUtils.stringIsEmpty(password)) {
      throw new FormIncompleteException();
    }
    String username =  mEditUsername.getText().toString();
    if (StringUtils.stringIsEmpty(username)) {
      throw new FormIncompleteException();
    }
    String phoneNumber =  mEditPhoneNumber.getText().toString();
    if (StringUtils.stringIsEmpty(phoneNumber)) {
      throw new FormIncompleteException();
    }

    String lastName =  mEditLastName.getText().toString();
    if (StringUtils.stringIsEmpty(lastName)) {
      throw new FormIncompleteException();
    }
    String firstName =  mEditFirstName.getText().toString();
    if (StringUtils.stringIsEmpty(firstName)) {
      throw new FormIncompleteException();
    }

    return user;
  }

  ////////////////////////////////////////////////////////////
  // Register
  ////////////////////////////////////////////////////////////

  private void attemptRegister() {
    try {
      beginNetworkRegister(getProfileFromUi());
      showDialog(PROGRESS_DIALOG);
    } catch (PasswordsDontMatchException e) {
      e.printStackTrace();
      goBlooey(e);
    } catch (FormIncompleteException e) {
      e.printStackTrace();
      goBlooey(e);
    }
  }

  private void beginNetworkRegister(final UserModel profile) {
    RegisterTask task = new RegisterTask(new PostUserListener() {

      @Override
      public void handleError(Throwable throwable) {
        mProgressDialog.dismiss();
        goBlooey(throwable);
      }

      @Override
      public void completed(Optional<UserModel> user) {
        if (user.isPresent()) {
          mProgressDialog.dismiss();
          saveProfileAndProceedToApp(profile);
        } else {
          mProgressDialog.dismiss();
          goBlooey(new Exception(""));
        }
      }


    });

    mRegisterTask = Optional.of(task);

    task.execute(profile);
  }

  ////////////////////////////////////////////////////////////
  // Nav
  ////////////////////////////////////////////////////////////

  private void saveProfileAndProceedToApp(UserModel profile) {
    // TODO Save profile ????
    proceedToApp();
  }

  private void proceedToApp() {
    startActivity(new Intent(RegisterActivity.this, HailActivity.class));
    finish();
  }


  ////////////////////////////////////////////////////////////
  // Utils
  ////////////////////////////////////////////////////////////

  void goBlooey(Throwable t) {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);

    builder.setTitle("Error!").setMessage(t.getMessage()).setPositiveButton("OK", null).show();
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
          if (mRegisterTask.isPresent()) {
            mRegisterTask.get().cancel(true);
          }
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
}
