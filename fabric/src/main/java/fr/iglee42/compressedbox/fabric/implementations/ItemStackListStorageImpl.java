package fr.iglee42.compressedbox.fabric.implementations;

import java.util.*;

import fr.iglee42.compressedbox.CompressedBox;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.impl.transfer.item.ItemVariantImpl;
import net.fabricmc.fabric.impl.transfer.item.SpecialLogicInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.properties.ChestType;
import org.jetbrains.annotations.Nullable;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.fabricmc.fabric.impl.transfer.DebugMessages;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;

public class ItemStackListStorageImpl extends CombinedStorage<ItemVariant, SingleSlotStorage<ItemVariant>> implements InventoryStorage {


	final List<ItemStack> inventory;
	final List<InventorySlotWrapper> backingList;
	final MarkDirtyParticipant markDirtyParticipant = new MarkDirtyParticipant();

	public ItemStackListStorageImpl(List<ItemStack> inventory) {
		super(Collections.emptyList());
		this.inventory = inventory;
		this.backingList = new ArrayList<>();
		resizeSlotList();
	}

	@Override
	public List<SingleSlotStorage<ItemVariant>> getSlots() {
		return parts;
	}

	private void resizeSlotList() {
		int inventorySize = inventory.size();

		if (inventorySize != parts.size()) {
			while (backingList.size() < inventorySize) {
				backingList.add(new InventorySlotWrapper(this, backingList.size()));
			}

			parts = Collections.unmodifiableList(backingList.subList(0, inventorySize));
		}
	}

	class MarkDirtyParticipant extends SnapshotParticipant<Boolean> {
		@Override
		protected Boolean createSnapshot() {
			return Boolean.TRUE;
		}

		@Override
		protected void readSnapshot(Boolean snapshot) {
		}

		@Override
		protected void onFinalCommit() {
		}
	}

	class InventorySlotWrapper extends SingleStackStorage {

		private final ItemStackListStorageImpl storage;
		final int slot;
		private ItemStack lastReleasedSnapshot = null;

		InventorySlotWrapper(ItemStackListStorageImpl storage, int slot) {
			this.storage = storage;
			this.slot = slot;
		}

		@Override
		protected ItemStack getStack() {
			return storage.inventory.get(slot);
		}

		@Override
		protected void setStack(ItemStack stack) {
			storage.inventory.set(slot, stack);
		}

		@Override
		public long insert(ItemVariant insertedVariant, long maxAmount, TransactionContext transaction) {
			CompressedBox.LOGGER.warn("Client side item handler called for inserting, this should not happen");
			if (!canInsert(slot, ((ItemVariantImpl) insertedVariant).getCachedStack())) {
				return 0;
			}

			long ret = super.insert(insertedVariant, maxAmount, transaction);
			return ret;
		}

		private boolean canInsert(int slot, ItemStack stack) {
			return super.canInsert(ItemVariant.of(stack));
		}

		@Override
		public long extract(ItemVariant variant, long maxAmount, TransactionContext transaction) {
			CompressedBox.LOGGER.warn("Client side item handler called for extracting, this should not happen");
			long ret = super.extract(variant, maxAmount, transaction);
			return ret;
		}


		@Override
		public int getCapacity(ItemVariant variant) {
			return Math.min(64, variant.getItem().getMaxStackSize());
		}

		@Override
		public void updateSnapshots(TransactionContext transaction) {
			storage.markDirtyParticipant.updateSnapshots(transaction);
			super.updateSnapshots(transaction);
		}

		@Override
		protected void releaseSnapshot(ItemStack snapshot) {
			lastReleasedSnapshot = snapshot;
		}

		@Override
		protected void onFinalCommit() {
			ItemStack original = lastReleasedSnapshot;
			ItemStack currentStack = getStack();

			if (!original.isEmpty() && original.getItem() == currentStack.getItem()) {
				// Components have changed, we need to copy the stack.
				if (!Objects.equals(original.getTag(), currentStack.getTag())) {
					// Remove all the existing components and copy the new ones on top.
					original.setTag(currentStack.getTag());
				}

				// None is empty and the items and components match: just update the amount, and reuse the original stack.
				original.setCount(currentStack.getCount());
				setStack(original);
			} else {
				// Otherwise assume everything was taken from original so empty it.
				original.setCount(0);
			}
		}

	}
}
