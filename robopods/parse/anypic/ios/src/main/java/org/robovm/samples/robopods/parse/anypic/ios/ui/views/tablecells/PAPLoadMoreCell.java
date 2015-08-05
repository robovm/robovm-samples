package org.robovm.samples.robopods.parse.anypic.ios.ui.views.tablecells;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIGraphics;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIImageView;
import org.robovm.apple.uikit.UITableViewCellAccessoryType;
import org.robovm.apple.uikit.UITableViewCellSelectionStyle;
import org.robovm.apple.uikit.UITableViewCellStyle;
import org.robovm.apple.uikit.UIView;
import org.robovm.pods.parse.ui.PFTableViewCell;
import org.robovm.samples.robopods.parse.anypic.ios.util.PAPUtility;

public class PAPLoadMoreCell extends PFTableViewCell {
    private final UIView mainView;
    private UIImageView separatorImageTop;
    private UIImageView separatorImageBottom;
    private final UIImageView loadMoreImageView;

    private boolean hideSeparatorTop;
    private boolean hideSeparatorBottom;

    private double cellInsetWidth;

    public PAPLoadMoreCell(UITableViewCellStyle style, String reuseIdentifier) {
        super(style, reuseIdentifier);

        setOpaque(false);
        setSelectionStyle(UITableViewCellSelectionStyle.None);
        setAccessoryType(UITableViewCellAccessoryType.None);
        setBackgroundColor(UIColor.clear());

        mainView = new UIView(getContentView().getFrame());
        if (reuseIdentifier.equals("NextPageDetails")) {
            mainView.setBackgroundColor(UIColor.white());
        } else {
            mainView.setBackgroundColor(UIColor.black());
        }

        loadMoreImageView = new UIImageView(UIImage.create("CellLoadMore"));
        mainView.addSubview(loadMoreImageView);

        getContentView().addSubview(mainView);
    }

    @Override
    public void layoutSubviews() {
        mainView.setFrame(new CGRect(cellInsetWidth, getContentView().getFrame().getOrigin().getY(), getContentView()
                .getFrame().getSize().getWidth()
                - 2 * cellInsetWidth, getContentView().getFrame().getSize().getHeight()));

        // Layout load more text
        loadMoreImageView.setFrame(new CGRect(105, 15, 111, 18));

        // Layout separator
        separatorImageBottom.setFrame(new CGRect(0, getFrame().getSize().getHeight() - 2, getFrame().getSize()
                .getWidth() - cellInsetWidth * 2, 2));
        separatorImageBottom.setHidden(hideSeparatorBottom);

        separatorImageTop.setFrame(new CGRect(0, 0, getFrame().getSize().getWidth() - cellInsetWidth * 2, 2));
        separatorImageTop.setHidden(hideSeparatorTop);
    }

    @Override
    public void draw(CGRect rect) {
        super.draw(rect);
        if (cellInsetWidth != 0) {
            PAPUtility.drawSideDropShadow(mainView.getFrame(), UIGraphics.getCurrentContext());
        }
    }

    public void setCellInsetWidth(double cellInsetWidth) {
        this.cellInsetWidth = cellInsetWidth;
        mainView.setFrame(new CGRect(cellInsetWidth, mainView.getFrame().getOrigin().getY(), mainView.getFrame()
                .getSize().getWidth()
                - 2 * cellInsetWidth, mainView.getFrame().getSize().getHeight()));
        setNeedsDisplay();
    }

    public UIView getMainView() {
        return mainView;
    }

    public void setHideSeparatorTop(boolean hideSeparatorTop) {
        this.hideSeparatorTop = hideSeparatorTop;
    }

    public void setHideSeparatorBottom(boolean hideSeparatorBottom) {
        this.hideSeparatorBottom = hideSeparatorBottom;
    }

    public UIImageView getSeparatorImageTop() {
        return separatorImageTop;
    }

    public UIImageView getSeparatorImageBottom() {
        return separatorImageBottom;
    }
}
