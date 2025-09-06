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
import io.github.udonabe.commandanalyzer.option.ArgType;
import io.github.udonabe.commandanalyzer.option.Option;
import io.github.udonabe.commandanalyzer.option.OptionDisplay;
import lombok.NonNull;

import java.util.*;
import java.util.stream.Collectors;

public class InnerParser {
    public static Map<String, ParseResult> parse(
            Option subCommand,
            @NonNull Set<Option> rawOptions,
            @NonNull List<Option> rawPositionalArgs,
            @NonNull List<String> args
    ) throws OptionParseException {
        Map<String, ParseResult> result = new HashMap<>();

        // コレクションを変更する可能性があるため、
        // コピーする
        var options = rawOptions.stream()
                .map(Option::clone)
                .collect(Collectors.toList());
        var positionalArgs = rawPositionalArgs.stream()
                .map(Option::clone)
                .collect(Collectors.toSet());

        CurrentMode mode = currentModeSetUp(subCommand != null, !options.isEmpty(), !positionalArgs.isEmpty(), true);

        Iterator<String> it = args.iterator();

        while (it.hasNext()) {
            String cmd = it.next();
            Map<String, ParseResult> parsed;
            switch (mode) {
                case SUBCOMMAND -> parsed = Parsers.subCommand.parse(Collections.singletonList(subCommand), cmd, it);
                case NORMAL_OPTION -> parsed = Parsers.argument.parse(options, cmd, it);
                default -> throw new UnsupportedOperationException("Not implemented.");
            }
            result.putAll(parsed);
            mode = currentModeSetUp(false, !options.isEmpty(), !positionalArgs.isEmpty(), false);
            if (mode == null) {
                //他に処理対象が無いので、ループを抜けて終了する
                break;
            }
        }
        return result;
    }

    private static CurrentMode currentModeSetUp(boolean subCommandFound,
                                                boolean normalOptionFound,
                                                boolean positionalArgumentFound,
                                                boolean isThrow) {
        if (subCommandFound) return CurrentMode.SUBCOMMAND;
        if (normalOptionFound) return CurrentMode.NORMAL_OPTION;
        if (positionalArgumentFound) return CurrentMode.POSITIONAL_ARGUMENT;
        if (isThrow) throw new IllegalStateException("パース開始位置を見つけられませんでした。全ての要素が空です。");
        return null;
    }

    private enum CurrentMode {
        SUBCOMMAND,
        NORMAL_OPTION,
        POSITIONAL_ARGUMENT;
    }
}
