/*
 * Copyright (c) 2025 Command-Analyzer Contributors.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 */

package io.github.udonabe.commandanalyzer.command;

import io.github.udonabe.commandanalyzer.option.Option;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.*;

/**
 * コマンドのオプションをまとめるクラス。
 * <b>このライブラリを使うときは、最初にこのクラスのインスタンスをBuilderを使って生成し、
 * parseしてください。</b>
 */
@Getter
@RequiredArgsConstructor
public class CommandOptions {
    /**
     * このCommandOptionsのサブコマンド。
     */
    private final Option subCommand;
    /**
     * 普通のオプションを格納する。基本的に順不同のため、{@link Set}にしています。
     */
    private final Set<Option> normalOptions;
    /**
     * 位置引数を格納する。これは、順序が重要なため、{@link #normalOptions}と違い、{@link List}にしています。
     */
    private final List<Option> positionalArgs;

    /**
     * 新しいビルダーを生成する。
     * @return 生成したビルダー。
     */
    public static Generator generator(Option subCommand) { return new Generator(subCommand); }

    /**
     * {@link CommandOptions}のビルダー。
     */
    public static class Generator {
        private final Option subCommand;
        private final Set<Option> normalOptions;
        private final List<Option> positionalArgs;

        private Generator(Option subCommand) {
            this.subCommand = subCommand;
            this.normalOptions = new HashSet<>();
            this.positionalArgs = new ArrayList<>();
        }
    }
}
