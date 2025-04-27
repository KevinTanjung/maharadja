package edu.uph.learn.maharadja.ui.form;

import edu.uph.learn.maharadja.common.Color;
import edu.uph.learn.maharadja.common.UI;
import edu.uph.learn.maharadja.game.GameEngine;
import edu.uph.learn.maharadja.ui.TextResource;
import edu.uph.learn.maharadja.ui.factory.ButtonFactory;
import edu.uph.learn.maharadja.ui.state.TileSelectionState;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

public abstract class EndableActionForm extends BaseActionForm {
  protected final Button submitButton;
  protected final Button endButton;

  public EndableActionForm() {
    super();

    double buttonWidth = (UI.TAB_WIDTH / 2) - UI.UNIT;
    submitButton = ButtonFactory.create(
        getSubmitButtonTitle(),
        buttonWidth,
        Color.IVORY_WHITE,
        getSubmitButtonColor()
    );
    submitButton.disableProperty().bind(TileSelectionState.get().selectedTargetProperty().isNull());
    submitButton.setOnAction(getSubmitButtonListener());
    endButton = ButtonFactory.create(
        TextResource.END_ACTION,
        buttonWidth,
        Color.IVORY_WHITE,
        Color.GUNMETAL_GREY
    );
    endButton.setOnAction(actionEvent -> GameEngine.get().nextPhase());
  }

  protected abstract EventHandler<ActionEvent> getSubmitButtonListener();

  protected abstract String getSubmitButtonTitle();

  protected abstract Color getSubmitButtonColor();

  public Button getSubmitButton() {
    return submitButton;
  }

  public Button getEndButton() {
    return endButton;
  }
}
