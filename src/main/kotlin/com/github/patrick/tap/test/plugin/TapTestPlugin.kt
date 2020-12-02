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

package com.github.patrick.tap.test.plugin

import com.github.noonmaru.kommand.kommand
import com.github.patrick.tap.test.command.CommandTest
import com.github.patrick.tap.test.event.TapEventTest
import com.github.patrick.tap.test.fake.TapFakeEntityTest
import com.github.patrick.tap.test.fake.TapFakeProjectileTest
import org.bukkit.plugin.java.JavaPlugin

class TapTestPlugin : JavaPlugin() {
    override fun onEnable() {
        instance = this

        kommand {
            register("test") {
                CommandTest.register(this)
            }
        }
    }

    override fun onDisable() {
        TapEventTest.clear()
        TapFakeEntityTest.clear()
        TapFakeProjectileTest.clear()
    }

    companion object {
        lateinit var instance: TapTestPlugin
            private set
    }
}