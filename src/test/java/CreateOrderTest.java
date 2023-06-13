import clients.OrderClient;
import clients.UserClient;
import dataprovider.UserProvider;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import pojo.CreateOrderRequest;
import pojo.LoginRequest;
import pojo.UserRequest;

import java.util.List;


@RunWith(Parameterized.class)
public class CreateOrderTest {
    OrderClient orderClient = new OrderClient();

    UserClient userClient = new UserClient();
    private String accessToken;

    private final List<String> ingredients;
    private final Integer expectedStatusCode;


    public CreateOrderTest(List<String> ingredients, Integer expectedStatusCode) {
        this.ingredients = ingredients;
        this.expectedStatusCode = expectedStatusCode;
    }

    @Parameterized.Parameters(name = "statusCode - {1} & _id ingredients - {0}")
    public static Object[][] data() {
        return new Object[][] {
                //с неверным хешем ингредиентов.
                { List.of("60d3b41abdacab0026a733c6", "609646e4dc916e00276b2870"), 400 },
                { List.of(""), 500 },
                //с ингредиентами,
                { List.of("61c0c5a71d1f82001bdaaa6d","61c0c5a71d1f82001bdaaa6f"), 200 },
                //без ингредиентов,
                { List.of(), 400 },
        };
    }

    //с авторизацией,
    @Test
    @DisplayName("Creating an order with authorisation")
    @Step("StatusCode = [{this.expectedStatusCode}] & Id ingredients - [{this.ingredients}]")
    @Description("Checking the body of the response and status code")
    public void checkCreateOrderWithLogin() {
        UserRequest randomUserRequest = UserProvider.getRandomUser();

        userClient.createUser(randomUserRequest)
                .statusCode(200)
                .body("success", Matchers.equalTo(true));

        LoginRequest loginRequest = LoginRequest.from(randomUserRequest);

        accessToken = userClient.login(loginRequest)
                .statusCode(200)
                .body("success", Matchers.equalTo(true))
                .extract().jsonPath().getString("accessToken").replace("Bearer ","");

        CreateOrderRequest createOrderRequest = new CreateOrderRequest(ingredients);

        ValidatableResponse response = orderClient.createOrderWithLogin(createOrderRequest, accessToken);

        if (response.extract().statusCode() == 200 ){
            response
                    .statusCode(expectedStatusCode)
                    .body("order.number", Matchers.notNullValue());
        }
        else if (response.extract().statusCode() == 400) {
            if (ingredients.isEmpty() == true) {
                response
                        .statusCode(expectedStatusCode)
                        .assertThat()
                        .body("message", Matchers.equalTo("Ingredient ids must be provided"));
            }
            else {
                response
                        .statusCode(expectedStatusCode)
                        .assertThat()
                        .body("message", Matchers.equalTo("One or more ids provided are incorrect"));
            }
        }
        else {
            response
                    .statusCode(expectedStatusCode)
                    .assertThat().statusLine("HTTP/1.1 500 Internal Server Error");
        }
    }
    //без авторизации,
    @Test
    @DisplayName("Creating an order without authorisation")
    @Step("StatusCode = [{this.expectedStatusCode}] & Id ingredients - [{this.ingredients}]")
    @Description("Checking the body of the response and status code")
    public void checkCreateOrderWithoutLogin() {
        UserRequest randomUserRequest = UserProvider.getRandomUser();

        accessToken = userClient.createUser(randomUserRequest)
                .statusCode(200)
                .body("success", Matchers.equalTo(true))
                .extract().jsonPath().getString("accessToken").replace("Bearer ","");

        CreateOrderRequest createOrderRequest = new CreateOrderRequest(ingredients);

        ValidatableResponse response = orderClient.createOrderWithoutLogin(createOrderRequest);

        if (response.extract().statusCode() == 200 ){
            response
                    .statusCode(expectedStatusCode)
                    .body("order.number", Matchers.notNullValue());
        }
        else if (response.extract().statusCode() == 400) {
            if (ingredients.isEmpty() == true) {
                response
                        .statusCode(expectedStatusCode)
                        .assertThat()
                        .body("message", Matchers.equalTo("Ingredient ids must be provided"));
            }
            else {
                response
                        .statusCode(expectedStatusCode)
                        .assertThat()
                        .body("message", Matchers.equalTo("One or more ids provided are incorrect"));
            }
        }
        else {
            response
                    .statusCode(expectedStatusCode)
                    .assertThat().statusLine("HTTP/1.1 500 Internal Server Error");
        }
    }

    @After
    public void tearDown(){
        if ( accessToken != null && !accessToken.isEmpty() )
            // удаление созанного пользователя
            userClient.deleteUser(accessToken)
                    .statusCode(202)
                    .assertThat().body("message", Matchers.equalTo("User successfully removed"));
    }
}
