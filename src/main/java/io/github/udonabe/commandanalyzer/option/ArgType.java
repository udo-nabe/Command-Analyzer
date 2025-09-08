/*
 * Copyright (c) 2025 Command-Analyzer Contributors.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 */

package io.github.udonabe.commandanalyzer.option;

import io.github.udonabe.commandanalyzer.OptionParseException;
import io.github.udonabe.commandanalyzer.ParseResult;

import java.util.Iterator;
import java.util.Optional;
import java.util.function.Function;

public enum ArgType {
    NONE(arg -> ParseResult.builder().present(true).rBoolean(true).build()),
    STRING(arg -> ParseResult.builder().present(true).rString(arg).build()),
    INTEGER(arg -> ParseResult.builder().present(true).rInt(Integer.parseInt(arg)).build()),
    DOUBLE(arg -> ParseResult.builder().present(true).rDouble(Double.parseDouble(arg)).build()),
    BOOLEAN(arg -> ParseResult.builder().present(true).rBoolean(Boolean.parseBoolean(arg)).build());
    private final Function<String, ParseResult> parser;

    ArgType(Function<String, ParseResult> parser) {
        this.parser = parser;
    }

    public ParseResult parse(String arg) throws OptionParseException {
        try {
            return this.parser.apply(arg);
        } catch (RuntimeException e) {
            throw new OptionParseException("引数が不足しているか、型が異なります。期待型: " + this, e);
        }
    }
}
