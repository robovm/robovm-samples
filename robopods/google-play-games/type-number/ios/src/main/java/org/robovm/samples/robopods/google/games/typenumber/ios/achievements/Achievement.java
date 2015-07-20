package org.robovm.samples.robopods.google.games.typenumber.ios.achievements;

public enum Achievement {
    Prime("CgkI3vnvkfoOEAIQAQ"),
    Bored("CgkI3vnvkfoOEAIQAg"),
    Humble("CgkI3vnvkfoOEAIQAw"),
    Cocky("CgkI3vnvkfoOEAIQBA"),
    Leet("CgkI3vnvkfoOEAIQBQ"),
    ReallyBored("CgkI3vnvkfoOEAIQBg");

    private String id;

    Achievement(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
