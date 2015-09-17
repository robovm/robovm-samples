package org.robovm.samples.collectionview.ui;

import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.foundation.NSIndexPath;
import org.robovm.apple.uikit.UICollectionView;
import org.robovm.apple.uikit.UICollectionViewCell;
import org.robovm.apple.uikit.UICollectionViewController;
import org.robovm.apple.uikit.UICollectionViewFlowLayout;
import org.robovm.apple.uikit.UICollectionViewScrollDirection;
import org.robovm.apple.uikit.UIImage;

public class ViewController extends UICollectionViewController {
    private static final String CELL_ID = "cellID";

    private final DetailViewController detailViewController;

    public ViewController() {
        super(getLayout());

        detailViewController = new DetailViewController();

        setTitle("Collection View");
        getCollectionView().registerReusableCellClass(Cell.class, CELL_ID);
    }

    private static UICollectionViewFlowLayout getLayout() {
        UICollectionViewFlowLayout layout = new UICollectionViewFlowLayout();
        layout.setItemSize(new CGSize(153, 128));
        layout.setMinimumInteritemSpacing(10);
        layout.setMinimumLineSpacing(10);
        layout.setScrollDirection(UICollectionViewScrollDirection.Vertical);
        return layout;
    }

    @Override
    public long getNumberOfItemsInSection(UICollectionView collectionView, long section) {
        return 32;
    }

    @Override
    public UICollectionViewCell getCellForItem(UICollectionView collectionView, NSIndexPath indexPath) {
        // we're going to use a custom UICollectionViewCell, which will hold an
        // image and its label
        Cell cell = (Cell) collectionView.dequeueReusableCell(CELL_ID, indexPath);

        // cell.setBackgroundColor(UIColor.blue());

        // make the cell's title the actual NSIndexPath value
        cell.getLabel().setText(String.format("{%d,%d}", indexPath.getRow(), indexPath.getSection()));

        // load the image for this cell
        cell.getImage().setImage(UIImage.getImage(String.valueOf(indexPath.getRow()) + ".jpg"));

        return cell;
    }

    @Override
    public void didSelectItem(UICollectionView collectionView, NSIndexPath indexPath) {
        // the user tapped a collection item, load and set the image on the
        // detail view controller
        UIImage image = UIImage.getImage(String.format("%d_full.jpg", indexPath.getRow()));
        detailViewController.setImage(image);
        getNavigationController().pushViewController(detailViewController, true);
    }
}
