package co.gargoyle.supercab.android.activities;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import co.gargoyle.supercab.android.R;
import co.gargoyle.supercab.android.exceptions.FormIncompleteException;
import co.gargoyle.supercab.android.exceptions.PasswordsDontMatchException;
import co.gargoyle.supercab.android.model.UserProfile;
import co.gargoyle.supercab.android.tasks.PostUserTask;
import co.gargoyle.supercab.android.tasks.listeners.PostUserListener;
import co.gargoyle.supercab.android.utilities.StringUtils;

//public class RegisterActivity extends LoginActivity {
public class RegisterActivity extends RoboActivity {
  
  private static final String TAG = "RegisterActivity";
  
  protected ProgressDialog mProgressDialog;
  
  @InjectView(R.id.edit_username) EditText mEditUsername;
  @InjectView(R.id.edit_phone_number) EditText mEditPhoneNumber;
  @InjectView(R.id.edit_password) EditText mEditPassword;
  @InjectView(R.id.edit_password_confirm) EditText mConfirmPassword;

  @InjectView(R.id.edit_last_name) EditText mEditLastName;
  @InjectView(R.id.edit_first_name) EditText mEditFirstName;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.register);

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
  
  private UserProfile getProfileFromUi() throws PasswordsDontMatchException, FormIncompleteException {
    String password1 =  mEditPassword.getText().toString();
    String password2 =  mConfirmPassword.getText().toString();

    if (!password1.equals(password2)) {
      throw new PasswordsDontMatchException("Your passwords don't match!");
    }

    UserProfile user = new UserProfile();

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
    } catch (PasswordsDontMatchException e) {
      e.printStackTrace();
      goBlooey(e);
    } catch (FormIncompleteException e) {
      e.printStackTrace();
      goBlooey(e);
    }
  }
  
  private void beginNetworkRegister(final UserProfile profile) {
    PostUserTask task = new PostUserTask(new PostUserListener() {

      @Override
      public void handleError(Throwable throwable) {
        mProgressDialog.dismiss();
        goBlooey(throwable);
      }

      @Override
      public void completed(Boolean success) {
        mProgressDialog.dismiss();
        saveProfileAndProceedToApp(profile);
      }

     
    });

    task.execute(profile);
  }

  private void saveProfileAndProceedToApp(UserProfile profile) {
    // TODO Auto-generated method stub
    
  }
  
  void goBlooey(Throwable t) {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);

    builder.setTitle("Error!").setMessage(t.getMessage()).setPositiveButton("OK", null).show();
  }
  
}
