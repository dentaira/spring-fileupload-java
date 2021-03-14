package fileupload.web.file.infra;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import fileupload.web.file.FileType;
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
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.UUID;

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
    @DataSet("fileupload/web/file/infra/JdbcFileRepositoryTest-data/setup-save.yml")
    @DisplayName("saveはStoredFileを登録する")
    class SaveTest {

        @Test
        @ExpectedDataSet("fileupload/web/file/infra/JdbcFileRepositoryTest-data/expected-testSaveBible.yml")
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
        @ExpectedDataSet("fileupload/web/file/infra/JdbcFileRepositoryTest-data/expected-testSavePandora.yml")
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