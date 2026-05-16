package manager;

import model.LibraryData;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import model.MusicScore;

public class LibraryStorage {

    private static final String LIBRARY_FOLDER =
            System.getProperty("user.home") + File.separator + "MusicScoreViewerLibrary";

    private static final String SCORES_FOLDER =
            LIBRARY_FOLDER + File.separator + "scores";

    private static final String LIBRARY_FILE =
            LIBRARY_FOLDER + File.separator + "library.json";

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public LibraryStorage() {
        createLibraryFolders();
    }

    private void createLibraryFolders() {
        new File(LIBRARY_FOLDER).mkdirs();
        new File(SCORES_FOLDER).mkdirs();
    }

    public String copyScoreToLibrary(File originalFile) throws IOException {
        Path source = originalFile.toPath();

        Path destination = Path.of(
                SCORES_FOLDER,
                originalFile.getName()
        );

        Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);

        return destination.toString();
    }

    public void saveLibrary(LibraryData libraryData) {
        try (FileWriter writer = new FileWriter(LIBRARY_FILE)) {
            gson.toJson(libraryData, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public LibraryData loadLibrary() {
        File file = new File(LIBRARY_FILE);

        if (!file.exists()) {
            return new LibraryData();
        }

        try (FileReader reader = new FileReader(file)) {
            LibraryData data = gson.fromJson(reader, LibraryData.class);

            if (data == null) {
                return new LibraryData();
            }

            return data;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return new LibraryData();
    }
}