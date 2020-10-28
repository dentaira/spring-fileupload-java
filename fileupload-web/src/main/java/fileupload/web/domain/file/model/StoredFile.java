package fileupload.web.domain.file.model;

import java.io.InputStream;

public class StoredFile {

    private final int id;

    private final String name;

    private final InputStream content;

    public StoredFile(int id, String name, InputStream content) {
        this.id = id;
        this.name = name;
        this.content = content;
    }

    public StoredFile(int id, String name) {
        this(id, name, null);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public InputStream getContent() {
        return content;
    }
}
