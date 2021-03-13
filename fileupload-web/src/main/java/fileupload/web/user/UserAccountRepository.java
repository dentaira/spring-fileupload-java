package fileupload.web.user;

public interface UserAccountRepository {

    public UserAccount findByEmail(String email);
}
