package hust.adventure.core.context;

import hust.adventure.gamestate.GameState;

/**
 * Interface representing the progress context in the game, decoupling context from concrete game states.
 */
public interface GameProgressContext {
    /**
     * Transitions the game to a new GameState.
     * 
     * @param newState the target GameState
     */
    void setGameState(GameState newState);
}
