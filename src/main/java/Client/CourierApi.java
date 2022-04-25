package Client;

import io.restassured.response.Response;
import model.Courier;
import org.apache.http.HttpStatus;

import java.util.ArrayList;

import static io.restassured.RestAssured.given;

public class CourierApi extends BaseHttpClient{

    private final ArrayList<Courier> createdCouriers = new ArrayList<>();

    public Response regNewCourier(Courier courier) {
        createdCouriers.add(courier);

        return given()
                .header("Content-type", BaseHttpClient.HEADER_CONTENT_TYPE)
                .body(Courier.toJson(courier))
                .when()
                .post("/api/v1/courier");
    }

    public Response loginCourier(Courier courier){
        return given()
                .header("Content-type", HEADER_CONTENT_TYPE)
                .body(Courier.toJson(courier))
                .post("/api/v1/courier/login");
    }

    public Response deleteCourier(int id){
        return given()
                .header("Content-type", HEADER_CONTENT_TYPE)
                .delete("/api/v1/courier/" + id);
    }

    public void clearCourierInfo(Courier courier){
        Response loginResponse = loginCourier(courier);
        if (loginResponse.statusCode() == HttpStatus.SC_OK){ // Успешный логин
            int id = loginResponse.then().extract().body().path("id");

            deleteCourier(id);
        }
    }

    public void clearCreatedCouriers(){
        createdCouriers.forEach(this::clearCourierInfo);
    }
}
