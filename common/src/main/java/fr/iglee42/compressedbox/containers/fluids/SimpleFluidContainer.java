package fr.iglee42.compressedbox.containers.fluids;

import dev.architectury.fluid.FluidStack;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class SimpleFluidContainer {

	public static final long MAX_FLUID = 16000;
	private FluidStack storedFluid;

	public SimpleFluidContainer() {
		this.storedFluid = FluidStack.empty();
	}

	public SimpleFluidContainer(FluidStack fluid) {
		this.storedFluid = fluid;
	}

	public FluidStack getFluid() {
		return storedFluid;
	}

	public FluidStack removeFluid(long amount) {
		long min = Math.min(amount, storedFluid.getAmount());
		FluidStack fluidStack = storedFluid.copyWithAmount(min);
		storedFluid.shrink(amount);
		if (!fluidStack.isEmpty()) {
			this.setChanged();
		}

		return fluidStack;
	}


	public FluidStack removeFluidNoUpdate() {
		FluidStack fluidStack = storedFluid.copy();
		if (fluidStack.isEmpty()) {
			return FluidStack.empty();
		} else {
			this.storedFluid = FluidStack.empty();
			return fluidStack;
		}
	}


	public void setFluid( FluidStack fluidStack) {
		this.storedFluid = fluidStack;
		if (!storedFluid.isEmpty() && storedFluid.getAmount() >= MAX_FLUID)
			storedFluid.setAmount(MAX_FLUID);
		this.setChanged();
	}


	public boolean isEmpty() {
		return storedFluid.isEmpty();
	}

	public void setChanged() {
	}

	public boolean stillValid(Player player) {
		return true;
	}

	public void clearContent() {
		this.storedFluid = FluidStack.empty();
		this.setChanged();
	}


	public void fromTag(CompoundTag tag) {
		if (tag.isEmpty()) {
			clearContent();
			return;
		}
		FluidStack stack = FluidStack.read(tag);
		if (stack.isEmpty()){
			clearContent();
			return;
		}
		storedFluid = stack;
	}

	public Tag createTag() {
		if (storedFluid.isEmpty()) return new CompoundTag();
		return storedFluid.write(new CompoundTag());
	}



}
