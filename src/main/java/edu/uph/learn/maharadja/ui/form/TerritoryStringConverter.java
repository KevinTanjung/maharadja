package edu.uph.learn.maharadja.ui.form;

import edu.uph.learn.maharadja.map.Territory;
import javafx.util.StringConverter;

import java.util.Optional;

public class TerritoryStringConverter extends StringConverter<Territory> {
    @Override
    public String toString(Territory territory) {
      return Optional.ofNullable(territory)
          .map(t -> String.format("(%02d) %s, %s", t.getNumberOfStationedTroops(), t.getName(), t.getRegion().getName()))
          .orElse("-");
    }

    @Override
    public Territory fromString(String s) {
      return null;
    }
  }
