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

@Service
public class FileService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    // TODO Repositoryを作成する
    final JdbcTemplate jdbcTemplate;

    public FileService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Deprecated
    @Transactional(readOnly = true)
    public List<StoredFile> search() {
        return jdbcTemplate.query("SELECT id, name, parent, type, size FROM FILE", (rs, rowNum) -> {
            return new StoredFile(rs.getInt("id")
                    , rs.getString("name")
                    , Path.of(rs.getString("parent"))
                    , FileType.valueOf(rs.getString("type"))
                    , rs.getLong("size"));
        });
    }

    @Transactional(readOnly = true)
    public List<StoredFile> search(String fileId) {
        return jdbcTemplate.query("SELECT id, name, parent, type, size FROM FILE WHERE parent like '%' || '/' || ?"
                , new String[]{fileId}
                , (rs, rowNum) -> {
                    return new StoredFile(rs.getInt("id")
                            , rs.getString("name")
                            , Path.of(rs.getString("parent"))
                            , FileType.valueOf(rs.getString("type"))
                            , rs.getLong("size"));
                });
    }

    @Transactional(rollbackFor = Exception.class)
    public void register(MultipartFile multipartFile, String parent) {
        // TODO MultipartFileに依存しないようにする
        try (InputStream in = multipartFile.getInputStream()) {
            int result = jdbcTemplate.update("INSERT INTO FILE(name, content, size, parent, type) VALUES(?, ?, ?, ?, ?)", (ps) -> {
                ps.setString(1, multipartFile.getOriginalFilename());
                ps.setBinaryStream(2, in);
                ps.setLong(3, multipartFile.getSize());
                ps.setString(4, "/" + parent);
                ps.setObject(5, FileType.FILE, Types.OTHER);
            });
            logger.info(String.valueOf(result));

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Transactional(readOnly = true)
    public StoredFile findById(int downloadId) {
        return (StoredFile) jdbcTemplate.query("SELECT id, name, parent, type, content, size FROM FILE WHERE id = ?", (rs) -> {
            rs.next();
            return new StoredFile(rs.getInt("id")
                    , rs.getString("name")
                    , Path.of(rs.getString("parent"))
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

