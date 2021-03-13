package fileupload.web.user;

public class UserAccount {

    private String email;

    private String name;

    private String password;

    private String role;

    public UserAccount(String email, String name, String password, String role) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }
}
