package fileupload.web.domain.file.model;

import java.io.InputStream;

public class StoredFile {

    private final int id;

    private final String name;

    private final InputStream content;

    private final long size;

    public StoredFile(int id, String name, InputStream content, long size) {
        this.id = id;
        this.name = name;
        this.content = content;
        this.size = size;
    }

    public StoredFile(int id, String name, long size) {
        // TODO ファイルのコンテンツそのものとメタデータはドメインオブジェクトとして分けるべきか？
        this(id, name, null, size);
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

    public long getSize() {
        return size;
    }
}
