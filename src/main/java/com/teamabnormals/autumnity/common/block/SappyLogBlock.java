package com.teamabnormals.autumnity.common.block;

import com.teamabnormals.autumnity.core.registry.AutumnityItems;
import com.teamabnormals.blueprint.core.util.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.Set;
import java.util.function.Supplier;

public class SappyLogBlock extends RotatedPillarBlock {
\tprivate static final Set<ResourceLocation> RU_MAPLE_BIOMES = Set.of(
\t\t\tResourceLocation.fromNamespaceAndPath("regions_unexplored", "maple_forest"),
\t\t\tResourceLocation.fromNamespaceAndPath("regions_unexplored", "autumnal_maple_forest"),
\t\t\tResourceLocation.fromNamespaceAndPath("regions_unexplored", "windswept_maple_forest")
\t);

\tprivate final Supplier<Block> saplessBlock;

\tpublic SappyLogBlock(DeferredBlock<Block> saplessBlockIn, Properties properties) {
\t\tsuper(properties);
\t\tthis.saplessBlock = saplessBlockIn;
\t}

\t@Override
\tpublic ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
\t\tif (stack.is(Items.GLASS_BOTTLE)) {
\t\t\tstack.shrink(1);
\t\t\tlevel.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.BOTTLE_FILL, SoundSource.NEUTRAL, 1.0F, 1.0F);
\t\t\tif (stack.isEmpty()) {
\t\t\t\tplayer.setItemInHand(hand, new ItemStack(AutumnityItems.SAP_BOTTLE.get()));
\t\t\t} else if (!player.getInventory().add(new ItemStack(AutumnityItems.SAP_BOTTLE.get()))) {
\t\t\t\tplayer.drop(new ItemStack(AutumnityItems.SAP_BOTTLE.get()), false);
\t\t\t}

\t\t\tlevel.gameEvent(player, GameEvent.FLUID_PICKUP, pos);
\t\t\tif (!level.isClientSide()) {
\t\t\t\tplayer.awardStat(Stats.ITEM_USED.get(stack.getItem()));
\t\t\t}

\t\t\tlevel.setBlockAndUpdate(pos, BlockUtil.transferAllBlockStates(state, getSaplessBlock(level, pos).defaultBlockState()));
\t\t\treturn ItemInteractionResult.sidedSuccess(level.isClientSide);
\t\t}

\t\treturn super.useItemOn(stack, state, level, pos, player, hand, result);
\t}

\tprivate Block getSaplessBlock(Level level, BlockPos pos) {
\t\tResourceLocation biomeId = level.getBiome(pos).unwrapKey().map(ResourceKey::location).orElse(null);
\t\tif (biomeId != null && RU_MAPLE_BIOMES.contains(biomeId)) {
\t\t\tResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(this);
\t\t\tResourceLocation targetId = blockId.getPath().equals("sappy_maple_log")
\t\t\t\t\t? ResourceLocation.fromNamespaceAndPath("regions_unexplored", "stripped_maple_log")
\t\t\t\t\t: ResourceLocation.fromNamespaceAndPath("regions_unexplored", "stripped_maple_wood");
\t\t\tBlock ruBlock = BuiltInRegistries.BLOCK.get(targetId);
\t\t\tif (ruBlock != Blocks.AIR) {
\t\t\t\treturn ruBlock;
\t\t\t}
\t\t}
\t\treturn this.saplessBlock.get();
\t}
}
