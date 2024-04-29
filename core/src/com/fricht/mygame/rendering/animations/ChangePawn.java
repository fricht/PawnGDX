package com.fricht.mygame.rendering.animations;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;


public class ChangePawn implements Animation{

    private final Vector2 pos;
    private final Sprite from_sprite;
    private final Sprite to_sprite;

    public ChangePawn(Vector2 pos, Sprite from_sprite, Sprite to_sprite) {
        this.pos = pos;
        this.from_sprite = from_sprite;
        this.to_sprite = to_sprite;
    }

    @Override
    public void render(SpriteBatch batch, float progress) {
        // setup
        from_sprite.setOriginCenter();
        to_sprite.setOriginCenter();
        from_sprite.setRotation(Interpolation.smoother.apply(0, 360, progress));
        to_sprite.setRotation(Interpolation.smoother.apply(0, 360, progress));
        from_sprite.setPosition(pos.x, pos.y);
        to_sprite.setPosition(pos.x, pos.y);
        to_sprite.setAlpha(Interpolation.smoother.apply(progress));
        // draw
        from_sprite.draw(batch);
        to_sprite.draw(batch);
        // reset
        to_sprite.setAlpha(1f);
        from_sprite.setRotation(0);
        to_sprite.setRotation(0);
    }

}
