package org.teacon.signmeup.hud;

import net.caffeinemc.mods.sodium.client.render.SodiumWorldRenderer;
import net.caffeinemc.mods.sodium.client.render.chunk.ChunkRenderer;
import net.caffeinemc.mods.sodium.client.render.chunk.RenderSectionManager;
import net.caffeinemc.mods.sodium.client.render.chunk.ShaderChunkRenderer;
import net.caffeinemc.mods.sodium.client.render.chunk.vertex.format.ChunkVertexType;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class SodiumAccess {
    private static final MethodHandle CHUNK_VERTEX_TYPE;

    private static final MethodHandle RENDER_SECTION_MANAGER;

    static {
        try {
            MethodHandles.Lookup myself = MethodHandles.lookup();

            MethodHandle getter1 = MethodHandles.privateLookupIn(RenderSectionManager.class, myself).findGetter(RenderSectionManager.class, "chunkRenderer", ChunkRenderer.class);
            MethodHandle getter2 = myself.findStatic(SodiumAccess.class, "castChunkRenderer", MethodType.methodType(ShaderChunkRenderer.class, ChunkRenderer.class));
            MethodHandle getter3 = MethodHandles.privateLookupIn(ShaderChunkRenderer.class, myself).findGetter(ShaderChunkRenderer.class, "vertexType", ChunkVertexType.class);

            RENDER_SECTION_MANAGER = MethodHandles.privateLookupIn(SodiumWorldRenderer.class, myself).findGetter(SodiumWorldRenderer.class, "renderSectionManager", RenderSectionManager.class);
            CHUNK_VERTEX_TYPE = MethodHandles.filterArguments(getter3, 0, MethodHandles.filterArguments(getter2, 0, getter1));
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private static ShaderChunkRenderer castChunkRenderer(ChunkRenderer r) {
        return (ShaderChunkRenderer) r;
    }

    public static SodiumWorldRenderer getWorldRenderer() {
        return SodiumWorldRenderer.instance();
    }

    public static ChunkVertexType getChunkVertexType(RenderSectionManager renderer) throws ReflectiveOperationException {
        try {
            return (ChunkVertexType) CHUNK_VERTEX_TYPE.invokeExact(renderer);
        } catch (Throwable t) {
            throw new ReflectiveOperationException(t);
        }
    }

    public static RenderSectionManager getRenderSectionManager(SodiumWorldRenderer renderer) throws ReflectiveOperationException {
        try {
            return (RenderSectionManager) RENDER_SECTION_MANAGER.invokeExact(renderer);
        } catch (Throwable t) {
            throw new ReflectiveOperationException(t);
        }
    }
}
