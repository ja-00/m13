package garcia.ioc.kebook;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkUtils {

    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();
    // Base URL para la API Rest kebook.
    // ***URL provisional del mockserver de Postman
    private static final String KEBOOK_BASE_URL =  "https://c33a24fd-f159-447a-b9a3-e90678dca9a8.mock.pstmn.io/login";
    // ***URL provisional del mockserver de Postman
    //private static final String KEBOOK_BASE_URL =  "http://83.44.140.151:8080/login/nacho@gmail.com/nacho";


    static String getToken(String user, String password, boolean isAdmin){
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String tokenJSONString = null;
        Uri builtURI = null;

        try {

            if (isAdmin) {
                builtURI = Uri.parse(KEBOOK_BASE_URL).buildUpon()
                        .appendPath("admin")
                    .appendPath(user)
                    .appendPath(password)
                        .build();

            } else {
                builtURI = Uri.parse(KEBOOK_BASE_URL).buildUpon()
                    .appendPath(user)
                    .appendPath(password)
                        .build();
            }

            Log.d ("info", builtURI.toString());
            URL requestURL = new URL(builtURI.toString());

            urlConnection = (HttpURLConnection) requestURL.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.connect();

            // Obtener el InputStream.
            InputStream inputStream = urlConnection.getInputStream();

            // Crear el buffered reader del input stream.
            reader = new BufferedReader(new InputStreamReader(inputStream));

            // Usar un StringBuilder para guardar la respuesta
            StringBuilder builder = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                // Since it's JSON, adding a newline isn't necessary (it won't
                // affect parsing) but it does make debugging a *lot* easier
                // if you print out the completed buffer for debugging.
                builder.append("\n");
            }

            // Verificar si el stream está vacío
            if (builder.length() == 0) {
                return null;
            }

            tokenJSONString = builder.toString();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.d(LOG_TAG, tokenJSONString);
        return tokenJSONString;
    }

}
