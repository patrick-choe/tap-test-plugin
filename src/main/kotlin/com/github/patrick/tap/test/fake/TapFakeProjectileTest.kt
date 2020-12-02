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

import com.github.noonmaru.tap.effect.playFirework
import com.github.noonmaru.tap.fake.FakeProjectile
import com.github.noonmaru.tap.fake.FakeProjectileManager
import com.github.noonmaru.tap.fake.Trail
import com.github.noonmaru.tap.math.normalizeAndLength
import com.github.noonmaru.tap.trail.trail
import com.github.patrick.tap.test.plugin.TapTestPlugin
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.FireworkEffect
import org.bukkit.FluidCollisionMode
import org.bukkit.GameMode
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.entity.Damageable
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask
import org.bukkit.util.BoundingBox
import java.util.function.Predicate
import kotlin.random.Random

/**
 * 해당 코드는 noonmaru 님이 제작하신 psychics 의 예시 능력으로 들어간 straight-shuriken 의
 * StraightShuriken.kt 의 코드를 참조하였습니다. (launch 부분에 ShurikenProjectile 코드를 간략하게 넣음)
 */
internal object TapFakeProjectileTest {
    private val managerDelegate = lazy {
        FakeProjectileManager().apply {
            updateTask = Bukkit.getScheduler().runTaskTimer(TapTestPlugin.instance, Runnable {
                update()
            }, 0L, 1L)
        }
    }

    private val fakeProjectileManager by managerDelegate

    private lateinit var updateTask: BukkitTask

    internal fun launch(target: Player) {
        val projectile = object : FakeProjectile(100, 256.0) {
            override fun onTrail(trail: Trail) {
                trail.velocity?.let {
                    val from = trail.from
                    val to = trail.to
                    val world = from.world
                    val length = velocity.normalizeAndLength()
                    val filter = Predicate<Entity> { entity ->
                        when (entity) {
                            target -> false
                            is Player -> {
                                entity.gameMode == GameMode.SURVIVAL || entity.gameMode == GameMode.ADVENTURE
                            }
                            is LivingEntity -> true
                            else -> false
                        }
                    }

                    world.rayTrace(
                        from,
                        velocity,
                        length,
                        FluidCollisionMode.NEVER,
                        true,
                        1.0,
                        filter
                    )?.let { result ->
                        remove()

                        val hitPosition = result.hitPosition
                        val hitLocation = hitPosition.toLocation(world)
                        val box = BoundingBox.of(hitPosition, 3.0, 3.0, 3.0)
                        val firework = FireworkEffect
                            .builder()
                            .with(FireworkEffect.Type.BALL_LARGE)
                            .withColor(Color.AQUA)
                            .build()

                        world.playFirework(hitLocation, firework)
                        world.getNearbyEntities(box, filter).forEach { entity ->
                            (entity as? Damageable)?.damage(6.0, target)
                        }
                    }

                    trail(from, to, 0.25) { trailWorld, x, y, z ->
                        trailWorld.spawnParticle(
                            Particle.CRIT_MAGIC,
                            x,
                            y,
                            z,
                            5,
                            0.1,
                            0.1,
                            0.1,
                            0.25,
                            null,
                            true
                        )
                    }

                    world.playSound(
                        to,
                        Sound.ENTITY_BLAZE_SHOOT,
                        SoundCategory.MASTER,
                        0.25F,
                        1.8F + Random.nextFloat() * 0.2F
                    )
                }
            }

            override fun onPostUpdate() {
                velocity = velocity.multiply(1.05)
            }
        }

        fakeProjectileManager.launch(target.eyeLocation, projectile)

        projectile.velocity = target.eyeLocation.direction.normalize().multiply(5.0)
    }

    internal fun clear() {
        fakeProjectileManager.clear()
    }
}