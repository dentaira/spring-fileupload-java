package fileupload.web.app.file.controller;

import fileupload.web.domain.file.service.DirectoryService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequestMapping("directory")
public class DirectoryController {

    DirectoryService directoryService;

    public DirectoryController(DirectoryService directoryService) {
        this.directoryService = directoryService;
    }

    @PostMapping({"create", "create/{currentDir}"})
    public String create(@RequestParam("createDirName") String createDirName, @PathVariable Optional<String> currentDir) {
        String parent = currentDir.orElse("");
        directoryService.create(createDirName, parent);
        return "redirect:/file/home/" + parent;
    }
}
