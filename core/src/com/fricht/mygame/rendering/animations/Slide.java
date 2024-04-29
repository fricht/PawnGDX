package com.fricht.mygame.rendering.animations;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;


public class Slide implements Animation {

    private final Vector2 from;
    private final Vector2 to;
    private final Sprite sprite;

    public Slide(Vector2 from, Vector2 to, Sprite sprite) {
        this.from = from;
        this.to = to;
        this.sprite = sprite;
    }

    @Override
    public void render(SpriteBatch batch, float progress) {
        // compute
        // i was about to write my own interpolation class, but i guess i don't have to
        Vector2 pos = new Vector2(from).interpolate(to, progress, Interpolation.fastSlow);
        sprite.setPosition(pos.x, pos.y);
        // draw
        sprite.draw(batch);
    }

}
