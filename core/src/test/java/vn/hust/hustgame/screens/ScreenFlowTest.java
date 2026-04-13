package vn.hust.hustgame.screens;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import vn.hust.hustgame.GameAssetManager;
import vn.hust.hustgame.HustGame;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ScreenFlowTest {

    @Test
    public void testLoadingScreenTransitionsToPlayScreen() {
        HustGame mockGame = Mockito.mock(HustGame.class);
        GameAssetManager mockAssetManager = Mockito.mock(GameAssetManager.class);

        when(mockGame.getAssetManager()).thenReturn(mockAssetManager);

        // Return true on update to simulate finished loading
        when(mockAssetManager.update()).thenReturn(true);

        LoadingScreen loadingScreen = new LoadingScreen(mockGame);

        // Action
        loadingScreen.render(0.1f);

        // Verification
        verify(mockAssetManager, times(1)).update();
        verify(mockGame, times(1)).setScreen(any(PlayScreen.class));
    }
}
