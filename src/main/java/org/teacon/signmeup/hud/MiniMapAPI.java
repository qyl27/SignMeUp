package org.teacon.signmeup.hud;

import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.moddiscovery.ModInfo;
import org.teacon.signmeup.api.MiniMap;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public final class MiniMapAPI implements MiniMap {
    public static final MiniMapAPI INSTANCE = new MiniMapAPI();

    private final Map<String, String> ID_NAME_MAP = FMLLoader.getLoadingModList().getMods().stream().collect(Collectors.toMap(
            ModInfo::getModId, ModInfo::getDisplayName
    ));

    private final CopyOnWriteArrayList<String> hider = new CopyOnWriteArrayList<>();

    @Override
    public void setMiniMapVisibility(String modID, boolean visibility) {
        Objects.requireNonNull(modID, "Argument modID cannot be null");
        if (!ID_NAME_MAP.containsKey(modID)) {
            throw new IllegalArgumentException("No such mod: " + modID);
        }

        if (!(visibility ? hider.remove(modID) : hider.addIfAbsent(modID))) {
            throw new IllegalStateException("The minimap has already been " + (!visibility ? "hided" : "visible") + " by " + modID);
        }
    }

    public boolean visible() {
        return hider.isEmpty();
    }

    public String getHiderString() {
        if (hider.isEmpty()) {
            return null;
        }

        Iterator<String> iterator = hider.iterator();
        if (!iterator.hasNext()) {
            return null;
        }
        String m1 = iterator.next();
        if (!iterator.hasNext()) {
            return m1;
        }
        String m2 = iterator.next();
        if (!iterator.hasNext()) {
            return m1 + ", " + m2;
        }
        int size = hider.size() - 2;
        if (size > 1) {
            return m1 + ", " + m2 + ", ... (" + size + " more mods)";
        } else if (size == 1) {
            return m1 + ", " + m2 + ", ... (1 more mod)";
        }else {
            return m1 + ", " + m2;
        }
    }
}
