/*
 * Copyright (c) 2025 Command-Analyzer Contributors.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 */

package io.github.udonabe.commandanalyzer.option;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class OptionTest {

    @Test
    void testInstantiate() {
        Option opt = new Option.TestOption(
                Set.of(new OptionDisplay(OptionDisplay.PrefixKind.SHORT_OPTION, "e"),
                        new OptionDisplay(OptionDisplay.PrefixKind.LONG_OPTION, "example")),
                ArgType.NONE,
                true,
                "This is a test option.",
                "test-option");
        assertEquals(Set.of(new OptionDisplay(OptionDisplay.PrefixKind.SHORT_OPTION, "e"),
                new OptionDisplay(OptionDisplay.PrefixKind.LONG_OPTION, "example")), opt.displays());
        assertEquals(ArgType.NONE, opt.type());
        assertTrue(opt.required());
        assertEquals("This is a test option.", opt.description());
        assertEquals("test-option", opt.managementName());
    }

    @Test
    void testEquals() {
        Option opt = new Option.TestOption(
                Set.of(new OptionDisplay(OptionDisplay.PrefixKind.SHORT_OPTION, "e"),
                        new OptionDisplay(OptionDisplay.PrefixKind.LONG_OPTION, "example")),
                ArgType.NONE,
                true,
                "This is a test option.",
                "test-option");
        Option equal = new Option.TestOption(
                Set.of(new OptionDisplay(OptionDisplay.PrefixKind.SHORT_OPTION, "e"),
                        new OptionDisplay(OptionDisplay.PrefixKind.LONG_OPTION, "example")),
                ArgType.NONE,
                true,
                "This is a test option.",
                "test-option");
        assertEquals(opt, equal);
    }

    @Test
    void testHashCode() {
        Option opt = new Option.TestOption(
                Set.of(new OptionDisplay(OptionDisplay.PrefixKind.SHORT_OPTION, "e"),
                        new OptionDisplay(OptionDisplay.PrefixKind.LONG_OPTION, "example")),
                ArgType.NONE,
                true,
                "This is a test option.",
                "test-option");
        Option equal = new Option.TestOption(
                Set.of(new OptionDisplay(OptionDisplay.PrefixKind.SHORT_OPTION, "e"),
                        new OptionDisplay(OptionDisplay.PrefixKind.LONG_OPTION, "example")),
                ArgType.NONE,
                true,
                "This is a test option.",
                "test-option");
        assertEquals(opt.hashCode(), equal.hashCode());
    }

    @Test
    void testToString() {
        Option opt = new Option.TestOption(
                Set.of(new OptionDisplay(OptionDisplay.PrefixKind.SHORT_OPTION, "e"),
                        new OptionDisplay(OptionDisplay.PrefixKind.LONG_OPTION, "example")),
                ArgType.NONE,
                true,
                "This is a test option.",
                "test-option");

        final StringBuilder sb = new StringBuilder("Option{");
        sb.append("displays=").append(Set.of(new OptionDisplay(OptionDisplay.PrefixKind.SHORT_OPTION, "e"),
                new OptionDisplay(OptionDisplay.PrefixKind.LONG_OPTION, "example")));
        sb.append(", type=").append(ArgType.NONE);
        sb.append(", required=").append(true);
        sb.append(", description='").append("This is a test option.").append('\'');
        sb.append(", managementName='").append("test-option").append('\'');
        sb.append('}');

        assertEquals(sb.toString(), opt.toString());
    }

    @Test
    void testClone() {
        Option opt = new Option.TestOption(
                Set.of(new OptionDisplay(OptionDisplay.PrefixKind.SHORT_OPTION, "e"),
                        new OptionDisplay(OptionDisplay.PrefixKind.LONG_OPTION, "example")),
                ArgType.NONE,
                true,
                "This is a test option.",
                "test-option");
        Option cloned = opt.clone();
        assertEquals(opt, cloned);
        assertNotSame(opt, cloned);
    }
}