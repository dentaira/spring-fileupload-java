package fileupload.web.app.file.controller;

import fileupload.web.domain.file.service.DirectoryService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("directory")
public class DirectoryController {

    DirectoryService directoryService;

    public DirectoryController(DirectoryService directoryService) {
        this.directoryService = directoryService;
    }

    @PostMapping("create")
    public String create(@RequestParam("createDirName") String createDirName) {
        directoryService.create(createDirName);
        return "redirect:/file/form";
    }
}
