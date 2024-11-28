package com.group16.tetris.models;

import java.util.Arrays;

public class PureGame {
    private int width;
    private int height;
    private int[][] cells;
    private int[][] currentShape;
    private int[][] nextShape;

    // Provides a string representation of the game state, including dimensions and shapes
    @Override
    public String toString() {
        return "PureGame{" +
                "width=" + width +
                ", height=" + height +
                ", cells=" + Arrays.deepToString(cells) +
                ", currentShape=" + Arrays.deepToString(currentShape) +
                ", nextShape=" + Arrays.deepToString(nextShape) +
                '}';
    }

    // Setters for game properties
    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setCells(int[][] cells) {
        this.cells = cells;
    }

    public void setCurrentShape(int[][] currentShape) {
        this.currentShape = currentShape;
    }

    public void setNextShape(int[][] nextShape) {
        this.nextShape = nextShape;
    }

    // Getters for game properties
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int[][] getCells() {
        return cells;
    }

    public int[][] getCurrentShape() {
        return currentShape;
    }

    public int[][] getNextShape() {
        return nextShape;
    }
}
