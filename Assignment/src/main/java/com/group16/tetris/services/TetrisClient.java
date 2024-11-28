package com.group16.tetris.services;

import com.group16.tetris.models.BlockModel;
import com.group16.tetris.models.OpMove;
import com.group16.tetris.models.PureGame;
import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;

public class TetrisClient {
    private static final String DEFAULT_SERVER_HOST = "localhost";
    private static final int DEFAULT_SERVER_PORT = 3000;

    private final String serverHost; // had to refactor this to pass in a mocked socket for testing
    private final int serverPort;

    public TetrisClient() {
        this.serverHost = DEFAULT_SERVER_HOST; // Default host
        this.serverPort = DEFAULT_SERVER_PORT; // Default port
    }

    public TetrisClient(String serverHost, int serverPort) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    public PureGame createPureGame(int fieldWidth, int fieldHeight, boolean[][] boardToUse,
                                   BlockModel currentBlockToUse, BlockModel nextBlock) {
        PureGame game = new PureGame();

        game.setWidth(fieldWidth);
        game.setHeight(fieldHeight);

        // Set cells (the current state of the board)
        int[][] cells = new int[fieldHeight][fieldWidth];
        for (int i = 0; i < fieldHeight; i++) {
            for (int j = 0; j < fieldWidth; j++) {
                cells[i][j] = boardToUse[i][j] ? 1 : 0;
            }
        }
        game.setCells(cells);

        // Set currentShape
        game.setCurrentShape(convertBlockTo2DArray(currentBlockToUse.getBlockShape()));

        // Set nextShape
        game.setNextShape(convertBlockTo2DArray(nextBlock.getBlockShape()));

        return game;
    }

    private int[][] convertBlockTo2DArray(boolean[][] blockShape) {
        int rows = blockShape.length;
        int cols = blockShape[0].length;
        int[][] shapeArray = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                shapeArray[i][j] = blockShape[i][j] ? 1 : 0;
            }
        }
        return shapeArray;
    }

    public OpMove getExternalMove(PureGame game) {
        try (Socket socket = new Socket(serverHost, serverPort)) {
            return handleExternalMove(game, socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new OpMove(-1, -1); // Provide default values
    }

    public OpMove getExternalMove(PureGame game, Socket socket) {
        return handleExternalMove(game, socket);
    }

    private OpMove handleExternalMove(PureGame game, Socket socket) { // Combined the joint functionality from the two methods previously
        try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Convert PureGame object to JSON
            Gson gson = new Gson();
            String jsonGameState = gson.toJson(game);

            // Send the game state to the server
            out.println(jsonGameState);

            // Wait for the server's response (OpMove)
            String response = in.readLine();

            // Convert the JSON response to an OpMove object
            return gson.fromJson(response, OpMove.class);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new OpMove(-1, -1); // Provide default values
    }

    public String getServerHost() {
        return serverHost;
    }
    
    public int getServerPort() {
        return serverPort;
    }
}