import clients.UserClient;
import dataprovider.UserProvider;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Test;
import pojo.LoginRequest;
import pojo.UserRequest;


public class ChangeUserDataTest {
    UserClient userClient = new UserClient();
    private String accessToken;

    // Изменение данных пользователя с авторизацией
    @Test
    @DisplayName("Changing user data with authorization - Name")
    @Description("Checking response body (user.name) and status code 200")
    public void checkChangeNameInUserDataWithAuth() {
        // создание пользователя
        UserRequest randomUserRequest = UserProvider.getRandomUser();

        userClient.createUser(randomUserRequest)
                .statusCode(200)
                .body("success", Matchers.equalTo(true));

        //авторизация
        LoginRequest loginRequest = LoginRequest.from(randomUserRequest);

        accessToken = userClient.login(loginRequest)
                .statusCode(200)
                .body("success", Matchers.equalTo(true))
                .extract().jsonPath().getString("accessToken").replace("Bearer ","");

        // Изменение поля Name
        String newFieldName = randomUserRequest.getName() + "_new";
        randomUserRequest.setName(newFieldName);

        userClient.changeDataWithAuth(randomUserRequest, accessToken)
                .statusCode(200)
                .assertThat().body("user.name", Matchers.equalTo(newFieldName));
    }

    @Test
    @DisplayName("Changing user data with authorization - Email")
    @Description("Checking response body (user.email) and status code 200")
    public void checkChangeEmailInUserDataWithAuth() {
        // создание пользователя
        UserRequest randomUserRequest = UserProvider.getRandomUser();

        userClient.createUser(randomUserRequest)
                .statusCode(200)
                .body("success", Matchers.equalTo(true));

        //авторизация
        LoginRequest loginRequest = LoginRequest.from(randomUserRequest);

        accessToken = userClient.login(loginRequest)
                .statusCode(200)
                .body("success", Matchers.equalTo(true))
                .extract().jsonPath().getString("accessToken").replace("Bearer ","");

        // Изменение поля Email
        String randomNewEmail = RandomStringUtils.randomAlphabetic(15)+"@yandex.ru";
        randomUserRequest.setEmail(randomNewEmail);

        userClient.changeDataWithAuth(randomUserRequest, accessToken)
                .statusCode(200)
                .assertThat().body("user.email", Matchers.equalTo(randomNewEmail.toLowerCase()));
    }

    @Test
    @DisplayName("Changing user data with authorization - Password")
    @Description("Checking status code 200")
    public void checkChangePasswordInUserDataWithAuth() {
        // создание пользователя
        UserRequest randomUserRequest = UserProvider.getRandomUser();

        userClient.createUser(randomUserRequest)
                .statusCode(200)
                .body("success", Matchers.equalTo(true));

        //авторизация
        LoginRequest loginRequest = LoginRequest.from(randomUserRequest);

        accessToken = userClient.login(loginRequest)
                .statusCode(200)
                .body("success", Matchers.equalTo(true))
                .extract().jsonPath().getString("accessToken").replace("Bearer ","");

        // Изменение поля Password
        String newFieldPsw = randomUserRequest.getPassword() + "_new";
        randomUserRequest.setPassword(newFieldPsw);

        userClient.changeDataWithAuth(randomUserRequest, accessToken)
                .statusCode(200);
                //.assertThat().body("user.email", Matchers.equalTo(randomNewEmail));

        //авторизация c новым паролем
        LoginRequest loginRequestWithNewPwd = LoginRequest.from(randomUserRequest);

        userClient.login(loginRequestWithNewPwd)
                .statusCode(200)
                .body("success", Matchers.equalTo(true));
    }

    // Изменение данных пользователя без авторизации
    @Test
    @DisplayName("Changing user data without authorization - Name")
    @Description("Checking response body (message) and status code 401")
    public void checkUpdateNameInUserDataWithoutAuth() {
        // создание пользователя
        UserRequest randomUserRequest = UserProvider.getRandomUser();

        accessToken = userClient.createUser(randomUserRequest)
                .statusCode(200)
                .body("success", Matchers.equalTo(true))
                .extract().jsonPath().getString("accessToken").replace("Bearer ","");

        // Изменение поля Name
        String newFieldName = randomUserRequest.getName() + "_new";
        randomUserRequest.setName(newFieldName);

        userClient.changeDataWithoutAuth(randomUserRequest)
                .statusCode(401)
                .assertThat().body("message", Matchers.equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Changing user data without authorization - Email")
    @Description("Checking response body (message) and status code 401")
    public void checkChangeEmailInUserDataWithoutAuth() {
        // создание пользователя
        UserRequest randomUserRequest = UserProvider.getRandomUser();

        accessToken = userClient.createUser(randomUserRequest)
                .statusCode(200)
                .body("success", Matchers.equalTo(true))
                .extract().jsonPath().getString("accessToken").replace("Bearer ","");

        // Изменение поля Email
        String randomNewEmail = RandomStringUtils.randomAlphabetic(15)+"@yandex.ru";
        randomUserRequest.setEmail(randomNewEmail);

        userClient.changeDataWithoutAuth(randomUserRequest)
                .statusCode(401)
                .assertThat().body("message", Matchers.equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Changing user data without authorization - Password")
    @Description("Checking response body (message) and status code 401")
    public void checkChangePasswordInUserDataWithoutAuth() {
        // создание пользователя
        UserRequest randomUserRequest = UserProvider.getRandomUser();

        accessToken = userClient.createUser(randomUserRequest)
                .statusCode(200)
                .body("success", Matchers.equalTo(true))
                .extract().jsonPath().getString("accessToken").replace("Bearer ","");

        // Изменение поля Password
        String newFieldPsw = randomUserRequest.getPassword() + "_new";
        randomUserRequest.setPassword(newFieldPsw);

        userClient.changeDataWithoutAuth(randomUserRequest)
                .statusCode(401)
                .assertThat().body("message", Matchers.equalTo("You should be authorised"));
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
