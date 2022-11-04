package garcia.ioc.kebook;

import org.junit.Test;

import garcia.ioc.kebook.controllers.RequestManager;

import static org.junit.Assert.*;

public class NetworkUtilsTest {

    @Test
    public void amb_login_admin_correcte_retorna_String() {
        assertFalse(RequestManager.login("javiA", "123", true).toLowerCase().contains("error"));
    }

    @Test
    public void amb_login_admin_incorrecte_retorna_String_que_conte_error() {
        assertTrue(RequestManager.login("javi", "456", true).toLowerCase().contains("error"));
    }

    @Test
    public void amb_login_correcte_retorna_String() {
        assertFalse(RequestManager.login("javi", "123", false).toLowerCase().contains("error"));
    }

    @Test
    public void amb_login_incorrecte_retorna_String_que_conte_error() {
        assertTrue(RequestManager.login("javi", "456", false).toLowerCase().contains("error"));
    }
}