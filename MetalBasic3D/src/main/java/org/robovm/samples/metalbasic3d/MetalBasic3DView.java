/*
 * Copyright (C) 2015 RoboVM AB
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 *   
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 * 
 * Portions of this code is based on Apple Inc's MetalBasic3D sample (v1.0)
 * which is copyright (C) 2014 Apple Inc.
 */
package org.robovm.samples.metalbasic3d;

import org.robovm.apple.coreanimation.CALayer;
import org.robovm.apple.coreanimation.CAMetalDrawable;
import org.robovm.apple.coreanimation.CAMetalLayer;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.foundation.Foundation;
import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.foundation.NSCoder;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.metal.MTLClearColor;
import org.robovm.apple.metal.MTLDevice;
import org.robovm.apple.metal.MTLLoadAction;
import org.robovm.apple.metal.MTLPixelFormat;
import org.robovm.apple.metal.MTLRenderPassColorAttachmentDescriptor;
import org.robovm.apple.metal.MTLRenderPassDepthAttachmentDescriptor;
import org.robovm.apple.metal.MTLRenderPassDescriptor;
import org.robovm.apple.metal.MTLRenderPassStencilAttachmentDescriptor;
import org.robovm.apple.metal.MTLStoreAction;
import org.robovm.apple.metal.MTLTexture;
import org.robovm.apple.metal.MTLTextureDescriptor;
import org.robovm.apple.metal.MTLTextureType;
import org.robovm.apple.uikit.UIView;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.Method;
import org.robovm.rt.bro.annotation.ByVal;
import org.robovm.rt.bro.annotation.Pointer;

/**
 * 
 */
@CustomClass("MetalBasic3DView")
public class MetalBasic3DView extends UIView {
    private MetalBasic3DViewDelegate delegate;

    // view has a handle to the metal device when created
    private MTLDevice device;

    // the current drawable created within the view's CAMetalLayer
    private CAMetalDrawable currentDrawable;

    // The current framebuffer can be read by delegate during
    // -[MetalViewDelegate render:]
    // This call may block until the framebuffer is available.
    private MTLRenderPassDescriptor renderPassDescriptor;

    // set these pixel formats to have the main drawable framebuffer get created
    // with depth and/or stencil attachments
    private MTLPixelFormat depthPixelFormat;
    private MTLPixelFormat stencilPixelFormat;
    private long sampleCount;

    private CAMetalLayer metalLayer;

    private boolean layerSizeDidUpdate;

    private MTLTexture depthTex;
    private MTLTexture stencilTex;
    private MTLTexture msaaTex;

    @Method(selector = "layerClass")
    public static Class<? extends CALayer> getLayerClass() {
        return CAMetalLayer.class;
    }

    public MTLDevice getDevice() {
        return device;
    }
    
    public long getSampleCount() {
        return sampleCount;
    }
    
    public MTLPixelFormat getDepthPixelFormat() {
        return depthPixelFormat;
    }
    
    public void setDepthPixelFormat(MTLPixelFormat depthPixelFormat) {
        this.depthPixelFormat = depthPixelFormat;
    }
    
    public void setStencilPixelFormat(MTLPixelFormat stencilPixelFormat) {
        this.stencilPixelFormat = stencilPixelFormat;
    }
    
    public void setSampleCount(long sampleCount) {
        this.sampleCount = sampleCount;
    }
    
    public void setDelegate(MetalBasic3DViewDelegate delegate) {
        this.delegate = delegate;
    }
    
    private void initCommon() {
        setOpaque(true);
        setBackgroundColor(null);
        metalLayer = (CAMetalLayer) getLayer();
        device = MTLDevice.getSystemDefaultDevice();
        metalLayer.setDevice(device);
        metalLayer.setPixelFormat(MTLPixelFormat.BGRA8Unorm);

        // this is the default but if we wanted to perform compute on the final
        // rendering layer we could set this to false
        metalLayer.setFramebufferOnly(true);
    }

    @Override
    public @Pointer long init(@ByVal CGRect frame) {
        long self = super.init(frame);
        if (self != 0) {
            initCommon();
        }
        return self;
    }

    @Override
    protected long init(NSCoder aDecoder) {
        long self = super.init(aDecoder);
        if (self != 0) {
            initCommon();
        }
        return self;
    }

    public void releaseTextures() {
        depthTex = null;
        stencilTex = null;
        msaaTex = null;
    }

    public void setupRenderPassDescriptor(MTLTexture texture) {
        // create lazily
        if (renderPassDescriptor == null) {
            renderPassDescriptor = new MTLRenderPassDescriptor();
        }

        // create a color attachment every frame since we have to recreate the
        // texture every frame
        MTLRenderPassColorAttachmentDescriptor colorAttachment = renderPassDescriptor.getColorAttachments().get(0);
        colorAttachment.setTexture(texture);

        // make sure to clear every frame for best performance
        colorAttachment.setLoadAction(MTLLoadAction.Clear);
        colorAttachment.setClearColor(new MTLClearColor(0.65, 0.65, 0.65, 1.0));

        // if sample count is greater than 1, render into using MSAA, then
        // resolve into our color texture
        if (sampleCount > 1) {
            boolean doUpdate = msaaTex != null
                    && ((msaaTex.getWidth() != texture.getWidth())
                            || (msaaTex.getHeight() != texture.getHeight())
                            || (msaaTex.getSampleCount() != sampleCount));

            if (msaaTex == null || (msaaTex != null && doUpdate)) {
                MTLTextureDescriptor desc = MTLTextureDescriptor.create2DDescriptor(MTLPixelFormat.BGRA8Unorm,
                        texture.getWidth(), texture.getHeight(), false);
                desc.setTextureType(MTLTextureType._2DMultisample);

                // sample count was specified to the view by the renderer.
                // this must match the sample count given to any pipeline state
                // using this render pass descriptor
                desc.setSampleCount(sampleCount);

                msaaTex = device.newTexture(desc);
            }

            // When multisampling, perform rendering to _msaaTex, then resolve
            // to 'texture' at the end of the scene
            colorAttachment.setTexture(msaaTex);
            colorAttachment.setResolveTexture(texture);

            // set store action to resolve in this case
            colorAttachment.setStoreAction(MTLStoreAction.MultisampleResolve);
        } else {
            // store only attachments that will be presented to the screen, as
            // in this case
            colorAttachment.setStoreAction(MTLStoreAction.Store);
        } // color0

        // Now create the depth and stencil attachments

        if (depthPixelFormat != MTLPixelFormat.Invalid) {
            boolean doUpdate = depthTex != null && ((depthTex.getWidth() != texture.getWidth())
                    || (depthTex.getHeight() != texture.getHeight())
                    || (depthTex.getSampleCount() != sampleCount));

            if (depthTex == null || doUpdate) {
                // If we need a depth texture and don't have one, or if the
                // depth texture we have is the wrong size
                // Then allocate one of the proper size
                MTLTextureDescriptor desc = MTLTextureDescriptor.create2DDescriptor(depthPixelFormat,
                        texture.getWidth(), texture.getHeight(), false);

                desc.setTextureType((sampleCount > 1) ? MTLTextureType._2DMultisample : MTLTextureType._2D);
                desc.setSampleCount(sampleCount);

                depthTex = device.newTexture(desc);

                MTLRenderPassDepthAttachmentDescriptor depthAttachment = renderPassDescriptor.getDepthAttachment();
                depthAttachment.setTexture(depthTex);
                depthAttachment.setLoadAction(MTLLoadAction.Clear);
                depthAttachment.setStoreAction(MTLStoreAction.DontCare);
                depthAttachment.setClearDepth(1.0);
            }
        } // depth

        if (stencilPixelFormat != MTLPixelFormat.Invalid) {
            boolean doUpdate = stencilTex != null && ((stencilTex.getWidth() != texture.getWidth())
                    || (stencilTex.getHeight() != texture.getHeight())
                    || (stencilTex.getSampleCount() != sampleCount));

            if (stencilTex == null || doUpdate) {
                // If we need a stencil texture and don't have one, or if the
                // depth texture we have is the wrong size
                // Then allocate one of the proper size
                MTLTextureDescriptor desc = MTLTextureDescriptor.create2DDescriptor(stencilPixelFormat,
                        texture.getWidth(), texture.getHeight(), false);

                desc.setTextureType((sampleCount > 1) ? MTLTextureType._2DMultisample : MTLTextureType._2D);
                desc.setSampleCount(sampleCount);

                stencilTex = device.newTexture(desc);

                MTLRenderPassStencilAttachmentDescriptor stencilAttachment = renderPassDescriptor
                        .getStencilAttachment();
                stencilAttachment.setTexture(stencilTex);
                stencilAttachment.setLoadAction(MTLLoadAction.Clear);
                stencilAttachment.setStoreAction(MTLStoreAction.DontCare);
                stencilAttachment.setClearStencil(0);
            }
        } // stencil
    }

    @Override
    public void didMoveToWindow() {
        setContentScaleFactor(getWindow().getScreen().getNativeScale());
    }

    public MTLRenderPassDescriptor renderPassDescriptor() {
        CAMetalDrawable drawable = getCurrentDrawable();
        if (drawable == null) {
            Foundation.log(">> ERROR: Failed to get a drawable!");
            renderPassDescriptor = null;
        } else {
            setupRenderPassDescriptor(drawable.getTexture());
        }

        return renderPassDescriptor;
    }

    public CAMetalDrawable getCurrentDrawable() {
        if (currentDrawable == null) {
            currentDrawable = metalLayer.nextDrawable();
        }
        return currentDrawable;
    }

    public void display() {
        // Create autorelease pool per frame to avoid possible deadlock
        // situations
        // because there are 3 CAMetalDrawables sitting in an autorelease pool.

        try (NSAutoreleasePool pool = new NSAutoreleasePool()) {
            // handle display changes here
            if (layerSizeDidUpdate) {
                // set the metal layer to the drawable size in case orientation
                // or size changes
                CGSize drawableSize = getBounds().getSize();
                drawableSize.setWidth(drawableSize.getWidth() * this.getContentScaleFactor());
                drawableSize.setHeight(drawableSize.getHeight() * this.getContentScaleFactor());

                metalLayer.setDrawableSize(drawableSize);

                // renderer delegate method so renderer can resize anything if
                // needed
                delegate.reshape(this);

                layerSizeDidUpdate = false;
            }

            // rendering delegate method to ask renderer to draw this frame's
            // content
            delegate.render(this);

            // do not retain current drawable beyond the frame.
            // There should be no strong references to this object outside of
            // this view class
            ((NSObject) currentDrawable).dispose();
            currentDrawable = null;
        }
    }

    @Override
    public void setContentScaleFactor(double contentScaleFactor) {
        super.setContentScaleFactor(contentScaleFactor);
        layerSizeDidUpdate = true;
    }

    @Override
    public void layoutSubviews() {
        super.layoutSubviews();
        layerSizeDidUpdate = true;
    }
}
