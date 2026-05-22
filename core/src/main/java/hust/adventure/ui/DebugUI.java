package hust.adventure.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import hust.adventure.core.context.ProgressContext;

/**
 * Renders the debug mode overlay panel when debug mode is enabled.
 * Also manages and renders selection sub-menus for maps, items, and monsters.
 */
public class DebugUI {
    private final OrthographicCamera uiCam;

    // UI Layout Constants to avoid magic numbers
    private static final float PANEL_X = 520f;
    private static final float PANEL_Y = 230f;
    private static final float PANEL_WIDTH = 260f;
    private static final float PANEL_HEIGHT = 350f;

    private static final float SELECT_X = 240f;
    private static final float SELECT_Y = 230f;
    private static final float SELECT_WIDTH = 260f;
    private static final float SELECT_HEIGHT = 350f;
    
    private static final float CAM_VIEW_WIDTH = 800f;
    private static final float CAM_VIEW_HEIGHT = 600f;

    // Stylized color theme constants
    private static final Color BG_COLOR = new Color(0.08f, 0.09f, 0.13f, 0.85f);
    private static final Color BORDER_COLOR = new Color(0.18f, 0.50f, 0.93f, 0.9f);
    private static final Color TITLE_COLOR = new Color(0.95f, 0.61f, 0.07f, 1f);
    private static final Color HIGHLIGHT_BG = new Color(0.18f, 0.50f, 0.93f, 0.35f);

    public enum SelectionMode {
        NONE, MAP, ITEM, MONSTER
    }

    public static class DebugOption {
        public final String id;
        public final String displayName;

        public DebugOption(String id, String displayName) {
            this.id = id;
            this.displayName = displayName;
        }
    }

    private static final DebugOption[] MAP_OPTIONS = {
        new DebugOption("tang1.tmx", "Floor 1 (tang1)"),
        new DebugOption("library.tmx", "Library"),
        new DebugOption("lab.tmx", "Lab"),
        new DebugOption("boss_room.tmx", "Boss Room"),
        new DebugOption("Final Outside.tmx", "Final Outside"),
        new DebugOption("test.tmx", "Test Map"),
        new DebugOption("tsx/map_1.tmx", "Map 1")
    };

    private static final DebugOption[] ITEM_OPTIONS = {
        new DebugOption("coffee_den", "Coffee Den"),
        new DebugOption("coffee_sua", "Coffee Sua"),
        new DebugOption("coffee_da", "Coffee Da"),
        new DebugOption("coffee_chon", "Coffee Chon"),
        new DebugOption("energy_drink", "Energy Drink"),
        new DebugOption("kho_ga", "Dried Chicken"),
        new DebugOption("whip", "Whip (Roi)"),
        new DebugOption("magic_wand", "Magic Wand (Đua)"),
        new DebugOption("garlic", "Garlic (Toi)"),
        new DebugOption("bun_dau", "Bun Dau (Đậu)")
    };

    private static final DebugOption[] MONSTER_OPTIONS = {
        new DebugOption("syntax_error", "Syntax Error"),
        new DebugOption("null_pointer", "Null Pointer"),
        new DebugOption("infinite_loop", "Infinite Loop"),
        new DebugOption("stack_overflow", "Stack Overflow"),
        new DebugOption("libboss", "Library Boss"),
        new DebugOption("finalboss", "Final Boss (THT)")
    };

    private SelectionMode activeMode = SelectionMode.NONE;
    private SelectionMode previousMode = SelectionMode.NONE;
    private DebugOption[] currentOptions = null;
    private int selectedIndex = 0;

    public DebugUI() {
        this.uiCam = new OrthographicCamera();
        this.uiCam.setToOrtho(false, CAM_VIEW_WIDTH, CAM_VIEW_HEIGHT);
        this.uiCam.update();
    }

    public void startSelection(SelectionMode mode) {
        this.activeMode = mode;
        this.previousMode = mode;
        this.selectedIndex = 0;
        switch (mode) {
            case MAP:
                this.currentOptions = MAP_OPTIONS;
                break;
            case ITEM:
                this.currentOptions = ITEM_OPTIONS;
                break;
            case MONSTER:
                this.currentOptions = MONSTER_OPTIONS;
                break;
            default:
                this.activeMode = SelectionMode.NONE;
                this.previousMode = SelectionMode.NONE;
                this.currentOptions = null;
                break;
        }
    }

    public boolean isActive() {
        return activeMode != SelectionMode.NONE;
    }

    public SelectionMode getPreviousMode() {
        return previousMode;
    }

    public void cancelSelection() {
        this.activeMode = SelectionMode.NONE;
        this.currentOptions = null;
    }

    /**
     * Updates selection menu input.
     * @return selected DebugOption if confirmed, null otherwise.
     */
    public DebugOption handleSelectionInput() {
        if (!isActive() || currentOptions == null) {
            return null;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selectedIndex = (selectedIndex - 1 + currentOptions.length) % currentOptions.length;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selectedIndex = (selectedIndex + 1) % currentOptions.length;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            cancelSelection();
            return null;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            DebugOption selection = currentOptions[selectedIndex];
            cancelSelection();
            return selection;
        }

        return null;
    }

    public void render(final SpriteBatch batch, final ShapeRenderer shapeRenderer, final BitmapFont font) {
        if (!ProgressContext.instance.isShowDebug()) {
            return;
        }

        // Set projection matrix for UI rendering
        shapeRenderer.setProjectionMatrix(uiCam.combined);
        batch.setProjectionMatrix(uiCam.combined);

        // Render panels backgrounds and borders
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        // Main panel background
        shapeRenderer.setColor(BG_COLOR);
        shapeRenderer.rect(PANEL_X, PANEL_Y, PANEL_WIDTH, PANEL_HEIGHT);
        
        // Selection panel background if active
        if (isActive() && currentOptions != null) {
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
        if (isActive() && currentOptions != null) {
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
        if (isActive() && currentOptions != null) {
            font.setColor(TITLE_COLOR);
            String title = "SELECT " + activeMode.name();
            font.draw(batch, title, SELECT_X + 20f, SELECT_Y + SELECT_HEIGHT - 20f);

            font.setColor(Color.LIGHT_GRAY);
            font.draw(batch, "Arrows to move, Enter select, ESC", SELECT_X + 15f, SELECT_Y + SELECT_HEIGHT - 45f);

            for (int i = 0; i < currentOptions.length; i++) {
                if (i == selectedIndex) {
                    font.setColor(Color.WHITE);
                    font.draw(batch, "> " + currentOptions[i].displayName, SELECT_X + 20f, SELECT_Y + SELECT_HEIGHT - 75f - (i * 30f));
                } else {
                    font.setColor(Color.LIGHT_GRAY);
                    font.draw(batch, "  " + currentOptions[i].displayName, SELECT_X + 20f, SELECT_Y + SELECT_HEIGHT - 75f - (i * 30f));
                }
            }
        }

        batch.end();
    }
}
