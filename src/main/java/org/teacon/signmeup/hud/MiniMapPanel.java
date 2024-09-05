package org.teacon.signmeup.hud;

import cn.ussshenzhou.t88.config.ConfigHelper;
import cn.ussshenzhou.t88.gui.widegt.TPanel;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.teacon.signmeup.config.MiniMap;

import static org.lwjgl.opengl.GL40C.*;

/**
 * @author USS_Shenzhou
 */
public class MiniMapPanel extends TPanel {
    public static final ObjectArrayList<SectionRenderDispatcher.RenderSection> VISIBLE_SECTIONS = new ObjectArrayList<>(10000);
    private static int minimapSize, FBO, COLOR, DEPTH;
    private static final Matrix4f MODEL_VIEW_MATRIX = new Matrix4f();
    private static final Matrix4f PROJECTION_MATRIX_IDENT = new Matrix4f();
    private static final Quaternionf QUATERNION = new Quaternionf();
    private static final float PI = (float) Math.PI;

    static {
        var window = Minecraft.getInstance().getWindow();
        minimapSize = (int) (window.getHeight() * 0.236);
        initOpenGL();
    }

    @SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
    private static void initOpenGL() {
        FBO = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, FBO);
        COLOR = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, COLOR);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, minimapSize, minimapSize, 0, GL_RGBA, GL_UNSIGNED_BYTE, 0);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, COLOR, 0);
        DEPTH = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, DEPTH);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, minimapSize, minimapSize);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, DEPTH);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glBindRenderbuffer(GL_RENDERBUFFER, 0);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public MiniMapPanel() {
    }

    @Override
    public void resizeAsHud(int screenWidth, int screenHeight) {
        this.setAbsBounds(0, 0, screenWidth, screenHeight);
        var window = Minecraft.getInstance().getWindow();
        minimapSize = (int) (window.getHeight() * 0.236);
        initOpenGL();
        super.resizeAsHud(screenWidth, screenHeight);
    }

    @Override
    public void render(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(graphics, pMouseX, pMouseY, pPartialTick);
        renderMap(graphics);
    }

    private void renderMap(GuiGraphics graphics) {
        var minecraft = Minecraft.getInstance();
        var pos = prepare(minecraft);
        glBindFramebuffer(GL_FRAMEBUFFER, FBO);
        glViewport(0, 0, minimapSize, minimapSize);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        RenderType.chunkBufferLayers()
                .stream().filter(renderType -> renderType != RenderType.tripwire())
                .forEach(renderType -> renderSectionLayer(renderType, pos.x, pos.y, pos.z));
        graphics.pose().pushPose();
        glBindFramebuffer(GL_FRAMEBUFFER, Minecraft.getInstance().getMainRenderTarget().frameBufferId);
        var window = minecraft.getWindow();
        glViewport(0, 0, window.getWidth(), window.getHeight());
        RenderSystem.setShaderTexture(0, COLOR);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        float x1 = window.getWidth() * 0.95f - minimapSize, x2 = x1 + minimapSize;
        float y1 = window.getHeight() * 0.05f, y2 = y1 + minimapSize;
        graphics.pose().scale((float) (1 / window.getGuiScale()), (float) (1 / window.getGuiScale()), 1);
        bufferbuilder.addVertex(graphics.pose().last().pose(), x1, y1, 0).setUv(0, 1);
        bufferbuilder.addVertex(graphics.pose().last().pose(), x1, y2, 0).setUv(0, 0);
        bufferbuilder.addVertex(graphics.pose().last().pose(), x2, y2, 0).setUv(1, 0);
        bufferbuilder.addVertex(graphics.pose().last().pose(), x2, y1, 0).setUv(1, 1);
        BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
        graphics.pose().popPose();
    }

    private static Vec3 prepare(Minecraft minecraft) {
        var cfgR = ConfigHelper.getConfigRead(MiniMap.class);
        var camera = minecraft.gameRenderer.getMainCamera();
        MODEL_VIEW_MATRIX.identity();
        QUATERNION.identity().rotationYXZ(0, PI / 2, 0);
        if (cfgR.followPlayerRotation) {
            QUATERNION.rotateY(PI + camera.getYRot() * PI / 180);
        }
        MODEL_VIEW_MATRIX.rotation(QUATERNION);
        PROJECTION_MATRIX_IDENT.identity();
        int mapCoverBlocksHalf = cfgR.getCoverRange() / 2;
        PROJECTION_MATRIX_IDENT.ortho(-mapCoverBlocksHalf, mapCoverBlocksHalf, -mapCoverBlocksHalf, mapCoverBlocksHalf, -300, 300);

        Frustum frustum = new Frustum(MODEL_VIEW_MATRIX, PROJECTION_MATRIX_IDENT);
        var pos = camera.getPosition().add(0, 0, 0);
        frustum.prepare(pos.x, pos.y, pos.z);
        var levelRenderer = minecraft.levelRenderer;
        VISIBLE_SECTIONS.clear();
        levelRenderer.sectionOcclusionGraph.addSectionsInFrustum(frustum, VISIBLE_SECTIONS);
        return pos;
    }


    private void renderSectionLayer(RenderType renderType, double x, double y, double z) {
        var minecraft = Minecraft.getInstance();
        renderType.setupRenderState();
        boolean notTranslucent = renderType != RenderType.translucent();
        ObjectListIterator<SectionRenderDispatcher.RenderSection> objectlistiterator = VISIBLE_SECTIONS.listIterator(notTranslucent ? 0 : VISIBLE_SECTIONS.size());
        ShaderInstance shaderinstance = RenderSystem.getShader();
        //noinspection DataFlowIssue
        shaderinstance.setDefaultUniforms(VertexFormat.Mode.QUADS, MODEL_VIEW_MATRIX, PROJECTION_MATRIX_IDENT, minecraft.getWindow());
        if (shaderinstance.SCREEN_SIZE != null) {
            shaderinstance.SCREEN_SIZE.set(minimapSize, minimapSize);
        }
        shaderinstance.apply();
        Uniform uniform = shaderinstance.CHUNK_OFFSET;

        while (notTranslucent ? objectlistiterator.hasNext() : objectlistiterator.hasPrevious()) {
            SectionRenderDispatcher.RenderSection renderSection = notTranslucent ? objectlistiterator.next() : objectlistiterator.previous();
            if (!renderSection.getCompiled().isEmpty(renderType)) {
                VertexBuffer vertexbuffer = renderSection.getBuffer(renderType);
                BlockPos blockpos = renderSection.getOrigin();
                if (uniform != null) {
                    uniform.set(
                            (float) ((double) blockpos.getX() - x),
                            (float) ((double) blockpos.getY() - y),
                            (float) ((double) blockpos.getZ() - z)
                    );
                    uniform.upload();
                }

                vertexbuffer.bind();
                vertexbuffer.draw();
            }
        }

        if (uniform != null) {
            uniform.set(0.0F, 0.0F, 0.0F);
        }

        shaderinstance.clear();
        VertexBuffer.unbind();
        renderType.clearRenderState();
    }
}
