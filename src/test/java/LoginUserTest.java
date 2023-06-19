import clients.UserClient;
import dataprovider.UserProvider;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Test;
import pojo.LoginRequest;
import pojo.UserRequest;

public class LoginUserTest {
    UserClient userClient = new UserClient();
    private String accessToken;

    //логин под существующим пользователем,
    @Test
    @DisplayName("Authorization of the user with the correct email & password")
    @Description("Checking response body (success) and status code 200")
    public void checkUserLogin() {
        UserRequest randomUserRequest = UserProvider.getRandomUser();

        userClient.createUser(randomUserRequest)
                .statusCode(200)
                .body("success", Matchers.equalTo(true));

        LoginRequest loginRequest = LoginRequest.from(randomUserRequest);

        accessToken = userClient.login(loginRequest)
                .statusCode(200)
                .body("success", Matchers.equalTo(true))
                .extract().jsonPath().getString("accessToken").replace("Bearer ","");
    }

    //логин с неверным логином и паролем
    @Test
    @DisplayName("Negative - Authorization of the user with the incorrect email & password")
    @Description("Checking message = 'email or password are incorrect' and status code 401")
    public void checkIncorrectUserLogin(){
        UserRequest randomUserRequest = UserProvider.getRandomUser();

        LoginRequest loginRequest = LoginRequest.from(randomUserRequest);

        userClient.login(loginRequest)
                .statusCode(401)
                .body("message", Matchers.equalTo("email or password are incorrect"));
    }


    @After
    public void tearDown(){
        if ( accessToken != null && !accessToken.isEmpty() )
            userClient.deleteUser(accessToken)
                    .statusCode(202)
                    .assertThat().body("message", Matchers.equalTo("User successfully removed"));
    }
}
