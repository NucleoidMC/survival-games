package supercoder79.survivalgames.game.map.gen.structure;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.world.level.ChunkPos;

public final class ChunkMask {
	private final LongSet chunks = new LongOpenHashSet();

	public ChunkMask() {

	}

	public void and(ChunkPos pos) {
		chunks.add(pos.pack());
	}

	public void and(ChunkBox box) {
		chunks.addAll(box.getAllPositions());
	}

	public boolean isIn(ChunkPos pos) {
		return chunks.contains(pos.pack());
	}

	public boolean isIn(int x, int z) {
		return chunks.contains(ChunkPos.pack(x, z));
	}
}
