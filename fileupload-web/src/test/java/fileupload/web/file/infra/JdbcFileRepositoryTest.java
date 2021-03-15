package fileupload.web.file.infra;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import fileupload.web.file.FileType;
import fileupload.web.file.StoredFile;
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@DatabaseRiderTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class JdbcFileRepositoryTest {

    JdbcFileRepository sut;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        sut = new JdbcFileRepository(jdbcTemplate);
    }

    @Nested
    @JdbcTest
    @DatabaseRiderTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @DisplayName("searchRoot(UserAccount)はUserAccountが所有するRoot直下のFileを取得する")
    class SearchRootTest {

        @Test
        @DataSet("fileupload/web/file/infra/JdbcFileRepositoryTest-data/SearchRootTest/setup-testFindOne.yml")
        @DisplayName("Root配下のFileが1つの場合は1つ取得する")
        void testFindOne() {
            UserAccount user = new UserAccount(
                    UUID.fromString("B0EEBC99-9C0B-4EF8-BB6D-6BB9BD380A11"),
                    "email",
                    "name",
                    "password",
                    "role");
            List<StoredFile> actual = sut.searchRoot(user);
            assertEquals(1, actual.size());
        }

        @Test
        @DataSet("fileupload/web/file/infra/JdbcFileRepositoryTest-data/SearchRootTest/setup-testFindThree.yml")
        @DisplayName("Root配下のFileが3つの場合は3つ取得する")
        void testFindThree() {
            UserAccount user = new UserAccount(
                    UUID.fromString("B0EEBC99-9C0B-4EF8-BB6D-6BB9BD380A13"),
                    "email",
                    "name",
                    "password",
                    "role");
            List<StoredFile> actual = sut.searchRoot(user);
            assertEquals(3, actual.size());
        }
    }

    @Nested
    @JdbcTest
    @DatabaseRiderTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @DataSet("fileupload/web/file/infra/JdbcFileRepositoryTest-data/SearchTest/setup-search.yml")
    @DisplayName("search(String,UserAccount)はUserAccountが所有する指定したFolder直下にあるFileを取得する")
    class SearchTest {

        @Test
        @DisplayName("指定したFolder配下のFileが1つの場合は1つ取得する")
        void testFindOne() {
            UserAccount user = new UserAccount(
                    UUID.fromString("B0EEBC99-9C0B-4EF8-BB6D-6BB9BD380A13"),
                    "email",
                    "name",
                    "password",
                    "role");
            List<StoredFile> actual = sut.search("A0EEBC99-9C0B-4EF8-BB6D-6BB9BD380B13", user);
            assertEquals(1, actual.size());
        }

        @Test
        @DisplayName("指定したFolder配下のFileが3つの場合は3つ取得する")
        void testFindThree() {
            UserAccount user = new UserAccount(
                    UUID.fromString("B0EEBC99-9C0B-4EF8-BB6D-6BB9BD380A14"),
                    "email",
                    "name",
                    "password",
                    "role");
            List<StoredFile> actual = sut.search("A0EEBC99-9C0B-4EF8-BB6D-6BB9BD380A11", user);
            assertEquals(3, actual.size());
        }

        @Test
        @DisplayName("指定したFolder配下にFileが存在しない場合は空のListを返す")
        void testFindZero() {
            UserAccount user = new UserAccount(
                    UUID.fromString("B0EEBC99-9C0B-4EF8-BB6D-6BB9BD380A13"),
                    "email",
                    "name",
                    "password",
                    "role");
            List<StoredFile> actual = sut.search("A0EEBC99-9C0B-4EF8-BB6D-6BB9BD380B12", user);
            assertEquals(0, actual.size());
        }

        @Test
        @DisplayName("指定したFileのtypeがFileだった場合は例外が発生する")
        void testSearchFile() {
            UserAccount user = new UserAccount(
                    UUID.fromString("B0EEBC99-9C0B-4EF8-BB6D-6BB9BD380A13"),
                    "email",
                    "name",
                    "password",
                    "role");
            List<StoredFile> actual = sut.search("A0EEBC99-9C0B-4EF8-BB6D-6BB9BD380A12", user);
            assertEquals(0, actual.size());
        }
    }

    @Nested
    @JdbcTest
    @DatabaseRiderTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @DataSet("fileupload/web/file/infra/JdbcFileRepositoryTest-data/FindByIdTest/setup-findById.yml")
    @DisplayName("findById(String,UserAccount)はUserAccountが所有するidが一致するFileを取得する")
    class FindByIdTest {

        @Test
        void testFindOneDirectory() {
            // given
            UserAccount user = new UserAccount(UUID.fromString("B0EEBC99-9C0B-4EF8-BB6D-6BB9BD380A13"),
                    "email", "name", "password", "USER");
            UUID fileId = UUID.fromString("A0EEBC99-9C0B-4EF8-BB6D-6BB9BD380A11");
            StoredFile expected = new StoredFile(
                    fileId,
                    "フォルダ１",
                    Path.of("/A0EEBC99-9C0B-4EF8-BB6D-6BB9BD380A11/"),
                    FileType.DIRECTORY,
                    null,
                    0L);
            // when
            StoredFile actual = sut.findById(fileId.toString(), user);
            // then
            assertThat(actual).isEqualToComparingFieldByField(expected);
        }

        @Test
        void testFindOneFile() throws IOException {
            // given
            UserAccount user = new UserAccount(UUID.fromString("B0EEBC99-9C0B-4EF8-BB6D-6BB9BD380A14"),
                    "email", "name", "password", "USER");
            UUID fileId = UUID.fromString("A0EEBC99-9C0B-4EF8-BB6D-6BB9BD380B11");
            StoredFile expected = new StoredFile(
                    fileId,
                    "ファイル４",
                    Path.of("/A0EEBC99-9C0B-4EF8-BB6D-6BB9BD380A11/A0EEBC99-9C0B-4EF8-BB6D-6BB9BD380B11/"),
                    FileType.FILE,
                    new ByteArrayInputStream("file4 content".getBytes()),
                    3L);
            // when
            StoredFile actual = sut.findById(fileId.toString(), user);
            // then
            assertThat(actual).isEqualToIgnoringGivenFields(expected, "content");
            assertThat(actual.getContent()).hasSameContentAs(expected.getContent());
        }

        @Test
        void testUserNotMatched() throws IOException {
            // given
            UserAccount user = new UserAccount(UUID.fromString("B0EEBC99-9C0B-4EF8-BB6D-6BB9BD380A13"),
                    "email", "name", "password", "USER");
            UUID fileId = UUID.fromString("A0EEBC99-9C0B-4EF8-BB6D-6BB9BD380B11");
            StoredFile expected = new StoredFile(
                    fileId,
                    "ファイル４",
                    Path.of("/A0EEBC99-9C0B-4EF8-BB6D-6BB9BD380A11/A0EEBC99-9C0B-4EF8-BB6D-6BB9BD380B11/"),
                    FileType.FILE,
                    new ByteArrayInputStream("file4 content".getBytes()),
                    3L);
            // when
            StoredFile actual = sut.findById(fileId.toString(), user);
            // then
            assertThat(actual).isNull();
        }

        @Test
        void testFileNotFound() throws IOException {
            // given
            UserAccount user = new UserAccount(UUID.fromString("B0EEBC99-9C0B-4EF8-BB6D-6BB9BD380A13"),
                    "email", "name", "password", "USER");
            UUID fileId = UUID.fromString("A0EEBC99-9C0B-4EF8-BB6D-6BB9BD380B99");
            StoredFile expected = new StoredFile(
                    fileId,
                    "ファイル４",
                    Path.of("/A0EEBC99-9C0B-4EF8-BB6D-6BB9BD380A11/A0EEBC99-9C0B-4EF8-BB6D-6BB9BD380B11/"),
                    FileType.FILE,
                    new ByteArrayInputStream("file4 content".getBytes()),
                    3L);
            // when
            StoredFile actual = sut.findById(fileId.toString(), user);
            // then
            assertThat(actual).isNull();
        }
    }

    @Nested
    @JdbcTest
    @DatabaseRiderTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @DataSet("fileupload/web/file/infra/JdbcFileRepositoryTest-data/SaveTest/setup-save.yml")
    @DisplayName("saveはStoredFileを登録する")
    class SaveTest {

        @Test
        @ExpectedDataSet("fileupload/web/file/infra/JdbcFileRepositoryTest-data/SaveTest/expected-testSaveBible.yml")
        void testSaveBible() {
            // given
            UUID id = UUID.fromString("A0EEBC99-9C0B-4EF8-BB6D-6BB9BD380A11");
            var file = new StoredFile(
                    id,
                    "Bible",
                    Path.of("/parent/" + id.toString() + "/"),
                    FileType.FILE,
                    new ByteArrayInputStream("content".getBytes(StandardCharsets.UTF_8)),
                    3L
            );
            // when
            sut.save(file);
        }

        @Test
        @ExpectedDataSet("fileupload/web/file/infra/JdbcFileRepositoryTest-data/SaveTest/expected-testSavePandora.yml")
        void testSavePandora() {
            // given
            UUID id = UUID.fromString("A0EEBC99-9C0B-4EF8-BB6D-6BB9BD380A12");
            var file = new StoredFile(
                    id,
                    "Pandora",
                    Path.of("/parent/" + id.toString() + "/"),
                    FileType.DIRECTORY,
                    null,
                    0L
            );
            // when
            sut.save(file);
        }
    }
}