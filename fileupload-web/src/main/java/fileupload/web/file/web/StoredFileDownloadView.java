package fileupload.web.file.web;

import fileupload.web.file.StoredFile;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

@Component
public class StoredFileDownloadView extends AbstractView {
    @Override
    protected void renderMergedOutputModel(Map<String, Object> map, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {


        StoredFile downloadFile = (StoredFile) map.get("downloadFile");

        // TODO 半角スペースが+に置換されてしまう
        String filename = URLEncoder.encode(downloadFile.getName(), StandardCharsets.UTF_8);

        // TODO 日本語含めるときはダブルクォーテーションが必要なはずでは？
        httpServletResponse.setHeader("Content-Disposition","attachment;filename=" + filename);

        try (InputStream in = downloadFile.getContent()) {

            Objects.requireNonNull(in);

            var out = new BufferedOutputStream(httpServletResponse.getOutputStream());
            in.transferTo(out);
            out.flush();
        }
    }
}
