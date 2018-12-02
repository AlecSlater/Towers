package com.wurmcraft.towers.game.api;

public class CubeData {

    public Entity.Type type;
    public int data;
    public int hp;
    public float x;
    public float y;

    public CubeData(Entity.Type type, int hp, float x, float y) {
        this.type = type;
        this.hp = hp;
        this.x = x;
        this.y = y;
    }
}
