package edu.uph.learn.maharadja.ui.scene;

import edu.uph.learn.maharadja.common.Color;
import edu.uph.learn.maharadja.common.UI;
import edu.uph.learn.maharadja.game.GameState;
import edu.uph.learn.maharadja.ui.GameWindow;
import edu.uph.learn.maharadja.ui.TextResource;
import edu.uph.learn.maharadja.ui.factory.ButtonFactory;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

import static edu.uph.learn.maharadja.common.UI.FORM_WIDTH;
import static edu.uph.learn.maharadja.common.UI.WIDTH;

public abstract class EndScene extends SceneWithLogo {
  public EndScene(GameWindow gameWindow) {
    super();

    renderSplash();
    renderEndButtons(gameWindow);
  }

  private void renderSplash() {
    BorderPane root = (BorderPane) getRoot();
    ImageView imageView = new ImageView(getSplashImage());
    imageView.setFitWidth(WIDTH / 2);
    imageView.setFitHeight(WIDTH / 2);
    imageView.setPickOnBounds(true);
    imageView.setPreserveRatio(true);
    imageView.fitWidthProperty().bind(root.widthProperty().multiply(0.5));
    StackPane wrapper = new StackPane(imageView);
    wrapper.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
    root.setCenter(wrapper);
  }

  private void renderEndButtons(GameWindow gameWindow) {
    double width = (FORM_WIDTH / 2) - 8;
    HBox hBox = new HBox();
    hBox.setMinWidth(FORM_WIDTH);
    hBox.setMaxWidth(FORM_WIDTH);
    BorderPane.setAlignment(hBox, Pos.CENTER);
    ((BorderPane) getRoot()).setBottom(hBox);

    //region Back to Lobby
    Button lobbyButton = ButtonFactory.create(
        TextResource.LOBBY_BUTTON,
        width,
        Color.VOLCANIC_BLACK,
        Color.IVORY_WHITE
    );
    HBox.setMargin(lobbyButton, new Insets(UI.UNIT, UI.UNIT, UI.EXTRA_LARGE, 0));
    lobbyButton.setOnAction(e -> {
      GameState.reset();
      gameWindow.openLobby();
    });
    //endregion

    //region Exit
    Button exitButton = ButtonFactory.create(
        TextResource.EXIT_BUTTON,
        width,
        Color.VOLCANIC_BLACK,
        Color.IVORY_WHITE
    );
    HBox.setMargin(exitButton, new Insets(UI.UNIT, 0, UI.EXTRA_LARGE, UI.UNIT));
    exitButton.setOnAction(e -> Platform.exit());
    //endregion

    hBox.getChildren().addAll(lobbyButton, exitButton);
  }

  public abstract String getSplashImage();
}
