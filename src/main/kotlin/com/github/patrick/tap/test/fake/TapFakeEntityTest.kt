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

package com.github.patrick.tap.test.fake

import com.github.noonmaru.tap.fake.FakeEntity
import com.github.noonmaru.tap.fake.FakeEntityServer
import com.github.noonmaru.tap.fake.invisible
import com.github.patrick.tap.test.plugin.TapTestPlugin
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitTask

internal object TapFakeEntityTest {
    private val serverDelegate = lazy {
        FakeEntityServer.create(TapTestPlugin.instance).apply {
            Bukkit.getPluginManager().registerEvents(object : Listener {
                @EventHandler
                fun onPlayerJoin(event: PlayerJoinEvent) {
                    addPlayer(event.player)
                }

                @EventHandler
                fun onPlayerQuit(event: PlayerQuitEvent) {
                    removePlayer(event.player)
                }
            }, TapTestPlugin.instance)

            Bukkit.getOnlinePlayers().forEach { player ->
                addPlayer(player)
            }

            serverUpdateTask = Bukkit.getScheduler().runTaskTimer(TapTestPlugin.instance, Runnable {
                update()
            }, 0L, 1L)
        }
    }

    private val fakeEntityServer by serverDelegate

    private var fakeEntity: FakeEntity? = null

    private var entityUpdateTask: BukkitTask? = null

    private lateinit var serverUpdateTask: BukkitTask

    internal fun on(target: Player) {
        fakeEntity?.remove()

        entityUpdateTask?.cancel()

        fakeEntity = fakeEntityServer.spawnEntity(target.location, ArmorStand::class.java).apply {
            updateMetadata<ArmorStand> {
                invisible = true
                isGlowing = true
                isMarker = true
            }

            updateEquipment {
                setItem(EquipmentSlot.HEAD, ItemStack(Material.STONE))
            }

            entityUpdateTask = Bukkit.getScheduler().runTaskTimer(TapTestPlugin.instance, Runnable {
                moveTo(target.location.apply {
                    add(direction.multiply(4.5))
                })
            }, 0L, 1L)
        }
    }

    internal fun off() {
        fakeEntity?.remove()

        entityUpdateTask?.cancel()
    }

    internal fun clear() {
        if (serverDelegate.isInitialized()) {
            fakeEntity?.remove()

            entityUpdateTask?.cancel()

            serverUpdateTask.cancel()

            fakeEntityServer.entities.forEach { entity ->
                entity.remove()
            }

            fakeEntityServer.shutdown()
        }
    }
}