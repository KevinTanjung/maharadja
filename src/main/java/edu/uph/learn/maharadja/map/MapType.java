package edu.uph.learn.maharadja.map;

public enum MapType {
  CLASSIC("classic.json", "DOT"),
  HEX("hex.json", "HEX");

  private final String fileName;
  private final String tile;

  MapType(String fileName, String tile) {
    this.fileName = fileName;
    this.tile = tile;
  }

  public String getFileName() {
    return fileName;
  }

  public String getTile() {
    return tile;
  }
}
