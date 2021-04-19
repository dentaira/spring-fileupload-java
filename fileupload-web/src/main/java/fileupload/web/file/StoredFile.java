package fileupload.web.file;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.UUID;

public class StoredFile {

    private UUID id;

    private String name;

    private Path path;

    private FileType type;

    private InputStream content;

    private DataSize size;

    public StoredFile(UUID id, String name, Path path, FileType type, DataSize size) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.type = type;
        this.size = size;
    }

    public StoredFile(UUID id, String name, Path path, FileType type, InputStream content, DataSize size) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.type = type;
        this.content = content;
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

    public InputStream getContent() {
        return content;
    }

    public DataSize getSize() {
        return size;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public void setType(FileType type) {
        this.type = type;
    }

    public void setContent(InputStream content) {
        this.content = content;
    }

    public void setSize(DataSize size) {
        this.size = size;
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
