package hust.adventure.gamestate;

import hust.adventure.core.context.ProgressContext;

public interface GameState {
    void enter(ProgressContext context);

    void update(ProgressContext context, float delta);

    void exit(ProgressContext context);
}
