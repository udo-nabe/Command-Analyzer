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

import java.util.*;

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
        Set<String> names = new HashSet<>();
        for (OptionGroup group : groups) {
            for (Option opt : group.options()) {
                names.add(opt.getDisplayName());
            }
        }

        //targetsの重複を調べる
        Set<String> seen = new HashSet<>();
        for (String s : targets) {
            if (!seen.add(s)) {
                throw new OptionParseException("Duplicate option detected.");
            }
        }

        for (OptionGroup group : groups) {
            switch (group.kind()) {
                case WRAP ->
                        searchAndPut(group.name(), group.required(), targets, result, group.options().getFirst(), OptionGroup.Kind.WRAP, names);
                case EQUAL -> {
                    for (Option option : group.options()) {
                        boolean isBreak = searchAndPut(group.name(), false, targets, result, option, OptionGroup.Kind.EQUAL, names);
                        if (isBreak) break;
                    }
                }
                case WHICH -> {
                    String matchedName = null;
                    for (Option option : group.options()) {
                        boolean isBreak = searchAndPut(group.name(), false, targets, result, option, OptionGroup.Kind.WHICH, names);
                        if (isBreak) {
                            if (matchedName != null) {
                                throw new OptionParseException("Conflict which option: " + matchedName + " and " + option.getDisplayName() + ".");
                            }
                            matchedName = option.getDisplayName();
                        }
                    }
                    result.put(group.name(), ParseResult.builder()
                            .rWhich(matchedName)
                            .build());
                }

                //SubCommandの場合は、無条件でrequiredがtrueとみなす。
                case SUBCOMMAND -> {
                    int commandIndex = groups.indexOf(group);
                    Optional<Option> match = group.options().stream()
                            .filter(t -> t.getDisplayName().equals(targets[commandIndex]))
                            .findFirst();
                    if (match.isEmpty())
                        throw new OptionParseException("SubCommand Group \"" + group.name() + "\" not found.");
                    result.put(group.name(), ParseResult.builder()
                            .rSubCommand(match.get().getDisplayName())
                            .build());
                }

                case ARGUMENT -> {
                    int commandIndex = groups.indexOf(group);
                    //Optional<Option> match = Optional.empty();
                    try {
                        put(group.options().getFirst(), result, commandIndex, targets[commandIndex], group.name());
                    } catch (OptionParseException e) {
                        throw new OptionParseException("Argument Group \"" + group.name() + "\" not found or invalid.");
                    }
//                    if (match.isEmpty())
//                        throw new OptionParseException("Argument Group \"" + group.name() + "\" not found.");
//                    result.put(group.name(), ParseResult.builder()
//                            .rSubCommand(match.get().getDisplayName())
//                            .build());
                }
            }
        }

        return result;
    }

    private static boolean searchAndPut(String groupName, boolean isRequired, String[] targets, Map<String, ParseResult> result, Option opt, OptionGroup.Kind kind, Set<String> names) throws OptionParseException {
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

            if (!names.contains(del)) {
                throw new OptionParseException("Unknown option: " + str);
            }

            if (opt.getDisplayName().equals(del)) {
                if (opt.getArgType() != NONE && i < targets.length - 1) {
                    value = targets[i + 1];
                    matchIndex = i;
                    break;
                } else if (opt.getArgType() == NONE) {
                    //不要な引数がある場合、エラーを出すようにする。
                    if (i < targets.length - 1) {
                        String arg = targets[i + 1];

                        if (!arg.matches("(-|--).*")) {
                            throw new OptionParseException("Option " + opt.getPrefix() + opt.getDisplayName() + " does not accept arguments");
                        }
                    }
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
                    return false;
                }
            } else if (kind == OptionGroup.Kind.EQUAL || kind == OptionGroup.Kind.WHICH) {
                result.put(groupName, ParseResult.builder().build());
                return false;
            }
        }

        put(opt, result, matchIndex, value, groupName);
        return true;
    }

    private static void put(Option opt, Map<String, ParseResult> result, int matchIndex, String value, String groupName) throws OptionParseException {
        switch (opt.getArgType()) {
            case NONE -> putNone(result, matchIndex != -1, groupName);
            case INTEGER -> putInteger(result, value, groupName);
            case STRING -> putString(result, value, groupName);
            case DOUBLE -> putDouble(result, value, groupName);
            case BOOLEAN -> putBoolean(result, value, groupName);
        }
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
            throw new OptionParseException("The type is different: " + value + " groupName=" + groupName);
        }
    }

    private static void putString(Map<String, ParseResult> res, String value, String groupName) throws OptionParseException {
        if (value != null) {
            res.put(groupName, ParseResult.builder()
                    .rString(value)
                    .build());
        } else {
            throw new OptionParseException("The type is different: " + value + " groupName=" + groupName);
        }
    }

    private static void putDouble(Map<String, ParseResult> res, String value, String groupName) throws OptionParseException {
        if (value != null && value.matches("^[+-]?(\\d+(\\.\\d+)?)|(\\d+(\\.\\d+)?([eE][+-]?\\d+))$")) {
            res.put(groupName, ParseResult.builder()
                    .rDouble(Double.parseDouble(value))
                    .build());
        } else {
            throw new OptionParseException("The type is different: " + value + " groupName=" + groupName);
        }
    }

    private static void putBoolean(Map<String, ParseResult> res, String value, String groupName) throws OptionParseException {
        if (value != null && value.matches("(?i)true|(?i)false")) {
            res.put(groupName, ParseResult.builder()
                    .rBoolean(Boolean.parseBoolean(value))
                    .build());
        } else {
            throw new OptionParseException("The type is different: " + value + " groupName=" + groupName);
        }
    }
}
