package pojo;

public class ChangeUserDataRequest {
    private String email;
    private String name;

    public ChangeUserDataRequest(String email, String name) {
        this.email = email;
        this.name = name;
    }
    //private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
    public static ChangeUserDataRequest form(UserRequest userRequest) {
        return new ChangeUserDataRequest(userRequest.getEmail(), userRequest.getName());
    }
}
