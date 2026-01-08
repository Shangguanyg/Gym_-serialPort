package com.ganainy.galaxyrun.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
Very simple implementation of a random-item chooser that uses weights.
Positive weights only!

Inspired by https://stackoverflow.com/a/6409767
 */
public class WeightedRandomChooser<T> {
    private final Random rand;
    private double totalWeight;
    private List<Pair<T, Double>> items = new ArrayList<>();

    public WeightedRandomChooser(Random r) {
        rand = r;
    }

    public void addItem(T item, double weight) {
        if (weight < 0) {
            throw new IllegalArgumentException("Weight cannot be negative");
        }
        items.add(new Pair<>(item, weight));
        totalWeight += weight;
    }

    public T choose() {
        double targetWeight = rand.nextDouble() * totalWeight;
        double runningSum = 0.0;
        for (Pair<T, Double> item : items) {
            runningSum += item.second;
            if (runningSum >= targetWeight) {
                return item.first;
            }
        }
        throw new RuntimeException("Should never happen");
    }
}
