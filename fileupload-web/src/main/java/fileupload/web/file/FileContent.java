package fileupload.web.file;

import java.io.InputStream;

public class FileContent {

    private StoredFile file;

    private InputStream stream;

    public FileContent(StoredFile file, InputStream stream) {
        this.file = file;
        this.stream = stream;
    }

    public StoredFile getFile() {
        return file;
    }

    public InputStream getStream() {
        return stream;
    }
}
