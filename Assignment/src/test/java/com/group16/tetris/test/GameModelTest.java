package com.group16.tetris.test;

import com.group16.tetris.models.BlockModel;
import com.group16.tetris.models.ConfigurationModel;
import com.group16.tetris.models.GameModel;
import com.group16.tetris.utils.TetrisMusicPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameModelTest {
    GameModel gameModelTest;
    @BeforeEach
    void setUp() {
        TetrisMusicPlayer testTetrisMusicPlayer = new TetrisMusicPlayer("sounds/Background Music - 1.wav");
        gameModelTest = new GameModel(10, 20, 1, false, false,
                ConfigurationModel.PlayerType.HUMAN, ConfigurationModel.PlayerType.HUMAN, true,
                testTetrisMusicPlayer);
    }

    @Test
    void isValidPosition() {
        // Initialises a test board with one invalid block
        boolean [][] testBoard = new boolean[20][10];
        testBoard[18][0] = true;
        gameModelTest.setBoard(testBoard);
        gameModelTest.setBoard2(testBoard);
        // Initialises the test block the testing will be on
        BlockModel testBlock = new BlockModel();
        boolean [][] testBlockShape = {
                {false, true},
                {false, true},
                {true, true}
        };
        testBlock.setBlockShape(testBlockShape, 0);
        gameModelTest.setCurrentBlock(testBlock);
        gameModelTest.setCurrentBlock2(testBlock);
        // Sets the currentX and currentY coordinates for the block on the board
        gameModelTest.setCurrentX(1);
        gameModelTest.setCurrentY(16.1);
        gameModelTest.setCurrentX2(1);
        gameModelTest.setCurrentY2(16.1);
        // Test player 1 for different x coordinates and y coordinates to attain all branches
        assertTrue(gameModelTest.isValidPosition(1, 0, 16.1));
        assertFalse(gameModelTest.isValidPosition(1, 0, 15.9));
        assertTrue(gameModelTest.isValidPosition(1, 2, 16.1));
        assertFalse(gameModelTest.isValidPosition(1, -5, 16.1));
        assertFalse(gameModelTest.isValidPosition(1, 15, 16.1));

        // Tests with player 2 to get the player 2 branch
        assertTrue(gameModelTest.isValidPosition(2, 0, 16.1));
        assertFalse(gameModelTest.isValidPosition(2, 0, 30.1));

        // Adds another invalid space into the board for an additional edge case
        testBoard[18][3] = true;
        gameModelTest.setBoard(testBoard);
        gameModelTest.setBoard2(testBoard);
        // Tests player 1 when the x and y coordinate changes
        assertFalse(gameModelTest.isValidPosition(1, 2, 16.1));
        assertFalse(gameModelTest.isValidPosition(1, 1, 18.1));

        // Tests when the x coordinate is less than 0
        gameModelTest.setCurrentX(-1);
        assertFalse(gameModelTest.isValidPosition(1, -1, 16.1));
        // Tests when the x coordinate is greater than field width
        gameModelTest.setCurrentX(11);
        assertFalse(gameModelTest.isValidPosition(1, 11, 16.1));

        // Tests the last branch if statement
        gameModelTest.setCurrentX(0);
        gameModelTest.setCurrentY(14.9);
        assertFalse(gameModelTest.isValidPosition(1, 0, 16));
        assertFalse(gameModelTest.isValidPosition(1, 0, 17));

        // Updates board with another invalid space and changes x and y coordinates
        gameModelTest.setCurrentX(7);
        gameModelTest.setCurrentY(16.1);
        testBoard[19][7] = true;
        gameModelTest.setBoard(testBoard);
        // Tests when the block goes beyond the field height
        assertFalse(gameModelTest.isValidPosition(1, 7, 16));
    }

    @Test
    void newShapeY() {
        // Initialises the test block the testing will be on
        BlockModel testBlock = new BlockModel();
        boolean [][] testBlockShape = {
                {true, true, true},
                {false, false, true}
        };
        testBlock.setBlockShape(testBlockShape, BlockModel.ShapeType.REVERSE_L_SHAPE, 0);
        gameModelTest.setCurrentBlock(testBlock);
        gameModelTest.setCurrentBlock2(testBlock);
        // Tests the block
        assertEquals(3, gameModelTest.newShapeY(1, 3));
        assertEquals(3, gameModelTest.newShapeY(2, 3));
        // Rotates the block to test it again
        testBlock.setBlockShape(testBlock.nextShape());
        gameModelTest.setCurrentBlock(testBlock);
        assertEquals(2, gameModelTest.newShapeY(1, 3));
        // Rotates the block twice to test it again
        testBlock.setBlockShape(testBlock.nextShape());
        testBlock.setBlockShape(testBlock.nextShape());
        gameModelTest.setCurrentBlock(testBlock);
        assertEquals(5, gameModelTest.newShapeY(1, 3));

        // Tests L shape
        testBlockShape = new boolean[][]{
                {true, true, true},
                {true, false, false}
        };
        testBlock.setBlockShape(testBlockShape, BlockModel.ShapeType.L_SHAPE, 0);
        gameModelTest.setCurrentBlock(testBlock);
        assertEquals(3, gameModelTest.newShapeY(1, 3));

        // Tests Straight Shape
        testBlockShape = new boolean[][]{
                {true, true, true, true}
        };
        testBlock.setBlockShape(testBlockShape, BlockModel.ShapeType.STRAIGHT, 0);
        gameModelTest.setCurrentBlock(testBlock);
        assertEquals(3, gameModelTest.newShapeY(1, 3));
        // Rotates the block twice to test edge case
        testBlock.setBlockShape(testBlock.nextShape());
        testBlock.setBlockShape(testBlock.nextShape());
        gameModelTest.setCurrentBlock(testBlock);
        assertEquals(0, gameModelTest.newShapeY(1, 3));

        testBlock.setBlockShape(testBlock.nextShape());
        gameModelTest.setCurrentBlock(testBlock);
        assertEquals(6, gameModelTest.newShapeY(1, 3));

        testBlockShape = new boolean[][]{
                {true, true},
                {true, true}
        };
        testBlock.setBlockShape(testBlockShape, BlockModel.ShapeType.SQUARE, 0);
        gameModelTest.setCurrentBlock(testBlock);
        assertEquals(3, gameModelTest.newShapeY(1, 3));
    }

    @Test
    void newShapeX() {
        // Initialises the test block the testing will be on
        BlockModel testBlock = new BlockModel();
        boolean [][] testBlockShape = {
                {false, true},
                {false, true},
                {true, true}
        };
        testBlock.setBlockShape(testBlockShape, BlockModel.ShapeType.REVERSE_L_SHAPE, 1);
        gameModelTest.setCurrentBlock(testBlock);
        gameModelTest.setCurrentBlock2(testBlock);

        assertEquals(2, gameModelTest.newShapeX(1, 3));
        assertEquals(2, gameModelTest.newShapeX(2, 3));
        testBlock.setBlockShape(testBlock.nextShape());
        gameModelTest.setCurrentBlock(testBlock);
        assertEquals(5, gameModelTest.newShapeX(1, 3));

        testBlockShape = new boolean[][]{
                {true, false},
                {true, false},
                {true, true}
        };
        testBlock.setBlockShape(testBlockShape, BlockModel.ShapeType.L_SHAPE, 3);
        gameModelTest.setCurrentBlock(testBlock);
        assertEquals(3, gameModelTest.newShapeX(1, 3));

        testBlockShape = new boolean[][]{
                {true},
                {true},
                {true},
                {true}
        };
        testBlock.setBlockShape(testBlockShape, BlockModel.ShapeType.STRAIGHT, 1);
        gameModelTest.setCurrentBlock(testBlock);
        assertEquals(0, gameModelTest.newShapeX(1, 3));
        testBlock.setBlockShape(testBlock.nextShape());
        gameModelTest.setCurrentBlock(testBlock);
        assertEquals(6, gameModelTest.newShapeX(1, 3));
        testBlock.setBlockShape(testBlock.nextShape());
        gameModelTest.setCurrentBlock(testBlock);
        assertEquals(3, gameModelTest.newShapeX(1, 3));

        testBlockShape = new boolean[][]{
                {true, true},
                {true, true}
        };
        testBlock.setBlockShape(testBlockShape, BlockModel.ShapeType.SQUARE, 0);
        gameModelTest.setCurrentBlock(testBlock);
        assertEquals(3, gameModelTest.newShapeX(1, 3));
    }
}