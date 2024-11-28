package com.group16.tetris.models;

import com.group16.tetris.models.BlockModel.ShapeType;
import com.group16.tetris.models.ConfigurationModel.PlayerType;
import com.group16.tetris.models.ConfigurationModel;
import com.group16.tetris.services.TetrisAI;
import com.group16.tetris.services.TetrisClient;
import com.group16.tetris.utils.TetrisMusicPlayer;
import com.group16.tetris.utils.TetrisSoundEffectsPlayer;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

// New imports for networking
import com.google.gson.Gson;
import java.io.*;

import com.group16.tetris.models.PureGame;
import com.group16.tetris.models.OpMove;

// GameManager class handles the game's core logic, rendering, and interactions
public class GameModel extends JPanel implements KeyListener, ActionListener {

    // Dimensions of the game field in terms of blocks
    private int fieldWidth;
    private int fieldHeight;

    // Size of each block in pixels
    private int blockSize = 20;

    // Arrays to track the state of the game field
    private boolean[][] board;
    private Color[][] colourBoard;

    // Current falling block's properties
    private BlockModel currentBlock;
    private int currentX;
    private double currentY;
    private int currentBlockIndex = 0;

    // Timer to control the update loop of the game
    private Timer timer;
    private final static int displayPanel = 120;

    // Initial game level
    private int initialGameLevel;
    private List<BlockModel> blockList;

    // Constants for timer delay and border thickness
    private final int TIMER_DELAY = 20;
    private final int BORDER_THICKNESS = 3;

    // Flags and variables to track the game state for player 1
    public boolean isPausedPlayer1;
    public boolean isGameEndedPlayer1 = true;
    public boolean isDisconnectedPlayer1 = false;
    private int gameLevelPlayer1 = 1;
    private int scorePlayer1;
    private int deletedRowsPlayer1;
    private int deletedRowsPlayer1Counter;
    private PlayerType typeOfPlayer1 = PlayerType.HUMAN;

    // Arrays to track the state of the game field for player 2
    private boolean[][] board2;
    private Color[][] colourBoard2;

    // Current falling block's properties for player 2
    private BlockModel currentBlock2;
    private int currentX2;
    private double currentY2;
    private int currentBlockIndex2 = 0;

    // Flags and variables to track the game state for player 2
    public boolean isPausedPlayer2;
    public boolean isGameEndedPlayer2 = true;
    public boolean isDisconnectedPlayer2 = false;
    private int gameLevelPlayer2 = 1;
    private int scorePlayer2;
    private int deletedRowsPlayer2;
    private int deletedRowsPlayer2Counter;
    private PlayerType typeOfPlayer2 = PlayerType.HUMAN;

    // Configuration settings for music, sound effects, and game modes
    private boolean music;
    private boolean soundEffects;
    private boolean extendMode;

    private TetrisMusicPlayer musicPlayer;
    private TetrisSoundEffectsPlayer soundEffectsPlayer;
    private ConfigurationModel configurationSettings;
    private boolean gameOverSoundPlayedPlayer1 = false;
    private boolean gameOverSoundPlayedPlayer2 = false;

    // AI manager and Tetris client for external control
    private TetrisAI aiManager;
    private TetrisClient tetrisClient;

    // Variables for managing moves for external player control
    private int targetX;
    private int targetX2;
    private int targetRotation;
    private int targetRotation2;

    private int[] movesArray;
    private int[] movesArray2;

    // Updates player-specific game variables based on the provided values
    private void updatePlayerTypeVariables(int player, int currentX, double currentY, BlockModel currentBlock,
            int currentBlockIndex, int gameLevel, int score, int deletedRows,
            int deletedRowsCounter, boolean isGameEnded, boolean[][] board,
            Color[][] colourBoard) {
        if (player == 1) {
            this.currentX = currentX;
            this.currentY = currentY;
            this.currentBlock = new BlockModel(currentBlock);
            this.currentBlockIndex = currentBlockIndex;
            this.gameLevelPlayer1 = gameLevel;
            this.scorePlayer1 = score;
            this.deletedRowsPlayer1 = deletedRows;
            this.deletedRowsPlayer1Counter = deletedRowsCounter;
            this.isGameEndedPlayer1 = isGameEnded;
            this.board = board;
            this.colourBoard = colourBoard;
        } else {
            this.currentX2 = currentX;
            this.currentY2 = currentY;
            this.currentBlock2 = new BlockModel(currentBlock);
            this.currentBlockIndex2 = currentBlockIndex;
            this.gameLevelPlayer2 = gameLevel;
            this.scorePlayer2 = score;
            this.deletedRowsPlayer2 = deletedRows;
            this.deletedRowsPlayer2Counter = deletedRowsCounter;
            this.isGameEndedPlayer2 = isGameEnded;
            this.board2 = board;
            this.colourBoard2 = colourBoard;
            this.isDisconnectedPlayer2 = false;
        }
    }

    // Constructor that initialises the game with specified field dimensions
    public GameModel(int width, int height, int gameLevel, boolean music, boolean soundEffects, PlayerType typeOfPlayer1, PlayerType typeOfPlayer2, boolean extendMode, TetrisMusicPlayer musicPlayer) {
        this.fieldWidth = width;
        this.aiManager = new TetrisAI();
        this.tetrisClient = new TetrisClient();
        this.fieldHeight = height;
        this.music = music;
        this.soundEffects = soundEffects;
        this.typeOfPlayer1 = typeOfPlayer1;
        this.gameLevelPlayer1 = gameLevel;
        this.initialGameLevel = gameLevel;
        this.extendMode = extendMode;
        this.musicPlayer = musicPlayer;
        this.soundEffectsPlayer = new TetrisSoundEffectsPlayer();
        this.configurationSettings = ConfigurationModel.getInstance();
        this.scorePlayer1 = 0;
        this.deletedRowsPlayer1 = 0;
        this.deletedRowsPlayer1Counter = 0;
        this.blockList = generateBlocks();
        // If 2 player mode is activated, the second player is initialised and the dimensions of the panel is increased
        if (this.extendMode) {
            this.gameLevelPlayer2 = gameLevel;
            this.typeOfPlayer2 = typeOfPlayer2;
            this.scorePlayer2 = 0;
            this.deletedRowsPlayer2 = 0;
            this.deletedRowsPlayer2Counter = 0;
            board2 = new boolean[fieldHeight][fieldWidth];
            colourBoard2 = new Color[fieldHeight][fieldWidth];
            setPreferredSize(new Dimension(2 * (width * blockSize + BORDER_THICKNESS * 2) + 2 * displayPanel + 20,
                    height * blockSize + BORDER_THICKNESS * 2));
            isPausedPlayer2 = true;
            isGameEndedPlayer2 = false;
            spawnBlock(2);
        } else {
            // Initialise the game field and block colors
            board = new boolean[fieldHeight][fieldWidth];
            colourBoard = new Color[fieldHeight][fieldWidth];

            // Set up the game panel size and appearance
            setPreferredSize(new Dimension(fieldWidth * blockSize + BORDER_THICKNESS * 2 + displayPanel,
                    fieldHeight * blockSize + BORDER_THICKNESS * 2));
            isPausedPlayer1 = true;
            isGameEndedPlayer1 = false;
        }
        initialiseGame(); // Set up the game environment and start the game
    }

    // Method to initialise the game state
    private void initialiseGame() {
        setFocusable(true); // Allow the panel to receive keyboard input
        addKeyListener(this); // Register this class as a key listener
        setBorder(BorderFactory.createLineBorder(Color.BLACK, BORDER_THICKNESS)); // Add a black border around the panel

        // Set up the game update timer
        timer = new Timer(TIMER_DELAY, this);

        // Start the game loop
        timer.start();
    }

    // Spawns a new block at the top of the game field for the specified player
    private void spawnBlock(int player) {
        BlockModel currentBlockRef;
        boolean[][] boardRef;
        int currentBlockIndexRef;
        int currentXRef;
        double currentYRef;
        boolean isGameEndedRef;

        // Set variables for player 1 or player 2 based on the passed player value
        if (player == 1) {
            currentBlockRef = new BlockModel(currentBlock);
            currentBlockIndexRef = currentBlockIndex;
            currentXRef = currentX;
            currentYRef = currentY;
            isGameEndedRef = isGameEndedPlayer1;
            boardRef = board;
        } else {
            currentBlockRef = new BlockModel(currentBlock2);
            currentBlockIndexRef = currentBlockIndex2;
            currentXRef = currentX2;
            currentYRef = currentY2;
            isGameEndedRef = isGameEndedPlayer2;
            boardRef = board2;
        }

        // Initialise a new block from the block list and place it in the centre
        currentBlockRef = new BlockModel(blockList.get(currentBlockIndexRef));
        currentBlockIndexRef++;
        currentXRef = (fieldWidth / 2) - (currentBlockRef.getWidth() / 2);
        currentYRef = 0;

        // Update the game state with the new block
        if (player == 1){
            updatePlayerTypeVariables(player, currentXRef, currentYRef, currentBlockRef, currentBlockIndexRef,
                    gameLevelPlayer1, scorePlayer1, deletedRowsPlayer1, deletedRowsPlayer1Counter, isGameEndedRef,
                    boardRef, colourBoard);
        } else {
            updatePlayerTypeVariables(player, currentXRef, currentYRef, currentBlockRef, currentBlockIndexRef,
                    gameLevelPlayer2, scorePlayer2, deletedRowsPlayer2, deletedRowsPlayer2Counter, isGameEndedRef,
                    boardRef, colourBoard2);
        }

        // End game if the new block can't be placed in a valid position
        if (!isValidPosition(player, currentXRef, currentYRef)) {
            if (player == 1) {
                isGameEndedPlayer1 = true;
            } else {
                isGameEndedPlayer2 = true;
            }
        }

        // Request a move from AI or external player if the player is not human
        if (!isHumanPlayer(player)) {
            boolean isExternalPlayer = (player == 1 && typeOfPlayer1 == PlayerType.EXTERNAL) ||
                    (player == 2 && typeOfPlayer2 == PlayerType.EXTERNAL);
            requestMove(player, isExternalPlayer);
        }
    }

    public boolean isValidPosition(int player, int x, double y) {
        boolean[][] boardRef;
        int currentXRef;
        double currentYRef;
        BlockModel currentBlockRef;

        // Determine the current player and assign the respective game variables
        if (player == 1) {
            boardRef = board;
            currentXRef = currentX;
            currentYRef = currentY;
            currentBlockRef = new BlockModel(currentBlock);
        } else {
            boardRef = board2;
            currentXRef = currentX2;
            currentYRef = currentY2;
            currentBlockRef = new BlockModel(currentBlock2);
        }

        int intY = (int) y;

        // Check if the block can move left or right into a valid position
        if (currentXRef != x) {
            boolean canMoveRow1 = true;
            boolean canMoveRow2 = true;

            // Check if the first row is valid for movement
            for (int i = 0; i < currentBlockRef.getHeight(); i++) {
                for (int j = 0; j < currentBlockRef.getWidth(); j++) {
                    if (currentBlockRef.getBlockShape()[i][j]) {
                        int newX = x + j;
                        int newY = intY + i;
                        // Verify if the block is out of bounds or collides with another block
                        if (newX < 0 || newX >= fieldWidth || newY >= fieldHeight || boardRef[newY][newX]) {
                            canMoveRow1 = false;
                            break;
                        }
                    }
                }
                if (!canMoveRow1)
                    break;
            }

            // Check if the second row is valid for movement
            intY++;
            for (int i = 0; i < currentBlockRef.getHeight(); i++) {
                for (int j = 0; j < currentBlockRef.getWidth(); j++) {
                    if (currentBlockRef.getBlockShape()[i][j]) {
                        int newX = x + j;
                        int newY = intY + i;
                        // Verify if the block is out of bounds or collides with another block
                        if (newX < 0 || newX >= fieldWidth || newY >= fieldHeight || boardRef[newY][newX]) {
                            canMoveRow2 = false;
                            break;
                        }
                    }
                }
                if (!canMoveRow2)
                    break;
            }

            // Return true if both rows are valid, or adjust the y-coordinate if only the second row is valid
            if (canMoveRow1 && canMoveRow2) {
                return true;
            } else if (!canMoveRow1 && canMoveRow2) {
                currentYRef = intY;
                if (player == 1) {
                    updatePlayerTypeVariables(player, currentXRef, currentYRef, currentBlockRef, currentBlockIndex,
                            gameLevelPlayer1, scorePlayer1, deletedRowsPlayer1, deletedRowsPlayer1Counter,
                            isGameEndedPlayer1, boardRef, colourBoard);
                } else {
                    updatePlayerTypeVariables(player, currentXRef, currentYRef, currentBlockRef, currentBlockIndex2,
                            gameLevelPlayer2, scorePlayer2, deletedRowsPlayer2, deletedRowsPlayer2Counter,
                            isGameEndedPlayer2, boardRef, colourBoard2);
                }
                return true;
            }
            return false;
        }

        // Check vertical movement within the same column
        intY = (int) y;
        for (int i = 0; i < currentBlockRef.getHeight(); i++) {
            for (int j = 0; j < currentBlockRef.getWidth(); j++) {
                if (currentBlockRef.getBlockShape()[i][j]) {
                    int newX = x + j;
                    int newY = intY + i;
                    // Verify if the block is out of bounds or collides with another block
                    if (newX < 0 || newX >= fieldWidth || newY >= fieldHeight || boardRef[newY][newX]) {
                        return false;
                    }
                }
            }
        }

        // Check if moving down further collides with blocks or is out of bounds
        intY++;
        for (int i = 0; i < currentBlockRef.getHeight(); i++) {
            for (int j = 0; j < currentBlockRef.getWidth(); j++) {
                if (currentBlockRef.getBlockShape()[i][j]) {
                    int newX = x + j;
                    int newY = intY + i;
                    // Verify if the block is out of bounds or collides with another block
                    if (newY >= fieldHeight || boardRef[newY][newX]) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    // Stops the current block and adds it to the board for the given player
    private void stopBlock(int player) {
        boolean[][] boardRef;
        Color[][] colourBoardRef;
        int currentXRef;
        double currentYRef;
        BlockModel currentBlockRef;

        // Reference game variables based on the player
        if (player == 1) {
            boardRef = board;
            colourBoardRef = colourBoard;
            currentXRef = currentX;
            currentYRef = currentY;
            currentBlockRef = new BlockModel(currentBlock);
        } else {
            boardRef = board2;
            colourBoardRef = colourBoard2;
            currentXRef = currentX2;
            currentYRef = currentY2;
            currentBlockRef = new BlockModel(currentBlock2);
        }

        int intY = (int) currentYRef;

        // Add the current block to the board
        for (int i = 0; i < currentBlockRef.getHeight(); i++) {
            for (int j = 0; j < currentBlockRef.getWidth(); j++) {
                if (currentBlockRef.getBlockShape()[i][j]) {
                    int newX = currentXRef + j;
                    int newY = intY + i;
                    boardRef[newY][newX] = true;
                    colourBoardRef[newY][newX] = currentBlockRef.getColour();
                }
            }
        }

        // Update the game variables for the player
        if (player == 1) {
            updatePlayerTypeVariables(player, currentXRef, currentYRef, currentBlockRef, currentBlockIndex,
                    gameLevelPlayer1, scorePlayer1, deletedRowsPlayer1, deletedRowsPlayer1Counter, isGameEndedPlayer1,
                    boardRef, colourBoardRef);
        } else {
            updatePlayerTypeVariables(player, currentXRef, currentYRef, currentBlockRef, currentBlockIndex2,
                    gameLevelPlayer2, scorePlayer2, deletedRowsPlayer2, deletedRowsPlayer2Counter, isGameEndedPlayer2,
                    boardRef, colourBoardRef);
        }

        // Play sound effect if enabled
        if (configurationSettings.isSoundEffectsEnabled()) {
            soundEffectsPlayer.playSettleBlockSound();
        }

        // Check for any completed rows
        checkCompletedRow(player);

        // Spawn a new block for the player
        spawnBlock(player);
    }

    // Checks for completed rows and removes them from the board
    private void checkCompletedRow(int player) {
        boolean[][] boardRef;
        Color[][] colourBoardRef;
        int currentXRef;
        double currentYRef;
        BlockModel currentBlockRef;
        int deleteRowsRef;
        int deleteRowsCountRef;
        int scoreRef;
        int gameLevelRef;
        boolean isGameEndedRef;

        // Reference game variables based on the player
        if (player == 1) {
            boardRef = board;
            colourBoardRef = colourBoard;
            currentXRef = currentX;
            currentYRef = currentY;
            currentBlockRef = new BlockModel(currentBlock);
            deleteRowsRef = deletedRowsPlayer1;
            deleteRowsCountRef = deletedRowsPlayer1Counter;
            scoreRef = scorePlayer1;
            gameLevelRef = gameLevelPlayer1;
            isGameEndedRef = isGameEndedPlayer1;
        } else {
            boardRef = board2;
            colourBoardRef = colourBoard2;
            currentXRef = currentX2;
            currentYRef = currentY2;
            currentBlockRef = new BlockModel(currentBlock2);
            deleteRowsRef = deletedRowsPlayer2;
            deleteRowsCountRef = deletedRowsPlayer2Counter;
            scoreRef = scorePlayer2;
            gameLevelRef = gameLevelPlayer2;
            isGameEndedRef = isGameEndedPlayer2;
        }

        int deletedRowsInACheck = 0;

        // Iterate over the rows and check for fully completed rows
        for (int i = 0; i < fieldHeight; i++) {
            boolean fullRow = true;

            // Check if a row is completely filled
            for (int j = 0; j < fieldWidth; j++) {
                if (!boardRef[i][j]) {
                    fullRow = false;
                    break;
                }
            }

            // If a full row is found, delete it and shift rows down
            if (fullRow) {
                deletedRowsInACheck++;
                for (int j = i; j > 0; j--) {
                    System.arraycopy(boardRef[j - 1], 0, boardRef[j], 0, fieldWidth);
                    System.arraycopy(colourBoardRef[j - 1], 0, colourBoardRef[j], 0, fieldWidth);
                }
                boardRef[0] = new boolean[fieldWidth];
                colourBoardRef[0] = new Color[fieldWidth];

                // Play erase row sound if enabled
                if (configurationSettings.isSoundEffectsEnabled()) {
                    soundEffectsPlayer.playEraseRowSound();
                }
            }
        }

        // Update the score based on the number of deleted rows
        deleteRowsRef += deletedRowsInACheck;
        if (deletedRowsInACheck == 1) {
            scoreRef += 100;
        } else if (deletedRowsInACheck == 2) {
            scoreRef += 300;
        } else if (deletedRowsInACheck == 3) {
            scoreRef += 600;
        } else if (deletedRowsInACheck == 4) {
            scoreRef += 1000;
        }

        // Update the game level after a certain number of rows
        deleteRowsCountRef += deletedRowsInACheck;
        if (deleteRowsCountRef >= 10 && !isGameEndedRef) {
            if (gameLevelRef < 10) {
                gameLevelRef++;
            }
            deleteRowsCountRef -= 10;
        }

        // Update the game variables after row completion
        if (player == 1) {
            updatePlayerTypeVariables(player, currentXRef, currentYRef, currentBlockRef, currentBlockIndex,
                    gameLevelRef, scoreRef, deleteRowsRef, deleteRowsCountRef, isGameEndedRef,
                    boardRef, colourBoardRef);
        } else {
            updatePlayerTypeVariables(player, currentXRef, currentYRef, currentBlockRef, currentBlockIndex2,
                    gameLevelRef, scoreRef, deleteRowsRef, deleteRowsCountRef, isGameEndedRef,
                    boardRef, colourBoardRef);
        }
    }

    // Adjusts the y-coordinate when rotating specific block shapes
    public double newShapeY(int player, double y) {
        BlockModel currentBlockRef;
        if (player == 1) {
            currentBlockRef = new BlockModel(currentBlock);
        } else {
            currentBlockRef = new BlockModel(currentBlock2);
        }

        // Handle y-adjustments for the Straight and L-shaped blocks
        if (currentBlockRef.getShapeType() == ShapeType.STRAIGHT) {
            if (currentBlockRef.getRotationIndex() == 2) {
                y -= 3;
            } else if (currentBlockRef.getRotationIndex() == 3) {
                y += 3;
            }
        } else if (currentBlockRef.getShapeType() == ShapeType.L_SHAPE
                || currentBlockRef.getShapeType() == ShapeType.REVERSE_L_SHAPE) {
            switch (currentBlockRef.getRotationIndex()) {
                case 1, 2 -> y -= 1;
                case 3 -> y += 2;
            }
        }
        return y;
    }

    // Adjusts the x-coordinate when rotating specific block shapes
    public int newShapeX(int player, int x) {
        BlockModel currentBlockRef;
        if (player == 1) {
            currentBlockRef = new BlockModel(currentBlock);
        } else {
            currentBlockRef = new BlockModel(currentBlock2);
        }

        // Handle x-adjustments for the Straight and L-shaped blocks
        if (currentBlockRef.getShapeType() == ShapeType.STRAIGHT) {
            switch (currentBlockRef.getRotationIndex()) {
                case 1 -> x -= 3;
                case 2 -> x += 3;
            }
        } else if (currentBlockRef.getShapeType() == ShapeType.L_SHAPE
                || currentBlockRef.getShapeType() == ShapeType.REVERSE_L_SHAPE) {
            switch (currentBlockRef.getRotationIndex()) {
                case 0, 1 -> x -= 1;
                case 2 -> x += 2;
            }
        }
        return x;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        drawBackground(g);  // Draws the background of the game field
        drawGrid(g, 0);  // Draws grid lines for player 1
        drawBoard(g, 1, 0);  // Draws placed blocks for player 1
        drawCurrentBlock(g, 1, 0);  // Draws current falling block for player 1
        drawDisplayInfo(g, 1, 0);  // Displays player 1's game info

        if (extendMode) {
            // Draws elements for player 2 in extended mode
            int x = displayPanel + fieldWidth * blockSize + 20;
            drawGrid(g, x);
            drawBoard(g, 2, x);
            drawCurrentBlock(g, 2, x);
            drawDisplayInfo(g, 2, x);

            // Handles game over or paused state for player 2
            if (isGameEndedPlayer2) {
                if (configurationSettings.isMusicEnabled() && isGameEndedPlayer1) {
                    musicPlayer.stopMusic();
                }
                if (configurationSettings.isSoundEffectsEnabled() && !gameOverSoundPlayedPlayer2) {
                    if (isGameEndedPlayer1) {
                        soundEffectsPlayer.playGameOverSound();
                    } else {
                        soundEffectsPlayer.playGameOverSound2();
                    }
                    gameOverSoundPlayedPlayer2 = true;
                }
                drawCenteredMessage(g, "Game Over", Color.RED, 30, 2 * displayPanel + fieldWidth * blockSize + 20 + fieldWidth * blockSize / 2, 0);
            } else if (isPausedPlayer2) {
                drawCenteredMessage(g, "Game Paused", Color.BLUE, 30, 2 * displayPanel + fieldWidth * blockSize + 20 + fieldWidth * blockSize / 2, 0);
                drawCenteredMessage(g, "Press 'Q' to Resume", Color.BLUE, 15, 2 * displayPanel + fieldWidth * blockSize + 20 + fieldWidth * blockSize / 2, 60);
            }
        }

        // Handles game over or paused state for player 1
        if (isGameEndedPlayer1) {
            if (configurationSettings.isMusicEnabled() && isGameEndedPlayer2) {
                musicPlayer.stopMusic();
            }
            if (configurationSettings.isSoundEffectsEnabled() && !gameOverSoundPlayedPlayer1) {
                if (isGameEndedPlayer2) {
                    soundEffectsPlayer.playGameOverSound();
                } else {
                    soundEffectsPlayer.playGameOverSound2();
                }
                gameOverSoundPlayedPlayer1 = true;
            }
            drawCenteredMessage(g, "Game Over", Color.RED, 30, displayPanel + fieldWidth * blockSize / 2, 0);
        } else if (isPausedPlayer1) {
            drawCenteredMessage(g, "Game Paused", Color.BLUE, 30, displayPanel + fieldWidth * blockSize / 2, 0);
            drawCenteredMessage(g, "Press 'P' to Resume", Color.BLUE, 15, displayPanel + fieldWidth * blockSize / 2, 60);
        }
    }

    private void drawDisplayInfo(Graphics g, int playerNumber, int xOffset) {
        String gameInfo = "Game Info (Player " + playerNumber + ")";
        drawDisplayMessages(g, gameInfo, 10, xOffset, 0, Color.BLACK); // Draws player number

        PlayerType playerType = (playerNumber == 1) ? typeOfPlayer1 : typeOfPlayer2;
        String player_type = "Player Type: ";
        if (playerType == PlayerType.HUMAN) {
            player_type = player_type.concat("Human");
        } else if (playerType == PlayerType.AI) {
            player_type = player_type.concat("AI");
        } else {
            player_type = player_type.concat("External");
        }
        drawDisplayMessages(g, player_type, 10, xOffset, 30, Color.BLACK); // Draws the player type

        String initialLevel = "Initial Level: " + initialGameLevel;
        drawDisplayMessages(g, initialLevel, 10, xOffset, 60, Color.BLACK); // Draws the initial level

        int currentGameLevel = (playerNumber == 1) ? gameLevelPlayer1 : gameLevelPlayer2;
        String currentLevel = "Current Level: " + currentGameLevel;
        drawDisplayMessages(g, currentLevel, 10, xOffset, 90, Color.BLACK); // Draws the current level

        int deletedRows = (playerNumber == 1) ? deletedRowsPlayer1 : deletedRowsPlayer2;
        String lineErased = "Line Erased: " + deletedRows;
        drawDisplayMessages(g, lineErased, 10, xOffset, 120, Color.BLACK); // Draws the number of deleted rows

        int currentScore = (playerNumber == 1) ? scorePlayer1 : scorePlayer2;
        String currentScoreStr = "Score: " + currentScore;
        drawDisplayMessages(g, currentScoreStr, 10, xOffset, 150, Color.BLACK); // Draws the current score

        // Displays the current Music status
        String musicOnOff = "ON";
        if (configurationSettings.isMusicEnabled()) {
            musicOnOff = "ON";
        } else {
            musicOnOff = "OFF";
        }
        String musicEnabled = "Music: " + musicOnOff;
        drawDisplayMessages(g, musicEnabled, 10, xOffset, 180, Color.BLACK);

        // Displays the current Sound Effects status
        String soundEffectsOnOff = "ON";
        if (configurationSettings.isSoundEffectsEnabled()) {
            soundEffectsOnOff = "ON";
        } else {
            soundEffectsOnOff = "OFF";
        }
        String soundEffectsEnabled = "Sound Effects: " + soundEffectsOnOff;
        drawDisplayMessages(g, soundEffectsEnabled, 10, xOffset, 210, Color.BLACK);

        drawDisplayMessages(g, "Next Tetromino:", 10, xOffset, 240, Color.BLACK);

        if (playerNumber == 1) {
            drawNextBlock(g, 1, 10, 310); // Draws next tetromino for player 1
        } else {
            drawNextBlock(g, 2, xOffset + 10, 310); // Draws next tetromino for player 2
        }

        // Displays the current server status
        if (playerType == PlayerType.EXTERNAL) {
            boolean isDisconnected = (playerNumber == 1) ? isDisconnectedPlayer1 : isDisconnectedPlayer2;
            if (isDisconnected) {
                drawDisplayMessages(g, "Could not connect", 10, xOffset, 330, Color.RED); // Draws the disconnected status
                drawDisplayMessages(g, "to TetrisServer", 10, xOffset, 340, Color.RED); // Draws the disconnected status
            } else {
                drawDisplayMessages(g, "Connected to", 10, xOffset, 330, Color.GREEN); // Draws the connected status
                drawDisplayMessages(g, "TetrisServer", 10, xOffset, 340, Color.GREEN); // Draws the connected status
            }
        }
    }

    // Method to draw the game field background
    private void drawBackground(Graphics g) {
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    // Method to draw the grid lines on the game field
    private void drawGrid(Graphics g, int xOffset) {
        g.setColor(Color.GRAY);
        for (int i = 0; i <= fieldHeight; i++) {
            g.drawLine(displayPanel + xOffset, i * blockSize, fieldWidth * blockSize + displayPanel + xOffset,
                    i * blockSize);
        }
        for (int j = 0; j <= fieldWidth; j++) {
            g.drawLine(j * blockSize + displayPanel + xOffset, 0, j * blockSize + displayPanel + xOffset,
                    fieldHeight * blockSize);
        }
    }

    // Method to draw the blocks that have been placed on the board
    private void drawBoard(Graphics g, int player, int xOffset) {
        boolean[][] boardRef;
        Color[][] colourBoardRef;
        if (player == 1) {
            boardRef = board;
            colourBoardRef = colourBoard;
        } else {
            boardRef = board2;
            colourBoardRef = colourBoard2;
        }

        for (int i = 0; i < fieldHeight; i++) {
            for (int j = 0; j < fieldWidth; j++) {
                if (boardRef[i][j]) {
                    int x = j * blockSize + displayPanel + xOffset;
                    int y = i * blockSize;

                    Color blockColor = colourBoardRef[i][j];

                    // Draw the main block color
                    g.setColor(blockColor);
                    g.fillRect(x, y, blockSize, blockSize);

                    drawEmbossedEffect(g, x, y, blockSize, blockColor);

                    // Draw internal grid lines for each block
                    g.setColor(Color.GRAY);
                    g.drawRect(x, y, blockSize, blockSize);
                }
            }
        }
    }

    // Method to draw the current falling block
    private void drawCurrentBlock(Graphics g, int player, int xOffset) {
        boolean[][] boardRef;
        Color[][] colourBoardRef;
        BlockModel currentBlockRef;
        int currentXRef;
        double currentYRef;
        if (player == 1) {
            boardRef = board;
            colourBoardRef = colourBoard;
            currentBlockRef = new BlockModel(currentBlock);
            currentXRef = currentX;
            currentYRef = currentY;
        } else {
            boardRef = board2;
            colourBoardRef = colourBoard2;
            currentBlockRef = new BlockModel(currentBlock2);
            currentXRef = currentX2;
            currentYRef = currentY2;
        }

        Color blockColor = currentBlockRef.getColour();
        for (int i = 0; i < currentBlockRef.getHeight(); i++) {
            for (int j = 0; j < currentBlockRef.getWidth(); j++) {
                if (currentBlockRef.getBlockShape()[i][j]) {
                    int x = (currentXRef + j) * blockSize + displayPanel + xOffset;
                    int y = (int) ((currentYRef + i) * blockSize);

                    // Draw the main block color
                    g.setColor(blockColor);
                    g.fillRect(x, y, blockSize, blockSize);

                    drawEmbossedEffect(g, x, y, blockSize, blockColor);

                    // Draw internal grid lines for each block
                    g.setColor(Color.GRAY);
                    g.drawRect(x, y, blockSize, blockSize);
                }
            }
        }
    }

    // Method to draw an embossed effect on blocks
    private void drawEmbossedEffect(Graphics g, int x, int y, int size, Color blockColor) {
        // Draw the highlight effect
        g.setColor(blockColor.brighter());
        g.drawLine(x, y, x + size - 1, y); // Top edge
        g.drawLine(x, y, x, y + size - 1); // Left edge

        // Draw the shadow effect
        g.setColor(blockColor.darker());
        g.drawLine(x + size - 1, y, x + size - 1, y + size - 1); // Right edge
        g.drawLine(x, y + size - 1, x + size - 1, y + size - 1); // Bottom edge
    }

    // Method to draw a centered message on the game field for game ended and paused
    // states
    private void drawCenteredMessage(Graphics g, String message, Color color, int fontSize, int xOffset, int yOffset) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f)); // Set transparency
        g2d.setFont(new Font("Arial", Font.BOLD, fontSize)); // Set font and size
        g2d.setColor(color); // Set color for the message

        FontMetrics metrics = g2d.getFontMetrics();
        int x = xOffset - metrics.stringWidth(message) / 2; // Calculate horizontal center
        int y = 50 + yOffset; // Vertical position with offset

        g2d.drawString(message, x, y); // Draw the message
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f)); // Reset transparency
    }

    // Draws a small black Arial message for the display panel for each player
    private void drawDisplayMessages(Graphics g, String message, int fontSize, int xOffset, int yOffset, Color colour) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setFont(new Font("Arial", Font.BOLD, fontSize)); // Set font and size
        // g2d.setColor(Color.BLACK);
        g2d.setColor(colour); // Set color for the message

        FontMetrics metrics = g2d.getFontMetrics();
        int x = xOffset; // Calculate horizontal center
        int y = 50 + yOffset; // Vertical position with offset

        g2d.drawString(message, x, y); // Draw the message
    }

    // Draws the next tetromino to be dropped
    private void drawNextBlock(Graphics g, int player, int xOffset, int yOffset){
        boolean[][] boardRef;
        Color[][] colourBoardRef;
        BlockModel currentBlockRef;
        int currentBlockIndexRef;
        int currentXRef;
        double currentYRef;
        if (player == 1){
            boardRef = board;
            colourBoardRef = colourBoard;
            currentBlockRef = new BlockModel(currentBlock);
            currentBlockIndexRef = currentBlockIndex;
            currentXRef = currentX;
            currentYRef = currentY;
        }
        else{
            boardRef = board2;
            colourBoardRef = colourBoard2;
            currentBlockRef = new BlockModel(currentBlock2);
            currentBlockIndexRef = currentBlockIndex2;
            currentXRef = currentX2;
            currentYRef = currentY2;
        }

        // Gets the next tetromino from the blockList
        int nextIndex = currentBlockIndexRef;
        BlockModel nextBlock = new BlockModel(blockList.get(nextIndex));
        Color blockColor = nextBlock.getColour();
        // Draws the next block
        for (int i = 0; i < nextBlock.getHeight(); i++) {
            for (int j = 0; j < nextBlock.getWidth(); j++) {
                if (nextBlock.getBlockShape()[i][j]) {
                    int x = j * blockSize + xOffset;
                    int y = i * blockSize + yOffset;

                    // Draw the main block color
                    g.setColor(blockColor);
                    g.fillRect(x, y, blockSize, blockSize);

                    drawEmbossedEffect(g, x, y, blockSize, blockColor);

                    // Draw internal grid lines for each block
                    g.setColor(Color.GRAY);
                    g.drawRect(x, y, blockSize, blockSize); // TODO: Consider combining this with similar code in drawBoard for DRYness
                }
            }
        }
    }

    // Method to handle key press events for game controls
    @Override
    public void keyPressed(KeyEvent e) {
        if (extendMode){
            // Player two - PAUSE
            if (e.getKeyCode() == KeyEvent.VK_Q) {
                if (isPausedPlayer2) {
                    resumeGame2();
                } else {
                    pauseGame2();
                }
                repaint();
                return;
            }

            // Player two - GAME CONTROLS
            if (!isPausedPlayer2 && !isGameEndedPlayer2) {
                int keyCode = e.getKeyCode();
                int newX = currentX2;
                double newY = currentY2;
                double oldY = currentY2;
                int oldConfigIndex = currentBlock2.rotationIndex;
                boolean[][] oldShape = currentBlock2.getBlockShape();
                boolean signal = false;

                switch (keyCode) {
                    case KeyEvent.VK_W -> {                                            // Rotate block
                        newX = newShapeX(2, newX);
                        newY = newShapeY(2, newY);
                        currentBlock2.setBlockShape(currentBlock2.nextShape());
                        if (!isValidPosition(2, newX, newY)) {
                            currentBlock2.setBlockShape(oldShape);
                            currentBlock2.rotationIndex = oldConfigIndex;
                        } else {
                            currentX2 = newX;
                            currentY2 = newY;
                        }
                        signal = true;
                        if (configurationSettings.isSoundEffectsEnabled()) {
                            soundEffectsPlayer.playRotateBlockSound();  // Play sound when rotating
                        }
                    }
                    case KeyEvent.VK_A -> {  // Move block left
                        newX--;
                        if (configurationSettings.isSoundEffectsEnabled()) {
                            soundEffectsPlayer.playMoveBlockSound();  // Play sound when moving left
                        }
                    }
                    case KeyEvent.VK_D -> {  // Move block right
                        newX++;
                        if (configurationSettings.isSoundEffectsEnabled()) {
                            soundEffectsPlayer.playMoveBlockSound();  // Play sound when moving right
                        }
                    }
                    case KeyEvent.VK_S -> {  // Move block down faster
                        newY += 0.5;
                        if (configurationSettings.isSoundEffectsEnabled()) {
                            soundEffectsPlayer.playMoveBlockSound();  // Play sound when moving down
                        }
                    }
                }

                if (isValidPosition(2, newX, newY) && !signal) {
                    currentX2 = newX;
                    if (currentY2 == oldY) {
                        currentY2 = newY;
                    }
                    repaint();
                } else if (keyCode == KeyEvent.VK_S && !signal) {
                    currentY2 = newY;
                    stopBlock(2);
                }
            }

            if (isGameEndedPlayer2) {
                repaint();
            }

        }

        // Music toggles
        if (!(isGameEndedPlayer1 && isGameEndedPlayer2)) {
            if (e.getKeyCode() == KeyEvent.VK_M) {
                if (configurationSettings.isMusicEnabled()) {
                    configurationSettings.setMusic(false);
                    musicPlayer.stopMusic();
                } else {
                    configurationSettings.setMusic(true);
                    musicPlayer.playMusic();
                }
                repaint();
                return;
            }

            // Sound effect toggle
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                if (configurationSettings.isSoundEffectsEnabled()) {
                    configurationSettings.setSoundEffects(false);
                } else {
                    configurationSettings.setSoundEffects(true);
                }
                repaint();
                return;
            }
        }

        // Handle pause and resume when 'P' is pressed
        if (e.getKeyCode() == KeyEvent.VK_P) {
            if (isPausedPlayer1) {
                resumeGame();
            } else {
                pauseGame();
            }
            repaint();
            return;
        }
        // Handle movement and rotation if the game is running
        if (!isPausedPlayer1 && !isGameEndedPlayer1) {
            int keyCode = e.getKeyCode();
            int newX = currentX;
            double newY = currentY;
            double oldY = currentY;
            int oldConfigIndex = currentBlock.rotationIndex;
            boolean[][] oldShape = currentBlock.getBlockShape();

            switch (keyCode) {
                case KeyEvent.VK_UP -> {                                            // Rotate block
                    newX = newShapeX(1, newX);
                    newY = newShapeY(1, newY);
                    currentBlock.setBlockShape(currentBlock.nextShape());
                    if (!isValidPosition(1, newX, newY)) {
                        currentBlock.setBlockShape(oldShape);
                        currentBlock.rotationIndex = oldConfigIndex;
                    } else {
                        currentX = newX;
                        currentY = newY;
                    }
                    if (configurationSettings.isSoundEffectsEnabled()) {
                        soundEffectsPlayer.playRotateBlockSound();  // Play sound when rotating
                    }
                    return;
                }
                case KeyEvent.VK_LEFT -> {  // Move block left
                    newX--;
                    if (configurationSettings.isSoundEffectsEnabled()) {
                        soundEffectsPlayer.playMoveBlockSound();  // Play sound when moving left
                    }
                }
                case KeyEvent.VK_RIGHT -> {  // Move block right
                    newX++;
                    if (configurationSettings.isSoundEffectsEnabled()) {
                        soundEffectsPlayer.playMoveBlockSound();  // Play sound when moving right
                    }
                }
                case KeyEvent.VK_DOWN -> {  // Move block down faster
                    newY += 0.5;
                    if (configurationSettings.isSoundEffectsEnabled()) {
                        soundEffectsPlayer.playMoveBlockSound();  // Play sound when moving down
                    }
                }
            }

            if (isValidPosition(1, newX, newY)) {
                currentX = newX;
                if (currentY == oldY) {
                    currentY = newY;
                }
                repaint();
            } else if (keyCode == KeyEvent.VK_DOWN) {
                currentY = newY;
                stopBlock(1);
            }
        }

        if (isGameEndedPlayer1) {
            repaint();
        }
    }

    // Method to handle key release events (required by KeyListener, but not used)
    @Override
    public void keyReleased(KeyEvent e) {
        // This method is only here because we implement KeyListener
    }

    // Method to handle key typed events (required by KeyListener, but not used)
    @Override
    public void keyTyped(KeyEvent e) {
        // This method is only here because we implement KeyListener
    }

    // Method to handle game updates via the timer
    @Override
    public void actionPerformed(ActionEvent e) {
        if (extendMode){
            if (!isPausedPlayer2 && !isGameEndedPlayer2) {                                            // Update the game only if it's not paused or ended
                requestFocus();

                double nonHumanDownSpeed = 0.0;

                if (!isHumanPlayer(2)) {
                    if (currentY2 > 2) { // Magic number
                        if (currentBlock2.getRotationIndex() != targetRotation2) { // rotate first
                            rotateBlockPlayer2();
                        } else if (currentX2 != targetX2) {
                            if (currentX2 < targetX2) {
                                currentX2++;
                            } else if (currentX2 > targetX2) {
                                currentX2--;
                            }
                        } else {
                            nonHumanDownSpeed = 0.2;
                        }
                    }
                }

                double speed = 0.02 + (gameLevelPlayer2 * 0.01);                               // Calculate the block drop speed (TODO: Confirm if this speed calculation is appropriate)
                double newY = currentY2 + speed + nonHumanDownSpeed;
                if (isValidPosition(2, currentX2, newY)) {
                    currentY2 = newY;
                } else {
                    currentY2 = newY;
                    stopBlock(2);                                                        // Stop the block when it can't move further down
                }
                repaint();
            }
        }
        if (!isPausedPlayer1 && !isGameEndedPlayer1) {                                            // Update the game only if it's not paused or ended
            requestFocus();
            double nonHumanDownSpeed = 0.0;
            if (!isHumanPlayer(1)) {
                if (currentY > 2) {
                if (currentBlock.getRotationIndex() != targetRotation) { // rotate first
                    rotateBlockPlayer1();
                } else if (currentX != targetX) {
                    if (currentX < targetX) {
                        currentX++;
                    } else if (currentX > targetX) {
                        currentX--;
                    }
                } else {
                    nonHumanDownSpeed = 0.2;
                }
                }
            }
            double speed = 0.02 + (gameLevelPlayer1 * 0.01);                               // Calculate the block drop speed (TODO: Confirm if this speed calculation is appropriate)
            double newY = currentY + speed + nonHumanDownSpeed;
            if (isValidPosition(1, currentX, newY)) {
                currentY = newY;
            } else {
                currentY = newY;
                stopBlock(1);                                                        // Stop the block when it can't move further down
            }
            repaint();
        }
    }

    // Method to pause the game
    public void pauseGame() {
        isPausedPlayer1 = true;
        if (isPausedPlayer2 || isGameEndedPlayer2) {
            if (configurationSettings.isMusicEnabled()) {
                musicPlayer.pauseMusic();                   // Pause music if both players are paused.
            }
        }
    }

    public void pauseGame2(){
        isPausedPlayer2 = true;
        if (isPausedPlayer1 || isGameEndedPlayer1) {
            if (configurationSettings.isMusicEnabled()) {
                musicPlayer.pauseMusic();                   // Pause music if both players are paused.
            }
        }
    }

    // Method to check if the game is paused
    public boolean isGamePaused() {
        return isPausedPlayer1;
    }

    public boolean isGamePaused2() {
        return isPausedPlayer2;
    }

    // Method to check if the game is over
    public boolean isGameOver() {
        return isGameEndedPlayer1;
    }

    public boolean isGameOver2() {
        return isGameEndedPlayer2;
    }

    // Method to reset the game state
    public void resetGame(int width, int height, int gameLevel, boolean music, boolean soundEffects, PlayerType typeOfPlayer1, PlayerType typeOfPlayer2, boolean extendMode) {
        this.fieldWidth = width;
        this.fieldHeight = height;
        this.gameLevelPlayer1 = gameLevel;
        this.initialGameLevel = gameLevel;
        this.music = music;
        this.soundEffects = soundEffects;
        this.typeOfPlayer1 = typeOfPlayer1;
        this.extendMode = extendMode;
        this.scorePlayer1 = 0;
        this.currentBlockIndex++; // Increments to a new block for the next game
        this.currentBlockIndex2 = currentBlockIndex; // Synchronizes the blocks if two player is enabled
        this.deletedRowsPlayer1 = 0;
        this.deletedRowsPlayer1Counter = 0;
        if (this.extendMode){
            this.gameLevelPlayer2 = gameLevel;
            this.typeOfPlayer2 = typeOfPlayer2;
            this.scorePlayer2 = 0;
            this.deletedRowsPlayer2 = 0;
            this.deletedRowsPlayer2Counter = 0;
            isPausedPlayer2 = false;
            isGameEndedPlayer2 = false;
            setPreferredSize(new Dimension(2*(width * blockSize + BORDER_THICKNESS * 2) + 2*displayPanel + 20,
                    height * blockSize + BORDER_THICKNESS * 2));
            board2 = new boolean[fieldHeight][fieldWidth];
            colourBoard2 = new Color[fieldHeight][fieldWidth];
            spawnBlock(2);
        }
        else{
            setPreferredSize(new Dimension(width * blockSize + BORDER_THICKNESS * 2 + displayPanel,
                    height * blockSize + BORDER_THICKNESS * 2));
        }
        isGameEndedPlayer1 = false;
        isPausedPlayer1 = false;
        gameOverSoundPlayedPlayer1 = false;
        gameOverSoundPlayedPlayer2 = false;
        board = new boolean[fieldHeight][fieldWidth];
        colourBoard = new Color[fieldHeight][fieldWidth];
        spawnBlock(1); // Start a new game with a new block
        repaint();
    }

    // Generates 100000 blocks that will be used for all games
    public List<BlockModel> generateBlocks(){
        List<BlockModel> blocks = new ArrayList<>();
        for (int i = 0; i<100000; i++){
            blocks.add(new BlockModel());
        }
        return blocks;
    }

    private void rotateBlockPlayer1(){ // Used For AI Mode
        int newX = currentX;
        double newY = currentY;
        int oldConfigIndex = currentBlock.rotationIndex;
        boolean[][] oldShape = currentBlock.getBlockShape();

        newX = newShapeX(1, newX);
        newY = newShapeY(1, newY);
        currentBlock.setBlockShape(currentBlock.nextShape());

        if (!isValidPosition(1, newX, newY)) {
            currentBlock.setBlockShape(oldShape);
            currentBlock.rotationIndex = oldConfigIndex;
        } else {
            currentX = newX;
            currentY = newY;
        }
    }

    private void rotateBlockPlayer2(){ // Used for AI Mode
        int newX = currentX2;
        double newY = currentY2;
        int oldConfigIndex = currentBlock2.rotationIndex;
        boolean[][] oldShape = currentBlock2.getBlockShape();

        newX = newShapeX(2, newX);
        newY = newShapeY(2, newY);
        currentBlock2.setBlockShape(currentBlock2.nextShape());

        if (!isValidPosition(2, newX, newY)) {
            currentBlock2.setBlockShape(oldShape);
            currentBlock2.rotationIndex = oldConfigIndex;
        } else {
            currentX2 = newX;
            currentY2 = newY;
        }
    }


    // Method to resume the game if it's not ended
    public void resumeGame() {
        if (!isGameEndedPlayer1) {
            isPausedPlayer1 = false;
            if (configurationSettings.isMusicEnabled()) {
                musicPlayer.playMusic();
            }
        }
    }

    public void resumeGame2() {
        if (!isGameEndedPlayer2) {
            isPausedPlayer2 = false;
            if (configurationSettings.isMusicEnabled()) {
                musicPlayer.playMusic();
            }
        }
    }

    public void endGame2() {
        isGameEndedPlayer2 = true;
        isPausedPlayer2 = true;
    }

    // Method to end the game
    public void endGame() {
        isGameEndedPlayer1 = true;
        isPausedPlayer1 = true;
    }

    // Method to check if the game has ended
    public boolean isGameEndedPlayer1() {
        return isGameEndedPlayer1;
    }

    public boolean isGameEndedPlayer2() {
        return isGameEndedPlayer2;
    }

    public int getScore() {
        return scorePlayer1;
    }

    public int getScore2() {
        return scorePlayer2;
    }

    public int getBlockSize() {
        return blockSize;
    }

    public boolean isExtendMode() {
        return extendMode;
    }

    public int getGameLevelPlayer1() {
        return gameLevelPlayer1;
    }

    public int getGameLevelPlayer2() {
        return gameLevelPlayer2;
    }

    public void setBoard(boolean[][] board){this.board = board;}

    public void setBoard2(boolean[][] board){this.board2 = board;}

    public void setCurrentBlock(BlockModel block){this.currentBlock = new BlockModel(block);}

    public void setCurrentBlock2(BlockModel block){this.currentBlock2 = new BlockModel(block);}

    public void setCurrentX(int x){this.currentX = x;}

    public void setCurrentY(double y){this.currentY = y;}

    public void setCurrentX2(int x){this.currentX2 = x;}

    public void setCurrentY2(double y){this.currentY2 = y;}

    // Method to return the game panel (this instance of GameManager)
    public JPanel getGamePanel() {
        return this;
    }

    private boolean isHumanPlayer(int player) {
        if (player == 1) {
            return ConfigurationModel.getInstance().getPlayer1Type() == PlayerType.HUMAN;
        } else {
            return ConfigurationModel.getInstance().getPlayer2Type() == PlayerType.HUMAN;
        }
    }

    private void requestMove(int playerNumber, boolean isExternalPlayer) {
        new Thread(() -> {
            // External player
            if (isExternalPlayer) {
                // Prepare the game state
                boolean[][] boardToUse = (playerNumber == 1) ? board : board2;
                BlockModel currentBlockToUse = (playerNumber == 1) ? currentBlock : currentBlock2;
                int nextIndex = (playerNumber == 1) ? currentBlockIndex : currentBlockIndex2;
                BlockModel nextBlock = blockList.get(nextIndex);
    
                PureGame game = tetrisClient.createPureGame(
                    fieldWidth,
                    fieldHeight,
                    boardToUse,
                    currentBlockToUse,
                    nextBlock
                );
    
                OpMove move = tetrisClient.getExternalMove(game);
                int opX = move.opX();
                int opRotate = move.opRotate();
                if (opX == -1 && opRotate == -1) {
                    int currentXRef = (playerNumber == 1) ? currentX : currentX2;
                    applyMove(playerNumber, currentXRef, 0); // Do nothing
                    if (playerNumber == 1) {
                        isDisconnectedPlayer1 = true;
                    } else if (playerNumber == 2) {
                        isDisconnectedPlayer2 = true;
                    }
                } else {
                    applyMove(playerNumber, opX, opRotate);
                    if (playerNumber == 1) {
                        isDisconnectedPlayer1 = false;
                    } else if (playerNumber == 2) {
                        isDisconnectedPlayer2 = false;
                    }
                }

            } 
            // AI
            else {
                int[] moveArray = aiManager.scanBoard(
                    (playerNumber == 1) ? board : board2,
                    (playerNumber == 1) ? currentBlock : currentBlock2
                );
                applyMove(playerNumber, moveArray[2], moveArray[1]);
            }
        }).start();
    }

    private void applyMove(int player, int targetX, int targetRotation) {
        if (player == 1) {
            this.targetX = targetX;
            this.targetRotation = targetRotation;
        } else {
            this.targetX2 = targetX;
            this.targetRotation2 = targetRotation;
        }
    }
}
