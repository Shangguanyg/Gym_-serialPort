package com.ganainy.galaxyrun.engine.map;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

// TODO: include/calculate metadata?
public class Path {
    public List<TileLocation> path;

    public Path() {
        path = new ArrayList<>();
    }

    public Path(List<TileLocation> path) {
        this.path = path;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Path(");
        for (TileLocation loc : path) {
            result.append("(").append(loc.row).append(", ").append(loc.col).append(")");
        }
        result.append(")");
        return result.toString();
    }
}
