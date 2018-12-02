package com.wurmcraft.towers.game.api;

public class GameState {

    public int wave;
    public CubeData[] cubes;
    public long balance;
    public int hp;
    public int kills;
    public int score;

    public GameState(int wave, CubeData[] cubes, long balance, int hp, int kills, int score) {
        this.wave = wave;
        this.cubes = cubes;
        this.balance = balance;
        this.hp = hp;
        this.kills = kills;
        this.score = score;
    }
}
