package hust.adventure.ui.puzzle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import hust.adventure.events.EventDispatcher;
import hust.adventure.events.EventType;
import hust.adventure.events.GameEvent;
import hust.adventure.screens.levels.LevelContext;

/**
 * Speed Math (Addition Only) mini-game puzzle.
 */
public class SpeedMathPuzzle implements PuzzleGame {
    private static final int MAX_ROUNDS = 15;
    private static final float FEEDBACK_DURATION = 0.5f;

    public enum MathState {
        AWAITING_INPUT,
        CORRECT_FEEDBACK,
        WRONG_FEEDBACK
    }

    private MathState state;
    private LevelContext context;
    private boolean isSolved;

    private int currentRound;
    private float timeRemaining;
    private final float[] roundTimeLimits = new float[MAX_ROUNDS];

    // Math operands
    private int operandA;
    private int operandB;
    private int correctAnswer;

    // Feedback
    private float feedbackTimer;
    private boolean isTimeout;

    // Buffers for zero-allocation
    private final StringBuilder answerBuilder;
    private final StringBuilder renderBuilder;
    private Texture textBoxTexture;
    private final GlyphLayout textLayout = new GlyphLayout();

    public SpeedMathPuzzle() {
        this.state = MathState.AWAITING_INPUT;
        this.isSolved = false;
        this.currentRound = 1;
        this.answerBuilder = new StringBuilder();
        this.renderBuilder = new StringBuilder();
    }

    @Override
    public String getBackgroundPath() {
        return "Library1.jpg";
    }

    @Override
    public void init(final LevelContext ctx) {
        this.context = ctx;

        // Compute round time limits
        // Rounds 1-10: linearly from 10.0s to 3.0s
        for (int i = 0; i < 10; i++) {
            roundTimeLimits[i] = 10.0f - (i * (10.0f - 3.0f) / 9f);
        }
        // Rounds 11-15: linearly from 10.0s to 4.0s
        for (int i = 10; i < 15; i++) {
            final int tierIdx = i - 10;
            roundTimeLimits[i] = 10.0f - (tierIdx * (10.0f - 4.0f) / 4f);
        }

        textBoxTexture = ctx.getGame().getAssetManager().getTexture("text_box.png");

        reset();
    }

    @Override
    public void reset() {
        this.currentRound = 1;
        this.isSolved = false;
        this.feedbackTimer = 0f;
        this.isTimeout = false;
        this.answerBuilder.setLength(0);
        this.state = MathState.AWAITING_INPUT;
        generateQuestion();
    }

    private void generateQuestion() {
        if (currentRound <= 10) {
            operandA = MathUtils.random(10, 99);
            operandB = MathUtils.random(10, 99);
        } else {
            operandA = MathUtils.random(100, 999);
            operandB = MathUtils.random(100, 999);
        }
        correctAnswer = operandA + operandB;
        timeRemaining = roundTimeLimits[currentRound - 1];
    }

    @Override
    public void update(final float delta) {
        if (isSolved) {
            return;
        }

        if (state == MathState.AWAITING_INPUT) {
            timeRemaining -= delta;
            if (timeRemaining <= 0) {
                isTimeout = true;
                playWrongSound();
                state = MathState.WRONG_FEEDBACK;
                feedbackTimer = FEEDBACK_DURATION;
                return;
            }

            handleKeyboardInput();
        } else {
            // Processing feedback state timers
            feedbackTimer -= delta;
            if (feedbackTimer <= 0) {
                if (state == MathState.CORRECT_FEEDBACK) {
                    if (currentRound == MAX_ROUNDS) {
                        isSolved = true;
                    } else {
                        currentRound++;
                        answerBuilder.setLength(0);
                        generateQuestion();
                        state = MathState.AWAITING_INPUT;
                    }
                } else if (state == MathState.WRONG_FEEDBACK) {
                    reset();
                }
            }
        }
    }

    private void handleKeyboardInput() {
        // Handle numeric digit keys (NUM_0 to NUM_9, NUMPAD_0 to NUMPAD_9)
        for (int key = Input.Keys.NUM_0; key <= Input.Keys.NUM_9; key++) {
            if (Gdx.input.isKeyJustPressed(key)) {
                appendDigit((char) ('0' + (key - Input.Keys.NUM_0)));
            }
        }
        for (int key = Input.Keys.NUMPAD_0; key <= Input.Keys.NUMPAD_9; key++) {
            if (Gdx.input.isKeyJustPressed(key)) {
                appendDigit((char) ('0' + (key - Input.Keys.NUMPAD_0)));
            }
        }

        // Handle Backspace
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)) {
            if (answerBuilder.length() > 0) {
                answerBuilder.setLength(answerBuilder.length() - 1);
            }
        }

        // Handle Enter key to submit
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_ENTER)) {
            if (answerBuilder.length() > 0) {
                evaluateAnswer();
            }
        }
    }

    private void appendDigit(final char digit) {
        if (answerBuilder.length() < 4) { // 999+999 = 1998 (4 digits max)
            answerBuilder.append(digit);
        }
    }

    private void evaluateAnswer() {
        // Custom zero-allocation integer parser
        int parsedValue = 0;
        for (int i = 0; i < answerBuilder.length(); i++) {
            parsedValue = parsedValue * 10 + (answerBuilder.charAt(i) - '0');
        }

        if (parsedValue == correctAnswer) {
            playCorrectSound();
            state = MathState.CORRECT_FEEDBACK;
            feedbackTimer = FEEDBACK_DURATION;
        } else {
            playWrongSound();
            isTimeout = false;
            state = MathState.WRONG_FEEDBACK;
            feedbackTimer = FEEDBACK_DURATION;
        }
    }

    private void playCorrectSound() {
        EventDispatcher.getInstance().dispatch(
                new GameEvent<>(EventType.PLAY_SFX, "audio/sfx/puzzle_boss/answer_correct.mp3")
        );
    }

    private void playWrongSound() {
        EventDispatcher.getInstance().dispatch(
                new GameEvent<>(EventType.PLAY_SFX, "audio/sfx/puzzle_boss/answer_wrong.mp3")
        );
    }

    private void drawTextInBox(final SpriteBatch batch, final BitmapFont font, final CharSequence text, final float x, final float y, final Texture textBoxTexture, final GlyphLayout layout) {
        layout.setText(font, text);
        final float paddingX = 20f;
        final float paddingY = 15f;
        final float boxWidth = layout.width + paddingX * 2;
        final float boxHeight = layout.height + paddingY * 2;

        final float boxX = x - boxWidth / 2f;
        final float boxY = y - boxHeight / 2f;

        batch.draw(textBoxTexture, boxX, boxY, boxWidth, boxHeight);
        
        final Color origColor = font.getColor();
        final float r = origColor.r;
        final float g = origColor.g;
        final float b = origColor.b;
        final float a = origColor.a;
        font.setColor(Color.BLACK);
        font.draw(batch, text, x - layout.width / 2f, y + layout.height / 2f);
        font.setColor(r, g, b, a);
    }

    @Override
    public void render(final ShapeRenderer shapeRenderer, final SpriteBatch batch) {
        if (isSolved) {
            return;
        }

        // Render top shrinking timer bar
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        final float maxTime = roundTimeLimits[currentRound - 1];
        if (timeRemaining > maxTime * 0.5f) {
            shapeRenderer.setColor(Color.GREEN);
        } else if (timeRemaining > maxTime * 0.2f) {
            shapeRenderer.setColor(Color.YELLOW);
        } else {
            shapeRenderer.setColor(Color.RED);
        }
        final float timerWidth = 800f * (timeRemaining / maxTime);
        shapeRenderer.rect(0f, 590f, timerWidth, 10f);
        shapeRenderer.end();

        // Render question text centered
        batch.begin();
        final BitmapFont font = context.getFont();
        font.setColor(Color.WHITE);

        // Progress Text
        renderBuilder.setLength(0);
        renderBuilder.append("Toán Nhanh - Câu Hỏi: ").append(currentRound).append(" / ").append(MAX_ROUNDS);
        if (textBoxTexture != null) {
            drawTextInBox(batch, font, renderBuilder, 400f, 530f, textBoxTexture, textLayout);
        } else {
            font.draw(batch, renderBuilder, 20f, 570f);
        }

        // Expression
        renderBuilder.setLength(0);
        renderBuilder.append(operandA).append(" + ").append(operandB).append(" = ?");
        if (textBoxTexture != null) {
            drawTextInBox(batch, font, renderBuilder, 400f, 340f, textBoxTexture, textLayout);
        } else {
            font.draw(batch, renderBuilder, 330f, 340f);
        }

        // Answer
        renderBuilder.setLength(0);
        renderBuilder.append("Đáp án: ").append(answerBuilder);
        if (textBoxTexture != null) {
            drawTextInBox(batch, font, renderBuilder, 400f, 260f, textBoxTexture, textLayout);
        } else {
            font.draw(batch, renderBuilder, 330f, 280f);
        }

        // Feedback overlay
        if (state == MathState.CORRECT_FEEDBACK) {
            font.setColor(Color.GREEN);
            renderBuilder.setLength(0);
            renderBuilder.append("Chính Xác!");
            if (textBoxTexture != null) {
                drawTextInBox(batch, font, renderBuilder, 400f, 170f, textBoxTexture, textLayout);
            } else {
                font.draw(batch, renderBuilder, 350f, 200f);
            }
        } else if (state == MathState.WRONG_FEEDBACK) {
            font.setColor(Color.RED);
            renderBuilder.setLength(0);
            if (isTimeout) {
                renderBuilder.append("Hết Giờ!");
            } else {
                renderBuilder.append("Sai Rồi!");
            }
            if (textBoxTexture != null) {
                drawTextInBox(batch, font, renderBuilder, 400f, 170f, textBoxTexture, textLayout);
            } else {
                font.draw(batch, renderBuilder, 360f, 200f);
            }
        }

        font.setColor(Color.WHITE);
        batch.end();
    }

    @Override
    public boolean isSolved() {
        return isSolved;
    }

    @Override
    public void dispose() {
        // No custom fonts/textures instantiated locally, using shared context components
    }
}
