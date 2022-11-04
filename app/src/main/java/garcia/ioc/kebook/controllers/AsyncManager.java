package garcia.ioc.kebook.controllers;

import android.os.AsyncTask;
import android.util.Log;

import garcia.ioc.kebook.controllers.RequestManager;

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
        Log.d("Info", "Dentro de Login.doinbackground");
        switch (strings[0]) {
            case "login":
                if (strings[3].equals("admin")) {
                    return RequestManager.login(strings[1], strings[2], true);
                } else {
                    return RequestManager.login(strings[1], strings[2], false);
                }
            case "register":
                return RequestManager.register(strings[1], strings[2], strings[3]);
            case "deleteUser":
                return RequestManager.deleteUser(strings[1], strings[2]);
            case "logout":
                return RequestManager.logout(strings[1]);
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
