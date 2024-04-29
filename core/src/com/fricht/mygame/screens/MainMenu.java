package com.fricht.mygame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.ScreenUtils;
import com.fricht.mygame.MyGame;

public class MainMenu implements Screen {

    MyGame game;

    public MainMenu(MyGame game) {
        this.game = game;
    }

    public MainMenu(MyGame game, int winner) {
        this.game = game;
        // TODO
        //  display winner
        //  and menu actions
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        if (Gdx.input.justTouched()) {
            game.setScreen(new GameScreen(game));
        }
        /*
        *******************
        ***** Drawing *****
        *******************
        */
        ScreenUtils.clear(0, 0, 0, 1);
        // game.batch.begin();
        // drawing here if necessary
        // game.batch.end();
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
