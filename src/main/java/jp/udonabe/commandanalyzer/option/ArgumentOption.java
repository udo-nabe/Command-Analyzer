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
 * 値のみが入力されるオプション。
 */
public class ArgumentOption extends Option {
    /**
     * コンストラクタ。
     * @param argType 引数の種別。
     */
    public ArgumentOption(@NonNull ArgType argType) {
        super("", "", argType);
    }
}
