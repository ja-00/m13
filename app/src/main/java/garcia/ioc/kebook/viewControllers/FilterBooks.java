package garcia.ioc.kebook.viewControllers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import garcia.ioc.kebook.R;

public class FilterBooks extends AppCompatActivity {

    private TextView title = null;
    private TextView keyValue = null;
    private EditText inputValue = null;
    private Button aplicar = null;
    private String reply = null;
    private String extraName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_books);
        getSupportActionBar().setTitle("Filtre");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        title = findViewById(R.id.lbl_filter_option);
        keyValue = findViewById(R.id.by_Value);
        keyValue.setVisibility(View.GONE);
        inputValue = findViewById(R.id.input_filter_value);
        inputValue.setVisibility(View.GONE);
        aplicar = findViewById(R.id.btn_apply);


    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_filter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.by_title:
                keyValue.setText(R.string.by_tittle);
                keyValue.setVisibility(View.VISIBLE);
                inputValue.setVisibility(View.VISIBLE);
                extraName = "titulo";
                break;

            case R.id.by_author:
                keyValue.setText(R.string.by_author);
                keyValue.setVisibility(View.VISIBLE);
                inputValue.setVisibility(View.VISIBLE);
                extraName = "autor";
                break;
        }
        return true;
    }


    public void aplicaFilter(View view) {
        Intent replyIntent = new Intent();
        //Bundle bundle = new Bundle();

        reply = inputValue.getText().toString();
        //bundle.putString("title", reply);
        replyIntent.putExtra("key", extraName);
        replyIntent.putExtra("value", reply);
        setResult(RESULT_OK, replyIntent);
        finish();
    }
}