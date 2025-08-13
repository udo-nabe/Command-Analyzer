/*
 * Copyright (c) 2025 Command-Analyzer Contributors.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 */

package com.github.udonabe.commandanalyzer;

/**
 * 当てはまるOptionが見つからなかったことを表す例外。
 */
public class OptionParseException extends Exception {
    public OptionParseException(String message) {
        super(message);
    }

    public OptionParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
