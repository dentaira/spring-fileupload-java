package fileupload.web.file;

import java.util.UUID;

public interface FileOwnershipRepository {

    public void create(UUID fileId, UUID ownedAt, String type);
}
