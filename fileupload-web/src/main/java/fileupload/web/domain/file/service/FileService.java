package fileupload.web.domain.file.service;

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
import java.util.List;

@Service
public class FileService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    final JdbcTemplate jdbcTemplate;

    public FileService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<StoredFile> search() {
        return jdbcTemplate.query("SELECT id, name FROM FILE", (rs, rowNum) -> {
            return new StoredFile(rs.getInt("id"), rs.getString("name"));
        });
    }

    @Transactional(rollbackFor = Exception.class)
    public void register(MultipartFile multipartFile) {

        try (InputStream in = multipartFile.getInputStream()) {
            int result = jdbcTemplate.update("INSERT INTO FILE(name, content) VALUES(?, ?)", (ps) -> {
                ps.setString(1, multipartFile.getOriginalFilename());
                ps.setBinaryStream(2, in);
            });
            logger.info(String.valueOf(result));

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public StoredFile findById(int downloadId) {
        return (StoredFile) jdbcTemplate.query("SELECT id, name, content FROM FILE WHERE id = ?", (rs) -> {
            rs.next();
            return new StoredFile(rs.getInt("id"), rs.getString("name"), rs.getBinaryStream("content"));
        }, downloadId);
    }
}

