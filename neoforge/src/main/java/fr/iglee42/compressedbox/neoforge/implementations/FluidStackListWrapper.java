package fr.iglee42.compressedbox.neoforge.implementations;

import dev.architectury.hooks.fluid.forge.FluidStackHooksForge;
import fr.iglee42.compressedbox.CompressedBox;
import fr.iglee42.compressedbox.containers.fluids.ConnectedTankHandler;
import fr.iglee42.compressedbox.containers.fluids.SimpleFluidContainer;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.List;

public class FluidStackListWrapper implements IFluidHandler {

    private final List<dev.architectury.fluid.FluidStack> tanks;

    public FluidStackListWrapper(List<dev.architectury.fluid.FluidStack> tanks) {
        this.tanks = tanks;
    }

    @Override
    public int getTanks() {
        return tanks.size();
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        return FluidStackHooksForge.toForge(tanks.get(tank));
    }

    @Override
    public int getTankCapacity(int tank) {
        return (int) SimpleFluidContainer.MAX_FLUID;
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return true;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        CompressedBox.LOGGER.warn("Client side fluid handler called for filling, this should not happen");
        if (resource.isEmpty())
            return 0;

        int filled = 0;

        for (int i = 0; i < getTanks(); i++) {

            FluidStack existing = getFluidInTank(i);

            if (existing.isEmpty()) {

                int toFill = Math.min(resource.getAmount(), getTankCapacity(i));

                if (action.execute()) {
                    FluidStack copy = resource.copy();
                    copy.setAmount(toFill);
                    tanks.set(i, FluidStackHooksForge.fromForge(copy));
                }

                filled += toFill;
                return filled;
            }

            if (FluidStack.isSameFluidSameComponents(existing, resource)) {

                int space = getTankCapacity(i) - existing.getAmount();
                if (space <= 0)
                    continue;

                int toFill = Math.min(space, resource.getAmount());

                if (action.execute()) {
                    existing.grow(toFill);
                    tanks.set(i, FluidStackHooksForge.fromForge(existing));
                }

                filled += toFill;
                return filled;
            }
        }

        return filled;
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        CompressedBox.LOGGER.warn("Client side fluid handler called for draining with FluidStack, this should not happen");
        if (resource.isEmpty())
            return FluidStack.EMPTY;

        for (int i = 0; i < getTanks(); i++) {

            FluidStack existing = getFluidInTank(i);

            if (existing.isEmpty())
                continue;

            if (!FluidStack.isSameFluidSameComponents(existing, resource))
                continue;

            int toDrain = Math.min(existing.getAmount(), resource.getAmount());

            if (action.execute()) {
                return FluidStackHooksForge.toForge(tanks.set(i,FluidStackHooksForge.fromForge(existing.copyWithAmount(existing.getAmount() - toDrain))));
            } else {
                FluidStack copy = existing.copy();
                copy.setAmount(toDrain);
                return copy;
            }
        }

        return FluidStack.EMPTY;
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        CompressedBox.LOGGER.warn("Client side fluid handler called for draining with max amount, this should not happen");
        if (maxDrain <= 0)
            return FluidStack.EMPTY;

        for (int i = 0; i < getTanks(); i++) {

            FluidStack existing = getFluidInTank(i);

            if (existing.isEmpty())
                continue;

            int toDrain = Math.min(existing.getAmount(), maxDrain);

            if (action.execute()) {
                return FluidStackHooksForge.toForge(tanks.set(i,FluidStackHooksForge.fromForge(existing.copyWithAmount(existing.getAmount() - toDrain))));
            } else {
                FluidStack copy = existing.copy();
                copy.setAmount(toDrain);
                return copy;
            }
        }

        return FluidStack.EMPTY;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FluidStackListWrapper other)) return false;
        return tanks.equals(other.tanks);
    }

    @Override
    public int hashCode() {
        return tanks.hashCode();
    }
}
