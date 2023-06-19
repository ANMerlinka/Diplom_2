import clients.UserClient;
import dataprovider.UserProvider;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Test;
import pojo.LoginRequest;
import pojo.UserRequest;

public class CreateUserTest {
    UserClient userClient = new UserClient();
    private String accessToken;

    //создать уникального пользователя
    @Test
    @DisplayName("Creation unique user") // имя теста
    @Description("Checking status code 200 and body (success == true)")
    public void checkUniqueUserCreated(){
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

    //создать пользователя, который уже зарегистрирован
    @Test
    @DisplayName("Create a user who is already registered") // имя теста
    @Description("Checking status code 403 and body (message = User already exists)")
    public void checkCreateRepeatedUser(){
        UserRequest randomUserRequest = UserProvider.getRandomUser();

        accessToken = userClient.createUser(randomUserRequest)
                .statusCode(200)
                .body("success", Matchers.equalTo(true))
                .extract().jsonPath().getString("accessToken").replace("Bearer ","");

        userClient.createUser(randomUserRequest)
                .statusCode(403)
                .body("message", Matchers.equalTo( "User already exists"));
    }

    //создать пользователя и не заполнить одно из обязательных полей.
    // без email
    @Test
    @DisplayName("Negative - Сreating a user without a required field - email") // имя теста
    @Description("Checking status code 403 and body (message = Email, password and name are required fields)")
    public void checkCreatedUserWithoutEmail(){
        UserRequest randomUserRequestWithoutEmail = UserProvider.getRandomUserWithoutEmail();

        userClient.createUser(randomUserRequestWithoutEmail)
                .statusCode(403)
                .body("message", Matchers.equalTo( "Email, password and name are required fields"));
    }
    // без password
    @Test
    @DisplayName("Negative - Сreating a user without a required field - password") // имя теста
    @Description("Checking status code 403 and body (message = Email, password and name are required fields)")
    public void checkCreatedUserWithoutPassword(){
        UserRequest randomUserRequestWithoutPsw = UserProvider.getRandomUserWithoutPsw();

        userClient.createUser(randomUserRequestWithoutPsw)
                .statusCode(403)
                .body("message", Matchers.equalTo( "Email, password and name are required fields"));
    }
    // без name
    @Test
    @DisplayName("Negative - Сreating a user without a required field - name") // имя теста
    @Description("Checking status code 403 and body (message = Email, password and name are required fields)")
    public void checkCreatedUserWithoutName(){
        UserRequest randomUserRequestWithoutName = UserProvider.getRandomUserWithoutName();

        userClient.createUser(randomUserRequestWithoutName)
                .statusCode(403)
                .body("message", Matchers.equalTo( "Email, password and name are required fields"));
    }

    @After
    public void tearDown(){
        if (accessToken != null && !accessToken.isEmpty() )
            userClient.deleteUser(accessToken)
                    .statusCode(202)
                    .assertThat().body("message", Matchers.equalTo("User successfully removed"));
    }
}
