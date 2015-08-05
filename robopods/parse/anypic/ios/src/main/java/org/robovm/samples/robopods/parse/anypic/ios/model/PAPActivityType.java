package org.robovm.samples.robopods.parse.anypic.ios.model;

public enum PAPActivityType {
    LIKE("like", "liked your photo"),
    FOLLOW("follow", "started following you"),
    COMMENT("comment", "commented on your photo"),
    JOINED("joined", "joined Anypic");

    private String key;
    private String message;

    PAPActivityType(String key, String message) {
        this.key = key;
        this.message = message;
    }

    public String getKey() {
        return key;
    }

    public String getMessage() {
        return message;
    }

    protected static PAPActivityType findByKey(String key) {
        if (key == null) {
            throw new NullPointerException("key");
        }
        PAPActivityType[] values = values();
        for (PAPActivityType type : values) {
            if (key.equals(type.key)) {
                return type;
            }
        }
        return null;
    }
}
