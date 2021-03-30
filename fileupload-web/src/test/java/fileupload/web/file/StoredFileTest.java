package fileupload.web.file;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StoredFileTest {

    @ParameterizedTest
    @CsvSource({
            "1,             FILE,       1B",
            "1024,          FILE,       1KB",
            "1048576,       FILE,       1MB",
            "1073741824,    FILE,       1GB",
            "0,             FILE,       0B",
            "0,             DIRECTORY,  ''",
            "1,             DIRECTORY,  ''",
            "1000,          DIRECTORY,  ''",
    })
    public void testDisplaySize(long size, FileType type, String expected) {
        var sut = new StoredFile(UUID.randomUUID(), "name", Path.of("parent"), type, DataSize.of(size));
        assertEquals(expected, sut.displaySize());
    }
}