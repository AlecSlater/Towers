package com.wurmcraft.towers.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.wurmcraft.towers.Towers;
import com.wurmcraft.towers.game.GameManager;

import static com.wurmcraft.towers.Towers.HEIGHT;
import static com.wurmcraft.towers.Towers.WIDTH;

public class GameScreen implements Screen {

    public Towers towers;
    private OrthographicCamera camrea;
    private Viewport viewport;
    private Stage stage;

    private Texture background = new Texture(Gdx.files.internal("background.png"));

    public GameScreen(Towers towers) {
        this.towers = towers;
        // Create and Init Camera
        camrea = new OrthographicCamera(WIDTH, HEIGHT);
        camrea.setToOrtho(false, WIDTH, HEIGHT);
        // Set Screen Scale
        viewport = new FitViewport(WIDTH, HEIGHT, camrea);
        viewport.apply();
        // Setup Menu
        stage = new Stage();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        GameManager.initGameSetttings();
    }

    @Override
    public void render(float delta) {
        // Clear the screen
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        //Run the Game Logic
        stage.act(delta);
        // Start Drawing the Game
        stage.getBatch().begin();
        stage.getBatch().draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        GameManager.INSTANCE.render(delta, stage, towers, camrea, viewport);
        drawHUD();
        stage.getBatch().end();
        stage.draw();
        // Get Ready for new Render Step
        GameManager.INSTANCE.run(delta, stage, towers, camrea, viewport);
    }

    private void drawHUD() {
        towers.font.draw(stage.getBatch(), "HP: " + GameManager.INSTANCE.baseHP, Towers.WIDTH - (Towers.WIDTH / 20), Towers.HEIGHT - Towers.HEIGHT/100);
        towers.font.draw(stage.getBatch(), GameManager.INSTANCE.balance + "c", Towers.WIDTH - (3 * (Towers.WIDTH / 20)), Towers.HEIGHT - Towers.HEIGHT/100);
        towers.font.draw(stage.getBatch(), "Score: " + GameManager.INSTANCE.killed, Towers.WIDTH - (5 * (Towers.WIDTH / 20)), Towers.HEIGHT - Towers.HEIGHT/100);
    }

    @Override
    public void resize(int width, int height) {
        viewport.setScreenSize(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
    }

}
