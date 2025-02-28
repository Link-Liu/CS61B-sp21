package byow.Core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class Save {
    String path;
    Save() {
        this.path = "byow/Core/history.txt";
    }
    public void write(String content) {
        try {
            Files.write(Paths.get(path), content.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String read() {
        List<String> lines = null;
        try {
            lines = Files.readAllLines(Paths.get(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String result = "";
        for (String line : lines) {
            result += line;
        }
        return result;
    }

    public void clean() {
        try {
            Files.write(Paths.get(path), "".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
