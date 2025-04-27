package edu.uph.learn.maharadja.ui.components;

import edu.uph.learn.maharadja.common.Color;
import edu.uph.learn.maharadja.common.UI;
import edu.uph.learn.maharadja.event.EventBus;
import edu.uph.learn.maharadja.game.GameState;
import edu.uph.learn.maharadja.player.Player;
import edu.uph.learn.maharadja.game.TurnPhase;
import edu.uph.learn.maharadja.game.event.GamePhaseEvent;
import edu.uph.learn.maharadja.game.event.TroopMovementEvent;
import edu.uph.learn.maharadja.map.Region;
import edu.uph.learn.maharadja.map.Territory;
import edu.uph.learn.maharadja.ui.factory.BorderFactory;
import edu.uph.learn.maharadja.ui.factory.LabelFactory;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PlayerTab extends Tab {
  private static final Logger LOG = LoggerFactory.getLogger(PlayerTab.class);

  private final Player player;
  private final Accordion accordion;
  private final Map<Region, FXRegion> regionPanes = new HashMap<>();
  private final Map<Territory, FXTerritory> territoryLabels = new HashMap<>();

  public PlayerTab(Player player) {
    super("  ", new VBox());
    EventBus.registerListener(TroopMovementEvent.class, this::onTroopMovementEvent);
    EventBus.registerListener(GamePhaseEvent.class, this::onGamePhaseEvent);

    this.player = player;
    this.accordion = new Accordion();

    setClosable(false);

    // TODO: change from accordion to empty label for "lost"
    ((VBox) getContent()).getChildren().addAll(
        LabelFactory.create(
            player.getUsername(),
            player.getColor(),
            Color.IVORY_WHITE,
            UI.TAB_WIDTH
        ),
        accordion
    );

    Platform.runLater(() -> {
      renderTabTitle(GameState.get().currentTurn());
      renderTerritories();
    });
  }

  public void renderTabTitle(Player currentPlayer) {
    if (Objects.equals(this.player, currentPlayer)) {
      setText("   " + player.getUsername() + "   ");
      setStyle(String.format(
          "-fx-background-color: %s; -fx-color: %s;",
          Color.IMPERIAL_GOLD.toHex(), Color.IVORY_WHITE.toHex()
      ));
    } else {
      setText("  ");
      setStyle(String.format(
          "-fx-background-color: %s; -fx-color: %s;",
          this.player.getColor().toHex(), Color.IVORY_WHITE.toHex()
      ));
    }
  }

  private void renderTerritories() {
    Platform.runLater(() -> {
      regionPanes.clear();
      territoryLabels.clear();
      accordion.getPanes().clear();
      for (Territory territory : player.getTerritories()) {
        FXTerritory fxTerritory = territoryLabels.computeIfAbsent(territory, FXTerritory::new);
        regionPanes.computeIfAbsent(territory.getRegion(), FXRegion::new).addTerritory(fxTerritory);
      }
      List<FXRegion> sortedPanes = regionPanes.values().stream().sorted().toList();
      accordion.getPanes().setAll(sortedPanes);
      if (!sortedPanes.isEmpty()) {
        accordion.setExpandedPane(sortedPanes.getFirst());
      }
      for (FXRegion region : sortedPanes) {
        Collections.sort(region.territoryList);
        region.setContent(new VBox(region.territoryList.toArray(new FXTerritory[0])));
      }
    });
  }

  private void onGamePhaseEvent(GamePhaseEvent gamePhaseEvent) {
    if (gamePhaseEvent.phase() == TurnPhase.START) {
      renderTabTitle(gamePhaseEvent.currentPlayer());
    }
  }

  private void onTroopMovementEvent(TroopMovementEvent event) {
    // TODO: optimize to detect change
    renderTerritories();
  }

  public static class FXRegion extends TitledPane implements Comparable<FXRegion> {
    private final ObservableList<FXTerritory> territoryList = FXCollections.observableArrayList();
    private final Region region;
    private final SimpleIntegerProperty totalTroopsInRegion = new SimpleIntegerProperty(0);

    public FXRegion(Region region) {
      this.region = region;
      this.totalTroopsInRegion.addListener((obs, oldVal, newVal) -> updateTitle());
      updateTitle();
      setBorder(BorderFactory.color(region.getColor()));
      setBackground(Background.fill(Color.IVORY_WHITE.get()));
    }

    private void updateTitle() {
      setText(String.format("%s (%s)", region.getName(), totalTroopsInRegion.get()));
    }

    public void addTerritory(FXTerritory territory) {
      if (territoryList.contains(territory)) {
        return;
      }
      territoryList.add(territory);
      totalTroopsInRegion.set(totalTroopsInRegion.get() + territory.getNumberOfStationedTroops());
      ;
    }

    @Override
    public int compareTo(FXRegion o) {
      return Integer.compare(totalTroopsInRegion.get(), o.totalTroopsInRegion.get()) * -1;
    }

    @Override
    public final boolean equals(Object object) {
      if (this == object) {
        return true;
      }
      if (!(object instanceof FXRegion fxRegion)) {
        return false;
      }

      return region.equals(fxRegion.region);
    }

    @Override
    public int hashCode() {
      return region.hashCode();
    }
  }

  public static class FXTerritory extends Label implements Comparable<FXTerritory> {
    private final String name;
    private final IntegerProperty numberOfStationedTroops = new SimpleIntegerProperty();

    public FXTerritory(Territory territory) {
      name = territory.getName();
      setNumberOfStationedTroops(territory.getNumberOfStationedTroops());
      setText(name + ": " + numberOfStationedTroops.get());
    }

    public String getName() {
      return name;
    }

    public int getNumberOfStationedTroops() {
      return numberOfStationedTroops.get();
    }

    public void setNumberOfStationedTroops(int numberOfStationedTroops) {
      this.numberOfStationedTroops.set(numberOfStationedTroops);
    }

    @Override
    public int compareTo(FXTerritory o) {
      return Integer.compare(getNumberOfStationedTroops(), o.getNumberOfStationedTroops()) * -1;
    }

    @Override
    public final boolean equals(Object object) {
      if (this == object) {
        return true;
      }
      if (!(object instanceof FXTerritory that)) {
        return false;
      }

      return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(name);
    }
  }
}
