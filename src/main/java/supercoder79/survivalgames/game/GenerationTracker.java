package supercoder79.survivalgames.game;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;

public final class GenerationTracker {
    private final CopyOnWriteArraySet<BlockPos> redstoneTracked = new CopyOnWriteArraySet<>();

    public void addRedstoneTracked(BlockPos pos) {
        this.redstoneTracked.add(pos);
    }

    public void iterateRedstoneTracked(Predicate<BlockPos> consumer) {
        this.redstoneTracked.removeIf(consumer);
    }

    public synchronized void removeRedstoneTracked(BlockPos pos) {
        this.redstoneTracked.remove(pos);
    }
}
