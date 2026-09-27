package com.teamabnormals.autumnity.core.other;

import com.teamabnormals.blueprint.core.util.BlockUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;

public final class AutumnityRUMapleCompat {
\tprivate static final ResourceLocation RU_MAPLE_LOG = ResourceLocation.fromNamespaceAndPath("regions_unexplored", "maple_log");
\tprivate static final ResourceLocation RU_MAPLE_WOOD = ResourceLocation.fromNamespaceAndPath("regions_unexplored", "maple_wood");
\tprivate static final ResourceLocation RU_STRIPPED_MAPLE_LOG = ResourceLocation.fromNamespaceAndPath("regions_unexplored", "stripped_maple_log");
\tprivate static final ResourceLocation RU_STRIPPED_MAPLE_WOOD = ResourceLocation.fromNamespaceAndPath("regions_unexplored", "stripped_maple_wood");
\tprivate static final ResourceLocation AUTUMNITY_SAPPY_MAPLE_LOG = ResourceLocation.fromNamespaceAndPath("autumnity", "sappy_maple_log");
\tprivate static final ResourceLocation AUTUMNITY_SAPPY_MAPLE_WOOD = ResourceLocation.fromNamespaceAndPath("autumnity", "sappy_maple_wood");

\tprivate AutumnityRUMapleCompat() {
\t}

\tpublic static void register() {
\t\tNeoForge.EVENT_BUS.register(AutumnityRUMapleCompat.class);
\t}

\t@SubscribeEvent
\tpublic static void onToolModification(BlockEvent.BlockToolModificationEvent event) {
\t\tif (event.getItemAbility() != ItemAbilities.AXE_STRIP) {
\t\t\treturn;
\t\t}

\t\tBlockState original = event.getState();
\t\tResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(original.getBlock());
\t\tResourceLocation strippedId;
\t\tResourceLocation sappyId;

\t\tif (RU_MAPLE_LOG.equals(blockId)) {
\t\t\tstrippedId = RU_STRIPPED_MAPLE_LOG;
\t\t\tsappyId = AUTUMNITY_SAPPY_MAPLE_LOG;
\t\t} else if (RU_MAPLE_WOOD.equals(blockId)) {
\t\t\tstrippedId = RU_STRIPPED_MAPLE_WOOD;
\t\t\tsappyId = AUTUMNITY_SAPPY_MAPLE_WOOD;
\t\t} else {
\t\t\treturn;
\t\t}

\t\tBlockState result = getBlock(strippedId).defaultBlockState();
\t\tif (!event.isSimulated() && shouldBecomeSappy(event.getHeldItemStack(), event.getLevel())) {
\t\t\tBlock sappyBlock = getBlock(sappyId);
\t\t\tif (sappyBlock != Blocks.AIR) {
\t\t\t\tresult = sappyBlock.defaultBlockState();
\t\t\t}
\t\t}

\t\tevent.setFinalState(BlockUtil.transferAllBlockStates(original, result));
\t}

\tprivate static boolean shouldBecomeSappy(ItemStack stack, Level level) {
\t\tint fortune = EnchantmentHelper.getTagEnchantmentLevel(
\t\t\t\tlevel.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.FORTUNE), stack);
\t\tfloat chance = -1.0F / (fortune * (1.0F / 3.0F) + (4.0F / 3.0F)) + 1.0F;
\t\treturn level.getRandom().nextFloat() <= chance;
\t}

\tprivate static Block getBlock(ResourceLocation id) {
\t\treturn BuiltInRegistries.BLOCK.get(id);
\t}
}
