package garcia.ioc.kebook;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

/**
 * Clase que crea el dashboard del usuario
 */
public class DashUser extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dash_user);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle("Kebook");

    }

    /**
     * Menú para el dashboard
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
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

        //Mensaje de advertencia
        builder.setMessage("Vols sortir de l'aplicació?")
                .setCancelable(false)
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Acción si se pulsa Si
                        finish();
                        System.exit(0);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Acción si se pulsa No
                        Toast.makeText(getApplicationContext(),"Cancelar",Toast.LENGTH_SHORT).show();
                    }
                });
        // Crear el mensaje de alerta
        AlertDialog alert = builder.create();
        // Título
        alert.setTitle("Atenció");
        alert.show();
    }
}