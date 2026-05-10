package supercoder79.survivalgames.game.map.gen.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import supercoder79.survivalgames.game.map.loot.LootHelper;
import supercoder79.survivalgames.game.map.loot.LootProvider;
import supercoder79.survivalgames.game.map.loot.LootProviders;
import xyz.nucleoid.substrate.gen.GenHelper;

public class FarmlandStructure implements StructureGen {
	public static StructureGen INSTANCE = new FarmlandStructure();

	@Override
	public void generate(ServerLevelAccessor world, BlockPos pos, RandomSource random) {
		boolean chestPlaced = false;
		for (int i = 0; i < 196; i++) {
			int aX = random.nextInt(16) - random.nextInt(16);
			int aY = random.nextInt(4) - random.nextInt(4);
			int aZ = random.nextInt(16) - random.nextInt(16);

			BlockPos local = pos.offset(aX, aY, aZ);
			if (world.getBlockState(local) == Blocks.GRASS_BLOCK.defaultBlockState()) {
				boolean canSpawn = true;

				for (Direction direction : GenHelper.HORIZONTALS) {
					BlockPos dLocal = local.relative(direction);
					if (!world.getBlockState(dLocal).canOcclude()) {
						if (!world.getBlockState(dLocal).is(Blocks.WATER)) {
							canSpawn = false;
						}

						break;
					}
				}

				if (canSpawn) {
					if (!chestPlaced) {
						chestPlaced = true;
						LootHelper.placeProviderChest(world, local.above(), LootProviders.FARMLAND);
					} else {
						if (random.nextInt(3) == 0) {
							world.setBlock(local, Blocks.WATER.defaultBlockState(), 3);
						} else {
							world.setBlock(local, Blocks.FARMLAND.defaultBlockState().setValue(BlockStateProperties.MOISTURE, 7), 3);
							world.setBlock(local.above(), Blocks.WHEAT.defaultBlockState().setValue(BlockStateProperties.AGE_7, random.nextInt(8)), 3);
						}
					}
				}
			}

		}
	}

	@Override
	public int nearbyChestCount(RandomSource random) {
		return 1 + random.nextInt(2);
	}

	@Override
	public LootProvider getLootProvider() {
		return LootProviders.FARMLAND;
	}
}
