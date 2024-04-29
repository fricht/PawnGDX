package com.fricht.mygame.rendering.animations;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;


public class SetPawn implements Animation{

    private final Vector2 pos;
    private final Sprite sprite;

    public SetPawn(Vector2 pos, Sprite sprite) {
        this.pos = pos;
        this.sprite = sprite;
    }

    @Override
    public void render(SpriteBatch batch, float progress) {
        sprite.setOriginCenter();
        sprite.setRotation(Interpolation.smoother.apply(0, 360, progress));
        sprite.setPosition(pos.x, pos.y);
        sprite.draw(batch);
        sprite.setRotation(0);
    }

}
