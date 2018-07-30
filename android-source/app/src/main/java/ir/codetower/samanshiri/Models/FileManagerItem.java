package ir.codetower.samanshiri.Models;

public class FileManagerItem {
    private String fileUrl;
    private String fileName;
    private String fileIcon;
    private boolean isDirectory;
    private boolean isBack;
    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileIcon() {
        return fileIcon;
    }

    public void setFileIcon(String fileIcon) {
        this.fileIcon = fileIcon;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setDirectory(boolean directory) {
        isDirectory = directory;
    }

    public boolean isBack() {
        return isBack;
    }

    public void setBack(boolean back) {
        isBack = back;
    }

    @Override
    public String toString() {
        return "FileManagerItem{" +
                "fileUrl='" + fileUrl + '\'' +
                ", fileName='" + fileName + '\'' +
                ", fileIcon='" + fileIcon + '\'' +
                ", isDirectory=" + isDirectory +
                '}';
    }
}
