/*
 * Copyright (c) 2025 Command-Analyzer Contributors.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 */

package io.github.udonabe.commandanalyzer.command;

import io.github.udonabe.commandanalyzer.option.Option;
import io.github.udonabe.commandanalyzer.option.OptionDisplay;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

/**
 * コマンドのオプションをまとめるクラス。
 * <b>このライブラリを使うときは、最初にこのクラスのインスタンスをBuilderを使って生成し、
 * parseしてください。</b>
 */
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

    public Option getSubCommand() {
        return subCommand == null ? null : subCommand.clone();
    }

    public Set<Option> getNormalOptions() {
        return normalOptions.stream().map(Option::clone)
                .collect(Collectors.toUnmodifiableSet());
    }

    public List<Option> getPositionalArgs() {
        return positionalArgs.stream().map(Option::clone)
                .toList();
    }

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

        private final Set<String> names = new HashSet<>();
        private final Set<String> displays = new HashSet<>();


        private Generator(Option subCommand) {
            this.subCommand = subCommand;
            this.normalOptions = new HashSet<>();
            this.positionalArgs = new ArrayList<>();
        }

        /**
         * 新規にオプションを追加する。
         * @param add 追加対象のオプション。
         * @return 自分自身
         */
        public Generator option(@NonNull Option add) {
            //引数をチェック
            checkNonAdded(add.managementName(), add.displays().stream().map(OptionDisplay::getFullDisplay).collect(Collectors.toUnmodifiableSet()));

            normalOptions.add(add);
            return this;
        }

        private void checkNonAdded(String managementName, Set<String> displays) {
            if (!names.add(managementName)) throw new IllegalArgumentException("既に同じ管理名(managementName)のオプションが追加されています。");
            for (String display : displays) {
                if (!this.displays.add(display)) throw new IllegalArgumentException("既に同じ表示名(OptionDisplay#getFullDisplay())のオプションが追加されています。");
            }
        }

        /**
         * 自身の内容から{@link CommandOptions}を生成する。
         * @return 生成した内容
         */
        public CommandOptions build() {
            return new CommandOptions(subCommand, normalOptions, positionalArgs);
        }
    }
}
