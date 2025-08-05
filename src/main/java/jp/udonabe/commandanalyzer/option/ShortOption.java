/*
 * Copyright (c) 2025 Command-Analyzer Contributors.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 */

package jp.udonabe.commandanalyzer.option;

/**
 * 短いタイプのコマンドのオプションを表すクラス。
 * このクラスは、「-」が接頭辞にあるオプションを表します。
 * このオプションは、まとめることができます。
 */
public class ShortOption extends Option {
    public ShortOption(String name, String displayName, ArgType type, boolean required) {
        super("-", name, displayName, type, required);
    }
}
