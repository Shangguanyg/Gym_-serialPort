package com.ganainy.galaxyrun.engine.map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;

/*
Find paths through a chunk.
 */
public class PathFinder {

    public static Path findPath(
            Chunk chunk,
            int maxSteps
    ) throws NoPathFoundException {
        List<TileLocation> possibleStarts = new ArrayList<>(6);
        for (int i = 0; i < chunk.numRows; i++) {
            if (chunk.tiles[i][0] == TileType.EMPTY) {
                possibleStarts.add(new TileLocation(i, 0));
            }
        }
        Collections.shuffle(possibleStarts);
        return findPath(chunk, possibleStarts, maxSteps);
    }

    public static Path findPath(
            Chunk chunk,
            List<TileLocation> startLocs,
            int maxSteps
    ) throws NoPathFoundException {
        PriorityQueue<PathNode> pathQueue = new PriorityQueue<>();

        for (TileLocation start : startLocs) {
            if (isFlyable(chunk, start.row, start.col)) {
                pathQueue.add(new PathNode(start.row, start.col, 0, null));
            }
        }

        return runDijkstra(chunk, pathQueue, maxSteps);
    }

    private static Path runDijkstra(
            Chunk chunk,
            PriorityQueue<PathNode> pathQueue,
            int maxSteps
    ) throws NoPathFoundException {
        HashSet<PathNode> explored = new HashSet<>();
        int numSteps = 0;

        while (!pathQueue.isEmpty() && numSteps < maxSteps) {
            numSteps++;
            PathNode next = pathQueue.poll();
            if (next == null || explored.contains(next)) {
                continue;
            }
            // TODO: is the first one found the solution?... can't remember
            if (next.col + 1 == chunk.numCols) {
                return unrollPath(next);
            }
            if (isFlyable(chunk, next.row, next.col + 1)) {
                pathQueue.add(new PathNode(next.row, next.col + 1, 0, next));
            }
            if (isFlyable(chunk, next.row + 1, next.col)) {
                pathQueue.add(new PathNode(next.row + 1, next.col, 0, next));
            }
            if (isFlyable(chunk, next.row - 1, next.col)) {
                pathQueue.add(new PathNode(next.row - 1, next.col, 0, next));
            }
            explored.add(next);
        }
        throw new NoPathFoundException();
    }

    private static boolean isFlyable(Chunk chunk, int i, int j) {
        return i >= 0 && i < chunk.numRows && j >= 0 && j < chunk.numCols && chunk.tiles[i][j] == TileType.EMPTY;
    }

    private static Path unrollPath(PathNode end) {
        List<TileLocation> unrolled = new ArrayList<>(20);
        while (end != null) {
            unrolled.add(new TileLocation(end.row, end.col));
            end = end.prev;
        }
        Collections.reverse(unrolled);
        return new Path(unrolled);
    }
}
