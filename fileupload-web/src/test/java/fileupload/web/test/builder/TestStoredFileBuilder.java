package fileupload.web.test.builder;

import fileupload.web.file.DataSize;
import fileupload.web.file.FileType;
import fileupload.web.file.StoredFile;

import java.nio.file.Path;
import java.util.UUID;

public class TestStoredFileBuilder {

    private UUID id = UUID.randomUUID();

    private String name = "default-name";

    private Path path = Path.of("default-path");

    private FileType type = FileType.FILE;

    private DataSize size = DataSize.of(0L);

    public StoredFile build() {
        return new StoredFile(id, name, path, type, size);
    }

    public TestStoredFileBuilder withId(UUID id) {
        this.id = id;
        return this;
    }

    public TestStoredFileBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public TestStoredFileBuilder withPath(Path path) {
        this.path = path;
        return this;
    }

    public TestStoredFileBuilder withType(FileType type) {
        this.type = type;
        return this;
    }

    public TestStoredFileBuilder withSize(DataSize size) {
        this.size = size;
        return this;
    }
}
