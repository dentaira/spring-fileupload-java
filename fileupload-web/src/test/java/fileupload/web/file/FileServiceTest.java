package fileupload.web.file;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import fileupload.web.file.infra.JdbcFileOwnershipRepository;
import fileupload.web.file.infra.JdbcFileRepository;
import fileupload.web.test.annotation.DatabaseRiderTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@DatabaseRiderTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FileServiceTest {

    FileService sut;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        sut = new FileService(
                new JdbcFileRepository(jdbcTemplate),
                new JdbcFileOwnershipRepository(jdbcTemplate),
                jdbcTemplate);
    }

    @Nested
    @JdbcTest
    @DatabaseRiderTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @DataSet("fileupload/web/file/FileServiceTest-data/DeleteTest/setup-delete.yml")
    @DisplayName("deleteメソッドはファイルを削除する")
    class DeleteTest {

        @Test
        @ExpectedDataSet("fileupload/web/file/FileServiceTest-data/DeleteTest/expected-testDeleteFile.yml")
        @DisplayName("削除するFileのtypeがFileの場合は指定したFileのみ削除する")
        void testDeleteFile() {
            sut.delete("A0EEBC99-9C0B-4EF8-BB6D-6BB9BD380A12");
        }

        @Test
        @ExpectedDataSet("fileupload/web/file/FileServiceTest-data/DeleteTest/expected-testDeleteFolder.yml")
        @DisplayName("削除するFileのtypeがFolderの場合は指定したFileと配下のFile全てを削除する")
        void testDeleteFolder() {
            sut.delete("A0EEBC99-9C0B-4EF8-BB6D-6BB9BD380A11");
        }
    }

    @Nested
    @JdbcTest
    @DatabaseRiderTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @DataSet("fileupload/web/file/FileServiceTest-data/FindAncestorsTest/setup-findAncestors.yml")
    @DisplayName("findAncestorsは祖先フォルダ全てのListを返す")
    class FindAncestorsTest {

        @Test
        void idで指定したファイルの祖先パスを返す() {
            var param = "A0EEBC99-9C0B-4EF8-BB6D-6BB9BD380B13";
            List<StoredFile> actual = sut.findAncestors(param);
            assertEquals(2, actual.size());
            assertEquals(UUID.fromString("A0EEBC99-9C0B-4EF8-BB6D-6BB9BD380A11"), actual.get(0).getId());
            assertEquals(UUID.fromString("A0EEBC99-9C0B-4EF8-BB6D-6BB9BD380B12"), actual.get(1).getId());
        }

        @Test
        void root直下のファイルの場合は空のリストを返す() {
            var param = "A0EEBC99-9C0B-4EF8-BB6D-6BB9BD380A11";
            List<StoredFile> actual = sut.findAncestors(param);
            assertEquals(0, actual.size());
        }
    }
}