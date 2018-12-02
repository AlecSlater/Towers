package com.wurmcraft.towers;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wurmcraft.towers.gui.MenuGui;
import com.wurmcraft.towers.json.Local;
import com.wurmcraft.towers.json.Settings;

public class Towers extends Game {

    // Config
    public static final int WIDTH = 1920;
    public static final int HEIGHT = 1080;
    public static Settings settings;
    public static Local local;
    // Singleton's
    public static SpriteBatch batch;
    public static BitmapFont font;
    public static Gson gson;
    // Gui's
    public static MenuGui menuGui;

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        gson = new GsonBuilder().setPrettyPrinting().create();
        settings = gson.fromJson(Gdx.files.internal("settings.json").reader(), Settings.class);
        font.getData().setScale(settings.fontScale);
        local = gson.fromJson(Gdx.files.internal("lang/en_us.json").reader(), Local.class);
        setScreen(menuGui = new MenuGui(this));
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}


//    @Override
//    public void create() {
//        batch = new SpriteBatch();
//        font = new BitmapFont();
//        settings = GSON.fromJson(Gdx.files.internal("settings.json").reader(), Settings.class);
//        font.getData().setScale(settings.fontScale);
//        local = GSON.fromJson(Gdx.files.internal("lang/en_us.json").reader(), Local.class);
//        setScreen(new MenuScreen(this));
//    }
//
//    @Override
//    public void dispose() {
//        batch.dispose();
//    }
//
//    @Override
//    public void render() {
//        super.render();
//    }
//
//    Stage stage;
//    World world = new World(new Vector2(0, -10), true);
//
//    public void create() {
//        stage = new Stage();
//        Gdx.input.setInputProcessor(stage);
//        final Skin skin = new Skin();
//        skin.add("default", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
//        skin.add("badlogic", new Texture("basic.png"));
//        skin.add("valid", new Texture("valid.png"));
//        skin.add("invalid", new Texture("invalid.png"));
//        skin.add("background", new Texture("background.png"));
//
//        Image background = new Image(skin, "background");
//        stage.addActor(background);
//
//        Image validTargetImage = new Image(skin, "valid");
//        validTargetImage.setBounds(0, Gdx.graphics.getHeight() - Gdx.graphics.getHeight() / 5, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 5);
//        stage.addActor(validTargetImage);
//        validTargetImage.setVisible(false);
//
//        Image invalidTargetImage = new Image(skin, "invalid");
//        invalidTargetImage.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() - Gdx.graphics.getHeight() / 5);
//        stage.addActor(invalidTargetImage);
//        invalidTargetImage.setVisible(false);
//
//        Image sourceImage = new Image(skin, "badlogic");
//        sourceImage.setBounds(50, 125, 128, 128);
//        stage.addActor(sourceImage);
//
//        BodyDef bodyDef = new BodyDef();
//        bodyDef.type = BodyDef.BodyType.DynamicBody;
//        bodyDef.position.set(50, 664);
//        Body body = world.createBody(bodyDef);
//        PolygonShape square = new PolygonShape();
//        square.setAsBox(128, 128);
//        body.setUserData(sourceImage);
//        sourceImage.setPosition(50, 128, 64);
//
//        DragAndDrop dragAndDrop = new DragAndDrop();
//
//        dragAndDrop.addSource(new DragAndDrop.Source(sourceImage) {
//            public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
//                DragAndDrop.Payload payload = new DragAndDrop.Payload();
//                validTargetImage.setVisible(true);
//                payload.setObject(sourceImage);
//
//                payload.setDragActor(sourceImage);
//
//                payload.setValidDragActor(sourceImage);
//
//                payload.setInvalidDragActor(sourceImage);
//
//                Array<Body> bodys = new Array<>();
//                world.getBodies(bodys);
//                for (Body body : bodys)
//                    if (sourceImage.equals(body.getUserData())) {
//                        world.destroyBody(body);
//                    }
//                return payload;
//            }
//        });
//        dragAndDrop.addTarget(new DragAndDrop.Target(validTargetImage) {
//            public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
//                payload.getDragActor().setPosition(x, y);
//                validTargetImage.setVisible(true);
//                invalidTargetImage.setVisible(true);
//                return true;
//            }
//
//            public void reset(DragAndDrop.Source source, DragAndDrop.Payload payload) {
//                validTargetImage.setVisible(false);
//                invalidTargetImage.setVisible(false);
//            }
//
//            public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
//                payload.getDragActor().setPosition(x, Gdx.graphics.getHeight() - y, 64);
//                stage.addActor(payload.getDragActor());
//                BodyDef bodyDef = new BodyDef();
//                bodyDef.type = BodyDef.BodyType.DynamicBody;
//                bodyDef.position.set(x, Gdx.graphics.getHeight() - y);
//                Body body = world.createBody(bodyDef);
//                body.setUserData(payload.getDragActor());
//            }
//        });
//        dragAndDrop.addTarget(new DragAndDrop.Target(invalidTargetImage) {
//            public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
//                validTargetImage.setVisible(true);
//                invalidTargetImage.setVisible(true);
//                return false;
//            }
//
//            public void reset(DragAndDrop.Source source, DragAndDrop.Payload payload) {
//                validTargetImage.setVisible(false);
//                invalidTargetImage.setVisible(false);
//            }
//
//            public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
//            }
//        });
//    }
//
//    public void render() {
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//        stage.act(Gdx.graphics.getDeltaTime());
//        stage.draw();
//        world.step(Gdx.graphics.getDeltaTime(), 1, 1);
//        Array<Body> bodys = new Array<>();
//        world.getBodies(bodys);
//        for (Body body : bodys)
//            ((Actor) body.getUserData()).setPosition(body.getPosition().x, body.getPosition().y);
//    }
//
//    public void resize(int width, int height) {
//        stage.getViewport().update(width, height, true);
//    }
//
//    public void dispose() {
//        stage.dispose();
//    }
//}

