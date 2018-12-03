package com.wurmcraft.towers.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.wurmcraft.towers.Towers;
import com.wurmcraft.towers.render.RenderUtils;

import java.util.Arrays;
import java.util.Collections;

import static com.wurmcraft.towers.Towers.font;

public class LeaderboardGUI implements Screen {

    private Towers towers;
    // Rendering
    private Stage stage;
    private Table table;
    private Skin skin;
    public TextureRegion[][] guiIconsTextures = TextureRegion.split(new Texture(Gdx.files.internal("guiIcons.png")), 128, 128);

    private boolean loaded = true;

    public LeaderboardGUI(Towers towers) {
        this.towers = towers;
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void show() {
        // Table Setup
        skin = new Skin();
        skin.add("default", font);
        Pixmap fillColor = new Pixmap(Gdx.graphics.getWidth() / 3, Gdx.graphics.getHeight() / 10, Pixmap.Format.RGBA8888);
        fillColor.setColor(com.badlogic.gdx.graphics.Color.WHITE);
        fillColor.fill();
        skin.add("background", new Texture(fillColor));
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.up = skin.newDrawable("background", com.badlogic.gdx.graphics.Color.GRAY);
        buttonStyle.down = skin.newDrawable("background", com.badlogic.gdx.graphics.Color.DARK_GRAY);
        buttonStyle.checked = skin.newDrawable("background", com.badlogic.gdx.graphics.Color.RED);
        buttonStyle.over = skin.newDrawable("background", com.badlogic.gdx.graphics.Color.DARK_GRAY);
        buttonStyle.font = skin.getFont("default");
        skin.add("default", buttonStyle);
        Label.LabelStyle style = new Label.LabelStyle();
        style.font = Towers.font;
        style.fontColor = Color.BLACK;
        skin.add("default", style);
        skin.add("default", new ScrollPane.ScrollPaneStyle());
        table = new Table(skin);
        table.setFillParent(true);
        table.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.addActor(table);
        // Add Data to table
        table.center();
        try {
            String[] scoresS = Gdx.files.local("leaderboard.json").readString().split("\n");
            Integer[] scores = new Integer[scoresS.length];
            int amountToDisplay = scores.length <= 10 ? scores.length : 10;
            for (int index = 0; index < amountToDisplay; index++)
                scores[index] = Integer.parseInt(scoresS[index]);
            Arrays.sort(scores, Collections.reverseOrder());
            for (int score : scores)
                table.add(score + "").row();
        } catch (Exception e) {
            e.printStackTrace();
            loaded = false;
        }
        // Back Button
        Image image = new Image(guiIconsTextures[0][2]);
        image.rotateBy(90);
        image.addCaptureListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                towers.setScreen(new MenuGui(towers));
            }
        });
        image.setBounds(Gdx.graphics.getWidth() - 40, 20, 128, 128);
        stage.addActor(image);
    }

    @Override
    public void render(float delta) {
        RenderUtils.clearScreen();
        stage.getBatch().begin();
        stage.getBatch().draw(new Texture(Gdx.files.internal("backgroundLeaderboard.png")), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        if (!loaded) {
            Towers.font.draw(stage.getBatch(), "None", Gdx.graphics.getWidth() / 2 - 20, Gdx.graphics.getHeight() / 2);
        }
        stage.getBatch().end();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
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

    }
}
