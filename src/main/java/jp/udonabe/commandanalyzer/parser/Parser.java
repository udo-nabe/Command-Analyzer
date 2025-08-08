/*
 * Copyright (c) 2025 Command-Analyzer Contributors.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 */

package jp.udonabe.commandanalyzer.parser;

import jp.udonabe.commandanalyzer.OptionParseException;
import jp.udonabe.commandanalyzer.ParseResult;
import jp.udonabe.commandanalyzer.option.Option;
import jp.udonabe.commandanalyzer.option.OptionGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static jp.udonabe.commandanalyzer.option.Option.ArgType.NONE;

/**
 * コマンドオプションをパースする。
 * 内部クラスのため、外部からの仕様は<b>不可</b>。
 */
public final class Parser {

    /**
     * パースを行う。
     * 引数が配列になっているので、通常はこちらをお使いください。
     *
     * @param groups  オプションの構文が入ったリスト。
     * @param targets パースする対象。
     * @return パース結果。キーはオプションの管理名で、値は入力されたものから抽出した。
     */
    public static Map<String, ParseResult> parse(List<OptionGroup> groups, String[] targets) throws OptionParseException {
        Map<String, ParseResult> result = new HashMap<>();
        for (OptionGroup group : groups) {
            switch (group.kind()) {
                case WRAP -> searchAndPut(group.name(), group.required(), targets, result, group.options().getFirst(), OptionGroup.Kind.WRAP);
                case EQUAL -> {
                    for (Option option : group.options()) {
                        boolean isBreak = searchAndPut(group.name(), false, targets, result, option, OptionGroup.Kind.EQUAL);
                        if (isBreak) break;
                    }
                }
                case WHICH -> {
                    String matchedName = "";
                    for (Option option : group.options()) {
                        boolean isBreak = searchAndPut(group.name(), false, targets, result, option, OptionGroup.Kind.WHICH);
                        if (isBreak){
                            matchedName = option.getDisplayName();
                            break;
                        }
                    }
                    result.put(group.name(), ParseResult.builder()
                            .rWhich(matchedName)
                            .build());
                }
            }
        }
        return result;
    }

    private static boolean searchAndPut(String groupName, boolean isRequired, String[] targets, Map<String, ParseResult> result, Option opt, OptionGroup.Kind kind) throws OptionParseException {
        //インデックスを探す必要があるため、あえてStream APIではなく、forループを使う。
        int matchIndex = -1;
        String value = null;
        for (int i = 0; i < targets.length; i++) {
            String str = targets[i];

            if (str.isEmpty()) continue;

            String del;
            if (str.startsWith("--")) {
                del = str.substring(2);
            } else if (str.startsWith("-")) {
                del = str.substring(1);
            } else {
                continue;
            }

            if (opt.getDisplayName().equals(del)) {
                if (opt.getArgType() != NONE && i <= targets.length - 1) {
                    value = targets[i + 1];
                    matchIndex = i;
                    System.out.println("Matched! display=" + opt.getDisplayName() + " name=" + groupName + " value=" + value);
                    break;
                } else if (opt.getArgType() == NONE) {
                    matchIndex = i;
                } else {
                    throw new OptionParseException("Option argument not found. groupName=\"" + groupName + "\" argType=" + opt.getArgType());
                }
            }
        }
        if (matchIndex == -1 && isRequired && opt.getArgType() != NONE)
            throw new OptionParseException("No items matching displayName \"" + groupName + "\" were found.");
        else if (matchIndex == -1) {
            if (kind == OptionGroup.Kind.WRAP) {
                if (opt.getArgType() != NONE) {
                    result.put(groupName, ParseResult.builder().build());
                }
            } else if (kind == OptionGroup.Kind.EQUAL || kind == OptionGroup.Kind.WHICH) {
                return false;
            }
        }

        //ここからはOptionの種別によって処理が変わる
        switch (opt.getArgType()) {
            case NONE -> putNone(result, matchIndex != -1, groupName);
            case INTEGER -> putInteger(result, value, groupName);
            case STRING -> putString(result, value, groupName);
            case DOUBLE -> putDouble(result, value, groupName);
            case BOOLEAN -> putBoolean(result, value, groupName);
        }
        return true;
    }

    private static void putNone(Map<String, ParseResult> res, boolean val, String groupName) {
        res.put(groupName, ParseResult.builder()
                .rBoolean(val)
                .build());
    }

    private static void putInteger(Map<String, ParseResult> res, String value, String groupName) throws OptionParseException {
        if (value != null && value.matches("^[+-]?\\d+$")) {
            res.put(groupName, ParseResult.builder()
                    .rInt(Integer.parseInt(value))
                    .build());
        } else {
            throw new OptionParseException("Invalid option argument: " + value + " groupName=" + groupName);
        }
    }

    private static void putString(Map<String, ParseResult> res, String value, String groupName) throws OptionParseException {
        if (value != null) {
            res.put(groupName, ParseResult.builder()
                    .rString(value)
                    .build());
        } else {
            throw new OptionParseException("Invalid option argument: " + value);
        }
    }

    private static void putDouble(Map<String, ParseResult> res, String value, String groupName) throws OptionParseException {
        if (value != null && value.matches("^[+-]?(\\d+(\\.\\d+)?)|(\\d+(\\.\\d+)?([eE][+-]?\\d+))$")) {
            res.put(groupName, ParseResult.builder()
                    .rDouble(Double.parseDouble(value))
                    .build());
        } else {
            throw new OptionParseException("Invalid option argument: " + value);
        }
    }

    private static void putBoolean(Map<String, ParseResult> res, String value, String groupName) throws OptionParseException {
        if (value != null && value.matches("(?i)true|(?i)false")) {
            res.put(groupName, ParseResult.builder()
                    .rBoolean(Boolean.parseBoolean(value))
                    .build());
        } else {
            throw new OptionParseException("Invalid option argument: " + value);
        }
    }
}
