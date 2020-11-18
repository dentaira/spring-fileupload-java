package fileupload.web.domain.file.service;

import fileupload.web.domain.file.model.FileType;
import fileupload.web.domain.file.model.StoredFile;
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
import java.util.List;
import java.util.UUID;

@Service
public class FileService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    // TODO Repositoryを作成する
    final JdbcTemplate jdbcTemplate;

    public FileService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(readOnly = true)
    public List<StoredFile> search() {
        return jdbcTemplate.query("SELECT id, name, path, type, size FROM FILE WHERE cast(id as text) = replace(path, '/', '')"
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
        return jdbcTemplate.query("SELECT id, name, path, type, size FROM FILE WHERE path like "
                        + "(SELECT path FROM FILE WHERE id = ?) || '%'"
                , (ps) -> ps.setInt(1, Integer.parseInt(dirId))
                , (rs, rowNum) -> {
                    return new StoredFile(UUID.fromString(rs.getString("id"))
                            , rs.getString("name")
                            , Path.of(rs.getString("path"))
                            , FileType.valueOf(rs.getString("type"))
                            , rs.getLong("size"));
                });
    }

    @Transactional(rollbackFor = Exception.class)
    public void register(MultipartFile multipartFile, String parent) {
        // TODO MultipartFileに依存しないようにする
        try (InputStream in = multipartFile.getInputStream()) {
            // TODO IDをパスに使用するためあらかじめ採番する必要がある
            var id = UUID.randomUUID().toString();
            int result = jdbcTemplate.update("INSERT INTO FILE(id, name, content, size, path, type) VALUES(?, ?, ?, ?, ?, ?)"
                    , (ps) -> {
                        ps.setString(1, id);
                        ps.setString(2, multipartFile.getOriginalFilename());
                        ps.setBinaryStream(3, in);
                        ps.setLong(4, multipartFile.getSize());
                        ps.setString(5, parent + id);
                        ps.setObject(6, FileType.FILE, Types.OTHER);
                    });
            logger.info(String.valueOf(result));

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Transactional(readOnly = true)
    public StoredFile findById(int downloadId) {
        return (StoredFile) jdbcTemplate.query("SELECT id, name, path, type, content, size FROM FILE WHERE id = ?", (rs) -> {
            rs.next();
            return new StoredFile(UUID.fromString(rs.getString("id"))
                    , rs.getString("name")
                    , Path.of(rs.getString("path"))
                    , FileType.valueOf(rs.getString("type"))
                    , rs.getBinaryStream("content")
                    , rs.getLong("size"));
        }, downloadId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(int fileId) {
        jdbcTemplate.update("DELETE FROM FILE WHERE id = ?", fileId);
    }
}

