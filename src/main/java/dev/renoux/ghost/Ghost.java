/*
 * MIT License
 *
 * Copyright (c) 2023 Simon RENOUX aka fantomitechno
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.renoux.ghost;

import dev.renoux.ghost.load.Events;
import dev.renoux.ghost.load.ModRegistries;
import net.minecraft.server.MinecraftServer;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.ModMetadata;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ghost implements ModInitializer {
  public static ModMetadata metadata;
  public static Logger LOGGER;

  public static MinecraftServer server;

  public static double BORDER = 0.14;

  @Override
  public void onInitialize(ModContainer mod) {
    metadata = mod.metadata();
    LOGGER = LoggerFactory.getLogger(metadata.id());

    LOGGER.info("{} : LOADING", metadata.name());

    ModRegistries.init();

    LOGGER.info("{} : ModRegistries loaded", metadata.name());

    Events.register();

    LOGGER.info("{} : Events loaded", metadata.name());

    LOGGER.info("{} : LOADED", metadata.name());
  }
}
