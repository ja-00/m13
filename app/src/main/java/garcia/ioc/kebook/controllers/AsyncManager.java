package garcia.ioc.kebook.controllers;

import android.os.AsyncTask;
import android.util.Log;

public class AsyncManager extends AsyncTask<String, Void, String> {

    /**
     * @param strings Numero de parámetros variables que dependen de la funcionalidad que se quiera usar
     * @return Devuelve un String dependiendo de la funcionalidad
     */

/*    private WeakReference<EditText> mUser;
    private WeakReference<EditText> mPass;*/

/*    Login(EditText user, EditText pass) {
        this.mUser = new WeakReference<>(user);
        this.mPass = new WeakReference<>(pass);
    }*/
    @Override
    protected String doInBackground(String... strings) {
        Log.d("Info", "Dentro de AsyncManager.doinbackground");
        switch (strings[0]) {
            case "login":
                if (strings[3].equals("admin")) {
                    return RequestManager.login(strings[1], strings[2], true);
                } else if (strings[3].equals("user")) {
                    return RequestManager.login(strings[1], strings[2], false);
                }
            case "register":
                return RequestManager.registerUser(strings[1], strings[2], strings[3]);
            case "deleteUser":
                return RequestManager.deleteUser(strings[1], strings[2]);
            case "logout":
                return RequestManager.logout(strings[1]);
            case "changePassword":
                return RequestManager.changePassword(strings[1], strings[2], strings[3], strings[4]);
            case "usersList":
                return RequestManager.usersList(strings[1]);
            case "getUserWithId":
                return RequestManager.getUserWithId(strings[1], strings[2]);
            case "booksList":
                return RequestManager.booksList((strings[1]));
            case "filterBooksList":
                return RequestManager.filteredBooksList(strings[1], strings[2], strings[3]);
            case "addBook":
                return  RequestManager.addBook(strings[1], strings[2], strings[3], strings[4], strings[5], strings[6], strings[7], strings [8]);
            case "addAuthor":
                return RequestManager.addAuthor(strings[1], strings[2]);
            case "getAuthorWithName" :
                return RequestManager.getAuthorWithName(strings[1], strings[2]);
            case "getBooksOfBook":
                return RequestManager.getBooksOfBook(strings[1], strings[2]);
            case "reservarLibro":
                return RequestManager.reservarLibro(strings[1], strings[2]);
            case "confirmarRecogida":
                return RequestManager.confirmRecogida(strings[1], strings[2]);
            case "obtenerReservasLibroPorUsuario":
                return  RequestManager.obtenerReservasLibroPorUsuario(strings[1], strings[2], strings[3]);
            case "obtenerLibroPorIsbn":
                return RequestManager.obtenerLibroPorIsbn(strings[1], strings[2]);
            default:
                return null;

        }
/*        if (strings[2] == "admin") {
            return RequestManager.getToken(strings[0], strings[1], true);
        } else {
            return RequestManager.getToken(strings[0], strings[1], false);
        }*/

    }
}
