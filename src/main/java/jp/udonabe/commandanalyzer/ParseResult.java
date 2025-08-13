/*
 * Copyright (c) 2025 Command-Analyzer Contributors.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 */

package jp.udonabe.commandanalyzer;

import lombok.Builder;

/**
 * オプションをパースした結果。
 * 不要なオプションは、代入されず初期値のままになる。
 *
 * @param rInt     オプションの引数として受け取った整数。
 * @param rBoolean オプションの引数として受け取った真偽値。
 * @param rDouble  オプションの引数として受け取った倍精度浮動小数点数。
 * @param rString  オプションの引数として受け取った文字列。
 * @param rWhich   オプションを選択した結果。選択されたオプションのnameが代入される。
 */
@Builder
public record ParseResult(int rInt, boolean rBoolean, double rDouble, String rString, String rWhich, String rSubCommand) { }
