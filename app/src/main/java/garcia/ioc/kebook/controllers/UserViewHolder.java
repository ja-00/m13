package garcia.ioc.kebook.controllers;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutionException;

import garcia.ioc.kebook.R;
import garcia.ioc.kebook.models.User;
import garcia.ioc.kebook.viewControllers.DashAdmin;

public class UserViewHolder extends RecyclerView.ViewHolder {
    private final TextView user;
    private final TextView id;
    private final TextView email;
    private final TextView dateCreation;
    private final TextView isAdmin;

    public UserViewHolder(@NonNull View itemView) {
        super(itemView);
        user = itemView.findViewById(R.id.user_fila);
        id = itemView.findViewById(R.id.id_fila);
        email = itemView.findViewById(R.id.email_fila);
        dateCreation = itemView.findViewById(R.id.date_creation_fila);
        isAdmin = itemView.findViewById(R.id.is_admin_fila);
    }

    void bindRow(@NonNull User user, UserAdapter.OnItemClickListener listener) {
        this.user.setText("Nom d'usuari: " + user.getNombre());
        id.setText("Id d'usuari: " + String.valueOf(user.getId()));
        email.setText("Email: " + user.getCorreo());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        dateCreation.setText("Data de creació: " + format.format(user.getFecha_creacion()));
        isAdmin.setText("Es admin?: " + String.valueOf(user.isAdmin()));

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                listener.onItemClick(user);
            }
        });
    }

}
