/*
 * Copyright (c) 2025 Command-Analyzer Contributors.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 */

package io.github.udonabe.commandanalyzer.option;

import lombok.Getter;

public record OptionDisplay(PrefixKind prefix, String display) {
    public String getFullDisplay() {
        return prefix().prefix + display;
    }

    public enum PrefixKind {
        SHORT_OPTION("-"),
        LONG_OPTION("--"),
        SLASH_OPTION("/"),
        SUBCOMMAND(""),
        ARGUMENT("");
        @Getter
        private final String prefix;

        PrefixKind(String prefix) {
            this.prefix = prefix;
        }
    }
}
