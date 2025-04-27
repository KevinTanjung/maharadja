package edu.uph.learn.maharadja.ui.state;

import edu.uph.learn.maharadja.event.BotActionEvent;
import edu.uph.learn.maharadja.ui.components.GameDialog;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.Queue;

public class DialogState {
  private static final Logger LOG = LoggerFactory.getLogger(DialogState.class);

  private final Queue<DialogTask> dialogQueue = new LinkedList<>();
  private final Stage stage;
  private volatile GameDialog activeDialog;

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
      activeDialog = new GameDialog(task, () -> {
        activeDialog = null;
        tryProcessNextDialog();
      });
      activeDialog.show(stage);
    }
  }

  public void onBotActionEvent(BotActionEvent event) {
    enqueue(new DialogTask(
        String.format("%s %s", event.currentPlayer().getUsername(), event.currentPhase()),
        "Deciding...",
        event.callback()
    ));
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
