package fileupload.web.file.infra;

import fileupload.web.file.FileRepository;
import fileupload.web.file.FileType;
import fileupload.web.file.StoredFile;
import fileupload.web.user.UserAccount;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.nio.file.Path;
import java.sql.Types;
import java.util.List;
import java.util.UUID;

@Repository
public class JdbcFileRepository implements FileRepository {

    private JdbcTemplate jdbcTemplate;

    public JdbcFileRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<StoredFile> searchRoot(UserAccount user) {
        return jdbcTemplate.query(
                "SELECT id, name, path, type, size " +
                        "FROM file " +
                        "WHERE cast(id as text) = replace(path, '/', '') " +
                        "AND id IN(SELECT file_id FROM file_ownership WHERE owned_at = ?)",
                (pss) -> pss.setObject(1, user.getId()),
                (rs, rowNum) -> {
                    return new StoredFile(UUID.fromString(rs.getString("id"))
                            , rs.getString("name")
                            , Path.of(rs.getString("path"))
                            , FileType.valueOf(rs.getString("type"))
                            , rs.getLong("size"));
                });
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
