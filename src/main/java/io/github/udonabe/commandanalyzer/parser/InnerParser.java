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
                .collect(Collectors.toList());

        CurrentMode mode = currentModeSetUp(subCommand != null, !options.isEmpty(), !positionalArgs.isEmpty());

        ValidationChecker validation = new ValidationChecker(options, positionalArgs);
        validation.checkStart();

        Iterator<String> it = args.iterator();

        while (it.hasNext()) {
            String cmd = it.next();
            Map<String, ParseResult> parsed;

            CurrentMode newMode = currentModeUpdate(cmd, mode);
            if (cmd.equals("--")) continue;

            try {
                switch (mode) {
                    case SUBCOMMAND -> parsed = Parsers.subCommand.parse(Collections.singletonList(subCommand), cmd, it);
                    case NORMAL_OPTION -> parsed = Parsers.option.parse(options, cmd, it);
                    case POSITIONAL_ARGUMENT -> parsed = Parsers.argument.parse(Collections.singletonList(positionalArgs.removeFirst()), cmd, it);
                    default -> throw new UnsupportedOperationException("Not implemented.");
                }
            } catch (NoSuchElementException e) {
                throw new OptionParseException("不要な引数があります。");
            }

            result.putAll(parsed);
            mode = newMode;
        }

        validation.checkEnd();

        return result;
    }

    private static CurrentMode currentModeSetUp(boolean subCommandFound,
                                                boolean normalOptionFound,
                                                boolean positionalArgumentFound) {
        if (subCommandFound) return CurrentMode.SUBCOMMAND;
        if (normalOptionFound) return CurrentMode.NORMAL_OPTION;
        if (positionalArgumentFound) return CurrentMode.POSITIONAL_ARGUMENT;
        throw new IllegalStateException("パース開始位置を見つけられませんでした。全ての要素が空です。");
    }

    private static CurrentMode currentModeUpdate(String cmd, CurrentMode mode) {
        //プレフィックスがあるかチェックする
        Set<String> prefixes = OptionDisplay.PrefixKind.getPrefixes();

        if (mode == CurrentMode.POSITIONAL_ARGUMENT) return CurrentMode.POSITIONAL_ARGUMENT;
        if (prefixes.stream().noneMatch(cmd::equals)) return CurrentMode.POSITIONAL_ARGUMENT;
        if (cmd.equals("--")) return CurrentMode.POSITIONAL_ARGUMENT;
        return CurrentMode.NORMAL_OPTION;
    }

    private enum CurrentMode {
        SUBCOMMAND,
        NORMAL_OPTION,
        POSITIONAL_ARGUMENT;
    }
}
