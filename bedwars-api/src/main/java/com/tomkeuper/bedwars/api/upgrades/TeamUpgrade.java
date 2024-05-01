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

package com.tomkeuper.bedwars.api.upgrades;

/**
 * This interface represents a team upgrade in a bed wars mini-game.
 */
public interface TeamUpgrade {

    /**
     * Get the name of the team upgrade.
     *
     * @return The name of the team upgrade.
     */
    String getName();

    /**
     * Get the total number of tiers available for the team upgrade.
     *
     * @return The number of tiers for the team upgrade.
     */
    int getTierCount();
}