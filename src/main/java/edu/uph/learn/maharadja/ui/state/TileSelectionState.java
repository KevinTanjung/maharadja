package edu.uph.learn.maharadja.ui.state;

import edu.uph.learn.maharadja.game.TurnPhase;
import edu.uph.learn.maharadja.map.Territory;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TileSelectionState {
  private final ObjectProperty<TurnPhase> currentPhase = new SimpleObjectProperty<>(null);
  // will affect which is "selected"
  private final ObjectProperty<Territory> selectedSource = new SimpleObjectProperty<>(null);
  private final ObjectProperty<Territory> selectedTarget = new SimpleObjectProperty<>(null);
  private final ObjectProperty<Territory> selectedDetail = new SimpleObjectProperty<>(null);
  // will affect which is "highlightable"
  private final ObservableList<Territory> validSources = FXCollections.observableArrayList();
  private final ObservableList<Territory> validTargets = FXCollections.observableArrayList();

  public ObjectProperty<TurnPhase> currentPhaseProperty() {
    return currentPhase;
  }

  public ObjectProperty<Territory> selectedSourceProperty() {
    return selectedSource;
  }

  public ObjectProperty<Territory> selectedTargetProperty() {
    return selectedTarget;
  }

  public ObjectProperty<Territory> selectedDetailProperty() {
    return selectedDetail;
  }

  public ObservableList<Territory> getValidSources() {
    return validSources;
  }

  public ObservableList<Territory> getValidTargets() {
    return validTargets;
  }

  //region Singleton
  private TileSelectionState() {}

  private static final TileSelectionState instance = new TileSelectionState();

  public static TileSelectionState get() {
    return instance;
  }
  //endregion
}
