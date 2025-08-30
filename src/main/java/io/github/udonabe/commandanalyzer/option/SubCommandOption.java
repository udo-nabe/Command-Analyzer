/*
 * Copyright (c) 2025 Command-Analyzer Contributors.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 */

package io.github.udonabe.commandanalyzer.option;

import io.github.udonabe.commandanalyzer.commnad.CommandOptions;
import lombok.Getter;

/**
 * 表示のみで、接頭辞もなく、値も受け取らないオプション。
 */
public class SubCommandOption extends Option {
    @Getter
    private final CommandOptions child;
    /**
     * コンストラクタ。
     * argTypeは自動的にNONEになります。
     * @param displayName 表示名。
     */
    public SubCommandOption(String displayName, CommandOptions child) {
        super("", displayName, ArgType.NONE);
        this.child = child;
    }
}
