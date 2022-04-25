import Client.BaseHttpClient;
import Client.CourierApi;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import model.Courier;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.greaterThan;

public class LoginCourierTest {
    private final CourierApi apiCourier = new CourierApi();

    @Before
    public void setUp() {
        RestAssured.baseURI = BaseHttpClient.BASE_URL;
    }
    /**
     * Курьер может авторизоваться.
     */
    @Test
    @DisplayName("Курьер должен успешно авторизоваться")
    public void newCourierSuccessLoginTest(){
        Courier randomCourier = Courier.getRandomCourier();
        Response registerResponse = apiCourier.regNewCourier(randomCourier);
        if (registerResponse.statusCode() == HttpStatus.SC_CREATED){ // Успешное создание учетной записи
            apiCourier
                    .loginCourier(randomCourier)
                    .then().assertThat().statusCode(HttpStatus.SC_OK)
                    .and().body("id", greaterThan(0)); // Успешный логин
        }
        else{
            Assert.fail("Не удалось создать курьера для проверки.");
        }
    }

    /**
     * Для авторизации нужно передать все обязательные поля.
     */
    @Test
    @DisplayName("Курьер должен успешно авторизоваться только со всеми обязательными полями")
    public void loginCourierWithAllNecessaryFieldsTest(){
        Courier courier = Courier.getRandomCourier();

        courier.setFirstName(null);

        Response registerResponse = apiCourier.regNewCourier(courier);
        if (registerResponse.statusCode() != HttpStatus.SC_CREATED){ // Успешное создание учетной записи
            Assert.fail("Не удалось создать курьера для проверки.");
            return;
        }

        apiCourier
                .loginCourier(courier)
                .then().assertThat().statusCode(HttpStatus.SC_OK)
                .and().body("id", greaterThan(0)); // Успешный логин
    }

    /**
     * Система вернёт ошибку, если неправильно указать логин или пароль.
     */
    @Test
    @DisplayName("Должна быть ошибка авторизации, если логин не передан")
    public void getErrorWhenLoginIsMissedTest(){
        Courier courier = Courier.getRandomCourier();

        if (apiCourier.regNewCourier(courier).statusCode() != HttpStatus.SC_CREATED){
            Assert.fail("Не удалось создать курьера для проверки.");
            return;
        }

        courier.setLogin(null);

        apiCourier
                .loginCourier(courier)
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST); // Запрос без логина или пароля
    }

    /**
     * Если какого-то поля нет, запрос возвращает ошибку.
     */
    @Test
    @DisplayName("Должна быть ошибка авторизации, если пароль не передан")
    public void getErrorWhenPasswordIsMissingTest(){
        Courier courier = Courier.getRandomCourier();

        if (apiCourier.regNewCourier(courier).statusCode() != HttpStatus.SC_CREATED){
            Assert.fail("Не удалось создать курьера для проверки.");
            return;
        }

        courier.setPassword("");

        apiCourier
                .loginCourier(courier)
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST); // Запрос без логина или пароля
    }

    /**
     * Если авторизоваться под несуществующим пользователем, запрос возвращает ошибку;.
     */
    @Test
    @DisplayName("Должна быть ошибка авторизации, если вход под несуществующим логином")
    public void getErrorWhenLoginWithNotExistedLoginTest(){
        Courier courier = Courier.getRandomCourier();

        // Создаем курьера. Авторизуемся под ним. Удаляем его. Пытаемся авторизоваться еще раз.

        Response registerResponse = apiCourier.regNewCourier(courier);
        if (registerResponse.statusCode() != HttpStatus.SC_CREATED){ // Успешное создание учетной записи
            Assert.fail("Не удалось создать курьера для проверки.");
            return;
        }

        Response loginResponse = apiCourier.loginCourier(courier);
        if (loginResponse.statusCode() != HttpStatus.SC_OK){ // Успешный логин
            int id = loginResponse.then().extract().body().path("id");
            apiCourier.deleteCourier(id);

            String message = loginResponse.then().extract().body().path("message");
            Assert.fail("Не удалось залогиниться курьеру (login:" + courier.getLogin() + "): " + message);

            return;
        }

        int id = loginResponse.then().extract().body().path("id");

        Response deleteResponse = apiCourier.deleteCourier(id);
        if (deleteResponse.statusCode() != HttpStatus.SC_OK){ // Успешное удаление курьера
            String message = deleteResponse.then().extract().body().path("message");
            Assert.fail("Не удалось удалить курьера для провнерки (login:" + courier.getLogin() + "/id:" + id + "): " + message);

            return;
        }

        apiCourier
                .loginCourier(courier)
                .then().assertThat().statusCode(HttpStatus.SC_NOT_FOUND); // Запрос c несуществующей парой логин-пароль
    }

    /**
     * Успешный запрос возвращает id.
     */
    @Test
    @DisplayName("Успешная авторизация возвращает идентификатор курьера")
    public void getCourierIdWhenSuccessLoginTest(){
        Courier randomCourier = Courier.getRandomCourier();

        Response registerResponse = apiCourier.regNewCourier(randomCourier);
        if (registerResponse.statusCode() != HttpStatus.SC_CREATED){ // Успешное создание учетной записи
            Assert.fail("Не удалось создать курьера для проверки.");
            return;
        }

        apiCourier
                .loginCourier(randomCourier)
                .then().assertThat().body("id", greaterThan(0))
                .and().statusCode(HttpStatus.SC_OK);
    }


    @After
    public void afterTest(){
        apiCourier.clearCreatedCouriers();
    }
}
