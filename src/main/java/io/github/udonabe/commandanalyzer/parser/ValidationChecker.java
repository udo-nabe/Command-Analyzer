/*
 * Copyright (c) 2025 Command-Analyzer Contributors.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 */

package io.github.udonabe.commandanalyzer.parser;

import io.github.udonabe.commandanalyzer.OptionParseException;
import io.github.udonabe.commandanalyzer.option.Option;

import java.util.List;
import java.util.Set;

record ValidationChecker(List<Option> options, List<Option> positionalArgs) {
    public void checkStart() throws OptionParseException {
        //今のところ何もチェックすべきことが無い
    }
    public void checkEnd() throws OptionParseException {
        if (!positionalArgs.isEmpty()) throw new OptionParseException("全ての位置引数を指定してください。");
        if (options.stream().anyMatch(Option::required)) throw new OptionParseException("必須オプションが指定されていません。");
    }
}
