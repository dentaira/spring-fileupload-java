package fileupload.web.app.file.controller;

import fileupload.web.app.file.form.UploadForm;
import fileupload.web.domain.file.model.StoredFile;
import fileupload.web.domain.file.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("file")
public class UploadController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    final FileService fileService;

    public UploadController(FileService fileService) {
        this.fileService = fileService;
    }

    @ModelAttribute
    public UploadForm setupForm() {
        return new UploadForm();
    }

    @GetMapping("form")
    public String form(UploadForm form) {
        List<StoredFile> files = fileService.search();
        form.setStoredFiles(files);
        return "upload";
    }

    @GetMapping("download/{fileId}")
    public String download(@PathVariable int fileId, Model model) {
        StoredFile downloadFile = fileService.findById(fileId);
        model.addAttribute("downloadFile", downloadFile);
        return "storedFileDownloadView";
    }

    @PostMapping("upload")
    public String upload(UploadForm form, RedirectAttributes redirectAttributes) {
        fileService.register(form.getUploadFile());
        redirectAttributes.addFlashAttribute("message", "アップロードが完了しました。");
        return "redirect:form";
    }
}
