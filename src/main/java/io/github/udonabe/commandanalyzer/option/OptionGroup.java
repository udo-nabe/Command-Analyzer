/*
 * Copyright (c) 2025 Command-Analyzer Contributors.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 */

package io.github.udonabe.commandanalyzer.option;

import lombok.NonNull;

import java.util.List;

/**
 * オプションのグループを表す。
 *
 * @param options グループに所属しているオプション。
 * @param name 管理名。
 * @param kind グループの種別。
 * @param required 必須かどうか。
 */
public record OptionGroup(String name, Kind kind, boolean required, List<Option> options) {
    public OptionGroup(String name, Kind kind, boolean required, @NonNull Option... options) {
        this(name, kind, required, List.of(options));
    }

    public enum Kind {
        WRAP,
        EQUAL,
        WHICH,
        SUBCOMMAND,
        ARGUMENT,
    }
}
