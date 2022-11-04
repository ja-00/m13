package garcia.ioc.kebook.controllers;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;

/**
 * Clase gestora de las peticiones al server
 */
public class RequestManager {

    private static final String LOG_TAG = RequestManager.class.getSimpleName();
    // Base URL para la API Rest kebook.
/*    // ***URL provisional del mockserver de Postman
    private static final String KEBOOK_BASE_URL = "https://c33a24fd-f159-447a-b9a3-e90678dca9a8.mock.pstmn.io/login";*/
    // ***URL provisional
    //private static final String KEBOOK_BASE_URL =  "http://83.44.140.151:8080/login/nacho@gmail.com/nacho";
    // ***URL de la maquina host local desde el emulador android
    private static final String KEBOOK_BASE_URL = "http://10.0.2.2:8080/";
/*    // ***URL de la maquina local
    private static final String KEBOOK_BASE_URL = "http://localhost:8080/";*/


    /**
     * Método que realiza la conexión con el server con las credenciales de usuario (user y password) y el boolean que
     * indica si es un administrador. Retorna un String con la respuesta devuelta por el server.
     *
     * @param user Usuario
     * @param password Contraseña
     * @param isAdmin Es o no administrador
     * @return Devuelve un String con el JWToken
     */
    static String login(String user, String password, boolean isAdmin) {
        // Inicializar variables
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String tokenJSONString = null;
        URL url;

        // Construir la URI para cada caso: admin y user
        try {
            if (isAdmin) {
                url = new URL(KEBOOK_BASE_URL + "login/admin/" + user + "/" + password);
            } else {
                url = new URL(KEBOOK_BASE_URL + "login/" + user + "/" + password);
            }

            //Log.d("info", builtURI.toString());
            //Log.d("info", url.toString());
            //URL requestURL = new URL(builtURI.toString());

            // Montar la URL y conectar con el server y el verbo correspondiente para la peti ción
            //urlConnection = (HttpURLConnection) requestURL.openConnection();
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
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

    static String logout(String id) {
        // Inicializar variables
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String loggedOut = null;
        URL url;

        // Construir la URI para cada caso: admin y user
        try {
            url = new URL(KEBOOK_BASE_URL + "logout/" + id);

            //Log.d("info", builtURI.toString());
            Log.d("info", url.toString());
            //URL requestURL = new URL(builtURI.toString());

            // Montar la URL y conectar con el server y el verbo correspondiente para la peti ción
            //urlConnection = (HttpURLConnection) requestURL.openConnection();
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
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

            loggedOut = builder.toString();

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
        //Log.d(LOG_TAG, loggedOut);
        // retorna el String devuelto por el server
        return loggedOut;
    }

    static String register(String user, String mail, String password) {
        // Inicializar variables
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String tokenJSONString = null;
        URL url;

        // Construir la URI
        try {
            url = new URL(KEBOOK_BASE_URL + "usuario");

            //Log.d("info", builtURI.toString());
            //Log.d("info", url.toString());
            //URL requestURL = new URL(builtURI.toString());

            // Montar la URL y conectar con el server y el verbo correspondiente para la peti ción
            //urlConnection = (HttpURLConnection) requestURL.openConnection();
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setDoOutput(true);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("nombre", user);
            jsonObject.put("correo", mail);
            jsonObject.put("contrasena", password);
            jsonObject.put("fecha_creacion", LocalDate.now());
            String body = jsonObject.toString();
            Log.d("info", body);

            try (OutputStream os = urlConnection.getOutputStream()) {
                byte[] input = body.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            //String jsonInputString = "{\"nombre\": + user + , \"correo\": + mail + , \"contrasena\": + password +, \"fecha_creacion\": + LocalDate.now()}";

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

        } catch (IOException | JSONException e) {
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

    static String deleteUser(String token, String id) {
        // Inicializar variables
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String userDeleted = null;
        URL url;

        // Construir la URI
        try {
            url = new URL(KEBOOK_BASE_URL + "usuario/" + id);

            //Log.d("info", builtURI.toString());
            //Log.d("info", url.toString());
            //URL requestURL = new URL(builtURI.toString());

            // Montar la URL y conectar con el server y el verbo correspondiente para la peti ción
            //urlConnection = (HttpURLConnection) requestURL.openConnection();
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("DELETE");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Token", token);
            urlConnection.setDoOutput(true);

/*            JSONObject jsonObject = new JSONObject();
            jsonObject.put("nombre", user);
            jsonObject.put("correo", mail);
            jsonObject.put("contrasena", password);
            jsonObject.put("fecha_creacion", LocalDate.now());

            Log.d("info", body);

            try (OutputStream os = urlConnection.getOutputStream()) {
                byte[] input = body.getBytes("utf-8");
                os.write(input, 0, input.length);
            }*/

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

            userDeleted = builder.toString();

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
        Log.d("info", userDeleted);
        return userDeleted;
    }

}
