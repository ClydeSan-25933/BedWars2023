/*
 * BedWars2023 - A bed wars mini-game.
 * Copyright (C) 2024 Tomas Keuper
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Contact e-mail: contact@fyreblox.com
 */

package com.tomkeuper.bedwars.support.vault;

import com.tomkeuper.bedwars.api.economy.IEconomy;
import org.bukkit.entity.Player;

public class WithEconomy implements IEconomy {

    private static net.milkbowl.vault.economy.Economy economy;

    @Override
    public boolean isEconomy() {
        return true;
    }

    @Override
    public double getMoney(Player p) {
        return economy.getBalance(p);
    }

    @Override
    public void giveMoney(Player p, double money) {
        economy.depositPlayer (p, money);
    }

    @Override
    public void buyAction(Player p, double cost) {
        economy.bankWithdraw(p.getName(), cost);
    }

    public static void setEconomy(net.milkbowl.vault.economy.Economy economy) {
        WithEconomy.economy = economy;
    }
}
