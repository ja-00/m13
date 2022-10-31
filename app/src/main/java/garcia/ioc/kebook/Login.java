package garcia.ioc.kebook;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.ref.WeakReference;

public class Login extends AsyncTask<String, Void, String> {

/*    private WeakReference<EditText> mUser;
    private WeakReference<EditText> mPass;*/

/*    Login(EditText user, EditText pass) {
        this.mUser = new WeakReference<>(user);
        this.mPass = new WeakReference<>(pass);
    }*/
    @Override
    protected String doInBackground(String... strings) {
        Log.d("Info","Dentro de Login.doinbackground");
        if (strings[2] == "admin") {
            return NetworkUtils.getToken(strings[0], strings[1], true);
        } else {
            return NetworkUtils.getToken(strings[0], strings[1], false);
        }

    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}
