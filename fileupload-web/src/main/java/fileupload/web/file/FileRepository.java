package fileupload.web.file;

import java.util.List;

public interface FileRepository {

    public List<StoredFile> searchRoot(Owner owner);

    public List<StoredFile> search(String parentDirId, Owner owner);

    public StoredFile findById(String id, Owner owner);

    public FileContent findContent(StoredFile file);

    public List<StoredFile> searchForAncestors(StoredFile file);

    public void save(FileContent fileContent);

    public void delete(StoredFile file);
}
