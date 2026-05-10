package supercoder79.survivalgames.game.map.gen.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import supercoder79.survivalgames.game.GameTrackable;
import supercoder79.survivalgames.game.GenerationTracker;
import supercoder79.survivalgames.game.map.biome.generator.BiomeGenerator;
import supercoder79.survivalgames.game.map.loot.LootProvider;
import supercoder79.survivalgames.game.map.loot.LootProviders;
import xyz.nucleoid.substrate.gen.GenHelper;

public final class SpawnerStructure implements StructureGen, GameTrackable {
    public static final SpawnerStructure INSTANCE = new SpawnerStructure();

    @Override
    public int nearbyChestCount(RandomSource random) {
        return 0;
    }

    @Override
    public LootProvider getLootProvider() {
        return LootProviders.GENERIC;
    }

    @Override
    public void generate(ServerLevelAccessor world, BlockPos pos, RandomSource random) {
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                int dist = Math.abs(x) + Math.abs(z);
                int height = 0;

                if (dist == 0) {
                    height = 6 + random.nextInt(3);
                } else if (dist == 1) {
                    height = 2 + random.nextInt(3);
                } else if (dist == 2) {
                    height = random.nextInt(2);
                }

                if (height == 0) {
                    continue;
                }

                for (int y = -2; y < height; y++) {
                    BlockPos local = pos.offset(x, y, z);
                    if (world.getBlockState(local).isAir() || y == 1) { // Skip air check around spawner
                        world.setBlock(local, getState(random), 3);
                    }
                }
            }
        }

        setSpawner(world, pos.above());

        setButtons(world, pos.above());
    }

    private static BlockState getState(RandomSource random) {
        int i = random.nextInt(5);
        if (i < 2) {
            return Blocks.MOSSY_STONE_BRICKS.defaultBlockState();
        } else if (i == 3) {
            return Blocks.CRACKED_STONE_BRICKS.defaultBlockState();
        } else {
            return Blocks.STONE_BRICKS.defaultBlockState();
        }
    }

    private static void setSpawner(ServerLevelAccessor world, BlockPos pos) {
        world.setBlock(pos, Blocks.SPAWNER.defaultBlockState(), 3);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof SpawnerBlockEntity) {
            ((SpawnerBlockEntity)blockEntity).getSpawner().setEntityId(EntityType.ZOMBIE, null, RandomSource.create(), pos);
        }
    }

    private static void setButtons(ServerLevelAccessor world, BlockPos pos) {
        for (Direction horizontal : GenHelper.HORIZONTALS) {
            world.setBlock(pos.relative(horizontal, 2), Blocks.WARPED_BUTTON.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, horizontal), 3);
        }
    }

    @Override
    public Tracker getTracker() {
        return (tracker, pos) -> tracker.addRedstoneTracked(pos.above());
    }
}
