package ljdp.voxelcoin.server.database;

import ljdp.voxelcoin.server.database.datatype.NBTTagCompoundPersister;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "items")
public class DBOItem extends DatabaseObject {
	
	public static final String ITEMID_FIELD = "itemID";
	public static final String ITEMMDG_FIELD = "itemMdg";
	public static final String ITEMTAG_FIELD = "tagCompound";
	
	@DatabaseField(columnName = ITEMID_FIELD)
	public int itemID;
	
	@DatabaseField(columnName = ITEMMDG_FIELD)
	public int itemDmg;
	
	@DatabaseField(persisterClass = NBTTagCompoundPersister.class, columnName = ITEMTAG_FIELD, canBeNull = true)
	public NBTTagCompound tagCompound;
	
	public DBOItem() {
		
	}
	
	public DBOItem(ItemStack itemstack) {
		this.itemID = itemstack.itemID;
		this.itemDmg = itemstack.getItemDamage();
		this.tagCompound = itemstack.getTagCompound();
	}
	
	public ItemStack getItemStack(int amount) {
		ItemStack itemstack = new ItemStack(itemID, amount, itemDmg);
		if(tagCompound != null)
			itemstack.setTagCompound(tagCompound);
		return itemstack;
	}
	
}
