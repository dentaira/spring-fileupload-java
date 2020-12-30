package fileupload.web.domain.file.service;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import fileupload.web.domain.file.model.Directories;
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
import java.util.UUID;

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
        void 引数にファイルIDを渡した場合は例外が発生する() {
            List<StoredFile> actual = sut.search("A0EEBC99-9C0B-4EF8-BB6D-6BB9BD380A12");
            assertEquals(0, actual.size());
        }
    }

    @Nested
    @JdbcTest
    @DbUnitTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    class deleteメソッドはファイルを削除する {

        @BeforeEach
        public void setUp() {
            sut = new FileService(jdbcTemplate);
        }

        @Test
        @DatabaseSetup("FileServiceTest.delete.xml")
        @ExpectedDatabase(value = "expected-引数にファイルのIDを渡した場合は指定したファイルのみを削除する.xml"
                , assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
        void 引数にファイルのIDを渡した場合は指定したファイルを削除する() {
            int count = sut.delete("A0EEBC99-9C0B-4EF8-BB6D-6BB9BD380A12");
            assertEquals(1, count);
        }

        @Test
        @DatabaseSetup("FileServiceTest.delete.xml")
        @ExpectedDatabase(value = "expected-引数にフォルダのIDを渡した場合は指定したフォルダおよび配下のファイルとフォルダを全て削除する.xml"
                , assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
        void 引数にフォルダのIDを渡した場合は指定したフォルダおよび配下のファイルとフォルダを全て削除する() {
            int count = sut.delete("A0EEBC99-9C0B-4EF8-BB6D-6BB9BD380A11");
            assertEquals(3, count);
        }
    }

    @Nested
    @JdbcTest
    @DbUnitTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    class findAncestorsは祖先フォルダ全てのListを返す {

        @BeforeEach
        public void setUp() {
            sut = new FileService(jdbcTemplate);
        }

        @Test
        @DatabaseSetup("FileServiceTest.findAncestors.xml")
        void idで指定したファイルの祖先パスを返す() {
            var param = "A0EEBC99-9C0B-4EF8-BB6D-6BB9BD380B13";
            Directories actual = sut.findAncestors(param);
            List<StoredFile> list = actual.value();
            assertEquals(2, list.size());
            assertEquals(UUID.fromString("A0EEBC99-9C0B-4EF8-BB6D-6BB9BD380A11"), list.get(0).getId());
            assertEquals(UUID.fromString("A0EEBC99-9C0B-4EF8-BB6D-6BB9BD380B12"), list.get(1).getId());
        }
    }
}