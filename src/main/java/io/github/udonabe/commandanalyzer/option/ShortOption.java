/*
 * Copyright (c) 2025 Command-Analyzer Contributors.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 */

package io.github.udonabe.commandanalyzer.option;

/**
 * 短いタイプのコマンドのオプションを表すクラス。
 * このクラスは、「-」が接頭辞にあるオプションを表します。
 * このオプションは、まとめることができます。
 */
public class ShortOption extends Option {
    public ShortOption(String displayName, ArgType type) {
        super("-", displayName, type);
        if (displayName.length() != 1) {
            throw new IllegalArgumentException("The argument 'displayName' must have length 1.");
        }
    }
}
