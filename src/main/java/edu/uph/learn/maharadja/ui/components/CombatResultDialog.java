package edu.uph.learn.maharadja.ui.components;

import edu.uph.learn.maharadja.common.Color;
import edu.uph.learn.maharadja.common.DiceRoll.RollResult;
import edu.uph.learn.maharadja.common.UI;
import edu.uph.learn.maharadja.common.UIUtil;
import edu.uph.learn.maharadja.game.CombatResult;
import edu.uph.learn.maharadja.ui.event.CombatResultEvent;
import edu.uph.learn.maharadja.ui.state.DialogTask;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Popup;
import javafx.stage.Window;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class CombatResultDialog extends Popup {
  private final SequentialTransition sequence;
  private final VBox vBox;

  public CombatResultDialog(DialogTask task, Runnable dialogStateCallback) {
    if (task.data().isEmpty()) {
      vBox = new VBox();
      sequence = new SequentialTransition();
      dialogStateCallback.run();
      return;
    }

    CombatResultEvent combatResultEvent = (CombatResultEvent) task.data().get();
    CombatResult combatResult = combatResultEvent.result();
    boolean attackerWon = combatResult.attackerLost() < combatResult.defenderLost();
    Color backgroundColor = Color.IVORY_WHITE;
    Color headingColor = attackerWon ? Color.IMPERIAL_GOLD : Color.SUNSET_RED;
    Color textColor = Color.VOLCANIC_BLACK;

    vBox = new VBox(UI.MEDIUM);
    vBox.setBorder(new Border(new BorderStroke(headingColor.get(), BorderStrokeStyle.SOLID, new CornerRadii(UI.UNIT), new BorderWidths(UI.SMALL))));
    vBox.setPadding(new Insets(UI.UNIT));
    vBox.setMinWidth(UI.FORM_WIDTH);
    vBox.setMinHeight(UI.FORM_WIDTH / 2);
    vBox.setMaxWidth(UI.FORM_WIDTH);
    vBox.setBackground(Background.fill(backgroundColor.get()));
    vBox.setAlignment(Pos.CENTER);

    Label resultLabel = new Label(attackerWon ? combatResult.result() == CombatResult.Result.OCCUPIED ? "CONQUERED" : "VICTORY" : "DEFEAT");
    resultLabel.setMaxWidth(Region.USE_PREF_SIZE);
    resultLabel.setTextAlignment(TextAlignment.CENTER);
    resultLabel.setFont(UI.EXTRA_LARGE_FONT);
    resultLabel.setStyle("-fx-text-fill: " + headingColor.toHex() + "; -fx-text-alignment: center;");
    resultLabel.setPadding(new Insets(UI.MEDIUM, 0, UI.MEDIUM, 0));
    vBox.getChildren().add(resultLabel);

    HBox hbox = new HBox();
    hbox.setAlignment(Pos.CENTER);
    vBox.getChildren().add(hbox);

    VBox left = new VBox();
    left.setMaxWidth(Double.MAX_VALUE);
    left.setAlignment(Pos.TOP_CENTER);
    left.getChildren().add(createLabel("ATTACKER", attackerWon ? Color.IMPERIAL_GOLD : Color.SUNSET_RED, UI.LARGE_FONT));
    left.getChildren().add(createLabel(combatResult.attackerLost(), attackerWon ? Color.IMPERIAL_GOLD : Color.SUNSET_RED));
    VBox right= new VBox();
    right.setMaxWidth(Double.MAX_VALUE);
    right.setAlignment(Pos.TOP_CENTER);
    right.getChildren().add(createLabel("DEFENDER", attackerWon ? Color.SUNSET_RED : Color.IMPERIAL_GOLD, UI.LARGE_FONT));
    right.getChildren().add(createLabel(combatResult.defenderLost(), attackerWon ? Color.SUNSET_RED : Color.IMPERIAL_GOLD));
    HBox.setHgrow(left, Priority.ALWAYS);
    HBox.setHgrow(right, Priority.ALWAYS);
    hbox.getChildren().addAll(left, right);

    RollResult rollResult = combatResult.rollResult();
    List<HBox> attackerRolls = new ArrayList<>();
    List<HBox> defenderRolls = new ArrayList<>();

    for (int i = 0; i < Math.max(rollResult.attackerRoll().size(), rollResult.defenderRoll().size()); i++) {
      if (i % 4 == 0) {
        attackerRolls.add(createCenteredHbox());
        defenderRolls.add(createCenteredHbox());
      }
      if (i >= rollResult.attackerRoll().size()) {
        defenderRolls.getLast().getChildren().add(createDice(rollResult.defenderRoll().get(i), false));
      } else if (i >= rollResult.defenderRoll().size()) {
        defenderRolls.getLast().getChildren().add(createDice(rollResult.attackerRoll().get(i), false));
      } else {
        int attacker = rollResult.attackerRoll().get(i);
        int defender = rollResult.defenderRoll().get(i);
        attackerRolls.getLast().getChildren().add(createDice(attacker, attacker > defender));
        defenderRolls.getLast().getChildren().add(createDice(defender, attacker <= defender));
      }
    }

    left.getChildren().addAll(attackerRolls);
    right.getChildren().addAll(defenderRolls);

    vBox.setOnMouseClicked((e -> hide()));
    getContent().add(vBox);
    setAutoFix(true);
    setAutoHide(true);
    setHideOnEscape(true);

    FadeTransition fadeIn = new FadeTransition(Duration.millis(500), vBox);
    fadeIn.setFromValue(0);
    fadeIn.setToValue(1);
    PauseTransition stayOn = new PauseTransition(Duration.seconds(task.autoDismissSeconds().orElse(5)));
    FadeTransition fadeOut = new FadeTransition(Duration.millis(500), vBox);
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

  private static HBox createCenteredHbox() {
    HBox box = new HBox();
    box.setAlignment(Pos.CENTER_LEFT);
    return box;
  }

  private Node createLabel(int num, Color textColor) {
    return createLabel(String.valueOf(num * -1), textColor, UI.EXTRA_LARGE_FONT);
  }

  private StackPane createDice(int roll, boolean border) {
    ImageView imageView = new ImageView(new Image(String.format("assets/dice/%d.png", roll)));
    imageView.setFitWidth(UI.EXTRA_LARGE);
    imageView.setFitHeight(UI.EXTRA_LARGE);
    StackPane pane = new StackPane(imageView);
    pane.setPadding(new Insets(UI.SMALL));
    Rectangle imageBorder = new Rectangle();
    imageBorder.setStroke(border ? Color.IMPERIAL_GOLD.get() : javafx.scene.paint.Color.TRANSPARENT);
    imageBorder.setStrokeWidth(4.0);
    imageBorder.setFill(javafx.scene.paint.Color.TRANSPARENT);
    imageBorder.setWidth(UI.EXTRA_LARGE + UI.SMALL);
    imageBorder.setHeight(UI.EXTRA_LARGE + UI.SMALL);
    return pane;
  }

  private static Label createLabel(String text, Color textColor, Font font) {
    Label label = new Label(text);
    label.setStyle("-fx-text-fill: " + textColor.toHex() + "; -fx-text-alignment: center;");
    label.setFont(font);
    label.setTextAlignment(TextAlignment.CENTER);
    label.setMinWidth(UI.LARGE);
    HBox.setHgrow(label, Priority.ALWAYS);
    return label;
  }

  @Override
  public void show(Window window) {
    super.show(window);
    Scene scene = window.getScene();
    setX(window.getX() + scene.getWidth() / 2 - getWidth() / 2);
    setY(window.getY() + scene.getHeight() / 2 - getHeight());
    sequence.play();
  }
}
