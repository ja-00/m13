package garcia.ioc.kebook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private EditText mUser;
    private EditText mPass;
    private Button mButtonLogin;
    private Switch mSwitchAdmin;
    private boolean mSwitchAdminState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUser = (EditText)findViewById(R.id.inputUser);
        mPass = (EditText)findViewById(R.id.inputPassword);
        mButtonLogin = (Button)findViewById(R.id.buttonLogin);
        mSwitchAdmin = (Switch)findViewById(R.id.switchAdmin);

    }

    public void login (View view) throws ExecutionException, InterruptedException {
        // Obtener los String de user y password de los editText
        String user = mUser.getText().toString();
        String pass = mPass.getText().toString();
        String jwToken;
        Intent dashUser = new Intent(this, DashUser.class);
        Intent dashAdmin = new Intent(this, DashAdmin.class);
        //new Login(mUser, mPass).execute(user, pass); //ojo con esta linea
        //new Login().execute(user, pass);

        if (mSwitchAdmin.isChecked()) {
            jwToken = new Login().execute(user, pass, "admin").get();
            if (jwToken != null) {
                //dashAdmin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(dashAdmin);
                finish();

            }
            
        } else {
            jwToken = new Login().execute(user, pass, null).get();
            if (jwToken != null) {
                startActivity(dashUser);
            }
            
        }

        Log.d("Info", "En MainActivity.login" + jwToken);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}

