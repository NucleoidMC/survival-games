package supercoder79.survivalgames.game.logic;

import supercoder79.survivalgames.entity.SpawnerZombieEntity;
import supercoder79.survivalgames.game.SurvivalGamesActive;
import supercoder79.survivalgames.game.map.loot.LootProviders;
import xyz.nucleoid.plasmid.api.util.ItemStackBuilder;
import xyz.nucleoid.stimuli.event.EventResult;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.levelgen.Heightmap;

public final class SpawnerLogic implements ActiveLogic {
    private final SurvivalGamesActive active;
    private final BlockPos pos;
    private Stage stage = Stage.INIT;
    private int placedMonsters = 0;
    private int lootRemaining = 10;
    private final Set<SpawnerZombieEntity> zombies = new HashSet<>();
    private final RandomSource random = RandomSource.create();
    private final int targetMonsters = 8 + this.random.nextInt(4);

    public SpawnerLogic(SurvivalGamesActive active, BlockPos pos) {
        this.active = active;
        this.pos = pos;

        // Start fireworks
        for (int i = 0; i < 3; i++) {
            spawnFirework(0xFF0000);
        }
    }

    private void spawnFirework(int color) {
        ServerLevel world = this.active.getWorld();
        BlockPos pos = world.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, this.pos);

        FireworkRocketEntity firework = new FireworkRocketEntity(
                world,
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                ItemStackBuilder.firework(color, 2, FireworkExplosion.Shape.LARGE_BALL).build()
        );

        world.addFreshEntity(firework);
    }

    @Override
    public void tick(long time) {
        if (time % 8 == 0) {
            if (this.stage == Stage.INIT) {
                if (this.placedMonsters >= this.targetMonsters) {
                    this.stage = Stage.ACTIVE;
                    return;
                }

                int x = this.pos.getX() + (this.random.nextInt(8) - this.random.nextInt(8));
                int z = this.pos.getZ() + (this.random.nextInt(8) - this.random.nextInt(8));
                int y = this.active.getWorld().getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);

                SpawnerZombieEntity zombie = new SpawnerZombieEntity(this.active.getWorld());
                // More damage
                zombie.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(5.0);

                zombie.snapTo(new BlockPos(x, y, z), 0, 0);
                this.active.getWorld().addFreshEntity(zombie);
                this.zombies.add(zombie);

                this.placedMonsters++;
            } else if (this.stage == Stage.ACTIVE) {
                if (this.zombies.size() == 0) {
                    this.stage = Stage.FINISHED;
                    return;
                }

                int x = this.pos.getX() + (this.random.nextInt(8) - this.random.nextInt(8));
                int z = this.pos.getZ() + (this.random.nextInt(8) - this.random.nextInt(8));
                int y = this.pos.getY() + (this.random.nextInt(6) - this.random.nextInt(6));

                this.active.getWorld().sendParticles(ParticleTypes.SMOKE, x, y, z, 4, 0.1, 0.1, 0.1, 0.1);
            } else if (this.stage == Stage.FINISHED) {
                if (this.lootRemaining >= 0) {
                    int x = this.pos.getX() + (this.random.nextInt(8) - this.random.nextInt(8));
                    int z = this.pos.getZ() + (this.random.nextInt(8) - this.random.nextInt(8));
                    int y = this.active.getWorld().getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);

                    ItemStack stack = LootProviders.SPAWNER_LOOT.stacks.pickRandom(this.random);
                    tryEnchant(stack);

                    ItemEntity item = new ItemEntity(this.active.getWorld(), x, y + 6, z, stack);
                    this.active.getWorld().addFreshEntity(item);

                    this.lootRemaining--;
                } else {
                    for (int i = 0; i < 3; i++) {
                        spawnFirework(0x0000FF);
                    }
                    // Destroy spawner
                    this.active.getWorld().removeBlock(this.pos, false);
                    this.active.getWorld().removeBlockEntity(this.pos);
                    // Done!
                    this.active.destroyLogic(this);
                    return;
                }
            }
        }
    }

    private void tryEnchant(ItemStack stack) {
        if (this.random.nextInt(2) == 0) {
            if (stack.isEnchantable()) {
                EnchantmentHelper.enchantItem(this.random, stack, 5 + this.random.nextInt(this.random.nextInt(25) + 1),
                        this.active.getWorld().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).listElements().map(Function.identity()));
            }
        }
    }

    @Override
    public EventResult onEntityDeath(Entity entity, DamageSource source) {
        if (entity instanceof SpawnerZombieEntity zombie) {
            if (this.zombies.contains(zombie)) {
                this.zombies.remove(zombie);

                return EventResult.ALLOW;
            }
        }

        return EventResult.PASS;
    }

    private enum Stage {
        INIT,
        ACTIVE,
        FINISHED
    }
}
