/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package goehler.privat.filesapp.controller;

import goehler.privat.filesapp.gui.Window;
import goehler.privat.filesapp.service.GuiService;
import java.io.File;
import java.util.List;

/**
 *
 * @author Heiko
 */
public class GuiController {

    Window window;
    
    GuiService gs;
    
    FileController fc;

    public GuiController(Window window) {
        this.window = window;
        gs = new GuiService(this, window);
    }
    
    public GuiService getGs() {
        return gs;
    }

    public void setGs(GuiService gs) {
        this.gs = gs;
    }
    
    public void addFileExtensionCheckboxes(List<String> extensions) {
        gs.addFileExtensionCheckboxes(extensions);
    }
    
    public void addFilesInformations(List<String[]> filesInformations) {
        gs.addFilesInformations(filesInformations);
    }
    
    public void addFilesToSort(List<File> filesToSort) {
        gs.addFilesToSort(filesToSort);
    }

    public void addFilesToDelete(List<File> filesToDelete) {
        gs.addFilesToDelete(filesToDelete);
    }

    public void setFc(FileController fc) {
        this.fc = fc;
        gs.setFc(fc);
    }
}
