package fileupload.web.user.infra;

import fileupload.web.user.UserAccount;
import fileupload.web.user.UserAccountRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcUserAccountRepository implements UserAccountRepository {

    private JdbcTemplate jdbcTemplate;

    public JdbcUserAccountRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public UserAccount findByEmail(String email) {
        return jdbcTemplate.query(
                "SELECT email, name, password, role FROM user_account WHERE email = ?",
                pss -> pss.setString(1, email),
                rs -> {
                    rs.next();
                    return new UserAccount(
                            rs.getString("email"),
                            rs.getString("name"),
                            rs.getString("password"),
                            rs.getString("role")
                    );
                }
        );
    }
}
