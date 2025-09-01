/*
 * Copyright (c) 2025 Command-Analyzer Contributors.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 */

package io.github.udonabe.commandanalyzer.option;

import lombok.Getter;

import java.util.List;

/**
 * コマンドのオプションを表すクラス。
 */
public record Option(Kind kind,
                     List<String> names,
                     ArgType type,
                     boolean required,
                     String exclusiveGroup,
                     String description,
                     String managementName) {

    /**
     * 文字列が自身のプレフィックス+内容(namesの要素一つ一つ)と等価か調べる。
     * @param in 比較対象。
     * @return 調べた結果。
     */
    public boolean matches(String in) {
        return names.stream().anyMatch(n -> (kind.getPrefix() + n).equals(in));
    }

    public static Option shortOption(String name, ArgType type, boolean required, String description, String managementName) {
        return new Option(Kind.SHORT_OPTION, List.of(name), type, required, null, description, managementName);
    }

    public static Option longOption(String name, ArgType type, boolean required, String description, String managementName) {
        return new Option(Kind.LONG_OPTION, List.of(name), type, required, null, description, managementName);
    }

    public static Option slashOption(String name, ArgType type, boolean required, String description, String managementName) {
        return new Option(Kind.SLASH_OPTION, List.of(name), type, required, null, description, managementName);
    }

    public enum Kind {
        SHORT_OPTION("-"),
        LONG_OPTION("--"),
        SLASH_OPTION("/"),
        SUBCOMMAND(""),
        ARGUMENT("");
        @Getter
        private final String prefix;

        Kind(String prefix) {
            this.prefix = prefix;
        }
    }

    public enum ArgType {
        NONE,
        STRING,
        INTEGER,
        DOUBLE,
        BOOLEAN,
    }
}
