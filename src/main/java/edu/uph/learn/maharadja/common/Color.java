package edu.uph.learn.maharadja.common;

public enum Color {
  IMPERIAL_GOLD("#EFC14B"),
  IVORY_WHITE("#F5F1E8"),
  VOLCANIC_BLACK("#1C1C1C"),
  GUNMETAL_GREY("#2F2F2F"),
  SUNSET_RED("#CC3D3D"),
  SLATE_GRAY("#708090"),
  LIGHT_BLUE("#ADD8E6"),
  SKY_BLUE("#87CEEB"),
  ALICE_BLUE("#F0F8FF"),

  // For Players
  DEEP_MAROON("#6B1E1E"),
  JADE_GREEN("#1E7F5C"),
  ROYAL_INDIGO("#3B2E5A"),
  COCONUT_HUSK("#8B5E3C"),

  // For Regions
  BURNT_SIENNA("#D68A59"),
  PERSIAN_GREEN("#009B77"),
  DEEP_CERULEAN("#007BA7"),
  OLIVE_DRAB("#6B8E23"),
  COPPER_PENNY("#B87333"),
  AMARANTH_PURPLE("#AB274F"),
  DUSTY_ROSE("#C08081"),
  CADET_BLUE("#5F9EA0");

  private final String hex;
  private final javafx.scene.paint.Color color;

  Color(String hex) {
    this.hex = hex;
    this.color = javafx.scene.paint.Color.web(hex);
  }

  public String toHex() {
    return hex;
  }

  public javafx.scene.paint.Color get() {
    return color;
  }
}
