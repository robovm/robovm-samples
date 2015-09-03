package org.robovm.samples.collectionview.ui;

import org.robovm.apple.coregraphics.CGContext;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.uikit.UIBezierPath;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIGraphics;
import org.robovm.apple.uikit.UIView;

public class CustomCellBackground extends UIView {
    public CustomCellBackground(CGRect rect) {
        super(rect);
    }

    @Override
    public void draw(CGRect rect) {
        // draw a rounded rect bezier path filled with blue
        CGContext context = UIGraphics.getCurrentContext();
        context.saveGState();

        UIBezierPath bezierPath = UIBezierPath.createFromRoundedRect(rect, 5);
        bezierPath.setLineWidth(5);
        UIColor.black().setStroke();

        UIColor fillColor = UIColor.fromRGBA(0.529, 0.808, 0.922, 1); // #87ceeb

        fillColor.setFill();

        bezierPath.stroke();
        bezierPath.fill();

        context.restoreGState();
    }
}
