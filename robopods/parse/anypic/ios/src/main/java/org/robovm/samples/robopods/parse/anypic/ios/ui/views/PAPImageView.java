package org.robovm.samples.robopods.parse.anypic.ios.ui.views;

import org.robovm.apple.foundation.NSData;
import org.robovm.apple.foundation.NSError;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIImageView;
import org.robovm.pods.parse.PFFile;
import org.robovm.pods.parse.PFGetDataCallback;
import org.robovm.samples.robopods.parse.anypic.ios.util.Log;

public class PAPImageView extends UIImageView {
    private String url;

    public void setFile(PFFile file) {
        final String requestURL = file.getUrl(); // Save copy of url locally
        url = file.getUrl(); // Save copy of url on the instance

        file.getDataInBackground(new PFGetDataCallback() {
            @Override
            public void done(NSData data, NSError error) {
                if (error == null) {
                    UIImage image = UIImage.create(data);
                    if (requestURL.equals(url)) {
                        setImage(image);
                        setNeedsDisplay();
                    }
                } else {
                    Log.e("Error on fetching file: %s", error);
                }
            }
        });
    }
}
