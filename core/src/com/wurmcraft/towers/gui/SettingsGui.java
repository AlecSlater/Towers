package com.wurmcraft.towers.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.wurmcraft.towers.Towers;
import com.wurmcraft.towers.render.RenderUtils;

import static com.wurmcraft.towers.Towers.HEIGHT;
import static com.wurmcraft.towers.Towers.WIDTH;
import static com.wurmcraft.towers.Towers.font;
import static com.wurmcraft.towers.Towers.local;
import static com.wurmcraft.towers.Towers.settings;

public class SettingsGui implements Screen {

    public Towers towers;
    // Rendering
    private Stage stage;
    private OrthographicCamera camrea;
    private Viewport viewport;
    private Skin skin;
    private static Texture background = new Texture("background.png");

    // Buttons
    public TextButton debug;
    public TextButton back;

    public SettingsGui(Towers towers) {
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
        skin = setupStyle();
        debug = createButton(local.BUTTON_DEBUG, Actions.sequence(Actions.run(() -> settings.debug = !settings.debug)));
        back = createButton(local.BUTTON_BACK, Actions.sequence(Actions.moveBy(0, -stage.getHeight(), 1), Actions.run(() -> towers.setScreen(new MenuGui(towers)))));
        addButtonsToTable();
    }

    @Override
    public void render(float delta) {
        // Clear the screen
        RenderUtils.clearScreen();
        viewport.apply();
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

    public static Skin setupStyle() {
        Skin skin = new Skin();
        skin.add("default", font);
        Pixmap fillColor = new Pixmap(Gdx.graphics.getWidth() / 3, Gdx.graphics.getHeight() / 10, Pixmap.Format.RGBA8888);
        fillColor.setColor(Color.WHITE);
        fillColor.fill();
        skin.add("background", new Texture(fillColor));
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.up = skin.newDrawable("background", Color.GRAY);
        buttonStyle.down = skin.newDrawable("background", Color.DARK_GRAY);
        buttonStyle.checked = skin.newDrawable("background", Color.RED);
        buttonStyle.over = skin.newDrawable("background", Color.DARK_GRAY);
        buttonStyle.font = skin.getFont("default");
        skin.add("default", buttonStyle);
        return skin;
    }

    private TextButton createButton(String text, SequenceAction actions) {
        TextButton button = new TextButton(text, skin);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stage.addAction(actions);
            }
        });
        return button;
    }

    private void quit() {
    }

    private void addButtonsToTable() {
        Table table = new Table(skin);
        table.setFillParent(true);
        table.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        table.add(debug).pad(4).row();
        table.add(back).pad(4).row();
        if (settings.debug)
            table.debug();
        stage.addActor(table);

    }
}

