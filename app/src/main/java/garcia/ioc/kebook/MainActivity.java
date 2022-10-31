package garcia.ioc.kebook;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

/**
 * Clase principal
 */
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

        mUser = (EditText) findViewById(R.id.inputUser);
        mPass = (EditText) findViewById(R.id.inputPassword);
        mButtonLogin = (Button) findViewById(R.id.buttonLogin);
        mSwitchAdmin = (Switch) findViewById(R.id.switchAdmin);
    }
    /**
     *
     * @param view
     * @throws ExecutionException
     * @throws InterruptedException
     *
     * Método login. Conecta con el server y resuelve si la conexión ha sido correcta
     */
    public void login(View view) throws ExecutionException, InterruptedException {
        // Obtener los String de user y password de los editText y declarar e inicializar las variables
        String user = mUser.getText().toString();
        String pass = mPass.getText().toString();
        String jwToken;
        Intent dashUser = new Intent(this, DashUser.class);
        Intent dashAdmin = new Intent(this, DashAdmin.class);

        // Conexión con el server
        if (mSwitchAdmin.isChecked()) {
            jwToken = new Login().execute(user, pass, "admin").get();
            if ((jwToken.contains("error")) || jwToken.isEmpty()) {
                showDialogError();
            } else {
                startActivity(dashAdmin);
                finish();
            }
        } else {
            jwToken = new Login().execute(user, pass, null).get();
            if ((jwToken.contains("error")) || jwToken.isEmpty()) {
                showDialogError();
            } else {
                startActivity(dashUser);
                finish();
            }
        }
        Log.d("Info", "En MainActivity.login. Token rebut: " + jwToken);
    }

    /**
     * Ventana de alerta si la conexión no ha sido correcta
     */
    private void showDialogError() {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        //builder.setMessage("Vols sortir de l'aplicació?");

        //Mensaje a mostrar y acción con la opción positiva
        builder.setMessage("Usuari i/o password no son correctes")
                .setCancelable(false)
                .setPositiveButton("Sortir", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Acción botón sortir, salir de la aplicacióin
                        finish();
                        System.exit(0);
                    }
                })
                .setNegativeButton("Tornar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Accíón botón Tornar, vuelve a login
                        Toast.makeText(getApplicationContext(),"Cancelar",Toast.LENGTH_SHORT).show();
                    }
                });
        // Crear caja de alerta
        AlertDialog alert = builder.create();
        alert.setTitle("Atenció");
        alert.show();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void registre(View view) {
        
    }
}

