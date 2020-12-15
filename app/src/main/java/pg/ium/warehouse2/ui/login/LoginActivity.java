package pg.ium.warehouse2.ui.login;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import pg.ium.warehouse2.R;
import pg.ium.warehouse2.network.Connection;
import pg.ium.warehouse2.network.Credentials;

public class LoginActivity extends AppCompatActivity {

    private int RC_SIGN_IN = 0x01;
    private Connection connection;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken("293750822743-21b25nnboijc6f2bn8p73qpkt77ihajq.apps.googleusercontent.com")
                .build();

        final GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);


        TextWatcher passwordListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // nothing to do here
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // nothing to do here
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() > 6)
                    loginButton.setEnabled(true);
                else
                    loginButton.setEnabled(false);
            }
        };

        passwordEditText.addTextChangedListener(passwordListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    signIn();
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    public void signIn() {
        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        if(password.length() < 7) {
            Toast.makeText(getApplicationContext(), "Your password is too short", Toast.LENGTH_SHORT).show();
            return;
        }

        connection = new Connection(this);
        Credentials credentials = new Credentials(username, password);
        connection.signIn(credentials);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if(account != null) {
                if(account.getDisplayName() != null)
                    Log.w("Display name:", account.getDisplayName());
                if(account.getEmail() != null)
                    Log.w("Email:", account.getEmail());
                String id_token = account.getIdToken();
                if(account.getIdToken() != null)
                    Log.w("Token:", account.getIdToken());
                Connection connection = new Connection(this);
                connection.token(id_token);
            }
            // Signed in successfully, show authenticated UI.
            Log.w("LOGGING", account.toString());
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("LOGGING", "signInResult:failed code=" + e.getStatusCode());
        }
    }


}