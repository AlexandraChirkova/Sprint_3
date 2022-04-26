import Client.BaseHttpClient;
import Client.OrderApi;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.*;

public class ListOrderTest {

    private final OrderApi api = new OrderApi();

    @Before
    public void setUp() {
        RestAssured.baseURI = BaseHttpClient.BASE_URL;
    }

    @Test
    @DisplayName("Получение списка заказов")
    public void getOrderListTest() {
        api.getOrderList()
                .then().assertThat().statusCode(HttpStatus.SC_OK)
                .and().body("orders", is(not(empty())));
    }

}
