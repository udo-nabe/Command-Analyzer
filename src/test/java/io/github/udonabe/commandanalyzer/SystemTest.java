/*
 * Copyright (c) 2025 Command-Analyzer Contributors.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 */

package io.github.udonabe.commandanalyzer;

import io.github.udonabe.commandanalyzer.commnad.CommandOptions;
import io.github.udonabe.commandanalyzer.option.LongOption;
import io.github.udonabe.commandanalyzer.option.Option;
import io.github.udonabe.commandanalyzer.option.ShortOption;
import io.github.udonabe.commandanalyzer.option.SubCommandOption;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ライブラリ全体のテストを行う。
 */
public class SystemTest {
    @Test
    void simpleTest() throws OptionParseException {
        CommandOptions options = new CommandOptions.Builder()
                .subCommand("mode", new SubCommandOption("encode", null), new SubCommandOption("decode", null))
                .argument("multiple", Option.ArgType.INTEGER)
                .argument("targetString", Option.ArgType.STRING)
                .build();
        var result = options.parse(new String[]{"encode", "2", "THIS IS A TEST."});
        assertEquals("encode", result.get("mode").rSubCommand());
        assertEquals(2, result.get("multiple").rInt());
        assertEquals("THIS IS A TEST.", result.get("targetString").rString());

        result = options.parse(new String[]{"decode", "2", "THIS IS A TEST."});
        assertEquals("decode", result.get("mode").rSubCommand());
    }

    /**
     * javaコマンドを模擬する。
     */
    @Test
    void javaTest() throws OptionParseException {
        CommandOptions options = new CommandOptions.Builder()
                .equal("classPath", false,
                        new LongOption("cp", Option.ArgType.STRING),
                        new LongOption("classpath", Option.ArgType.STRING),
                        new LongOption("class-path", Option.ArgType.STRING))
                .equal("modulePath", false,
                        new ShortOption("p", Option.ArgType.STRING),
                        new LongOption("module-path", Option.ArgType.STRING))
                .argument("mainClass", Option.ArgType.STRING)
                .build();
        var result = options.parse(new String[]{"--cp", ".", "com.example.main.Main"});
        assertEquals(".", result.get("classPath").rString());
        assertEquals("com.example.main.Main", result.get("mainClass").rString());
    }

    /**
     * gitコマンドを模擬する。
     * @throws OptionParseException
     */
    @Test
    void gitTest() throws OptionParseException {
        CommandOptions add = new CommandOptions.Builder()
                .argument("fileName", Option.ArgType.STRING)
                .build();
        CommandOptions commit = new CommandOptions.Builder()
                .option("amend", false, new LongOption("amend", Option.ArgType.NONE))
                .option("message", true, new ShortOption("m", Option.ArgType.STRING))
                .build();
        CommandOptions options = new CommandOptions.Builder()
                .subCommand("mode",
                        new SubCommandOption("init", null),
                        new SubCommandOption("add", add),
                        new SubCommandOption("commit", commit))
                .build();
        var result = options.parse(new String[]{"init"});
        assertEquals("init", result.get("mode").rSubCommand());

        result = options.parse(new String[]{"commit", "-m", "This is a TEST-COMMIT."});
        assertEquals("commit", result.get("mode").rSubCommand());
        assertEquals("This is a TEST-COMMIT.", result.get("commit.message").rString());
    }
}
