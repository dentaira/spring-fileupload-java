package fileupload.web.domain.file.service;

import fileupload.web.domain.file.model.Directories;
import fileupload.web.domain.file.model.FileType;
import fileupload.web.domain.file.model.StoredFile;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.sql.Types;
import java.util.*;

@Service
public class FileService {

    public static final Path ROOT_PATH = Path.of("/");

    private Logger logger = LoggerFactory.getLogger(getClass());

    // TODO Repositoryを作成する
    final JdbcTemplate jdbcTemplate;

    public FileService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(readOnly = true)
    public List<StoredFile> searchRoot() {
        return jdbcTemplate.query(
                "SELECT id, name, path, type, size FROM file WHERE cast(id as text) = replace(path, '/', '')"
                , (rs, rowNum) -> {
                    return new StoredFile(UUID.fromString(rs.getString("id"))
                            , rs.getString("name")
                            , Path.of(rs.getString("path"))
                            , FileType.valueOf(rs.getString("type"))
                            , rs.getLong("size"));
                });
    }

    @Transactional(readOnly = true)
    public List<StoredFile> search(String dirId) {
        int level = jdbcTemplate.queryForObject(
                "SELECT LENGTH(path) - LENGTH(REPLACE(path, '/', '')) FROM file WHERE id = ?"
                , new Object[]{dirId}
                , Integer.class);

        return jdbcTemplate.query(
                "SELECT id, name, path, type, size FROM file"
                        + " WHERE path LIKE (SELECT path FROM file WHERE id = ?) || '_%'"
                        + " AND LENGTH(path) - LENGTH(REPLACE(path, '/', '')) = (? + 1)"
                , (ps) -> {
                    ps.setString(1, dirId);
                    ps.setInt(2, level);
                }
                , (rs, rowNum) -> {
                    return new StoredFile(UUID.fromString(rs.getString("id"))
                            , rs.getString("name")
                            , Path.of(rs.getString("path"))
                            , FileType.valueOf(rs.getString("type"))
                            , rs.getLong("size"));
                });
    }

    @Transactional(rollbackFor = Exception.class)
    public void register(MultipartFile multipartFile, Path parentPath) {
        // TODO MultipartFileに依存しないようにする

        try (InputStream in = multipartFile.getInputStream()) {
            var id = UUID.randomUUID().toString();
            int result = jdbcTemplate.update(
                    "INSERT INTO file(id, name, content, size, path, type) VALUES(?, ?, ?, ?, ?, ?)"
                    , (ps) -> {
                        ps.setString(1, id);
                        ps.setString(2, multipartFile.getOriginalFilename());
                        ps.setBinaryStream(3, in);
                        ps.setLong(4, multipartFile.getSize());
                        ps.setString(5, parentPath.resolve(id).toString() + "/");
                        ps.setObject(6, FileType.FILE, Types.OTHER);
                    });
            logger.info(String.valueOf(result));

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Transactional(readOnly = true)
    public StoredFile findById(String downloadId) {
        return (StoredFile) jdbcTemplate.query(
                "SELECT id, name, path, type, content, size FROM file WHERE id = ?"
                , (rs) -> {
                    rs.next();
                    return new StoredFile(UUID.fromString(rs.getString("id"))
                            , rs.getString("name")
                            , Path.of(rs.getString("path"))
                            , FileType.valueOf(rs.getString("type"))
                            , rs.getBinaryStream("content")
                            , rs.getLong("size"));
                }, downloadId);
    }

    @Transactional(readOnly = true)
    public Path findPathById(String id) {
        return jdbcTemplate.query(
                "SELECT path FROM file WHERE id = ?"
                , rs -> {
                    rs.next();
                    return Path.of(rs.getString("path"));
                }
                , id);
    }

    @Transactional(rollbackFor = Exception.class)
    public int delete(String fileId) {
        FileType type = jdbcTemplate.queryForObject("SELECT type FROM file WHERE id = ?", FileType.class, fileId);
        if (type == FileType.FILE) {
            return jdbcTemplate.update("DELETE FROM file WHERE id = ?", fileId);
        } else {
            Path path = findPathById(fileId);
            return jdbcTemplate.update("DELETE FROM file WHERE path LIKE ? || '%'", path.toString() + "/");
        }
    }

    @Transactional(readOnly = true)
    public Directories findAncestors(String id) {

        // パスを取得
        Path path = findPathById(id);

        if (path.getParent().equals(ROOT_PATH)) {
            return new Directories(Collections.emptyList());
        }

        // パラメータとプレースホルダーを作成
        var params = new ArrayList<String>();
        var placeholders = new ArrayList<String>();
        for (Iterator<Path> itr = path.getParent().iterator(); itr.hasNext(); ) {
            Path p = itr.next();
            params.add(p.toString());
            placeholders.add("?");
        }

        // SQLを作成
        var sql = new StringBuilder("SELECT id, name, path, type, size FROM file WHERE id IN (");
        String in = StringUtils.join(placeholders, ", ");
        sql.append(in).append(")");
        sql.append("ORDER BY char_length(path)");

        // SQL実行
        List<StoredFile> ancestors = jdbcTemplate.query(sql.toString(),
                (ps) -> {
                    for (int i = 0; i < params.size(); i++) {
                        ps.setString(i + 1, params.get(i));
                    }
                },
                (rs, rowNum) -> {
                    return new StoredFile(
                            UUID.fromString(rs.getString("id")),
                            rs.getString("name"),
                            Path.of(rs.getString("path")),
                            FileType.valueOf(rs.getString("type")),
                            rs.getLong("size")
                    );
                });

        return new Directories(ancestors);
    }
}

