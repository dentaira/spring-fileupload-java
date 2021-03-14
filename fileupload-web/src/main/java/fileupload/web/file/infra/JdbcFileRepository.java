package fileupload.web.file.infra;

import fileupload.web.file.FileRepository;
import fileupload.web.file.StoredFile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Types;

@Repository
public class JdbcFileRepository implements FileRepository {

    private JdbcTemplate jdbcTemplate;

    public JdbcFileRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(StoredFile file) {
        jdbcTemplate.update(
                "INSERT INTO file(id, name, content, size, path, type) VALUES(?, ?, ?, ?, ?, ?)"
                , (ps) -> {
                    ps.setString(1, file.getId().toString());
                    ps.setString(2, file.getName());
                    ps.setBinaryStream(3, file.getContent());
                    ps.setLong(4, file.getSize());
                    ps.setString(5, file.getPath().toString() + "/");
                    ps.setObject(6, file.getType(), Types.OTHER);
                }
        );
    }
}
