package supercoder79.survivalgames.game.config;

import net.minecraft.world.level.LevelHeightAccessor;

public final class Y256Height implements LevelHeightAccessor {
    public static final Y256Height INSTANCE = new Y256Height();

    @Override
    public int getHeight() {
        return 256;
    }

    @Override
    public int getMinY() {
        return 0;
    }
}
