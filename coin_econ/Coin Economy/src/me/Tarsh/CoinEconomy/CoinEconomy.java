package me.Tarsh.CoinEconomy;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;

public class CoinEconomy extends JavaPlugin{
	
	Server server;
	private static final Logger log = Logger.getLogger("Minecraft");
	public static Economy econ = null;
	public static String prefix = "" + ChatColor.DARK_GREEN + ChatColor.BOLD + "HYVERIA>> ";
	
	@Override
	public void onEnable() {
		server = this.getServer();
		getServer().getPluginManager().registerEvents(new CoinListener(), this);
		if (!setupEconomy()) {
			log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		getCommand("withdraw").setExecutor(this);
	}

	@Override
	public void onDisable() {
		log.severe("Coin Economy Shutting Down!");

	}
	

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}

	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		if (!(sender instanceof Player)) {
			log.info(ChatColor.RED + "You cannot start " + ChatColor.AQUA + "Puffer Party " + ChatColor.RED
					+ "through console!");
			return true;
		}
		
		Player player = (Player) sender;
		if (command.getName().equalsIgnoreCase("withdraw")) {
			if (args[0] == null) {
				sender.sendMessage(CoinEconomy.prefix + ChatColor.RED + "Usage: /withdraw [amount]");
				return false;
			}
			
			double withdraw = Double.parseDouble(args[0]);
			if (withdraw <= econ.getBalance(player)) {
				econ.withdrawPlayer(player, withdraw);
				CoinListener.giveCoins(player, (int)withdraw);
				sender.sendMessage(CoinEconomy.prefix + ChatColor.GREEN + "Coins Withdrew");
			}
			else {
				sender.sendMessage(CoinEconomy.prefix + ChatColor.RED + "Insufficient Funds");
			}
		}
		
		return true;
	}
	
	
}
