package hust.adventure.screens.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import hust.adventure.core.context.ProgressContext;
import hust.adventure.entities.enemies.FinalBoss;
import hust.adventure.screens.LoadingScreen;

import java.util.ArrayList;
import java.util.List;

/**
 * Behavior class for the final Boss Fight level, managing dialogues, Q&A, and typing phases.
 */
public class BossFightBehavior implements LevelBehavior {
    // Phase constants
    private static final int PHASE_CUTSCENE = 0;
    private static final int PHASE_QA = 1;
    private static final int PHASE_DODGE = 2;
    private static final int PHASE_FINAL = 3;
    private static final int PHASE_VICTORY = 4;

    // Boss fight parameter constants
    private static final float INITIAL_BOSS_HP = 1000f;
    private static final float QUESTION_TIMER_RESET = 5f;
    private static final float ANSWER_HEAL = 10f;
    private static final float ANSWER_DAMAGE_DEALT = 150f;
    private static final float WRONG_ANSWER_DAMAGE = 25f;
    private static final float TIME_OUT_DAMAGE = 25f;
    private static final float FINAL_PHASE_WRONG_DAMAGE = 30f;
    private static final float PAPER_DODGE_DAMAGE = 10f;

    private static final String[] PAPER_TEXTS = { "SAI", "CHINH LAI", "THIEU REF" };

    private FinalBoss finalBoss;
    private int phase = PHASE_CUTSCENE;

    private final String[] dialogue = { "...Em da den.", "Ta nghe noi em da vuot qua thu vien... va phong lab.",
            "Bay gio... hay bao ve do an cua em." };
    private int dialogueIndex = 0;

    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private Rectangle[] answerRects;
    private float answerTimer = QUESTION_TIMER_RESET;

    // Phase 2 Dodge
    private List<FallingPaper> fallingPapers;

    // Background texture
    private Texture bgTexture;

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

    static class FallingPaper {
        final Rectangle rect;
        final String text;

        FallingPaper(final Rectangle r, final String t) {
            this.rect = r;
            this.text = t;
        }
    }

    @Override
    public void init(final LevelContext context) {
        finalBoss = context.getEntityFactory().createFinalBoss(350, 450,
                context.getGame().getAssetManager().getTexture("Boss THT.png"));

        bgTexture = context.getGame().getAssetManager().getTexture("Boss Room.jpg");

        initBossContent();
        initUI(context);
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

    private void initUI(final LevelContext context) {
        stage = new Stage(new ScreenViewport());
        final Skin skin = new Skin();
        final Pixmap pixmap = new Pixmap(100, 30, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        skin.add("white", new Texture(pixmap));
        skin.add("default", context.getFont());

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
    public void update(final LevelContext context, final float delta) {
        float dt = delta;
        if (ProgressContext.instance.isHasNao() && context.getInputReader().isQJustPressed()) {
            dt *= 0.3f;
        }

        switch (phase) {
        case PHASE_CUTSCENE:
            updateCutscene(context);
            break;
        case PHASE_QA:
        case PHASE_DODGE:
            updateBattle(context, dt);
            break;
        case PHASE_FINAL:
            updateFinalPhase(context, delta);
            break;
        case PHASE_VICTORY:
            victoryTimer += delta;
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                context.getGame().setScreen(new LoadingScreen(context.getGame()));
            }
            break;
        }
    }

    private void updateCutscene(final LevelContext context) {
        if (context.getInputReader().isEnterJustPressed()) {
            dialogueIndex++;
            if (dialogueIndex >= dialogue.length) {
                phase = PHASE_QA;
            }
        }
    }

    private void updateBattle(final LevelContext context, float dt) {
        if (currentQuestionIndex < questions.size()) {
            answerTimer -= dt;
            if (phase == PHASE_DODGE) {
                updateDodge(context, dt);
            }

            if (answerTimer <= 0) {
                context.getPlayer().takeDamage(TIME_OUT_DAMAGE, false, true);
                nextQuestion();
            } else if (context.getInputReader().isSpaceJustPressed()) {
                handleAnswerInput(context);
            }
        }
    }

    private void updateDodge(final LevelContext context, float dt) {
        // Spawn falling papers
        if (MathUtils.random() < 2f * dt) {
            final float startX = MathUtils.random(100f, 700f);
            final String text = PAPER_TEXTS[MathUtils.random(PAPER_TEXTS.length - 1)];
            fallingPapers.add(new FallingPaper(new Rectangle(startX, 600f, 80f, 30f), text));
        }

        // Check collision with player
        final Rectangle playerBounds = new Rectangle(context.getPlayer().getX() - 25f, context.getPlayer().getY() - 25f,
                50f, 50f);

        for (int i = fallingPapers.size() - 1; i >= 0; i--) {
            final FallingPaper paper = fallingPapers.get(i);
            paper.rect.y -= 150f * dt;
            if (paper.rect.overlaps(playerBounds)) {
                context.getPlayer().takeDamage(PAPER_DODGE_DAMAGE);
                fallingPapers.remove(i);
            } else if (paper.rect.y < 0) {
                fallingPapers.remove(i);
            }
        }
    }

    private void handleAnswerInput(final LevelContext context) {
        for (int i = 0; i < 3; i++) {
            if (answerRects[i].contains(context.getPlayer().getX(), context.getPlayer().getY())) {
                if (i == questions.get(currentQuestionIndex).correctIndex) {
                    context.getPlayer().heal(ANSWER_HEAL);
                    finalBoss.takeDamage(ANSWER_DAMAGE_DEALT);
                } else {
                    context.getPlayer().takeDamage(WRONG_ANSWER_DAMAGE, false, true);
                }
                nextQuestion();
                break;
            }
        }
    }

    private void nextQuestion() {
        currentQuestionIndex++;
        answerTimer = QUESTION_TIMER_RESET;
        checkPhase();
    }

    private void updateFinalPhase(final LevelContext context, float delta) {
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
                    phase = PHASE_VICTORY;
                    textField.setVisible(false);
                    Gdx.input.setInputProcessor(context.getInputReader());
                } else {
                    context.getPlayer().takeDamage(FINAL_PHASE_WRONG_DAMAGE, false, true);
                    textField.setText("");
                }
            }
        }
    }

    private void checkPhase() {
        if (finalBoss.getHp() <= 700f && phase == PHASE_QA) {
            phase = PHASE_DODGE;
        }
        if (finalBoss.getHp() <= 350f || currentQuestionIndex >= questions.size()) {
            phase = PHASE_FINAL;
        }
    }

    @Override
    public void draw(final LevelContext context) {

        // Render Boss HP bar at the top of the screen (phases 0, 1, 2, 3)
        if (phase != PHASE_VICTORY && finalBoss != null) {
            context.getShapeRenderer().setProjectionMatrix(context.getCamera().combined);
            context.getShapeRenderer().begin(ShapeType.Filled);
            context.getShapeRenderer().setColor(Color.RED);
            context.getShapeRenderer().rect(200f, 580f, 400f, 10f);
            context.getShapeRenderer().setColor(Color.GREEN);
            context.getShapeRenderer().rect(200f, 580f, (finalBoss.getHp() / finalBoss.getMaxHp()) * 400f, 10f);
            context.getShapeRenderer().end();
        }

        switch (phase) {
        case PHASE_CUTSCENE:
            drawCutscene(context);
            break;
        case PHASE_QA:
        case PHASE_DODGE:
            drawBattle(context);
            break;
        case PHASE_FINAL:
            drawFinalPhase(context);
            break;
        case PHASE_VICTORY:
            drawVictory(context);
            break;
        }
    }

    private void drawCutscene(final LevelContext context) {
        // Draw dialogue box background
        context.getShapeRenderer().setProjectionMatrix(context.getCamera().combined);
        context.getShapeRenderer().begin(ShapeType.Filled);
        context.getShapeRenderer().setColor(0f, 0f, 0f, 0.8f);
        context.getShapeRenderer().rect(100f, 50f, 600f, 100f);
        context.getShapeRenderer().end();

        // Draw dialogue texts
        context.getGame().getSpriteBatch().setProjectionMatrix(context.getCamera().combined);
        context.getGame().getSpriteBatch().begin();
        if (dialogueIndex < dialogue.length) {
            context.getGame().getFont().setColor(Color.WHITE);
            context.getGame().getFont().draw(context.getGame().getSpriteBatch(), "T.H.T: " + dialogue[dialogueIndex],
                    120f, 120f);
            context.getGame().getFont().draw(context.getGame().getSpriteBatch(), "[PRESS ENTER TO CONTINUE]", 500f,
                    70f);
        }
        context.getGame().getSpriteBatch().end();
    }

    private void drawBattle(final LevelContext context) {
        if (currentQuestionIndex < questions.size()) {
            final Question q = questions.get(currentQuestionIndex);

            // 1. Draw UI boxes using ShapeRenderer
            context.getShapeRenderer().setProjectionMatrix(context.getCamera().combined);
            context.getShapeRenderer().begin(ShapeType.Filled);

            // Question background (semi-transparent black)
            context.getShapeRenderer().setColor(0f, 0f, 0f, 0.7f);
            context.getShapeRenderer().rect(100f, 300f, 600f, 50f);

            // Answer backgrounds (blue)
            for (int i = 0; i < 3; i++) {
                context.getShapeRenderer().setColor(Color.BLUE);
                context.getShapeRenderer().rect(answerRects[i].x, answerRects[i].y, answerRects[i].width,
                        answerRects[i].height);
            }

            // Timer bar (yellow)
            context.getShapeRenderer().setColor(Color.YELLOW);
            context.getShapeRenderer().rect(100f, 280f, (answerTimer / QUESTION_TIMER_RESET) * 600f, 5f);
            context.getShapeRenderer().end();

            // Outline of answer boxes
            context.getShapeRenderer().begin(ShapeType.Line);
            context.getShapeRenderer().setColor(Color.WHITE);
            for (int i = 0; i < 3; i++) {
                context.getShapeRenderer().rect(answerRects[i].x, answerRects[i].y, answerRects[i].width,
                        answerRects[i].height);
            }
            context.getShapeRenderer().end();

            // 2. Draw texts using SpriteBatch
            context.getGame().getSpriteBatch().setProjectionMatrix(context.getCamera().combined);
            context.getGame().getSpriteBatch().begin();
            context.getGame().getFont().setColor(Color.WHITE);
            context.getGame().getFont().draw(context.getGame().getSpriteBatch(), q.text, 120f, 335f);
            for (int i = 0; i < q.answers.length; i++) {
                context.getGame().getFont().draw(context.getGame().getSpriteBatch(), (i + 1) + ". " + q.answers[i],
                        answerRects[i].x + 10f, answerRects[i].y + 25f);
            }
            context.getGame().getFont().draw(context.getGame().getSpriteBatch(), "[Đứng vào ô đáp án và nhấn Space]",
                    250f, 150f);
            context.getGame().getSpriteBatch().end();
        }

        // Draw falling papers in phase 2
        if (phase == PHASE_DODGE) {
            // Draw background rectangles for papers
            context.getShapeRenderer().setProjectionMatrix(context.getCamera().combined);
            context.getShapeRenderer().begin(ShapeType.Filled);
            context.getShapeRenderer().setColor(Color.WHITE);
            for (final FallingPaper paper : fallingPapers) {
                context.getShapeRenderer().rect(paper.rect.x, paper.rect.y, paper.rect.width, paper.rect.height);
            }
            context.getShapeRenderer().end();

            // Draw texts on papers
            context.getGame().getSpriteBatch().setProjectionMatrix(context.getCamera().combined);
            context.getGame().getSpriteBatch().begin();
            context.getGame().getFont().setColor(Color.RED);
            for (final FallingPaper paper : fallingPapers) {
                context.getGame().getFont().draw(context.getGame().getSpriteBatch(), paper.text, paper.rect.x + 5f,
                        paper.rect.y + 20f);
            }
            context.getGame().getFont().setColor(Color.WHITE);
            context.getGame().getSpriteBatch().end();
        }
    }

    private void drawFinalPhase(final LevelContext context) {
        // Draw code puzzle box background
        context.getShapeRenderer().setProjectionMatrix(context.getCamera().combined);
        context.getShapeRenderer().begin(ShapeType.Filled);
        context.getShapeRenderer().setColor(0f, 0f, 0f, 0.8f);
        context.getShapeRenderer().rect(200f, 200f, 400f, 150f);
        context.getShapeRenderer().end();

        // Draw code puzzle texts
        context.getGame().getSpriteBatch().setProjectionMatrix(context.getCamera().combined);
        context.getGame().getSpriteBatch().begin();
        context.getGame().getFont().setColor(Color.WHITE);
        context.getGame().getFont().draw(context.getGame().getSpriteBatch(), "if (codeWorks && studentUnderstands) {",
                220f, 330f);
        context.getGame().getFont().draw(context.getGame().getSpriteBatch(), "    return ???;", 220f, 300f);
        context.getGame().getFont().draw(context.getGame().getSpriteBatch(), "}", 220f, 270f);
        context.getGame().getSpriteBatch().end();

        stage.draw();
    }

    private void drawVictory(final LevelContext context) {
        // White screen victory fade-in
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        context.getShapeRenderer().setProjectionMatrix(context.getCamera().combined);
        context.getShapeRenderer().begin(ShapeType.Filled);
        context.getShapeRenderer().setColor(new Color(1f, 1f, 1f, Math.min(1f, victoryTimer / 2f)));
        context.getShapeRenderer().rect(0f, 0f, 800f, 600f);
        context.getShapeRenderer().end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        context.getGame().getSpriteBatch().setProjectionMatrix(context.getCamera().combined);
        context.getGame().getSpriteBatch().begin();

        if (victoryTimer > 3f) {
            context.getGame().getFont().setColor(Color.BLACK);
            context.getGame().getFont().draw(context.getGame().getSpriteBatch(), "✅ ASSIGNMENT SUBMITTED SUCCESSFULLY",
                    250f, 400f);
            if (victoryTimer > 5f) {
                context.getGame().getFont().draw(context.getGame().getSpriteBatch(), "Chuc mung em da qua mon - THT",
                        280f, 200f + (victoryTimer - 5f) * 50f);
            }
            context.getGame().getFont().draw(context.getGame().getSpriteBatch(), "[Nhấn Esc để về Menu]", 300f, 100f);
        } else {
            // Classic Vietnamese victory message during fade-in
            context.getGame().getFont().setColor(Color.YELLOW);
            context.getGame().getFont().draw(context.getGame().getSpriteBatch(),
                    "CHIẾN THẮNG!!! CHÚC MỪNG BẠN ĐÃ TỐT NGHIỆP!", 100f, 300f);
            context.getGame().getFont().draw(context.getGame().getSpriteBatch(),
                    "Thời gian kết thúc: " + (int) victoryTimer + "s", 100f, 250f);
        }
        context.getGame().getSpriteBatch().end();
    }

    @Override
    public void dispose(final LevelContext context) {
        if (stage != null) {
            stage.dispose();
        }
    }
}
