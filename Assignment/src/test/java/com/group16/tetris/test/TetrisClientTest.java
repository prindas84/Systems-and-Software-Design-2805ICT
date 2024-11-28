package com.group16.tetris.test;

import com.group16.tetris.models.BlockModel;
import com.group16.tetris.models.OpMove;
import com.group16.tetris.models.PureGame;
import com.group16.tetris.services.TetrisClient;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.IOException;
import java.net.Socket;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TetrisClientTest {

    private TetrisClient tetrisClient;
    private Socket mockSocket;
    private PrintWriter mockWriter;
    private BufferedReader mockReader;

    @BeforeEach
    void setUp() {
        tetrisClient = new TetrisClient();
        mockSocket = mock(Socket.class);
        mockWriter = mock(PrintWriter.class);
        mockReader = mock(BufferedReader.class);
    }

    @Test
    void testDefaultConstructor() {
        TetrisClient client = new TetrisClient();
        assertEquals("localhost", client.getServerHost());
        assertEquals(3000, client.getServerPort());
    }

    @Test
    void testParameterizedConstructor() {
        String customHost = "127.0.0.1";
        int customPort = 4000;
        TetrisClient client = new TetrisClient(customHost, customPort);
        assertEquals(customHost, client.getServerHost());
        assertEquals(customPort, client.getServerPort());
    }

    @Test
    void testCreatePureGame() {
        int fieldWidth = 10;
        int fieldHeight = 20;
        boolean[][] boardToUse = new boolean[fieldHeight][fieldWidth];
        BlockModel currentBlock = new BlockModel();
        BlockModel nextBlock = new BlockModel();

        PureGame game = tetrisClient.createPureGame(fieldWidth, fieldHeight, boardToUse, currentBlock, nextBlock);

        assertNotNull(game);
        assertEquals(fieldWidth, game.getWidth());
        assertEquals(fieldHeight, game.getHeight());
        assertNotNull(game.getCells());
        assertNotNull(game.getCurrentShape());
        assertNotNull(game.getNextShape());
    }

    @Test
    // Indirect testing of convertBlockTo2DArray (private method)
    void testCreatePureGameWithBlockShapes() {
        int fieldWidth = 10;
        int fieldHeight = 20;
        boolean[][] boardToUse = new boolean[fieldHeight][fieldWidth];
        boardToUse[0][0] = true; // Set some cells to true
        boardToUse[1][1] = true;

        boolean[][] currentBlockShape = {
            { true, false },
            { true, true }
        };
        BlockModel currentBlock = new BlockModel();
        currentBlock.setBlockShape(currentBlockShape);

        boolean[][] nextBlockShape = {
            { false, true },
            { true, true }
        };
        BlockModel nextBlock = new BlockModel();
        nextBlock.setBlockShape(nextBlockShape);

        PureGame game = tetrisClient.createPureGame(fieldWidth, fieldHeight, boardToUse, currentBlock, nextBlock);

        assertNotNull(game);
        assertEquals(fieldWidth, game.getWidth());
        assertEquals(fieldHeight, game.getHeight());
        assertEquals(1, game.getCells()[0][0]);
        assertEquals(1, game.getCells()[1][1]);
        assertEquals(0, game.getCells()[0][1]);

        // Verify currentShape
        assertArrayEquals(new int[][] { {1, 0}, {1, 1} }, game.getCurrentShape());

        // Verify nextShape
        assertArrayEquals(new int[][] { {0, 1}, {1, 1} }, game.getNextShape());
    }


    @Test
    void testGetExternalMove() throws Exception {
        PureGame mockGame = new PureGame();
        mockGame.setWidth(10);
        mockGame.setHeight(20);

        // Mock socket and input/output streams
        when(mockSocket.getOutputStream()).thenReturn(mock(OutputStream.class));
        
        // Mocking input stream to return the expected JSON response for OpMove
        String jsonResponse = "{\"opX\":1,\"opRotate\":2}";
        when(mockSocket.getInputStream()).thenReturn(new ByteArrayInputStream(jsonResponse.getBytes()));

        // Inject mockSocket into the method
        OpMove move = tetrisClient.getExternalMove(mockGame, mockSocket);

        assertNotNull(move);
        assertEquals(1, move.opX());
        assertEquals(2, move.opRotate());
    }

    @Test
    void testGetExternalMoveWithError() throws Exception {
        PureGame mockGame = new PureGame();
        mockGame.setWidth(10);
        mockGame.setHeight(20);
    
        // Mock the output stream to return a valid OutputStream mock
        when(mockSocket.getOutputStream()).thenReturn(mock(OutputStream.class));
    
        // Simulate an IOException when trying to get the input stream
        when(mockSocket.getInputStream()).thenThrow(new IOException());
    
        OpMove move = tetrisClient.getExternalMove(mockGame, mockSocket);
    
        // Verify the default move (should return default values -1 and -1 due to IOException)
        assertNotNull(move);
        assertEquals(-1, move.opX());
        assertEquals(-1, move.opRotate());
    }

    @Test
    void testGetExternalMoveWithoutSocket() {
        // This method could not be tested because it creates a real socket connection,
        // which makes it difficult to mock without changing the class structure.
        // Ideally, the TetrisClient class should've be refactored to allow
        // dependency injection of the Socket creation logic.
    }
}
