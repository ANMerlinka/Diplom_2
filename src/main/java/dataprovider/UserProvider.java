package dataprovider;

import org.apache.commons.lang3.RandomStringUtils;
import pojo.UserRequest;

public class UserProvider {
    public static UserRequest getRandomUser(){
        UserRequest user = new UserRequest();
        user.setEmail(RandomStringUtils.randomAlphabetic(15)+"@yandex.ru");
        user.setPassword(RandomStringUtils.randomAlphabetic(15));
        user.setName(RandomStringUtils.randomAlphabetic(15));

        return user;
    }

    public static UserRequest getRandomUserWithoutEmail(){
        UserRequest userRequest = new UserRequest();
        userRequest.setPassword(RandomStringUtils.randomAlphabetic(15));
        userRequest.setName(RandomStringUtils.randomAlphabetic(15));

        return userRequest;
    }

    public static UserRequest getRandomUserWithoutPsw(){
        UserRequest userRequest = new UserRequest();
        userRequest.setEmail(RandomStringUtils.randomAlphabetic(15)+"@yandex.ru");
        userRequest.setName(RandomStringUtils.randomAlphabetic(15));

        return userRequest;
    }

    public static UserRequest getRandomUserWithoutName(){
        UserRequest userRequest = new UserRequest();
        userRequest.setEmail(RandomStringUtils.randomAlphabetic(15)+"@yandex.ru");
        userRequest.setPassword(RandomStringUtils.randomAlphabetic(15));

        return userRequest;
    }

}
