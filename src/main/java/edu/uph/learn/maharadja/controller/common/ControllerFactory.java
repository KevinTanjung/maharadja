package edu.uph.learn.maharadja.controller.common;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ControllerFactory {
  private static final Map<Action, Controller> controllerMap = new HashMap<>();

  public ControllerFactory(List<Controller> controllers) {
    controllers.forEach(this::register);
  }

  private void register(Controller controller) {
    log.info("Registering controller: {} to action: {}", controller.getClass().getName(), controller.getAction());
    controllerMap.put(controller.getAction(), controller);
  }

  public Message dispatch(Message message) {
    return controllerMap.get(message.getAction()).handle(message);
  }
}
