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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.FloatBuffer;
import static org.robovm.samples.hellogl.GLES2.*;

public class GLUtil {

    public static int createShaderProgram (String vertSource, String fragSource) {
        // NOTE: the error handling here is crap; a real program would keep track of all the GL
        // resources it created and free them as appropriate; don't copy and paste this code
        int vertShader = createShader(GL_VERTEX_SHADER, vertSource);
        int fragShader = createShader(GL_FRAGMENT_SHADER, fragSource);

        int program = glCreateProgram();
        if (program == 0) throw new RuntimeException("glCreateProgram failed");

        glAttachShader(program, vertShader);
        glAttachShader(program, fragShader);
        glLinkProgram(program);

        int linked = glGetProgrami(program, GL_LINK_STATUS);
        if (linked == 0) throw new RuntimeException(
            "glLinkProgram failed: " + glGetProgramInfoLog(program));
        return program;
    }

    public static ByteBuffer newBuffer (int size) {
        ByteBuffer buffer = ByteBuffer.allocateDirect(size);
        buffer.order(ByteOrder.nativeOrder());
        return buffer;
    }
    public static IntBuffer newIntBuffer (int numInts) {
        return newBuffer(numInts * 4).asIntBuffer();
    }
    public static FloatBuffer newFloatBuffer (int numFloats) {
        return newBuffer(numFloats * 4).asFloatBuffer();
    }

    private static int createShader (int type, String source) {
        int shader = glCreateShader(type);
        if (shader == 0) throw new RuntimeException("glCreateShader failed");
        glShaderSource(shader, source);
        glCompileShader(shader);
        int compiled = glGetShaderi(shader, GL_COMPILE_STATUS);
        if (compiled == 0) throw new RuntimeException(
            "glCompileShader failed: " + glGetShaderInfoLog(shader));
        return shader;
    }
}
