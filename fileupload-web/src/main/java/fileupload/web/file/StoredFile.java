package fileupload.web.file;

import java.nio.file.Path;
import java.util.UUID;

public class StoredFile {

    private final UUID id;

    private final String name;

    private final Path path;

    private final FileType type;

    private final DataSize size;

    public StoredFile(UUID id, String name, Path path, FileType type, DataSize size) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.type = type;
        this.size = size;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Path getPath() {
        return path;
    }

    public FileType getType() {
        return type;
    }

    public DataSize getSize() {
        return size;
    }

    public String displaySize() {
        return isFile() ? getSize().toString() : "";
    }

    public boolean isFile() {
        return type == FileType.FILE;
    }

    public boolean isDirectory() {
        return type == FileType.DIRECTORY;
    }
}
