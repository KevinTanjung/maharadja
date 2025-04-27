package edu.uph.learn.maharadja.ui.state;

import edu.uph.learn.maharadja.game.CombatResult;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;

public class LoadingState {
  public enum Key {
    COMBAT_RESULT,
  }

  private final ObservableSet<String> loadingKeys = FXCollections.observableSet();

  public void add(String key) {
    loadingKeys.add(key);
  }

  public void remove(String key) {
    loadingKeys.remove(key);
  }

  public boolean isLoading() {
    return !loadingKeys.isEmpty();
  }

  public boolean isLoading(Key key) {
    return loadingKeys.contains(key.name());
  }

  public boolean isLoading(String key) {
    return loadingKeys.contains(key);
  }

  public void bind(InvalidationListener listener) {
    loadingKeys.addListener(listener);
  }

  public void bind(SetChangeListener<String> listener) {
    loadingKeys.addListener(listener);
  }

  //region Singleton
  private LoadingState() {}

  private static final LoadingState instance = new LoadingState();

  public static LoadingState get() {
    return instance;
  }
  //endregion
}
