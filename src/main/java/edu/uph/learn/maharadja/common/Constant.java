package edu.uph.learn.maharadja.common;

import javafx.scene.text.Font;

public class Constant {
  private Constant() {}

  public static final int MAX_PLAYERS = 4;
  public static final String LOBBY_FULL = "LOBBY_FULL";
  public static final int INITIAL_TROOP_PER_PLAYER = 40;

  //region UI Constants
  public static final Font SMALL_FONT = new Font("Verdana Bold", 12);
  public static final Font LARGE_FONT = new Font("Verdana Bold", 16);

  public static final int DEFAULT_WIDTH = 1920/2;
  public static final int DEFAULT_HEIGHT = 1080/2;
  public static final double DEFAULT_SPACING = 8;
  public static final double HEIGHT_SMALL = 24.0;
  public static final double HEIGHT_NORMAL = 32.0;
  public static final double HEIGHT_LARGE = 48.0;
  //endregion
}
