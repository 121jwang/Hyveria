package me.Tarsh.CoinEconomy;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.ess3.api.events.UserBalanceUpdateEvent;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_14_R1.NBTTagCompound;
import net.minecraft.server.v1_14_R1.NBTTagList;

public class CoinListener implements Listener{
	
	int coinPouch = 1000;
	
	static ItemStack coins = setMeta(new ItemStack(Material.SUNFLOWER), ChatColor.YELLOW + "COINS", Arrays.asList(ChatColor.LIGHT_PURPLE + "$1", "Right Click To Deposit"));
	
	@EventHandler
    public void onUpdate(UserBalanceUpdateEvent event) {
		double bal = event.getNewBalance().doubleValue();
		if (bal > coinPouch) {
			//event.getPlayer().sendMessage("" + CoinEconomy.econ.getBalance((event.getPlayer())));
			
			// Set the balance to 1000, give the rest in coin value
			double toGive = bal - coinPouch;
			event.setNewBalance(new BigDecimal(coinPouch));
			// Convert toGive to coins
			int counter = 0;
			for (int i = 0; i < toGive; i++) {
				if (event.getPlayer().getInventory().firstEmpty() != -1)
					event.getPlayer().getInventory().addItem(coins);
				else if (counter < coinPouch){
					event.getPlayer().getWorld().dropItem(event.getPlayer().getLocation(), coins);
					counter++;
				}
			}
		}
	}
	
	public static ItemStack setMeta(ItemStack material, String name, List<String> lore) {
		ItemMeta im = material.getItemMeta();
		im.setDisplayName(name);
		im.setLore(lore);
		
		material.setItemMeta(im);
		return material;
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority=EventPriority.HIGH)
	public void onPlayerUse(PlayerInteractEvent event){
	    Player p = event.getPlayer();
	    double toGive = p.getItemInHand().getAmount();
	    double oldBal = CoinEconomy.econ.getBalance(p);
	    if(p.getItemInHand().getType() == Material.SUNFLOWER && p.getItemInHand().getItemMeta().hasLore()){
	        if (toGive + CoinEconomy.econ.getBalance(p) > coinPouch) {
	        	while (CoinEconomy.econ.getBalance(p) < coinPouch) {
	        		p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
		        	CoinEconomy.econ.depositPlayer(p, 1);
	        	}
	        }
	        else {
		        p.getItemInHand().setAmount(0);
	        	CoinEconomy.econ.depositPlayer(p, toGive);
        	}
	        p.updateInventory();
	        
	        if (CoinEconomy.econ.getBalance(p) != oldBal)
		    	p.sendMessage(CoinEconomy.prefix + ChatColor.GREEN + "Coins Deposited");
		    else {
		    	p.sendMessage(CoinEconomy.prefix + ChatColor.RED + "Coin Pouch Full");
		    }
	    }
	    
	}
	
	
	public static void giveCoins(Player player, int toGive) {
		for (int i = 0; i < toGive; i++) {
			if (player.getInventory().firstEmpty() != -1)
				player.getInventory().addItem(coins);
			else
				player.getWorld().dropItem(player.getLocation(), coins);
		}
	}
}
