/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package goehler.privat.filesapp.service;

import goehler.privat.filesapp.controller.FileController;
import java.io.File;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author Heiko
 */
public class FileService {

    FileController fc;

    List<String> extensions = new ArrayList<>();

    List<String> chosenExtensions = new ArrayList<>();

    List<File> filesToSort = new ArrayList<>();

    List<File> filesToBeDeleted = new ArrayList<>();

    List<File> filesToMove = new ArrayList<>();

    List<File> filesToDelete = new ArrayList<>();

    List<File> directoriesToDelete = new ArrayList<>();

    List<String[]> filesInformations = new ArrayList<>();

    public FileService(FileController fc) {
        this.fc = fc;
    }

    public List<String> getExtensions() {
        return extensions;
    }

    public void setExtensions(List<String> extensions) {
        this.extensions = extensions;
    }

    public List<File> getFilesToSort() {
        return filesToSort;
    }

    public void setFilesToSort(List<File> filesToSort) {
        this.filesToSort = filesToSort;
    }

    public List<String[]> getFilesInformations() {
        return filesInformations;
    }

    public void setFilesInformations(List<String[]> filesInformations) {
        this.filesInformations = filesInformations;
    }

    public List<File> getFilesToDelete() {
        return filesToDelete;
    }

    public void setFilesToDelete(List<File> filesToDelete) {
        this.filesToDelete = filesToDelete;
    }

    public void moveFilesOutOfFolders(File startPath) {
        chosenExtensions.clear();
        for(JCheckBox cb : fc.getGc().getGs().getFileExtensionCheckboxes()) {
            if(cb.isSelected()) {
                chosenExtensions.add(cb.getText());
            }
        }
        filesToMove.clear();
        filesToBeDeleted.clear();
        scanFiles(startPath);
        moveFiles(startPath);
        removeFiles();
        fc.scanFileExtensions();
        JOptionPane.showMessageDialog(null, "Dateien erfolgreich bewegt");
    }

    private void scanFiles(File path) {
        File[] fileList = path.listFiles();
        for(File file : fileList) {
            if(file.isDirectory()) {
                directoriesToDelete.add(file);
                scanFiles(file);
            } else if(checkChosenExtensions(file)) {
                filesToMove.add(file);
            } else {
                filesToBeDeleted.add(file);
            }
        }
    }

    private boolean checkChosenExtensions(File file) {
        boolean isMediaFile = false;
        for(String s : chosenExtensions) {
            if(FilenameUtils.getExtension(file.getAbsolutePath()).equals(s)) {
                isMediaFile = true;
            }
        }
        return isMediaFile;
    }

    private void moveFiles(File startPath) {
        for(File file : filesToMove) {
            if(file.getName().contains(".part")) {
                file.renameTo(new File(startPath.getAbsolutePath() + "\\" + file.getName().replaceAll(".part", "")));
            } else {
                file.renameTo(new File(startPath.getAbsolutePath() + "\\" + file.getName()));
            }
        }
    }

    private void removeFiles() {
        for(File file : filesToBeDeleted) {
            file.delete();
        }
        for(File file : directoriesToDelete) {
            file.delete();
        }
    }

    public void scanFileExtensions(File path) {
        extensions.clear();
        File[] fileList = path.listFiles();
        for(File file : fileList) {
            if(file.isDirectory()) {
                scanFileExtensionFolders(file);
            } else {
                if(!extensions.contains(FilenameUtils.getExtension(file.getName()))) {
                    extensions.add(FilenameUtils.getExtension(file.getName()));
                }
            }
        }
    }

    private void scanFileExtensionFolders(File folder) {
        File[] fileList = folder.listFiles();
        for(File file : fileList) {
            if(file.isDirectory()) {
                scanFileExtensionFolders(file);
            } else {
                if(!extensions.contains(FilenameUtils.getExtension(file.getName()))) {
                    extensions.add(FilenameUtils.getExtension(file.getName()));
                }
            }
        }
    }

    public void scanFilesInformations(File path) {
        File[] fileList = path.listFiles();
        String[] filesInInitialPath = {"Files in initial path", String.valueOf(fileList.length), getFileBytes(path)};
        String[] allFiles = {"Files in total", String.valueOf(countAllFiles(path)), getFileBytes(path)};
        filesInformations.add(filesInInitialPath);
        filesInformations.add(allFiles);
        for(String fileExtensionString : extensions) {
            filesInformations.add(countWeightFileType(path, fileExtensionString));
        }
    }

    private String[] countWeightFileType(File path, String fileExtension) {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.GERMANY);
        dfs.setDecimalSeparator(',');
        df.setDecimalFormatSymbols(dfs);
        List<File> files = findFilesOfType(path, fileExtension);
        double size = weightFileType(files);
        String fileSize = size <= 1024 ? df.format(size) + " B" : size <= 1048576 ? df.format(size / 1024) + " Kb" : df.format(size / 1024 / 1024) + " Mb";
        String[] fileType = {fileExtension, String.valueOf(files.size()), fileSize};
        return fileType;
    }

    private List<File> findFilesOfType(File path, String fileExtension) {
        List<File> files = new ArrayList<>();
        for(File file : path.listFiles()) {
            if(file.isDirectory()) {
                files.addAll(findFilesOfType(file, fileExtension));
            } else {
                if(FilenameUtils.getExtension(file.getName()).equals(fileExtension)) {
                    files.add(file);
                }
            }
        }
        return files;
    }

    private double weightFileType(List<File> files) {
        double size = 0.0;
        for(File file : files) {
            if(file.isDirectory()) {
                List<File> filesList = new ArrayList<>();
                for(File f : file.listFiles()) {
                    filesList.add(f);
                }
                size = size + weightFileType(filesList);
            } else {
                size = size + file.length();
            }
        }
        return size;
    }

    public void scanForFilesToSort(File path) {
        filesToSort = null;
        filesToSort = Arrays.asList(path.listFiles());
    }

    public void scanForFilesToDelete(File path) {
        filesToDelete = null;
        filesToDelete = Arrays.asList(path.listFiles());
    }

    private int countAllFiles(File path) {
        File[] fileList = path.listFiles();
        int fileCount = fileList.length;
        for(File file : fileList) {
            if(file.isDirectory()) {
                fileCount = fileCount + countAllFiles(file);
            }
        }
        return fileCount;
    }

    public double getDirSize(File dir) {
        double size = 0.0;
        for(File file : dir.listFiles()) {
            if(!file.isDirectory()) {
                size = size + file.length();
            } else {
                size = size + getDirSize(file);
            }
        }
        return size;
    }

    private String getFileBytes(File file) {
        double size = 0.0;
        if(file.isDirectory()) {
            size = getDirSize(file);
        } else {
            size = file.length();
        }
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.GERMANY);
        dfs.setDecimalSeparator(',');
        df.setDecimalFormatSymbols(dfs);
        return size <= 1024 ? df.format(size) + " B" : size <= 1048576 ? df.format(size / 1024) + " Kb" : df.format(size / 1024 / 1024) + " Mb";
    }

    public void sortFilesBySize() {
        filesToDelete.sort(new Comparator<File>() {
            @Override
            public int compare(File f1, File f2) {
                int i = (int) ((f2.isDirectory() ? getDirSize(f2) : f2.length()) - (int) (f1.isDirectory() ? getDirSize(f1) : f1.length()));
                return i;
            }
        });
    }

    public void sortFilesByDate() {
        filesToSort.sort(new Comparator<File>() {
            @Override
            public int compare(File f1, File f2) {
                if(f1.lastModified() >= f2.lastModified()) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
    }

    public void changeFileDate(String dragFileName, String dropFileName) {
        String startPath = fc.getStartPath().getAbsolutePath();
        File dragFile = new File(startPath + "\\" + dragFileName);
        File dropFile = new File(startPath + "\\" + dropFileName);
        dragFile.setLastModified(dropFile.lastModified() - 1000);
        fc.scanForFilesToSort();
    }

    public void deleteDirectory(File file) {
        for(File f : file.listFiles()) {
            if(f.isDirectory() && f.listFiles().length != 0) {
                deleteDirectory(f);
            }
            f.delete();
        }
    }
}
