/*
 * Copyright (c) 2025 Command-Analyzer Contributors.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 */

package com.github.udonabe.commandanalyzer.command;

import com.github.udonabe.commandanalyzer.commnad.CommandOptions;
import com.github.udonabe.commandanalyzer.option.LongOption;
import com.github.udonabe.commandanalyzer.option.Option;
import com.github.udonabe.commandanalyzer.option.OptionGroup;
import com.github.udonabe.commandanalyzer.option.ShortOption;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CommandOptionsTest {
    /**
     * CommandOptionsクラスのインスタンス化を行うテストです。
     */
    @Test
    @SuppressWarnings("unchecked")
    void instantiateWithOptions() throws NoSuchFieldException, IllegalAccessException {
        //正常系

        //-a -b -c -dの生成
        CommandOptions options = new CommandOptions.Builder()
                .option("a", false, new ShortOption("a", Option.ArgType.NONE))
                .option("b", false,new ShortOption("b", Option.ArgType.NONE))
                .option("c", false,new ShortOption("c",  Option.ArgType.NONE))
                .option("d", false,new ShortOption("d", Option.ArgType.NONE))
                .build();

        //privateフィールドにアクセスするため、リフレクションを使う。
        Class<?> clazz = options.getClass();
        Field groupsField = clazz.getDeclaredField("groups");
        groupsField.setAccessible(true);
        List<OptionGroup> group = (List<OptionGroup>) groupsField.get(options);
        List<OptionGroup> equal = new ArrayList<>();

        equal.add(new OptionGroup("a", OptionGroup.Kind.WRAP, false, new ShortOption("a", Option.ArgType.NONE)));
        equal.add(new OptionGroup("b", OptionGroup.Kind.WRAP, false, new ShortOption("b", Option.ArgType.NONE)));
        equal.add(new OptionGroup("c", OptionGroup.Kind.WRAP, false, new ShortOption("c", Option.ArgType.NONE)));
        equal.add(new OptionGroup("d", OptionGroup.Kind.WRAP, false, new ShortOption("d", Option.ArgType.NONE)));

        assertEquals(equal, group);
    }

    @Test
    @SuppressWarnings("unchecked")
    void instantiateWithEqual() throws NoSuchFieldException, IllegalAccessException {
        //正常系

        //-e == --exampleの生成
        CommandOptions options = new CommandOptions.Builder()
                .equal("example", false,
                        new ShortOption("e", Option.ArgType.NONE),
                        new LongOption("example", Option.ArgType.NONE))
                .build();

        //privateフィールドにアクセスするため、リフレクションを使う。
        Class<?> clazz = options.getClass();
        Field groupsField = clazz.getDeclaredField("groups");
        groupsField.setAccessible(true);
        List<OptionGroup> group = (List<OptionGroup>) groupsField.get(options);
        List<OptionGroup> equal = new ArrayList<>();

        equal.add(new OptionGroup("example", OptionGroup.Kind.EQUAL, false,
                new ShortOption("e", Option.ArgType.NONE),
                new LongOption("example", Option.ArgType.NONE)));

        assertEquals(equal, group);

        //異常系: displayNameとprefix以外がバラバラの場合、エラーが出るか
        assertThrows(IllegalArgumentException.class, () -> {
            new CommandOptions.Builder()
                    .equal("test", false,
                            new ShortOption("a", Option.ArgType.NONE),
                            new LongOption("bbb", Option.ArgType.STRING));
        });
    }

    @Test
    @SuppressWarnings("unchecked")
    void instantiateWithWhich() throws NoSuchFieldException, IllegalAccessException {
        //正常系

        //-e == --exampleの生成
        CommandOptions options = new CommandOptions.Builder()
                .which("example",
                        false,
                        new ShortOption("e", Option.ArgType.NONE),
                        new LongOption("example2", Option.ArgType.NONE))
                .build();

        //privateフィールドにアクセスするため、リフレクションを使う。
        Class<?> clazz = options.getClass();
        Field groupsField = clazz.getDeclaredField("groups");
        groupsField.setAccessible(true);
        List<OptionGroup> group = (List<OptionGroup>) groupsField.get(options);
        List<OptionGroup> equal = new ArrayList<>();

        equal.add(new OptionGroup("example", OptionGroup.Kind.WHICH, false,
                new ShortOption("e", Option.ArgType.NONE),
                new LongOption("example2", Option.ArgType.NONE)));

        assertEquals(equal, group);

        //異常系: displayNameかnameが重複した場合、エラーが出るか
        assertThrows(IllegalArgumentException.class, () -> {
            new CommandOptions.Builder()
                    .which("test",
                            false,
                            new ShortOption("a", Option.ArgType.NONE),
                            new LongOption("a", Option.ArgType.STRING));
        });
    }


    @Test
    void duplicate() {
        //OptionGroupのnameが重複した場合、エラーを出すか
        assertThrows(IllegalArgumentException.class, () -> {
            new CommandOptions.Builder()
                    .option("test", false, new ShortOption("duplicate", Option.ArgType.NONE))
                    .option("test", false, new ShortOption("test", Option.ArgType.NONE));
        });
        //OptionのdisplayNameが重複した場合、エラーを出すか
        assertThrows(IllegalArgumentException.class, () -> {
            new CommandOptions.Builder()
                    .option("test", false, new ShortOption("duplicate", Option.ArgType.NONE))
                    .option("test1", false, new ShortOption("duplicate", Option.ArgType.NONE));
        });
    }
}
