package clients;

import io.restassured.response.ValidatableResponse;
import pojo.ChangeUserDataRequest;
import pojo.LoginRequest;
import pojo.UserRequest;

import static io.restassured.RestAssured.given;

public class UserClient extends BaseClient {

    public ValidatableResponse createUser(UserRequest userRequest){
       return given()
               .spec(getSpec())
               .body(userRequest)
               .when()
               .post("/api/auth/register")
               .then();
    }

    public ValidatableResponse login(LoginRequest loginRequest) {
        return given()
                .spec(getSpec())
                .body(loginRequest)
                .when()
                .post( "/api/auth/login")
                .then();
    }

    public ValidatableResponse changeDataWithAuth(UserRequest userRequest, String accessToken) {
        return given()
                .spec(getSpec())
                .auth().oauth2(accessToken)
                .body(userRequest)
                .when()
                .patch( "/api/auth/user")
                .then();
    }

        public ValidatableResponse changeDataWithoutAuth(UserRequest userRequest) {
        return given()
                .spec(getSpec())
                .body(userRequest)
                .when()
                .patch( "/api/auth/user")
                .then();
    }

    public ValidatableResponse deleteUser(String accessToken) {
        return given()
                .spec(getSpec())
                .auth().oauth2(accessToken)
                .when()
                .delete("/api/auth/user")
                .then();
    }
}
