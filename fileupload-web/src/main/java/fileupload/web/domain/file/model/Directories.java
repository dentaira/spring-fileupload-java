package fileupload.web.domain.file.model;

import java.util.Collections;
import java.util.List;

public class Directories {

    private List<StoredFile> value;

    public static Directories empty() {
        return new Directories(Collections.emptyList());
    }

    public Directories(List<StoredFile> value) {
        this.value = value;
    }

    public List<StoredFile> value() {
        return value;
    }
}
