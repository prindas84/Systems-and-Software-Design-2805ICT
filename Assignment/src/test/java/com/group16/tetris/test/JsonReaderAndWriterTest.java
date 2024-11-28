package com.group16.tetris.test;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.group16.tetris.models.HighScoresModel;
import com.group16.tetris.models.PlayerModel;
import com.group16.tetris.utils.JsonFileException;
import com.group16.tetris.utils.JsonReaderAndWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.Filer;

import static org.junit.jupiter.api.Assertions.*;

public class JsonReaderAndWriterTest {

    private JsonReaderAndWriter jsonReaderAndWriter;

    @TempDir
    Path temporaryDirectory;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        jsonReaderAndWriter = new JsonReaderAndWriter();
    }

    @AfterEach
    public void tearDown() {
        JsonReaderAndWriter.scoresRelativePath = "./src/main/resources/data/high_scores.json";
    }

    @Test
    public void testReadFromFile_fileNotFound() {
        // Defines the PlayerModel type for reading from the file
        Type playerModelType = new TypeToken<ArrayList<PlayerModel>>(){}.getType();

        // Attempt to read from a file that does not exist
        assertThrows(JsonFileException.class, () -> {
            jsonReaderAndWriter.readFromFile("error_test_file.json", playerModelType);
        });
    }

    @Test
    public void testReadFromFile_malformedJson(@TempDir Path tempDir) throws Exception {
        // Create a temporary Json file with malformed content called malformed
        Path tempFile = tempDir.resolve("malformed.json");
        try (FileWriter writer = new FileWriter(tempFile.toFile())) {
            writer.write("{\"name\":\"Player1\",\"score\":malformedcontent}"); // The score is a string instead of an integer
        }

        // Defines the PlayerModel type for reading from the file
        Type playerModelType = new TypeToken<ArrayList<PlayerModel>>(){}.getType();

        // Attempt to read from the malformed Json file
        JsonFileException exception = assertThrows(JsonFileException.class, () -> {
            jsonReaderAndWriter.readFromFile(tempFile.toString(), playerModelType);
        });

        // Test if the malformed error message appears
        assertTrue(exception.getMessage().contains("Malformed JSON content"));
    }

    @Test
    public void testReadScoresJson(@TempDir Path tempDir) throws Exception {
        // Create a temporary Json file called high scores
        Path tempFile = tempDir.resolve("high_scores.json");
        try (FileWriter writer = new FileWriter(tempFile.toFile())) {
            writer.write("[{\"name\":\"TestPlayer1\",\"score\":69},{\"name\":\"TestPlayer2\",\"score\":6969}]");
        }

        // Change the scoresRelativePath for testing the temporary file
        JsonReaderAndWriter.scoresRelativePath = tempFile.toString();

        jsonReaderAndWriter.readScoresJson();

        // Test if reading from the file is correct
        ArrayList<PlayerModel> players = jsonReaderAndWriter.getPlayers();
        assertEquals("TestPlayer1", players.get(0).getName());
        assertEquals(69, players.get(0).getScore());
        assertEquals("TestPlayer2", players.get(1).getName());
        assertEquals(6969, players.get(1).getScore());
        // Revert the scoresRelativePath back to the original path
        JsonReaderAndWriter.scoresRelativePath = "./src/main/resources/data/high_scores.json";
    }

    @Test
    public void testWriteScoresJson() throws IOException {
        // Create a temporary high scores model instance
        HighScoresModel highScoresModelTest = new HighScoresModel(); // Create an actual instance of HighScoresModel
        List<Map.Entry<String, Integer>> topScoresTest = new ArrayList<>();
        topScoresTest.add(new AbstractMap.SimpleEntry<>("Player1", 100));
        topScoresTest.add(new AbstractMap.SimpleEntry<>("Player2", 200));

        // Manually add scores to the HighScoresModel instance
        highScoresModelTest.addNewPlayerAndScore("Player1", "test", 100); // Assuming this method exists
        highScoresModelTest.addNewPlayerAndScore("Player2", "test", 200); // Assuming this method exists

        // Create a test file called high_scores.json
        String tempFilePath = temporaryDirectory.resolve("high_scores.json").toString();
        // Change the relative path to the test temporary file
        JsonReaderAndWriter.scoresRelativePath = tempFilePath;

        // Perform the function
        jsonReaderAndWriter.writeScoresJson(highScoresModelTest);

        // Create a file given the test file path
        File file = new File(tempFilePath);
        // Check if its not null
        assertTrue(file.exists());

        // Check that the test file contains the test top scores
        Gson gson = new Gson();
        try (FileReader fileReader = new FileReader(file)) {
            PlayerModel[] players = gson.fromJson(fileReader, PlayerModel[].class);
            assertEquals(2, players.length);
            assertEquals("Player2", players[0].getName());
            assertEquals(200, players[0].getScore());
            assertEquals("test", players[0].getConfig());
            assertEquals("Player1", players[1].getName());
            assertEquals(100, players[1].getScore());
            assertEquals("test", players[1].getConfig());
        }

        // Test the illegal argument if statement
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            jsonReaderAndWriter.writeScoresJson(null);
        });
        // Ensure it prints the correct error statement
        assertTrue(exception.getMessage().contains("HighScoresManager cannot be null"));
    }

    @Test
    public void testWriteToFile() throws IOException {
        // Attempt to read from a file that does not exist using a fake directory
        List<PlayerModel> playerList = new ArrayList<>();
        assertThrows(JsonFileException.class, () -> {
            jsonReaderAndWriter.writeToFile("/invalid_directory/fake_file.json", playerList);
        });
    }
}
