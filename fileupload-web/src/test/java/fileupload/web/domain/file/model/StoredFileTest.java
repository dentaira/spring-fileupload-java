package fileupload.web.domain.file.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StoredFileTest {

    @ParameterizedTest
    @CsvSource({
            "1,             1B",
            "2,             2B",
            "1023,          1023B",
            "1024,          1KB",
            "1047552,       1023KB",
            "1048575,       1023KB", // TODO 1MB-1B 切り捨てより切り上げの方が良いのでは
            "1048576,       1MB",
            "1073741823,    1023MB",
            "1073741824,    1GB",
    })
    public void displaySizeはsizeの単位を計算してフォーマットした文字列を返す(long size, String expected) {
        var sut = new StoredFile(123456, "name", "parent", FileType.FILE, size);
        assertEquals(expected, sut.displaySize());
    }
}