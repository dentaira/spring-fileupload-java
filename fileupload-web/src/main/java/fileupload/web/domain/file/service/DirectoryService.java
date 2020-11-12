package fileupload.web.domain.file.service;

import fileupload.web.domain.file.model.FileType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Types;

@Service
public class DirectoryService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final JdbcTemplate jdbcTemplate;

    public DirectoryService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(rollbackFor = Exception.class)
    public void create(String name) {
        int result = jdbcTemplate.update("INSERT INTO FILE(name, size, parent, type) VALUES(?, ?, ?, ?)", (ps) -> {
            ps.setString(1, name);
            ps.setLong(2, 0L);
            ps.setString(3, "/");
            ps.setObject(4, FileType.DIRECTORY, Types.OTHER);
        });
    }
}
