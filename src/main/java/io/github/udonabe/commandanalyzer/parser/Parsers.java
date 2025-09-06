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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Parsers {
    public static Parser subCommand = (options, cmd, it) -> {
        // 引数を変更してしまう可能性があるため、コピーする
        Map<String, ParseResult> result = new HashMap<>();

        // サブコマンドは一つのみが想定される
        assert options.size() == 1 : "サブコマンドは一つしか指定できません。";
        Option option = options.getFirst();

        Optional<Option> matched = Parser.match(options, cmd);
        if (matched.isEmpty())
            throw new OptionParseException("サブコマンドがありません。入力候補: " + option.displays());

        result.put(matched.get().managementName(), ParseResult.builder().rSubCommand(cmd).build());

        return result;
    };
}
