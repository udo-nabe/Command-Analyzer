/*
 * Copyright (c) 2025 Command-Analyzer Contributors.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 */

package jp.udonabe.commandanalyzer.option;

import lombok.NonNull;

/**
 * 表示のみで、接頭辞もなく、値も受け取らないオプション。
 */
public class OnlyDisplayOption extends Option {
    /**
     * コンストラクタ。
     * argTypeは自動的にNONEになります。
     * @param name 管理名。
     * @param displayName 表示名。
     */
    public OnlyDisplayOption(@NonNull String name, String displayName) {
        super("", name, displayName, ArgType.NONE);
    }
}
