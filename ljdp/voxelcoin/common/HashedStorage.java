package ljdp.voxelcoin.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.item.ItemStack;

public class HashedStorage {
	HashMap<Integer,ItemStack> inventory = new HashMap();
	
	public void addStack(ItemStack stack) {
		int hashcode = stack.hashCode();
		inventory.put(hashcode, stack);
	}
	
	public ItemStack getStack(int hashcode) {
		return inventory.get(hashcode);
	}
	
	public ItemStack decrStackSize(int hashcode, int amount) {
		ItemStack stack = getStack(hashcode);
		if(stack != null) {
			ItemStack returnStack = stack.copy();
			if(stack.stackSize > amount) {
				returnStack.stackSize = amount;
				stack.stackSize -= amount;
			} else if(stack.stackSize <= amount) {
				inventory.remove(hashcode);
			}
			return returnStack;
		}
		return null;
	}
}
