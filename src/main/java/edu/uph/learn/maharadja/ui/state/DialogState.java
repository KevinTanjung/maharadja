package edu.uph.learn.maharadja.ui.state;

import edu.uph.learn.maharadja.event.BotActionEvent;
import edu.uph.learn.maharadja.ui.components.CombatResultDialog;
import edu.uph.learn.maharadja.ui.components.GameDialog;
import edu.uph.learn.maharadja.ui.event.CombatResultEvent;
import javafx.stage.Popup;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.Queue;

public class DialogState {
  private static final Logger LOG = LoggerFactory.getLogger(DialogState.class);

  private final Queue<DialogTask> dialogQueue = new LinkedList<>();
  private final Stage stage;
  private volatile Popup activeDialog;

  public void enqueue(DialogTask task) {
    LOG.info("Enqueuing DialogTask: {}", task.title());
    synchronized (this) {
      dialogQueue.add(task);
      tryProcessNextDialog();
    }
  }

  private void tryProcessNextDialog() {
    synchronized (this) {
      if (activeDialog != null || dialogQueue.isEmpty()) {
        return;
      }
      showDialog(dialogQueue.poll());
    }
  }

  private void showDialog(DialogTask task) {
    synchronized (this) {
      if (task.data().isPresent() && task.data().get() instanceof CombatResultEvent) {
        activeDialog = new CombatResultDialog(task, () -> {
          activeDialog = null;
          tryProcessNextDialog();
        });
      } else {
        activeDialog = new GameDialog(task, () -> {
          activeDialog = null;
          tryProcessNextDialog();
        });
      }
      activeDialog.show(stage);
    }
  }

  public void onBotActionEvent(BotActionEvent event) {
    if (event.combatResult().isEmpty() || event.from().isEmpty() || event.to().isEmpty()) {
      enqueue(new DialogTask(
          String.format("%s %s", event.currentPlayer().getUsername(), event.currentPhase()),
          "Deciding...",
          event.callback()
      ));
    } else {
      showDialog(new DialogTask(
          new CombatResultEvent(event.from().get(), event.to().get(), event.combatResult().get()),
          event.callback(),
          2 // make bot decision dismiss faster
      ));
    }
  }

  public void onCombatResultEvent(CombatResultEvent combatResultEvent) {
    showDialog(new DialogTask(combatResultEvent, () -> {}));
  }

  //region Singleton
  private DialogState(Stage stage) {
    this.stage = stage;
  }

  private static DialogState instance;

  public static DialogState init(Stage stage) {
    if (instance != null) {
      return instance;
    }
    instance = new DialogState(stage);
    return instance;
  }

  public static DialogState get() {
    return instance;
  }
  //endregion
}
