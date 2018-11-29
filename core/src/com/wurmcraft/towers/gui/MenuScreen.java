package com.wurmcraft.towers.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.wurmcraft.towers.Towers;

import static com.wurmcraft.towers.Towers.*;

public class MenuScreen implements Screen {

    public Towers towers;
    private OrthographicCamera camrea;
    private Viewport viewport;

    private Stage stage;
    private Skin skin;
    private Texture background = new Texture(Gdx.files.internal("backgroundMenu.png"));

    public MenuScreen(Towers towers) {
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
        setupStyle();
        createButtons();
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        // Clear the screen
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        // Draw the Actors / Objects
        stage.act(delta);
        stage.getBatch().begin();
        stage.getBatch().draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.getBatch().end();
        stage.draw();
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

    private void setupStyle() {
        skin = new Skin();
        skin.add("default", towers.font);
        skin.add("default", towers.font);
        Pixmap fillColor = new Pixmap(Gdx.graphics.getWidth() / 3, Gdx.graphics.getHeight() / 10, Pixmap.Format.RGBA8888);
        fillColor.setColor(Color.WHITE);
        fillColor.fill();
        skin.add("background", new Texture(fillColor));
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.up = skin.newDrawable("background", Color.GRAY);
        buttonStyle.down = skin.newDrawable("background", Color.RED);
        buttonStyle.checked = skin.newDrawable("background", Color.RED);
        buttonStyle.over = skin.newDrawable("background", Color.PURPLE);
        buttonStyle.font = skin.getFont("default");
        skin.add("default", buttonStyle);
    }

    private void createButtons() {
        Table table = new Table(skin);
        table.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        TextButton playButton = new TextButton(local.BUTTON_PLAY, skin);
        playButton.addListener(new ScreenListener(new GameScreen(towers)));
        table.add(playButton).pad(4).row();
        TextButton settingsButton = new TextButton(local.BUTTON_SETTINGS, skin);
        table.add(settingsButton).pad(4).row();
        settingsButton.addListener(new ScreenListener(new SettingsScreen(towers)));
        if (settings.debug)
            table.debug();
        stage.addActor(table);
    }

    public class ScreenListener extends ClickListener {

        private Screen screen;

        public ScreenListener(Screen screen) {
            this.screen = screen;
        }

        @Override
        public void clicked(InputEvent event, float x, float y) {
            super.clicked(event, x, y);
            towers.setScreen(screen);
            if (settings.debug)
                System.out.println("Screen changed to " + screen.toString());
        }
    }
}
