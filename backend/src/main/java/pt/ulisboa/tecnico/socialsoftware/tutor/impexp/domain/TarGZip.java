package pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;

public class TarGZip {
    //this class will create a tar.gz zip of source_folder
    private String sourceFolder;

    public TarGZip(String sourceFolder) {
        this.sourceFolder = sourceFolder;
    }

    public void createTarFile() {
        TarArchiveOutputStream tarOs = null;
        try {
            File source = new File(sourceFolder);
            // Using input name to create output name
            FileOutputStream fos = new FileOutputStream(source.getAbsolutePath().concat(".tar.gz"));
            GZIPOutputStream gos = new GZIPOutputStream(new BufferedOutputStream(fos));
            tarOs = new TarArchiveOutputStream(gos);
            addFilesToTarGZ(sourceFolder, "", tarOs);
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            try {
                tarOs.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void addFilesToTarGZ(String filePath, String parent, TarArchiveOutputStream tarArchive) throws IOException {
        File file = new File(filePath);
        // Create entry name relative to parent file path
        String entryName = parent + file.getName();
        // add tar ArchiveEntry
        tarArchive.putArchiveEntry(new TarArchiveEntry(file, entryName));
        if (file.isFile()) {
            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis);
            // Write file content to archive
            IOUtils.copy(bis, tarArchive);
            tarArchive.closeArchiveEntry();
            bis.close();
        } else if (file.isDirectory()) {
            // no need to copy any content since it is
            // a directory, just close the outputstream
            tarArchive.closeArchiveEntry();
            // for files in the directories
            for (File f : file.listFiles()) {
                // recursively call the method for all the subdirectories
                addFilesToTarGZ(f.getAbsolutePath(), entryName+File.separator, tarArchive);
            }
        }
    }
}

