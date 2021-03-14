package fileupload.web.file.web;

import fileupload.web.file.DirectoryService;
import fileupload.web.file.FileService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.nio.file.Path;
import java.util.Optional;

@Controller
@RequestMapping("directory")
public class DirectoryController {

    FileService fileService;

    DirectoryService directoryService;

    public DirectoryController(FileService fileService, DirectoryService directoryService) {
        this.fileService = fileService;
        this.directoryService = directoryService;
    }

    @PostMapping({"create", "create/{currentDir}"})
    public String create(@RequestParam("createDirName") String createDirName, @PathVariable Optional<String> currentDir) {
        Path parentPath = null;
        if (currentDir.isPresent()) {
            parentPath = fileService.findPathById(currentDir.get());
        } else {
            parentPath = Path.of("/");
        }
        directoryService.create(createDirName, parentPath);
        return "redirect:/file/home/" + currentDir.orElse("");
    }
}
