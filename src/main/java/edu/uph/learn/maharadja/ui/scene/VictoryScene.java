package edu.uph.learn.maharadja.ui.scene;

import edu.uph.learn.maharadja.common.Color;
import edu.uph.learn.maharadja.ui.GameWindow;

public class VictoryScene extends EndScene {
  public VictoryScene(GameWindow gameWindow) {
    super(gameWindow);
  }

  @Override
  public String getSplashImage() {
    return "assets/victory.png";
  }

  @Override
  public Color getBackgroundColor() {
    return Color.IMPERIAL_GOLD;
  }

  @Override
  public LogoColor getLogoColor() {
    return LogoColor.VOLCANIC_BLACK;
  }
}
