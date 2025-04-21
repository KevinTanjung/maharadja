package edu.uph.learn.maharadja.ui.components;

import edu.uph.learn.maharadja.common.UI;
import edu.uph.learn.maharadja.event.EventBus;
import edu.uph.learn.maharadja.game.GameEngine;
import edu.uph.learn.maharadja.game.GameState;
import edu.uph.learn.maharadja.ui.event.MapTileSelectedEvent;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

public class ActionPane extends BorderPane {
  private MapTile sourceTile;
  private MapTile destinationTile;

  public ActionPane() {
    EventBus.registerListener(MapTileSelectedEvent.class, this::onMapTileSelected);
    setHeight(UI.ACTION_PANE_HEIGHT);
    setPrefHeight(UI.ACTION_PANE_HEIGHT);
    setMinHeight(UI.ACTION_PANE_HEIGHT);
    setMaxHeight(UI.ACTION_PANE_HEIGHT);

    setCenter(new Label("Click on a Hex Tile!"));
  }

  private void onMapTileSelected(MapTileSelectedEvent event) {
    sourceTile = event.mapTile();
    setCenter(null);
    setTop(new Label(sourceTile.getTerritory().getName()));
    GridPane pane = new GridPane();
    setCenter(pane);
  }
}
