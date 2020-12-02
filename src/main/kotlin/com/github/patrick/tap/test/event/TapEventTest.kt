/*
 * Copyright (C) 2020 PatrickKR
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
 * Contact me on <mailpatrickkr@gmail.com>
 */

package com.github.patrick.tap.test.event

import com.github.noonmaru.tap.event.EntityEventManager
import com.github.patrick.tap.test.plugin.TapTestPlugin
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent

internal object TapEventTest {
    private val managerDelegate = lazy {
        EntityEventManager(TapTestPlugin.instance)
    }

    private val entityEventManager by managerDelegate

    private val listener by lazy {
        object : Listener {
            @EventHandler
            fun onPlayerInteract(event: PlayerInteractEvent) {
                event.player.sendMessage(
                    "PlayerInteractEvent action:${event.action} item:${event.item} block:${event.clickedBlock}"
                )
            }
        }
    }

    private var currentTarget: Player? = null

    internal fun on(target: Player) {
        val current = currentTarget
        if (current != null) {
            entityEventManager.unregisterEvent(current, listener)
        }

        currentTarget = target
        entityEventManager.registerEvents(target, listener)
    }

    internal fun off() {
        val current = currentTarget
        if (current != null) {
            entityEventManager.unregisterEvent(current, listener)
        }

        currentTarget = null
    }

    internal fun clear() {
        if (managerDelegate.isInitialized()) {
            entityEventManager.unregisterAll()
        }
    }
}