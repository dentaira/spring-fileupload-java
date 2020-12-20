package fileupload.web.domain.file.service;

import fileupload.web.domain.file.model.FileType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.sql.Types;
import java.util.UUID;

@Service
public class DirectoryService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final JdbcTemplate jdbcTemplate;

    public DirectoryService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(rollbackFor = Exception.class)
    public void create(String name, Path parentPath) {

        jdbcTemplate.update(
                "INSERT INTO FILE(id, name, size, path, type) VALUES(?, ?, ?, ?, ?)"
                , (ps) -> {
                    var id = UUID.randomUUID().toString();
                    ps.setString(1, id);
                    ps.setString(2, name);
                    ps.setLong(3, 0L);
                    ps.setString(4, parentPath.resolve(id).toString() + "/");
                    ps.setObject(5, FileType.DIRECTORY, Types.OTHER);
                });
    }
}
