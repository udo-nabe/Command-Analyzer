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
    NONE(it -> ParseResult.builder().rBoolean(true).build()),
    STRING(it -> ParseResult.builder().rString(it.next()).build()),
    INTEGER(it -> ParseResult.builder().rInt(Integer.parseInt(it.next())).build()),
    DOUBLE(it -> ParseResult.builder().rDouble(Double.parseDouble(it.next())).build()),
    BOOLEAN(it -> ParseResult.builder().rBoolean(Boolean.parseBoolean(it.next())).build());
    private final Function<Iterator<String>, ParseResult> parser;

    ArgType(Function<Iterator<String>, ParseResult> parser) {
        this.parser = parser;
    }

    public ParseResult parse(Iterator<String> it) throws OptionParseException {
        try {
            return this.parser.apply(it);
        } catch (RuntimeException e) {
            throw new OptionParseException("引数が不足しているか、型が異なります。期待型: " + this, e);
        }
    }
}
