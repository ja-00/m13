package garcia.ioc.kebook.controllers;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;

import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.LocalDate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import garcia.ioc.kebook.R;
import garcia.ioc.kebook.models.Book;
import garcia.ioc.kebook.models.Escritor;
import garcia.ioc.kebook.models.Reserva;
import garcia.ioc.kebook.models.User;

/**
 * Clase gestora de las diferentes peticiones al server
 */
public class RequestManager {

    private static final String LOG_TAG = RequestManager.class.getSimpleName();
    // Base URL para la API Rest kebook.
/*    // ***URL provisional del mockserver de Postman
    private static final String KEBOOK_BASE_URL = "https://c33a24fd-f159-447a-b9a3-e90678dca9a8.mock.pstmn.io/login";*/
    // ***URL provisional
    //private static final String KEBOOK_BASE_URL =  "http://83.44.140.151:8080/login/nacho@gmail.com/nacho";
    // ***URL de la maquina host local desde el emulador android
    private static final String KEBOOK_BASE_URL = "https://10.0.2.2:8080/";
/*    // ***URL de la maquina local
    private static final String KEBOOK_BASE_URL = "http://localhost:8080/";*/

    // Necesarias para poder establecer una comunicación cifrada con el server mediante https
    private static SSLContext context;
    private static HttpsURLConnection urlConnection = null;


    /**
     * Request con las credenciales de usuario (email y password) y el boolean que
     * indica si es un administrador. Retorna un String con la respuesta devuelta por el server.
     *
     * @param email    Usuario
     * @param password Contraseña
     * @param isAdmin  Es o no administrador
     * @return Devuelve un String con el JWToken
     */
    static String login(String email, String password, boolean isAdmin) throws IOException, CertificateException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {

        // Inicializar variables
        //HttpURLConnection urlConnection = null;
        //URL url = new URL(KEBOOK_BASE_URL);
        BufferedReader reader = null;
        String tokenJSONString = null;
        URL url;
        //HttpsURLConnection urlConnection = null;

        // Construir la URI para cada caso: admin y email
        try {
            if (isAdmin) {
                url = new URL(KEBOOK_BASE_URL + "login/admin/" + email + "/" + password);
            } else {
                url = new URL(KEBOOK_BASE_URL + "login/" + email + "/" + password);
            }

            // Montar la URL y conectar con el server y el tipo de petición correspondiente
            //urlConnection = (HttpURLConnection) url.openConnection();
            setupSecureConnection();
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setSSLSocketFactory(context.getSocketFactory());
            urlConnection.setHostnameVerifier(new AllowAllHostnameVerifier());
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

    /**
     * Request para el logout del server
     *
     * @param id id del usuario
     * @return respuesta del server
     */
    static String logout(String id) {
        // Inicializar variables
        //HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String response = null;
        URL url;


        // Construir la URI para cada caso: admin y user
        try {
            url = new URL(KEBOOK_BASE_URL + "logout/" + id);

            Log.d("info", url.toString());

            // Montar la URL y conectar con el server y el verbo correspondiente para la peti ción
            //urlConnection = (HttpURLConnection) requestURL.openConnection();
            //urlConnection = (HttpURLConnection) url.openConnection();
            setupSecureConnection();
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setSSLSocketFactory(context.getSocketFactory());
            urlConnection.setHostnameVerifier(new AllowAllHostnameVerifier());
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

            response = builder.toString();

        } catch (IOException | CertificateException | NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
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
        return response;
    }

    /**
     * Request para registrar un nuevo usuario
     *
     * @param user     nombre usuario
     * @param mail     email usuario
     * @param password password usuario
     * @return respuesta del server
     */
    static String registerUser(String user, String mail, String password) {
        // Inicializar variables
        //HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String response = null;
        URL url;

        // Construir la URI
        try {
            url = new URL(KEBOOK_BASE_URL + "usuario");

            // Montar la URL y conectar con el server y el verbo correspondiente para la peti ción
            //urlConnection = (HttpURLConnection) requestURL.openConnection();
            //urlConnection = (HttpURLConnection) url.openConnection();
            setupSecureConnection();
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setSSLSocketFactory(context.getSocketFactory());
            urlConnection.setHostnameVerifier(new AllowAllHostnameVerifier());
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

            response = builder.toString();

        } catch (IOException | JSONException | CertificateException | NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
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
        return response;
    }

    /**
     * Request para borrar un usuario
     *
     * @param token token del usuario logado
     * @param id    id del usuario
     * @return respuesta del server
     */
    static String deleteUser(String token, String id) {
        // Inicializar variables
        //HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String response = null;
        URL url;

        // Construir la URI
        try {
            url = new URL(KEBOOK_BASE_URL + "usuario/" + id);

            // Montar la URL y conectar con el server y el verbo correspondiente para la peti ción
            //urlConnection = (HttpURLConnection) requestURL.openConnection();
            //urlConnection = (HttpURLConnection) url.openConnection();
            setupSecureConnection();
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setSSLSocketFactory(context.getSocketFactory());
            urlConnection.setHostnameVerifier(new AllowAllHostnameVerifier());
            urlConnection.setRequestMethod("DELETE");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Token", token);
            urlConnection.setDoOutput(true);

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

            response = builder.toString();

        } catch (IOException | KeyManagementException | KeyStoreException | NoSuchAlgorithmException | CertificateException e) {
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
        // retorna el String devuelto por el server
        Log.d("info", response);
        return response;
    }

    /**
     * Request que devuelve una lista de todos los usuarios
     *
     * @param token token del usuario
     * @return lista de usuarios
     */
    static String usersList(String token) {
        // Inicializar variables
        //HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String response = null;
        URL url;

        // Construir la URI
        try {
            url = new URL(KEBOOK_BASE_URL + "usuario");

            //Log.d("info", builtURI.toString());
            //Log.d("info", url.toString());
            //URL requestURL = new URL(builtURI.toString());

            // Montar la URL y conectar con el server y el verbo correspondiente para la peti ción
            //urlConnection = (HttpURLConnection) requestURL.openConnection();
            //urlConnection = (HttpURLConnection) url.openConnection();
            setupSecureConnection();
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setSSLSocketFactory(context.getSocketFactory());
            urlConnection.setHostnameVerifier(new AllowAllHostnameVerifier());
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Token", token);

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

            response = builder.toString();

        } catch (IOException | CertificateException | NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
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
        Log.d("info", response);
        return response;
    }

    static String getUserWithEmail(String token, String email) {
        // Inicializar variables
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String response = null;
        URL url;

        // Construir la URI
        try {
            url = new URL(KEBOOK_BASE_URL + "usuario/correo?correo=" + email);

            // Montar la URL y conectar con el server y el verbo correspondiente para la peti ción
            //urlConnection = (HttpURLConnection) requestURL.openConnection();
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            //urlConnection.setRequestProperty("Content-Type", "application/json");
            //urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Token", token);
            urlConnection.setDoOutput(true);

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

            response = builder.toString();

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
        // retorna el String devuelto por el server
        Log.d("info", response);
        return response;
    }

    /**
     * Request para obtener un usuario a partir de su id
     *
     * @param token token del usuario que peticiona
     * @param id    id del usuario a consultar
     * @return información del usuario de la id introducida
     */
    static String getUserWithId(String token, String id) {
        // Inicializar variables
        //HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String response = null;
        URL url;

        Log.d("info", token);

        // Construir la URI
        try {
            url = new URL(KEBOOK_BASE_URL + "usuario/" + id);

            // Montar la URL y conectar con el server y el verbo correspondiente para la peti ción
            //urlConnection = (HttpURLConnection) url.openConnection();
            setupSecureConnection();
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setSSLSocketFactory(context.getSocketFactory());
            urlConnection.setHostnameVerifier(new AllowAllHostnameVerifier());
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Token", token);

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

            response = builder.toString();

        } catch (IOException | NoSuchAlgorithmException | KeyStoreException | KeyManagementException | CertificateException e) {
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
        // retorna el String devuelto por el server
        Log.d("info", response);
        return response;
    }

    /**
     * Request para obtener la lista de libros de la base de datos
     *
     * @param token token del usuario activo
     * @return lista de libros
     */
    static String booksList(String token) {
        // Inicializar variables
        //HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String response = null;
        URL url;

        // Construir la URI
        try {
            url = new URL(KEBOOK_BASE_URL + "libro");

            // Montar la URL y conectar con el server y el verbo correspondiente para la peti ción
            //urlConnection = (HttpURLConnection) requestURL.openConnection();
            //urlConnection = (HttpURLConnection) url.openConnection();
            setupSecureConnection();
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setSSLSocketFactory(context.getSocketFactory());
            urlConnection.setHostnameVerifier(new AllowAllHostnameVerifier());
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            //urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Token", token);

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

            response = builder.toString();

        } catch (IOException | CertificateException | NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
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
        Log.d("info", response);
        return response;
    }

    /**
     * Request que permite cambiar la contraseña del usuario activo
     *
     * @param token token del usuario activo
     * @param id    id del usuario activo
     * @param oldP  contraseña antigua
     * @param newP  contraseña nueva
     * @return respuesta del server resultado de la petición
     */
    static String changePassword(String token, String id, String oldP, String newP) {
        // Inicializar variables
        //HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String response = null;
        URL url;

        // Construir la URI
        try {
            url = new URL(KEBOOK_BASE_URL + "usuario/contrasena/cambiar" + "?id=" + id + "&contrasenaAntigua=" + oldP + "&contrasenaNueva=" + newP);
            //url = new URL(KEBOOK_BASE_URL + "usuario/contrasena/cambiar");
            // Montar la URL y conectar con el server y el verbo correspondiente para la peti ción
            //urlConnection = (HttpURLConnection) requestURL.openConnection();
            //urlConnection = (HttpURLConnection) url.openConnection();
            setupSecureConnection();
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setSSLSocketFactory(context.getSocketFactory());
            urlConnection.setHostnameVerifier(new AllowAllHostnameVerifier());
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Token", token);
            urlConnection.setDoOutput(true);

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

            response = builder.toString();

        } catch (IOException | CertificateException | KeyStoreException | KeyManagementException | NoSuchAlgorithmException e) {
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
        return response;
    }

    /**
     * Request que permite obtener una lista de libros filtrados según un criterio
     *
     * @param token    token del usuario que peticiona
     * @param filterBy criterio de filtrado
     * @param value    valor del criterio de filtrado
     * @return lista de libros filtrada
     */
    static String filteredBooksList(String token, String filterBy, String value) {
        // Inicializar variables
        //HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String response = null;
        URL url;

        // Construir la URI
        try {
            url = new URL(KEBOOK_BASE_URL + "libro/" + filterBy + "?" + filterBy + "=" + value);

            // Montar la URL y conectar con el server y el verbo correspondiente para la peti ción
            //urlConnection = (HttpURLConnection) requestURL.openConnection();
            //urlConnection = (HttpURLConnection) url.openConnection();
            setupSecureConnection();
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setSSLSocketFactory(context.getSocketFactory());
            urlConnection.setHostnameVerifier(new AllowAllHostnameVerifier());
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            //urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Token", token);

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

            response = builder.toString();

        } catch (IOException | CertificateException | NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
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
        Log.d("info", response);
        return response;
    }

    /**
     * Request que permite añadir un libro a la base de datos
     *
     * @param token     token del usuario activo
     * @param isbn      isbn del libro
     * @param title     título del libro
     * @param autorId   id del autor del libro
     * @param autorName nombre del autor
     * @param sinopsis  sinopsis del libro
     * @param genre     género del libro
     * @param available disponibilidad del libro
     * @return respuesta del server
     */
    static String addBook(String token, String isbn, String title, String autorId, String autorName, String sinopsis, String genre, String available) {
        // Inicializar variables
        //HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String response = null;
        URL url;

        // Construir la URI
        try {
            url = new URL(KEBOOK_BASE_URL + "libro");

            // Montar la URL y conectar con el server y el verbo correspondiente para la peti ción
            //urlConnection = (HttpURLConnection) requestURL.openConnection();
            //urlConnection = (HttpURLConnection) url.openConnection();
            setupSecureConnection();
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setSSLSocketFactory(context.getSocketFactory());
            urlConnection.setHostnameVerifier(new AllowAllHostnameVerifier());
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Token", token);
            urlConnection.setDoOutput(true);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("isbn", isbn);
            jsonObject.put("titulo", title);
            JSONObject jsonObjectAuthor = new JSONObject();
            jsonObjectAuthor.put("id", autorId);
            jsonObjectAuthor.put("nombre", autorName);
            jsonObject.put("autor", jsonObjectAuthor);
            jsonObject.put("sinopsis", sinopsis);
            jsonObject.put("genero", genre);
            jsonObject.put("disponible", available);
            String body = jsonObject.toString();
            Log.d("info", body);

            try (OutputStream os = urlConnection.getOutputStream()) {
                byte[] input = body.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

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

            response = builder.toString();

        } catch (IOException | JSONException | CertificateException | NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
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
        return response;
    }

    /**
     * Request que obtiene la información de un autor a partir de su nombre
     *
     * @param token      token del usuario activo
     * @param authorName nombre del autor
     * @return información del autor
     */
    static String getAuthorWithName(String token, String authorName) {
        // Inicializar variables
        //HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String response = null;
        URL url;

        // Construir la URI
        try {
            url = new URL(KEBOOK_BASE_URL + "escritor/nombre?nombre=" + authorName);

            // Montar la URL y conectar con el server y el verbo correspondiente para la peti ción
            //urlConnection = (HttpURLConnection) url.openConnection();
            setupSecureConnection();
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setSSLSocketFactory(context.getSocketFactory());
            urlConnection.setHostnameVerifier(new AllowAllHostnameVerifier());
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Token", token);

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

            response = builder.toString();

        } catch (IOException | CertificateException | NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
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
        // retorna el String devuelto por el server
        Log.d("info", response);
        return response;
    }

    /**
     * Request que permite añadir un autor a la base de datos
     *
     * @param token      token del usuario activo
     * @param authorName nombre del autor
     * @return respuesta del server
     */
    static String addAuthor(String token, String authorName) {
        // Inicializar variables
        //HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String response = null;
        URL url;

        // Construir la URI
        try {
            url = new URL(KEBOOK_BASE_URL + "escritor");

            // Montar la URL y conectar con el server y el verbo correspondiente para la peti ción
            //urlConnection = (HttpURLConnection) requestURL.openConnection();
            //urlConnection = (HttpURLConnection) url.openConnection();
            setupSecureConnection();
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setSSLSocketFactory(context.getSocketFactory());
            urlConnection.setHostnameVerifier(new AllowAllHostnameVerifier());
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Token", token);
            urlConnection.setDoOutput(true);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("nombre", authorName);
            String body = jsonObject.toString();
            Log.d("info", body);

            try (OutputStream os = urlConnection.getOutputStream()) {
                byte[] input = body.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

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

            response = builder.toString();

        } catch (IOException | JSONException | CertificateException | NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
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
        return response;
    }

    static String checkBookAvailable(String token, String isbn) {
        // Inicializar variables
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String response = null;
        URL url;

        // Construir la URI
        try {
            url = new URL(KEBOOK_BASE_URL + "libro/disponible/" + isbn);

            // Montar la URL y conectar con el server y el verbo correspondiente para la peti ción
            //urlConnection = (HttpURLConnection) requestURL.openConnection();
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Token", token);

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

            response = builder.toString();

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
        return response;
    }

    /**
     * Request que obtiene las reservas que existen de un libro
     *
     * @param token token del usuario activo
     * @param isbn  isbn del libro a consultar
     * @return reservas existentes del libro consultado
     */
    static String obtenerReservasPorLibro(String token, String isbn) {
        // Inicializar variables
        //HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String response = null;
        URL url;

        // Construir la URI
        try {
            url = new URL(KEBOOK_BASE_URL + "reserva/" + isbn);

            // Montar la URL y conectar con el server y el verbo correspondiente para la peti ción
            //urlConnection = (HttpURLConnection) requestURL.openConnection();
            //urlConnection = (HttpURLConnection) url.openConnection();
            setupSecureConnection();
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setSSLSocketFactory(context.getSocketFactory());
            urlConnection.setHostnameVerifier(new AllowAllHostnameVerifier());
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Token", token);

            urlConnection.connect();

            int code = urlConnection.getResponseCode();

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

            response = builder.toString();

        } catch (IOException | CertificateException | NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
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
        return response;
    }

    /**
     * Request que permite realizar una reserva de un libro
     *
     * @param token    token del usuario activo
     * @param bookBook Datos de la reserva en formato JSON
     * @return respuesta del server
     */
    static String reservarLibro(String token, String bookBook) {
        // Inicializar variables
        //HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        URL url;
        int responseCode;
        String code = null;

        // Construir la URI
        try {
            url = new URL(KEBOOK_BASE_URL + "reserva");

            // Montar la URL y conectar con el server y el verbo correspondiente para la peti ción
            //urlConnection = (HttpURLConnection) requestURL.openConnection();
            //urlConnection = (HttpURLConnection) url.openConnection();
            setupSecureConnection();
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setSSLSocketFactory(context.getSocketFactory());
            urlConnection.setHostnameVerifier(new AllowAllHostnameVerifier());
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Token", token);
            urlConnection.setDoOutput(true);

/*            Gson gson = new Gson();
            Reserva reserva = gson.fromJson(bookBook, Reserva.class);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("", reserva);
            String body = jsonObject.toString();*/

            try (OutputStream os = urlConnection.getOutputStream()) {
                byte[] input = bookBook.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            urlConnection.connect();

            responseCode = urlConnection.getResponseCode();
            code = String.valueOf(responseCode);

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

/*            // Verificar si el stream está vacío
            if (builder.length() == 0) {
                return null;
            }*/

        } catch (IOException | CertificateException | NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
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
        return code;
        //return response;
    }

    /**
     * Request que confirma que un libro ha sido recogido (después de haber sido reservado)
     *
     * @param token     token del usuario activo
     * @param idReserva id de la reserva
     * @return respuesta del server
     */
    static String confirmarRecogida(String token, String idReserva) {
        // Inicializar variables
        //HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String response = null;
        URL url;

        // Construir la URI
        try {
            url = new URL(KEBOOK_BASE_URL + "reserva/" + idReserva + "/recogido");

            // Montar la URL y conectar con el server y el verbo correspondiente para la peti ción
            //urlConnection = (HttpURLConnection) requestURL.openConnection();
            //urlConnection = (HttpURLConnection) url.openConnection();
            setupSecureConnection();
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setSSLSocketFactory(context.getSocketFactory());
            urlConnection.setHostnameVerifier(new AllowAllHostnameVerifier());
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Token", token);
            urlConnection.setDoOutput(true);

/*            try (OutputStream os = urlConnection.getOutputStream()) {
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

            response = builder.toString();

        } catch (IOException | CertificateException | NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
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
        return response;
    }

    /**
     * Request que confirma que un libro ha sido recogido (después de haber sido reservado)
     *
     * @param token     token del usuario activo
     * @param idReserva id de la reserva
     * @return respuesta del server
     */
    static String confirmarDevolucion(String token, String idReserva) {
        // Inicializar variables
        //HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String response = null;
        URL url;

        // Construir la URI
        try {
            url = new URL(KEBOOK_BASE_URL + "reserva/" + idReserva + "/devuelto");

            // Montar la URL y conectar con el server y el verbo correspondiente para la peti ción
            //urlConnection = (HttpURLConnection) requestURL.openConnection();
            //urlConnection = (HttpURLConnection) url.openConnection();
            setupSecureConnection();
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setSSLSocketFactory(context.getSocketFactory());
            urlConnection.setHostnameVerifier(new AllowAllHostnameVerifier());
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Token", token);
            urlConnection.setDoOutput(true);

/*            try (OutputStream os = urlConnection.getOutputStream()) {
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

            response = builder.toString();

        } catch (IOException | CertificateException | NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
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
        return response;
    }

    /**
     * Request que obtiene las reservas de un libro realizadas por un usuario
     *
     * @param token     token del usaurio activo
     * @param isbn      isbn del libor
     * @param idUsuario id del usuario
     * @return respuesta del server
     */
    static String obtenerReservasLibroPorUsuario(String token, String isbn, String idUsuario) {
        // Inicializar variables
        //HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String response = null;
        URL url;

        // Construir la URI
        try {
            url = new URL(KEBOOK_BASE_URL + "reserva/" + isbn + "/usuario?idUsuario=" + idUsuario);

            // Montar la URL y conectar con el server y el verbo correspondiente para la peti ción
            //urlConnection = (HttpURLConnection) requestURL.openConnection();
            //urlConnection = (HttpURLConnection) url.openConnection();
            setupSecureConnection();
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setSSLSocketFactory(context.getSocketFactory());
            urlConnection.setHostnameVerifier(new AllowAllHostnameVerifier());
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Token", token);

            urlConnection.connect();

            int code = urlConnection.getResponseCode();

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

            response = builder.toString();

        } catch (IOException | CertificateException | NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
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
        return response;
    }

    /**
     * Request que obtiene la información de un libro a partir de su isbn
     *
     * @param token token del usuario activo
     * @param isbn  isbn del libor
     * @return informacioón del libro
     */
    static String obtenerLibroPorIsbn(String token, String isbn) {
        // Inicializar variables
        //HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String response = null;
        URL url;

        // Construir la URI
        try {
            url = new URL(KEBOOK_BASE_URL + "libro/" + isbn);

            // Montar la URL y conectar con el server y el verbo correspondiente para la peti ción
            //urlConnection = (HttpURLConnection) requestURL.openConnection();
            //urlConnection = (HttpURLConnection) url.openConnection();
            setupSecureConnection();
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setSSLSocketFactory(context.getSocketFactory());
            urlConnection.setHostnameVerifier(new AllowAllHostnameVerifier());
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Token", token);

            urlConnection.connect();

            int code = urlConnection.getResponseCode();

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

            response = builder.toString();

        } catch (IOException | CertificateException | NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
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
        return response;
    }

    /**
     * Request para registrar un nuevo usuario
     *
     * @param token  token del usuario actual
     * @param resena ressenya
     * @return respuesta del server
     */
    static String guardarResena(String token, String resena) {
        // Inicializar variables
        //HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String response = null;
        URL url;
        int responseCode;
        String code = null;

        // Construir la URI
        try {
            url = new URL(KEBOOK_BASE_URL + "resena");

            // Montar la URL y conectar con el server y el verbo correspondiente para la peti ción
            //urlConnection = (HttpURLConnection) requestURL.openConnection();
            //urlConnection = (HttpURLConnection) url.openConnection();
            setupSecureConnection();
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setSSLSocketFactory(context.getSocketFactory());
            urlConnection.setHostnameVerifier(new AllowAllHostnameVerifier());
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Token", token);
            urlConnection.setDoOutput(true);

/*            JSONObject jsonObject = new JSONObject();
            jsonObject.put("resena", resena);
            String body = jsonObject.toString();
            Log.d("info", body);*/

            try (OutputStream os = urlConnection.getOutputStream()) {
                byte[] input = resena.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            urlConnection.connect();

            responseCode = urlConnection.getResponseCode();
            code = String.valueOf(responseCode);

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

            response = builder.toString();

        } catch (IOException | CertificateException | NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
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
        return code;
    }

    /**
     * Request para registrar una propuesta de evento
     *
     * @param token  token del usuario actual
     * @param evento propuesta de evento
     * @return respuesta del server
     */
    static String guardarEvento(String token, String evento) {
        // Inicializar variables
        //HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String response = null;
        URL url;
        int responseCode;
        String code = null;

        // Construir la URI
        try {
            url = new URL(KEBOOK_BASE_URL + "evento");

            // Montar la URL y conectar con el server y el verbo correspondiente para la peti ción
            //urlConnection = (HttpURLConnection) requestURL.openConnection();
            //urlConnection = (HttpURLConnection) url.openConnection();
            setupSecureConnection();
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setSSLSocketFactory(context.getSocketFactory());
            urlConnection.setHostnameVerifier(new AllowAllHostnameVerifier());
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Token", token);
            urlConnection.setDoOutput(true);

/*            JSONObject jsonObject = new JSONObject();
            jsonObject.put("resena", resena);
            String body = jsonObject.toString();
            Log.d("info", body);*/

            try (OutputStream os = urlConnection.getOutputStream()) {
                byte[] input = evento.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            urlConnection.connect();

            responseCode = urlConnection.getResponseCode();
            code = String.valueOf(responseCode);

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

            response = builder.toString();

        } catch (IOException | CertificateException | NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
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
        return code;
    }

    /**
     * Método que permite configurar el tipo de conexión para que las comunicaciones sean cifradas
     * mediante https (SSL)
     *
     * @throws CertificateException
     * @throws IOException
     * @throws KeyStoreException
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    static void setupSecureConnection() throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        // Crear certificado
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        InputStream caInput = new BufferedInputStream(new FileInputStream("/sdcard/Download/kebook_cert_v2.crt"));
        Certificate ca;
        try {
            ca = cf.generateCertificate(caInput);
            System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
        } finally {
            caInput.close();
        }

        // Crear un keystore que contiene el certificado de confianza
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);

        // Crear un trustmanager que confia en el certificado de la keystore
        /****** Finalmente no se usa. En su lugar se usa un trustmanager tonto para evitar
         * la comprobación de la validación del certificado*/
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);


        //ByPass la validación del certificado con un trustmanager tonto
        TrustManager[] dummyTrustManager = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }};

        // Crear un sslcontext que utiliza el trustmanager
        context = SSLContext.getInstance("TLS");
        //context.init(null, tmf.getTrustManagers(), null);
        context.init(null, dummyTrustManager, null);

    }
}
