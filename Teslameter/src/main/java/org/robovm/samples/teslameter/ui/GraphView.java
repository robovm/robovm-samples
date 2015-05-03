/*
 * Copyright (C) 2013-2015 RoboVM AB
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
 * Portions of this code is based on Apple Inc's PhotoPicker sample (v2.0)
 * which is copyright (C) 2010-2013 Apple Inc.
 */
package org.robovm.samples.teslameter.ui;

import org.robovm.apple.coregraphics.CGContext;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.uikit.UIGraphics;
import org.robovm.apple.uikit.UIView;
import org.robovm.objc.annotation.CustomClass;

@CustomClass("GraphView")
public class GraphView extends UIView {
    private int nextIndex;
    private double[][] history;

    public void updateHistory(double x, double y, double z) {
        // Add to history.
        history[nextIndex][0] = x;
        history[nextIndex][1] = y;
        history[nextIndex][2] = z;

        // Advance the index counter.
        nextIndex = (nextIndex + 1) % 150;

        // Mark itself as needing to be redrawn.
        setNeedsDisplay();
    }

    private void drawGraph(CGContext context, CGRect bounds) {
        double value, temp;

        // Save any previous graphics state settings before setting the color
        // and line width for the current draw.
        context.saveGState();
        context.setLineWidth(1.0);

        // Draw the intermediate lines
        context.setGrayStrokeColor(0.6, 1.0);
        context.beginPath();
        for (value = -5 + 1.0; value <= 5 - 1.0; value += 1.0) {
            if (value == 0.0) {
                continue;
            }
            temp = 0.5 + Math.round(bounds.getOrigin().getY() + bounds.getSize().getHeight() / 2 + value / (2 * 5)
                    * bounds.getSize().getHeight());
            context.moveToPoint(bounds.getOrigin().getX(), temp);
            context.addLineToPoint(bounds.getOrigin().getX() + bounds.getSize().getWidth(), temp);
        }
        context.strokePath();

        // Draw the center line
        context.setGrayStrokeColor(0.25, 1.0);
        context.beginPath();
        temp = 0.5 + Math.round(bounds.getOrigin().getY() + bounds.getSize().getHeight() / 2);
        context.moveToPoint(bounds.getOrigin().getX(), temp);
        context.addLineToPoint(bounds.getOrigin().getX() + bounds.getSize().getWidth(), temp);
        context.strokePath();

        // Restore previous graphics state.
        context.restoreGState();
    }

    private void drawHistory(int axis, int index, CGContext context, CGRect bounds) {
        double value;

        context.beginPath();
        for (int counter = 0; counter < 150; ++counter) {
            // UIView referential has the Y axis going down, so we need to draw
            // upside-down.
            value = history[(index + counter) % 150][axis] / -128;
            if (counter > 0) {
                context.addLineToPoint(bounds.getOrigin().getX() + (float) counter / (float) (150 - 1)
                        * bounds.getSize().getWidth(), bounds.getOrigin().getY() + bounds.getSize().getHeight() / 2
                        + value
                        * bounds.getSize().getHeight() / 2);
            } else {
                context.moveToPoint(bounds.getOrigin().getX() + (float) counter / (float) (150 - 1)
                        * bounds.getSize().getWidth(),
                        bounds.getOrigin().getY() + bounds.getSize().getHeight() / 2 + value
                                * bounds.getSize().getHeight() / 2);
            }
        }
        // Save any previous graphics state settings before setting the color
        // and line width for the current draw.
        context.saveGState();
        context.setRGBStrokeColor((axis == 0 ? 1.0 : 0.0), (axis == 1 ? 1.0 : 0.0), (axis == 2 ? 1.0 : 0.0), 1.0);
        context.setLineWidth(2.0);
        context.strokePath();
        // Restore previous graphics state.
        context.restoreGState();
    }

    @Override
    public void draw(CGRect rect) {
        int index = nextIndex;

        if (history == null) {
            // TODO move to constructor when #894 is fixed
            history = new double[150][3];
        }

        CGContext context = UIGraphics.getCurrentContext();
        CGRect bounds = new CGRect(0, 0, getBounds().getSize().getWidth(), getBounds().getSize().getHeight());

        // create the graph
        drawGraph(context, bounds);

        // plot x,y,z with anti-aliasing turned off
        context.setAllowsAntialiasing(false);
        for (int i = 0; i < 3; ++i) {
            drawHistory(i, index, context, bounds);
        }
        context.setAllowsAntialiasing(true);
    }
}
