package com.ganainy.galaxyrun.engine;

import com.ganainy.galaxyrun.sprite.Alien;
import com.ganainy.galaxyrun.sprite.AlienBullet;
import com.ganainy.galaxyrun.sprite.Asteroid;
import com.ganainy.galaxyrun.sprite.Bullet;
import com.ganainy.galaxyrun.sprite.Coin;
import com.ganainy.galaxyrun.sprite.Obstacle;
import com.ganainy.galaxyrun.sprite.Spaceship;
import com.ganainy.galaxyrun.sprite.Sprite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Super-simple layer-based collision detection.
 * TODO: this isn't the right way. Sprites should be able to decide themselves
 *   how to react to collisions with other Sprites. Rather than reducing
 *   collision checks this way, we should implement some kind of bucket-
 *   based checking
 */
public class HitDetector {
    // Stores the references of two sprites that are in collision
    public static class CollisionTuple {
        public Sprite sprite1, sprite2;

        public CollisionTuple(Sprite sprite1, Sprite sprite2) {
            this.sprite1 = sprite1;
            this.sprite2 = sprite2;
        }
    }

    public static class CollisionLayer {
        public String layerName;
        public String[] collidingLayers;

        public CollisionLayer(String layerName, String[] collidingLayers) {
            this.layerName = layerName;
            this.collidingLayers = collidingLayers;
        }
    }

    // TODO: NAMING IS HORRIBLE
    // Configured CollisionLayers
    private HashMap<String, CollisionLayer> layers;
    // Existing Sprites, mapped to the corresponding layer
    private HashMap<String, List<Sprite>> sprites;

    public HitDetector(List<CollisionLayer> collisionLayers) {
        layers = new HashMap<>();
        sprites = new HashMap<>();
        for (CollisionLayer layer : collisionLayers) {
            layers.put(layer.layerName, layer);
            sprites.put(layer.layerName, new ArrayList<>());
        }
    }

    public void addSprite(Sprite sprite) {
        sprites.get(sprite.getClass().getSimpleName()).add(sprite);
    }

    public List<CollisionTuple> determineCollisions() {
        List<CollisionTuple> collisions = new LinkedList<>();
        for (CollisionLayer layerDef : layers.values()) {
            for (String collidingLayer : layerDef.collidingLayers) {
                checkCollisionsBtwnLayers(
                        layerDef.layerName,
                        collidingLayer,
                        collisions
                );
            }
        }
        return collisions;
    }

    public void clear() {
        for (String layerName : layers.keySet()) {
            sprites.get(layerName).clear();
        }
    }

    private void checkCollisionsBtwnLayers(
            String layerOne,
            String layerTwo,
            List<CollisionTuple> collisionsList
    ) {
        List<Sprite> spritesLayerOne = sprites.get(layerOne);
        List<Sprite> spritesLayerTwo = sprites.get(layerTwo);

        for (Sprite l1Sprite : spritesLayerOne) {
            if (l1Sprite.canCollide()) {
                for (Sprite l2Sprite : spritesLayerTwo) {
                    if (l2Sprite.canCollide() && l1Sprite.collidesWith(l2Sprite)) {
                        collisionsList.add(new CollisionTuple(
                                l1Sprite,
                                l2Sprite
                        ));
                    }
                }
            }
        }
    }

    public static HitDetector MakeDefaultHitDetector() {
        return new HitDetector(new ArrayList<>(Arrays.asList(
                new HitDetector.CollisionLayer(
                        Alien.class.getSimpleName(),
                        new String[]{}
                ),
                new HitDetector.CollisionLayer(
                        AlienBullet.class.getSimpleName(),
                        new String[]{}
                ),
                new HitDetector.CollisionLayer(
                        Asteroid.class.getSimpleName(),
                        new String[]{}
                ),
                new HitDetector.CollisionLayer(
                        Bullet.class.getSimpleName(),
                        new String[] {
                                Obstacle.class.getSimpleName(),
                                Alien.class.getSimpleName(),
                                Asteroid.class.getSimpleName(),
                        }
                ),
                new HitDetector.CollisionLayer(
                        Coin.class.getSimpleName(),
                        new String[]{}
                ),
                new HitDetector.CollisionLayer(
                        Obstacle.class.getSimpleName(),
                        new String[]{}
                ),
                new HitDetector.CollisionLayer(
                        Spaceship.class.getSimpleName(),
                        new String[] {
                                Obstacle.class.getSimpleName(),
                                Coin.class.getSimpleName(),
                                Alien.class.getSimpleName(),
                                AlienBullet.class.getSimpleName(),
                                Asteroid.class.getSimpleName(),
                        }
                )
        )));
    }
}
