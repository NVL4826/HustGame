package hust.adventure.screens.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.graphics.Pixmap;
import hust.adventure.HustGame;
import hust.adventure.core.config.LevelConfig;
import hust.adventure.core.context.ProgressContext;
import hust.adventure.entities.enemies.FinalBoss;
import java.util.ArrayList;
import java.util.List;

/**
 * Final boss fight screen with unique mechanics and Q&A.
 */
public class BossFightLevel extends BaseLevelScreen {
    private FinalBoss finalBoss;
    private int phase = 0; // 0: Cutscene, 1: Q&A, 2: Dodge, 3: Final, 4: Victory

    private final String[] dialogue = { "...Em da den.", "Ta nghe noi em da vuot qua thu vien... va phong lab.",
            "Bay gio... hay bao ve do an cua em." };
    private int dialogueIndex = 0;

    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private Rectangle[] answerRects;
    private float answerTimer = 5f;

    private Stage stage;
    private TextField textField;
    private boolean typingPhase = false;
    private boolean victory = false;
    private float victoryTimer = 0f;

    static class Question {
        final String text;
        final String[] answers;
        final int correctIndex;

        Question(final String t, final String[] a, final int c) {
            this.text = t;
            this.answers = a;
            this.correctIndex = c;
        }
    }

    public BossFightLevel(final HustGame game, final LevelConfig config) {
        super(game, config);
    }

    @Override
    protected void initLevel() {
        finalBoss = entityFactory.createFinalBoss(350, 450,
                game.getAssetManager().getTexture("Boss THT.png"));

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
    }

    private void initUI() {
        stage = new Stage(new ScreenViewport());
        final Skin skin = new Skin();
        final Pixmap pixmap = new Pixmap(100, 30, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        skin.add("white", new Texture(pixmap));
        skin.add("default", game.getFont());

        final TextField.TextFieldStyle tfs = new TextField.TextFieldStyle();
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
    protected void updateLevel(float delta) {
        float dt = delta;
        if (ProgressContext.instance.hasNao && inputReader.isQJustPressed())
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
        if (inputReader.isEnterJustPressed()) {
            dialogueIndex++;
            if (dialogueIndex >= dialogue.length)
                phase = 1;
        }
    }

    private void updateBattle(float dt) {
        if (currentQuestionIndex < questions.size()) {
            answerTimer -= dt;
            if (phase == 2)
                updateDodge(dt);

            if (answerTimer <= 0) {
                player.takeDamage(25);
                nextQuestion();
            } else if (inputReader.isSpaceJustPressed()) {
                handleAnswerInput();
            }
        }
    }

    private void updateDodge(float dt) {
        if (MathUtils.random() < 2f * dt) {
            final float startX = MathUtils.random(100, VIEW_WIDTH - 100);
            entityFactory.createProjectile(startX, VIEW_HEIGHT, 0, -150, 10, Color.WHITE, false);
        }
    }

    private void handleAnswerInput() {
        for (int i = 0; i < 3; i++) {
            if (answerRects[i].contains(player.getX(), player.getY())) {
                if (i == questions.get(currentQuestionIndex).correctIndex) {
                    player.heal(10);
                    finalBoss.takeDamage(150);
                } else {
                    player.takeDamage(25);
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
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                final String answer = textField.getText().trim().toUpperCase();
                if (answer.equals("PASS") || answer.equals("GRADUATE")) {
                    finalBoss.takeDamage(finalBoss.getHp());
                    victory = true;
                    phase = 4;
                    textField.setVisible(false);
                    Gdx.input.setInputProcessor(inputReader);
                } else {
                    player.takeDamage(30);
                    textField.setText("");
                }
            }
        }
    }

    private void checkPhase() {
        if (finalBoss.getHp() <= 700 && phase == 1)
            phase = 2;
        if (finalBoss.getHp() <= 350 || currentQuestionIndex >= questions.size())
            phase = 3;
    }

    @Override
    protected void drawLevel() {
        switch (phase) {
        case 0:
            drawCutscene();
            break;
        case 1:
        case 2:
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

    private void drawCutscene() {
        game.getSpriteBatch().setProjectionMatrix(getCamera().combined);
        game.getSpriteBatch().begin();
        if (dialogueIndex < dialogue.length) {
            game.getFont().setColor(Color.WHITE);
            game.getFont().draw(game.getSpriteBatch(), dialogue[dialogueIndex], 100, 300);
            game.getFont().draw(game.getSpriteBatch(), "[Nhấn Enter để tiếp tục]", 300, 200);
        }
        game.getSpriteBatch().end();
    }

    private void drawBattle() {
        game.getSpriteBatch().setProjectionMatrix(getCamera().combined);
        game.getSpriteBatch().begin();
        if (currentQuestionIndex < questions.size()) {
            Question q = questions.get(currentQuestionIndex);
            game.getFont().setColor(Color.WHITE);
            game.getFont().draw(game.getSpriteBatch(), "CÂU HỎI: " + q.text, 100, 400);
            game.getFont().draw(game.getSpriteBatch(), "Thời gian: " + (int) answerTimer + "s", 100, 430);

            for (int i = 0; i < q.answers.length; i++) {
                game.getFont().draw(game.getSpriteBatch(), (i + 1) + ". " + q.answers[i], answerRects[i].x,
                        answerRects[i].y + 25);
            }
            game.getFont().draw(game.getSpriteBatch(), "[Đứng vào ô đáp án và nhấn Space]", 250, 150);
        }
        game.getSpriteBatch().end();

        shapeRenderer.setProjectionMatrix(getCamera().combined);
        shapeRenderer.begin(com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Line);
        for (int i = 0; i < 3; i++) {
            shapeRenderer.setColor(Color.YELLOW);
            shapeRenderer.rect(answerRects[i].x, answerRects[i].y, answerRects[i].width, answerRects[i].height);
        }
        shapeRenderer.end();
    }

    private void drawFinalPhase() {
        stage.draw();
    }

    private void drawVictory() {
        game.getSpriteBatch().setProjectionMatrix(getCamera().combined);
        game.getSpriteBatch().begin();
        game.getFont().setColor(Color.YELLOW);
        game.getFont().draw(game.getSpriteBatch(), "CHIẾN THẮNG!!! CHÚC MỪNG BẠN ĐÃ TỐT NGHIỆP!", 100, 300);
        game.getFont().draw(game.getSpriteBatch(), "Thời gian kết thúc: " + (int) victoryTimer + "s", 100, 250);
        game.getFont().draw(game.getSpriteBatch(), "[Nhấn Esc để về Menu]", 100, 200);
        game.getSpriteBatch().end();
    }

    @Override
    public void dispose() {
        if (stage != null)
            stage.dispose();
        super.dispose();
    }
}
