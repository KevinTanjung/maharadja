package edu.uph.learn.maharadja.map;

public enum MapType {
  CLASSIC("classic.json");

  private final String fileName;

  MapType(String fileName) {
    this.fileName = fileName;
  }

  public String getFileName() {
    return fileName;
  }
}
