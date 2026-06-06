package hust.adventure.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.viewport.FitViewport;

import hust.adventure.HustGame;
import hust.adventure.core.data.LevelConfig;

/**
 * MainMenuScreen represents the main landing menu of the game. It is structured into isolated, testable, and
 * SRP-compliant inner components.
 */
public class MainMenuScreen extends BaseScreen {

    private Stage stage;
    private Skin skin;
    private Texture backgroundTexture;

    /**
     * Interface defining button actions. MainMenuScreen supplies concrete lambdas.
     */
    public interface ButtonActionRouter {
        /** Triggered when the user starts the game. */
        void onStartGame();

        /** Triggered when the user opens the settings menu. */
        void onSettings();

        /** Triggered when the user exits the game. */
        void onExit();
    }

    /**
     * Factory class responsible for loading the default skin and compiling the Vietnamese fonts.
     */
    static class MenuSkinFactory {
        static Skin create() {
            Skin skin;
            try {
                skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
            } catch (GdxRuntimeException e) {
                Gdx.app.log("MenuSkinFactory", "Failed to load uiskin.json, creating empty skin", e);
                skin = new Skin();
            }

            // Create fonts with Vietnamese support
            BitmapFont menuFont = null;
            BitmapFont titleFont = null;
            FreeTypeFontGenerator generator = null;
            try {
                generator = new FreeTypeFontGenerator(Gdx.files.internal("ui/font.ttf"));
                final FreeTypeFontParameter parameter = new FreeTypeFontParameter();
                parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS
                        + "àáạảãâầấậẩẫăằắặẳẵèéẹẻẽêềếệểễìíịỉĩòóọỏõôồốộổỗơờớợởỡùúụủũưừứựửữỳýỵỷỹđÀÁẠẢÃÂẦẤẬẨẪĂẰẮẶẲẴÈÉẸẺẼÊỀẾỆỂỄÌÍỊỈĨÒÓỌỎÕÔỒỐỘỔỖƠỜỚỢỞỠÙÚỤỦŨƯỪỨỰỬỮỲÝỴỶỸĐ";

                // Generate button font
                parameter.size = 24;
                menuFont = generator.generateFont(parameter);

                // Generate title font
                parameter.size = 48;
                titleFont = generator.generateFont(parameter);
            } catch (Exception e) {
                Gdx.app.log("MenuSkinFactory", "Failed to generate FreeType fonts. Falling back to default BitmapFont.",
                        e);
                // Fallback to default BitmapFonts
                if (skin.has("default", BitmapFont.class)) {
                    menuFont = skin.getFont("default");
                    titleFont = skin.getFont("default");
                } else {
                    menuFont = new BitmapFont();
                    titleFont = new BitmapFont();
                }
            } finally {
                // Dispose font generator immediately to prevent native memory leak
                if (generator != null) {
                    generator.dispose();
                }
            }

            // Register fonts in skin
            skin.add("menu-font", menuFont);
            skin.add("title-font", titleFont);

            // Register LabelStyle for the menu title
            LabelStyle titleStyle = new LabelStyle();
            titleStyle.font = titleFont;
            titleStyle.fontColor = Color.YELLOW;
            skin.add("menu-title", titleStyle);

            // Register TextButtonStyle based on standard buttons but with custom menu-font
            TextButtonStyle defaultStyle = null;
            if (skin.has("default", TextButtonStyle.class)) {
                defaultStyle = skin.get("default", TextButtonStyle.class);
            }

            TextButtonStyle menuButtonStyle = new TextButtonStyle();
            if (defaultStyle != null) {
                menuButtonStyle.up = defaultStyle.up;
                menuButtonStyle.down = defaultStyle.down;
                menuButtonStyle.over = defaultStyle.over;
                menuButtonStyle.focused = defaultStyle.focused;
                menuButtonStyle.disabled = defaultStyle.disabled;
                menuButtonStyle.fontColor = defaultStyle.fontColor;
                menuButtonStyle.overFontColor = defaultStyle.overFontColor;
                menuButtonStyle.downFontColor = defaultStyle.downFontColor;
                menuButtonStyle.disabledFontColor = defaultStyle.disabledFontColor;
            }
            menuButtonStyle.font = menuFont;
            skin.add("menu-button", menuButtonStyle);

            return skin;
        }
    }

    /**
     * Layout builder that builds the layout table. Keeps layout separate from lifecycle and routing logic (DIP).
     */
    static class MenuLayoutBuilder {
        static Table build(final Skin skin, final ButtonActionRouter router) {
            final Table table = new Table();
            table.setFillParent(true);
            table.center();
            table.defaults().pad(12).width(240).height(50);

            // Menu Title Label
            final Label titleLabel = new Label("HUST ADVENTURE", skin, "menu-title");
            titleLabel.setAlignment(com.badlogic.gdx.utils.Align.center);

            // Add title row (spanning across default settings if needed, here just single column)
            table.add(titleLabel).width(600).height(80).padBottom(40).row();

            // Text Buttons
            final TextButton startBtn = new TextButton("Bắt đầu", skin, "menu-button");
            final TextButton settingsBtn = new TextButton("Cài đặt", skin, "menu-button");
            final TextButton exitBtn = new TextButton("Thoát", skin, "menu-button");

            // Attach listeners to router
            startBtn.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                    router.onStartGame();
                }
            });

            settingsBtn.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                    router.onSettings();
                }
            });

            exitBtn.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                    router.onExit();
                }
            });

            // Add buttons to table layout
            table.add(startBtn).row();
            table.add(settingsBtn).row();
            table.add(exitBtn).row();

            return table;
        }
    }

    public MainMenuScreen(final HustGame game) {
        super(game);
    }

    @Override
    public void show() {
        backgroundTexture = game.getAssetManager().getTexture("background.png");
        skin = MenuSkinFactory.create();

        final ButtonActionRouter router = new ButtonActionRouter() {
            @Override
            public void onStartGame() {
                final LevelConfig template = game.getLevelDataManager().getLevelConfig("FINAL_OUTSIDE");
                if (template != null) {
                    final LevelConfig config = new LevelConfig("FINAL_OUTSIDE", template.getName(),
                            template.getMapPath(), template.getSpawnX(), template.getSpawnY(), template.getZoom(),
                            template.getBgmPath(), template.getAmbientColor(), template.isInfinite());
                    final com.badlogic.gdx.Screen nextScreen = game.getLevelFactory().createLevel(game, config);
                    if (nextScreen != null) {
                        game.getScreenTransition().fadeOut(nextScreen, 0.5f);
                    }
                }
            }

            @Override
            public void onSettings() {
                Gdx.app.log("MainMenuScreen", "Settings button clicked. Not yet implemented.");
            }

            @Override
            public void onExit() {
                Gdx.app.exit();
            }
        };

        final Table table = MenuLayoutBuilder.build(skin, router);

        stage = new Stage(new FitViewport(800, 600));
        stage.addActor(table);

        Gdx.input.setInputProcessor(stage);

        if (game.getAudioManager() != null) {
            game.getAudioManager().playMusic("audio/music/menu.mp3", true);
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (stage == null) {
            return;
        }

        // Draw background texture fitting the stage viewport
        batch.setProjectionMatrix(stage.getViewport().getCamera().combined);
        batch.begin();
        if (backgroundTexture != null) {
            batch.draw(backgroundTexture, 0, 0, 800, 600);
        }
        batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        if (stage != null) {
            stage.getViewport().update(width, height, true);
        }
    }

    @Override
    public void dispose() {
        if (stage != null) {
            stage.dispose();
            stage = null;
        }
        if (skin != null) {
            skin.dispose();
            skin = null;
        }
        backgroundTexture = null;
    }
}
