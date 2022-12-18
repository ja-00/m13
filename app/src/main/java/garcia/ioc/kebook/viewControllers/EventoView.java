package garcia.ioc.kebook.viewControllers;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import garcia.ioc.kebook.R;

public class EventoView extends AppCompatActivity {

    private EditText etPlannedDate;
    private TextView titulo;
    private TextView autor;
    private Button propuestaFechaEvento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.evento);

        titulo = findViewById(R.id.titulo_book);
        autor = findViewById(R.id.autor_book);
        etPlannedDate = (EditText) findViewById(R.id.etPlannedDate);
        etPlannedDate.setOnClickListener(this::onClick);
        titulo.setText(getIntent().getStringExtra("titulo"));
        autor.setText(getIntent().getStringExtra("autor"));
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.etPlannedDate:
                showDatePickerDialog(etPlannedDate);
                break;
        }
    }

    public void showDatePickerDialog(View view) {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 because January is zero
                final String selectedDate = year + "-" + (month+1) + "-" + day;
                etPlannedDate.setText(selectedDate);
            }
        });
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void propuestaFechaEvento(View view) {
        Intent data = new Intent();
        data.putExtra("fecha_propuesta",etPlannedDate.getText().toString());
        setResult(RESULT_OK,data);
        finish();
    }
}