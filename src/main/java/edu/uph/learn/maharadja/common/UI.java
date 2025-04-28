package edu.uph.learn.maharadja.common;

import javafx.scene.text.Font;

public class UI {

  private UI() {}

  public static final double UNIT = 8;
  public static final double SMALL = UNIT / 2;
  public static final double MEDIUM = UNIT * 2;
  public static final double LARGE = UNIT * 3;
  public static final double EXTRA_LARGE = UNIT * 4;

  public static final double WIDTH = 1280;
  public static final double HEIGHT = 720;
  public static final double FORM_WIDTH = 420;
  public static final double TAB_WIDTH = WIDTH / 4;
  public static final double ACTION_PANE_HEIGHT = HEIGHT / 2;

  public static final double MIN_SCALE = 1;
  public static final double MAX_SCALE = 2;
  public static final double HEX_SIZE = UNIT * 5;
  public static final double ZOOM_STEP = 1.01;

  public static final Font SMALL_FONT = new Font("Verdana Bold", 12);
  public static final Font LARGE_FONT = new Font("Verdana Bold", 16);
  public static final Font EXTRA_LARGE_FONT = new Font("Verdana Bold", 32);
}
