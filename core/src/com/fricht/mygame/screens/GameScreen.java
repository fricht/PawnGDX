package com.fricht.mygame.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.ScreenUtils;
import com.fricht.mygame.MyGame;
import com.fricht.mygame.rendering.GameRenderer;


public class GameScreen implements Screen {

    MyGame game;
    GameRenderer game_renderer;

    public GameScreen(MyGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        game_renderer = new GameRenderer(7);
    }

    @Override
    public void render(float delta) {
        game_renderer.update(delta);
        float[] cls_col = game_renderer.get_clear_color();
        ScreenUtils.clear(cls_col[0], cls_col[1], cls_col[2], 1);
        game.batch.begin();
        //game.batch.disableBlending();  // faster, but remove if transparent images
        //
        game_renderer.render(game.batch);
        //
        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {}
}
