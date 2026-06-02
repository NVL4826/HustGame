package hust.adventure.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import hust.adventure.core.context.ProgressContext;
import hust.adventure.input.DebugInputHandler;

/**
 * Renders the debug mode overlay panel when debug mode is enabled. Queries state from DebugInputHandler to render
 * selection menus.
 */
public class DebugUI {
    private DebugInputHandler inputHandler;

    // UI Layout Constants to avoid magic numbers
    private static final float PANEL_X = 520f;
    private static final float PANEL_Y = 230f;
    private static final float PANEL_WIDTH = 260f;
    private static final float PANEL_HEIGHT = 350f;

    private static final float SELECT_X = 240f;
    private static final float SELECT_Y = 230f;
    private static final float SELECT_WIDTH = 260f;
    private static final float SELECT_HEIGHT = 350f;

    // Stylized color theme constants
    private static final Color BG_COLOR = new Color(0.08f, 0.09f, 0.13f, 0.85f);
    private static final Color BORDER_COLOR = new Color(0.18f, 0.50f, 0.93f, 0.9f);
    private static final Color TITLE_COLOR = new Color(0.95f, 0.61f, 0.07f, 1f);
    private static final Color HIGHLIGHT_BG = new Color(0.18f, 0.50f, 0.93f, 0.35f);

    /**
     * Constructs a new DebugUI.
     */
    public DebugUI() {
    }

    /**
     * Sets the input handler from which the UI queries selection state.
     *
     * @param inputHandler the debug input handler
     */
    public void setInputHandler(final DebugInputHandler inputHandler) {
        this.inputHandler = inputHandler;
    }

    /**
     * Renders the debug panel overlay and any active selection menus.
     *
     * @param batch         the sprite batch
     * @param shapeRenderer the shape renderer
     * @param font          the bitmap font
     */
    public void render(final SpriteBatch batch, final ShapeRenderer shapeRenderer, final BitmapFont font) {
        if (!ProgressContext.instance.isShowDebug()) {
            return;
        }

        // Render panels backgrounds and borders
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        final boolean active = (inputHandler != null && inputHandler.isActive());
        final DebugOption[] currentOptions = (inputHandler != null) ? inputHandler.getCurrentOptions() : null;
        final int selectedIndex = (inputHandler != null) ? inputHandler.getSelectedIndex() : 0;
        final SelectionMode activeMode = (inputHandler != null) ? inputHandler.getActiveMode() : SelectionMode.NONE;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Main panel background
        shapeRenderer.setColor(BG_COLOR);
        shapeRenderer.rect(PANEL_X, PANEL_Y, PANEL_WIDTH, PANEL_HEIGHT);

        // Selection panel background if active
        if (active && currentOptions != null) {
            shapeRenderer.rect(SELECT_X, SELECT_Y, SELECT_WIDTH, SELECT_HEIGHT);

            // Render selected option highlight background
            shapeRenderer.setColor(HIGHLIGHT_BG);
            float itemY = SELECT_Y + SELECT_HEIGHT - 75f - (selectedIndex * 30f);
            shapeRenderer.rect(SELECT_X + 10f, itemY - 5f, SELECT_WIDTH - 20f, 26f);
        }
        shapeRenderer.end();

        // Neon border lines
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(BORDER_COLOR);
        shapeRenderer.rect(PANEL_X, PANEL_Y, PANEL_WIDTH, PANEL_HEIGHT);
        if (active && currentOptions != null) {
            shapeRenderer.rect(SELECT_X, SELECT_Y, SELECT_WIDTH, SELECT_HEIGHT);
        }
        shapeRenderer.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);

        // Render text elements
        batch.begin();

        // Main panel title and info
        font.setColor(TITLE_COLOR);
        font.draw(batch, "=== DEBUG MENU ===", PANEL_X + 30f, PANEL_Y + PANEL_HEIGHT - 20f);

        font.setColor(Color.LIGHT_GRAY);
        font.draw(batch, "Press keys to action:", PANEL_X + 15f, PANEL_Y + PANEL_HEIGHT - 60f);

        // F2: Debug Menu indicator
        font.setColor(Color.WHITE);
        font.draw(batch, "[F2] Debug Menu: ", PANEL_X + 15f, PANEL_Y + PANEL_HEIGHT - 100f);
        font.setColor(Color.GREEN);
        font.draw(batch, "VISIBLE", PANEL_X + 150f, PANEL_Y + PANEL_HEIGHT - 100f);

        // F3: Hitboxes indicator
        font.setColor(Color.WHITE);
        font.draw(batch, "[F3] Hitboxes: ", PANEL_X + 15f, PANEL_Y + PANEL_HEIGHT - 140f);
        if (ProgressContext.instance.isShowHitbox()) {
            font.setColor(Color.GREEN);
            font.draw(batch, "VISIBLE", PANEL_X + 150f, PANEL_Y + PANEL_HEIGHT - 140f);
        } else {
            font.setColor(Color.RED);
            font.draw(batch, "HIDDEN", PANEL_X + 150f, PANEL_Y + PANEL_HEIGHT - 140f);
        }

        // F4: God Mode
        font.setColor(Color.WHITE);
        font.draw(batch, "[F4] God Mode: ", PANEL_X + 15f, PANEL_Y + PANEL_HEIGHT - 180f);
        if (ProgressContext.instance.isGodMode()) {
            font.setColor(Color.GREEN);
            font.draw(batch, "ON", PANEL_X + 150f, PANEL_Y + PANEL_HEIGHT - 180f);
        } else {
            font.setColor(Color.RED);
            font.draw(batch, "OFF", PANEL_X + 150f, PANEL_Y + PANEL_HEIGHT - 180f);
        }

        // F5: Speed Run
        font.setColor(Color.WHITE);
        font.draw(batch, "[F5] Speed Hack: ", PANEL_X + 15f, PANEL_Y + PANEL_HEIGHT - 220f);
        if (ProgressContext.instance.isFastRun()) {
            font.setColor(Color.GREEN);
            font.draw(batch, "ON", PANEL_X + 150f, PANEL_Y + PANEL_HEIGHT - 220f);
        } else {
            font.setColor(Color.RED);
            font.draw(batch, "OFF", PANEL_X + 150f, PANEL_Y + PANEL_HEIGHT - 220f);
        }

        // F6: Switch Map
        font.setColor(Color.WHITE);
        font.draw(batch, "[F6] Switch Map", PANEL_X + 15f, PANEL_Y + PANEL_HEIGHT - 260f);

        // F7: Spawn Item
        font.draw(batch, "[F7] Spawn Item", PANEL_X + 15f, PANEL_Y + PANEL_HEIGHT - 300f);

        // F8: Spawn Monster
        font.draw(batch, "[F8] Spawn Monster", PANEL_X + 15f, PANEL_Y + PANEL_HEIGHT - 330f);

        // Render Selection list if active
        if (active && currentOptions != null) {
            font.setColor(TITLE_COLOR);
            final String title = "SELECT " + activeMode.name();
            font.draw(batch, title, SELECT_X + 20f, SELECT_Y + SELECT_HEIGHT - 20f);

            font.setColor(Color.LIGHT_GRAY);
            font.draw(batch, "Arrows to move, Enter select, ESC", SELECT_X + 15f, SELECT_Y + SELECT_HEIGHT - 45f);

            for (int i = 0; i < currentOptions.length; i++) {
                if (i == selectedIndex) {
                    font.setColor(Color.WHITE);
                    font.draw(batch, "> " + currentOptions[i].displayName, SELECT_X + 20f,
                            SELECT_Y + SELECT_HEIGHT - 75f - (i * 30f));
                } else {
                    font.setColor(Color.LIGHT_GRAY);
                    font.draw(batch, "  " + currentOptions[i].displayName, SELECT_X + 20f,
                            SELECT_Y + SELECT_HEIGHT - 75f - (i * 30f));
                }
            }
        }

        batch.end();
    }
}
