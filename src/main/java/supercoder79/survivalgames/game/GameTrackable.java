package supercoder79.survivalgames.game;

import net.minecraft.core.BlockPos;

public interface GameTrackable {
    Tracker getTracker();

    @FunctionalInterface
    interface Tracker {
        void track(GenerationTracker tracker, BlockPos origin);
    }
}
