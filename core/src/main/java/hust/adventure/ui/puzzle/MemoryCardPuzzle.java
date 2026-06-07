package hust.adventure.ui.puzzle;

import com.badlogic.gdx.Gdx;
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
 * Memory Card matching mini-game puzzle.
 */
public class MemoryCardPuzzle implements PuzzleGame {
    private static final int NUM_CARDS = 36;
    private static final int NUM_PAIRS = 18;
    private static final float TIME_LIMIT = 180f; // 3 minutes

    public enum MatchState {
        IDLE, FIRST_CARD_SELECTED, SECOND_CARD_SELECTED, MISMATCH_DELAY
    }

    private MatchState state;
    private LevelContext context;
    private boolean isSolved;
    private boolean showIntro = true;

    private float timeRemaining;
    private final MemoryCard[] cards = new MemoryCard[NUM_CARDS];
    private final int[] cardIds = new int[NUM_CARDS];

    private int firstSelectedIndex;
    private int secondSelectedIndex;
    private float mismatchTimer;

    // Assets eager binding
    private Texture backTexture;
    private final Texture[] faceTextures = new Texture[NUM_PAIRS];
    private final StringBuilder textBuilder = new StringBuilder();
    private final StringBuilder tempBuilder = new StringBuilder();
    private Texture textBoxTexture;
    private final GlyphLayout textLayout = new GlyphLayout();
    private final com.badlogic.gdx.math.Vector2 tmpMouse = new com.badlogic.gdx.math.Vector2();

    @Override
    public String getBackgroundPath() {
        return "Library1.jpg";
    }

    @Override
    public void init(final LevelContext ctx) {
        this.context = ctx;

        // Card dimensions & positioning parameters
        final float cardWidth = 49f;
        final float cardHeight = 65f;
        final float gapX = 15f;
        final float gapY = 10f;
        final float startX = 215.5f;
        final float startY = 80f;

        // Create cards grid
        for (int i = 0; i < NUM_CARDS; i++) {
            final int col = i % 6;
            final int row = i / 6;
            final float x = startX + col * (cardWidth + gapX);
            final float y = startY + row * (cardHeight + gapY);
            cards[i] = new MemoryCard(0, x, y, cardWidth, cardHeight);
        }

        // Cache textures from GameAssetManager
        backTexture = ctx.getGame().getAssetManager().getTexture("puzzle/cards/card_back.png");
        textBoxTexture = ctx.getGame().getAssetManager().getTexture("text_box.png");
        for (int i = 0; i < NUM_PAIRS; i++) {
            // Reusable buffer to avoid allocations
            tempBuilder.setLength(0);
            tempBuilder.append("puzzle/cards/card_face_");
            if (i + 1 < 10) {
                tempBuilder.append("0");
            }
            tempBuilder.append(i + 1).append(".png");
            faceTextures[i] = ctx.getGame().getAssetManager().getTexture(tempBuilder.toString());
        }

        reset();
    }

    @Override
    public void reset() {
        this.isSolved = false;
        this.showIntro = true;
        this.timeRemaining = TIME_LIMIT;
        this.firstSelectedIndex = -1;
        this.secondSelectedIndex = -1;
        this.state = MatchState.IDLE;

        // Generate paired layout: two of each ID from 1 to 18
        for (int i = 0; i < NUM_PAIRS; i++) {
            cardIds[i * 2] = i + 1;
            cardIds[i * 2 + 1] = i + 1;
        }

        // Fisher-Yates Shuffle
        for (int i = NUM_CARDS - 1; i > 0; i--) {
            final int j = MathUtils.random(i);
            final int temp = cardIds[i];
            cardIds[i] = cardIds[j];
            cardIds[j] = temp;
        }

        // Configure pre-allocated memory cards
        for (int i = 0; i < NUM_CARDS; i++) {
            cards[i].setPairId(cardIds[i]);
            cards[i].setFlipped(false);
            cards[i].setMatched(false);
        }
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

        timeRemaining -= delta;
        if (timeRemaining <= 0f) {
            EventDispatcher.getInstance()
                    .dispatch(new GameEvent<>(EventType.PLAY_SFX, "audio/sfx/puzzle_boss/puzzle_failed.mp3"));
            reset();
            return;
        }

        if (state == MatchState.MISMATCH_DELAY) {
            mismatchTimer -= delta;
            if (mismatchTimer <= 0) {
                if (firstSelectedIndex != -1 && secondSelectedIndex != -1) {
                    cards[firstSelectedIndex].setFlipped(false);
                    cards[secondSelectedIndex].setFlipped(false);
                }
                firstSelectedIndex = -1;
                secondSelectedIndex = -1;
                state = MatchState.IDLE;
            }
            return;
        }

        handleInput();
    }

    private void handleInput() {
        if (Gdx.input.justTouched() && context != null) {
            tmpMouse.set(Gdx.input.getX(), Gdx.input.getY());
            context.unproject(tmpMouse);
            final float mx = tmpMouse.x;
            final float my = tmpMouse.y;

            for (int i = 0; i < NUM_CARDS; i++) {
                final MemoryCard card = cards[i];
                if (card.getBounds().contains(mx, my)) {
                    if (card.isMatched() || card.isFlipped()) {
                        return; // Ignore if already matched or flipped
                    }

                    if (state == MatchState.IDLE) {
                        card.setFlipped(true);
                        playFlipSound();
                        firstSelectedIndex = i;
                        state = MatchState.FIRST_CARD_SELECTED;
                    } else if (state == MatchState.FIRST_CARD_SELECTED) {
                        if (i == firstSelectedIndex) {
                            return; // Ignore clicking same card
                        }
                        card.setFlipped(true);
                        playFlipSound();
                        secondSelectedIndex = i;
                        state = MatchState.SECOND_CARD_SELECTED;
                        evaluateMatch();
                    }
                    break;
                }
            }
        }
    }

    private void evaluateMatch() {
        final MemoryCard card1 = cards[firstSelectedIndex];
        final MemoryCard card2 = cards[secondSelectedIndex];

        if (card1.getPairId() == card2.getPairId()) {
            card1.setMatched(true);
            card2.setMatched(true);
            EventDispatcher.getInstance()
                    .dispatch(new GameEvent<>(EventType.PLAY_SFX, "audio/sfx/puzzle_boss/answer_correct.mp3"));
            firstSelectedIndex = -1;
            secondSelectedIndex = -1;
            state = MatchState.IDLE;

            checkWinCondition();
        } else {
            EventDispatcher.getInstance()
                    .dispatch(new GameEvent<>(EventType.PLAY_SFX, "audio/sfx/puzzle_boss/answer_wrong.mp3"));
            state = MatchState.MISMATCH_DELAY;
            mismatchTimer = 1.0f;
        }
    }

    private void checkWinCondition() {
        boolean allMatched = true;
        for (int i = 0; i < NUM_CARDS; i++) {
            if (!cards[i].isMatched()) {
                allMatched = false;
                break;
            }
        }
        if (allMatched) {
            isSolved = true;
        }
    }

    private void playFlipSound() {
        EventDispatcher.getInstance()
                .dispatch(new GameEvent<>(EventType.PLAY_SFX, "audio/sfx/puzzle_boss/card_flip.mp3"));
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

        // Draw cards
        batch.begin();
        for (int i = 0; i < NUM_CARDS; i++) {
            final MemoryCard card = cards[i];
            final Texture tex = (card.isFlipped() || card.isMatched()) ? faceTextures[card.getPairId() - 1]
                    : backTexture;
            batch.draw(tex, card.getBounds().x, card.getBounds().y, card.getBounds().width, card.getBounds().height);
        }

        // Render timer overlay numbers
        final BitmapFont font = context.getFont();
        font.setColor(Color.WHITE);

        textBuilder.setLength(0);
        textBuilder.append("Ghép thẻ - Thời gian: ").append((int) timeRemaining).append("s");

        if (textBoxTexture != null) {
            drawTextInBox(batch, font, textBuilder, 400f, 555f, textBoxTexture, textLayout);
        } else {
            font.draw(batch, textBuilder, 20f, 580f);
        }

        batch.end();

        // Highlight selected cards & draw timer bar
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.YELLOW);
        if (firstSelectedIndex != -1) {
            final MemoryCard card = cards[firstSelectedIndex];
            shapeRenderer.rect(card.getBounds().x, card.getBounds().y, card.getBounds().width, card.getBounds().height);
        }
        if (secondSelectedIndex != -1) {
            final MemoryCard card = cards[secondSelectedIndex];
            shapeRenderer.rect(card.getBounds().x, card.getBounds().y, card.getBounds().width, card.getBounds().height);
        }
        shapeRenderer.end();

        // Render top shrinking timer bar
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        if (timeRemaining > 90f) {
            shapeRenderer.setColor(Color.GREEN);
        } else if (timeRemaining > 30f) {
            shapeRenderer.setColor(Color.YELLOW);
        } else {
            shapeRenderer.setColor(Color.RED);
        }
        final float timerWidth = 800f * (timeRemaining / TIME_LIMIT);
        shapeRenderer.rect(0f, 590f, timerWidth, 10f);
        shapeRenderer.end();
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
        textLayout.setText(font, "THỬ THÁCH 2: LẬT BÀI CẶP");
        font.draw(batch, "THỬ THÁCH 2: LẬT BÀI CẶP", 400f - textLayout.width / 2f, 480f);

        // Draw intro body text
        font.setColor(Color.BLACK);
        final String introText = "Chào mừng bạn đến với thử thách thứ hai!\n\n" + "Luật chơi Lật Bài rất đơn giản:\n"
                + "1. Trên màn hình là 36 tấm thẻ chứa các khái niệm lập trình.\n"
                + "2. Hãy click để lật các thẻ lên và tìm các cặp thẻ giống nhau.\n"
                + "3. Bạn được phép sai nhiều lần, nhưng phải ghép đúng toàn bộ.\n"
                + "4. Hoàn thành toàn bộ cặp bài trước khi hết 3 phút.\n\n"
                + "Hãy nhấn nút bên dưới để bắt đầu lật bài!";

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
