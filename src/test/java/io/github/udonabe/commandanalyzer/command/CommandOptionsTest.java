/*
 * Copyright (c) 2025 Command-Analyzer Contributors.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 */

package io.github.udonabe.commandanalyzer.command;

import io.github.udonabe.commandanalyzer.option.Option;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

class CommandOptionsTest {
    @Test
    void builder() {
        assertNotNull(CommandOptions.generator(null));
    }
}