package edu.uph.learn.maharadja.ui.scene;

import edu.uph.learn.maharadja.common.Color;
import edu.uph.learn.maharadja.common.Constant;
import edu.uph.learn.maharadja.game.GameResult;
import edu.uph.learn.maharadja.ui.GameWindow;
import edu.uph.learn.maharadja.ui.TextResource;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

public class DefeatScene extends EndScene {
  public DefeatScene(GameWindow gameWindow) {
    super(gameWindow);
  }

  @Override
  public Color getBackgroundColor() {
    return Color.VOLCANIC_BLACK;
  }

  @Override
  public LogoColor getLogoColor() {
    return LogoColor.IVORY_WHITE;
  }

  @Override
  public String getSplashImage() {
    return "assets/defeat.png";
  }
}
