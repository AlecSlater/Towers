package com.wurmcraft.towers.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.wurmcraft.towers.Towers;
import com.wurmcraft.towers.game.api.Enemy;
import com.wurmcraft.towers.game.api.Entity;

import org.cliffc.high_scale_lib.NonBlockingHashSet;

public class GameManager {

    private final int STARTING_Y = 14;
    private final int SIZE = 64;

    public World world = new World(new Vector2(0, -1), true);
    public NonBlockingHashSet<Body> entities = new NonBlockingHashSet<>();
    private GestureDetector gesture;

    public static final GameManager INSTANCE = new GameManager();

    public GameManager() {
        placeGround();
        gesture = new GestureDetector(new GestureHandler());
        Gdx.input.setInputProcessor(gesture);
    }

    // Piggybacked off GameScreen#Render
    public void render(float delta, Stage stage, Towers t, OrthographicCamera camera, Viewport viewport) {
        for (Body e : entities)
            if (e.getUserData() instanceof Enemy) {
                e.setGravityScale(0);
                e.setLinearVelocity(1, 0);
            }
        // TODO Testing Only
        boolean validEnemy = false;
        for(Body body : entities)
            if(body.getUserData() instanceof Enemy) {
            validEnemy = true;
            }
        if (!validEnemy) {
            createEntity(0, 0, 0, 0);
        }

        for (Body entity : entities) {
            stage.getBatch().draw(((Entity) entity.getUserData()).texture, entity.getPosition().x, entity.getPosition().y);
        }
    }

    // Piggybacked off GameScreen#Render
    public void run(float delta, Stage stage, Towers t, OrthographicCamera camera, Viewport viewport) {
        world.step(20, 4, 1);
        if (world.getContactCount() > 0)
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
                            entities.remove(contact.getFixtureA().getBody());
                            contact.getFixtureA().getBody().destroyFixture(contact.getFixtureA());
                        } else if (b.hp <= 0) {
                            entities.remove(contact.getFixtureB().getBody());
                            contact.getFixtureB().getBody().destroyFixture(contact.getFixtureB());
                        }
                    }
                }
            }
    }

    private void placeGround() {
        BodyDef groundDef = new BodyDef();
        groundDef.type = BodyDef.BodyType.StaticBody;
        groundDef.position.set(0, 0);
        ChainShape groundShape = new ChainShape();
        groundShape.createChain(new Vector2[]{new Vector2(-2000, STARTING_Y), new Vector2(2000, STARTING_Y)});
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = groundShape;
        world.createBody(groundDef).createFixture(fixtureDef);
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
            Enemy entity = new Enemy(body, new Texture("enemy.png"), 0);
            body.setUserData(entity);
            entities.add(body);
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = square;
            body.createFixture(fixtureDef);
            square.dispose();
        } else if (type == 1) {
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.DynamicBody;
            bodyDef.position.set(x, y + SIZE);
            Body body = world.createBody(bodyDef);
            PolygonShape square = new PolygonShape();
            square.setAsBox(SIZE, SIZE);
            Entity entity = new Entity(body, new Texture("basic.png"), 1);
            body.setUserData(entity);
            entities.add(body);
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = square;
            body.createFixture(fixtureDef);
            square.dispose();
        }
    }
}
