package hust.adventure.ui.puzzle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
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
    private static final int MAX_ROUNDS = 10;
    private static final float FEEDBACK_DURATION = 0.5f;

    public enum MathState {
        AWAITING_INPUT, CORRECT_FEEDBACK, WRONG_FEEDBACK
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
    private final com.badlogic.gdx.math.Vector2 tmpMouse = new com.badlogic.gdx.math.Vector2();
    private boolean showIntro = true;

    public SpeedMathPuzzle() {
        this.state = MathState.AWAITING_INPUT;
        this.isSolved = false;
        this.currentRound = 1;
        this.answerBuilder = new StringBuilder();
        this.renderBuilder = new StringBuilder();
        this.showIntro = true;
    }

    @Override
    public String getBackgroundPath() {
        return "Library1.jpg";
    }

    @Override
    public void init(final LevelContext ctx) {
        this.context = ctx;

        // Compute round time limits
        // Rounds 1-10: linearly from 20.0s to 10.0s
        for (int i = 0; i < 10; i++) {
            roundTimeLimits[i] = 20.0f - (i * (20.0f - 10.0f) / 9f);
        }

        textBoxTexture = ctx.getGame().getAssetManager().getTexture("text_box.png");

        reset();
    }

    @Override
    public void reset() {
        this.currentRound = 1;
        this.isSolved = false;
        this.showIntro = true;
        this.feedbackTimer = 0f;
        this.isTimeout = false;
        this.answerBuilder.setLength(0);
        this.state = MathState.AWAITING_INPUT;
        generateQuestion();
    }

    private void generateQuestion() {
        if (currentRound <= 5) {
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

        if (showIntro) {
            if (Gdx.input.justTouched()) {
                tmpMouse.set(Gdx.input.getX(), Gdx.input.getY());
                if (context != null) {
                    context.unproject(tmpMouse);
                }
                final float mx = tmpMouse.x;
                final float my = tmpMouse.y;

                final float btnW = 160f;
                final float btnH = 45f;
                final float btnX = 400f - btnW / 2f;
                final float btnY = 110f;

                if (mx >= btnX && mx <= btnX + btnW && my >= btnY && my <= btnY + btnH) {
                    showIntro = false;
                    EventDispatcher.getInstance()
                            .dispatch(new GameEvent<>(EventType.PLAY_SFX, "audio/sfx/ui_click.wav"));
                }
            }
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
        EventDispatcher.getInstance()
                .dispatch(new GameEvent<>(EventType.PLAY_SFX, "audio/sfx/puzzle_boss/answer_correct.mp3"));
    }

    private void playWrongSound() {
        EventDispatcher.getInstance()
                .dispatch(new GameEvent<>(EventType.PLAY_SFX, "audio/sfx/puzzle_boss/answer_wrong.mp3"));
    }

    private void drawTextInBox(final SpriteBatch batch, final BitmapFont font, final CharSequence text, final float x,
            final float y, final Texture textBoxTexture, final GlyphLayout layout) {
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

        if (showIntro) {
            drawIntro(shapeRenderer, batch);
            return;
        }

        // Draw timer bar background
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

    private void drawIntro(final ShapeRenderer shapeRenderer, final SpriteBatch batch) {
        final float boxW = 620f;
        final float boxH = 460f;
        final float boxX = 90f;
        final float boxY = 70f;

        final float btnW = 160f;
        final float btnH = 45f;
        final float btnX = 400f - btnW / 2f;
        final float btnY = 110f;

        // Check hover
        tmpMouse.set(Gdx.input.getX(), Gdx.input.getY());
        if (context != null) {
            context.unproject(tmpMouse);
        }
        final boolean isHovered = (tmpMouse.x >= btnX && tmpMouse.x <= btnX + btnW && tmpMouse.y >= btnY
                && tmpMouse.y <= btnY + btnH);

        // Draw overlay using ShapeRenderer
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0f, 0f, 0f, 0.75f));
        shapeRenderer.rect(0, 0, 800, 600);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // Draw Text Box and Texts using SpriteBatch
        batch.begin();
        if (textBoxTexture != null) {
            batch.setColor(Color.WHITE);
            batch.draw(textBoxTexture, boxX, boxY, boxW, boxH);
        }

        final BitmapFont font = context.getFont();
        final Color origColor = font.getColor();

        // Draw title
        font.setColor(new Color(0.6f, 0.1f, 0.1f, 1f));
        textLayout.setText(font, "THỬ THÁCH 3: TÍNH NHẨM NHANH");
        font.draw(batch, "THỬ THÁCH 3: TÍNH NHẨM NHANH", 400f - textLayout.width / 2f, 480f);

        // Draw intro body text
        font.setColor(Color.BLACK);
        final String introText = "Chào mừng bạn đến với thử thách cuối cùng!\n\n"
                + "Luật chơi Tính Nhẩm rất đơn giản:\n" + "1. Hệ thống sẽ đưa ra các phép toán cộng ngẫu nhiên.\n"
                + "2. Nhập đáp án bằng các phím số từ bàn phím của bạn.\n"
                + "3. Nhấn [Enter] để gửi đáp án, hoặc [Backspace] để xóa.\n" + "4. Vượt qua đúng " + MAX_ROUNDS
                + " câu hỏi để hoàn thành thử thách.\n"
                + "5. Thời gian giới hạn cho mỗi câu sẽ ngắn dần theo từng vòng!\n\n"
                + "Hãy nhấn nút bên dưới để bắt đầu tính nhẩm!";

        font.draw(batch, introText, 140f, 420f);

        // Draw Start Button Box
        if (textBoxTexture != null) {
            batch.setColor(isHovered ? Color.LIGHT_GRAY : Color.WHITE);
            batch.draw(textBoxTexture, btnX, btnY, btnW, btnH);
        }

        // Draw Start Button Text
        font.setColor(isHovered ? new Color(0.1f, 0.6f, 0.1f, 1f) : new Color(0.1f, 0.4f, 0.1f, 1f));
        textLayout.setText(font, "BẮT ĐẦU");
        font.draw(batch, "BẮT ĐẦU", 400f - textLayout.width / 2f, btnY + btnH / 2f + textLayout.height / 2f);

        font.setColor(origColor);
        batch.end();

        // Draw Gold highlight border if hovered
        if (isHovered) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.GOLD);
            shapeRenderer.rect(btnX - 2, btnY - 2, btnW + 4, btnH + 4);
            shapeRenderer.end();
        }
    }
}
