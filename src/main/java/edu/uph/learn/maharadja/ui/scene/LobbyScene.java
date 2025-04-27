package edu.uph.learn.maharadja.ui.scene;

import edu.uph.learn.maharadja.common.Color;
import edu.uph.learn.maharadja.common.UI;
import edu.uph.learn.maharadja.ui.GameWindow;
import edu.uph.learn.maharadja.ui.TextResource;
import edu.uph.learn.maharadja.ui.factory.ButtonFactory;
import edu.uph.learn.maharadja.ui.factory.FormFactory;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Optional;

import static edu.uph.learn.maharadja.common.UI.FORM_WIDTH;

public class LobbyScene extends SceneWithLogo {
  private final GameWindow gameWindow;
  private final SimpleStringProperty usernameProperty = new SimpleStringProperty("");
  private TextField usernameField;

  public LobbyScene(GameWindow gameWindow) {
    super();
    this.gameWindow = gameWindow;
    renderPlayerRegistration();
  }

  @Override
  public Color getBackgroundColor() {
    return Color.IVORY_WHITE;
  }

  @Override
  public LogoColor getLogoColor() {
    return LogoColor.IMPERIAL_GOLD;
  }

  private void renderPlayerRegistration() {
    double padding = (UI.WIDTH - FORM_WIDTH) / 2;
    VBox container = new VBox();
    container.setPadding(new Insets(0, padding, 0, padding));
    container.setMinWidth(FORM_WIDTH);
    BorderPane.setMargin(container, new Insets(160, 0, 0, 0));

    //region Label & Field
    Label errorLabel = FormFactory.label("", FORM_WIDTH, FormFactory.Aligment.CENTER);
    container.getChildren().add(errorLabel);
    container.getChildren().add(FormFactory.label(TextResource.USERNAME_FIELD, FORM_WIDTH, FormFactory.Aligment.CENTER));
    usernameField = FormFactory.textField(FORM_WIDTH);
    usernameField.textProperty().bindBidirectional(usernameProperty);
    container.getChildren().add(usernameField);
    //endregion

    //region Buttons
    double width = (FORM_WIDTH / 2) - 8;

    Button serverButton = ButtonFactory.primary(TextResource.HOST_BUTTON, width);
    HBox.setMargin(serverButton, new Insets(8, 16, 0, 0));
    serverButton.setOnAction(e -> checkUsername()
        .ifPresent(u -> gameWindow.registerPlayer(u, true))
    );
    serverButton.disableProperty().bind(usernameProperty.isEmpty());

    Button clientButton = ButtonFactory.primary(TextResource.JOIN_BUTTON, width);
    HBox.setMargin(clientButton, new Insets(8, 0, 0, 0));
    clientButton.setOnAction(e -> checkUsername()
        .ifPresent(u -> gameWindow.registerPlayer(u, false))
    );
    clientButton.disableProperty().bind(usernameProperty.isEmpty());

    container.getChildren().add(new HBox(serverButton, clientButton));
    //endregion

    ((BorderPane) getRoot()).setCenter(container);
  }

  private Optional<String> checkUsername() {
    String username = usernameField.getText();
    if (username != null && !username.isEmpty()) {
      return Optional.of(username);
    }
    return Optional.empty();
  }
}
