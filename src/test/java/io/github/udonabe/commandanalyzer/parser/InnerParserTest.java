/*
 * Copyright (c) 2025 Command-Analyzer Contributors.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 */

package io.github.udonabe.commandanalyzer.parser;
import io.github.udonabe.commandanalyzer.OptionParseException;
import io.github.udonabe.commandanalyzer.command.CommandOptions;
import io.github.udonabe.commandanalyzer.option.ArgType;
import io.github.udonabe.commandanalyzer.option.Option;
import io.github.udonabe.commandanalyzer.option.OptionDisplay;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class InnerParserTest {
    @Test
    void testSubCommand() throws OptionParseException {
        CommandOptions options = CommandOptions.generator(Option.subCommand(
                Set.of("example", "test"),
                "Test Option",
                "mode"
        )).build();
        var result = InnerParser.parse(options.getSubCommand(), options.getNormalOptions(), options.getPositionalArgs(), List.of("example"));
        assertEquals("example", result.get("mode").rSubCommand());

        result = InnerParser.parse(options.getSubCommand(), options.getNormalOptions(), options.getPositionalArgs(), List.of("test"));
        assertEquals("test", result.get("mode").rSubCommand());
    }

    @Test
    void testNormalOption_normal() throws OptionParseException {
        CommandOptions options = CommandOptions.generator(null)
                .option(Option.normalOption(
                        Set.of(
                                new OptionDisplay(OptionDisplay.PrefixKind.SHORT_OPTION, "e")
                        ),
                        ArgType.STRING,
                        false,
                        "Test String Option",
                        "example"
                ))
                .build();
        var result = InnerParser.parse(options.getSubCommand(), options.getNormalOptions(), options.getPositionalArgs(), List.of("-e", "This is a test"));
        assertEquals("This is a test", result.get("example").rString());
    }

    @Test
    void testNormalOption_normal_present() throws OptionParseException {
        //オプションが無い場合、正しくpresent=falseで追加されるか
        CommandOptions options = CommandOptions.generator(null)
                .option(Option.normalOption(
                        Set.of(
                                new OptionDisplay(OptionDisplay.PrefixKind.SHORT_OPTION, "e")
                        ),
                        ArgType.NONE,
                        false,
                        "Test String Option",
                        "example"
                ))
                .build();
        var result = InnerParser.parse(options.getSubCommand(), options.getNormalOptions(), options.getPositionalArgs(), List.of());
        assertFalse(result.get("example").present());

        //ある場合、present=trueになるか
        result = InnerParser.parse(options.getSubCommand(), options.getNormalOptions(), options.getPositionalArgs(), List.of("-e"));
        assertTrue(result.get("example").present());
        assertTrue(result.get("example").rBoolean());
    }

    @Test
    void testNormalOption_abnormality_arg() throws OptionParseException {
        CommandOptions options = CommandOptions.generator(null)
                .option(Option.normalOption(
                        Set.of(
                                new OptionDisplay(OptionDisplay.PrefixKind.SHORT_OPTION, "e")
                        ),
                        ArgType.INTEGER,
                        false,
                        "Test String Option",
                        "example"
                ))
                .build();
        //引数が無い場合
        assertThrows(OptionParseException.class, () -> {
            InnerParser.parse(options.getSubCommand(), options.getNormalOptions(), options.getPositionalArgs(), List.of("-e"));
        });

        //型が異なる場合
        assertThrows(OptionParseException.class, () -> {
            InnerParser.parse(options.getSubCommand(), options.getNormalOptions(), options.getPositionalArgs(), List.of("-e", "invalid"));
        });
    }

    @Test
    void testNormalOption_unknown() {
        CommandOptions options = CommandOptions.generator(null)
                .option(Option.normalOption(
                        Set.of(
                                new OptionDisplay(OptionDisplay.PrefixKind.SHORT_OPTION, "e")
                        ),
                        ArgType.NONE,
                        false,
                        "Test String Option",
                        "example"
                ))
                .build();
        //不明な引数がある場合
        assertThrows(OptionParseException.class, () -> {
            InnerParser.parse(options.getSubCommand(), options.getNormalOptions(), options.getPositionalArgs(), List.of("--unknown"));
        });
    }

    @Test
    void testPositionalArgument_argument() throws OptionParseException {
        CommandOptions options = CommandOptions.generator(null)
                .option(Option.normalOption(
                        Set.of(
                                new OptionDisplay(OptionDisplay.PrefixKind.SHORT_OPTION, "e")
                        ),
                        ArgType.NONE,
                        false,
                        "Test String Option",
                        "example"
                ))
                .argument(Option.argument(
                        ArgType.STRING,
                        "Test Positional Argument",
                        "test-pos-arg"
                ))
                .build();
        var res = options.parse(List.of("-e", "TEST"));
        assertTrue(res.get("example").rBoolean());
        assertEquals("TEST", res.get("test-pos-arg").rString());

        //引数にプレフィックスがある場合、エラーとなるか
        assertThrows(OptionParseException.class, () -> {
            options.parse(List.of("-e", "--TEST"));
        });

        res = options.parse(List.of("-e", "--", "--TEST"));
        assertTrue(res.get("example").rBoolean());
        assertEquals("--TEST", res.get("test-pos-arg").rString());
    }
}
