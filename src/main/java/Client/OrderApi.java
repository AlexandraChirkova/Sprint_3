package Client;

import io.restassured.response.Response;
import model.Order;

import static io.restassured.RestAssured.given;

public class OrderApi extends BaseHttpClient {

    public Response createOrder(Order order) {
        return given()
                .header("Content-type", HEADER_CONTENT_TYPE)
                .and()
                .body(order.toJson())
                .when()
                .post("/api/v1/orders");
    }

    public Response getOrderList() {
        return given()
                .get("/api/v1/orders");
    }
}
