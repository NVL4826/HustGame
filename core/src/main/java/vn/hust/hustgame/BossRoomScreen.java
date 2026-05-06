package vn.hust.hustgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;
import java.util.List;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class BossRoomScreen implements Screen {
    private MainGame game;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private Texture bgTexture;
    private Texture bossTexture;
    private BitmapFont font;

    private EntityManager entityManager;
    private Player player;
    private GameInputHandler inputHandler;

    // Túi đồ
    private InventoryUI inventoryUI;

    // Boss State
    private int phase = 0; // 0: Cutscene, 1: Q&A, 2: Dodge, 3: Final
    private Rectangle bossRect;
    private float bossHp = 1000;

    // Cutscene
    private String[] dialogue = {"...Em da den.", "Ta nghe noi em da vuot qua thu vien... va phong lab.", "Bay gio... hay bao ve do an cua em."};
    private int dialogueIndex = 0;

    // Phase 1 & 2 Q&A
    class Question {
        String text;
        String[] answers;
        int correctIndex;
        Question(String t, String[] a, int c) { text = t; answers = a; correctIndex = c; }
    }
    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private Rectangle[] answerRects;
    private float answerTimer = 5f;

    // Phase 2 Dodge
    private List<Rectangle> fallingPapers;
    private String[] paperTexts = {"SAI", "CHINH LAI", "THIEU REF"};

    // Phase 3 Final Code Puzzle
    private Stage stage;
    private TextField textField;
    private boolean typingPhase = false;

    // Victory
    private boolean victory = false;
    private float victoryTimer = 0f;

    public BossRoomScreen(MainGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);

        inventoryUI = new InventoryUI();

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        font.setColor(Color.WHITE);

        try {
            bgTexture = new Texture("Boss Room.jpg");
            bossTexture = new Texture("Boss THT.png");
        } catch(Exception e) {}

        entityManager = new EntityManager();
        inputHandler = new GameInputHandler();
        Gdx.input.setInputProcessor(inputHandler);

        player = new Player(400, 100, GameState.instance.globalInventory, inputHandler, null);
        entityManager.addEntity(player);

        bossRect = new Rectangle(350, 450, 100, 100); // placeholder size if texture fails

        questions = new ArrayList<>();
        questions.add(new Question("Tai sao em chon thuat toan nay?", new String[]{"Em thay tren mang", "Em copy ban", "Do phuc tap phu hop"}, 2));
        questions.add(new Question("Dataset cua em co bao nhieu records?", new String[]{"Nhieu", "Chua dem", "10,847 records"}, 2));
        questions.add(new Question("Code O(n^2) - tai sao khong dung O(n log n)?", new String[]{"Khong biet", "Vi dataset nho", "Vi nhin quen hon"}, 1));
        questions.add(new Question("He thong deploy o dau?", new String[]{"Localhost", "Cloud voi CI/CD", "May ban em"}, 1));

        answerRects = new Rectangle[3];
        answerRects[0] = new Rectangle(100, 200, 150, 40);
        answerRects[1] = new Rectangle(325, 200, 150, 40);
        answerRects[2] = new Rectangle(550, 200, 150, 40);

        fallingPapers = new ArrayList<>();

        // Setup Stage for UI (Text input)
        stage = new Stage(new ScreenViewport());
        Skin skin = new Skin();
        // Create basic skin elements programmatically (normally loaded from json)
        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(100, 30, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        skin.add("white", new Texture(pixmap));
        skin.add("default", new BitmapFont());
        TextField.TextFieldStyle tfs = new TextField.TextFieldStyle();
        tfs.font = skin.getFont("default");
        tfs.fontColor = Color.BLACK;
        tfs.background = skin.newDrawable("white", Color.WHITE);
        tfs.cursor = skin.newDrawable("white", Color.BLACK);

        textField = new TextField("", tfs);
        textField.setPosition(300, 200);
        textField.setSize(200, 40);
        textField.setVisible(false);
        stage.addActor(textField);

        // In cutscene, we wait for ENTER
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float dt = delta;
        if (GameState.instance.hasNao && Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            // artifact slow mo
            dt *= 0.3f;
        }

        // Update
        if (phase == 0) {
            // Cutscene
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                dialogueIndex++;
                if (dialogueIndex >= dialogue.length) {
                    phase = 1;
                }
            }
        } else if (phase == 1 || phase == 2) {
            entityManager.update(dt);
            player.setX(MathUtils.clamp(player.getX(), 0, 800));
            player.setY(MathUtils.clamp(player.getY(), 0, 600));

            if (currentQuestionIndex < questions.size()) {
                answerTimer -= dt;

                if (phase == 2) {
                    // Spawn falling papers
                    if (MathUtils.random() < 2f * dt) {
                        fallingPapers.add(new Rectangle(MathUtils.random(100, 700), 600, 30, 40));
                    }
                    Rectangle pBounds = new Rectangle(player.getX() - 25, player.getY() - 25, 50, 50);
                    for (int i = fallingPapers.size() - 1; i >= 0; i--) {
                        Rectangle paper = fallingPapers.get(i);
                        paper.y -= 150 * dt;
                        if (paper.overlaps(pBounds)) {
                            GameState.instance.hp -= 10;
                            fallingPapers.remove(i);
                        } else if (paper.y < 0) {
                            fallingPapers.remove(i);
                        }
                    }
                }

                if (answerTimer <= 0) {
                    // Time out
                    GameState.instance.hp -= 25;
                    currentQuestionIndex++;
                    answerTimer = 5f;
                    checkPhase();
                } else {
                    // Check if player stands on answer and press SPACE or just stands
                    // Let's say player must press SPACE on it
                    if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                        for (int i = 0; i < 3; i++) {
                            if (answerRects[i].contains(player.getX(), player.getY())) {
                                if (i == questions.get(currentQuestionIndex).correctIndex) {
                                    GameState.instance.hp += 10;
                                    bossHp -= 150;
                                } else {
                                    GameState.instance.hp -= 25;
                                }
                                currentQuestionIndex++;
                                answerTimer = 5f;
                                checkPhase();
                                break;
                            }
                        }
                    }
                }
            }
        } else if (phase == 3) {
            if (!typingPhase) {
                typingPhase = true;
                Gdx.input.setInputProcessor(stage);
                textField.setVisible(true);
                stage.setKeyboardFocus(textField);
            } else {
                if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                    String answer = textField.getText().trim().toUpperCase();
                    if (answer.equals("PASS") || answer.equals("GRADUATE")) {
                        bossHp = 0;
                        victory = true;
                        phase = 4;
                        textField.setVisible(false);
                        Gdx.input.setInputProcessor(inputHandler); // restore
                    } else {
                        GameState.instance.hp -= 30;
                        textField.setText("");
                    }
                }
            }
            stage.act(delta);
        } else if (phase == 4) {
            // Victory
            victoryTimer += delta;
        }

        // Draw
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        batch.begin();
        if (bgTexture != null) {
            if (victory) batch.setColor(1f, 1f, 1f, Math.max(0, 1 - victoryTimer/3f));
            batch.draw(bgTexture, 0, 0, 800, 600);
            batch.setColor(Color.WHITE);
        }
        batch.end();

        if (phase != 4) {
            batch.begin();
            if (bossTexture != null) {
                batch.draw(bossTexture, bossRect.x, bossRect.y, bossRect.width, bossRect.height);
            } else {
                batch.end();
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                shapeRenderer.setColor(Color.RED);
                shapeRenderer.rect(bossRect.x, bossRect.y, bossRect.width, bossRect.height);
                shapeRenderer.end();
                batch.begin();
            }
            batch.end();

            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.rect(200, 580, 400, 10);
            shapeRenderer.setColor(Color.GREEN);
            shapeRenderer.rect(200, 580, (bossHp / 1000f) * 400, 10);
            shapeRenderer.end();
        }

        if (phase == 0) {
            // Draw dialogue box
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0, 0, 0, 0.8f);
            shapeRenderer.rect(100, 50, 600, 100);
            shapeRenderer.end();

            batch.begin();
            font.draw(batch, "T.H.T: " + dialogue[dialogueIndex], 120, 120);
            font.draw(batch, "[PRESS ENTER TO CONTINUE]", 500, 70);
            batch.end();
        } else if (phase == 1 || phase == 2) {
            batch.begin();
            entityManager.draw(batch);
            batch.end();

            if (currentQuestionIndex < questions.size()) {
                Question q = questions.get(currentQuestionIndex);

                // Question UI
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                shapeRenderer.setColor(0, 0, 0, 0.7f);
                shapeRenderer.rect(100, 300, 600, 50);

                for (int i = 0; i < 3; i++) {
                    shapeRenderer.setColor(Color.BLUE);
                    shapeRenderer.rect(answerRects[i].x, answerRects[i].y, answerRects[i].width, answerRects[i].height);
                }

                // Timer bar
                shapeRenderer.setColor(Color.YELLOW);
                shapeRenderer.rect(100, 280, (answerTimer / 5f) * 600, 5);
                shapeRenderer.end();

                batch.begin();
                font.draw(batch, q.text, 120, 335);
                for (int i = 0; i < 3; i++) {
                    font.draw(batch, q.answers[i], answerRects[i].x + 10, answerRects[i].y + 25);
                }
                batch.end();
            }

            if (phase == 2) {
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                shapeRenderer.setColor(Color.WHITE);
                for (Rectangle p : fallingPapers) shapeRenderer.rect(p.x, p.y, p.width, p.height);
                shapeRenderer.end();
                batch.begin();
                for (Rectangle p : fallingPapers) {
                    font.setColor(Color.RED);
                    font.draw(batch, "SAI", p.x, p.y + p.height/2);
                    font.setColor(Color.WHITE);
                }
                batch.end();
            }
        } else if (phase == 3) {
            // Draw code puzzle box
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0, 0, 0, 0.8f);
            shapeRenderer.rect(200, 200, 400, 150);
            shapeRenderer.end();

            batch.begin();
            font.draw(batch, "if (codeWorks && studentUnderstands) {", 220, 330);
            font.draw(batch, "    return ???;", 220, 300);
            font.draw(batch, "}", 220, 270);
            batch.end();

            stage.draw();
        } else if (phase == 4) {
            // White screen victory
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(new Color(1, 1, 1, Math.min(1, victoryTimer/2f)));
            shapeRenderer.rect(0, 0, 800, 600);
            shapeRenderer.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);

            if (victoryTimer > 3f) {
                batch.begin();
                font.setColor(Color.BLACK);
                font.draw(batch, "✅ ASSIGNMENT SUBMITTED SUCCESSFULLY", 250, 400);
                if (victoryTimer > 5f) {
                    font.draw(batch, "Chuc mung em da qua mon - THT", 280, 200 + (victoryTimer-5f)*50);
                }
                font.setColor(Color.WHITE);
                batch.end();
            }
        }

        // HUD
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(10, 580, 100, 10);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(10, 580, (GameState.instance.hp / GameState.instance.maxHp) * 100, 10);
        shapeRenderer.end();

        inventoryUI.render(player, batch, shapeRenderer, font);
    }

    private void checkPhase() {
        if (bossHp <= 700 && phase == 1) {
            phase = 2; // Move to Dodge phase
        }
        if (bossHp <= 350 || currentQuestionIndex >= questions.size()) {
            phase = 3; // Final code phase
        }
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        if (bgTexture != null) bgTexture.dispose();
        if (bossTexture != null) bossTexture.dispose();
        font.dispose();
        entityManager.dispose();
        if (stage != null) stage.dispose();
    }
}
