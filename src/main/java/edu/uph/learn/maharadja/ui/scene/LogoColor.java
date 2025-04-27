package edu.uph.learn.maharadja.ui.scene;

public enum LogoColor {
  IMPERIAL_GOLD("assets/logo_gold.png"),
  IVORY_WHITE("assets/logo_white.png"),
  VOLCANIC_BLACK("assets/logo_black.png");

  private final String path;

  LogoColor(String path) {
    this.path = path;
  }

  public String getPath() {
    return path;
  }
}
