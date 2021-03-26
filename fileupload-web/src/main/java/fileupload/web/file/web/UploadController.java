package fileupload.web.file.web;

import fileupload.web.file.FileContent;
import fileupload.web.file.FileService;
import fileupload.web.file.Owner;
import fileupload.web.file.StoredFile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("file")
public class UploadController {

    final FileService fileService;

    public UploadController(FileService fileService) {
        this.fileService = fileService;
    }

    @ModelAttribute
    public UploadForm setupForm() {
        return new UploadForm();
    }

    @GetMapping("home")
    public String home(UploadForm form, Model model, Owner owner) {
        List<StoredFile> files = fileService.searchRoot(owner);
        form.setStoredFiles(files);
        model.addAttribute("ancestors", Collections.emptyList());
        return "file-list";
    }

    @GetMapping("home/{fileId}")
    public String home(UploadForm form, @PathVariable String fileId, Owner owner, Model model) {
        List<StoredFile> files = fileService.search(fileId, owner);
        form.setStoredFiles(files);

        StoredFile currentDir = fileService.findById(fileId, owner);
        model.addAttribute("currentDir", currentDir);

        List<StoredFile> ancestors = fileService.findAncestors(fileId, owner);
        model.addAttribute("ancestors", ancestors);

        return "file-list";
    }

    @GetMapping("download/{fileId}")
    public String download(@PathVariable String fileId, Model model, Owner owner) {
        FileContent fileContent = fileService.findContentById(fileId, owner);
        model.addAttribute("downloadFile", fileContent);
        return "contentDownloadView";
    }

    @PostMapping({"upload", "upload/{currentDir}"})
    public String upload(UploadForm form, @PathVariable Optional<String> currentDir,
                         Owner owner, RedirectAttributes redirectAttributes) {
        Path parentPath = null;
        if (currentDir.isPresent()) {
            parentPath = fileService.findById(currentDir.get(), owner).getPath();
        } else {
            parentPath = Path.of("/");
        }
        fileService.register(form.getUploadFile(), parentPath, owner);
        redirectAttributes.addFlashAttribute("message", "アップロードが完了しました。");
        return "redirect:/file/home/" + currentDir.orElse("");
    }

    @PostMapping({"create/directory", "create/directory/{currentDir}"})
    public String createDirectory(@RequestParam("createDirName") String createDirName,
                                  @PathVariable Optional<String> currentDir, Owner owner) {
        Path parentPath = null;
        if (currentDir.isPresent()) {
            parentPath = fileService.findById(currentDir.get(), owner).getPath();
        } else {
            parentPath = Path.of("/");
        }
        fileService.createDirectory(createDirName, parentPath, owner);
        return "redirect:/file/home/" + currentDir.orElse("");
    }

    @PostMapping({"delete/{fileId}", "delete/{currentDir}/{fileId}"})
    public String delete(@PathVariable String fileId, Owner owner, @PathVariable Optional<String> currentDir,
                         RedirectAttributes redirectAttributes) {
        fileService.delete(fileId, owner);
        redirectAttributes.addFlashAttribute("message", "削除しました。");
        return "redirect:/file/home/" + currentDir.orElse("");
    }
}
