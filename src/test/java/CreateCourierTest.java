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

import static org.hamcrest.Matchers.equalTo;

public class CreateCourierTest {

    private final CourierApi apiCourier = new CourierApi();

    @Before
    public void setUp() {
        RestAssured.baseURI = BaseHttpClient.BASE_URL;
    }

    /**
     * Курьер может авторизоваться.
     */
    @Test
    @DisplayName("Должна быть возможность создать курьера")
    public void createNewCourierTest() {
        Courier courier = Courier.getRandomCourier();
        Response response =
                apiCourier.regNewCourier(courier);
        response.then().assertThat().body("ok", equalTo(true))
                .and().assertThat().statusCode(HttpStatus.SC_CREATED);

    }

    /**
     * Нельзя создать двух одинаковых курьеров.
     */
    @Test
    @DisplayName("Нельзя создать двух одинаковых курьеров")
    public void getErrorWhenTwoEqualCouriersCreatedTest() {
        Courier courier = Courier.getRandomCourier();
        boolean isCourierRegistered =
                apiCourier.regNewCourier(courier)
                        .then().statusCode(HttpStatus.SC_CREATED)
                        .and().extract().body().path("ok");

        if(!isCourierRegistered) {
            Assert.fail("Не удалось создать курьера для проверки.");
            return;
        }

        apiCourier.regNewCourier(courier)
                .then().assertThat().statusCode(HttpStatus.SC_CONFLICT);

    }

    /**
     * Чтобы создать курьера, нужно передать в ручку все обязательные поля.
     */
    @Test
    @DisplayName("Чтобы создать курьера, нужно передать в ручку все обязательные поля")
    public void newCourierCreateWithOnlyNecessaryFieldsTest() {
        Courier courierWithoutFirstName = new Courier(Courier.getRandomLogin(), Courier.getRandomPassword());
        Response response =
                apiCourier.regNewCourier(courierWithoutFirstName);
           response.then().assertThat().body("ok", equalTo(true))
                .and().assertThat().statusCode(HttpStatus.SC_CREATED);

    }

    /**
     * Если одного из полей нет, запрос возвращает ошибку.
     */
    @Test
    @DisplayName("Должна быть ошибка, если при создании курьера не передан пароль")
    public void getErrorWhenRegisterNewCourierWithoutPasswordTest(){
        Courier courierWithoutPassword = new Courier(Courier.getRandomLogin());
        Response response =
                apiCourier.regNewCourier(courierWithoutPassword);
        response.then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST); // Запрос без логина или пароля
    }

    /**
     * Если создать пользователя с логином, который уже есть, возвращается ошибка.
     */
    @Test
    @DisplayName("Должна быть ошибка, если создается курьер с существующим именем")
    public void shouldGetErrorWhenTwoCouriersWithEqualLoginsAreCreated(){
        Courier firstCourier = Courier.getRandomCourier();

        boolean isFirstCourierRegistered =
                apiCourier
                        .regNewCourier(firstCourier)
                        .then().statusCode(HttpStatus.SC_CREATED)
                        .and().extract().body().path("ok");

        if (!isFirstCourierRegistered){
            Assert.fail("Не удалось создать курьера для проверки.");
            return;
        }

        Courier secondCourier = Courier.getRandomCourier();
        secondCourier.setLogin(firstCourier.getLogin());

        apiCourier
                .regNewCourier(secondCourier)
                .then().assertThat().statusCode(HttpStatus.SC_CONFLICT); // Запрос с повторяющимся логином
    }

    @After
    public void afterTest(){
        apiCourier.clearCreatedCouriers();
    }

}



