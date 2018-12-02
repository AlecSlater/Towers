package com.wurmcraft.towers.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.wurmcraft.towers.Towers;
import com.wurmcraft.towers.game.GameManager;
import com.wurmcraft.towers.game.api.Block;
import com.wurmcraft.towers.game.api.CubeData;
import com.wurmcraft.towers.game.api.Entity;
import com.wurmcraft.towers.game.api.GameState;
import com.wurmcraft.towers.game.api.ShopData;
import com.wurmcraft.towers.game.api.Tower;
import com.wurmcraft.towers.render.RenderUtils;

import org.cliffc.high_scale_lib.NonBlockingHashSet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.wurmcraft.towers.Towers.HEIGHT;
import static com.wurmcraft.towers.Towers.WIDTH;
import static com.wurmcraft.towers.Towers.font;
import static com.wurmcraft.towers.Towers.local;
import static com.wurmcraft.towers.Towers.settings;

public class GameGui implements Screen {

    public Towers towers;
    // Rendering
    private Stage stage;
    private OrthographicCamera camrea;
    private Viewport viewport;
    public TextureRegion[][] enemyTextures = TextureRegion.split(new Texture(Gdx.files.internal("enemies.png")), 128, 128);
    public TextureRegion[][] blockTextures = TextureRegion.split(new Texture(Gdx.files.internal("blocks.png")), 128, 128);
    public TextureRegion[][] towerTextures = TextureRegion.split(new Texture(Gdx.files.internal("towers.png")), 128, 128);
    public TextureRegion[][] guiIconsTextures = TextureRegion.split(new Texture(Gdx.files.internal("guiIcons.png")), 128, 128);
    private Texture background = new Texture(Gdx.files.internal("background.png"));
    private float elapsedTime;
    private long messageTimer = settings.messageTimeout;
    private String currentMessage = "";
    // HUD
    public Image validTargetImage;
    public Image invalidTargetImage;
    public List<ShopData> shopItems = new ArrayList<>();
    private Image selectionBackgroundButton;
    // Physics
    public World world;
    public NonBlockingHashSet<Entity> entities;
    public DragAndDrop dragAndDrop = new DragAndDrop();
    public final int STARTING_Y = 20;
    public static long lastCleanup = System.currentTimeMillis();
    // Game Data
    public long balance;
    public int wave;
    public int score;
    public int kills;
    public int hp;
    private GameManager manager;

    public GameGui(Towers towers) {
        this.towers = towers;
        // Create and Init Camera
        camrea = new OrthographicCamera(WIDTH, HEIGHT);
        camrea.setToOrtho(false, WIDTH, HEIGHT);
        // Set Screen Scale
        viewport = new FitViewport(WIDTH, HEIGHT, camrea);
        viewport.apply();
        // Setup Game
        stage = new Stage(viewport);
        world = new World(new Vector2(0, -Towers.settings.gravity), true);
        // Create new Game
        entities = new NonBlockingHashSet<>();
        balance = settings.startingBalance;
        wave = 1;
        score = 0;
        kills = 0;
        hp = settings.baseHP;
        if (world == null)
            Gdx.app.exit();
        manager = new GameManager(this, stage);
        if (Gdx.files.external("autoSave.save").exists()) {
            loadGameState(Towers.gson.fromJson(Gdx.files.external("autoSave.save").readString(), GameState.class));
        }
    }

    public GameGui(Towers towers, GameState state) {
        this.towers = towers;
        this.stage = new Stage();
        // Create and Init Camera
        camrea = new OrthographicCamera(WIDTH, HEIGHT);
        camrea.setToOrtho(false, WIDTH, HEIGHT);
        // Set Screen Scale
        viewport = new FitViewport(WIDTH, HEIGHT, camrea);
        viewport.apply();
        this.world = new World(new Vector2(0, -Towers.settings.gravity), true);
        loadGameState(state);
    }

    private void loadGameState(GameState state) {
        if (state != null) {
            wave = state.wave;
            for (CubeData data : state.cubes)
                manager.createEntity(data.type, data.data, data.hp, 1, 1, data.x, data.y);
            balance = state.balance;
            hp = state.hp;
            kills = state.kills;
            score = state.score;
        }
    }

    private GameState saveGameState() {
        return new GameState(wave, getActiveCubes(), balance, hp, kills, score);
    }

    private CubeData[] getActiveCubes() {
        List<CubeData> data = new ArrayList<>();
        for (Body body : manager.worldBodyTracker)
            if (body.getUserData() instanceof Block || body.getUserData() instanceof Tower) {
                Entity entity = (Entity) body.getUserData();
                Entity.Type type = null;
                if (body.getUserData() instanceof Block)
                    type = Entity.Type.BLOCK;
                else
                    type = Entity.Type.TOWER;
                data.add(new CubeData(type, entity.hp, entity.body.getPosition().x, entity.body.getPosition().y));
            }
        return data.toArray(new CubeData[0]);
    }

    @Override
    public void show() {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        createSelectionWarning();
        createShop();
        drawSelectionGUI();
        // TODO Remove After Completion of Wave
        displayMessage("Wave: " + wave, false);
    }

    @Override
    public void render(float delta) {
        RenderUtils.clearScreen();
        cleanup();
        // Update Stage
        stage.act(Gdx.graphics.getDeltaTime());
        // Rendering
        elapsedTime += delta;
        // Draw Background
        stage.getBatch().begin();
        stage.getBatch().draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.getBatch().end();
        // Draw Stage Entities
        stage.draw();
        // Draw HUD
        stage.getBatch().begin();
        manager.render(stage, elapsedTime);
        displayMessage("", true);
        drawHUD();
        stage.getBatch().end();
        // Tick Game
        manager.update();
        world.step(Gdx.graphics.getDeltaTime() * settings.gameSpeed, 1, 6);
        if (wave % 10 == 0)
            try {
                Gdx.files.external("autoSave.save").file().createNewFile();
                Gdx.files.external("autoSave.save").writeString(Towers.gson.toJson(saveGameState()), false);
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    @Override
    public void resize(int width, int height) {
        viewport.setScreenSize(width, height);
    }

    @Override
    public void pause() {
        try {
            Gdx.files.external("autoSave.save").file().createNewFile();
            Gdx.files.external("autoSave.save").writeString(Towers.gson.toJson(saveGameState()), false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void resume() {
        if (Gdx.files.external("autoSave.save").exists())
            loadGameState(Towers.gson.fromJson(Gdx.files.external("autoSave.save").readString(), GameState.class));
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
        world.dispose();
    }

    private void drawHUD() {
        // Info Hud
        font.draw(stage.getBatch(), Towers.local.HUD_WAVE.replaceAll("%WAVE%", wave + ""), 213, Gdx.graphics.getHeight() - 84);
        font.draw(stage.getBatch(), Towers.local.HUD_SCORE.replaceAll("%SCORE%", score + ""), 500, Gdx.graphics.getHeight() - 84);
        font.draw(stage.getBatch(), Towers.local.HUD_HEALTH.replaceAll("%HEALTH%", hp + ""), 800, Gdx.graphics.getHeight() - 84);
        font.draw(stage.getBatch(), Towers.local.HUD_BALANCE.replaceAll("%BALANCE%", balance + ""), Gdx.graphics.getWidth() - 200, Gdx.graphics.getHeight() - 84);
        // Shop HUD
        if (selectionBackgroundButton.isVisible()) {
            font.getData().setScale(settings.fontScale * 2);
            for (ShopData data : shopItems)
                font.draw(stage.getBatch(), data.cost + "c", data.x + 136, data.y + 96);
            font.getData().setScale(settings.fontScale);
        }
    }

    private void createShop() {
        // Block / Tower Selection
        selectionBackgroundButton = new Image(guiIconsTextures[0][3]);
        selectionBackgroundButton.setBounds(149, Gdx.graphics.getHeight() - 160 - (Gdx.graphics.getHeight() - 400), Gdx.graphics.getWidth() - 600, Gdx.graphics.getHeight() - 400);
        stage.addActor(selectionBackgroundButton);
        selectionBackgroundButton.setVisible(false);
        Image selectButton = new Image(guiIconsTextures[0][2]);
        selectButton.setBounds(20, Gdx.graphics.getHeight() - 160, 128, 128);
        stage.addActor(selectButton);
        selectButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                boolean vis = !(selectionBackgroundButton.isVisible());
                selectionBackgroundButton.setVisible(vis);
                for (ShopData data : shopItems)
                    data.image.setVisible(vis);
            }
        });
    }

    private void createSelectionWarning() {
        // Placement Rendering
        validTargetImage = new Image(new Texture("green.png"));
        validTargetImage.setBounds(0, 720, Gdx.graphics.getWidth(), 360);
        stage.addActor(validTargetImage);
        invalidTargetImage = new Image(new Texture("red.png"));
        invalidTargetImage.setBounds(0, 0, Gdx.graphics.getWidth(), 720);
        stage.addActor(invalidTargetImage);
        invalidTargetImage.setVisible(false);
        validTargetImage.setVisible(false);
        dragAndDrop.addTarget(new DragAndDrop.Target(validTargetImage) {
            public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                getActor().setColor(Color.GREEN);
                invalidTargetImage.setVisible(true);
                validTargetImage.setVisible(true);
                selectionBackgroundButton.setVisible(false);
                return true;
            }

            public void reset(DragAndDrop.Source source, DragAndDrop.Payload payload) {
                getActor().setColor(Color.WHITE);
                drawSelectionGUI();
            }

            public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                ShopData data = (ShopData) payload.getValidDragActor().getUserObject();
                if (balance - data.cost >= 0) {
                    Vector2 vec = viewport.unproject(new Vector2(x, y));
                    payload.getValidDragActor().setPosition(vec.x, vec.y);
                    stage.addActor(payload.getValidDragActor());
                    manager.createEntity(data.type, data.extraData, data.hp, 0, 0, (int) vec.x - 64, (int) vec.y - 128);
                    balance -= data.cost;
                } else {
                    displayMessage(local.NO_MONEY, false);
                }
            }
        });
        dragAndDrop.addTarget(new DragAndDrop.Target(invalidTargetImage) {
            public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                getActor().setColor(Color.RED);
                return true;
            }

            public void reset(DragAndDrop.Source source, DragAndDrop.Payload payload) {
                getActor().setColor(Color.WHITE);
                drawSelectionGUI();
            }

            @Override
            public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {

            }
        });
    }

    private void drawSelectionGUI() {
        addShopEntry(new Image(new Texture("red.png")), 10, Entity.Type.BLOCK, 0, 1, 180, 760);
        addShopEntry(new Image(new Texture("red.png")), 40, Entity.Type.BLOCK, 1, 4, 180, 630);
        addShopEntry(new Image(new Texture("cube.png")), 200, Entity.Type.TOWER, 0, 5, 568, 760);
    }

    private void addShopEntry(final Image sourceImage, int cost, Entity.Type type, int extraData, int hp, int x, int y) {
        sourceImage.setBounds(x, y, 128, 128);
        stage.addActor(sourceImage);
        ShopData data = new ShopData(sourceImage, type, extraData, x, y, cost, hp);
        sourceImage.setUserObject(data);
        dragAndDrop.addSource(new DragAndDrop.Source(sourceImage) {
            public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
                DragAndDrop.Payload payload = new DragAndDrop.Payload();
                payload.setObject(sourceImage);
                payload.setDragActor(sourceImage);
                payload.setValidDragActor(sourceImage);
                payload.setInvalidDragActor(sourceImage);
                invalidTargetImage.setVisible(true);
                validTargetImage.setVisible(true);
                selectionBackgroundButton.setVisible(false);
                for (ShopData data : shopItems) {
                    if (data.image != sourceImage)
                        data.image.setVisible(false);
                }
                return payload;
            }

            @Override
            public void dragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target) {
                invalidTargetImage.setVisible(false);
                validTargetImage.setVisible(false);
                for (ShopData data : shopItems) {
                    data.image.setPosition(data.x, data.y);
                    data.image.setVisible(false);
                }
            }
        });
        sourceImage.setVisible(false);
        shopItems.add(data);
    }

    public void displayMessage(String msg, boolean render) {
        if (msg.length() > 0)
            this.currentMessage = msg;
        if (messageTimer > 0 && currentMessage.length() > 0) {
            if (render) {
                font.getData().setScale(settings.fontScale * 4);
                GlyphLayout layout = new GlyphLayout(font, currentMessage);
                font.setColor(Color.PINK);
                font.draw(stage.getBatch(), currentMessage, (Gdx.graphics.getWidth() / 2) - (layout.width / 2), (Gdx.graphics.getHeight() / 2) + (Gdx.graphics.getHeight() / 4));
                font.getData().setScale(settings.fontScale);
                font.setColor(Color.WHITE);
                messageTimer--;
            }
        } else {
            messageTimer = settings.messageTimeout;
            currentMessage = "";
        }
    }

    public void cleanup() {
        if (lastCleanup + 5000 <= System.currentTimeMillis()) {
            for (Body body : manager.worldBodyTracker)
                if (body.getType() == BodyDef.BodyType.DynamicBody)
                    if (body.getPosition().y >= Gdx.graphics.getHeight() + 20 || body.getPosition().y <= -20)
                        world.destroyBody(body);
            lastCleanup = System.currentTimeMillis();
        }
    }
}
