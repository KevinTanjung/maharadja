package edu.uph.learn.maharadja.ui.components;

import edu.uph.learn.maharadja.common.Color;
import edu.uph.learn.maharadja.event.EventBus;
import edu.uph.learn.maharadja.game.Player;
import edu.uph.learn.maharadja.game.event.TroopMovementEvent;
import edu.uph.learn.maharadja.map.Region;
import edu.uph.learn.maharadja.map.Territory;
import edu.uph.learn.maharadja.ui.factory.BorderFactory;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SidePlayerTab extends Tab {
  private final Player player;
  private final Accordion accordion;
  private final Map<Region, FXRegion> regionPanes = new HashMap<>();
  private final Map<Territory, FXTerritory> territoryLabels = new HashMap<>();

  public SidePlayerTab(Player player) {
    super(player.getUsername(), new VBox());
    setClosable(true);
    setStyle(String.format(
        "-fx-background-color: %s; -fx-text-fill: %s; -fx-fill: %s;",
        player.getColor().toHex(), Color.IVORY_WHITE.toHex(), Color.IVORY_WHITE.toHex()
    ));
    this.player = player;
    this.accordion = new Accordion();
    EventBus.registerListener(TroopMovementEvent.class, this::onTroopMovementEvent);

    // TODO: change from accordion to empty label for "lost"
    ((VBox) getContent()).getChildren().add(accordion);
    renderTerritories();
  }

  private void onTroopMovementEvent(TroopMovementEvent event) {
    // TODO: optimize to detect change
    renderTerritories();
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
      accordion.setExpandedPane(sortedPanes.getFirst());
      for (FXRegion region : sortedPanes) {
        Collections.sort(region.territoryList);
        region.setContent(new VBox(region.territoryList.toArray(new FXTerritory[0])));
      }
    });
  }

  public static class FXRegion extends TitledPane implements Comparable<FXRegion> {
    private final ObservableList<FXTerritory> territoryList = FXCollections.observableArrayList();
    private final Region region;
    private int totalTroopsInRegion;

    public FXRegion(Region region) {
      this.region = region;
      setText(region.getName());
      setBorder(BorderFactory.color(region.getColor()));
      totalTroopsInRegion = 0;
    }

    public void addTerritory(FXTerritory territory) {
      if (territoryList.contains(territory)) {
        return;
      }
      territoryList.add(territory);
      totalTroopsInRegion += territory.getNumberOfStationedTroops();
    }

    @Override
    public int compareTo(FXRegion o) {
      return Integer.compare(totalTroopsInRegion, o.totalTroopsInRegion) * -1;
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
