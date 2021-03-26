package fileupload.web.file;

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
import java.util.List;
import java.util.UUID;

@Service
public class FileService {

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
                    multipartFile.getSize()
            );

            fileRepository.save(new FileContent(file, in));
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
    public FileContent findContentById(String fileId, Owner owner) {
        StoredFile file = findById(fileId, owner);
        return fileRepository.findContent(file);
    }

    /**
     * TODO findById に置き換える。
     */
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
                0L
        );

        fileRepository.save(new FileContent(file, null));

        fileOwnershipRepository.create(fileId, owner.getId(), "READ_AND_WRITE");
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(String fileId) {
        // TODO findById に置き換える。
        StoredFile file = jdbcTemplate.query(
                "SELECT id, name, path, type, size FROM file WHERE LOWER(id) = LOWER(?)",
                rs -> {
                    if (rs.next()) {
                        return new StoredFile(
                                UUID.fromString(rs.getString("id")),
                                rs.getString("name"),
                                Path.of(rs.getString("path")),
                                FileType.valueOf(rs.getString("type")),
                                rs.getLong("size"));
                    } else {
                        return null;
                    }
                },
                fileId);
        fileRepository.delete(file);
    }

    @Transactional(readOnly = true)
    public List<StoredFile> findAncestors(String fileId) {

        // パスを取得
        Path path = findPathById(fileId);

        // TODO id と path 以外は適当。
        var file = new StoredFile(
                UUID.fromString(fileId),
                "name",
                path,
                null,
                0L
        );
        return fileRepository.searchForAncestors(file);
    }
}

