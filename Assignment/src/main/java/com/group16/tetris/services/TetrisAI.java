package com.group16.tetris.services;

import com.group16.tetris.models.BlockModel;
import com.group16.tetris.models.GameModel;

import javax.swing.*;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class TetrisAI {

    public int[] scanBoard(boolean[][] board, BlockModel block) {
        // Number of time a shape can be rotated. 3 times for each shape that isn't square
        int maxRotations = (block.getShapeType() == BlockModel.ShapeType.SQUARE) ? 0 : 3;
        
        int[] highestScore = new int[4];                                        // Index 0: score, 1: rotation, 2: x, 3: y
        highestScore[0] = Integer.MIN_VALUE;                                    // Start with the lowest possible score
        boolean[][] shape = block.getBlockShape();                              // Set the shape as a boolean
        
        // Loop for the number of maximum rotations
        for (int rotation = 0; rotation <= maxRotations; rotation++) {
            for (int y = board.length - 1; y >= 0; y--) {                       // Start from the last row and move upwards
                boolean rowIsEmpty = true;                                      // Assume the row is empty at the start
                for (int x = 0; x < board[0].length; x++) {                     // Iterate through each column
                    if (board[y][x]) {
                        rowIsEmpty = false;                                     // If we find a true in this row, the row is not empty
                    }
                    // Call the isValidPlacement method for the current (x, y) position
                    if (isValidPlacement(board, shape, x, y)) {
                        // Get the score for the position
                        int score = scorePosition(board, shape, x, y);
                        // If the score is higher than the previous, store it as the new highest
                        if (score > highestScore[0]) {
                            highestScore[0] = score;
                            highestScore[1] = rotation;
                            highestScore[2] = x;
                            highestScore[3] = y;
                        }
                    }
                }
                // If the entire row is empty, and it's not the last row, stop rotation and scanning
                if (rowIsEmpty && y < board.length - 1) {
                    break;
                }
            }
            // Rotate the block to the next shape, except for the last rotation
            if (rotation < maxRotations) {
                shape = rotateShape(shape);
            }
        }
        return highestScore;
    }
    
    public boolean isValidPlacement(boolean[][] board, boolean[][] shape, int x, int y) {
        int shapeHeight = shape.length;
        int shapeWidth = shape[0].length;
        boolean hasSupport = false;                                                             // Set a flag to check if there's at least one block below the shape

        // Loop through the shape starting from the bottom row, left to right, moving upwards
        for (int i = shapeHeight - 1; i >= 0; i--) {                                            // Start from the bottom row of the shape
            for (int j = 0; j < shapeWidth; j++) {                                              // Move left to right across the row
                if (shape[i][j]) {                                                              // If this part of the shape is filled
                    int boardX = x + j;                                                         // Calculate the corresponding X position on the board
                    int boardY = y - (shapeHeight - 1 - i);                                     // Calculate the corresponding Y position on the board

                    // Check if the board position is out of bounds
                    if (boardX < 0 || boardX >= board[0].length || boardY < 0 || boardY >= board.length) {
                        return false;
                    }

                    // Check if the board position is already occupied by another block
                    if (board[boardY][boardX]) {
                        return false;
                    }

                    // Check if there's support beneath the current block part
                    if (boardY + 1 < board.length && board[boardY + 1][boardX]) {
                        hasSupport = true;  // There is a block beneath, so the shape has support
                    }

                    // Edge case: If this block is on the last row, it is considered supported by the floor
                    if (boardY + 1 >= board.length) {
                        hasSupport = true;  // The block is on the last row, so it has support
                    }
                }
            }
        }
        // Return true only if the block has at least one supporting block underneath it
        return hasSupport;
    }

    public boolean[][] rotateShape(boolean[][] shape) {
        int shapeHeight = shape.length;
        int shapeWidth = shape[0].length;

        // Create a new array with dimensions swapped
        boolean[][] rotatedShape = new boolean[shapeWidth][shapeHeight];

        // Rotate the shape clockwise
        for (int i = 0; i < shapeHeight; i++) {
            for (int j = 0; j < shapeWidth; j++) {
                rotatedShape[j][shapeHeight - 1 - i] = shape[i][j];             // Transpose and reverse rows
            }
        }
        return rotatedShape;
    }


    public int scorePosition(boolean[][] board, boolean[][] shape, int x, int y) {
        int boardHeight = board.length;
        int boardWidth = board[0].length;
        int shapeHeight = shape.length;
        int shapeWidth = shape[0].length;

        int clearedLines = 0;
        int supportingBlocks = 0;
        int emptySpacesBelow = 0;

        // Create a temporary board to simulate the placement of the piece to check for cleared lines
        boolean[][] tempBoard = new boolean[boardHeight][boardWidth];

        // Copy the current board into the tempBoard
        for (int i = 0; i < boardHeight; i++) {
            System.arraycopy(board[i], 0, tempBoard[i], 0, boardWidth);
        }

        // Place the shape on the temporary board
        for (int i = shapeHeight - 1; i >= 0; i--) {
            for (int j = 0; j < shapeWidth; j++) {
                if (shape[i][j]) {
                    int boardX = x + j;
                    int boardY = y - (shapeHeight - 1 - i);
                    // Place the shape on the temp board
                    if (boardY >= 0 && boardY < boardHeight && boardX >= 0 && boardX < boardWidth) {
                        tempBoard[boardY][boardX] = true;
                    }
                }
            }
        }

        // Check that no blocks are above the shape that will block future shapes above.
        for (int i = 0; i < shapeWidth; i++) {
            int boardX = x + i;
            for (int j = y - shapeHeight; j >= 0; j--) {
                if (board[j][boardX]) {                                         // Use the original board so shape piece aren't counted
                    return 0;                                                   // Return a 0 score if there are blocks above.
                }
            }
        }

        // Check the position for supporting pieces below or empty spaces below
        for (int i = shapeHeight - 1; i >= 0; i--) {
            for (int j = 0; j < shapeWidth; j++) {
                if (shape[i][j]) {
                    int boardX = x + j;
                    int boardY = y - (shapeHeight - 1 - i);

                    // Check for supporting blocks, walls, or floor (directly adjacent or below)
                    if (boardY + 1 == boardHeight || board[boardY + 1][boardX]) {
                        supportingBlocks++;                                                         // Floor or block below
                    } else if (boardY + 1 < boardHeight && !tempBoard[boardY + 1][boardX]) {
                        emptySpacesBelow++;                                                         // Count empty spaces below the shape to deduct points
                    }

                    // Left (wall or block)
                    if (boardX - 1 < 0 || board[boardY][boardX - 1]) {
                        supportingBlocks++;                                                         // Wall or block to the left
                    }

                    // Right (wall or block)
                    if (boardX + 1 >= boardWidth || board[boardY][boardX + 1]) {
                        supportingBlocks++;                                                         // Wall or block to the right
                    }
                }
            }
        }

        // Check for cleared lines in the tempBoard
        for (int row = 0; row < boardHeight; row++) {
            boolean rowIsFull = true;
            for (int col = 0; col < boardWidth; col++) {
                if (!tempBoard[row][col]) {
                    rowIsFull = false;
                    break;
                }
            }
            if (rowIsFull) {
                clearedLines++;
            }
        }

        // Calculate the score and return it
        return (clearedLines * 300) + (supportingBlocks * 50) - (((boardHeight - y) * 10) + (emptySpacesBelow * 20));
    }
}
