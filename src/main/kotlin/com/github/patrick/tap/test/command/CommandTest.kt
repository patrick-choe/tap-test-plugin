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

package com.github.patrick.tap.test.command

import com.github.noonmaru.kommand.KommandBuilder
import com.github.patrick.tap.test.event.TapEventTest
import com.github.patrick.tap.test.fake.TapFakeEntityTest
import com.github.patrick.tap.test.fake.TapFakeProjectileTest
import org.bukkit.entity.Player

internal object CommandTest {
    internal fun register(builder: KommandBuilder) {
        builder.apply {
            then("event") {
                then("on") {
                    require {
                        this is Player
                    }

                    executes {
                        TapEventTest.on(it.sender as Player)
                    }
                }

                then("off") {
                    executes {
                        TapEventTest.off()
                    }
                }
            }

            then("fake") {
                then("entity") {
                    then("on") {
                        require {
                            this is Player
                        }

                        executes {
                            TapFakeEntityTest.on(it.sender as Player)
                        }
                    }

                    then("off") {
                        executes {
                            TapFakeEntityTest.off()
                        }
                    }
                }

                then("projectile") {
                    require {
                        this is Player
                    }

                    executes {
                        TapFakeProjectileTest.launch(it.sender as Player)
                    }
                }
            }
        }
    }
}