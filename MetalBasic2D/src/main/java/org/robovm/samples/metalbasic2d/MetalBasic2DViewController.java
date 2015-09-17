package org.robovm.samples.metalbasic2d;

import org.robovm.apple.coreanimation.CADisplayLink;
import org.robovm.apple.coreanimation.CAMetalDrawable;
import org.robovm.apple.coreanimation.CAMetalLayer;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.foundation.*;
import org.robovm.apple.metal.*;
import org.robovm.apple.uikit.*;
import org.robovm.objc.Selector;
import org.robovm.objc.annotation.Method;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class MetalBasic2DViewController extends UIViewController {
    float quadVertexData[] = {
            0.5f, -0.5f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f,
            -0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f,
            -0.5f, 0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f,

            0.5f, 0.5f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f,
            0.5f, -0.5f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f,
            -0.5f, 0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f,
    };

    CAMetalLayer metalLayer;
    MTLDevice device;
    MTLCommandQueue commandQueue;
    MTLLibrary defaultLibrary;
    MTLRenderPipelineState pipelineState;
    MTLBuffer uniformBuffer;
    MTLBuffer vertexBuffer;
    CAMetalDrawable currentDrawable;
    CADisplayLink timer;

    boolean layerSizeDidUpdate;
    float rotationAngle;

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();
        getView().setBackgroundColor(UIColor.white());

        setupMetal();
        buildPipeline();

        timer = UIScreen.getMainScreen().getDisplayLink(this, Selector.register("redraw"));
        timer.addToRunLoop(NSRunLoop.getMain(), NSRunLoopMode.Default);
    }

    private void setupMetal() {
        device = MTLDevice.getSystemDefaultDevice();
        metalLayer = new CAMetalLayer();
        metalLayer.setDevice(device);
        metalLayer.setPixelFormat(MTLPixelFormat.BGRA8Unorm);
        metalLayer.setFrame(getView().getBounds());
        getView().getLayer().addSublayer(metalLayer);

        commandQueue = device.newCommandQueue();
        defaultLibrary = device.newDefaultLibrary();
        getView().setContentScaleFactor(UIScreen.getMainScreen().getScale());
    }

    private void buildPipeline() {
        vertexBuffer = device.newBuffer(quadVertexData.length * 4, MTLResourceOptions.CPUCacheModeDefaultCache);
        ByteBuffer buffer = vertexBuffer.getContents();
        buffer.order(ByteOrder.nativeOrder());
        buffer.asFloatBuffer().put(quadVertexData);

        uniformBuffer = device.newBuffer(16 * 4, MTLResourceOptions.CPUCacheModeDefaultCache);

        MTLFunction vertexProgram = defaultLibrary.newFunction("vertex_function");
        MTLFunction fragmentProgram = defaultLibrary.newFunction("fragment_function");

        MTLRenderPipelineDescriptor pipelineDescriptor = new MTLRenderPipelineDescriptor();
        pipelineDescriptor.setVertexFunction(vertexProgram);
        pipelineDescriptor.setFragmentFunction(fragmentProgram);
        pipelineDescriptor.getColorAttachments().get(0).setPixelFormat(MTLPixelFormat.BGRA8Unorm);

        try {
            pipelineState = device.newRenderPipelineState(pipelineDescriptor);
        } catch (NSErrorException e) {
            throw new Error("Couldn't create render pipeline state, " + e.getMessage());
        }
    }

    private MTLRenderPassDescriptor renderPassDescriptor(MTLTexture texture) {
        MTLRenderPassDescriptor renderPassDescriptor = new MTLRenderPassDescriptor();
        renderPassDescriptor.getColorAttachments().get(0).setTexture(texture);
        renderPassDescriptor.getColorAttachments().get(0).setLoadAction(MTLLoadAction.Clear);
        renderPassDescriptor.getColorAttachments().get(0).setClearColor(new MTLClearColor(1, 1, 1, 1));
        renderPassDescriptor.getColorAttachments().get(0).setStoreAction(MTLStoreAction.Store);
        return renderPassDescriptor;
    }

    private void render() {
        update();

        MTLCommandBuffer commandBuffer = commandQueue.getCommandBuffer();
        CAMetalDrawable drawable = getCurrentDrawable();

        if (drawable != null) {
            MTLRenderPassDescriptor renderPassDescriptor = renderPassDescriptor(drawable.getTexture());
            MTLRenderCommandEncoder renderEncoder = commandBuffer.newRenderCommandEncoder(renderPassDescriptor);

            renderEncoder.setRenderPipelineState(pipelineState);
            renderEncoder.setVertexBuffer(vertexBuffer, 0, 0);
            renderEncoder.setVertexBuffer(uniformBuffer, 0, 1);
            renderEncoder.drawPrimitives(MTLPrimitiveType.Triangle, 0, 6);
            renderEncoder.endEncoding();

            commandBuffer.presentDrawable(drawable);
            commandBuffer.commit();
        }
    }

    private void update() {
        float cos = (float) Math.cos(rotationAngle);
        float sin = (float) Math.sin(rotationAngle);
        float matrix[] = {
                cos, sin, 0, 0,
                -sin, cos, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
        };
        ByteBuffer buffer = uniformBuffer.getContents();
        buffer.order(ByteOrder.nativeOrder());
        buffer.asFloatBuffer().put(matrix);

        rotationAngle += 0.01;
    }

    @Method
    private void redraw() {
        try (NSAutoreleasePool pool = new NSAutoreleasePool()) {
            if (layerSizeDidUpdate) {
                double nativeScale = getView().getWindow().getScreen().getNativeScale();
                CGSize drawableSize = metalLayer.getBounds().getSize();
                drawableSize.setWidth(drawableSize.getWidth() * nativeScale);
                drawableSize.setHeight(drawableSize.getHeight() * nativeScale);
                metalLayer.setDrawableSize(drawableSize);
                layerSizeDidUpdate = false;
            }

            render();
            ((NSObject) currentDrawable).dispose();
            currentDrawable = null;
        }
    }

    @Override
    public void viewDidLayoutSubviews() {
        layerSizeDidUpdate = true;

        CGSize parentSize = getView().getBounds().getSize();
        float minSize = (float) Math.min(parentSize.getWidth(), parentSize.getHeight());
        CGRect frame = new CGRect((parentSize.getWidth() - minSize) / 2,
                (parentSize.getHeight() - minSize) / 2,
                minSize,
                minSize);
        metalLayer.setFrame(frame);
    }

    private CAMetalDrawable getCurrentDrawable() {
        if (currentDrawable == null) {
            currentDrawable = metalLayer.nextDrawable();
        }
        return currentDrawable;
    }
}
