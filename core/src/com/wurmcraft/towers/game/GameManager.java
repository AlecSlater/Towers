package com.wurmcraft.towers.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.Array;
import com.wurmcraft.towers.Towers;
import com.wurmcraft.towers.game.api.Block;
import com.wurmcraft.towers.game.api.Enemy;
import com.wurmcraft.towers.game.api.Entity;
import com.wurmcraft.towers.gui.GameGui;
import com.wurmcraft.towers.render.RenderUtils;


public class GameManager {

    //  Instance Data
    private GameGui gui;
    private Stage stage;
    private DragAndDrop dragAndDrop;
    private Array<Body> worldBodyTracker;
    // Game Data

    public GameManager(GameGui gui, Stage stage, World world, DragAndDrop dragAndDrop) {
        this.gui = gui;
        this.stage = stage;
        this.dragAndDrop = dragAndDrop;
        worldBodyTracker = new Array<>();
    }

    public void update() {
        // Move Entity To Align with its Hitbox
        gui.world.getBodies(worldBodyTracker);
        for (Body body : worldBodyTracker)
            if (body.getUserData() != null && body.getUserData() instanceof Actor) {
                if (body.getUserData() instanceof Enemy) {
                    body.setLinearVelocity(((Enemy) body.getUserData()).movementSpeed * Towers.settings.gravity, 0);
                    continue;
                }
                ((Actor) body.getUserData()).setPosition(body.getPosition().x, body.getPosition().y);
            }
        // TODO Replace with Wave Spawning
        if (worldBodyTracker.size <= 3) {
            createEntity(Entity.Type.ENEMY, 0, 8, 1, 2, 0, gui.STARTING_Y);

        }
        placeGround();
    }

    public void render(Stage stage, float time) {
        for (Body body : worldBodyTracker)
            if (body.getUserData() != null && body.getUserData() instanceof Entity) {
                ((Entity) body.getUserData()).render(stage, time);
            }
    }

    public Entity createEntity(Entity.Type type, int data, int hp, int damage, int speed, int x, int y) {
        // Physics / HitBox Creation
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        Body body = gui.world.createBody(bodyDef);
        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape square = new PolygonShape();
        square.setAsBox(64, 64);
        fixtureDef.shape = square;
        body.createFixture(fixtureDef);
        square.dispose();
        // Entity Creation
        Entity entity = null;
        if (type == Entity.Type.ENEMY) {
            entity = new Enemy(RenderUtils.getAnimationForEntity(data, gui.enemyTextures, speed), body, hp, damage, speed);
        } else if (type == Entity.Type.BLOCK) {
            entity = new Block(RenderUtils.getAnimationForEntity(data, gui.blockTextures, 1), body, hp);
        }
        body.setUserData(entity);
        stage.addActor(entity);
        return entity;

    }

    private Body placeGround() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(0, -10);
        Body body = gui.world.createBody(bodyDef);
        ChainShape groundShape = new ChainShape();
        groundShape.createChain(new Vector2[]{new Vector2(-2000, -10), new Vector2(2000, -10)});
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = groundShape;
        body.createFixture(fixtureDef);
        return body;
    }

    private void nextWave() {

    }
}
