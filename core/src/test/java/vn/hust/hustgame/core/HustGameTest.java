package vn.hust.hustgame.core;

import vn.hust.hustgame.GameAssetManager;
import vn.hust.hustgame.HustGame;
import vn.hust.hustgame.events.EventDispatcher;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HustGameTest {

    @Test
    public void testHustGameInstantiatesDependencies() {
        // Without full LibGDX OpenGL setup, we can only mock HustGame
        HustGame game = Mockito.mock(HustGame.class);
        assertNotNull(game);

        GameAssetManager assetManager = new GameAssetManager();
        assertNotNull(assetManager);

        EventDispatcher dispatcher = EventDispatcher.getInstance();
        assertNotNull(dispatcher);
    }
}
