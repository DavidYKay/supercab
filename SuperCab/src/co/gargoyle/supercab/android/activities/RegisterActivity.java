package co.gargoyle.supercab.android.activities;

import roboguice.inject.InjectView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import co.gargoyle.supercab.android.R;
import co.gargoyle.supercab.android.model.UserProfile;
import co.gargoyle.supercab.android.tasks.PostUserTask;
import co.gargoyle.supercab.android.tasks.listeners.PostUserListener;

public class RegisterActivity extends LoginActivity {
  
  private static final String TAG = "RegisterActivity";
  
  @InjectView(R.id.edit_username) EditText mEditUsername;
  @InjectView(R.id.edit_email) EditText mEditEmail;
  @InjectView(R.id.edit_password) EditText mEditPassword;
  @InjectView(R.id.edit_password_confirm) EditText mConfirmPassword;

  @InjectView(R.id.edit_last_name) EditText mEditLastName;
  @InjectView(R.id.edit_first_name) EditText mEditFirstName;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.register);

    mEditLastName.setOnEditorActionListener(mPasswordEnterListener);
  }

  ////////////////////////////////////////////////////////////
  // UI Methods
  ////////////////////////////////////////////////////////////

  public void onRegisterButtonClicked(View view) {
    // Kabloey
    Log.v(TAG, "BOOM");
    attemptRegister();
  }
  
  private UserProfile getProfileFromUi() {
    String username = mEditUsername.getText().toString();
    String password = mEditPassword.getText().toString();

    UserProfile user = new UserProfile();

    return user;
  }

  ////////////////////////////////////////////////////////////
  // Register
  ////////////////////////////////////////////////////////////

  private void attemptRegister() {
    beginNetworkRegister(getProfileFromUi());
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
  
}
