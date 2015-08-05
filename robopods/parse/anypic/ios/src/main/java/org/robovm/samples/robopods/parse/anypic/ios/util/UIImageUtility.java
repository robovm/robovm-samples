package org.robovm.samples.robopods.parse.anypic.ios.util;

import org.robovm.apple.coregraphics.CGAffineTransform;
import org.robovm.apple.coregraphics.CGBitmapContext;
import org.robovm.apple.coregraphics.CGBitmapInfo;
import org.robovm.apple.coregraphics.CGColorSpace;
import org.robovm.apple.coregraphics.CGContext;
import org.robovm.apple.coregraphics.CGImage;
import org.robovm.apple.coregraphics.CGImageAlphaInfo;
import org.robovm.apple.coregraphics.CGInterpolationQuality;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIViewContentMode;

public class UIImageUtility {

    /**
     * 
     * @param image
     * @return true if the image has an alpha layer
     */
    public static boolean hasAlpha(UIImage image) {
        CGImageAlphaInfo alpha = image.getCGImage().getAlphaInfo();
        return alpha == CGImageAlphaInfo.First || alpha == CGImageAlphaInfo.Last ||
                alpha == CGImageAlphaInfo.PremultipliedFirst || alpha == CGImageAlphaInfo.PremultipliedLast;
    }

    /**
     * 
     * @param image
     * @return a copy of the given image, adding an alpha channel if it doesn't
     *         already have one
     */
    public static UIImage addAlpha(UIImage image) {
        if (hasAlpha(image)) {
            return image;
        }

        CGImage i = image.getCGImage();
        long width = i.getWidth();
        long height = i.getHeight();

        // The bitsPerComponent and bitmapInfo values are hard-coded to prevent
        // an "unsupported parameter combination" error
        CGBitmapContext offscreenContext = CGBitmapContext.create(width, height, 8, 0, i.getColorSpace(),
                new CGBitmapInfo(CGBitmapInfo.ByteOrderDefault.value() | CGImageAlphaInfo.PremultipliedFirst.value())); // TODO
                                                                                                                        // could
                                                                                                                        // be
                                                                                                                        // improved

        // Draw the image into the context and retrieve the new image, which
        // will now have an alpha layer
        offscreenContext.drawImage(new CGRect(0, 0, width, height), i);
        CGImage ia = offscreenContext.toImage();
        UIImage alphaImage = UIImage.create(ia);

        return alphaImage;
    }

    /**
     * 
     * @param image
     * @param borderSize
     * @return a copy of the image with a transparent border of the given size
     *         added around its edges. If the image has no alpha layer, one will
     *         be added to it.
     */
    public static UIImage addTransparentBorder(UIImage image, int borderSize) {
        // If the image does not have an alpha layer, add one
        image = addAlpha(image);

        CGRect newRect = new CGRect(0, 0, image.getSize().getWidth() + borderSize * 2, image.getSize().getHeight()
                + borderSize * 2);

        CGImage i = image.getCGImage();
        // Build a context that's the same dimensions as the new size
        CGBitmapContext bitmap = CGBitmapContext.create((long) newRect.getSize().getWidth(), (long) newRect.getSize()
                .getHeight(), i.getBitsPerComponent(), 0, i.getColorSpace(), i.getBitmapInfo());

        // Draw the image in the center of the context, leaving a gap around the
        // edges
        CGRect imageLocation = new CGRect(borderSize, borderSize, image.getSize().getWidth(), image.getSize()
                .getHeight());
        bitmap.drawImage(imageLocation, i);
        CGImage borderImage = bitmap.toImage();

        // Create a mask to make the border transparent, and combine it with the
        // image
        CGImage maskImage = newBorderMask(borderSize, newRect.getSize());
        CGImage tbi = CGImage.createWithMask(borderImage, maskImage);
        UIImage transparentBorderImage = UIImage.create(tbi);

        return transparentBorderImage;
    }

    /**
     * 
     * @param borderSize
     * @param size
     * @return a mask that makes the outer edges transparent and everything else
     *         opaque The size must include the entire mask (opaque part +
     *         transparent border)
     */
    private static CGImage newBorderMask(int borderSize, CGSize size) {
        CGColorSpace colorSpace = CGColorSpace.createDeviceGray();

        // Build a context that's the same dimensions as the new size
        CGBitmapContext maskContext = CGBitmapContext.create((long) size.getWidth(), (long) size.getHeight(), 8, 0,
                colorSpace, new CGBitmapInfo(CGBitmapInfo.ByteOrderDefault.value() | CGImageAlphaInfo.None.value())); // TODO
        // easier

        // Start with a mask that's entirely transparent
        maskContext.setFillColor(UIColor.black().getCGColor());
        maskContext.fillRect(new CGRect(0, 0, size.getWidth(), size.getHeight()));

        // Make the inner part (within the border) opaque
        maskContext.setFillColor(UIColor.white().getCGColor());
        maskContext.fillRect(new CGRect(borderSize, borderSize, size.getWidth() - borderSize * 2, size.getHeight()
                - borderSize * 2));

        // Get an image of the context
        CGImage maskImage = maskContext.toImage();

        return maskImage;
    }

    /**
     * 
     * @param image
     * @param bounds
     * @return a copy of this image that is cropped to the given bounds. The
     *         bounds will be adjusted using CGRectIntegral. This method ignores
     *         the image's imageOrientation setting.
     */
    public static UIImage crop(UIImage image, CGRect bounds) {
        CGImage i = CGImage.createWithImageInRect(image.getCGImage(), bounds);
        UIImage croppedImage = UIImage.create(i);
        return croppedImage;
    }

    /**
     * 
     * @param image
     * @param thumbnailSize
     * @param borderSize
     * @param cornerRadius
     * @param quality
     * @return a copy of this image that is squared to the thumbnail size. If
     *         transparentBorder is non-zero, a transparent border of the given
     *         size will be added around the edges of the thumbnail. (Adding a
     *         transparent border of at least one pixel in size has the
     *         side-effect of antialiasing the edges of the image when rotating
     *         it using Core Animation.)
     */
    public static UIImage createThumbnail(UIImage image, int thumbnailSize, int borderSize, int cornerRadius,
            CGInterpolationQuality quality) {
        UIImage resizedImage = resize(image, UIViewContentMode.ScaleAspectFill,
                new CGSize(thumbnailSize, thumbnailSize), quality);

        // Crop out any part of the image that's larger than the thumbnail size
        // The cropped rect must be centered on the resized image
        // Round the origin points so that the size isn't altered when
        // CGRectIntegral is later invoked
        CGRect cropRect = new CGRect(Math.round((resizedImage.getSize().getWidth() - thumbnailSize) / 2),
                Math.round((resizedImage.getSize().getHeight() - thumbnailSize) / 2), thumbnailSize, thumbnailSize);
        UIImage croppedImage = crop(resizedImage, cropRect);

        UIImage transparentBorderImage = borderSize != 0 ? addTransparentBorder(croppedImage, borderSize)
                : croppedImage;

        return createRoundedCornerImage(transparentBorderImage, cornerRadius, borderSize);
    }

    /**
     * 
     * @param image
     * @param newSize
     * @param quality
     * @return a rescaled copy of the image, taking into account its orientation
     *         The image will be scaled disproportionately if necessary to fit
     *         the bounds specified by the parameter
     */
    public static UIImage resize(UIImage image, CGSize newSize, CGInterpolationQuality quality) {
        boolean drawTransposed;

        switch (image.getOrientation()) {
        case Left:
        case LeftMirrored:
        case Right:
        case RightMirrored:
            drawTransposed = true;
            break;
        default:
            drawTransposed = false;
            break;
        }

        return resize(image, newSize, getTransformForOrientation(image, newSize), drawTransposed, quality);
    }

    /**
     * Resizes the image according to the given content mode, taking into
     * account the image's orientation
     * 
     * @param image
     * @param contentMode
     * @param bounds
     * @param quality
     * @return
     */
    public static UIImage resize(UIImage image, UIViewContentMode contentMode, CGSize bounds,
            CGInterpolationQuality quality) {
        double horizontalRatio = bounds.getWidth() / image.getSize().getWidth();
        double verticalRatio = bounds.getHeight() / image.getSize().getHeight();
        double ratio;

        switch (contentMode) {
        case ScaleAspectFill:
            ratio = Math.max(horizontalRatio, verticalRatio);
            break;
        case ScaleAspectFit:
            ratio = Math.min(horizontalRatio, verticalRatio);
            break;
        default:
            throw new IllegalArgumentException("Unsupported content mode: " + contentMode);
        }

        CGSize newSize = new CGSize(image.getSize().getWidth() * ratio, image.getSize().getHeight() * ratio);
        return resize(image, newSize, quality);
    }

    /**
     * 
     * @param image
     * @param newSize
     * @param transform
     * @param transpose
     * @param quality
     * @return a copy of the image that has been transformed using the given
     *         affine transform and scaled to the new size The new image's
     *         orientation will be UIImageOrientationUp, regardless of the
     *         current image's orientation If the new size is not integral, it
     *         will be rounded up
     */
    private static UIImage resize(UIImage image, CGSize newSize, CGAffineTransform transform, boolean transpose,
            CGInterpolationQuality quality) {
        CGRect newRect = new CGRect(0, 0, newSize.getWidth(), newSize.getHeight()).integral();
        CGRect transposedRect = new CGRect(0, 0, newRect.getSize().getHeight(), newRect.getSize().getWidth());
        CGImage i = image.getCGImage();

        // Build a context that's the same dimensions as the new size
        CGBitmapContext bitmap = CGBitmapContext.create((long) newRect.getSize().getWidth(), (long) newRect.getSize()
                .getHeight(), i.getBitsPerComponent(), 0, i.getColorSpace(), i.getBitmapInfo());

        // Rotate and/or flip the image if required by its orientation
        bitmap.concatCTM(transform);

        // Set the quality level to use when rescaling
        bitmap.setInterpolationQuality(quality);

        // Draw into the context; this scales the image
        bitmap.drawImage(transpose ? transposedRect : newRect, i);

        // Get the resized image from the context and a UIImage
        CGImage ni = bitmap.toImage();
        UIImage newImage = UIImage.create(ni);

        return newImage;
    }

    /**
     * 
     * @param image
     * @param newSize
     * @return an affine transform that takes into account the image orientation
     *         when drawing a scaled image
     */
    private static CGAffineTransform getTransformForOrientation(UIImage image, CGSize newSize) {
        CGAffineTransform transform = CGAffineTransform.Identity();

        switch (image.getOrientation()) {
        case Down: // EXIF = 3
        case DownMirrored: // EXIF = 4
            transform = transform.translate(newSize.getWidth(), newSize.getHeight());
            transform = transform.rotate(Math.PI);
            break;
        case Left: // EXIF = 6
        case LeftMirrored: // EXIF = 5
            transform = transform.translate(newSize.getWidth(), 0);
            transform = transform.rotate(Math.PI / 2);
            break;
        case Right: // EXIF = 8
        case RightMirrored: // EXIF = 7
            transform = transform.translate(0, newSize.getHeight());
            transform.rotate(-Math.PI / 2);
            break;
        default:
            break;
        }

        switch (image.getOrientation()) {
        case UpMirrored: // EXIF = 2
        case DownMirrored: // EXIF = 4
            transform = transform.translate(newSize.getWidth(), 0);
            transform = transform.scale(-1, 1);
            break;
        case LeftMirrored: // EXIF = 5
        case RightMirrored: // EXIF = 7
            transform = transform.translate(newSize.getHeight(), 0);
            transform = transform.scale(-1, 1);
        default:
            break;
        }

        return transform;
    }

    /**
     * Creates a copy of this image with rounded corners If borderSize is
     * non-zero, a transparent border of the given size will also be added
     * Original author: Björn Sållarp. Used with permission. See:
     * http://blog.sallarp.com/iphone-uiimage-round-corners/
     * 
     * @param image
     * @param cornerSize
     * @param borderSize
     * @return
     */
    private static UIImage createRoundedCornerImage(UIImage image, int cornerSize, int borderSize) {
        // If the image does not have an alpha layer, add one
        image = addAlpha(image);

        // Build a context that's the same dimensions as the new size
        CGImage i = image.getCGImage();
        CGBitmapContext context = CGBitmapContext.create((long) image.getSize().getWidth(), (long) image.getSize()
                .getHeight(), i.getBitsPerComponent(), 0, i.getColorSpace(), i.getBitmapInfo());

        // Create a clipping path with rounded corners
        context.beginPath();
        addRoundedRectToPath(new CGRect(borderSize, borderSize, image.getSize().getWidth() - borderSize * 2, image
                .getSize().getHeight() - borderSize * 2)
                , context, cornerSize, cornerSize);
        context.closePath();
        context.clip();

        // Draw the image to the context; the clipping path will make anything
        // outside the rounded rect transparent
        context.drawImage(new CGRect(0, 0, image.getSize().getWidth(), image.getSize().getHeight()), i);

        // Create a CGImage from the context
        CGImage clippedImage = context.toImage();

        // Create a UIImage from the CGImage
        UIImage roundedImage = UIImage.create(clippedImage);
        return roundedImage;
    }

    /**
     * Adds a rectangular path to the given context and rounds its corners by
     * the given extents Original author: Björn Sållarp. Used with permission.
     * See: http://blog.sallarp.com/iphone-uiimage-round-corners/
     * 
     * @param rect
     * @param context
     * @param ovalWidth
     * @param ovalHeight
     */
    private static void addRoundedRectToPath(CGRect rect, CGContext context, double ovalWidth, double ovalHeight) {
        if (ovalWidth == 0 || ovalHeight == 0) {
            context.addRect(rect);
            return;
        }

        context.saveGState();
        context.translateCTM(rect.getMinX(), rect.getMinY());
        context.scaleCTM(ovalWidth, ovalHeight);
        double fw = rect.getWidth() / ovalWidth;
        double fh = rect.getHeight() / ovalHeight;
        context.moveToPoint(fw, fh / 2);
        context.addArcToPoint(fw, fh, fw / 2, fh, 1);
        context.addArcToPoint(0, fh, 0, fh / 2, 1);
        context.addArcToPoint(0, 0, fw / 2, 0, 1);
        context.addArcToPoint(fw, 0, fw, fh / 2, 1);
        context.closePath();
        context.restoreGState();
    }
}
