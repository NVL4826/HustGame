package vn.hust.hustgame.screens;

import vn.hust.hustgame.HustGame;
import vn.hust.hustgame.GameState;

public class FinalOutsideScreen extends PlayScreen {

    public FinalOutsideScreen(HustGame game) {
        super(game);
    }

    @Override
    public void show() {
        super.show();
        loadMap("Final Outside.tmx", 1024f, 1024f);
        GameState.instance.previousScreen = "FinalOutsideScreen";
    }

    @Override
    protected void onUpdate(float delta) {
        // Không có logic đặc thù cho map này ngoài các Portal đã được xử lý ở class cha
    }

    @Override
    protected void onDraw() {
        // Sử dụng logic vẽ mặc định của PlayScreen
    }
}
