package vn.hust.hustgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;
import java.util.List;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import vn.hust.hustgame.HustGame;
import vn.hust.hustgame.GameState;
import vn.hust.hustgame.entities.EntityFactory;
import vn.hust.hustgame.world.GameCamera;

public class BossRoomScreen extends PlayScreen {
    private Texture bossTexture;
    private int phase = 0; // 0: Cutscene, 1: Q&A, 2: Dodge, 3: Final, 4: Victory
    private Rectangle bossRect;
    private float bossHp = 1000;

    private final String[] dialogue = { "...Em da den.", "Ta nghe noi em da vuot qua thu vien... va phong lab.",
            "Bay gio... hay bao ve do an cua em." };
    private int dialogueIndex = 0;

    static class Question {
        String text;
        String[] answers;
        int correctIndex;

        Question(String t, String[] a, int c) {
            text = t;
            answers = a;
            correctIndex = c;
        }
    }

    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private Rectangle[] answerRects;
    private float answerTimer = 5f;

    private List<Rectangle> fallingPapers;

    private Stage stage;
    private TextField textField;
    private boolean typingPhase = false;

    private boolean victory = false;
    private float victoryTimer = 0f;

    public BossRoomScreen(HustGame game) {
        super(game);
    }

    @Override
    public void show() {
        super.show();
        loadMap("boss_room.tmx", 400, 100);
        bossTexture = game.getAssetManager().getTexture("Boss THT.png");

        bossRect = new Rectangle(350, 450, 100, 100);

        initBossContent();
        initUI();
    }

    private void initBossContent() {
        questions = new ArrayList<>();
        questions.add(new Question("Tai sao em chon thuat toan nay?",
                new String[] { "Em thay tren mang", "Em copy ban", "Do phuc tap phu hop" }, 2));
        questions.add(new Question("Dataset cua em co bao nhieu records?",
                new String[] { "Nhieu", "Chua dem", "10,847 records" }, 2));
        questions.add(new Question("Code O(n^2) - tai sao khong dung O(n log n)?",
                new String[] { "Khong biet", "Vi dataset nho", "Vi nhin quen hon" }, 1));
        questions.add(new Question("He thong deploy o dau?",
                new String[] { "Localhost", "Cloud voi CI/CD", "May ban em" }, 1));

        answerRects = new Rectangle[3];
        answerRects[0] = new Rectangle(100, 200, 150, 40);
        answerRects[1] = new Rectangle(325, 200, 150, 40);
        answerRects[2] = new Rectangle(550, 200, 150, 40);

        fallingPapers = new ArrayList<>();
    }

    private void initUI() {
        stage = new Stage(new ScreenViewport());
        Skin skin = new Skin();
        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(100, 30,
                com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
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
    }

    @Override
    protected void onUpdate(float delta) {
        if (GameState.instance.isInventoryOpen)
            return;

        float dt = delta;
        if (GameState.instance.hasNao && inputHandler.isQJustPressed())
            dt *= 0.3f;

        switch (phase) {
            case 0:
                updateCutscene();
                break;
            case 1:
            case 2:
                updateBattle(dt);
                break;
            case 3:
                updateFinalPhase(delta);
                break;
            case 4:
                victoryTimer += delta;
                break;
        }
    }

    private void updateCutscene() {
        if (inputHandler.isEnterJustPressed()) {
            dialogueIndex++;
            if (dialogueIndex >= dialogue.length)
                phase = 1;
        }
    }

    private void updateBattle(float dt) {
        player.setX(MathUtils.clamp(player.getX(), 0, 800));
        player.setY(MathUtils.clamp(player.getY(), 0, 600));

        if (currentQuestionIndex < questions.size()) {
            answerTimer -= dt;
            if (phase == 2)
                updateDodge(dt);

            if (answerTimer <= 0) {
                GameState.instance.hp -= 25;
                nextQuestion();
            } else if (inputHandler.isSpaceJustPressed()) {
                handleAnswerInput();
            }
        }
    }

    private void updateDodge(float dt) {
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

    private void handleAnswerInput() {
        for (int i = 0; i < 3; i++) {
            if (answerRects[i].contains(player.getX(), player.getY())) {
                if (i == questions.get(currentQuestionIndex).correctIndex) {
                    GameState.instance.hp += 10;
                    bossHp -= 150;
                } else {
                    GameState.instance.hp -= 25;
                }
                nextQuestion();
                break;
            }
        }
    }

    private void nextQuestion() {
        currentQuestionIndex++;
        answerTimer = 5f;
        checkPhase();
    }

    private void updateFinalPhase(float delta) {
        if (!typingPhase) {
            typingPhase = true;
            Gdx.input.setInputProcessor(stage);
            textField.setVisible(true);
            stage.setKeyboardFocus(textField);
        } else {
            stage.act(delta);
            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ENTER)) {
                String answer = textField.getText().trim().toUpperCase();
                if (answer.equals("PASS") || answer.equals("GRADUATE")) {
                    bossHp = 0;
                    victory = true;
                    phase = 4;
                    textField.setVisible(false);
                    Gdx.input.setInputProcessor(inputHandler);
                } else {
                    GameState.instance.hp -= 30;
                    textField.setText("");
                }
            }
        }
        stage.act(delta);
    }

    private void checkPhase() {
        if (bossHp <= 700 && phase == 1)
            phase = 2;
        if (bossHp <= 350 || currentQuestionIndex >= questions.size())
            phase = 3;
    }

    @Override
    protected void onDraw() {
        OrthographicCamera cam = gameCamera.getCamera();
        batch.setProjectionMatrix(cam.combined);
        shapeRenderer.setProjectionMatrix(cam.combined);

        // Map handles background via mapRenderer in PlayScreen.render()
        if (phase != 4)
            drawBoss();

        switch (phase) {
            case 0:
                drawCutscene();
                break;
            case 1:
            case 2:
                // Draw player in battle phases
                drawEntities();
                drawBattle();
                break;
            case 3:
                drawFinalPhase();
                break;
            case 4:
                drawVictory();
                break;
        }
    }

    private void drawBoss() {
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

    private void drawCutscene() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.8f);
        shapeRenderer.rect(100, 50, 600, 100);
        shapeRenderer.end();

        batch.begin();
        font.draw(batch, "T.H.T: " + dialogue[dialogueIndex], 120, 120);
        font.draw(batch, "[PRESS ENTER TO CONTINUE]", 500, 70);
        batch.end();
    }

    private void drawBattle() {
        if (currentQuestionIndex < questions.size()) {
            Question q = questions.get(currentQuestionIndex);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0, 0, 0, 0.7f);
            shapeRenderer.rect(100, 300, 600, 50);

            for (int i = 0; i < 3; i++) {
                shapeRenderer.setColor(Color.BLUE);
                shapeRenderer.rect(answerRects[i].x, answerRects[i].y, answerRects[i].width, answerRects[i].height);
            }

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
            for (Rectangle p : fallingPapers)
                shapeRenderer.rect(p.x, p.y, p.width, p.height);
            shapeRenderer.end();
            batch.begin();
            for (Rectangle p : fallingPapers) {
                font.setColor(Color.RED);
                font.draw(batch, "SAI", p.x, p.y + p.height / 2);
                font.setColor(Color.WHITE);
            }
            batch.end();
        }
    }

    private void drawFinalPhase() {
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
    }

    private void drawVictory() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(1, 1, 1, Math.min(1, victoryTimer / 2f)));
        shapeRenderer.rect(0, 0, 800, 600);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        if (victoryTimer > 3f) {
            batch.begin();
            font.setColor(Color.BLACK);
            font.draw(batch, "✅ ASSIGNMENT SUBMITTED SUCCESSFULLY", 250, 400);
            if (victoryTimer > 5f) {
                font.draw(batch, "Chuc mung em da qua mon - THT", 280, 200 + (victoryTimer - 5f) * 50);
            }
            font.setColor(Color.WHITE);
            batch.end();
        }
    }

    @Override
    public void dispose() {
        if (stage != null)
            stage.dispose();
        super.dispose();
    }
}
