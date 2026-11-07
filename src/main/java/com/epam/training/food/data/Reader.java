package com.epam.training.food.data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public abstract class Reader<T> {

    public abstract List<T> read(String pathOfFile);

    protected List<String> readFromFile(String pathOfFile) {
        List<String> lines;
        var path = Paths.get(pathOfFile).normalize().toAbsolutePath();
        try {
            lines = Files.readAllLines(path);
        } catch (IOException e) {
            throw new RuntimeException("IOException happened while reading file: " + pathOfFile, e);
        }
        if (lines.isEmpty()) {
            throw new IllegalArgumentException("The file is empty!");
        }
        return lines;
    }

    protected String[] splitLineByComma(String line) {
        return line.split(",");
    }
}
