package edu.uph.learn.maharadja.ui.components;

import edu.uph.learn.maharadja.ui.state.DialogTask;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Popup;
import javafx.stage.Window;
import javafx.util.Duration;

public class GameDialog extends Popup {
  private final StackPane root;
  private final SequentialTransition sequence;

  public GameDialog(DialogTask task, Runnable dialogStateCallback) {
    Label label = new Label(task.title());
    label.setStyle(
        "-fx-background-color: rgba(0, 0, 0, 0.75);" +
            "-fx-text-fill: white;" +
            "-fx-padding: 10px 20px;" +
            "-fx-border-radius: 10px;" +
            "-fx-background-radius: 10px;" +
            "-fx-font-size: 14px;"
    );
    label.setOpacity(0);
    root = new StackPane(label);
    root.setStyle("-fx-padding: 10px;");
    getContent().add(root);
    setAutoFix(true);
    setAutoHide(true);
    setHideOnEscape(true);

    FadeTransition fadeIn = new FadeTransition(Duration.millis(500), label);
    fadeIn.setFromValue(0);
    fadeIn.setToValue(1);
    PauseTransition stayOn = new PauseTransition(Duration.seconds(task.autoDismissSeconds().orElse(2)));
    FadeTransition fadeOut = new FadeTransition(Duration.millis(500), label);
    fadeOut.setFromValue(1);
    fadeOut.setToValue(0);

    fadeIn.setOnFinished(e -> {
      task.callback().run();
    });
    fadeOut.setOnFinished(e -> {
      hide();
      dialogStateCallback.run();
    });

    sequence = new SequentialTransition(fadeIn, stayOn, fadeOut);
  }

  @Override
  public void show(Window window) {
    Scene scene = window.getScene();
    setX(window.getX() + scene.getWidth() / 2 - root.getWidth() / 2);
    setY(window.getY() + scene.getHeight() - 100);
    sequence.play();
    super.show(window);
  }
}
