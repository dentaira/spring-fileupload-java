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
import java.util.Optional;

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

    @GetMapping("home")
    public String home(UploadForm form, Model model) {
        List<StoredFile> files = fileService.search();
        form.setStoredFiles(files);
        model.addAttribute("currentDir", "");
        return "file-list";
    }

    @GetMapping("home/{fileId}")
    public String home(UploadForm form, @PathVariable(required = false) Optional<String> fileId, Model model) {
        // TODO Rootを検索する場合はSQLを分ける。深さで探す。
        String currentDir = fileId.orElse("");
        List<StoredFile> files = fileService.search(currentDir);
        form.setStoredFiles(files);
        model.addAttribute("currentDir", currentDir);
        return "file-list";
    }

    @GetMapping("download/{fileId}")
    public String download(@PathVariable int fileId, Model model) {
        StoredFile downloadFile = fileService.findById(fileId);
        model.addAttribute("downloadFile", downloadFile);
        return "storedFileDownloadView";
    }

    @PostMapping({"upload", "upload/{currentDir}"})
    public String upload(UploadForm form, @PathVariable Optional<String> currentDir, RedirectAttributes redirectAttributes) {
        String parent = currentDir.orElse("/");
        fileService.register(form.getUploadFile(), parent);
        redirectAttributes.addFlashAttribute("message", "アップロードが完了しました。");
        return "redirect:/file/home" + parent;
    }

    @PostMapping({"delete/{fileId}", "delete/{currentDir}/{fileId}"})
    public String delete(@PathVariable int fileId, @PathVariable Optional<String> currentDir, RedirectAttributes redirectAttributes) {
        // TODO フォルダを削除しても下位のファイルが削除されない
        fileService.delete(fileId);
        redirectAttributes.addFlashAttribute("message", "削除しました。");
        return "redirect:/file/home/" + currentDir.orElse("");
    }
}
