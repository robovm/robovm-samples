package org.robovm.samples.robopods.parse.anypic.ios.model;

import org.robovm.pods.parse.PFObject;
import org.robovm.pods.parse.PFUser;
import org.robovm.pods.parse.ParseClassName;

@ParseClassName("Activity")
public class PAPActivity extends PFObject {
    public static final String TYPE_KEY = "type";
    public static final String FROM_USER_KEY = "fromUser";
    public static final String TO_USER_KEY = "toUser";
    public static final String CONTENT_KEY = "content";
    public static final String PHOTO_KEY = "photo";

    public PAPActivityType getType() {
        String activity = getString(TYPE_KEY);
        return PAPActivityType.findByKey(activity);
    }

    public void setType(PAPActivityType type) {
        put(TYPE_KEY, type.getKey());
    }

    public PAPUser getFromUser() {
        return (PAPUser) getParseUser(FROM_USER_KEY);
    }

    public void setFromUser(PFUser user) {
        put(FROM_USER_KEY, user);
    }

    public PAPUser getToUser() {
        return (PAPUser) getParseUser(TO_USER_KEY);
    }

    public void setToUser(PFUser user) {
        put(TO_USER_KEY, user);
    }

    public String getContent() {
        return getString(CONTENT_KEY);
    }

    public void setContent(String content) {
        put(CONTENT_KEY, content);
    }

    public PAPPhoto getPhoto() {
        return (PAPPhoto) get(PHOTO_KEY);
    }

    public void setPhoto(PAPPhoto photo) {
        put(PHOTO_KEY, photo);
    }
}
