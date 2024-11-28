package com.group16.tetris.test;

import com.group16.tetris.models.HighScoresModel;
import com.group16.tetris.models.PlayerModel;
import com.group16.tetris.models.ConfigurationModel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

class HighScoresModelTest {

    private HighScoresModel highScoresModel;

    @BeforeEach
    void setUp() {
        highScoresModel = new HighScoresModel();
    }

    @Test
    void testAddNewPlayerAndScore() {
        highScoresModel.addNewPlayerAndScore("Player1", "10x20(5) Human Double", 1000);
        highScoresModel.addNewPlayerAndScore("Player2", "10x20(3) AI Single", 2000);

        assertEquals(2, highScoresModel.getTopScores(10).size());
        assertEquals(2000, highScoresModel.getTopScores(10).get(0).getValue());
        assertEquals(1000, highScoresModel.getTopScores(10).get(1).getValue());
    }

    @Test
    void testGetTopScores() {
        highScoresModel.addNewPlayerAndScore("Player1", "10x20(5) Human Double", 1000);
        highScoresModel.addNewPlayerAndScore("Player2", "10x20(3) AI Single", 2000);
        highScoresModel.addNewPlayerAndScore("Player3", "10x20(7) External Single", 1500);

        List<Map.Entry<String, Integer>> topScores = highScoresModel.getTopScores(2);
        assertEquals(2, topScores.size());
        assertEquals(2000, topScores.get(0).getValue());
        assertEquals(1500, topScores.get(1).getValue());
    }

    @Test
    void testNewPlayerName() {
        // Player name does not exist
        String uniqueName = highScoresModel.newPlayerName("UniquePlayer");
        assertEquals("UniquePlayer", uniqueName);

        // Add a player with name "Player1"
        highScoresModel.addNewPlayerAndScore("Player1", "10x20(5) Human Double", 1000);

        // Player name already exists
        String newName = highScoresModel.newPlayerName("Player1");
        assertEquals("Player11", newName);

        // Simulate that "Player11" also exists
        highScoresModel.addNewPlayerAndScore("Player11", "10x20(5) Human Double", 1100);

        // Should get Player12 back here
        String newName2 = highScoresModel.newPlayerName("Player1");
        assertEquals("Player12", newName2);
    }

    @Test
    void testLoadFromFile() {
        ArrayList<PlayerModel> playerList = new ArrayList<>();
        PlayerModel player1 = new PlayerModel("Player1", 1000, "10x20(5) Human Double");
        PlayerModel player2 = new PlayerModel("Player2", 1500, "10x20(7) AI Single");
        playerList.add(player1);
        playerList.add(player2);

        highScoresModel.loadFromFile(playerList);

        assertEquals(2, highScoresModel.getTopScores(10).size());
        assertEquals(1500, highScoresModel.getTopScores(10).get(0).getValue());
    }

    @Test
    void testResetScores() {
        highScoresModel.addNewPlayerAndScore("Player1", "10x20(5) Human Double", 1000);
        highScoresModel.addNewPlayerAndScore("Player2", "10x20(3) AI Single", 2000);

        highScoresModel.resetScores();

        assertEquals(0, highScoresModel.getTopScores(10).size());
    }

    @Test
    void testConstructConfig() {
        String config1 = highScoresModel.constructConfig(10, 20, 5, ConfigurationModel.PlayerType.HUMAN, true);
        assertEquals("10x20(5) Human Double", config1);

        String config2 = highScoresModel.constructConfig(10, 20, 7, ConfigurationModel.PlayerType.AI, false);
        assertEquals("10x20(7) AI Single", config2);

        String config3 = highScoresModel.constructConfig(10, 20, 7, ConfigurationModel.PlayerType.EXTERNAL, false);
        assertEquals("10x20(7) External Single", config3);
    }

    @Test
    void testGetConfig() {
        highScoresModel.addNewPlayerAndScore("Player1", "10x20(5) Human Double", 1000);
        assertEquals("10x20(5) Human Double", highScoresModel.getConfig("Player1"));
    }
}
