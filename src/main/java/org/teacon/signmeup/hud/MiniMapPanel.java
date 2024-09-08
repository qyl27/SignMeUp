package org.teacon.signmeup.hud;

import cn.ussshenzhou.t88.T88;
import cn.ussshenzhou.t88.config.ConfigHelper;
import cn.ussshenzhou.t88.gui.widegt.TPanel;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.caffeinemc.mods.sodium.client.gl.device.CommandList;
import net.caffeinemc.mods.sodium.client.gl.device.RenderDevice;
import net.caffeinemc.mods.sodium.client.render.SodiumWorldRenderer;
import net.caffeinemc.mods.sodium.client.render.chunk.ChunkRenderMatrices;
import net.caffeinemc.mods.sodium.client.render.chunk.DefaultChunkRenderer;
import net.caffeinemc.mods.sodium.client.render.chunk.RenderSectionManager;
import net.caffeinemc.mods.sodium.client.render.chunk.ShaderChunkRenderer;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.DefaultTerrainRenderPasses;
import net.caffeinemc.mods.sodium.client.render.chunk.vertex.format.ChunkVertexType;
import net.caffeinemc.mods.sodium.client.render.viewport.Viewport;
import net.caffeinemc.mods.sodium.client.render.viewport.ViewportProvider;
import net.caffeinemc.mods.sodium.client.util.FlawlessFrames;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.uniforms.CapturedRenderingState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.teacon.signmeup.SignMeUp;
import org.teacon.signmeup.config.MiniMap;

import static org.lwjgl.opengl.GL40C.*;

/**
 * @author USS_Shenzhou
 */
public class MiniMapPanel extends TPanel {
    public static final ObjectArrayList<SectionRenderDispatcher.RenderSection> VISIBLE_SECTIONS = new ObjectArrayList<>(10000);
    private static int minimapSize, FBO, COLOR, DEPTH;
    private static final Matrix4f MODEL_VIEW_MATRIX = new Matrix4f();
    private static final Matrix4f PROJECTION_MATRIX = new Matrix4f();
    private static final Quaternionf QUATERNION = new Quaternionf();
    private static final float PI = (float) Math.PI;
    public static boolean rendering = false;

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
        prepare(minecraft);
        var camera = minecraft.gameRenderer.getMainCamera();
        var pos = camera.getPosition();
        glBindFramebuffer(GL_FRAMEBUFFER, FBO);
        glViewport(0, 0, minimapSize, minimapSize);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        Frustum frustum = new Frustum(MODEL_VIEW_MATRIX, PROJECTION_MATRIX);
        if (SignMeUp.IS_SODIUM_INSTALLED) {
            try {
                @SuppressWarnings("JavaReflectionMemberAccess")
                var sodiumWorldRendererField = LevelRenderer.class.getDeclaredField("renderer");
                sodiumWorldRendererField.setAccessible(true);
                var sodiumWorldRenderer = (SodiumWorldRenderer) sodiumWorldRendererField.get(minecraft.levelRenderer);
                var renderSectionManagerField = SodiumWorldRenderer.class.getDeclaredField("renderSectionManager");
                renderSectionManagerField.setAccessible(true);
                var renderSectionManager = (RenderSectionManager) renderSectionManagerField.get(sodiumWorldRenderer);
                var chunkRendererField = RenderSectionManager.class.getDeclaredField("chunkRenderer");
                chunkRendererField.setAccessible(true);
                var shaderChunkRenderer = (ShaderChunkRenderer) chunkRendererField.get(renderSectionManager);
                var vertexTypeField = ShaderChunkRenderer.class.getDeclaredField("vertexType");
                vertexTypeField.setAccessible(true);
                var vertexType = (ChunkVertexType) vertexTypeField.get(shaderChunkRenderer);
                var projectionMatrixTmp = RenderSystem.getProjectionMatrix();

                RenderSystem.setProjectionMatrix(PROJECTION_MATRIX, RenderSystem.getVertexSorting());
                var posSodium = pos.add(0, 320, 0);
                frustum.prepare(posSodium.x, posSodium.y, posSodium.z);
                @SuppressWarnings("DataFlowIssue")
                Viewport viewport = ((ViewportProvider) frustum).sodium$createViewport();
                var matrices = new ChunkRenderMatrices(PROJECTION_MATRIX, MODEL_VIEW_MATRIX);
                var gbufferModelViewTmp = CapturedRenderingState.INSTANCE.getGbufferModelView();
                CapturedRenderingState.INSTANCE.setGbufferModelView(MODEL_VIEW_MATRIX);
                camera.setPosition(posSodium);
                RenderDevice.enterManagedCode();
                var renderer = new DefaultChunkRenderer(RenderDevice.INSTANCE, vertexType);
                var name = Iris.getIrisConfig().getShaderPackName();
                Iris.getIrisConfig().setShaderPackName(null);
                rendering = true;
                sodiumWorldRenderer.setupTerrain(camera, viewport, minecraft.player.isSpectator(), FlawlessFrames.isActive());
                RenderDevice device = RenderDevice.INSTANCE;
                CommandList commandList = device.createCommandList();
                renderSectionManager.renderLayer(matrices, DefaultTerrainRenderPasses.SOLID, posSodium.x, posSodium.y, posSodium.z);
                renderSectionManager.renderLayer(matrices, DefaultTerrainRenderPasses.CUTOUT, posSodium.x, posSodium.y, posSodium.z);
                renderSectionManager.renderLayer(matrices, DefaultTerrainRenderPasses.TRANSLUCENT, posSodium.x, posSodium.y, posSodium.z);
                commandList.flush();
                rendering = false;
                name.ifPresent(s -> Iris.getIrisConfig().setShaderPackName(s));
                RenderDevice.exitManagedCode();
                CapturedRenderingState.INSTANCE.setGbufferModelView(gbufferModelViewTmp);
                camera.setPosition(pos);
                RenderSystem.setProjectionMatrix(projectionMatrixTmp, RenderSystem.getVertexSorting());
            } catch (NoSuchFieldException | IllegalAccessException e) {
                if (T88.TEST) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            frustum.prepare(pos.x, pos.y, pos.z);
            minecraft.levelRenderer.sectionOcclusionGraph.addSectionsInFrustum(frustum, VISIBLE_SECTIONS);
            RenderType.chunkBufferLayers()
                    .stream().filter(renderType -> renderType != RenderType.tripwire())
                    .forEach(renderType -> renderSectionLayer(renderType, pos.x, pos.y, pos.z));
        }
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

    private static void prepare(Minecraft minecraft) {
        var cfgR = ConfigHelper.getConfigRead(MiniMap.class);
        var camera = minecraft.gameRenderer.getMainCamera();
        MODEL_VIEW_MATRIX.identity();
        QUATERNION.identity().rotationYXZ(0, PI / 2, 0);
        if (cfgR.followPlayerRotation) {
            QUATERNION.rotateY(PI + camera.getYRot() * PI / 180);
        }
        MODEL_VIEW_MATRIX.rotation(QUATERNION);
        PROJECTION_MATRIX.identity();
        int mapCoverBlocksHalf = cfgR.getCoverRange() / 2;
        PROJECTION_MATRIX.ortho(-mapCoverBlocksHalf, mapCoverBlocksHalf, -mapCoverBlocksHalf, mapCoverBlocksHalf, -1000, 1000);
        VISIBLE_SECTIONS.clear();
    }


    private void renderSectionLayer(RenderType renderType, double x, double y, double z) {
        var minecraft = Minecraft.getInstance();
        renderType.setupRenderState();
        boolean notTranslucent = renderType != RenderType.translucent();
        ObjectListIterator<SectionRenderDispatcher.RenderSection> objectlistiterator = VISIBLE_SECTIONS.listIterator(notTranslucent ? 0 : VISIBLE_SECTIONS.size());
        ShaderInstance shaderinstance = RenderSystem.getShader();
        //noinspection DataFlowIssue
        shaderinstance.setDefaultUniforms(VertexFormat.Mode.QUADS, MODEL_VIEW_MATRIX, PROJECTION_MATRIX, minecraft.getWindow());
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
