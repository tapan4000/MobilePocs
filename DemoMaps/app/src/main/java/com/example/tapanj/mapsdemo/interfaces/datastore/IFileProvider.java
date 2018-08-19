package com.example.tapanj.mapsdemo.interfaces.datastore;

public interface IFileProvider {
    boolean writeToFile(String message);
    boolean truncateFile();
}
