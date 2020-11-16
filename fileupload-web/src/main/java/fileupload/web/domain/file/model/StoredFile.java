package fileupload.web.domain.file.model;

import java.io.InputStream;
import java.nio.file.Path;

public class StoredFile {

    private final int id;

    private final String name;

    private final Path parent;

    private final FileType type;

    private final InputStream content;

    private final long size;

    public StoredFile(int id, String name, Path parent, FileType type, InputStream content, long size) {
        this.id = id;
        this.name = name;
        this.parent = parent;
        this.type = type;
        this.content = content;
        this.size = size;
    }

    public StoredFile(int id, String name, Path parent, FileType type, long size) {
        // TODO ファイルのコンテンツそのものとメタデータはドメインオブジェクトとして分けるべきか？
        this(id, name, parent, type, null, size);
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

    public static final long ONE_KILOBYTE = 1024L;
    public static final long ONE_MEGABYTE = ONE_KILOBYTE * ONE_KILOBYTE;
    public static final long ONE_GIGABYTE = ONE_MEGABYTE * ONE_KILOBYTE;

    public String displaySize() {

        if (type == FileType.DIRECTORY) {
            return "";
        }

        // TODO 少数桁まで出力したい
        if (size < ONE_KILOBYTE) {
            return size + "B";
        } else if (ONE_KILOBYTE <= size && size < ONE_MEGABYTE) {
            return size / ONE_KILOBYTE + "KB";
        } else if (ONE_MEGABYTE <= size && size < ONE_GIGABYTE) {
            return size / ONE_MEGABYTE + "MB";
        } else {
            return size / ONE_GIGABYTE + "GB";
        }
    }

    public boolean isFile() {
        return type == FileType.FILE;
    }

    public boolean isDirectory() {
        return type == FileType.DIRECTORY;
    }

    public Path getPath() {
        // TODO DBに保存するのはidにしないとファイル名変更がきついなぁ。画面表示がめんどい
        return parent.resolve(name);
    }
}
