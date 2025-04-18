package edu.uph.learn.maharadja.ui.scene;

import edu.uph.learn.maharadja.common.Color;
import edu.uph.learn.maharadja.common.Constant;
import edu.uph.learn.maharadja.ui.factory.ButtonFactory;
import edu.uph.learn.maharadja.ui.factory.FormFactory;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import static edu.uph.learn.maharadja.common.Constant.DEFAULT_WIDTH;

public class LobbyScene extends Scene {
  private static final double FORM_WIDTH = 420;

  public LobbyScene() {
    super(new BorderPane(), Constant.DEFAULT_WIDTH, Constant.DEFAULT_HEIGHT);
    setFill(Color.IVORY_WHITE.get());

    BorderPane root = ((BorderPane) getRoot());
    root.setPadding(new Insets(64, 0, 0, 0));
    root.setBackground(Background.fill(Color.IVORY_WHITE.get()));
    renderLogo();
    renderPlayerRegistration();
  }

  private void renderLogo() {
    ImageView imageView = new ImageView(new Image("assets/logo_gold.png"));
    imageView.setFitWidth(FORM_WIDTH);
    imageView.setFitHeight(FORM_WIDTH/2);
    imageView.setPickOnBounds(true);
    imageView.setPreserveRatio(true);
    BorderPane.setAlignment(imageView, Pos.CENTER);
    ((BorderPane) getRoot()).setTop(imageView);
  }

  private void renderPlayerRegistration() {
    double padding = (DEFAULT_WIDTH-FORM_WIDTH)/2;
    VBox container = new VBox();
    container.setPadding(new Insets(0, padding, 0, padding));
    container.setMinWidth(FORM_WIDTH);
    BorderPane.setMargin(container, new Insets(160, 0, 0, 0));

    //region Label & Field
    Label errorLabel = FormFactory.label("", FORM_WIDTH, FormFactory.Aligment.CENTER);
    container.getChildren().add(errorLabel);
    container.getChildren().add(FormFactory.label("USERNAME", FORM_WIDTH, FormFactory.Aligment.CENTER));
    container.getChildren().add(FormFactory.textField(FORM_WIDTH));
    //endregion

    //region Buttons
    double width = (FORM_WIDTH / 2) - 8;
    Button serverButton = ButtonFactory.primary("HOST", width);
    HBox.setMargin(serverButton, new Insets(8, 16, 0, 0));
    Button clientButton = ButtonFactory.primary("JOIN", width);
    HBox.setMargin(clientButton, new Insets(8, 0, 0, 0));
    container.getChildren().add(new HBox(serverButton, clientButton));
    //endregion

    ((BorderPane) getRoot()).setCenter(container);
  }
}
