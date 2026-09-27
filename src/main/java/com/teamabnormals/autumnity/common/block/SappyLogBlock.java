package com.teamabnormals.autumnity.common.block;

import com.teamabnormals.autumnity.core.registry.AutumnityItems;
import com.teamabnormals.blueprint.core.util.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
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
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.function.Supplier;

public class SappyLogBlock extends RotatedPillarBlock {
	private static final ResourceLocation RU_STRIPPED_MAPLE_LOG = ResourceLocation.fromNamespaceAndPath("regions_unexplored", "stripped_maple_log");
	private static final ResourceLocation RU_STRIPPED_MAPLE_WOOD = ResourceLocation.fromNamespaceAndPath("regions_unexplored", "stripped_maple_wood");

	private final Supplier<Block> saplessBlock;

	public SappyLogBlock(DeferredBlock<Block> saplessBlockIn, Properties properties) {
		super(properties);
		this.saplessBlock = saplessBlockIn;
	}

	@Override
	public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
		if (stack.is(Items.GLASS_BOTTLE)) {
			stack.shrink(1);
			level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.BOTTLE_FILL, SoundSource.NEUTRAL, 1.0F, 1.0F);
			if (stack.isEmpty()) {
				player.setItemInHand(hand, new ItemStack(AutumnityItems.SAP_BOTTLE.get()));
			} else if (!player.getInventory().add(new ItemStack(AutumnityItems.SAP_BOTTLE.get()))) {
				player.drop(new ItemStack(AutumnityItems.SAP_BOTTLE.get()), false);
			}

			level.gameEvent(player, GameEvent.FLUID_PICKUP, pos);
			if (!level.isClientSide()) {
				player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
			}

			level.setBlockAndUpdate(pos, BlockUtil.transferAllBlockStates(state, getSaplessBlock().defaultBlockState()));
			return ItemInteractionResult.sidedSuccess(level.isClientSide);
		}

		return super.useItemOn(stack, state, level, pos, player, hand, result);
	}

	private Block getSaplessBlock() {
		ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(this);
		ResourceLocation targetId = blockId.getPath().equals("sappy_maple_log") ? RU_STRIPPED_MAPLE_LOG : RU_STRIPPED_MAPLE_WOOD;
		Block ruBlock = BuiltInRegistries.BLOCK.get(targetId);
		return ruBlock != Blocks.AIR ? ruBlock : this.saplessBlock.get();
	}
}
