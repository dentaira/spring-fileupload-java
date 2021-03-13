package fileupload.web.user.infra;

import com.github.database.rider.core.api.dataset.DataSet;
import fileupload.web.test.annotation.DatabaseRiderTest;
import fileupload.web.user.UserAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@DatabaseRiderTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class JdbcUserAccountRepositoryTest {

    JdbcUserAccountRepository sut;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        sut = new JdbcUserAccountRepository(jdbcTemplate);
    }

    @Nested
    @JdbcTest
    @DatabaseRiderTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @DataSet("fileupload/web/user/infra/JdbcUserAccountRepositoryTest-data/setup-findByEmail.yml")
    @DisplayName("findByEmailはemailが一致するUserを取得する")
    class FindByEmailTest {

        @Test
        void testFindWolverine() {
            // when
            UserAccount actual = sut.findByEmail("wolverine@marvel");
            // then
            UserAccount expected = new UserAccount(UUID.fromString("07c91478-e1ee-4b00-a436-f427ecb2d4f1"), "wolverine@marvel", "Wolverine", "password01", "USER");
            assertThat(actual).isEqualToComparingFieldByField(expected);
        }

        @Test
        void testFindIronman() {
            // when
            UserAccount actual = sut.findByEmail("ironman@marvel");
            // then
            UserAccount expected = new UserAccount(UUID.fromString("07c91478-e1ee-4b00-a436-f427ecb2d4f2"),"ironman@marvel", "Ironman", "password02", "ADMIN");
            assertThat(actual).isEqualToComparingFieldByField(expected);
        }

        @Test
        void testFindZero() {
            // when
            UserAccount actual = sut.findByEmail("captain_america@marvel");
            // then
            assertThat(actual).isNull();
        }
    }
}