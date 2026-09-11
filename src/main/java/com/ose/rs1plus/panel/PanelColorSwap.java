package com.ose.rs1plus.panel;

import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.api.network.node.INetworkNodeManager;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.block.NetworkNodeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;

// Logica de swap de color + preservacion de nodo, extraida de
// AbstractPanelBlock.use() para que la puedan usar tambien los paneles que
// NO pueden extender AbstractPanelBlock (Crafter Manager, Storage Monitor),
// porque esos necesitan extender su propia clase de bloque vanilla (ver
// nota en CrafterManagerPanelBlock sobre por que).
public final class PanelColorSwap {

    private PanelColorSwap() {
    }

    // Contrato minimo que cualquier panel coloreable debe cumplir.
    public interface Host {
        @Nullable
        DyeColor getColor();

        DirectionProperty getDirectionProperty();

        boolean hasConnectedState();

        @Nullable
        RegistryObject<? extends Block> panelForColor(DyeColor color);
    }

    /**
     * @return el InteractionResult si se manejo un swap de color, o null si
     * el item en mano no era un dye util -- en ese caso el caller debe
     * seguir con su logica normal de "primary use".
     */
    @Nullable
    public static InteractionResult trySwapColor(Host host, BlockState state, Level level, BlockPos pos,
                                                 Player player, ItemStack heldItem) {
        if (!(heldItem.getItem() instanceof DyeItem dyeItem)) {
            return null;
        }

        DyeColor newColor = dyeItem.getDyeColor();
        DyeColor currentColor = (host.getColor() == null) ? DyeColor.LIGHT_BLUE : host.getColor();

        if (newColor == currentColor) {
            return null;
        }

        RegistryObject<? extends Block> target = host.panelForColor(newColor);
        if (target == null || !target.isPresent()) {
            return null;
        }

        DirectionProperty dirProp = host.getDirectionProperty();
        BlockState newState = target.get().defaultBlockState()
                .setValue(dirProp, state.getValue(dirProp));

        // Ver comentario detallado del mismo paso en AbstractPanelBlock.use():
        // preservar CONNECTED y el nodo de red a traves del swap de instancia.
        if (host.hasConnectedState()) {
            newState = newState.setValue(NetworkNodeBlock.CONNECTED, state.getValue(NetworkNodeBlock.CONNECTED));
        }

        INetworkNodeManager manager = level.isClientSide
                ? null
                : API.instance().getNetworkNodeManager((ServerLevel) level);

        INetworkNode oldNode = manager != null ? manager.getNode(pos) : null;
        INetwork oldNetwork = oldNode != null ? oldNode.getNetwork() : null;

        level.setBlockAndUpdate(pos, newState);

        if (manager != null && oldNode != null) {
            manager.setNode(pos, oldNode);
            manager.markForSaving();

            if (oldNetwork != null) {
                oldNetwork.getNodeGraph().invalidate(Action.PERFORM, oldNetwork.getLevel(), oldNetwork.getPosition());
            }
        }

        if (!level.isClientSide && !player.getAbilities().instabuild) {
            heldItem.shrink(1);
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}