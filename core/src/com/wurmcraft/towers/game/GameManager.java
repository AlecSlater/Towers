package com.wurmcraft.towers.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.wurmcraft.towers.Towers;
import com.wurmcraft.towers.game.api.Enemy;
import com.wurmcraft.towers.game.api.Entity;
import com.wurmcraft.towers.gui.MenuScreen;

import org.cliffc.high_scale_lib.NonBlockingHashSet;

public class GameManager extends InputAdapter {

    private final int STARTING_Y = 0;
    public static  final int SIZE = 64;
    public static GameManager INSTANCE = new GameManager();

    public World world = new World(new Vector2(0, -Towers.settings.gravity), true);
    private GestureDetector gesture;
    private Stage stage;
    public OrthographicCamera camera;
    public NonBlockingHashSet<Body> entities = new NonBlockingHashSet<>();

    // Game Variables
    public int baseHP;
    public int balance;
    public int killed;

    public GameManager() {
        Body ground = placeGround();
        gesture = new GestureDetector(new GestureHandler());
        Gdx.input.setInputProcessor(gesture);
    }

    // Piggybacked off GameScreen#Render
    public void render(float delta, Stage stage, Towers t, OrthographicCamera camera, Viewport viewport) {
        this.stage = stage;
        this.camera = camera;
        for (Body e : entities)
            if (e.getUserData() instanceof Enemy) {
                e.setGravityScale(0);
                e.setLinearVelocity(1, 0);
            }
        // TODO Testing Only
        boolean validEnemy = false;
        for (Body body : entities)
            if (body.getUserData() instanceof Enemy) {
                validEnemy = true;
            }
        if (!validEnemy) {
            createEntity(0, 0, -SIZE * 2, 0);
        }
        for (Body entity : entities) {
            ((Entity) entity.getUserData()).draw(stage.getBatch(), 0);
        }
    }

    // Piggybacked off GameScreen#Render
    public void run(float delta, Stage stage, Towers t, OrthographicCamera camera, Viewport viewport) {
        world.step(20, 4, 1);
        if (world.getContactCount() > entities.size())
            checkForCollision();
        // TODO Move to after HP check,after working on animation system
        for (Actor actor : stage.getActors())
            if (actor instanceof Entity) {
                if (((Entity) actor).body.getPosition().x > Towers.WIDTH || ((Entity) actor).body.getPosition().y < 0) {
                    if (((Entity) actor).body.getPosition().x > Towers.WIDTH) {
                        baseHP--;
                        if (baseHP <= 0) {
                            gameOver(t);
                        }
                    }
                    killEntity((Entity) actor);
                }
            }
    }

    private void gameOver(Towers t) {
        INSTANCE = new GameManager();
        t.setScreen(new MenuScreen(t));
    }

    private void checkForCollision() {
        for (Contact contact : world.getContactList()) {
            if (contact.getFixtureA() != null && contact.getFixtureA().getBody() != null && contact.getFixtureB() != null && contact.getFixtureB().getBody() != null) {
                Entity a = (Entity) contact.getFixtureA().getBody().getUserData();
                Entity b = (Entity) contact.getFixtureB().getBody().getUserData();
                // TODO Redo once weapons exist
                if (a != null && b != null) {
                    if (!(a instanceof Enemy) && !(b instanceof Enemy))
                        continue;
                    a.hp--;
                    b.hp--;
                    if (a.hp <= 0) {
                        killEntity(((Entity) contact.getFixtureA().getBody().getUserData()));
                    } else if (b.hp <= 0) {
                        killEntity(((Entity) contact.getFixtureB().getBody().getUserData()));
                    }
                }
            }
        }
    }

    private void killEntity(Entity entity) {
        if (entity instanceof Enemy) {
            balance += 100;
            killed++;
        }
        entities.remove(entity.body);
        stage.getActors().removeValue(entity, false);
        ((Entity) entity.body.getUserData()).addAction(Actions.removeActor());
        world.destroyBody(entity.body);
    }

    private Body placeGround() {
        BodyDef groundDef = new BodyDef();
        groundDef.type = BodyDef.BodyType.StaticBody;
        groundDef.position.set(0, 0);
        ChainShape groundShape = new ChainShape();
        groundShape.createChain(new Vector2[]{new Vector2(-2000, STARTING_Y), new Vector2(2000, STARTING_Y)});
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = groundShape;
        Body body = world.createBody(groundDef);
        body.createFixture(fixtureDef);
        return body;
    }

    // TODO Redo for multiple entity types
    public void createEntity(int type, int data, float x, float y) {
        if (type == 0) {
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.DynamicBody;
            bodyDef.position.set(x, y + SIZE);
            Body body = world.createBody(bodyDef);
            PolygonShape square = new PolygonShape();
            square.setAsBox(SIZE, SIZE);
            Enemy entity;
            if (killed > 10 && (Math.random() * 10) > 5) {
                entity = new Enemy(body, new Texture("enemy2.png"), 0, 5);
            } else {
                entity = new Enemy(body, new Texture("enemy.png"), 0, 2);
            }
            body.setUserData(entity);
            entities.add(body);
            stage.addActor(entity);
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = square;
            body.createFixture(fixtureDef);
            square.dispose();
        } else if (type == 1) {
            if (data == 0) {
                if (balance >= 50) {
                    BodyDef bodyDef = new BodyDef();
                    bodyDef.type = BodyDef.BodyType.DynamicBody;
                    bodyDef.position.set(x, y + SIZE);
                    Body body = world.createBody(bodyDef);
                    PolygonShape square = new PolygonShape();
                    square.setAsBox(SIZE, SIZE);
                    Entity entity = new Entity(body, new Texture("basic.png"), 1);
                    body.setUserData(entity);
                    entities.add(body);
                    stage.addActor(entity);
                    FixtureDef fixtureDef = new FixtureDef();
                    fixtureDef.shape = square;
                    body.createFixture(fixtureDef);
                    square.dispose();
                    balance -= 50;
                }
            } else if (data == 1) {
                if (balance >= 200) {
                    BodyDef bodyDef = new BodyDef();
                    bodyDef.type = BodyDef.BodyType.DynamicBody;
                    bodyDef.position.set(x, y + SIZE);
                    Body body = world.createBody(bodyDef);
                    PolygonShape square = new PolygonShape();
                    square.setAsBox(SIZE, SIZE);
                    Entity entity = new Entity(body, new Texture("basic2.png"), 8);
                    body.setUserData(entity);
                    entities.add(body);
                    stage.addActor(entity);
                    FixtureDef fixtureDef = new FixtureDef();
                    fixtureDef.shape = square;
                    body.createFixture(fixtureDef);
                    square.dispose();
                    balance -= 200;
                }
            }
        }
    }


    public static void initGameSetttings() {
        GameManager.INSTANCE.baseHP = Towers.settings.baseHP;
        GameManager.INSTANCE.balance = 500;
        GameManager.INSTANCE.killed = 0;
    }
}
