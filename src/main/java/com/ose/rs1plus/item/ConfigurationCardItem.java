package com.ose.rs1plus.item;

import com.ose.rs1plus.network.ConfigurationFeedbackPacket;
import com.ose.rs1plus.network.ModNetworking;
import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import com.refinedmods.refinedstorage.blockentity.config.IRedstoneConfigurable;
import com.refinedmods.refinedstorage.blockentity.config.RedstoneMode;
import com.refinedmods.refinedstorage.inventory.fluid.FluidInventory;
import com.refinedmods.refinedstorage.util.NetworkUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigurationCardItem extends Item {
    private static final String TAG_NODE_ID = "NodeId";
    private static final String TAG_DEVICE_ID = "DeviceId";
    private static final String TAG_CONFIG = "Config";
    private static final String TAG_UPGRADES = "Upgrades";
    private static final String TAG_UPGRADE_SLOT = "Slot";
    private static final String TAG_REDSTONE_MODE = "RedstoneMode";
    public static final String TAG_POS = "Pos";
    public static final String TAG_DIMENSION = "Dimension";
    private static final String CONTROLLER_SENTINEL_ID = "controller";
    private static final String[] ITEM_FILTER_METHOD_NAMES = {"getItemFilters", "getExportFilterItems"};
    private static final String[] FLUID_FILTER_METHOD_NAMES = {"getFluidFilters", "getOut"};
    private static final Map<Class<?>, Optional<Method>> UPGRADES_METHOD_CACHE = new ConcurrentHashMap<>();
    private static final Map<Class<?>, List<Method>> ITEM_FILTER_METHOD_CACHE = new ConcurrentHashMap<>();
    private static final Map<Class<?>, List<Method>> FLUID_FILTER_METHOD_CACHE = new ConcurrentHashMap<>();

    public ConfigurationCardItem(Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        return super.getName(stack).copy().withStyle(ChatFormatting.YELLOW);
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        if (context.getLevel().isClientSide) {
            return InteractionResult.SUCCESS;
        }


        BlockEntity be = context.getLevel().getBlockEntity(context.getClickedPos());

        BlockEntity be = context.getLevel().getBlockEntity(context.getClickedPos());

        ResourceLocation blockId =
                ForgeRegistries.BLOCKS.getKey(context.getLevel().getBlockState(context.getClickedPos()).getBlock());

        if (new ResourceLocation("refinedstorage", "cable").equals(blockId)) {
            return InteractionResult.PASS;
        }

        INetworkNode iNode = NetworkUtils.getNodeFromBlockEntity(be);
        Player player = context.getPlayer();

        if (!(iNode instanceof NetworkNode node)) {
            if (be instanceof IRedstoneConfigurable redstoneConfig) {
                return handleControllerInteraction(stack, be, redstoneConfig, player, context);
            }
            if (player != null && player.isShiftKeyDown() && hasStoredConfiguration(stack)) {
                clearConfiguration(stack.getOrCreateTag());
                player.displayClientMessage(Component.translatable("item.rs1plus.configuration_card.cleared"), true);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }

        if (player == null) {
            return InteractionResult.PASS;
        }

        var tag = stack.getOrCreateTag();

        if (player.isShiftKeyDown()) {
            copyConfiguration(node, tag);
            storeDeviceAndLocation(tag, be, context);
            player.displayClientMessage(Component.translatable("item.rs1plus.configuration_card.copied", getDeviceName(tag)), true);
            return InteractionResult.SUCCESS;
        }

        if (!tag.contains(TAG_NODE_ID)) {
            return InteractionResult.PASS;
        }

        if (!tag.getString(TAG_NODE_ID).equals(node.getId().toString())) {
            player.displayClientMessage(Component.translatable("item.rs1plus.configuration_card.mismatch", getDeviceName(tag)), true);
            return InteractionResult.FAIL;
        }

        pasteConfiguration(node, player, tag);
        ModNetworking.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (net.minecraft.server.level.ServerPlayer) player), new ConfigurationFeedbackPacket(context.getClickedPos()));
        player.displayClientMessage(Component.translatable("item.rs1plus.configuration_card.pasted", getDeviceName(tag).copy().withStyle(ChatFormatting.GREEN)), true);

        return InteractionResult.SUCCESS;
    }

    private InteractionResult handleControllerInteraction(ItemStack stack, BlockEntity be, IRedstoneConfigurable redstoneConfig, Player player, UseOnContext context) {
        if (player == null) {
            return InteractionResult.PASS;
        }

        var tag = stack.getOrCreateTag();

        if (player.isShiftKeyDown()) {
            tag.putString(TAG_NODE_ID, CONTROLLER_SENTINEL_ID);
            tag.remove(TAG_CONFIG);
            tag.remove(TAG_UPGRADES);
            tag.putString(TAG_REDSTONE_MODE, redstoneConfig.getRedstoneMode().name());
            storeDeviceAndLocation(tag, be, context);
            player.displayClientMessage(Component.translatable("item.rs1plus.configuration_card.copied", getDeviceName(tag)), true);
            return InteractionResult.SUCCESS;
        }

        if (!tag.contains(TAG_NODE_ID)) {
            return InteractionResult.PASS;
        }

        if (!CONTROLLER_SENTINEL_ID.equals(tag.getString(TAG_NODE_ID))) {
            player.displayClientMessage(Component.translatable("item.rs1plus.configuration_card.mismatch", getDeviceName(tag)), true);
            return InteractionResult.FAIL;
        }

        try {
            redstoneConfig.setRedstoneMode(RedstoneMode.valueOf(tag.getString(TAG_REDSTONE_MODE)));
        } catch (IllegalArgumentException ignored) {
            return InteractionResult.FAIL;
        }

        ModNetworking.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (net.minecraft.server.level.ServerPlayer) player), new ConfigurationFeedbackPacket(context.getClickedPos()));
        player.displayClientMessage(Component.translatable("item.rs1plus.configuration_card.pasted", getDeviceName(tag).copy().withStyle(ChatFormatting.GREEN)), true);

        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);


        if (!player.isShiftKeyDown() || !hasStoredConfiguration(stack)) {
            return InteractionResultHolder.pass(stack);
        }

        if (!level.isClientSide) {
            clearConfiguration(stack.getOrCreateTag());
            player.displayClientMessage(Component.translatable("item.rs1plus.configuration_card.cleared"), true);
        }

        return InteractionResultHolder.success(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        if (!hasStoredConfiguration(stack)) {
            tooltip.add(Component.translatable("item.rs1plus.configuration_card.tooltip.empty").withStyle(ChatFormatting.GRAY));
            return;
        }
        tooltip.add(Component.translatable("item.rs1plus.configuration_card.tooltip.contains", getDeviceName(stack.getTag())).withStyle(ChatFormatting.GRAY));
    }

    private static Component getDeviceName(CompoundTag tag) {
        if (tag.contains(TAG_DEVICE_ID)) {
            ResourceLocation id = ResourceLocation.tryParse(tag.getString(TAG_DEVICE_ID));
            if (id != null) {
                Item item = ForgeRegistries.ITEMS.getValue(id);
                if (item != null && item != Items.AIR) {
                    return item.getDescription().copy().withStyle(ChatFormatting.AQUA);
                }
            }
        }
        return Component.translatable("item.rs1plus.configuration_card.tooltip.unknown_device").withStyle(ChatFormatting.AQUA);
    }

    private static boolean hasStoredConfiguration(ItemStack stack) {
        return stack.hasTag() && stack.getTag().contains(TAG_NODE_ID);
    }

    private static void clearConfiguration(CompoundTag tag) {
        tag.remove(TAG_NODE_ID);
        tag.remove(TAG_DEVICE_ID);
        tag.remove(TAG_CONFIG);
        tag.remove(TAG_UPGRADES);
        tag.remove(TAG_REDSTONE_MODE);
        tag.remove(TAG_POS);
        tag.remove(TAG_DIMENSION);
    }

    private static void storeDeviceAndLocation(CompoundTag tag, BlockEntity be, UseOnContext context) {
        Item deviceItem = Item.BY_BLOCK.get(be.getBlockState().getBlock());
        if (deviceItem != null && deviceItem != Items.AIR) {
            tag.putString(TAG_DEVICE_ID, ForgeRegistries.ITEMS.getKey(deviceItem).toString());
        } else {
            tag.remove(TAG_DEVICE_ID);
        }
        tag.putLong(TAG_POS, context.getClickedPos().asLong());
        tag.putString(TAG_DIMENSION, context.getLevel().dimension().location().toString());
    }

    private void copyConfiguration(NetworkNode node, CompoundTag tag) {
        tag.putString(TAG_NODE_ID, node.getId().toString());
        tag.put(TAG_CONFIG, node.writeConfiguration(new CompoundTag()));
        tag.remove(TAG_REDSTONE_MODE);

        IItemHandlerModifiable upgrades = getUpgradesHandler(node);
        if (upgrades == null) {
            tag.remove(TAG_UPGRADES);
            return;
        }

        ListTag upgradeList = new ListTag();
        for (int i = 0; i < upgrades.getSlots(); i++) {
            ItemStack sourceStack = upgrades.getStackInSlot(i);
            if (sourceStack.isEmpty()) {
                continue;
            }
            CompoundTag stackTag = sourceStack.save(new CompoundTag());
            stackTag.putInt(TAG_UPGRADE_SLOT, i);
            upgradeList.add(stackTag);
        }
        tag.put(TAG_UPGRADES, upgradeList);
    }

    private void pasteConfiguration(NetworkNode node, Player player, CompoundTag tag) {
        IItemHandlerModifiable itemFilters = getItemFiltersHandler(node);
        if (itemFilters != null) {
            for (int slot = 0; slot < itemFilters.getSlots(); slot++) {
                itemFilters.setStackInSlot(slot, ItemStack.EMPTY);
            }
        }

        FluidInventory fluidFilters = getFluidFiltersHandler(node);
        if (fluidFilters != null) {
            for (int slot = 0; slot < fluidFilters.getSlots(); slot++) {
                fluidFilters.setFluid(slot, FluidStack.EMPTY);
            }
        }

        node.readConfiguration(tag.getCompound(TAG_CONFIG));

        IItemHandlerModifiable upgrades = getUpgradesHandler(node);
        if (upgrades == null) {
            node.markDirty();
            return;
        }

        Map<Integer, ItemStack> templateBySlot = new HashMap<>();
        if (tag.contains(TAG_UPGRADES)) {
            ListTag upgradeList = tag.getList(TAG_UPGRADES, Tag.TAG_COMPOUND);
            for (int i = 0; i < upgradeList.size(); i++) {
                CompoundTag stackTag = upgradeList.getCompound(i);
                templateBySlot.put(stackTag.getInt(TAG_UPGRADE_SLOT), ItemStack.of(stackTag));
            }
        }

        for (int slot = 0; slot < upgrades.getSlots(); slot++) {
            ItemStack existing = upgrades.getStackInSlot(slot);
            ItemStack template = templateBySlot.get(slot);

            if (template == null) {
                if (!existing.isEmpty()) {
                    giveOrDropSilently(player, existing.copy());
                    upgrades.setStackInSlot(slot, ItemStack.EMPTY);
                }
                continue;
            }

            if (ItemStack.isSameItemSameTags(existing, template) && existing.getCount() == template.getCount()) {
                continue;
            }

            ItemStack extracted = extractMatchingFromPlayer(player, template);
            if (extracted.isEmpty()) {
                if (!player.isCreative()) {
                    continue;
                }
                extracted = template.copy();
            }

            if (!existing.isEmpty()) {
                giveOrDropSilently(player, existing.copy());
            }
            upgrades.setStackInSlot(slot, extracted);
        }

        node.markDirty();
    }

    private static void giveOrDropSilently(Player player, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        player.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve().ifPresentOrElse(handler -> {
            ItemStack remainder = ItemHandlerHelper.insertItemStacked(handler, stack, false);
            if (!remainder.isEmpty()) {
                player.drop(remainder, false);
            }
        }, () -> {
            if (!player.getInventory().add(stack)) {
                player.drop(stack, false);
            }
        });
    }

    private static IItemHandlerModifiable getUpgradesHandler(NetworkNode node) {
        Optional<Method> cached = UPGRADES_METHOD_CACHE.computeIfAbsent(node.getClass(), cls -> {
            try {
                return Optional.of(cls.getMethod("getUpgrades"));
            } catch (NoSuchMethodException ignored) {
                return Optional.empty();
            }
        });

        if (cached.isEmpty()) {
            return null;
        }

        try {
            Object result = cached.get().invoke(node);
            if (result instanceof IItemHandlerModifiable modifiable) {
                return modifiable;
            }
        } catch (ReflectiveOperationException ignored) {
        }

        return null;
    }

    private static IItemHandlerModifiable getItemFiltersHandler(NetworkNode node) {
        List<Method> candidates = ITEM_FILTER_METHOD_CACHE.computeIfAbsent(node.getClass(), cls -> resolveExistingMethods(cls, ITEM_FILTER_METHOD_NAMES));

        for (Method m : candidates) {
            try {
                Object result = m.invoke(node);
                if (result instanceof IItemHandlerModifiable modifiable) {
                    return modifiable;
                }
            } catch (ReflectiveOperationException ignored) {
            }
        }

        return null;
    }

    private static FluidInventory getFluidFiltersHandler(NetworkNode node) {
        List<Method> candidates = FLUID_FILTER_METHOD_CACHE.computeIfAbsent(node.getClass(), cls -> resolveExistingMethods(cls, FLUID_FILTER_METHOD_NAMES));

        for (Method m : candidates) {
            try {
                Object result = m.invoke(node);
                if (result instanceof FluidInventory fluidInventory) {
                    return fluidInventory;
                }
            } catch (ReflectiveOperationException ignored) {
            }
        }

        return null;
    }

    private static List<Method> resolveExistingMethods(Class<?> nodeClass, String[] candidateNames) {
        List<Method> found = new ArrayList<>(candidateNames.length);
        for (String methodName : candidateNames) {
            try {
                found.add(nodeClass.getMethod(methodName));
            } catch (NoSuchMethodException ignored) {
            }
        }
        return found;
    }

    private static ItemStack extractMatchingFromPlayer(Player player, ItemStack template) {
        int needed = template.getCount();
        var inv = player.getInventory();

        int found = extractFrom(inv.items, template, needed);
        if (found < needed) {
            found += extractFrom(inv.offhand, template, needed - found);
        }
        if (found == 0) {
            return ItemStack.EMPTY;
        }

        ItemStack result = template.copy();
        result.setCount(found);
        return result;
    }

    private static int extractFrom(List<ItemStack> slots, ItemStack template, int needed) {
        int found = 0;
        for (int i = 0; i < slots.size() && found < needed; i++) {
            ItemStack candidate = slots.get(i);
            if (!candidate.isEmpty() && ItemStack.isSameItemSameTags(candidate, template)) {
                int take = Math.min(needed - found, candidate.getCount());
                candidate.shrink(take);
                found += take;
            }
        }
        return found;
    }
}