package org.robovm.samples.robopods.google.games.typenumber.ios.leaderboards;

public enum Leaderboard {
    Easy("CgkI3vnvkfoOEAIQBw"),
    Hard("CgkI3vnvkfoOEAIQCA");

    private String id;

    Leaderboard(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
