package com.andrei1058.bedwars.shop;

import com.andrei1058.bedwars.api.ShopBuyEvent;
import com.andrei1058.bedwars.api.TeamColor;
import com.andrei1058.bedwars.arena.Arena;
import com.andrei1058.bedwars.arena.BedWarsTeam;
import com.andrei1058.bedwars.configuration.Language;
import com.andrei1058.bedwars.configuration.Messages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static com.andrei1058.bedwars.Main.getEconomy;
import static com.andrei1058.bedwars.Main.nms;
import static com.andrei1058.bedwars.configuration.Language.getMsg;

public class BuyItemsAction extends ContentAction {

    private String currency;
    private List<ShopItem> items = new ArrayList<>();
    int cost = 0;
    private CategoryContent categoryContent;

    public BuyItemsAction(int price, String currency, CategoryContent parent) {
        this.cost = price;
        this.currency = currency;
        this.categoryContent = parent;
    }

    @Override
    public void doStuff(Player p) {
        int money = 0;
        Material currency = null;
        if (getCurrency().equalsIgnoreCase("iron")) {
            currency = Material.IRON_INGOT;
        }
        if (getCurrency().equalsIgnoreCase("gold")) {
            currency = Material.GOLD_INGOT;
        }
        if (getCurrency().equalsIgnoreCase("emerald")) {
            currency = Material.EMERALD;
        }
        if (getCurrency().equalsIgnoreCase("diamond")) {
            currency = Material.DIAMOND;
        }
        if (getCurrency().equalsIgnoreCase("vault")) {
            if (!getEconomy().isEconomy()) {
                p.sendMessage("§cThis item requires vault support!");
                return;
            } else {
                money = (int) getEconomy().getMoney(p);
            }
        } else if (currency != null) {
            for (ItemStack i : p.getInventory().getContents()) {
                if (i == null) continue;
                if (i.getType() == null) continue;
                if (i.getType() == Material.AIR) continue;
                if (i.getType() == currency) {
                    money += i.getAmount();
                }
            }
        }
        for (ItemStack i : p.getInventory().getArmorContents()) {
            if (!nms.isCustomBedWarsItem(i)) continue;
            for (ShopItem i2 : items) {
                if (nms.isCustomBedWarsItem(i2.getItemStack())) {
                    if (nms.getCustomData(i).equals(nms.getCustomData(i2.getItemStack()))){
                        p.sendMessage(getMsg(p, Messages.SHOP_ALREADY_BOUGHT));
                        return;
                    }
                }
            }
        }
        if (money < getCost()) {
            p.playSound(p.getLocation(), nms.insufficientMoney(), 1f, 1f);
            p.sendMessage(getMsg(p, Messages.SHOP_INSUFFICIENT_MONEY).replace("{currency}", getMsg(p, "meaning." + getCurrency().toLowerCase())).replace("{amount}", String.valueOf(getCost() - money)));
            return;
        }
        boolean done = false;
        if (currency == null) {
            getEconomy().buyAction(p, getCost());
        } else {
            int costt = cost;
            for (ItemStack i : p.getInventory().getContents()) {
                if (done) break;
                if (i == null) continue;
                if (i.getType() == null) continue;
                if (i.getType() == Material.AIR) continue;
                if (i.getType() == currency) {
                    if (i.getAmount() < costt) {
                        costt -= i.getAmount();
                        nms.minusAmount(p, i, i.getAmount());
                        p.updateInventory();
                    } else {
                        nms.minusAmount(p, i, costt);
                        p.updateInventory();
                        done = true;
                    }
                }
            }
        }
        p.playSound(p.getLocation(), nms.bought(), 1f, 1f);
        getCategoryContent().getShopCategory().openToPlayer(p);
        p.sendMessage(getMsg(p, Messages.SHOP_NEW_PURCHASE).replace("{item}", ChatColor.stripColor(getMsg(p, getCategoryContent().getShopCategory().getName().replace("main.", Messages.SHOP_PATH) + "." + getCategoryContent().getName() + ".name"))));
        BedWarsTeam.PlayerVault pv = BedWarsTeam.getVault(p);

        //Call buy event
        Bukkit.getPluginManager().callEvent(new ShopBuyEvent(this, p));
        //

        for (ShopItem si : getItems()) {
            if (si.isPermanent() && pv != null) {
                int slot = 0;
                for (ItemStack i : pv.getInvItems()) {
                    slot++;
                    if (nms.isSword(si.getItemStack()) && nms.isSword(i)) {
                        if (nms.getDamage(si.getItemStack()) >= nms.getDamage(i)) {
                            pv.getInvItems().add(slot, si.getItemStack());
                        }
                    }
                }
                if (nms.isArmor(si.getItemStack())) {
                    Material M = si.getItemStack().getType();
                    if (M == Material.LEATHER_HELMET || M == Material.CHAINMAIL_HELMET || M == Material.DIAMOND_HELMET || M == Material.GOLD_HELMET || M == Material.IRON_HELMET) {
                        if (nms.getProtection(si.getItemStack()) >= nms.getProtection(pv.getHelmet())) {
                            pv.setHelmet(si.getItemStack());
                        }
                    } else if (M == Material.LEATHER_CHESTPLATE || M == Material.CHAINMAIL_CHESTPLATE || M == Material.GOLD_CHESTPLATE || M == Material.DIAMOND_CHESTPLATE || M == Material.IRON_CHESTPLATE) {
                        if (nms.getProtection(si.getItemStack()) >= nms.getProtection(pv.getChestplate())) {
                            pv.setChestplate(si.getItemStack());
                        }
                    } else if (M == Material.LEATHER_LEGGINGS || M == Material.CHAINMAIL_LEGGINGS || M == Material.DIAMOND_LEGGINGS || M == Material.GOLD_LEGGINGS || M == Material.IRON_LEGGINGS) {
                        if (nms.getProtection(si.getItemStack()) >= nms.getProtection(pv.getPants())) {
                            pv.setPants(si.getItemStack());
                        }
                    } else {
                        if (nms.getProtection(si.getItemStack()) >= nms.getProtection(pv.getBoots())) {
                            pv.setBoots(si.getItemStack());
                        }
                    }
                }

            }
            if (si.isAutoequip() && nms.isArmor(si.getItemStack())) {
                Material M = si.getItemStack().getType();
                if (M == Material.LEATHER_HELMET || M == Material.CHAINMAIL_HELMET || M == Material.DIAMOND_HELMET || M == Material.GOLD_HELMET || M == Material.IRON_HELMET) {
                    p.getInventory().setHelmet(si.getItemStack());
                } else if (M == Material.LEATHER_CHESTPLATE || M == Material.CHAINMAIL_CHESTPLATE || M == Material.GOLD_CHESTPLATE || M == Material.DIAMOND_CHESTPLATE || M == Material.IRON_CHESTPLATE) {
                    p.getInventory().setChestplate(si.getItemStack());
                } else if (M == Material.LEATHER_LEGGINGS || M == Material.CHAINMAIL_LEGGINGS || M == Material.DIAMOND_LEGGINGS || M == Material.GOLD_LEGGINGS || M == Material.IRON_LEGGINGS) {
                    p.getInventory().setLeggings(si.getItemStack());
                } else {
                    p.getInventory().setBoots(si.getItemStack());
                }
                continue;
            } else {
                for (ItemStack i : p.getInventory().getContents()) {
                    if (i == null) continue;
                    if (i.getType() == Material.AIR) continue;
                    if (nms.isSword(si.getItemStack()) && nms.isSword(i)) {
                        if (nms.getDamage(si.getItemStack()) >= nms.getDamage(i)) {
                            p.getInventory().remove(i);
                            p.getInventory().addItem(si.getItemStack());
                            updateEnchantments(p);
                            return;
                        }
                    }
                }
                ItemStack i = si.getItemStack();
                if (si.getItemStack().getType() == Material.WOOL || si.getItemStack().getType() == Material.STAINED_CLAY ||
                        si.getItemStack().getType() == Material.STAINED_GLASS) {
                    i = new ItemStack(i.getType(), i.getAmount(), TeamColor.itemColor(Arena.getArenaByPlayer(p).getTeam(p).getColor()));
                }
                p.getInventory().addItem(i);
            }

        }
        updateEnchantments(p);
        p.updateInventory();
    }

    /**
     * Update a player enchantments
     */
    private static void updateEnchantments(Player p) {
        if (!Arena.getArenaByPlayer(p).getTeam(p).getBowsEnchantments().isEmpty()) {
            for (ItemStack i : p.getInventory().getContents()) {
                if (i == null) continue;
                if (i.getType() == Material.BOW) {
                    ItemMeta im = i.getItemMeta();
                    for (BedWarsTeam.Enchant e : Arena.getArenaByPlayer(p).getTeam(p).getBowsEnchantments()) {
                        im.addEnchant(e.getEnchantment(), e.getAmplifier(), true);
                    }
                    i.setItemMeta(im);
                }
                p.updateInventory();
            }
        }
        if (!Arena.getArenaByPlayer(p).getTeam(p).getSwordsEnchantemnts().isEmpty()) {
            for (ItemStack i : p.getInventory().getContents()) {
                if (i == null) continue;
                if (nms.isSword(i)) {
                    ItemMeta im = i.getItemMeta();
                    for (BedWarsTeam.Enchant e : Arena.getArenaByPlayer(p).getTeam(p).getSwordsEnchantemnts()) {
                        im.addEnchant(e.getEnchantment(), e.getAmplifier(), true);
                    }
                    i.setItemMeta(im);
                }
                p.updateInventory();
            }
        }
        if (!Arena.getArenaByPlayer(p).getTeam(p).getArmorsEnchantemnts().isEmpty()) {
            for (ItemStack i : p.getInventory().getArmorContents()) {
                if (i == null) continue;
                if (nms.isArmor(i)) {
                    ItemMeta im = i.getItemMeta();
                    for (BedWarsTeam.Enchant e : Arena.getArenaByPlayer(p).getTeam(p).getArmorsEnchantemnts()) {
                        im.addEnchant(e.getEnchantment(), e.getAmplifier(), true);
                    }
                    i.setItemMeta(im);
                }
                p.updateInventory();
            }
        }
    }

    public CategoryContent getCategoryContent() {
        return categoryContent;
    }

    @Override
    public int getCost() {
        return cost;
    }

    public void addItem(ShopItem shopItem) {
        this.items.add(shopItem);
    }

    public String getCurrency() {
        return currency;
    }

    public List<ShopItem> getItems() {
        return items;
    }
}
