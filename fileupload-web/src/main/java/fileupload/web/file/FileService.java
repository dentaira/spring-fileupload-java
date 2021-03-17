package fileupload.web.file;

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
import java.util.*;

@Service
public class FileService {

    public static final Path ROOT_PATH = Path.of("/");

    private Logger logger = LoggerFactory.getLogger(getClass());

    final FileRepository fileRepository;

    final FileOwnershipRepository fileOwnershipRepository;

    // TODO Repositoryを作成する
    final JdbcTemplate jdbcTemplate;

    public FileService(FileRepository fileRepository, FileOwnershipRepository fileOwnershipRepository, JdbcTemplate jdbcTemplate) {
        this.fileRepository = fileRepository;
        this.fileOwnershipRepository = fileOwnershipRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(readOnly = true)
    public List<StoredFile> searchRoot(Owner owner) {
        return fileRepository.searchRoot(owner);
    }

    @Transactional(readOnly = true)
    public List<StoredFile> search(String parentId, Owner owner) {
        return fileRepository.search(parentId, owner);
    }

    @Transactional(rollbackFor = Exception.class)
    public void register(MultipartFile multipartFile, Path parentPath, Owner owner) {
        // TODO MultipartFileに依存しないようにする

        try (InputStream in = multipartFile.getInputStream()) {
            var fileId = UUID.randomUUID();
            var file = new StoredFile(
                    fileId,
                    multipartFile.getOriginalFilename(),
                    parentPath.resolve(fileId.toString()),
                    FileType.FILE,
                    in,
                    multipartFile.getSize()
            );

            fileRepository.save(file);

            fileOwnershipRepository.create(fileId, owner.getId(), "READ_AND_WRITE");

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Transactional(readOnly = true)
    public StoredFile findById(String fileId, Owner owner) {
        return fileRepository.findById(fileId, owner);
    }

    @Transactional(readOnly = true)
    public Path findPathById(String fileId) {
        return jdbcTemplate.query(
                "SELECT path FROM file WHERE id = ?",
                rs -> {
                    rs.next();
                    return Path.of(rs.getString("path"));
                },
                fileId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void createDirectory(String name, Path parentPath, Owner owner) {
        var fileId = UUID.randomUUID();
        var file = new StoredFile(
                fileId,
                name,
                parentPath.resolve(fileId.toString()),
                FileType.DIRECTORY,
                null,
                0L
        );

        fileRepository.save(file);

        fileOwnershipRepository.create(fileId, owner.getId(), "READ_AND_WRITE");
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
    public List<StoredFile> findAncestors(String fileId) {

        // パスを取得
        Path path = findPathById(fileId);

        if (path.getParent().equals(ROOT_PATH)) {
            return Collections.emptyList();
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
        return jdbcTemplate.query(sql.toString(),
                ps -> {
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
    }
}

