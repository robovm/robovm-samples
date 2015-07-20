package org.robovm.samples.robopods.google.games.typenumber.ios.game;

public enum DifficultyLevel {
    Easy, Hard;

    public static DifficultyLevel getByOrdinal(int ordinal) {
        return values()[ordinal];
    }
}
