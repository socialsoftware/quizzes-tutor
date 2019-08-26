package pt.ulisboa.tecnico.socialsoftware.tutor.impexp.api;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.service.ImpExpService;
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.PropertiesManager;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Controller
public class ImportExportController {
    @Autowired
    private ImpExpService service;

    @GetMapping(value = "/admin/export")
    public void exportAll(HttpServletResponse response) throws IOException {
        String filename = service.exportAll();

        String exportDir = PropertiesManager.getProperties().getProperty("export.dir");
        File directory = new File(exportDir);
        File file = new File(directory, filename);
        response.setHeader("Content-Disposition", "attachment; filename=" + filename);
        response.setHeader("Content-Type", "application/zip");
        InputStream is = new FileInputStream(file);
        FileCopyUtils.copy(IOUtils.toByteArray(is), response.getOutputStream());
        response.flushBuffer();
    }
}
