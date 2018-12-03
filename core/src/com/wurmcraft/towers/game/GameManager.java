package com.wurmcraft.towers.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Array;
import com.wurmcraft.towers.Towers;
import com.wurmcraft.towers.game.api.Block;
import com.wurmcraft.towers.game.api.Enemy;
import com.wurmcraft.towers.game.api.Entity;
import com.wurmcraft.towers.game.api.Tower;
import com.wurmcraft.towers.gui.GameGui;
import com.wurmcraft.towers.gui.MenuGui;
import com.wurmcraft.towers.render.RenderUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class GameManager {

    //  Instance Data
    private GameGui gui;
    private Stage stage;
    public Array<Body> worldBodyTracker;

    public GameManager(GameGui gui, Stage stage) {
        this.gui = gui;
        this.stage = stage;
        worldBodyTracker = new Array<>();
        placeGround();
    }

    public void update() {
        // Move Entity To Align with its Hitbox
        gui.world.getBodies(worldBodyTracker);
        List<Body> outsideWorld = new ArrayList<>();
        Array<Body> updates = new Array<>();
        gui.world.getBodies(updates);
        for (Body body : worldBodyTracker) {
            if (body.getUserData() != null && body.getUserData() instanceof Actor) {
                if (body.getUserData() instanceof Tower) {
                    ((Entity) body.getUserData()).update(updates);
                }
                if (body.getUserData() instanceof Enemy) {
                    body.setLinearVelocity(((Enemy) body.getUserData()).movementSpeed * Towers.settings.gravity, -Towers.settings.gravity);
                    if (body.getPosition().x >= Gdx.graphics.getWidth()) {
                        gui.hp--;
                        outsideWorld.add(body);
                    }
                    continue;
                }
                ((Actor) body.getUserData()).setPosition(body.getPosition().x, body.getPosition().y);
            }
        }
        // TODO Replace with Wave Spawning
        int count = 0;
        for (Body body : worldBodyTracker)
            if (body.getUserData() instanceof Enemy)
                count++;
        if (count <= 2) {
            nextWave();
        }
        for (Body body : outsideWorld) {
            ((Entity) body.getUserData()).kill();
            stage.getActors().removeValue((Actor) body.getUserData(), false);
            worldBodyTracker.removeValue(body, false);
            gui.world.destroyBody(body);
        }
        if (gui.hp <= 0) {
            gui.displayMessage(Towers.local.GAME_OVER, false);
            Gdx.files.local("towers.save").delete();
            // Add Entry To leaderboard.json
            if (!Gdx.files.local("leaderboard.json").exists()) {
                try {
                    Gdx.files.local("leaderboard.json").file().createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (gui.score > 0)
                Gdx.files.local("leaderboard.json").writeString(gui.score + "\n", true);
            gui.towers.setScreen(new MenuGui(gui.towers));
        }
        checkForCollision();
    }

    // TODO Attack Speed / Animations
    private void checkForCollision() {
        for (Contact contact : gui.world.getContactList()) {
            if (contact.getFixtureA() != null && contact.getFixtureA().getBody() != null && contact.getFixtureB() != null && contact.getFixtureB().getBody() != null) {
                Entity a = (Entity) contact.getFixtureA().getBody().getUserData();
                Entity b = (Entity) contact.getFixtureB().getBody().getUserData();
                // TODO Redo once weapons exist
                if (a != null && b != null) {
                    if (!(a instanceof Enemy) && !(b instanceof Enemy) || a instanceof Enemy && b instanceof Enemy)
                        continue;
                    a.hp--;
                    b.hp--;
                    a.setColor(Color.RED);
                    b.setColor(Color.RED);
                    if (a.hp <= 0) {
                        killEntity(((Entity) contact.getFixtureA().getBody().getUserData()));
                        a.kill();
                    } else if (b.hp <= 0) {
                        killEntity(((Entity) contact.getFixtureB().getBody().getUserData()));
                        b.kill();
                    }
                }
            }
        }
    }

    private void killEntity(Entity entity) {
        if (entity instanceof Enemy) {
            gui.balance += 100;
            gui.kills++;
            gui.score += ((Enemy) entity).damage;
        }
        worldBodyTracker.removeValue(entity.body, false);
        stage.getActors().removeValue(entity, false);
        ((Entity) entity.body.getUserData()).addAction(Actions.removeActor());
        gui.world.destroyBody(entity.body);
    }

    public void render(Stage stage, float time) {
        for (Body body : worldBodyTracker)
            if (body.getUserData() != null && body.getUserData() instanceof Entity) {
                ((Entity) body.getUserData()).render(stage, time);
            }
    }

    public Entity createEntity(Entity.Type type, int data, int hp, int damage, int speed, float x, float y) {
        // Physics / HitBox Creation
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        Body body = gui.world.createBody(bodyDef);
        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape square = new PolygonShape();
        square.setAsBox(64, 64);
        fixtureDef.shape = square;
        if (type == Entity.Type.ENEMY) {
            // Keeps Enemy's from hitting itself
            fixtureDef.filter.groupIndex = -2;
        }
        body.createFixture(fixtureDef);
        square.dispose();
        // Entity Creation
        Entity entity = null;
        if (type == Entity.Type.ENEMY) {
            entity = new Enemy(RenderUtils.getAnimationForEntity(data, gui.enemyTextures, speed), body, hp, damage, speed);
        } else if (type == Entity.Type.BLOCK) {
            entity = new Block(RenderUtils.getAnimationForEntity(data, gui.blockTextures, 1), body, hp);
        } else if (type == Entity.Type.TOWER) {
            entity = new Tower(RenderUtils.getAnimationForEntity(data, gui.towerTextures, 1), body, hp);
        }
        body.setUserData(entity);
        stage.addActor(entity);
        return entity;
    }

    private Body placeGround() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(0, -10);
        Body body = gui.world.createBody(bodyDef);
        ChainShape groundShape = new ChainShape();
        groundShape.createChain(new Vector2[]{new Vector2(-4000, 0), new Vector2(4000, 0)});
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = groundShape;
        body.createFixture(fixtureDef);
        return body;
    }

    private void nextWave() {
        gui.wave++;
        gui.displayMessage(Towers.local.HUD_WAVE.replaceAll("%WAVE%", gui.wave + ""), false);
        int amountForWave = gui.wave * 5;
        for (int amt = 0; amt < amountForWave; amt++)
            createEntity(Entity.Type.ENEMY, 0, 8, 1, 2, (int) -(Math.random() * (100 * gui.wave)), gui.STARTING_Y);
    }
}
