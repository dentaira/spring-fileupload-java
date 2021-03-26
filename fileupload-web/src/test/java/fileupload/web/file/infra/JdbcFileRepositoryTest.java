package fileupload.web.file.infra;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import fileupload.web.file.FileType;
import fileupload.web.file.Owner;
import fileupload.web.file.StoredFile;
import fileupload.web.test.annotation.DatabaseRiderTest;
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
            var owner = new Owner(UUID.fromString("B0EEBC99-9C0B-4EF8-BB6D-6BB9BD380A11"));
            List<StoredFile> actual = sut.searchRoot(owner);
            assertEquals(1, actual.size());
        }

        @Test
        @DataSet("fileupload/web/file/infra/JdbcFileRepositoryTest-data/SearchRootTest/setup-testFindThree.yml")
        @DisplayName("Root配下のFileが3つの場合は3つ取得する")
        void testFindThree() {
            var owner = new Owner(UUID.fromString("B0EEBC99-9C0B-4EF8-BB6D-6BB9BD380A13"));
            List<StoredFile> actual = sut.searchRoot(owner);
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
            var owner = new Owner(UUID.fromString("B0EEBC99-9C0B-4EF8-BB6D-6BB9BD380A13"));
            List<StoredFile> actual = sut.search("A0EEBC99-9C0B-4EF8-BB6D-6BB9BD380B13", owner);
            assertEquals(1, actual.size());
        }

        @Test
        @DisplayName("指定したFolder配下のFileが3つの場合は3つ取得する")
        void testFindThree() {
            var owner = new Owner(UUID.fromString("B0EEBC99-9C0B-4EF8-BB6D-6BB9BD380A14"));
            List<StoredFile> actual = sut.search("A0EEBC99-9C0B-4EF8-BB6D-6BB9BD380A11", owner);
            assertEquals(3, actual.size());
        }

        @Test
        @DisplayName("指定したFolder配下にFileが存在しない場合は空のListを返す")
        void testFindZero() {
            var owner = new Owner(UUID.fromString("B0EEBC99-9C0B-4EF8-BB6D-6BB9BD380A13"));
            List<StoredFile> actual = sut.search("A0EEBC99-9C0B-4EF8-BB6D-6BB9BD380B12", owner);
            assertEquals(0, actual.size());
        }

        @Test
        @DisplayName("指定したFileのtypeがFileだった場合は例外が発生する")
        void testSearchFile() {
            var owner = new Owner(UUID.fromString("B0EEBC99-9C0B-4EF8-BB6D-6BB9BD380A13"));
            List<StoredFile> actual = sut.search("A0EEBC99-9C0B-4EF8-BB6D-6BB9BD380A12", owner);
            assertEquals(0, actual.size());
        }
    }

    @Nested
    @JdbcTest
    @DatabaseRiderTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @DataSet("fileupload/web/file/infra/JdbcFileRepositoryTest-data/FindByIdTest/setup-findById.yml")
    @DisplayName("findById(String,Owner)はOwnerが所有するidが一致するFileを取得する")
    class FindByIdTest {

        @Test
        void testFindOneDirectory() {
            // given
            var owner = new Owner(UUID.fromString("B0EEBC99-9C0B-4EF8-BB6D-6BB9BD380A13"));
            UUID fileId = UUID.fromString("A0EEBC99-9C0B-4EF8-BB6D-6BB9BD380A11");
            StoredFile expected = new StoredFile(
                    fileId,
                    "フォルダ１",
                    Path.of("/A0EEBC99-9C0B-4EF8-BB6D-6BB9BD380A11/"),
                    FileType.DIRECTORY,
                    null,
                    0L);
            // when
            StoredFile actual = sut.findById(fileId.toString(), owner);
            // then
            assertThat(actual).isEqualToComparingFieldByField(expected);
        }

        @Test
        void testFindOneFile() {
            // given
            var owner = new Owner(UUID.fromString("B0EEBC99-9C0B-4EF8-BB6D-6BB9BD380A14"));
            UUID fileId = UUID.fromString("A0EEBC99-9C0B-4EF8-BB6D-6BB9BD380B11");
            StoredFile expected = new StoredFile(
                    fileId,
                    "ファイル４",
                    Path.of("/A0EEBC99-9C0B-4EF8-BB6D-6BB9BD380A11/A0EEBC99-9C0B-4EF8-BB6D-6BB9BD380B11/"),
                    FileType.FILE,
                    new ByteArrayInputStream("file4 content".getBytes()),
                    3L);
            // when
            StoredFile actual = sut.findById(fileId.toString(), owner);
            // then
            assertThat(actual).isEqualToIgnoringGivenFields(expected, "content");
            assertThat(actual.getContent()).hasSameContentAs(expected.getContent());
        }

        @Test
        void testUserNotMatched() {
            // given
            var owner = new Owner(UUID.fromString("B0EEBC99-9C0B-4EF8-BB6D-6BB9BD380A13"));
            UUID fileId = UUID.fromString("A0EEBC99-9C0B-4EF8-BB6D-6BB9BD380B11");
            StoredFile expected = new StoredFile(
                    fileId,
                    "ファイル４",
                    Path.of("/A0EEBC99-9C0B-4EF8-BB6D-6BB9BD380A11/A0EEBC99-9C0B-4EF8-BB6D-6BB9BD380B11/"),
                    FileType.FILE,
                    new ByteArrayInputStream("file4 content".getBytes()),
                    3L);
            // when
            StoredFile actual = sut.findById(fileId.toString(), owner);
            // then
            assertThat(actual).isNull();
        }

        @Test
        void testFileNotFound() {
            // given
            var owner = new Owner(UUID.fromString("B0EEBC99-9C0B-4EF8-BB6D-6BB9BD380A13"));
            UUID fileId = UUID.fromString("A0EEBC99-9C0B-4EF8-BB6D-6BB9BD380B99");
            StoredFile expected = new StoredFile(
                    fileId,
                    "ファイル４",
                    Path.of("/A0EEBC99-9C0B-4EF8-BB6D-6BB9BD380A11/A0EEBC99-9C0B-4EF8-BB6D-6BB9BD380B11/"),
                    FileType.FILE,
                    new ByteArrayInputStream("file4 content".getBytes()),
                    3L);
            // when
            StoredFile actual = sut.findById(fileId.toString(), owner);
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

    @Nested
    @JdbcTest
    @DatabaseRiderTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @DataSet("fileupload/web/file/infra/JdbcFileRepositoryTest-data/DeleteTest/setup-delete.yml")
    @DisplayName("deleteはStoredFileを削除する")
    class DeleteTest {

        @Test
        @ExpectedDataSet("fileupload/web/file/infra/JdbcFileRepositoryTest-data/DeleteTest/expected-testDeleteFile.yml")
        @DisplayName("削除するFileのtypeがFileの場合は指定したFileのみ削除する")
        void testDeleteFile() {
            // given
            var file = new StoredFile(UUID.fromString("A0EEBC99-9C0B-4EF8-BB6D-6BB9BD380A12"),
                    "name",
                    Path.of("/A0EEBC99-9C0B-4EF8-BB6D-6BB9BD380A12/"),
                    FileType.FILE,
                    2L);
            // when
            sut.delete(file);
        }

        @Test
        @ExpectedDataSet("fileupload/web/file/infra/JdbcFileRepositoryTest-data/DeleteTest/expected-testDeleteFolder.yml")
        @DisplayName("削除するFileのtypeがFolderの場合は指定したFileと配下のFile全てを削除する")
        void testDeleteFolder() {
            // given
            var file = new StoredFile(UUID.fromString("A0EEBC99-9C0B-4EF8-BB6D-6BB9BD380A11"),
                    "name",
                    Path.of("/A0EEBC99-9C0B-4EF8-BB6D-6BB9BD380A11/"),
                    FileType.DIRECTORY,
                    0L);
            // when
            sut.delete(file);
        }
    }
}