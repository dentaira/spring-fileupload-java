package fileupload.web.app.file;

import fileupload.web.domain.file.Directories;
import fileupload.web.domain.file.StoredFile;
import fileupload.web.domain.file.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Path;
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
        List<StoredFile> files = fileService.searchRoot();
        form.setStoredFiles(files);
        model.addAttribute("ancestors", Directories.empty());
        return "file-list";
    }

    @GetMapping("home/{fileId}")
    public String home(UploadForm form, @PathVariable String fileId, Model model) {
        List<StoredFile> files = fileService.search(fileId);
        form.setStoredFiles(files);

        StoredFile currentDir = fileService.findById(fileId);
        model.addAttribute("currentDir", currentDir);

        Directories ancestors = fileService.findAncestors(fileId);
        model.addAttribute("ancestors", ancestors);

        return "file-list";
    }

    @GetMapping("download/{fileId}")
    public String download(@PathVariable String fileId, Model model) {
        StoredFile downloadFile = fileService.findById(fileId);
        model.addAttribute("downloadFile", downloadFile);
        return "storedFileDownloadView";
    }

    @PostMapping({"upload", "upload/{currentDir}"})
    public String upload(UploadForm form, @PathVariable Optional<String> currentDir, RedirectAttributes redirectAttributes) {
        Path parentPath = null;
        if (currentDir.isPresent()) {
            parentPath = fileService.findPathById(currentDir.get());
        } else {
            parentPath = Path.of("/");
        }
        fileService.register(form.getUploadFile(), parentPath);
        redirectAttributes.addFlashAttribute("message", "アップロードが完了しました。");
        return "redirect:/file/home/" + currentDir.orElse("");
    }

    @PostMapping({"delete/{fileId}", "delete/{currentDir}/{fileId}"})
    public String delete(@PathVariable String fileId, @PathVariable Optional<String> currentDir, RedirectAttributes redirectAttributes) {
        fileService.delete(fileId);
        redirectAttributes.addFlashAttribute("message", "削除しました。");
        return "redirect:/file/home/" + currentDir.orElse("");
    }
}
