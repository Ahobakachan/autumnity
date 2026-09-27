package com.teamabnormals.autumnity.core;

import com.teamabnormals.autumnity.core.data.client.AutumnityBlockStateProvider;
import com.teamabnormals.autumnity.core.data.client.AutumnityItemModelProvider;
import com.teamabnormals.autumnity.core.data.server.*;
import com.teamabnormals.autumnity.core.data.server.tags.*;
import com.teamabnormals.autumnity.core.other.AutumnityClientCompat;
import com.teamabnormals.autumnity.core.other.AutumnityCompat;
import com.teamabnormals.autumnity.core.other.AutumnityRUMapleCompat;
import com.teamabnormals.autumnity.core.registry.*;
import com.teamabnormals.blueprint.core.util.registry.RegistryHelper;
import com.teamabnormals.gallery.core.data.client.GalleryItemModelProvider;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@Mod(Autumnity.MOD_ID)
public class Autumnity {
\tpublic static final String MOD_ID = "autumnity";
\tpublic static final RegistryHelper REGISTRY_HELPER = new RegistryHelper(MOD_ID);

\tpublic Autumnity(IEventBus bus, ModContainer container) {
\t\tAutumnityBlocks.BLOCKS.register(bus);
\t\tAutumnityItems.ITEMS.register(bus);
\t\tAutumnityEntityTypes.ENTITY_TYPES.register(bus);
\t\tAutumnitySoundEvents.SOUND_EVENTS.register(bus);
\t\tAutumnityMobEffects.MOB_EFFECTS.register(bus);
\t\tAutumnityPotions.POTIONS.register(bus);
\t\tAutumnityFeatures.FEATURES.register(bus);
\t\tAutumnityFeatures.TREE_DECORATOR_TYPES.register(bus);
\t\tAutumnityParticleTypes.PARTICLE_TYPES.register(bus);
\t\tAutumnityConditions.CONDITION_SERIALIZERS.register(bus);
\t\tAutumnityCriteriaTriggers.TRIGGERS.register(bus);
\t\tAutumnityArmorMaterials.ARMOR_MATERIALS.register(bus);

\t\tbus.addListener(this::commonSetup);
\t\tbus.addListener(this::clientSetup);
\t\tbus.addListener(this::dataSetup);

\t\tcontainer.registerConfig(ModConfig.Type.COMMON, AutumnityConfig.COMMON_SPEC);
\t}

\tprivate void commonSetup(FMLCommonSetupEvent event) {
\t\tevent.enqueueWork(() -> {
\t\t\tAutumnityCompat.register();
\t\t\tAutumnityRUMapleCompat.register();
\t\t});
\t}

\tprivate void clientSetup(FMLClientSetupEvent event) {
\t\tevent.enqueueWork(AutumnityClientCompat::register);
\t}

\tprivate void dataSetup(GatherDataEvent event) {
\t\tDataGenerator generator = event.getGenerator();
\t\tPackOutput output = generator.getPackOutput();
\t\tCompletableFuture<Provider> provider = event.getLookupProvider();
\t\tExistingFileHelper helper = event.getExistingFileHelper();

\t\tboolean server = event.includeServer();

\t\tAutumnityDatapackProvider datapackEntries = new AutumnityDatapackProvider(output, provider);
\t\tgenerator.addProvider(server, datapackEntries);
\t\tprovider = datapackEntries.getRegistryProvider();

\t\tAutumnityBlockTagsProvider blockTags = new AutumnityBlockTagsProvider(output, provider, helper);
\t\tgenerator.addProvider(server, blockTags);
\t\tgenerator.addProvider(server, new AutumnityItemTagsProvider(output, provider, blockTags.contentsGetter(), helper));
\t\tgenerator.addProvider(server, new AutumnityBiomeTagsProvider(output, provider, helper));
\t\tgenerator.addProvider(server, new AutumnityBannerPatternTagsProvider(output, provider, helper));
\t\tgenerator.addProvider(server, new AutumnityPaintingVariantTagsProvider(output, provider, helper));
\t\tgenerator.addProvider(server, new AutumnityStructureTagsProvider(output, provider, helper));
\t\tgenerator.addProvider(server, new AutumnityEntityTypeTagsProvider(output, provider, helper));
\t\tgenerator.addProvider(server, new AutumnityRecipeProvider(output, provider));
\t\tgenerator.addProvider(server, AutumnityAdvancementProvider.create(output, provider, helper));
\t\tgenerator.addProvider(server, new AutumnityLootTableProvider(output, provider));
\t\tgenerator.addProvider(server, new AutumnityAdvancementModifierProvider(output, provider));
\t\tgenerator.addProvider(server, new AutumnityDataRemolderProvider(output, provider));
\t\tgenerator.addProvider(server, new AutumnityDataMapProvider(output, provider));

\t\tboolean client = event.includeClient();
\t\tgenerator.addProvider(client, new AutumnityItemModelProvider(output, helper));
\t\tgenerator.addProvider(client, new AutumnityBlockStateProvider(output, helper));

\t\tgenerator.addProvider(client, new GalleryItemModelProvider(MOD_ID, output, helper, provider));
\t}

\tpublic static ResourceLocation location(String path) {
\t\treturn ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
\t}
}
