package com.group16.tetris.models;

import java.awt.*;
import java.util.Random;

public class BlockModel {

    // Represents the different shapes a block can have
    public enum ShapeType {
        STRAIGHT, SQUARE, Z_SHAPE, REVERSE_Z_SHAPE, L_SHAPE, REVERSE_L_SHAPE
    }

    private boolean[][] blockShape;  // Current shape of the block
    private Color colour;  // Colour of the block
    private ShapeType shapeType;  // Type of shape
    public int rotationIndex;  // Current rotation of the shape

    // All possible shapes and their rotations
    public static final boolean[][][][] SHAPES = {
            {
                    // Straight shape
                    {
                            {true, true, true, true}
                    },
                    {
                            {true},
                            {true},
                            {true},
                            {true}
                    },
                    {
                            {true, true, true, true}
                    },
                    {
                            {true},
                            {true},
                            {true},
                            {true}
                    }
            },
            // Square shape
            {
                    {
                            {true, true},
                            {true, true}
                    },
                    {
                            {true, true},
                            {true, true}
                    },
                    {
                            {true, true},
                            {true, true}
                    },
                    {
                            {true, true},
                            {true, true}
                    }
            },
            // Z shape
            {
                    {
                            {true, true, false},
                            {false, true, true}
                    },
                    {
                            {false, true},
                            {true, true},
                            {true, false}
                    },
                    {
                            {true, true, false},
                            {false, true, true}
                    },
                    {
                            {false, true},
                            {true, true},
                            {true, false}
                    }
            },
            // Reverse Z shape
            {
                    {
                            {false, true, true},
                            {true, true, false}
                    },
                    {
                            {true, false},
                            {true, true},
                            {false, true}
                    },
                    {
                            {false, true, true},
                            {true, true, false}
                    },
                    {
                            {true, false},
                            {true, true},
                            {false, true}
                    }
            },
            // L shape
            {
                    {
                            {true, true, true},
                            {true, false, false}
                    },
                    {
                            {true, true},
                            {false, true},
                            {false, true}
                    },
                    {
                            {false, false, true},
                            {true, true, true}
                    },
                    {
                            {true, false},
                            {true, false},
                            {true, true}
                    }
            },
            // Reverse L shape
            {
                    {
                            {true, true, true},
                            {false, false, true}
                    },
                    {
                            {false, true},
                            {false, true},
                            {true, true}
                    },
                    {
                            {true, false, false},
                            {true, true, true}
                    },
                    {
                            {true, true},
                            {true, false},
                            {true, false}
                    }
            }
    };

    // Colours corresponding to each shape type
    private static final Color[] SHAPE_COLOURS = {
            new Color(173, 216, 230), // Light blue for straight
            Color.YELLOW, // Square
            Color.GREEN, // Z shape
            Color.RED, // Reverse Z shape
            Color.ORANGE, // L shape
            new Color(0, 0, 139) // Deep blue for reverse L shape
    };

    private static final Random RANDOM = new Random();

    // Constructor that randomly assigns a shape and colour to the block
    public BlockModel(){
        this.shapeType = ShapeType.values()[RANDOM.nextInt(ShapeType.values().length)];
        this.rotationIndex = 0;
        this.blockShape = SHAPES[shapeType.ordinal()][rotationIndex];
        this.colour = SHAPE_COLOURS[shapeType.ordinal()];
    }

    // Copy constructor that creates a block based on another block's properties
    public BlockModel(BlockModel block){
        if (block == null){
            return;
        }
        this.shapeType = block.shapeType;
        this.rotationIndex = block.getRotationIndex();
        this.blockShape = block.getBlockShape();
        this.colour = block.getColour();
    }

    // Rotates the block to the next shape configuration
    public boolean[][] nextShape() {
        rotationIndex = (rotationIndex + 1) % 4;
        blockShape = SHAPES[shapeType.ordinal()][rotationIndex];
        return blockShape;
    }

    // Getter methods for block properties
    public Color getColour() {
        return colour;
    }

    public int getHeight(){
        return blockShape.length;
    }

    public int getWidth(){
        return blockShape[0].length;
    }

    public boolean[][] getBlockShape() {
        return blockShape;
    }

    public ShapeType getShapeType() {
        return shapeType;
    }

    public int getRotationIndex() {
        return rotationIndex;
    }

    // Setter methods for block shape and rotation
    public void setBlockShape(boolean[][] shape) {
        this.blockShape = shape;
    }

    public void setBlockShape(boolean[][] shape, int rotationIndex){
        this.blockShape = shape;
        this.rotationIndex = rotationIndex;
    }

    public void setBlockShape(boolean[][] shape, ShapeType type, int rotationIndex){
        this.blockShape = shape;
        this.shapeType = type;
        this.rotationIndex = rotationIndex;
    }
}
