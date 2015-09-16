/*
 * Copyright (C) 2015 Trillian Mobile AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.robovm.samples.metalbasic3d;

import java.util.Arrays;

/**
 * 
 */
public class Matrix4 {
    private float[][] data = new float[4][4];

    public Matrix4 identity() {
        Arrays.fill(data[0], 0.0f);
        Arrays.fill(data[1], 0.0f);
        Arrays.fill(data[2], 0.0f);
        Arrays.fill(data[3], 0.0f);
        data[0][0] = 1.0f;
        data[1][1] = 1.0f;
        data[2][2] = 1.0f;
        data[3][3] = 1.0f;
        return this;
    }
    
    public Matrix4 translate(float x, float y, float z) {
        data[3][0] = x;
        data[3][1] = y;
        data[3][2] = z;
        return this;
    }
}
