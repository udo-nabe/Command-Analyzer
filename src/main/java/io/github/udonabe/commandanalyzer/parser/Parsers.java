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

import java.util.*;

class Parsers {
    static Parser subCommand = (options, cmd, it) -> {
        // 引数を変更してしまう可能性があるため、コピーする
        Map<String, ParseResult> result = new HashMap<>();

        // サブコマンドは一つのみが想定される
        assert options.size() == 1 : "サブコマンドは一つしか指定できません。";
        Option option = options.getFirst();

        Optional<Option> matched = Parser.match(options, cmd);
        if (matched.isEmpty())
            throw new OptionParseException("サブコマンドがありません。入力候補: " + option.displays());

        result.put(matched.get().managementName(), ParseResult.builder().present(true).rSubCommand(cmd).build());

        return result;
    };

    static Parser argument = (options, cmd, it) -> {
        Map<String, ParseResult> result = new HashMap<>();
        try {
            result.put(options.getFirst().managementName(), options.getFirst().type().parse(cmd));
        } catch (OptionParseException e) {
            throw new OptionParseException("引数にエラーがあります。入力値: " + cmd + ", 管理名: " + options.getFirst().managementName(), e);
        }
        return result;
    };

    private static final Set<String> exclusiveDisplayNames = new HashSet<>();

    static Parser option = (options, cmd, it) -> {
        Optional<Option> matched = Parser.match(options, cmd);
        if (matched.isEmpty()) {
            if (exclusiveDisplayNames.contains(cmd)) {
                throw new OptionParseException("排他グループが重複指定されています: " + cmd);
            }
            throw new OptionParseException("不明なオプション:" + cmd);
        }

        if (matched.get().type() == ArgType.NONE) {
            options.remove(matched.get());
            if (matched.get().exclusive()) {
                String matchedDisplay = matched.get().displays()
                        .stream()
                        .filter(t -> (t.prefix().getPrefix() + t.display()).equals(cmd))
                        .map(OptionDisplay::display)
                        .findFirst()
                        .orElseThrow(() -> new AssertionError("排他グループのマッチ処理が間違っている可能性があります。"));
                exclusiveDisplayNames.addAll(matched.get().getFullDisplays());
                return new HashMap<>() {
                    {
                        put(matched.get().managementName(),
                                ParseResult.builder().rWhich(matchedDisplay).build());
                    }
                };
            }
            return new HashMap<>() {
                {
                    put(matched.get().managementName(),
                            ParseResult.builder().present(true).rBoolean(true).build());
                }
            };
        }

        try {
            Map<String, ParseResult> result = argument.parse(List.of(matched.get()), it.next(), it);
            options.remove(matched.get());
            return result;
        } catch (NoSuchElementException e) {
            throw new OptionParseException("引数がありません。 オプション: " + matched.get().getFullDisplays(), e);
        } catch (RuntimeException e) {
            throw new OptionParseException("引数の型が異なります。 オプション: " + matched.get().getFullDisplays(), e);
        }
    };
}
