package etu1987.framework;

public class FileUploader {
    String name;
    byte[] bytes;
    String paths;

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setPaths(String paths) {
        this.paths = paths;
    }
    public String getPaths() {
        return paths;
    }
}
