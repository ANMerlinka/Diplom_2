import clients.UserClient;
import clients.OrderClient;
import dataprovider.UserProvider;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Test;
import pojo.CreateOrderRequest;
import pojo.LoginRequest;
import pojo.UserRequest;

import java.util.List;

public class GetUserOrderTest {
    UserClient userClient = new UserClient();
    private String accessToken;

    OrderClient orderClient = new OrderClient();

    //Получение заказов конкретного авторизованного пользователя
    @Test
    @DisplayName("Receiving orders from a specific authorized user")
    @Description("Checking response body (success) and status code 200")
    public void checkReceivingOrdersWithAuth() {
        UserRequest randomUserRequest = UserProvider.getRandomUser();

        userClient.createUser(randomUserRequest)
                .statusCode(200)
                .body("success", Matchers.equalTo(true));

        LoginRequest loginRequest = LoginRequest.from(randomUserRequest);

        accessToken = userClient.login(loginRequest)
                .statusCode(200)
                .body("success", Matchers.equalTo(true))
                .extract().jsonPath().getString("accessToken").replace("Bearer ","");

        CreateOrderRequest createOrderRequest = new CreateOrderRequest(List.of("61c0c5a71d1f82001bdaaa6d","61c0c5a71d1f82001bdaaa6f"));

        String number = orderClient.createOrderWithLogin(createOrderRequest, accessToken)
                .statusCode(200)
                .extract().jsonPath().getString("number");

        orderClient.getOrderUserWithLogin(accessToken)
                .statusCode(200)
                .body("order.number", Matchers.equalTo(number));
    }


    //Получение заказов конкретного неавторизованного пользователя
    @Test
    @DisplayName("Receiving orders from a specific Non-authorized user")
    @Description("Checking response body (message) and status code 401")
    public void checkReceivingOrdersWithoutAuth() {
        UserRequest randomUserRequest = UserProvider.getRandomUser();

        accessToken = userClient.createUser(randomUserRequest)
                .statusCode(200)
                .body("success", Matchers.equalTo(true))
                .extract().jsonPath().getString("accessToken").replace("Bearer ","");

        CreateOrderRequest createOrderRequest = new CreateOrderRequest(List.of("61c0c5a71d1f82001bdaaa6d","61c0c5a71d1f82001bdaaa6f"));

        orderClient.createOrderWithoutLogin(createOrderRequest)
                .statusCode(200)
                .extract().jsonPath().getString("number");

        orderClient.getOrderUserWithoutLogin()
                .statusCode(401)
                .body("message", Matchers.equalTo( "You should be authorised"));
    }

    @After
    public void tearDown(){
        if (accessToken != null && !accessToken.isEmpty() )
            // удаление созанного пользователя
            userClient.deleteUser(accessToken)
                    .statusCode(202)
                    .assertThat().body("message", Matchers.equalTo("User successfully removed"));
    }
}
