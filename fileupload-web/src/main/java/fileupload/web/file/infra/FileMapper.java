package fileupload.web.file.infra;

import fileupload.web.file.Owner;
import fileupload.web.file.StoredFile;
import fileupload.web.infra.mybatis.DataSizeTypeHandler;
import fileupload.web.infra.mybatis.UUIDCharTypeHandler;
import fileupload.web.infra.mybatis.UUIDTypeHandler;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.LongTypeHandler;

import java.io.InputStream;
import java.util.UUID;

@Mapper
public interface FileMapper {

    @Results({
            @Result(property = "id", column = "id", id = true, typeHandler = UUIDCharTypeHandler.class),
            @Result(property = "name", column = "name"),
            @Result(property = "path", column = "path"),
            @Result(property = "type", column = "type"),
            @Result(property = "content", column = "id",
                    one = @One(select = "findContentById", fetchType = FetchType.LAZY)),
            @Result(property = "size", column = "size", typeHandler = DataSizeTypeHandler.class)})
    @Select({"SELECT id, name, path, type, size",
            "FROM file WHERE LOWER(id) = LOWER(#{id})",
            "AND id IN(SELECT file_id FROM file_ownership",
            "WHERE owned_at = #{owner.id})"})
    public StoredFile findById(@Param("id") String id, @Param("owner") Owner owner);

    @Select("SELECT content FROM file WHERE id = #{id}")
    public InputStream findContentById(@Param("id") String id);
}
