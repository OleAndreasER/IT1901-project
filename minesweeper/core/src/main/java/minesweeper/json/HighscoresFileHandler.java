package minesweeper.json;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import minesweeper.core.Difficulty;
import minesweeper.core.HighscoreEntry;
import minesweeper.core.HighscoreList;
import minesweeper.json.internal.HighscoreEntryDeserializer;
import minesweeper.json.internal.HighscoreEntrySerializer;
import minesweeper.json.internal.HighscoreListDeserializer;
import minesweeper.json.internal.HighscoreListSerializer;

/**
 * Takes care of saving and retrieving HighscoreLists from files.
 * Files are stored in minesweeeper directory in the user's home directory.
 * Each difficulty has its own file containing a highscore list.
 */
public class HighscoresFileHandler {
    private final ObjectMapper mapper;
    private final File highscoreListFile;
    private final HashMap<Difficulty, File> files = new HashMap<>();
    public static final Path MINESWEEPER_DIR =
        Paths.get(System.getProperty("user.home"), "minesweeper");

    /**
     * Constructor for HighscoresFileHandler.
     */
    public HighscoresFileHandler() {
        highscoreListFile = null;
        setFiles();
        mapper = registerModule(new ObjectMapper());
        makeFiles();
    }

    /**
     * Constructor for HighscoresFileHandler used when saving to a custom file.
     * The path is MINESWEEPER_DIR/fileName.
     * @param fileName the fileName in the minesweeper directory
     */
    public HighscoresFileHandler(final String fileName) {
        highscoreListFile = new File(MINESWEEPER_DIR.toString(), fileName);
        setFiles();
        mapper = registerModule(new ObjectMapper());
        makeFiles();
    }

    /**
     * Sets the files for the different difficulties.
     */
    private void setFiles() {
        for (Difficulty difficulty : Difficulty.values()) {
            String difficultyName = difficulty.getName().toLowerCase();
            String fileName = difficultyName + "HighscoreList.json";
            File file = new File(MINESWEEPER_DIR.toString(), fileName);
            files.put(difficulty, file);
        }
    }

    /**
     * Get-method for the objectmapper.
     * @return the objectmapper
     */
    public ObjectMapper getMapper() {
        return mapper;
    }

    /**
     * Registers modules to the mapper.
     * @param objMapper the mapper which will get modules registered
     * @return the mapper, with modules registered
     */
    private ObjectMapper registerModule(final ObjectMapper objMapper) {
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addDeserializer(
            HighscoreEntry.class,
            new HighscoreEntryDeserializer()
        );
        simpleModule.addSerializer(
            HighscoreEntry.class,
            new HighscoreEntrySerializer()
        );
        simpleModule.addDeserializer(
            HighscoreList.class,
            new HighscoreListDeserializer()
        );
        simpleModule.addSerializer(
            HighscoreList.class,
            new HighscoreListSerializer()
        );
        objMapper.registerModule(simpleModule);
        return objMapper;
    }

    /**
     * Saves serialized score object to highscore list in the file
     * corresponding to the difficulty.
     * @param score the score to be saved
     * @param difficulty the difficulty file to be saved to
     */
    public void saveScore(
        final HighscoreEntry score,
        final Difficulty difficulty
    ) {
        HighscoreList highscoreList = readHighscoreList(difficulty);
        highscoreList.addEntry(score);
        try {
            mapper.writeValue(files.get(difficulty), highscoreList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves serialized score object to highscore list in data.json.
     * @param score the score to be saved
     */
    public void saveScore(final HighscoreEntry score) {
        HighscoreList highscoreList = readHighscoreList();
        highscoreList.addEntry(score);
        try {
            mapper.writeValue(highscoreListFile, highscoreList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads highscore list from the file corresponding to the difficulty.
     * @param difficulty difficulty chosen
     * @return highscorelist in the file
     */
    public HighscoreList readHighscoreList(final Difficulty difficulty) {
        makeFiles();
        try {
            return mapper.readValue(
                files.get(difficulty),
                HighscoreList.class
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Reads highscore list from highscorelistFile.
     * @return highscore list in highscorelistFile
     */
    public HighscoreList readHighscoreList() {
        makeFiles();
        try {
            return mapper.readValue(highscoreListFile, HighscoreList.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Saves empty highscore list to file.
     */
    public void setEmptyLists() {
        HighscoreList highscoreList = new HighscoreList();
        try {
            if (highscoreListFile != null && highscoreListFile.length() == 0) {
                mapper.writeValue(highscoreListFile, highscoreList);
            }
            for (File file : files.values()) {
                if (file.length() == 0) {
                    mapper.writeValue(file, highscoreList);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void makeFiles() {
        try {
            MINESWEEPER_DIR.toFile().mkdirs();
            if (highscoreListFile != null) {
                highscoreListFile.createNewFile();
            }
            for (File file : files.values()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        setEmptyLists();
    }
}
