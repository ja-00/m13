package garcia.ioc.kebook;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

/**
 * Clase gestora de la conexión con el server
 */
public class NetworkUtils {

    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();
    // Base URL para la API Rest kebook.
    // ***URL provisional del mockserver de Postman
    private static final String KEBOOK_BASE_URL = "https://c33a24fd-f159-447a-b9a3-e90678dca9a8.mock.pstmn.io/login";
    // ***URL provisional
    //private static final String KEBOOK_BASE_URL =  "http://83.44.140.151:8080/login/nacho@gmail.com/nacho";

    /**
     * Método que realiza la conexión con el server con las credenciales de usuario (user y password) y el boolean que
     * indica si es un administrador. Retorna un String con la respuesta devuelta por el server.
     * @param user
     * @param password
     * @param isAdmin
     * @return
     */
    static String getToken(String user, String password, boolean isAdmin) {
        // Inicializar variables
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String tokenJSONString = null;
        URL url = null;
        Uri builtURI = null;
        // Construir la URI para cada caso: admin y user
        try {
            if (isAdmin) {
                url = new URL (KEBOOK_BASE_URL + "/admin" + "/" + user + "/" + password);
                //url.a
/*                builtURI = Uri.parse(KEBOOK_BASE_URL).buildUpon()
                        .appendPath("admin")
                        .appendPath(user)
                        .appendPath(password)
                        .build();*/
            } else {
                url = new URL (KEBOOK_BASE_URL + "/" + user + "/" + password);
/*                builtURI = Uri.parse(KEBOOK_BASE_URL).buildUpon()
                        .appendPath(user)
                        .appendPath(password)
                        .build();*/
            }

            //Log.d("info", builtURI.toString());
            //Log.d("info", url.toString());
            //URL requestURL = new URL(builtURI.toString());


            // Montar la URL y conectar con el server y el verbo correspondiente para la peti ción
            //urlConnection = (HttpURLConnection) requestURL.openConnection();
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.connect();

            // Obtener el InputStream.
            InputStream inputStream = urlConnection.getInputStream();

            // Crear el buffered reader del input stream.
            reader = new BufferedReader(new InputStreamReader(inputStream));

            // Inicializar StringBuilder para guardar la respuesta
            StringBuilder builder = new StringBuilder();

            // Extraer el String del reader y añadirlo al builder en bucle
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
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
        //Log.d(LOG_TAG, tokenJSONString);
        // retorna el String devuelto por el server
        return tokenJSONString;
    }

}
