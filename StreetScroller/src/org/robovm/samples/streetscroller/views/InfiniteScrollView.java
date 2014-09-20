
package org.robovm.samples.streetscroller.views;

import java.util.ArrayList;
import java.util.List;

import org.robovm.apple.coregraphics.CGPoint;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.uikit.UILabel;
import org.robovm.apple.uikit.UIScrollView;
import org.robovm.apple.uikit.UIView;

public class InfiniteScrollView extends UIScrollView {
    private final List<UILabel> visibleLabels = new ArrayList<>();
    private final UIView labelContainerView;

    public InfiniteScrollView (CGRect frame) {
        super(frame);

        setContentSize(new CGSize(5000, getFrame().size().height()));

        labelContainerView = new UIView(new CGRect(0, 0, getContentSize().width(), getContentSize().height() / 2));
        labelContainerView.setUserInteractionEnabled(false);
        addSubview(labelContainerView);

        // hide horizontal scroll indicator so our recentering trick is not revealed
        setShowsHorizontalScrollIndicator(false);
    }

    /** Recenter content periodically to achieve impression of infinite scrolling. */
    private void recenterIfNecessary () {
        CGPoint currentOffset = getContentOffset();
        double contentWidth = getContentSize().width();
        double centerOffsetX = (contentWidth - getBounds().size().width()) / 2.0;
        double distanceFromCenter = Math.abs(currentOffset.x() - centerOffsetX);

        if (distanceFromCenter > (contentWidth / 4.0)) {
            setContentOffset(currentOffset.x(centerOffsetX));

            // move content by the same amount so it appears to stay still
            for (UILabel label : visibleLabels) {
                CGPoint center = labelContainerView.convertPointToView(label.getCenter(), this);
                center.x(center.x() + centerOffsetX - currentOffset.x());
                label.setCenter(convertPointToView(center, labelContainerView));
            }
        }
    }

    @Override
    public void layoutSubviews () {
        super.layoutSubviews();

        recenterIfNecessary();

        // tile content in visible bounds
        CGRect visibleBounds = convertRectToView(getBounds(), labelContainerView);
        double minimumVisibleX = visibleBounds.getMinX();
        double maximumVisibleX = visibleBounds.getMaxX();

        tileLabels(minimumVisibleX, maximumVisibleX);
    }

    private UILabel insertLabel () {
        UILabel label = new UILabel(new CGRect(0, 0, 500, 80));
        label.setNumberOfLines(3);
        label.setText("1024 Block Street\nShaffer, CA\n95014");
        labelContainerView.addSubview(label);

        return label;
    }

    private double placeNewLabelOnRight (double rightEdge) {
        UILabel label = insertLabel();
        visibleLabels.add(label); // add rightmost label at the end of the array

        CGRect frame = label.getFrame();
        frame.origin().x(rightEdge);
        frame.origin().y(labelContainerView.getBounds().size().height() - frame.size().height());
        label.setFrame(frame);

        return frame.getMaxX();
    }

    private double placeNewLabelOnLeft (double leftEdge) {
        UILabel label = insertLabel();
        visibleLabels.add(0, label); // add leftmost label at the beginning of the array

        CGRect frame = label.getFrame();
        frame.origin().x(leftEdge - frame.size().width());
        frame.origin().y(labelContainerView.getBounds().size().height() - frame.size().height());
        label.setFrame(frame);

        return frame.getMinX();
    }

    private void tileLabels (double minimumVisibleX, double maximumVisibleX) {
        // the upcoming tiling logic depends on there already being at least one label in the visibleLabels array, so
        // to kick off the tiling we need to make sure there's at least one label
        if (visibleLabels.size() == 0) {
            placeNewLabelOnRight(minimumVisibleX);
        }

        // add labels that are missing on right side
        UILabel lastLabel = visibleLabels.get(visibleLabels.size() - 1);
        double rightEdge = lastLabel.getFrame().getMaxX();
        while (rightEdge < maximumVisibleX) {
            rightEdge = placeNewLabelOnRight(rightEdge);
        }

        // add labels that are missing on left side
        UILabel firstLabel = visibleLabels.get(0);
        double leftEdge = firstLabel.getFrame().getMinX();
        while (leftEdge > minimumVisibleX) {
            leftEdge = placeNewLabelOnLeft(leftEdge);
        }

        // remove labels that have fallen off right edge
        lastLabel = visibleLabels.get(visibleLabels.size() - 1);
        while (lastLabel.getFrame().origin().x() > maximumVisibleX) {
            lastLabel.removeFromSuperview();
            visibleLabels.remove(visibleLabels.size() - 1);
            lastLabel = visibleLabels.get(visibleLabels.size() - 1);
        }

        // remove labels that have fallen off left edge
        firstLabel = visibleLabels.get(0);
        while (firstLabel.getFrame().getMaxX() < minimumVisibleX) {
            firstLabel.removeFromSuperview();
            visibleLabels.remove(0);
            firstLabel = visibleLabels.get(0);
        }
    }
}
