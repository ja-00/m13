package garcia.ioc.kebook;

import org.junit.Test;

import java.net.URL;

import static org.junit.Assert.*;

public class NetworkUtilsTest {

    @Test
    public void amb_login_admin_correcte_retorna_String() {
        assertTrue(NetworkUtils.getToken("javi", "123", true) instanceof String);
    }

    @Test
    public void amb_login_admin_incorrecte_retorna_String_que_conte_error() {
        assertTrue(NetworkUtils.getToken("javi", "456", true).contains("error"));
    }

    @Test
    public void amb_login_correcte_retorna_String() {
        assertTrue(NetworkUtils.getToken("javi", "123", false) instanceof String);
    }

    @Test
    public void amb_login_incorrecte_retorna_String_que_conte_error() {
        assertTrue(NetworkUtils.getToken("javi", "456", false).contains("error"));
    }
}