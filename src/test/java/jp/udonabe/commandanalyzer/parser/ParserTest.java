/*
 * Copyright (c) 2025 Command-Analyzer Contributors.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 */

package jp.udonabe.commandanalyzer.parser;

import jp.udonabe.commandanalyzer.OptionParseException;
import jp.udonabe.commandanalyzer.ParseResult;
import jp.udonabe.commandanalyzer.commnad.CommandOptions;
import jp.udonabe.commandanalyzer.option.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

/**
 * Parserのテストを行う。
 */
public class ParserTest {

    /**
     * 最も基本的な、オプションの存在によって判断する場合のテスト。
     */
    @Test
    void exist() throws OptionParseException {

        Map<String, ParseResult> eResult =  Parser.parse(List.of(
                new OptionGroup("example", OptionGroup.Kind.WRAP, false, new ShortOption("e", Option.ArgType.NONE))
        ), new String[]{"-e"});
        Map<String, ParseResult> neResult =  Parser.parse(List.of(
                new OptionGroup("example", OptionGroup.Kind.WRAP, false, new ShortOption("e", Option.ArgType.NONE))
        ), new String[]{});

        assertTrue(eResult.get("example").rBoolean());
        assertFalse(neResult.get("example").rBoolean());

        //不要なオプションの存在を検知できるか
        assertThrows(OptionParseException.class, () -> {
            Parser.parse(new CommandOptions.Builder()
                        .option("example", false, new ShortOption("e", Option.ArgType.NONE))
                        .build()
                        .getGroups(),
            new String[] {"-e", "--unknown"});
        });

        //不要な引数の存在を検知できるか
        assertThrows(OptionParseException.class, () -> {
            Parser.parse(new CommandOptions.Builder()
                            .option("example", false, new ShortOption("e", Option.ArgType.NONE))
                            .build()
                            .getGroups(),
                    new String[] {"-e", "123"});
        });
    }

    /**
     * 正常に値を返すことができるかのテスト。
     */
    @Test
    void returnValue() throws OptionParseException {
        Map<String, ParseResult> res =  Parser.parse(List.of(
                new OptionGroup("example", OptionGroup.Kind.WRAP, false, new ShortOption("e", Option.ArgType.INTEGER))
        ), new String[]{"-e", "123"});
        assertSame(123, res.get("example").rInt());

        res = Parser.parse(List.of(
                new OptionGroup("example", OptionGroup.Kind.WRAP, false, new LongOption("example", Option.ArgType.STRING))
        ), new String[]{"--example", "This is a test."});
        assertEquals("This is a test.", res.get("example").rString());

        res = Parser.parse(List.of(
                new OptionGroup("example", OptionGroup.Kind.WRAP, false, new LongOption("example", Option.ArgType.DOUBLE))
        ), new String[]{"--example", "3.141592"});
        assertEquals(3.141592, res.get("example").rDouble());

        res = Parser.parse(List.of(
                new OptionGroup("example", OptionGroup.Kind.WRAP, false, new LongOption("example", Option.ArgType.DOUBLE))
        ), new String[]{"--example", "3141592e-6"});
        assertEquals(3141592e-6, res.get("example").rDouble());

        res = Parser.parse(List.of(
                new OptionGroup("example", OptionGroup.Kind.WRAP, false, new LongOption("example", Option.ArgType.BOOLEAN))
        ), new String[]{"--example", "true"});
        assertTrue(res.get("example").rBoolean());

        res = Parser.parse(List.of(
                new OptionGroup("example", OptionGroup.Kind.WRAP, false, new LongOption("example", Option.ArgType.BOOLEAN))
        ), new String[]{"--example", "fALse"});
        assertFalse(res.get("example").rBoolean());

        assertThrows(OptionParseException.class, () -> {
            Map<String, ParseResult> resu = Parser.parse(List.of(
                    new OptionGroup("example", OptionGroup.Kind.WRAP, true, new LongOption("example", Option.ArgType.STRING))
            ), new String[]{"--eee", "aaa"});
            assertFalse(resu.get("example").rBoolean());
        });
    }

    @Test
    void duplicate() {
        assertThrows(OptionParseException.class, () -> {
            Parser.parse(List.of(), new String[]{"--foo", "--foo"});
        });
    }

    /**
     * 複数のオプションで、正しくパースできるかのテスト。
     */
    @Test
    void multiOptions() throws OptionParseException {
        Map<String, ParseResult> res =  Parser.parse(List.of(
                new OptionGroup("example", OptionGroup.Kind.WRAP, false,
                        new ShortOption("e", Option.ArgType.NONE)),
                new OptionGroup("test", OptionGroup.Kind.WRAP, false,
                        new ShortOption("t", Option.ArgType.NONE))
        ), new String[]{"-e", "-t"});
        assertTrue(res.get("example").rBoolean());
        assertTrue(res.get("test").rBoolean());

        res = Parser.parse(List.of(
                new OptionGroup("example", OptionGroup.Kind.WRAP, false,
                        new ShortOption("e", Option.ArgType.NONE)),
                new OptionGroup("test", OptionGroup.Kind.WRAP, false,
                        new ShortOption("t", Option.ArgType.NONE))
        ), new String[]{"-e"});
        assertTrue(res.get("example").rBoolean());
        assertFalse(res.get("test").rBoolean());
    }

    /**
     * 順不同に関するテスト
     */
    @Test
    void noOrderSensitive() throws OptionParseException {
        Parser.parse(List.of(
                new OptionGroup("example", OptionGroup.Kind.WRAP, false, new ShortOption("e", Option.ArgType.NONE)),
                new OptionGroup("test", OptionGroup.Kind.WRAP, false, new ShortOption("t", Option.ArgType.NONE)),
                new OptionGroup("command", OptionGroup.Kind.WRAP, false, new ShortOption("c", Option.ArgType.NONE))
        ), new String[]{"-e", "-t", "-c"});

        Parser.parse(List.of(
                new OptionGroup("example", OptionGroup.Kind.WRAP, false, new ShortOption("e", Option.ArgType.NONE)),
                new OptionGroup("test", OptionGroup.Kind.WRAP, false, new ShortOption("t", Option.ArgType.NONE)),
                new OptionGroup("command", OptionGroup.Kind.WRAP, false, new ShortOption("c", Option.ArgType.NONE))
        ), new String[]{"-t", "-e", "-c"});

        Map<String, ParseResult> res =  Parser.parse(List.of(
                new OptionGroup("example", OptionGroup.Kind.WRAP, false, new ShortOption("e", Option.ArgType.INTEGER)),
                new OptionGroup("test", OptionGroup.Kind.WRAP, false, new ShortOption("t", Option.ArgType.STRING)),
                new OptionGroup("command", OptionGroup.Kind.WRAP, false, new ShortOption("c", Option.ArgType.DOUBLE))
        ), new String[]{"-t", "This is a test.", "-c", "3.14", "-e", "777"});

        assertEquals(777, res.get("example").rInt());
        assertEquals("This is a test.", res.get("test").rString());
        assertEquals(3.14, res.get("command").rDouble());
    }

    /**
     * 等価にする場合、正しく判定できるかのテスト。
     */
    @Test
    void equal() throws OptionParseException {
        Map<String, ParseResult> res =  Parser.parse(List.of(
                new OptionGroup("example", OptionGroup.Kind.EQUAL, false,
                        new ShortOption("e", Option.ArgType.INTEGER),
                        new LongOption("example", Option.ArgType.INTEGER))
        ), new String[]{"-e", "123"});
        assertEquals(123, res.get("example").rInt());

        res =  Parser.parse(List.of(
                new OptionGroup("example", OptionGroup.Kind.EQUAL, false,
                        new ShortOption("e", Option.ArgType.INTEGER),
                        new LongOption("example", Option.ArgType.INTEGER))
        ), new String[]{"--example", "123"});
        assertEquals(123, res.get("example").rInt());
    }

    /**
     * 正しくオプションを選択できるかのテスト。
     */
    @Test
    void which() throws OptionParseException {
        Map<String, ParseResult> res =  Parser.parse(List.of(
                new OptionGroup("example_or_test", OptionGroup.Kind.WHICH, false,
                        new ShortOption("e", Option.ArgType.NONE),
                        new ShortOption("t", Option.ArgType.NONE))
        ), new String[]{"-e"});
        assertEquals("e", res.get("example_or_test").rWhich());

        res =  Parser.parse(List.of(
                new OptionGroup("example_or_test", OptionGroup.Kind.WHICH, false,
                        new ShortOption("e", Option.ArgType.NONE),
                        new ShortOption("t", Option.ArgType.NONE))
        ), new String[]{"-t"});
        assertEquals("t", res.get("example_or_test").rWhich());

        //異常系: オプションが重複した場合の挙動
        assertThrows(OptionParseException.class, () -> {
            Parser.parse(List.of(
                    new OptionGroup("which_test", OptionGroup.Kind.WHICH, false,
                            new ShortOption("e", Option.ArgType.NONE),
                            new ShortOption("t", Option.ArgType.NONE))
            ), new String[]{"-e -t"});
        });
    }

    @Test
    void subCommand() throws OptionParseException {
        //正常系
        Map<String, ParseResult> res =  Parser.parse(List.of(
                new OptionGroup("test", OptionGroup.Kind.SUBCOMMAND, true,
                        new SubCommandOption("foo"),
                        new SubCommandOption("bar"),
                        new SubCommandOption("hoge"))
        ), new String[]{"foo"});
        assertEquals("foo", res.get("test").rSubCommand());

        res =  Parser.parse(List.of(
                new OptionGroup("test", OptionGroup.Kind.SUBCOMMAND, true,
                        new SubCommandOption("foo"),
                        new SubCommandOption("bar"),
                        new SubCommandOption("hoge"))
        ), new String[]{"bar"});
        assertEquals("bar", res.get("test").rSubCommand());

        res =  Parser.parse(List.of(
                new OptionGroup("test", OptionGroup.Kind.SUBCOMMAND, true,
                        new SubCommandOption("foo"),
                        new SubCommandOption("bar"),
                        new SubCommandOption("hoge"))
        ), new String[]{"hoge"});
        assertEquals("hoge", res.get("test").rSubCommand());

        res =  Parser.parse(List.of(
                new OptionGroup("test", OptionGroup.Kind.SUBCOMMAND, true,
                        new SubCommandOption("foo"),
                        new SubCommandOption("bar"),
                        new SubCommandOption("hoge")),
                new OptionGroup("example", OptionGroup.Kind.SUBCOMMAND, true,
                        new SubCommandOption("foo"),
                        new SubCommandOption("bar"),
                        new SubCommandOption("hoge"))
        ), new String[]{"hoge", "foo"});
        assertEquals("hoge", res.get("test").rSubCommand());
        assertEquals("foo", res.get("example").rSubCommand());

        //異常系: 想定していない位置にサブコマンドがあった場合
        assertThrows(OptionParseException.class, () -> {
            Parser.parse(List.of(
                    new OptionGroup("test", OptionGroup.Kind.SUBCOMMAND, true,
                            new SubCommandOption("foo"),
                            new SubCommandOption("bar"),
                            new SubCommandOption("hoge"))
            ), new String[]{"--unknown hoge"});
        });

        //異常系: サブコマンドが無かった場合
        assertThrows(OptionParseException.class, () -> {
            Parser.parse(List.of(
                    new OptionGroup("test", OptionGroup.Kind.SUBCOMMAND, true,
                            new SubCommandOption("foo"),
                            new SubCommandOption("bar"),
                            new SubCommandOption("hoge"))
            ), new String[]{""});
        });

        //異常系: 想定と違うサブコマンドがあった場合
        assertThrows(OptionParseException.class, () -> {
            Parser.parse(List.of(
                    new OptionGroup("test", OptionGroup.Kind.SUBCOMMAND, true,
                            new SubCommandOption("foo"),
                            new SubCommandOption("bar"),
                            new SubCommandOption("hoge"))
            ), new String[]{"piyo"});
        });
    }

    @Test
    void argument() throws OptionParseException {
        //正常系
        Map<String, ParseResult> res =  Parser.parse(List.of(
                new OptionGroup("test", OptionGroup.Kind.ARGUMENT, true,
                        new ArgumentOption(Option.ArgType.STRING))
        ), new String[]{"foo"});
        assertEquals("foo", res.get("test").rString());

        res =  Parser.parse(List.of(
                new OptionGroup("test", OptionGroup.Kind.ARGUMENT, true,
                        new ArgumentOption(Option.ArgType.INTEGER))
        ), new String[]{"123"});
        assertEquals(123, res.get("test").rInt());

        res =  Parser.parse(List.of(
                new OptionGroup("test", OptionGroup.Kind.ARGUMENT, true,
                        new ArgumentOption(Option.ArgType.INTEGER)),
                new OptionGroup("test1", OptionGroup.Kind.ARGUMENT, true,
                        new ArgumentOption(Option.ArgType.STRING))
        ), new String[]{"123", "This is a test."});
        assertEquals(123, res.get("test").rInt());
        assertEquals("This is a test.", res.get("test1").rString());

        //異常系: 型が異なる場合
        assertThrows(OptionParseException.class, () -> {
            Parser.parse(List.of(
                    new OptionGroup("test", OptionGroup.Kind.ARGUMENT, true,
                            new ArgumentOption(Option.ArgType.INTEGER))
            ), new String[]{"except"});
        });

        //異常系: 引数が無い場合
        assertThrows(OptionParseException.class, () -> {
            Parser.parse(List.of(
                    new OptionGroup("test", OptionGroup.Kind.ARGUMENT, true,
                            new ArgumentOption(Option.ArgType.INTEGER))
            ), new String[]{""});
        });
    }
}
