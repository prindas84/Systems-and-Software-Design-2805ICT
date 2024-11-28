package com.group16.tetris.utils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;
import com.group16.tetris.models.ConfigurationModel;
import com.group16.tetris.models.HighScoresModel;
import com.group16.tetris.models.PlayerModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.nio.file.Paths;
import java.io.FileWriter;
import java.io.FileReader;

public class JsonReaderAndWriter {

    private ArrayList<PlayerModel> players;  // List of players read from JSON
    public static String scoresRelativePath = "./src/main/resources/data/high_scores.json";
    public static String configurationRelativePath = "./src/main/resources/config/configuration_settings.json";

    public JsonReaderAndWriter() {
        this.players = new ArrayList<>();
    }

    // Reads from a JSON file and returns data of the specified type
    public <T> T readFromFile(String relativePath, Type typeOfT) throws JsonFileException {
        String absolutePath = Paths.get(relativePath).toAbsolutePath().normalize().toString();
        try (FileReader fileReader = new FileReader(absolutePath)) {
            Gson gson = new Gson();
            T data = gson.fromJson(fileReader, typeOfT);
            return data;
        } catch (JsonSyntaxException e) {
            throw new JsonFileException("Malformed JSON content at path: " + relativePath, e);
        } catch (IOException e) {
            throw new JsonFileException("Could not find the JSON file at path: " + relativePath, e);
        }
    }

    // Reads high scores from the JSON file
    public void readScoresJson() {
        Type type = new TypeToken<ArrayList<PlayerModel>>() {}.getType();
        players = readFromFile(scoresRelativePath, type);
    }

    // Reads configuration settings from the JSON file
    public void readConfigurationJson() {
        ConfigurationModel settings = ConfigurationModel.getInstance();
        ConfigurationModel fileSettings = readFromFile(configurationRelativePath, ConfigurationModel.class);

        if (fileSettings != null) {
            settings.loadSettings(
                    fileSettings.getFieldWidth(),
                    fileSettings.getFieldHeight(),
                    fileSettings.getGameLevel(),
                    fileSettings.isMusicEnabled(),
                    fileSettings.isSoundEffectsEnabled(),
                    fileSettings.isExtendModeEnabled(),
                    fileSettings.getPlayer1Type(),
                    fileSettings.getPlayer2Type()
            );
        } else {
            settings.resetToDefaults();
        }
    }

    // Writes data to a JSON file
    public <T> void writeToFile(String relativePath, T data) {
        String absolutePath = Paths.get(relativePath).toAbsolutePath().normalize().toString();
        try (FileWriter fileWriter = new FileWriter(absolutePath)) {
            Gson gson = new Gson();
            gson.toJson(data, fileWriter);
        } catch (IOException e) {
            throw new JsonFileException("Failed to write to JSON file at path: " + relativePath, e);
        }
    }

    // Writes high scores to the JSON file
    public void writeScoresJson(HighScoresModel manager) {
        if (manager == null) {
            throw new IllegalArgumentException("HighScoresManager cannot be null");
        }
        List<PlayerModel> playerList = new ArrayList<>();
        List<Map.Entry<String, Integer>> topScores = manager.getTopScores(10);

        for (Map.Entry<String, Integer> entry : topScores) {
            playerList.add(new PlayerModel(entry.getKey(), entry.getValue(), manager.getConfig(entry.getKey())));
        }

        writeToFile(scoresRelativePath, playerList);
    }

    // Writes configuration settings to the JSON file
    public void writeConfigurationFile() {
        ConfigurationModel settings = ConfigurationModel.getInstance();
        if (settings == null) {
            throw new IllegalArgumentException("ConfigurationManager cannot be null");
        }
        writeToFile(configurationRelativePath, settings);
    }

    // Clears content of the specified file
    public void clearFile(String filePath) {
        players = new ArrayList<>();
        try {
            String absolutePath = Paths.get(filePath).toAbsolutePath().normalize().toString();
            FileWriter fileWriter = new FileWriter(absolutePath, false);  // Clears the file
            fileWriter.close();
        } catch (IOException e) {
            throw new JsonFileException("Failed to write to Json file at path: " + filePath, e);
        }
    }

    // Clears the high scores file
    public void clearScoresFile() {
        clearFile(scoresRelativePath);
    }

    // Clears the configuration settings file
    public void clearConfigurationFile() {
        clearFile(configurationRelativePath);
    }

    // Returns the list of players
    public ArrayList<PlayerModel> getPlayers() {
        return players;
    }
}
