package com.example.battleship.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Ship implements Serializable {
    private static final long serialVersionUID = 1L;
    private String type;
    private int size;
    // Data Structure 1: ArrayList used instead of array
    private List<Coordinate> coordinates;
    private int hits;

    public Ship(String type, int size) {
        this.type = type;
        this.size = size;
        this.coordinates = new ArrayList<>();
        this.hits = 0;
    }
    public int getSize() { return size; }

    public void addCoordinate(Coordinate coord) {
        coordinates.add(coord);
    }

    public boolean isSunk() {
        return hits >= size;
    }

    public void registerHit() {
        hits++;
    }

    public List<Coordinate> getCoordinates() {
        return coordinates;
    }

    public String getType() { return type; }
}