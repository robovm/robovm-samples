/*
 * Copyright (C) 2014 RoboVM AB
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
 */

package org.robovm.samples.hellogl;

import java.nio.FloatBuffer;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.glkit.GLKView;
import org.robovm.apple.glkit.GLKViewController;
import org.robovm.apple.glkit.GLKViewControllerDelegate;
import org.robovm.apple.opengles.EAGLContext;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIInterfaceOrientation;
import org.robovm.apple.uikit.UIInterfaceOrientationMask;
import org.robovm.objc.Selector;
import org.robovm.objc.annotation.BindSelector;
import org.robovm.rt.bro.annotation.Callback;

import static org.robovm.samples.hellogl.GLES2.*;

public class HelloViewController extends GLKViewController implements GLKViewControllerDelegate {

    private final EAGLContext ctx;
    private final int glProgram;
    private final int resLoc, colorLoc;
    private float hue = 0;

    public HelloViewController (EAGLContext ctx, CGRect bounds) {
        this.ctx = ctx;

        GLKView view = new GLKView(bounds, ctx) {
            @Override
            public void draw (CGRect rect) {
                // fill in a white background
                glClearColor(1, 1, 1, 1);
                glClear(GL_COLOR_BUFFER_BIT);

                // draw our triangles on top of that
                glUseProgram(glProgram);
                int rgb = HSBtoRGB(hue, 0.75f, 0.75f);
                glUniform4f(colorLoc,
                            ((rgb & 0xFF0000) >> 16)/255f,
                            ((rgb & 0x00FF00) >>  8)/255f,
                            ((rgb & 0x0000FF) >>  0)/255f,
                            1);
                glDrawArrays(GL_TRIANGLES, 0, 6);
            }
        };
        view.setMultipleTouchEnabled(true);
        view.setBackgroundColor(UIColor.yellow());
        setView(view);

        setDelegate(this);
        setPreferredFramesPerSecond(60); // TODO

        // compile our shaders, set up our vertex buffer
        makeCurrent();
        String vertShader =
            "attribute vec2 a_position;\n" +
            "uniform vec2 u_resolution;\n" +
            "uniform vec4 u_color;\n" +
            "varying lowp vec4 v_color;\n" +
            "void main() {\n" +
            "  // convert the rectangle from pixels to 0.0 to 1.0\n" +
            "  vec2 zeroToOne = a_position / u_resolution;\n" +
            "  // convert from 0->1 to 0->2\n" +
            "  vec2 zeroToTwo = zeroToOne * 2.0;\n" +
            "  // convert from 0->2 to -1->+1 (clipspace)\n" +
            "  vec2 clipSpace = zeroToTwo - 1.0;\n" +
            "  gl_Position = vec4(clipSpace * vec2(1, -1), 0, 1);\n" +
            "  // just copy the color uniform into a varying\n" +
            "  v_color = u_color;\n" +
            "}";
        String fragShader =
            "varying lowp vec4 v_color;\n" +
            "void main() {\n" +
            "  gl_FragColor = v_color;\n" +
            "}";
        glProgram = GLUtil.createShaderProgram(vertShader, fragShader);
        glUseProgram(glProgram);

        // bind the current screen size to an attribute so that our shader operates on pixels
        // rather than in clip space
        resLoc = glGetUniformLocation(glProgram, "u_resolution");
        glUniform2f(resLoc, (float)bounds.getWidth(), (float)bounds.getHeight());

        // note our color uniform, which we'll set in draw()
        colorLoc = glGetUniformLocation(glProgram, "u_color");

        // create a buffer with two triangles arranged to make a rectangle
        int buffer = glGenBuffer();
        glBindBuffer(GL_ARRAY_BUFFER, buffer);
        FloatBuffer data = GLUtil.newFloatBuffer(12);
        float t = 100, l = 50, b = 200, r = 200;
        data.put(new float[] {
            l, t,
            r, t,
            l, b,
            l, b,
            r, t,
            r, b });
        glBufferData(GL_ARRAY_BUFFER, 12*4, data, GL_STATIC_DRAW);

        // configre a_position as our vertex array
        int posLoc = glGetAttribLocation(glProgram, "a_position");
        glEnableVertexAttribArray(posLoc);
        glVertexAttribPointer(posLoc, 2, GL_FLOAT, false, 0, 0);
    }

    @Override // from GLKViewControllerDelegate
    public void update (GLKViewController self) {
        hue += 0.01f;
        if (hue > 1) hue -= 1;
    }

    @Override // from GLKViewControllerDelegate
    public void willPause (GLKViewController self, boolean paused) {
        System.out.println("willPause(" + paused + ")");
    }

    @Override // from ViewController
    public void viewWillAppear (boolean animated) {
        super.viewWillAppear(animated);
        System.out.println("viewWillAppear(" + animated + ")");
    }

    @Override // from ViewController
    public void viewDidAppear (boolean animated) {
        super.viewDidAppear(animated);
        System.out.println("viewDidAppear(" + animated + ")");
    }

    @Override // from ViewController
    public void didRotate (UIInterfaceOrientation orientation) {
        super.didRotate(orientation);
        CGRect bounds = getView().getBounds();
        System.out.println("didRotate(" + orientation + "): " + bounds);
        glUniform2f(resLoc, (float)bounds.getWidth(), (float)bounds.getHeight());
    }

    @Override // from ViewController
    public UIInterfaceOrientationMask getSupportedInterfaceOrientations () {
        return UIInterfaceOrientationMask.All;
    }

    @Override // from ViewController
    public boolean shouldAutorotate () {
        return true;
    }

    public boolean shouldAutorotateToInterfaceOrientation (UIInterfaceOrientation orientation) {
        return true;
    }

    void makeCurrent () {
        System.out.println("makeCurrent");
        EAGLContext.setCurrentContext(ctx);
    }

    @Callback @BindSelector("shouldAutorotateToInterfaceOrientation:")
    private static boolean shouldAutorotateToInterfaceOrientation (
        HelloViewController self, Selector sel, UIInterfaceOrientation orientation) {
        return self.shouldAutorotateToInterfaceOrientation(orientation);
    }

    private static int HSBtoRGB(float hue, float saturation, float brightness) {
        int r = 0, g = 0, b = 0;
        if (saturation == 0) {
            r = g = b = (int) (brightness * 255.0f + 0.5f);
        } else {
            float h = (hue - (float)Math.floor(hue)) * 6.0f;
            float f = h - (float)java.lang.Math.floor(h);
            float p = brightness * (1.0f - saturation);
            float q = brightness * (1.0f - saturation * f);
            float t = brightness * (1.0f - (saturation * (1.0f - f)));
            switch ((int) h) {
            case 0:
                r = (int) (brightness * 255.0f + 0.5f);
                g = (int) (t * 255.0f + 0.5f);
                b = (int) (p * 255.0f + 0.5f);
                break;
            case 1:
                r = (int) (q * 255.0f + 0.5f);
                g = (int) (brightness * 255.0f + 0.5f);
                b = (int) (p * 255.0f + 0.5f);
                break;
            case 2:
                r = (int) (p * 255.0f + 0.5f);
                g = (int) (brightness * 255.0f + 0.5f);
                b = (int) (t * 255.0f + 0.5f);
                break;
            case 3:
                r = (int) (p * 255.0f + 0.5f);
                g = (int) (q * 255.0f + 0.5f);
                b = (int) (brightness * 255.0f + 0.5f);
                break;
            case 4:
                r = (int) (t * 255.0f + 0.5f);
                g = (int) (p * 255.0f + 0.5f);
                b = (int) (brightness * 255.0f + 0.5f);
                break;
            case 5:
                r = (int) (brightness * 255.0f + 0.5f);
                g = (int) (p * 255.0f + 0.5f);
                b = (int) (q * 255.0f + 0.5f);
                break;
            }
        }
        return 0xff000000 | (r << 16) | (g << 8) | (b << 0);
    }
}
