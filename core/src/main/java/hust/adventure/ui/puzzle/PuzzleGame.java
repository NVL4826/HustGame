package hust.adventure.ui.puzzle;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import hust.adventure.screens.levels.LevelContext;

/**
 * Interface representing a puzzle game stage.
 */
public interface PuzzleGame {
    /**
     * Initializes the puzzle with the LevelContext.
     *
     * @param context The current level context.
     */
    void init(final LevelContext context);

    /**
     * Updates the puzzle logic per frame.
     *
     * @param delta The time step in seconds.
     */
    void update(final float delta);

    /**
     * Renders the puzzle geometries and text.
     *
     * @param shapeRenderer The shape renderer to draw lines/rectangles.
     * @param batch         The sprite batch to draw textures/fonts.
     */
    void render(final ShapeRenderer shapeRenderer, final SpriteBatch batch);

    /**
     * Resets the puzzle state entirely on failure.
     */
    void reset();

    /**
     * Checks if the puzzle has been successfully solved.
     *
     * @return true if solved, false otherwise.
     */
    boolean isSolved();

    /**
     * Disposes of any native or custom resources allocated by the puzzle.
     */
    void dispose();

    /**
     * Gets the background texture path for the puzzle game.
     * Return null or empty to fallback to a neutral gray-white color.
     *
     * @return The background texture path, or null if none.
     */
    default String getBackgroundPath() {
        return null;
    }
}
