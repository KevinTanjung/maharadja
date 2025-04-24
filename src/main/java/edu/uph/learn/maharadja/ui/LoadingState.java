package edu.uph.learn.maharadja.ui;

import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;

public class LoadingState {
  private static final ObservableSet<String> loadingKeys = FXCollections.observableSet();

  private LoadingState() {}

  public static void add(String key) {
    loadingKeys.add(key);
  }

  public static void remove(String key) {
    loadingKeys.remove(key);
  }

  public static boolean isLoading() {
    return !loadingKeys.isEmpty();
  }

  public static boolean isLoading(String key) {
    return loadingKeys.contains(key);
  }

  public static void bind(InvalidationListener listener) {
    loadingKeys.addListener(listener);
  }

  public static void bind(SetChangeListener<String> listener) {
    loadingKeys.addListener(listener);
  }
}
