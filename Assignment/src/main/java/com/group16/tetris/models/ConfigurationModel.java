package com.group16.tetris.models;

// Manages game configuration settings such as field size, game level, and player types
public class ConfigurationModel {

    private static ConfigurationModel instance;  // Singleton instance

    // Enum for different player types
    public enum PlayerType {
        HUMAN(1),
        AI(2),
        EXTERNAL(3);

        private int value;

        PlayerType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static PlayerType fromValue(int value) {
            for (PlayerType type : PlayerType.values()) {
                if (type.getValue() == value) {
                    return type;
                }
            }
            return HUMAN;  // Default to HUMAN if value not found
        }
    }

    // Default configuration settings
    private static final int DEFAULT_FIELD_WIDTH = 10;
    private static final int DEFAULT_FIELD_HEIGHT = 20;
    private static final int DEFAULT_GAME_LEVEL = 1;
    private static final boolean DEFAULT_MUSIC = true;
    private static final boolean DEFAULT_SOUND_EFFECTS = true;
    private static final boolean DEFAULT_EXTEND_MODE = false;
    private static final PlayerType DEFAULT_PLAYER1_TYPE = PlayerType.HUMAN;
    private static final PlayerType DEFAULT_PLAYER2_TYPE = PlayerType.HUMAN;

    // Current configuration settings
    private int fieldWidth;
    private int fieldHeight;
    private int gameLevel;
    private boolean music;
    private boolean soundEffects;
    private boolean extendMode;
    private PlayerType player1Type;
    private PlayerType player2Type;

    // Private constructor to initialise configuration with default values
    private ConfigurationModel() {
        resetToDefaults();
    }

    // Returns the singleton instance of ConfigurationModel
    public static ConfigurationModel getInstance() {
        if (instance == null) {
            synchronized (ConfigurationModel.class) {
                if (instance == null) {
                    instance = new ConfigurationModel();
                }
            }
        }
        return instance;
    }

    // Resets all configuration settings to their default values
    public void resetToDefaults() {
        this.fieldWidth = DEFAULT_FIELD_WIDTH;
        this.fieldHeight = DEFAULT_FIELD_HEIGHT;
        this.gameLevel = DEFAULT_GAME_LEVEL;
        this.music = DEFAULT_MUSIC;
        this.soundEffects = DEFAULT_SOUND_EFFECTS;
        this.extendMode = DEFAULT_EXTEND_MODE;
        this.player1Type = DEFAULT_PLAYER1_TYPE;
        this.player2Type = DEFAULT_PLAYER2_TYPE;
    }

    // Loads configuration settings, including handling of different player type values
    public void loadSettings(int width, int height, int gameLevel, boolean music, boolean soundEffects, boolean extendMode, Object player1TypeValue, Object player2TypeValue) {
        this.fieldWidth = width;
        this.fieldHeight = height;
        this.gameLevel = gameLevel;
        this.music = music;
        this.soundEffects = soundEffects;
        this.extendMode = extendMode;

        if (player1TypeValue instanceof Integer) {
            this.player1Type = PlayerType.fromValue((int) player1TypeValue);
        } else if (player1TypeValue instanceof PlayerType) {
            this.player1Type = (PlayerType) player1TypeValue;
        }

        if (player2TypeValue instanceof Integer) {
            this.player2Type = PlayerType.fromValue((int) player2TypeValue);
        } else if (player2TypeValue instanceof PlayerType) {
            this.player2Type = (PlayerType) player2TypeValue;
        }
    }

    // Getter and setter methods for configuration settings
    public int getFieldWidth() {
        return fieldWidth;
    }

    public ConfigurationModel setFieldWidth(int fieldWidth) {
        this.fieldWidth = fieldWidth;
        return this;
    }

    public int getFieldHeight() {
        return fieldHeight;
    }

    public ConfigurationModel setFieldHeight(int fieldHeight) {
        this.fieldHeight = fieldHeight;
        return this;
    }

    public int getGameLevel() {
        return gameLevel;
    }

    public ConfigurationModel setGameLevel(int gameLevel) {
        this.gameLevel = gameLevel;
        return this;
    }

    public boolean isMusicEnabled() {
        return music;
    }

    public ConfigurationModel setMusic(boolean music) {
        this.music = music;
        return this;
    }

    public boolean isSoundEffectsEnabled() {
        return soundEffects;
    }

    public ConfigurationModel setSoundEffects(boolean soundEffects) {
        this.soundEffects = soundEffects;
        return this;
    }

    public boolean isExtendModeEnabled() {
        return extendMode;
    }

    public ConfigurationModel setExtendMode(boolean extendMode) {
        this.extendMode = extendMode;
        return this;
    }

    public PlayerType getPlayer1Type() {
        return player1Type;
    }

    public void setPlayer1Type(PlayerType player1Type) {
        this.player1Type = player1Type;
    }

    public PlayerType getPlayer2Type() {
        return player2Type;
    }

    public void setPlayer2Type(PlayerType player2Type) {
        this.player2Type = player2Type;
    }
}
