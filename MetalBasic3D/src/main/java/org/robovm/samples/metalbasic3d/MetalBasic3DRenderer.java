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

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.Semaphore;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.BufferUtils;
import org.robovm.apple.foundation.NSErrorException;
import org.robovm.apple.metal.MTLBuffer;
import org.robovm.apple.metal.MTLCommandBuffer;
import org.robovm.apple.metal.MTLCommandQueue;
import org.robovm.apple.metal.MTLCompareFunction;
import org.robovm.apple.metal.MTLDepthStencilDescriptor;
import org.robovm.apple.metal.MTLDepthStencilState;
import org.robovm.apple.metal.MTLDevice;
import org.robovm.apple.metal.MTLFunction;
import org.robovm.apple.metal.MTLLibrary;
import org.robovm.apple.metal.MTLPixelFormat;
import org.robovm.apple.metal.MTLPrimitiveType;
import org.robovm.apple.metal.MTLRenderCommandEncoder;
import org.robovm.apple.metal.MTLRenderPassDescriptor;
import org.robovm.apple.metal.MTLRenderPipelineDescriptor;
import org.robovm.apple.metal.MTLRenderPipelineState;
import org.robovm.apple.metal.MTLResourceOptions;


public class MetalBasic3DRenderer implements MetalBasic3DViewControllerDelegate, MetalBasic3DViewDelegate {
    private static final int kInFlightCommandBuffers = 3;
    private static final int kNumberOfBoxes = 2;

    private static final float[][] kBoxAmbientColors = {
            {0.18f, 0.24f, 0.8f, 1.0f},
            {0.8f, 0.24f, 0.1f, 1.0f}
    };

    private static final float[][] kBoxDiffuseColors = {
            {0.4f, 0.4f, 1.0f, 1.0f},
            {0.8f, 0.4f, 0.4f, 1.0f}
    };

    private static final float kFOVY    = 65.0f;
    private static final Vector3 kEye    = new Vector3(0, 0, 0);
    private static final Vector3 kCenter = new Vector3(0, 0, 1);
    private static final Vector3 kUp     = new Vector3(0, 1, 0);

    private static final float kWidth  = 0.75f;
    private static final float kHeight = 0.75f;
    private static final float kDepth  = 0.75f;
    
    private static final float[] kCubeVertexData = {
        kWidth, -kHeight, kDepth,   0.0f, -1.0f,  0.0f,
        -kWidth, -kHeight, kDepth,   0.0f, -1.0f, 0.0f,
        -kWidth, -kHeight, -kDepth,   0.0f, -1.0f,  0.0f,
        kWidth, -kHeight, -kDepth,  0.0f, -1.0f,  0.0f,
        kWidth, -kHeight, kDepth,   0.0f, -1.0f,  0.0f,
        -kWidth, -kHeight, -kDepth,   0.0f, -1.0f,  0.0f,
        
        kWidth, kHeight, kDepth,    1.0f, 0.0f,  0.0f,
        kWidth, -kHeight, kDepth,   1.0f,  0.0f,  0.0f,
        kWidth, -kHeight, -kDepth,  1.0f,  0.0f,  0.0f,
        kWidth, kHeight, -kDepth,   1.0f, 0.0f,  0.0f,
        kWidth, kHeight, kDepth,    1.0f, 0.0f,  0.0f,
        kWidth, -kHeight, -kDepth,  1.0f,  0.0f,  0.0f,
        
        -kWidth, kHeight, kDepth,    0.0f, 1.0f,  0.0f,
        kWidth, kHeight, kDepth,    0.0f, 1.0f,  0.0f,
        kWidth, kHeight, -kDepth,   0.0f, 1.0f,  0.0f,
        -kWidth, kHeight, -kDepth,   0.0f, 1.0f,  0.0f,
        -kWidth, kHeight, kDepth,    0.0f, 1.0f,  0.0f,
        kWidth, kHeight, -kDepth,   0.0f, 1.0f,  0.0f,
        
        -kWidth, -kHeight, kDepth,  -1.0f,  0.0f, 0.0f,
        -kWidth, kHeight, kDepth,   -1.0f, 0.0f,  0.0f,
        -kWidth, kHeight, -kDepth,  -1.0f, 0.0f,  0.0f,
        -kWidth, -kHeight, -kDepth,  -1.0f,  0.0f,  0.0f,
        -kWidth, -kHeight, kDepth,  -1.0f,  0.0f, 0.0f,
        -kWidth, kHeight, -kDepth,  -1.0f, 0.0f,  0.0f,
        
        kWidth, kHeight,  kDepth,  0.0f, 0.0f,  1.0f,
        -kWidth, kHeight,  kDepth,  0.0f, 0.0f,  1.0f,
        -kWidth, -kHeight, kDepth,   0.0f,  0.0f, 1.0f,
        -kWidth, -kHeight, kDepth,   0.0f,  0.0f, 1.0f,
        kWidth, -kHeight, kDepth,   0.0f,  0.0f,  1.0f,
        kWidth, kHeight,  kDepth,  0.0f, 0.0f,  1.0f,
        
        kWidth, -kHeight, -kDepth,  0.0f,  0.0f, -1.0f,
        -kWidth, -kHeight, -kDepth,   0.0f,  0.0f, -1.0f,
        -kWidth, kHeight, -kDepth,  0.0f, 0.0f, -1.0f,
        kWidth, kHeight, -kDepth,  0.0f, 0.0f, -1.0f,
        kWidth, -kHeight, -kDepth,  0.0f,  0.0f, -1.0f,
        -kWidth, kHeight, -kDepth,  0.0f, 0.0f, -1.0f
    };

    private Semaphore inflightSemaphore = new Semaphore(kInFlightCommandBuffers);
    private MTLBuffer[] dynamicConstantBuffer = new MTLBuffer[kInFlightCommandBuffers];

    private MTLDevice device;
    private MTLCommandQueue commandQueue;
    private MTLLibrary defaultLibrary;
    private MTLRenderPipelineState pipelineState;
    private MTLBuffer vertexBuffer;
    private MTLDepthStencilState depthState;

    // globals used in update calculation
    Matrix4 projectionMatrix = new Matrix4();
    Matrix4 viewMatrix = new Matrix4();
    private float rotation;

    private static final int sizeOfConstantT = 175;
    private long maxBufferBytesPerFrame = sizeOfConstantT * kNumberOfBoxes;
    private int constantDataBufferIndex = 0;
    private int float4x4Size = 4 * 4 * 4;
    private int float4Size = 4 * 4;
    private int modelViewProjectionMatrixOffset = 0;
    private int normalMatrixOffset = float4x4Size;
    private int ambientColorOffset = float4x4Size * 2;
    private int diffuseColorOffset = float4x4Size * 2 + float4Size;
    private int multiplierOffset = float4x4Size * 2 + float4Size * 2;

    public void configure(MetalBasic3DView view) {
        // find a usable Device
        device = view.getDevice();
        
        // setup view with drawable formats
        view.setDepthPixelFormat(MTLPixelFormat.Depth32Float);
        view.setStencilPixelFormat(MTLPixelFormat.Invalid);
        view.setSampleCount(1);
        
        // create a new command queue
        commandQueue = device.newCommandQueue();
        
        defaultLibrary = device.newDefaultLibrary();
        if (defaultLibrary == null) {
            // If the shader libary isn't loading, nothing good will happen
            throw new Error(">> ERROR: Couldnt create a default shader library");
        }
        
        preparePipelineState(view);
        
        MTLDepthStencilDescriptor depthStateDesc = new MTLDepthStencilDescriptor();
        depthStateDesc.setDepthCompareFunction(MTLCompareFunction.Less);
        depthStateDesc.setDepthWriteEnabled(true);
        depthState = device.newDepthStencilState(depthStateDesc);
        
        // allocate a number of buffers in memory that matches the sempahore count so that
        // we always have one self contained memory buffer for each buffered frame.
        // In this case triple buffering is the optimal way to go so we cycle through 3 memory buffers
        for (int i = 0; i < kInFlightCommandBuffers; i++) {
            dynamicConstantBuffer[i] = device.newBuffer(maxBufferBytesPerFrame, MTLResourceOptions.CPUCacheModeDefaultCache);
            dynamicConstantBuffer[i].setLabel("ConstantBuffer" + i);
            
            // write initial color values for both cubes (at each offset).
            // Note, these will get animated during update
            ByteBuffer constant_buffer = dynamicConstantBuffer[i].getContents();
            constant_buffer.order(ByteOrder.nativeOrder());

            for (int j = 0; j < kNumberOfBoxes; j++) {
                if (j % 2 == 0) {
                    constant_buffer.position((sizeOfConstantT * j) + ambientColorOffset);
                    BufferUtils.copy(kBoxAmbientColors[0], 0, 4, constant_buffer);
                    constant_buffer.position((sizeOfConstantT * j) + diffuseColorOffset);
                    BufferUtils.copy(kBoxDiffuseColors[0], 0, 4, constant_buffer);
                    constant_buffer.putInt((sizeOfConstantT * j) + multiplierOffset, 1);
                } else {
                    constant_buffer.position((sizeOfConstantT * j) + ambientColorOffset);
                    BufferUtils.copy(kBoxAmbientColors[1], 0, 4, constant_buffer);
                    constant_buffer.position((sizeOfConstantT * j) + diffuseColorOffset);
                    BufferUtils.copy(kBoxDiffuseColors[1], 0, 4, constant_buffer);
                    constant_buffer.putInt((sizeOfConstantT * j) + multiplierOffset, -1);
                }
            }
        }
    }
    
    private void preparePipelineState(MetalBasic3DView view) {
        // get the fragment function from the library
        MTLFunction fragmentProgram = defaultLibrary.newFunction("lighting_fragment");
        if (fragmentProgram == null) {
            throw new Error(">> ERROR: Couldn't load fragment function from default library");
        }
        
        // get the vertex function from the library
        MTLFunction vertexProgram = defaultLibrary.newFunction("lighting_vertex");
        if (vertexProgram == null) {
            throw new Error(">> ERROR: Couldn't load vertex function from default library");
        }
        
        // setup the vertex buffers;
        vertexBuffer = device.newBuffer(kCubeVertexData.length * 4, MTLResourceOptions.CPUCacheModeDefaultCache);
        vertexBuffer.setLabel("Vertices");
        ByteBuffer b = vertexBuffer.getContents();
        b.order(ByteOrder.nativeOrder());
        b.asFloatBuffer().put(kCubeVertexData);
        
        // create a pipeline state descriptor which can be used to create a compiled pipeline state object
        MTLRenderPipelineDescriptor pipelineStateDescriptor = new MTLRenderPipelineDescriptor();
        
        pipelineStateDescriptor.setLabel("MyPipeline");
        pipelineStateDescriptor.setSampleCount(view.getSampleCount());
        pipelineStateDescriptor.setVertexFunction(vertexProgram);
        pipelineStateDescriptor.setFragmentFunction(fragmentProgram);
        pipelineStateDescriptor.getColorAttachments().get(0).setPixelFormat(MTLPixelFormat.BGRA8Unorm);
        pipelineStateDescriptor.setDepthAttachmentPixelFormat(view.getDepthPixelFormat());
        
        // create a compiled pipeline state object. Shader functions (from the render pipeline descriptor)
        // are compiled when this is created unlessed they are obtained from the device's cache
        try {
            pipelineState = device.newRenderPipelineState(pipelineStateDescriptor);
        } catch (NSErrorException e) {
            throw new Error(">> ERROR: Failed Aquiring pipeline state: ", e);
        }
    }

    @Override
    public void render(MetalBasic3DView view) {
        // Allow the renderer to preflight 3 frames on the CPU (using a semapore as a guard) and commit them to the GPU.
        // This semaphore will get signaled once the GPU completes a frame's work via addCompletedHandler callback below,
        // signifying the CPU can go ahead and prepare another frame.
        try {
            inflightSemaphore.acquire();
        } catch (InterruptedException e) {
            throw new Error(e);
        }
        
        // Prior to sending any data to the GPU, constant buffers should be updated accordingly on the CPU.
        updateConstantBuffer();
        
        // create a new command buffer for each renderpass to the current drawable
        MTLCommandBuffer commandBuffer = commandQueue.getCommandBuffer();
        
        // create a render command encoder so we can render into something
        MTLRenderPassDescriptor renderPassDescriptor = view.renderPassDescriptor();
        if (renderPassDescriptor != null) {
            MTLRenderCommandEncoder renderEncoder = commandBuffer.newRenderCommandEncoder(renderPassDescriptor);
            renderEncoder.pushDebugGroup("Boxes");
            renderEncoder.setDepthStencilState(depthState);
            renderEncoder.setRenderPipelineState(pipelineState);
            renderEncoder.setVertexBuffer(vertexBuffer, 0, 0);
            
            for (int i = 0; i < kNumberOfBoxes; i++) {
                //  set constant buffer for each box
                renderEncoder.setVertexBuffer(dynamicConstantBuffer[constantDataBufferIndex], i * sizeOfConstantT, 1);
                
                // tell the render context we want to draw our primitives
                renderEncoder.drawPrimitives(MTLPrimitiveType.Triangle, 0, 36);
            }
            
            renderEncoder.endEncoding();
            renderEncoder.popDebugGroup();
            
            // schedule a present once rendering to the framebuffer is complete
            commandBuffer.presentDrawable(view.getCurrentDrawable());
        }
        
        // call the view's completion handler which is required by the view since it will signal its semaphore and set up the next buffer
        commandBuffer.addCompletedHandler((buffer) -> {
            // GPU has completed rendering the frame and is done using the contents of any buffers previously encoded on the CPU for that frame.
            // Signal the semaphore and allow the CPU to proceed and construct the next frame.
            synchronized (inflightSemaphore) {
                inflightSemaphore.release();
            }
        });
        
        // finalize rendering here. this will push the command buffer to the GPU
        commandBuffer.commit();
        
        // This index represents the current portion of the ring buffer being used for a given frame's constant buffer updates.
        // Once the CPU has completed updating a shared CPU/GPU memory buffer region for a frame, this index should be updated so the
        // next portion of the ring buffer can be written by the CPU. Note, this should only be done *after* all writes to any
        // buffers requiring synchronization for a given frame is done in order to avoid writing a region of the ring buffer that the GPU may be reading.
        constantDataBufferIndex = (constantDataBufferIndex + 1) % kInFlightCommandBuffers;
    }

    @Override
    public void reshape(MetalBasic3DView view) {
        // when reshape is called, update the view and projection matricies since this means the view orientation or size changed
        float aspect = (float) Math.abs(view.getBounds().getSize().getWidth() / view.getBounds().getSize().getHeight());
        projectionMatrix.setToProjection(0.1f, 100.0f, kFOVY, aspect);
        viewMatrix.setToLookAt(kEye, kCenter, kUp);
    }

    // scratch matrices to avoid GC
    Matrix4 baseModelViewMatrix = new Matrix4();
    Matrix4 modelViewMatrix = new Matrix4();
    Matrix4 normalMatrix = new Matrix4();
    Matrix4 combinedMatrix = new Matrix4();

    private void updateConstantBuffer() {
        baseModelViewMatrix.set(viewMatrix).translate(0.0f,  0.0f,  5f).rotate(1, 1, 1, rotation);
        ByteBuffer constant_buffer = dynamicConstantBuffer[constantDataBufferIndex].getContents();
        constant_buffer.order(ByteOrder.nativeOrder());
        for (int i = 0; i < kNumberOfBoxes; i++) {
            int multiplier = ((i % 2) == 0?1: -1);
            modelViewMatrix.set(baseModelViewMatrix).translate(0, 0, multiplier * 1.5f).rotate(1, 1, 1, rotation);

            normalMatrix.set(modelViewMatrix).tra().inv();
            constant_buffer.position((sizeOfConstantT *i) + normalMatrixOffset);
            BufferUtils.copy(normalMatrix.getValues(), 0, 16, constant_buffer);

            combinedMatrix.set(projectionMatrix).mul(modelViewMatrix);
            constant_buffer.position((sizeOfConstantT * i) + modelViewProjectionMatrixOffset);
            BufferUtils.copy(combinedMatrix.getValues(), 0, 16, constant_buffer);
        }
        rotation += 0.01;
    }
    
    @Override
    public void update(MetalBasic3DViewController controller) {
        rotation += controller.getTimeSinceLastDraw() * 50.0f;
    }

    @Override
    public void willPause(MetalBasic3DViewController controller, boolean pause) {
        // timer is suspended/resumed
        // Can do any non-rendering related background work here when suspended
    }
}
