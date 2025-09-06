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
    void testNormalOption() throws OptionParseException {
        // 正常系
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

        //異常系: 想定された引数が無い場合、エラーとなるか
        assertThrows(OptionParseException.class, () -> {
            var res = InnerParser.parse(options.getSubCommand(), options.getNormalOptions(), options.getPositionalArgs(), List.of("-e"));
        });
    }
}
