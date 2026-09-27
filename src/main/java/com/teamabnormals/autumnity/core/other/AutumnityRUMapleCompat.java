package com.teamabnormals.autumnity.core.other;

import com.teamabnormals.blueprint.core.util.BlockUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;

public final class AutumnityRUMapleCompat {
	private static final ResourceLocation RU_MAPLE_LOG = ResourceLocation.fromNamespaceAndPath("regions_unexplored", "maple_log");
	private static final ResourceLocation RU_MAPLE_WOOD = ResourceLocation.fromNamespaceAndPath("regions_unexplored", "maple_wood");
	private static final ResourceLocation RU_STRIPPED_MAPLE_LOG = ResourceLocation.fromNamespaceAndPath("regions_unexplored", "stripped_maple_log");
	private static final ResourceLocation RU_STRIPPED_MAPLE_WOOD = ResourceLocation.fromNamespaceAndPath("regions_unexplored", "stripped_maple_wood");
	private static final ResourceLocation AUTUMNITY_SAPPY_MAPLE_LOG = ResourceLocation.fromNamespaceAndPath("autumnity", "sappy_maple_log");
	private static final ResourceLocation AUTUMNITY_SAPPY_MAPLE_WOOD = ResourceLocation.fromNamespaceAndPath("autumnity", "sappy_maple_wood");

	private AutumnityRUMapleCompat() {
	}

	public static void register() {
		NeoForge.EVENT_BUS.register(AutumnityRUMapleCompat.class);
	}

	@SubscribeEvent
	public static void onToolModification(BlockEvent.BlockToolModificationEvent event) {
		if (event.getItemAbility() != ItemAbilities.AXE_STRIP) {
			return;
		}

		BlockState original = event.getState();
		ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(original.getBlock());
		ResourceLocation strippedId;
		ResourceLocation sappyId;

		if (RU_MAPLE_LOG.equals(blockId)) {
			strippedId = RU_STRIPPED_MAPLE_LOG;
			sappyId = AUTUMNITY_SAPPY_MAPLE_LOG;
		} else if (RU_MAPLE_WOOD.equals(blockId)) {
			strippedId = RU_STRIPPED_MAPLE_WOOD;
			sappyId = AUTUMNITY_SAPPY_MAPLE_WOOD;
		} else {
			return;
		}

		BlockState result = getBlock(strippedId).defaultBlockState();
		if (!event.isSimulated() && shouldBecomeSappy(event.getHeldItemStack(), event.getLevel())) {
			Block sappyBlock = getBlock(sappyId);
			if (sappyBlock != Blocks.AIR) {
				result = sappyBlock.defaultBlockState();
			}
		}

		event.setFinalState(BlockUtil.transferAllBlockStates(original, result));
	}

	private static boolean shouldBecomeSappy(ItemStack stack, LevelAccessor level) {
		int fortune = EnchantmentHelper.getTagEnchantmentLevel(
				level.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.FORTUNE), stack);
		float chance = -1.0F / (fortune * (1.0F / 3.0F) + (4.0F / 3.0F)) + 1.0F;
		return level.getRandom().nextFloat() <= chance;
	}

	private static Block getBlock(ResourceLocation id) {
		return BuiltInRegistries.BLOCK.get(id);
	}
}
