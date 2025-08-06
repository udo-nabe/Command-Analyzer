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
public class OnlyValueOption extends Option {
    /**
     * コンストラクタ。requiredをfalseにすると、紛らわしくなってしまうため、
     * trueにすることを推奨します。
     * @param name
     * @param argType
     * @param required
     */
    public OnlyValueOption(@NonNull String name, @NonNull ArgType argType, boolean required) {
        super("", name, "", argType, required);
    }
}
