package edu.uph.learn.maharadja.common;

public enum Color {
  IMPERIAL_GOLD("#EFC14B"),
  IVORY_WHITE("#F5F1E8"),
  VOLCANIC_BLACK("#1C1C1C");

  private final javafx.scene.paint.Color hex;

  Color(String hex) {
    this.hex = javafx.scene.paint.Color.web(hex);
  }

  public javafx.scene.paint.Color get() {
    return hex;
  }
}
