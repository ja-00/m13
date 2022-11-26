package garcia.ioc.kebook.viewControllers;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.concurrent.ExecutionException;

import garcia.ioc.kebook.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddBookDialogFragment} factory method to
 * create an instance of this fragment.
 */
public class AddBookDialogFragment extends DialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_book_dialog_fragment, container, false);
    }

    @Override
    public void onViewCreated (@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final EditText isbn = view.findViewById(R.id.input_isbn);
        final EditText title = view.findViewById(R.id.input_titulo);
        final EditText autor = view.findViewById(R.id.input_autor);
        final EditText sinopsis = view.findViewById(R.id.input_sinopsis);
        final EditText genre = view.findViewById(R.id.input_genero);

        Button btnAddBook = view.findViewById(R.id.btn_anadir);
        btnAddBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddBookDialogListener addBookDialogListener = (AddBookDialogListener) getActivity();
                try {
                    // TODO Revisar Warning
                    addBookDialogListener.onFinishAddBookDialog(isbn.getText().toString(), title.getText().toString(), autor.getText().toString(), sinopsis.getText().toString(), genre.getText().toString());
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                dismiss();
            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        // TODO Revisar Warning
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public interface AddBookDialogListener {
        void onFinishAddBookDialog(String isbn, String title, String autor, String sinopsis, String genre) throws ExecutionException, InterruptedException;
    }

}