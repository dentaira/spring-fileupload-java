package fileupload.web.app.file.form;

import fileupload.web.domain.file.StoredFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.List;

public class UploadForm implements Serializable {

    private MultipartFile uploadFile;

    private List<StoredFile> storedFiles;
    private int downloadId;

    public MultipartFile getUploadFile() {
        return uploadFile;
    }

    public void setUploadFile(MultipartFile uploadFile) {
        this.uploadFile = uploadFile;
    }

    public void setStoredFiles(List<StoredFile> storedFiles) {
        this.storedFiles = storedFiles;
    }

    public List<StoredFile> getStoredFiles() {
        return storedFiles;
    }

    public int getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(int downloadId) {
        this.downloadId = downloadId;
    }
}
