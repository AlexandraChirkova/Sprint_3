package model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.RandomStringUtils;

public class Courier {
    private String login;
    private String password;
    private String firstName;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Courier() {

    }

    public Courier(String login) {
        this.login = login;
        this.password = null;
        this.firstName = null;
    }

    public Courier(String login, String password) {
        this.login = login;
        this.password = password;
        this.firstName = null;
    }

    public Courier(String login, String password, String firstName) {
        this.login = login;
        this.password = password;
        this.firstName = firstName;
    }

    public static Courier getRandomCourier() {
        return new Courier(
                getRandomLogin(),
                getRandomPassword(),
                getRandomFirstName()
        );
    }

    /**
     * Получить случайный логин.
     */
    public static String getRandomLogin() {
        return getRandomString(10);
    }

    /**
     * Получить случайный пароль.
     */
    public static String getRandomPassword() {
        return getRandomString(10);
    }

    /**
     * Получить случайное имя.
     */
    private static String getRandomFirstName() {
        return getRandomString(10);
    }

    private static String getRandomString(int count) {
        return RandomStringUtils.randomAlphabetic(count);
    }

    /**
     * Получить курьера в виде json-строки.
     */
    public static String toJson(Courier courier) {
        Gson gson = new GsonBuilder().serializeNulls().create();
        return gson.toJson(courier);
    }

    @Override
    public String toString() {
        return "[Login: " + getLogin() + "; password: " + getPassword() + "; First name: " + getFirstName() + "]";
    }

}
