package com.wurmcraft.towers.game.api;


import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class ShopData {

    public Image image;
    public Entity.Type type;
    public int extraData;
    public int x;
    public int y;
    public int cost;
    public int hp;

    public ShopData(Image image, int x, int y, int cost) {
        this.image = image;
        this.x = x;
        this.y = y;
        this.cost = cost;
        this.extraData = 0;
        this.type = Entity.Type.BLOCK;
        this.hp = 1;
    }

    public ShopData(Image image, Entity.Type type, int extraData, int x, int y, int cost) {
        this.image = image;
        this.type = type;
        this.extraData = extraData;
        this.x = x;
        this.y = y;
        this.cost = cost;
        this.hp = 1;
    }

    public ShopData(Image image, Entity.Type type, int extraData, int x, int y, int cost, int hp) {
        this.image = image;
        this.type = type;
        this.extraData = extraData;
        this.x = x;
        this.y = y;
        this.cost = cost;
        this.hp = hp;
    }
}
