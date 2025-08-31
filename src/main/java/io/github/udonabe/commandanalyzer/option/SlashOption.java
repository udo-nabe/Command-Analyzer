/*
 * Copyright (c) 2025 Command-Analyzer Contributors.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 */

package io.github.udonabe.commandanalyzer.option;
/**
 * スラッシュが接頭辞のオプション(Windowsタイプのオプション)を表すクラス。
 * このクラスは、「/」が接頭辞にあるオプションを表します。
 * このオプションは、まとめることができません。
 */
public final class SlashOption extends Option {
    public SlashOption(String displayName, ArgType type) {
        super("/", displayName, type);
    }
}
