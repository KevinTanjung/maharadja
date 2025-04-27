package edu.uph.learn.maharadja.ui.scene;

import edu.uph.learn.maharadja.common.Color;
import edu.uph.learn.maharadja.common.UI;
import edu.uph.learn.maharadja.utils.UIUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;

import static edu.uph.learn.maharadja.common.UI.FORM_WIDTH;

public abstract class SceneWithLogo extends Scene {
  public SceneWithLogo() {
    super(new BorderPane(), UI.WIDTH, UI.HEIGHT);
    setFill(getBackgroundColor().get());
    renderLogo();
    BorderPane root = ((BorderPane) getRoot());
    root.setPadding(new Insets(32, 0, 0, 0));
    root.setBackground(Background.fill(getBackgroundColor().get()));
  }

  public abstract Color getBackgroundColor();

  public abstract LogoColor getLogoColor();

  public void renderLogo() {
    ImageView imageView = new ImageView(getLogoColor().getPath());
    imageView.setFitWidth(FORM_WIDTH / 2);
    imageView.setFitHeight(FORM_WIDTH / 2);
    imageView.setPickOnBounds(true);
    imageView.setPreserveRatio(true);
    BorderPane.setAlignment(imageView, Pos.CENTER);
    ((BorderPane) getRoot()).setTop(imageView);
  }
}
