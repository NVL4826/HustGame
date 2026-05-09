package hust.adventure.state;

import hust.adventure.core.ProgressContext;

public interface GameState {
    void enter(ProgressContext context);
    void update(ProgressContext context, float delta);
    void exit(ProgressContext context);
}
