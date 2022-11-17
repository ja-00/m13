package garcia.ioc.kebook.viewControllers;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.concurrent.ExecutionException;

import garcia.ioc.kebook.R;

public class ChangePassDialogFragment extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.change_pass_dialog_fragment, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final EditText pass1 = view.findViewById(R.id.newPass1);
        final EditText pass2 = view.findViewById(R.id.newPass2);

        if (getArguments() != null && !TextUtils.isEmpty(getArguments().getString("pass1")))
            pass1.setText(getArguments().getString("pass1"));
        if (getArguments() != null && !TextUtils.isEmpty(getArguments().getString("pass2")))
            pass1.setText(getArguments().getString("pass2"));

        Button btnDone = view.findViewById(R.id.btn_fet);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogListener dialogListener = (DialogListener) getActivity();
                try {
                    dialogListener.onFinishEditDialog(pass1.getText().toString(), pass2.getText().toString());
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
    public void onDestroyView() {
        super.onDestroyView();
    }

    public interface DialogListener {
        void onFinishEditDialog(String pass1, String pass2) throws ExecutionException, InterruptedException;
    }
}
