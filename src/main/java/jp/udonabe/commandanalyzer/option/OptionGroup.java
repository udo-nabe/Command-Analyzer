/*
 * Copyright (c) 2025 Command-Analyzer Contributors.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 */

package jp.udonabe.commandanalyzer.option;

import lombok.NonNull;

import java.util.List;

/**
 * オプションのグループを表す。
 *
 * @param options グループに所属しているオプション。
 */
public record OptionGroup(List<Option> options) {
    public OptionGroup(@NonNull Option... options) {
        this(List.of(options));
    }
}
