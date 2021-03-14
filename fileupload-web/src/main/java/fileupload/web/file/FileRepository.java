package fileupload.web.file;

import fileupload.web.user.UserAccount;

import java.util.List;

public interface FileRepository {

    public List<StoredFile> searchRoot(UserAccount user);

    public void save(StoredFile file);
}
