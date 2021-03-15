package fileupload.web.file;

import fileupload.web.user.UserAccount;

import java.util.List;

public interface FileRepository {

    public List<StoredFile> searchRoot(UserAccount user);

    public List<StoredFile> search(String parentDirId, UserAccount user);

    public StoredFile findById(String id, UserAccount user);

    public void save(StoredFile file);
}
