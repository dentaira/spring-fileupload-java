package fileupload.web.file.infra;

import fileupload.web.file.FileRepository;
import fileupload.web.file.FileType;
import fileupload.web.file.Owner;
import fileupload.web.file.StoredFile;
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
    public List<StoredFile> searchRoot(Owner owner) {
        return jdbcTemplate.query(
                "SELECT id, name, path, type, size " +
                        "FROM file " +
                        "WHERE cast(id as text) = replace(path, '/', '') " +
                        "AND id IN(SELECT file_id FROM file_ownership WHERE owned_at = ?)",
                ps -> ps.setObject(1, owner.getId()),
                (rs, rowNum) -> {
                    return new StoredFile(UUID.fromString(rs.getString("id"))
                            , rs.getString("name")
                            , Path.of(rs.getString("path"))
                            , FileType.valueOf(rs.getString("type"))
                            , rs.getLong("size"));
                });
    }

    @Override
    public List<StoredFile> search(String parentDirId, Owner owner) {

        // TODO levelを使わない方法にリファクタリングする。
        int level = getLevel(parentDirId);

        return jdbcTemplate.query(
                "SELECT id, name, path, type, size FROM file " +
                        "WHERE path LIKE (SELECT path FROM file WHERE id = ?) || '_%' " +
                        "AND LENGTH(path) - LENGTH(REPLACE(path, '/', '')) = (? + 1) " +
                        "AND id IN(SELECT file_id FROM file_ownership WHERE owned_at = ?)",
                ps -> {
                    ps.setString(1, parentDirId);
                    ps.setInt(2, level);
                    ps.setObject(3, owner.getId());
                },
                (rs, rowNum) -> {
                    return new StoredFile(UUID.fromString(rs.getString("id"))
                            , rs.getString("name")
                            , Path.of(rs.getString("path"))
                            , FileType.valueOf(rs.getString("type"))
                            , rs.getLong("size"));
                });
    }

    private int getLevel(String parentDirId) {
        return jdbcTemplate.queryForObject(
                "SELECT LENGTH(path) - LENGTH(REPLACE(path, '/', '')) FROM file WHERE id = ?",
                new Object[]{parentDirId},
                int.class);
    }

    @Override
    public StoredFile findById(String id, Owner owner) {
        return jdbcTemplate.query(
                "SELECT id, name, path, type, content, size FROM file WHERE LOWER(id) = LOWER(?) " +
                        "AND id IN(SELECT file_id FROM file_ownership WHERE owned_at = ?)",
                rs -> {
                    if (rs.next()) {
                        return new StoredFile(
                                UUID.fromString(rs.getString("id")),
                                rs.getString("name"),
                                Path.of(rs.getString("path")),
                                FileType.valueOf(rs.getString("type")),
                                rs.getBinaryStream("content"),
                                rs.getLong("size"));
                    } else {
                        return null;
                    }
                },
                id, owner.getId());
    }

    @Override
    public void save(StoredFile file) {
        jdbcTemplate.update(
                "INSERT INTO file(id, name, content, size, path, type) VALUES(?, ?, ?, ?, ?, ?)",
                ps -> {
                    ps.setString(1, file.getId().toString());
                    ps.setString(2, file.getName());
                    ps.setBinaryStream(3, file.getContent());
                    ps.setLong(4, file.getSize());
                    ps.setString(5, file.getPath().toString() + "/");
                    ps.setObject(6, file.getType(), Types.OTHER);
                }
        );
    }

    @Override
    public void delete(StoredFile file) {
        if (file.getType() == FileType.FILE) {
            jdbcTemplate.update("DELETE FROM file WHERE LOWER(id) = LOWER(?)", file.getId().toString());
        } else {
            jdbcTemplate.update("DELETE FROM file WHERE path LIKE ? || '%'", file.getPath().toString() + "/");
        }
    }
}
