package hust.adventure.ui.puzzle;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import hust.adventure.HustGame;
import hust.adventure.core.assets.GameAssetManager;
import hust.adventure.events.EventDispatcher;
import hust.adventure.screens.levels.LevelContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SpeedMathPuzzleTest {
    private LevelContext mockContext;

    @BeforeEach
    public void setUp() {
        Gdx.app = mock(Application.class);
        mockContext = mock(LevelContext.class);

        final HustGame mockGame = mock(HustGame.class);
        final GameAssetManager mockAssetManager = mock(GameAssetManager.class);
        final Texture mockTexture = mock(Texture.class);

        when(mockContext.getGame()).thenReturn(mockGame);
        when(mockGame.getAssetManager()).thenReturn(mockAssetManager);
        when(mockAssetManager.getTexture("text_box.png")).thenReturn(mockTexture);

        EventDispatcher.resetInstance(); // Reset Singleton event dispatcher state
    }

    @Test
    public void testRoundTimeLimitsCalculation() throws Exception {
        SpeedMathPuzzle puzzle = new SpeedMathPuzzle();
        puzzle.init(mockContext);

        // Access private roundTimeLimits array via reflection
        Field timeLimitsField = SpeedMathPuzzle.class.getDeclaredField("roundTimeLimits");
        timeLimitsField.setAccessible(true);
        float[] roundTimeLimits = (float[]) timeLimitsField.get(puzzle);

        // Check Round 1 and Round 10 time limits
        assertEquals(20.0f, roundTimeLimits[0], 0.001f);
        assertEquals(10.0f, roundTimeLimits[9], 0.001f);
    }

    @Test
    public void testQuestionGenerationBounds() throws Exception {
        SpeedMathPuzzle puzzle = new SpeedMathPuzzle();
        puzzle.init(mockContext);

        // Access operands via reflection
        Field opAField = SpeedMathPuzzle.class.getDeclaredField("operandA");
        Field opBField = SpeedMathPuzzle.class.getDeclaredField("operandB");
        Field ansField = SpeedMathPuzzle.class.getDeclaredField("correctAnswer");
        Field roundField = SpeedMathPuzzle.class.getDeclaredField("currentRound");

        opAField.setAccessible(true);
        opBField.setAccessible(true);
        ansField.setAccessible(true);
        roundField.setAccessible(true);

        java.lang.reflect.Method generateQuestionMethod = SpeedMathPuzzle.class.getDeclaredMethod("generateQuestion");
        generateQuestionMethod.setAccessible(true);

        // Test Tier 1 (Round 1)
        roundField.setInt(puzzle, 1);
        generateQuestionMethod.invoke(puzzle);
        int a1 = opAField.getInt(puzzle);
        int b1 = opBField.getInt(puzzle);
        int ans1 = ansField.getInt(puzzle);

        assertTrue(a1 >= 10 && a1 <= 99);
        assertTrue(b1 >= 10 && b1 <= 99);
        assertEquals(a1 + b1, ans1);

        // Test Tier 2 (Round 6)
        roundField.setInt(puzzle, 6);
        generateQuestionMethod.invoke(puzzle);
        int a6 = opAField.getInt(puzzle);
        int b6 = opBField.getInt(puzzle);
        int ans6 = ansField.getInt(puzzle);

        assertTrue(a6 >= 100 && a6 <= 999);
        assertTrue(b6 >= 100 && b6 <= 999);
        assertEquals(a6 + b6, ans6);
    }
}
