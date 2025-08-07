/*
 * Copyright (c) 2025 Command-Analyzer Contributors.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 */

package jp.udonabe.commandanalyzer.option;
/**
 * 長いタイプのコマンドのオプションを表すクラス。
 * このクラスは、「--」が接頭辞にあるオプションを表します。
 * このオプションは、まとめることができません。
 */
public class LongOption extends Option {
    public LongOption(String name, String displayName, ArgType type) {
        super("--", name, displayName, type);
    }
}
