package pt.ulisboa.tecnico.socialsoftware.tutor.impexp;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Controller
public class ImportExportController {
    private static Logger logger = LoggerFactory.getLogger(ImportExportController.class);

    @Autowired
    private ImpExpService impExpService;

    @Value("${export.dir}")
    private String exportDir;

    @GetMapping(value = "/admin/export")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void exportAll(HttpServletResponse response) throws IOException {
        logger.debug("exportAll");

        String filename = impExpService.exportAll();

        File directory = new File(exportDir);
        File file = new File(directory, filename);
        response.setHeader("Content-Disposition", "attachment; filename=" + filename);
        response.setHeader("Content-Type", "application/zip");
        InputStream is = new FileInputStream(file);
        FileCopyUtils.copy(IOUtils.toByteArray(is), response.getOutputStream());
        response.flushBuffer();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/admin/import")
    public void importAll() {
        logger.debug("importAll");

        impExpService.importAll();
    }
}
