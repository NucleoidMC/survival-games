package supercoder79.survivalgames.game.map.loot;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;

public final class LootHelper {
	public static List<ItemStack> get(List<LootProviderEntry> entries) {
		RandomSource random = RandomSource.create();
		WeightedList<WeightedList<ItemStack>> weights = new WeightedList<>();
		int maxCount = 0;
		int minCount = 0;
		int count = 0;

		// Add the stacks and values
		for (LootProviderEntry entry : entries) {
			weights.add(entry.provider.stacks, entry.count);
			maxCount += entry.provider.maxCount * entry.count;
			minCount += entry.provider.minCount * entry.count;
			count += entry.count;
		}

		// Normalize and get final count
		maxCount /= count;
		minCount /= count;
		int finalCount = random.nextInt(Math.max(maxCount - minCount, 1)) + minCount;

		// Get final stacks
		List<ItemStack> stacks = new ArrayList<>();
		for (int i = 0; i < finalCount; i++) {
			stacks.add(weights.pickRandom(random).pickRandom(random).copy());
		}

		return stacks;
	}

	public static void placeProviderChest(LevelAccessor world, BlockPos pos, LootProvider provider) {
		RandomSource random = RandomSource.create();

		List<ItemStack> stacks = LootHelper.get(ImmutableList.of(new LootProviderEntry(provider, 96 * 96)));
		world.setBlock(pos, Blocks.CHEST.defaultBlockState(), 3);
		ChestBlockEntity chest = (ChestBlockEntity) world.getBlockEntity(pos);

		for (ItemStack stack : stacks) {
			chest.setItem(random.nextInt(27), stack);
		}
	}
}
