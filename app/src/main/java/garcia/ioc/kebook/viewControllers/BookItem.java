package garcia.ioc.kebook.viewControllers;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import garcia.ioc.kebook.R;

public class BookItem extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_item);
    }
}