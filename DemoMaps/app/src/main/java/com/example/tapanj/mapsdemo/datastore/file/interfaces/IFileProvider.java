package com.example.tapanj.mapsdemo.datastore.file.interfaces;

public interface IFileProvider {
    boolean writeToFile(String message);
    boolean truncateFile();
}
