package supercoder79.survivalgames.game;

import supercoder79.survivalgames.SurvivalGames;
import supercoder79.survivalgames.game.config.SurvivalGamesConfig;
import supercoder79.survivalgames.game.logic.ActiveLogic;
import supercoder79.survivalgames.game.logic.SpawnerLogic;
import supercoder79.survivalgames.game.map.SurvivalGamesMap;
import xyz.nucleoid.plasmid.api.game.GameCloseReason;
import xyz.nucleoid.plasmid.api.game.GameSpace;
import xyz.nucleoid.plasmid.api.game.common.GlobalWidgets;
import xyz.nucleoid.plasmid.api.game.event.GameActivityEvents;
import xyz.nucleoid.plasmid.api.game.event.GamePlayerEvents;
import xyz.nucleoid.plasmid.api.game.player.JoinIntent;
import xyz.nucleoid.plasmid.api.game.player.PlayerSet;
import xyz.nucleoid.plasmid.api.game.rule.GameRuleType;
import xyz.nucleoid.plasmid.api.util.PlayerRef;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.block.BlockBreakEvent;
import xyz.nucleoid.stimuli.event.block.BlockPlaceEvent;
import xyz.nucleoid.stimuli.event.entity.EntityDeathEvent;
import xyz.nucleoid.stimuli.event.player.PlayerDeathEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundInitializeBorderPacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderLerpSizePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public final class SurvivalGamesActive {
    private final GameSpace space;
    private final SurvivalGamesMap map;
    private final SurvivalGamesConfig config;
    
    private final SurvivalGamesSpawnLogic spawnLogic;
    private final SurvivalGamesBar bar;

    private long startTime;
    private long shrinkStartTime;
    private boolean borderShrinkStarted = false;
    private long gameCloseTick = Long.MAX_VALUE;
    private boolean finished = false;
    private final ServerLevel world;
    private final GenerationTracker tracker;
    private final Set<ActiveLogic> logics = new HashSet<>();

    private SurvivalGamesActive(GameSpace space, SurvivalGamesMap map, SurvivalGamesConfig config, GlobalWidgets widgets, ServerLevel world, GenerationTracker tracker) {
        this.space = space;
        this.map = map;
        this.config = config;
        this.world = world;
        this.tracker = tracker;

        this.spawnLogic = new SurvivalGamesSpawnLogic(space, config);
        this.bar = SurvivalGamesBar.create(widgets);
    }

    public static void open(GameSpace space, SurvivalGamesMap map, SurvivalGamesConfig config, ServerLevel world, GenerationTracker tracker) {
        space.setActivity(game -> {
            GlobalWidgets widgets = GlobalWidgets.addTo(game);
            SurvivalGamesActive active = new SurvivalGamesActive(space, map, config, widgets, world, tracker);

            game.setRule(GameRuleType.CRAFTING, EventResult.PASS);
            game.setRule(GameRuleType.PORTALS, EventResult.DENY);
            game.setRule(GameRuleType.PVP, EventResult.PASS);
            game.setRule(GameRuleType.BLOCK_DROPS, EventResult.PASS);
            game.setRule(GameRuleType.FALL_DAMAGE, EventResult.PASS);
            game.setRule(GameRuleType.HUNGER, EventResult.DENY);
            game.setRule(GameRuleType.SATURATED_REGENERATION, EventResult.DENY);
            game.setRule(GameRuleType.UNSTABLE_TNT, EventResult.PASS);
            game.setRule(GameRuleType.THROW_ITEMS, EventResult.ALLOW);
            game.setRule(SurvivalGames.DISABLE_SPAWNERS, EventResult.ALLOW);

            game.listen(GameActivityEvents.CREATE, active::open);
            game.listen(GameActivityEvents.DESTROY, active::close);

            game.listen(GameActivityEvents.STATE_UPDATE, state -> state.canPlay(false));

            game.listen(GamePlayerEvents.OFFER, x -> x.intent() == JoinIntent.SPECTATE ? x.accept() : x.pass());
            game.listen(GamePlayerEvents.JOIN, (player -> active.spawnSpectator(player, world)));
            game.listen(GamePlayerEvents.ADD, (player -> active.addPlayer(player, world)));

            game.listen(GameActivityEvents.TICK, active::tick);

            game.listen(BlockBreakEvent.EVENT, active::onBreakBlock);

            game.listen(PlayerDeathEvent.EVENT, active::onPlayerDeath);
            game.listen(EntityDeathEvent.EVENT, active::onEntityDeath);
            game.listen(BlockPlaceEvent.BEFORE, active::onUseBlock);
        });
    }

    private void open() {
        // World border stuff
        world.getWorldBorder().setCenter(0, 0);
        world.getWorldBorder().setSize(config.borderConfig().startSize);
        world.getWorldBorder().setDamagePerBlock(0.5);
        startTime = space.getTime();

        int index = 0;

        RandomSource random = RandomSource.create();

        double radius = (config.borderConfig().startSize / 2.0);

        double maxSpawnDistance = radius * this.config.noiseGenerator().maxSpawnDistFactor();
        double minSpawnDistance = radius * this.config.noiseGenerator().minSpawnDistFactor();

        for (ServerPlayer player : this.space.getPlayers().participants()) {
            player.connection.send(new ClientboundInitializeBorderPacket(world.getWorldBorder()));

            double theta = ((double) index++ / this.space.getPlayers().participants().size()) * 2 * Math.PI;

            int spawnDistance = (int) Mth.lerp(random.nextDouble(), minSpawnDistance, maxSpawnDistance);

            int x = Mth.floor(Math.cos(theta) * spawnDistance);
            int z = Mth.floor(Math.sin(theta) * spawnDistance);

            this.spawnLogic.resetPlayer(player, GameType.SURVIVAL);
            this.spawnLogic.spawnPlayerAt(player, x, z, player.level());

            for (var stack : config.kit()) {
                player.getInventory().add(stack.create());
            }
        }
    }

    private void close(GameCloseReason gameCloseReason) {
        // this should hopefully fix players returning as survival mode to the lobby
        for (ServerPlayer player : this.space.getPlayers().participants()) {
            player.setGameMode(GameType.SURVIVAL);
        }
    }

    private void addPlayer(ServerPlayer player, ServerLevel world) {
        if (!this.space.getPlayers().participants().contains(PlayerRef.of(player))) {
            player.connection.send(new ClientboundInitializeBorderPacket(player.level().getWorldBorder()));
            this.spawnSpectator(player, world);
        }
    }

    private void tick() {
        long time = this.space.getTime();

        if (!this.borderShrinkStarted) {
            long totalSafeTime = config.borderConfig().safeSecs * 20L;
            this.bar.tickSafe(totalSafeTime - (time - startTime), totalSafeTime);

            if ((time - startTime) > totalSafeTime) {
                this.bar.setActive();
                this.borderShrinkStarted = true;
                this.shrinkStartTime = time;
                this.space.getPlayers().participants().sendMessage(Component.literal("The worldborder has started shrinking!").withStyle(ChatFormatting.RED));

                world.getWorldBorder().lerpSizeBetween(config.borderConfig().startSize, config.borderConfig().endSize, 20 * config.borderConfig().shrinkSecs, world.getGameTime());
                for (ServerPlayer player : this.space.getPlayers().participants()) {
                    player.connection.send(new ClientboundSetBorderLerpSizePacket(world.getWorldBorder()));
                }
            }
        } else {
            long totalShrinkTime = config.borderConfig().shrinkSecs * 20L;

            if ((time - shrinkStartTime) > totalShrinkTime || world.getWorldBorder().getSize() == this.config.borderConfig().endSize) {
                if (!this.finished) {
                    this.space.getPlayers().participants().sendMessage(Component.literal("Last one standing wins!").withStyle(ChatFormatting.BLUE));
                    world.getWorldBorder().setDamagePerBlock(2.5);
                    world.getWorldBorder().setSafeZone(0.125);
                    this.bar.setFinished();

                    this.finished = true;
                }
            } else {
                this.bar.tickActive(totalShrinkTime - (time - shrinkStartTime), totalShrinkTime);
            }
        }

        if (time > this.gameCloseTick) {
            this.space.close(GameCloseReason.FINISHED);
        }

        if (time % 20 == 0) {
            this.tracker.iterateRedstoneTracked(this::tickMobSpawners);
        }

        for (var logic : List.copyOf(this.logics)) {
            logic.tick(time);
        }
    }

    private boolean tickMobSpawners(BlockPos pos) {
        if (this.world.hasNeighborSignal(pos)) {
            addLogic(new SpawnerLogic(this, pos));
            TargetingConditions pred = TargetingConditions.DEFAULT;
            pred.selector((p, w) -> p instanceof ServerPlayer player && this.space.getPlayers().participants().contains(player) && player.gameMode.isSurvival());

            Player player = this.world.getNearestPlayer(pred, pos.getX(), pos.getY(), pos.getZ());

            if (player != null) {
                this.space.getPlayers().participants().sendMessage(Component.literal(player.getScoreboardName() + " triggered a spawner!").withStyle(ChatFormatting.GOLD));
            } else {
                this.space.getPlayers().participants().sendMessage(Component.literal("A spawner has been triggered!").withStyle(ChatFormatting.GOLD));
            }

            return true;
        }
        return false;
    }

    private EventResult onPlayerDeath(ServerPlayer player, DamageSource source) {
        this.eliminatePlayer(player);
        return EventResult.DENY;
    }

    private EventResult onEntityDeath(Entity entity, DamageSource source) {
        for (ActiveLogic logic : this.logics) {
            var res = logic.onEntityDeath(entity, source);

            if (res != EventResult.PASS) {
                return res;
            }
        }

        return EventResult.PASS;
    }

    private void eliminatePlayer(ServerPlayer player) {
        Component message = player.getDisplayName().copy().append(" has been eliminated!")
                .withStyle(ChatFormatting.RED);

        PlayerSet players = this.space.getPlayers();
        players.sendMessage(message);
        players.forEach(p -> p.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1, 1));

        Containers.dropContents(player.level(), player.blockPosition(), player.getInventory());

        this.spawnLogic.resetPlayer(player, GameType.SPECTATOR);

        int survival = 0;
        for (ServerPlayer participant : this.space.getPlayers().participants()) {
            if (participant.gameMode.isSurvival()) {
                survival++;
            }
        }

        if (survival == 1) {
            for (ServerPlayer participant : this.space.getPlayers().participants()) {
                if (participant.gameMode.isSurvival()) {
                    this.endGame(Component.literal(participant.getScoreboardName() + " won!").withStyle(ChatFormatting.GOLD));
                    break;
                }
            }
        } else if (survival == 0) {
            this.endGame(Component.literal("Nobody won!").withStyle(ChatFormatting.GOLD));
        }
    }

    private void spawnSpectator(ServerPlayer player, ServerLevel world) {
        this.spawnLogic.resetPlayer(player, GameType.SPECTATOR);
        this.spawnLogic.spawnPlayerAtCenter(player, world);
    }

    private void endGame(Component message) {
        this.space.getPlayers().sendMessage(message);
        this.gameCloseTick = this.space.getTime() + (20 * 10);
    }

    private EventResult onBreakBlock(ServerPlayer player, ServerLevel world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);

        if (state.is(BlockTags.LOGS) && !player.isShiftKeyDown()) {
            Set<BlockPos> logs = new HashSet<>();
            logs.add(pos);

            findLogs(world, pos, logs);

            for (BlockPos log : logs) {
                BlockState logState = world.getBlockState(log);
                world.destroyBlock(log, false);

                world.addFreshEntity(new ItemEntity(world, log.getX(), log.getY(), log.getZ(), new ItemStack(logState.getBlock())));
            }

            return EventResult.DENY;
        }

        if (state.is(Blocks.SPAWNER)) {
            return EventResult.DENY;
        }

        if (state.is(Blocks.IRON_ORE)) {
            world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.IRON_INGOT)));
            world.destroyBlock(pos, false);

            return EventResult.DENY;
        }

        if (state.is(Blocks.RAW_IRON_BLOCK)) {
            world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.IRON_INGOT, 9)));
            world.destroyBlock(pos, false);

            return EventResult.DENY;
        }

        if (state.is(Blocks.COAL_ORE)) {
            world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.COAL)));
            world.destroyBlock(pos, false);

            return EventResult.DENY;
        }

        if (state.is(Blocks.ENCHANTING_TABLE)) {
            return EventResult.DENY;
        }

        return EventResult.PASS;
    }

    private void findLogs(ServerLevel world, BlockPos pos, Set<BlockPos> logs) {
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                for (int y = -1; y <= 1; y++) {
                    BlockPos local = pos.offset(x, y, z);
                    BlockState state = world.getBlockState(local);

                    if (!logs.contains(local)) {
                        if (state.is(BlockTags.LOGS)) {
                            logs.add(local);
                            findLogs(world, local, logs);
                        }
                    }
                }
            }
        }
    }

    private EventResult onUseBlock(ServerPlayer playerEntity, ServerLevel world, BlockPos pos, BlockState state, UseOnContext itemUsageContext) {
        if (pos.getY() >= 100) {
            return EventResult.DENY;
        }

        return EventResult.PASS;
    }

    public ServerLevel getWorld() {
        return world;
    }

    public void addLogic(ActiveLogic logic) {
        this.logics.add(logic);
    }

    public void destroyLogic(ActiveLogic logic) {
        this.logics.remove(logic);
    }
}
