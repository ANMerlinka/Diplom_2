package clients;

import io.restassured.response.ValidatableResponse;
import pojo.CreateOrderRequest;
import pojo.UserRequest;

import static io.restassured.RestAssured.given;

public class OrderClient extends BaseClient {
    public ValidatableResponse createOrderWithoutLogin(CreateOrderRequest createOrderRequest){
        return given()
                    .spec(getSpec())
                    .body(createOrderRequest)
                    .when()
                    .post("/api/orders")
                    .then();
    }

    public ValidatableResponse createOrderWithLogin(CreateOrderRequest createOrderRequest, String accessToken){
        return given()
                .spec(getSpec())
                .auth().oauth2(accessToken)
                .body(createOrderRequest)
                .when()
                .post("/api/orders")
                .then();
    }

    public ValidatableResponse getOrderUserWithoutLogin(){
        return given()
                .spec(getSpec())
                .when()
                .get("/api/orders")
                .then();
    }

    public ValidatableResponse getOrderUserWithLogin(String accessToken){
        return given()
                .spec(getSpec())
                .auth().oauth2(accessToken)
                .when()
                .get("/api/orders")
                .then();
    }
}
