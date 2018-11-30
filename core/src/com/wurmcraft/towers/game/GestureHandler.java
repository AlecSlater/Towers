package com.wurmcraft.towers.game;

import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class GestureHandler implements GestureDetector.GestureListener {

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        Vector3 loc = GameManager.INSTANCE.camera.unproject(new Vector3(x, y, 0));
        if (count == 1) {
            GameManager.INSTANCE.createEntity(1, 0, loc.x + GameManager.SIZE, loc.y );
            return true;
        }
        return false;
    }


    @Override
    public boolean longPress(float x, float y) {
        Vector3 loc = GameManager.INSTANCE.camera.unproject(new Vector3(x, y, 0));
        GameManager.INSTANCE.createEntity(1, 1, loc.x + GameManager.SIZE, loc.y - (GameManager.SIZE * 2));
        return true;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    @Override
    public void pinchStop() {

    }
}
