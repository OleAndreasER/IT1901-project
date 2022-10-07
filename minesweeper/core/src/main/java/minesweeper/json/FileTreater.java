package minesweeper.json;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.json.simple.JSONObject;

public class FileTreater {
    
    /**
     * Writes the arguments to a file.
     * @param text the text to be saved
     * @param number the number to be saved
     */
    public void writeToFile(String text, String number) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(text, number);
        try (FileWriter file = new FileWriter(new File("src/main/resources/minesweeper/data.json"), false)){
            file.write(jsonObject.toJSONString());
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }
}
