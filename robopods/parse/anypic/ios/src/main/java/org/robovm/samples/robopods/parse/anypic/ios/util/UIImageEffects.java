package org.robovm.samples.robopods.parse.anypic.ios.util;

import org.robovm.apple.coregraphics.CGContext;
import org.robovm.apple.coregraphics.CGPoint;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIGraphics;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIScreen;

public class UIImageEffects {

    public static UIImage applyLightEffect(UIImage image) {
        UIColor tintColor = UIColor.fromWhiteAlpha(1, 0.3);
        return applyBlur(image, 30, tintColor, 1.8, null);
    }

    public static UIImage applyExtraLightEffect(UIImage image) {
        UIColor tintColor = UIColor.fromWhiteAlpha(0.97, 0.82);
        return applyBlur(image, 20, tintColor, 1.8, null);
    }

    public static UIImage applyDarkEffect(UIImage image) {
        UIColor tintColor = UIColor.fromWhiteAlpha(0.11, 0.73);
        return applyBlur(image, 20, tintColor, 1.8, null);
    }

    public static UIImage applyTintEffect(UIImage image, UIColor tintColor) {
        final double effectColorAlpha = 0.6;
        UIColor effectColor = tintColor;
        long componentCount = tintColor.getCGColor().getNumberOfComponents();
        if (componentCount == 2) {
            double[] b = tintColor.getWhiteAlpha();
            if (b != null) {
                effectColor = UIColor.fromWhiteAlpha(b[0], effectColorAlpha);
            }
        } else {
            double[] rgba = tintColor.getRGBA();
            if (rgba != null) {
                effectColor = UIColor.fromRGBA(rgba[0], rgba[1], rgba[2], effectColorAlpha);
            }
        }
        return applyBlur(image, 10, effectColor, -1, null);
    }

    public static UIImage applyBlur(UIImage image, double blurRadius, UIColor tintColor, double saturationDeltaFactor,
            UIImage maskImage) {
        // Check pre-conditions.
        if (image.getSize().getWidth() < 1 || image.getSize().getHeight() < 1) {
            Log.e("invalid size: (%.2f x %.2f). Both dimensions must be >= 1: %s", image.getSize().getWidth(), image
                    .getSize().getHeight(), image);
            return null;
        }
        if (image.getCGImage() == null) {
            Log.e("image must be backed by a CGImage: %s", image);
            return null;
        }
        if (maskImage != null && maskImage.getCGImage() == null) {
            Log.e("maskImage must be backed by a CGImage: %s", maskImage);
            return null;
        }

        CGRect imageRect = new CGRect(CGPoint.Zero(), image.getSize());
        UIImage effectImage = image;

        boolean hasBlur = blurRadius > 0.000001f;
        boolean hasSaturationChange = Math.abs(saturationDeltaFactor - 1) > 0.000001f;
        if (hasBlur | hasSaturationChange) {
//            UIGraphics.beginImageContext(image.getSize(), false, UIScreen.getMainScreen().getScale());
//            CGContext effectInContext = UIGraphics.getCurrentContext();
//            effectInContext.scaleCTM(1, -1);
//            effectInContext.translateCTM(1, -1);
//            effectInContext.drawImage(imageRect, image.getCGImage());

//            vImage_Buffer effectInBuffer;
//            effectInBuffer.data     = CGBitmapContextGetData(effectInContext); TODO
//            effectInBuffer.width    = CGBitmapContextGetWidth(effectInContext);
//            effectInBuffer.height   = CGBitmapContextGetHeight(effectInContext);
//            effectInBuffer.rowBytes = CGBitmapContextGetBytesPerRow(effectInContext);
//        
//            UIGraphicsBeginImageContextWithOptions(self.size, NO, [[UIScreen mainScreen] scale]);
//            CGContextRef effectOutContext = UIGraphicsGetCurrentContext();
//            vImage_Buffer effectOutBuffer;
//            effectOutBuffer.data     = CGBitmapContextGetData(effectOutContext);
//            effectOutBuffer.width    = CGBitmapContextGetWidth(effectOutContext);
//            effectOutBuffer.height   = CGBitmapContextGetHeight(effectOutContext);
//            effectOutBuffer.rowBytes = CGBitmapContextGetBytesPerRow(effectOutContext);
//
//            if (hasBlur) {
//                // A description of how to compute the box kernel width from the Gaussian
//                // radius (aka standard deviation) appears in the SVG spec:
//                // http://www.w3.org/TR/SVG/filters.html#feGaussianBlurElement
//                // 
//                // For larger values of 's' (s >= 2.0), an approximation can be used: Three
//                // successive box-blurs build a piece-wise quadratic convolution kernel, which
//                // approximates the Gaussian kernel to within roughly 3%.
//                //
//                // let d = floor(s * 3*sqrt(2*pi)/4 + 0.5)
//                // 
//                // ... if d is odd, use three box-blurs of size 'd', centered on the output pixel.
//                // 
//                CGFloat inputRadius = blurRadius * [[UIScreen mainScreen] scale];
//                NSUInteger radius = floor(inputRadius * 3. * sqrt(2 * M_PI) / 4 + 0.5);
//                if (radius % 2 != 1) {
//                    radius += 1; // force radius to be odd so that the three box-blur methodology works.
//                }
//                vImageBoxConvolve_ARGB8888(&effectInBuffer, &effectOutBuffer, NULL, 0, 0, radius, radius, 0, kvImageEdgeExtend);
//                vImageBoxConvolve_ARGB8888(&effectOutBuffer, &effectInBuffer, NULL, 0, 0, radius, radius, 0, kvImageEdgeExtend);
//                vImageBoxConvolve_ARGB8888(&effectInBuffer, &effectOutBuffer, NULL, 0, 0, radius, radius, 0, kvImageEdgeExtend);
//            }
//            BOOL effectImageBuffersAreSwapped = NO;
//            if (hasSaturationChange) {
//                CGFloat s = saturationDeltaFactor;
//                CGFloat floatingPointSaturationMatrix[] = {
//                    0.0722 + 0.9278 * s,  0.0722 - 0.0722 * s,  0.0722 - 0.0722 * s,  0,
//                    0.7152 - 0.7152 * s,  0.7152 + 0.2848 * s,  0.7152 - 0.7152 * s,  0,
//                    0.2126 - 0.2126 * s,  0.2126 - 0.2126 * s,  0.2126 + 0.7873 * s,  0,
//                                      0,                    0,                    0,  1,
//                };
//                const int32_t divisor = 256;
//                NSUInteger matrixSize = sizeof(floatingPointSaturationMatrix)/sizeof(floatingPointSaturationMatrix[0]);
//                int16_t saturationMatrix[matrixSize];
//                for (NSUInteger i = 0; i < matrixSize; ++i) {
//                    saturationMatrix[i] = (int16_t)roundf(floatingPointSaturationMatrix[i] * divisor);
//                }
//                if (hasBlur) {
//                    vImageMatrixMultiply_ARGB8888(&effectOutBuffer, &effectInBuffer, saturationMatrix, divisor, NULL, NULL, kvImageNoFlags);
//                    effectImageBuffersAreSwapped = YES;
//                }
//                else {
//                    vImageMatrixMultiply_ARGB8888(&effectInBuffer, &effectOutBuffer, saturationMatrix, divisor, NULL, NULL, kvImageNoFlags);
//                }
//            }
//            if (!effectImageBuffersAreSwapped)
//                effectImage = UIGraphicsGetImageFromCurrentImageContext();
//            UIGraphicsEndImageContext();
//
//            if (effectImageBuffersAreSwapped)
//                effectImage = UIGraphicsGetImageFromCurrentImageContext();
//            UIGraphicsEndImageContext();
        }

        // Set up output context.
        UIGraphics.beginImageContext(image.getSize(), false, UIScreen.getMainScreen().getScale());
        CGContext outputContext = UIGraphics.getCurrentContext();
        outputContext.scaleCTM(1, -1);
        outputContext.translateCTM(0, -image.getSize().getHeight());

        // Draw base image.
        outputContext.drawImage(imageRect, image.getCGImage());

        // Draw effect image.
        if (hasBlur) {
            outputContext.saveGState();
            if (maskImage != null) {
                outputContext.clipToMask(imageRect, maskImage.getCGImage());
            }
            outputContext.drawImage(imageRect, effectImage.getCGImage());
            outputContext.restoreGState();
        }

        // Add in color tint.
        if (tintColor != null) {
            outputContext.saveGState();
            outputContext.setFillColor(tintColor.getCGColor());
            outputContext.fillRect(imageRect);
            outputContext.saveGState();
        }

        // Output image is ready.
        UIImage outputImage = UIGraphics.getImageFromCurrentImageContext();
        UIGraphics.endImageContext();

        return outputImage;
    }
}
