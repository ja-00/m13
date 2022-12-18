package garcia.ioc.kebook.viewControllers;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

import garcia.ioc.kebook.R;
import garcia.ioc.kebook.controllers.AsyncManager;

public class Registre extends AppCompatActivity {

    private EditText mUser, mPass, mMail;
    private Button mButtonRegistrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registre);

        mUser = findViewById(R.id.inputUserReg);
        mMail = findViewById(R.id.inputEmailReg);
        mPass = findViewById(R.id.inputPasswordReg);
        mButtonRegistrar = findViewById(R.id.buttonRegister);
    }

    public void registrar(View view) throws ExecutionException, InterruptedException {
        String user = mUser.getText().toString();
        String mail = mMail.getText().toString();
        String pass = mPass.getText().toString();

        String response = new AsyncManager().execute("register", user, mail, pass).get();

        Toast.makeText(getApplicationContext(),"Usuari registrat correctament",Toast.LENGTH_SHORT).show();
        finish();
    }
}