/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package goehler.privat.filesapp.controller;

import goehler.privat.filesapp.service.FileService;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author Heiko
 */
public class FileController {

    GuiController gc;

    FileService fs;

    File startPath;

    public FileController(GuiController guiController, File startPath) {
        this.gc = guiController;
        this.startPath = startPath;
        fs = new FileService(this);
    }

    public GuiController getGc() {
        return gc;
    }

    public void setGc(GuiController gc) {
        this.gc = gc;
    }

    public FileService getFs() {
        return fs;
    }

    public void setFs(FileService fs) {
        this.fs = fs;
    }

    public void moveFilesOutOfFolders() {
        fs.moveFilesOutOfFolders(startPath);
    }

    public void scanFileExtensions() {
        fs.scanFileExtensions(startPath);
        gc.addFileExtensionCheckboxes(fs.getExtensions());
    }
    
    public void getFilesInformations() {
        fs.scanFilesInformations(startPath);
        gc.addFilesInformations(fs.getFilesInformations());
    }

    public void scanForFilesToSort() {
        fs.scanForFilesToSort(startPath);
        fs.sortFilesByDate();
        gc.addFilesToSort(fs.getFilesToSort());
    }
    
    public void scanForFilesToDelete() {
        fs.scanForFilesToDelete(startPath);
        fs.sortFilesBySize();
        gc.addFilesToDelete(fs.getFilesToDelete());
    }
    
    public double getDirSize(File dir) {
        return fs.getDirSize(dir);
    }

    public File getStartPath() {
        return startPath;
    }
    
    public void deleteDirectory(File file) {
        fs.deleteDirectory(file);
    }
}
