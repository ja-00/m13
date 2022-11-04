package garcia.ioc.kebook.viewControllers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Base64;
import java.util.concurrent.ExecutionException;

import garcia.ioc.kebook.R;
import garcia.ioc.kebook.controllers.AsyncManager;

/**
 * Clase que crea y gestiona el dashboard del usuario admin
 */
public class DashAdmin extends AppCompatActivity {

    String token = null;
    String id = null;
    String response = null;
    String loggedOut = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dash_admin);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle("Kebook");

        //Obtener datos pasados por el activity que ha lanzado el actual
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            token = extras.getString("token");
        }
        //Log.d ("Info: ", "Token recibido por el activity dashUser: " + token);

        String[] splitToken = token.split("\\.");

        Base64.Decoder decoder = Base64.getUrlDecoder();

        String header = new String(decoder.decode(splitToken[0]));
        String payload = new String(decoder.decode(splitToken[1]));
        Log.d ("Info: ", "Payload del recibido por el activity dashUser: " + payload);
        Gson JSONPayload = new Gson();
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(payload);
            id = jsonObject.getString("jti");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d ("Info: ", "Id extraido del Payload: " + id);
    }
    /**
     * Menú para el dashboard
     * @param menu Menú al que aplicar el inflater (Convertir el recurso XML en elemento programable)
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_user:
                try {
                    response = new AsyncManager().execute("deleteUser", token, id).get();
                    if (response.contains("true")) {
                        finish();
                        Toast.makeText(getApplicationContext(), "Compte eliminat correctament", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "No s'ha pogut eliminar el compte", Toast.LENGTH_SHORT).show();
                    }
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                return true;
            case R.id.change_pass:
                //showHelp();
                return true;
            case R.id.logout:
                try {
                    //Log.d("Info:", "En DashUser case logout con id: " + id);
                    response = new AsyncManager().execute("logout", id).get();
                    if (response.contains("true")) {
                        finish();
                        Toast.makeText(getApplicationContext(), "Sessió tancada correctament", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "No s'ha pogut tancar sessió", Toast.LENGTH_SHORT).show();
                    }
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Si se pulsa el botón de Atràs en el teléfono
     */
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        showExitDialog();
    }
    /**
     * Cuadro de aviso al pulsar el botón de Atrás del tele´fono
     */
    private void showExitDialog(){
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        //builder.setMessage("Vols sortir de l'aplicació?");

        //Setting message manually and performing action on button click
        builder.setMessage("Vols sortir de l'aplicació?")
                .setCancelable(false)
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'Yes' Button
                        //exit application
                        finish();
                        System.exit(0);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'No' Button
                        Toast.makeText(getApplicationContext(),"Cancelar",Toast.LENGTH_SHORT).show();
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("Atenció");
        alert.show();
    }
}