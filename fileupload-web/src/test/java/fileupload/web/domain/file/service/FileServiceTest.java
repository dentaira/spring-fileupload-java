package fileupload.web.domain.file.service;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import fileupload.web.domain.file.model.StoredFile;
import fileupload.web.test.infra.dbunit.annotation.DbUnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@DbUnitTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FileServiceTest {

    FileService sut;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Nested
    @JdbcTest
    @DbUnitTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    class searchは指定したパス配下のファイルを取得する {

        @BeforeEach
        public void setUp() {
            sut = new FileService(jdbcTemplate);
        }

        @Test
        @DatabaseSetup("FileServiceTest.search.xml")
        void 引数なしの場合はroot配下のファイルを返す() {
            List<StoredFile> actual = sut.search();
            assertEquals(3, actual.size());
        }

        @Test
        @DatabaseSetup("FileServiceTest.search.xml")
        void 引数にフォルダIDを渡した場合は配下のファイルを返す() {
            List<StoredFile> actual = sut.search("A0EEBC99-9C0B-4EF8-BB6D-6BB9BD380A11");
            assertEquals(2, actual.size());
        }

        @Test
        @DatabaseSetup("FileServiceTest.search.xml")
        void 引数にフォルダIDを渡して配下にファイルが存在しない場合は空のリストを返す() {
            List<StoredFile> actual = sut.search("A0EEBC99-9C0B-4EF8-BB6D-6BB9BD380B12");
            assertEquals(0, actual.size());
        }

        @Test
        @DatabaseSetup("FileServiceTest.search.xml")
        void 引数にファイルIDを渡した場合は空例外が発生する() {
            List<StoredFile> actual = sut.search("A0EEBC99-9C0B-4EF8-BB6D-6BB9BD380A12");
            assertEquals(0, actual.size());
        }
    }
}