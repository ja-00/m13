package garcia.ioc.kebook.viewControllers;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.concurrent.ExecutionException;

import garcia.ioc.kebook.R;
import garcia.ioc.kebook.controllers.AsyncManager;

public class ResenaView extends AppCompatActivity {

    private TextView titulo;
    private TextView autor;
    private Button resena;
    private String token;
    private String isbn;
    private String idUsuario;
    private String hayResenas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resena);

        titulo = findViewById(R.id.titulo_book);
        autor = findViewById(R.id.autor_book);

        titulo.setText("Titol: " + getIntent().getStringExtra("titulo"));
        titulo.setText("Autor: " + getIntent().getStringExtra("autor"));

        resena = findViewById(R.id.btn_resena);
    }

    public void publicarResena(View view) {
        finish();
    }
}