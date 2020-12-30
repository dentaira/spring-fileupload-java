package fileupload.web.domain.file.model;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.UUID;

public class StoredFile {

    private final UUID id;

    private final String name;

    /**
     * TODO 必要ない。祖先のStoredFileオブジェクトを持った方がいい。
     * ただし、上記の修正を行なってもDBの経路列挙モデルを持ち込んでいることには代わりない。
     * Javaとしては単純に親のみを参照する木構造にした方がいいかもしれない。
     */
    private final Path path;

    private final FileType type;

    private final InputStream content;

    private final long size;

    public StoredFile(UUID id, String name, Path path, FileType type, InputStream content, long size) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.type = type;
        this.content = content;
        this.size = size;
    }

    public StoredFile(UUID id, String name, Path path, FileType type, long size) {
        // TODO ファイルのコンテンツそのものとメタデータはドメインオブジェクトとして分けるべきか？
        this(id, name, path, type, null, size);
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
}
