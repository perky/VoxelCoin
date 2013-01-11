package ljdp.voxelcoin.common;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import buildcraft.core.CommandBuildCraft;

import com.google.common.eventbus.Subscribe;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraft.src.ModLoader;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import ljdp.minechem.common.network.PacketHandler;
import ljdp.voxelcoin.common.command.BalanceCommand;
import ljdp.voxelcoin.common.command.BuyCommand;
import ljdp.voxelcoin.common.command.CancelOrderCommand;
import ljdp.voxelcoin.common.command.ListOrdersCommand;
import ljdp.voxelcoin.common.command.MyOrdersCommand;
import ljdp.voxelcoin.common.command.SellCommand;
import ljdp.voxelcoin.common.command.TestCommand;
import ljdp.voxelcoin.common.command.TransactionLogCommand;
import ljdp.voxelcoin.common.network.NetworkManager;
import ljdp.voxelcoin.server.database.Database;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.Mod.ServerStarting;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.server.FMLServerHandler;

@Mod(modid="ljdp|voxelcoin", name="VoxelCoin", version="0.0.0")
@NetworkMod(
		clientSideRequired=true, 
		serverSideRequired=false, 
		channels={"Blockcoin"}, 
		packetHandler=NetworkManager.class
)
public class VoxelCoinMain {
	@Instance("ljdp|blockcoin")
	public static VoxelCoinMain instance;
	public static final String BLOCKCOIN_DIR = "/blockcoin";
	public static final String BLOCKCOIN_DATABASE_NAME = "Blockcoin.db";
	public static Database database;
	
	@PreInit
	public void preInit(FMLPreInitializationEvent event) {
		
	}
	
	@Init
	public void init(FMLInitializationEvent event) {
		//initDatabase(BLOCKCOIN_DATABASE_NAME);
	}
	
	@PostInit
	public void postInit(FMLPostInitializationEvent event) {
		
	}
	
	@ServerStarting
    public void serverStarting(FMLServerStartingEvent event)
    {
		CommandHandler commandManager = (CommandHandler) event.getServer().getCommandManager();
		commandManager.registerCommand(new SellCommand());
		commandManager.registerCommand(new BuyCommand());
		commandManager.registerCommand(new CancelOrderCommand());
		commandManager.registerCommand(new ListOrdersCommand());
		commandManager.registerCommand(new BalanceCommand());
		commandManager.registerCommand(new TransactionLogCommand());
		commandManager.registerCommand(new CancelOrderCommand());
		commandManager.registerCommand(new MyOrdersCommand());
    }
	
	private void initDatabase(String databaseName) {
		File minecraftDir = Minecraft.getMinecraftDir();
		File blockcoinDir = new File(minecraftDir.getAbsolutePath() + BLOCKCOIN_DIR);
		if(!blockcoinDir.exists())
			blockcoinDir.mkdir();
		Path databasePath = FileSystems.getDefault().getPath(blockcoinDir.getAbsolutePath(), databaseName);
		try {
			database = new Database(databasePath);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
