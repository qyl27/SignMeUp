package org.teacon.signmeup.hud.compat;

import net.irisshaders.iris.Iris;
import net.irisshaders.iris.uniforms.CapturedRenderingState;
import org.joml.Matrix4fc;
import org.teacon.signmeup.SignMeUp;

public class IrisAccess {
    static {
        if (!SignMeUp.IRIS_INSTALLED) {
            throw new AssertionError("Iris is not installed.");
        }
    }

    public static String getShaderPackName() {
        return Iris.getIrisConfig().getShaderPackName().orElse(null);
    }

    public static void setShaderPackName(String sp) {
        Iris.getIrisConfig().setShaderPackName(sp);
    }

    public static Matrix4fc getGBufferModelView() {
        return CapturedRenderingState.INSTANCE.getGbufferModelView();
    }

    public static void setGBufferModelView(Matrix4fc matrix4fc) {
        CapturedRenderingState.INSTANCE.setGbufferModelView(matrix4fc);
    }
}
