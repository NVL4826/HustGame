package hust.adventure.progression;

import hust.adventure.events.EventDispatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the MapDirector progression and gating state machine through its public interface.
 * Zero LibGDX graphics mocks: tests strictly verify behavioral contracts.
 */
public class MapDirectorTest {

    private MapDirector director;

    @BeforeEach
    void setUp() {
        EventDispatcher.resetInstance();
        director = new MapDirectorImpl();
    }

    private void completeStage1Outside() {
        director.onDeadlinesCleared();
        director.onKeyItemCollected("note");
        director.advanceToNextMap();
    }

    private void completeStage2Floor1() {
        director.onDeadlinesCleared();
        director.onKeyItemCollected("note");
        director.advanceToNextMap();
    }

    private void completeStage3Library() {
        director.onAcademicChallengesCleared();
        director.onBossDefeated();
        director.onKeyItemCollected("brain");
        director.advanceToNextMap();
    }

    private void completeStage4Lab() {
        director.onDeadlinesCleared();
        director.onKeyItemCollected("usb");
        director.advanceToNextMap();
    }

    @Test
    @DisplayName("Run starts on Map 1 (Outside) according to ADR-0001")
    void runStartsWithMap1Outside() {
        assertEquals(CampusMap.MAP_1_OUTSIDE, director.getCurrentStage());
        assertEquals(0f, director.getStageElapsedTime(), 0.001f);
        assertFalse(director.canTransition(), "Cannot transition at start of stage");
        assertFalse(director.isPortalActive(), "Portal should be dormant initially");
        assertTrue(director.isAutoAttackAllowed(), "Auto-attack should be enabled outside");
    }

    @Test
    @DisplayName("Simulation time accumulates on update ticks")
    void updateTracksElapsedTime() {
        director.update(0.5f);
        director.update(1.2f);
        assertEquals(1.7f, director.getStageElapsedTime(), 0.001f);
    }

    @Test
    @DisplayName("Stage sequence follows ADR-0001 strictly from Map 1 through Final Map")
    void stageSequenceFollowsADR0001() {
        // Stage 1 -> Stage 2
        director.onDeadlinesCleared();
        director.onKeyItemCollected("note");
        assertTrue(director.canTransition());
        assertTrue(director.isPortalActive());
        assertEquals(CampusMap.MAP_2_FLOOR_1, director.advanceToNextMap());

        // Stage 2 -> Stage 3
        assertEquals(CampusMap.MAP_2_FLOOR_1, director.getCurrentStage());
        assertFalse(director.canTransition(), "New stage reset transition flags");
        assertFalse(director.isAutoAttackAllowed(), "Floor 1 lecture hall is a peaceful phase");
        director.onDeadlinesCleared();
        director.onKeyItemCollected("note");
        assertTrue(director.canTransition());
        assertEquals(CampusMap.MAP_3_LIBRARY, director.advanceToNextMap());

        // Stage 3 -> Stage 4
        assertEquals(CampusMap.MAP_3_LIBRARY, director.getCurrentStage());
        assertFalse(director.canTransition());
        director.onAcademicChallengesCleared();
        director.onBossDefeated();
        director.onKeyItemCollected("brain");
        assertTrue(director.canTransition());
        assertEquals(CampusMap.MAP_4_LAB, director.advanceToNextMap());

        // Stage 4 -> Final Map
        assertEquals(CampusMap.MAP_4_LAB, director.getCurrentStage());
        assertFalse(director.canTransition());
        director.onDeadlinesCleared();
        director.onKeyItemCollected("usb");
        assertTrue(director.canTransition());
        assertEquals(CampusMap.FINAL_MAP_BOSS, director.advanceToNextMap());

        // Final Map
        assertEquals(CampusMap.FINAL_MAP_BOSS, director.getCurrentStage());
    }

    @Test
    @DisplayName("advanceToNextMap throws IllegalStateException when gating conditions are not met")
    void advanceThrowsWhenConditionsNotMet() {
        assertFalse(director.canTransition());
        assertThrows(IllegalStateException.class, () -> director.advanceToNextMap());
    }

    @Test
    @DisplayName("advanceToNextMap throws IllegalStateException when attempting to advance past Final Map")
    void advanceThrowsOnFinalMap() {
        completeStage1Outside();
        completeStage2Floor1();
        completeStage3Library();
        completeStage4Lab();

        assertEquals(CampusMap.FINAL_MAP_BOSS, director.getCurrentStage());
        director.onBossDefeated();
        assertTrue(director.canTransition());
        assertThrows(IllegalStateException.class, () -> director.advanceToNextMap());
    }

    @Test
    @DisplayName("Portal remains locked if Deadlines are cleared but Key Item is missing")
    void portalLockedWithoutKeyItem() {
        director.onDeadlinesCleared();
        assertFalse(director.canTransition(), "Deadlines alone cannot unlock portal");
        assertFalse(director.isPortalActive());
    }

    @Test
    @DisplayName("Portal remains locked if Key Item collected but Deadlines are still active")
    void portalLockedWithoutDeadlinesCleared() {
        director.onKeyItemCollected("note");
        assertFalse(director.canTransition(), "Key item alone cannot unlock portal");
        assertFalse(director.isPortalActive());
    }

    @Test
    @DisplayName("Handles null and unrecognized key items gracefully")
    void handlesNullAndUnknownKeyItems() {
        director.onDeadlinesCleared();
        director.onKeyItemCollected(null);
        director.onKeyItemCollected("unknown_item");
        assertFalse(director.canTransition());
    }

    @Test
    @DisplayName("Map 3 (Library) suppresses auto-attacks during Academic Challenges")
    void librarySuppressesAutoAttackDuringChallenges() {
        completeStage1Outside();
        completeStage2Floor1();

        assertEquals(CampusMap.MAP_3_LIBRARY, director.getCurrentStage());
        assertFalse(director.isAutoAttackAllowed(), "Auto-attack must be suppressed during library academic challenges");

        director.onAcademicChallengesCleared();
        assertTrue(director.isAutoAttackAllowed(), "Auto-attack resumes after academic challenges are solved");
    }

    @Test
    @DisplayName("Final Map requires Professor T.H.T defeat to graduate")
    void finalMapRequiresBossDefeat() {
        completeStage1Outside();
        completeStage2Floor1();
        completeStage3Library();
        completeStage4Lab();

        assertEquals(CampusMap.FINAL_MAP_BOSS, director.getCurrentStage());
        assertFalse(director.canTransition());

        director.onBossDefeated();
        assertTrue(director.canTransition(), "Defeating Professor T.H.T fulfills Graduation criteria");
    }

    @Test
    @DisplayName("resetRun resets stage to Map 1 and clears all collected key items")
    void resetRunResetsToMap1() {
        completeStage1Outside();
        assertEquals(CampusMap.MAP_2_FLOOR_1, director.getCurrentStage());

        director.resetRun();
        assertEquals(CampusMap.MAP_1_OUTSIDE, director.getCurrentStage());
        assertEquals(0f, director.getStageElapsedTime(), 0.001f);
        assertFalse(director.canTransition());
        assertFalse(director.isPortalActive());
    }
}
