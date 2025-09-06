/*
 * Copyright (c) 2025 Command-Analyzer Contributors.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 */

package io.github.udonabe.commandanalyzer.parser;

import io.github.udonabe.commandanalyzer.OptionParseException;
import io.github.udonabe.commandanalyzer.ParseResult;
import io.github.udonabe.commandanalyzer.option.Option;

import java.util.*;

@FunctionalInterface
public interface Parser {
    Map<String, ParseResult> parse(List<Option> options, String cmd, Iterator<String> it) throws OptionParseException;

    static Optional<Option> match(Collection<Option> commands, String cmd) {
        return commands.stream()
                .filter(t -> t.matches(cmd))
                .findFirst();
    }
}
