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
import io.github.udonabe.commandanalyzer.option.OptionGroup;
import io.github.udonabe.commandanalyzer.option.SubCommandOption;

import java.util.*;

import static io.github.udonabe.commandanalyzer.option.Option.ArgType.NONE;

/**
 * コマンドオプションをパースする。
 * 内部クラスのため、外部からの仕様は<b>不可</b>。
 */
public final class InnerParser {

    /**
     * パースを行う。
     *
     * @param groups  オプションの構文が入ったリスト。
     * @param targets パースする対象。
     * @return パース結果。キーはオプションの管理名で、値は入力されたものから抽出した。
     */
    public static Map<String, ParseResult> parse(List<OptionGroup> groups, String[] targets) throws OptionParseException {
        Map<String, ParseResult> result = new HashMap<>();
        Set<String> names = new HashSet<>();
        initNames(names, groups, true);

        //targetsの重複&不要な引数がないかを調べる
        Set<String> seen = new HashSet<>();
        for (String s : targets) {
            if (!seen.add(s)) {
                throw new OptionParseException("Duplicate option detected.");
            }
            checkContainsName(names, s);
        }

        List<String> newTargets = expandShortOption(targets);
        String[] buf = new String[newTargets.size()];
        parseImpl(groups, newTargets.toArray(buf), result, names, "");

        return result;
    }

    private static List<String> expandShortOption(String[] targets) throws OptionParseException {
        List<String> newTargets = new ArrayList<>();
        //まとめられたショートオプションを展開する
        for (String opt : targets) {
            if (!opt.startsWith("--") && opt.startsWith("-") && opt.length() > 1) {
                String noPrefix = opt.substring(1);
                char[] opts = new char[noPrefix.length()];
                noPrefix.getChars(0, noPrefix.length(), opts, 0);

                //重複があった場合、エラー
                //それ以外の場合展開し、追加する
                Set<Character> charSeen = new HashSet<>();
                for (char ch : opts) {
                    if (!charSeen.add(ch)) throw new OptionParseException("Multi-ShortOption duplicated:" + opt);
                    newTargets.add("-" + ch);
                }
            } else { //当てはまらない場合、そのまま追加する
                newTargets.add(opt);
            }
        }
        return newTargets;
    }

    private static Map<String, ParseResult> parseImpl(List<OptionGroup> groups, String[] targets, Map<String, ParseResult> result, Set<String> names, String prefix) throws OptionParseException {
        for (OptionGroup group : groups) {
            switch (group.kind()) {
                case WRAP ->
                        searchAndPut(prefix + group.name(), group.required(), targets, result, group.options().getFirst(), OptionGroup.Kind.WRAP, names);
                case EQUAL -> {
                    for (Option option : group.options()) {
                        boolean isBreak = searchAndPut(prefix + group.name(), false, targets, result, option, OptionGroup.Kind.EQUAL, names);
                        if (isBreak) break;
                    }
                }
                case WHICH -> {
                    String matchedName = null;
                    for (Option option : group.options()) {
                        boolean isBreak = searchAndPut(prefix + group.name(), false, targets, result, option, OptionGroup.Kind.WHICH, names);
                        if (isBreak) {
                            if (matchedName != null) {
                                throw new OptionParseException("Conflict which option: " + matchedName + " and " + option.getDisplayName() + ".");
                            }
                            matchedName = option.getDisplayName();
                        }
                    }
                    result.put(prefix + group.name(), ParseResult.builder()
                            .rWhich(matchedName)
                            .build());
                }

                //SubCommandの場合は、無条件でrequiredがtrueとみなす。
                case SUBCOMMAND -> parseSubCommand(groups, group, targets, result, prefix, names);

                case ARGUMENT -> {
                    int commandIndex = groups.indexOf(group);
                    //Optional<Option> match = Optional.empty();
                    try {
                        put(group.options().getFirst(), result, commandIndex, targets[commandIndex], prefix + group.name());
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

    private static void checkContainsName(Set<String> names, String str) throws OptionParseException {
//        String del;
//        if (str.startsWith("--")) {
//            del = str.substring(2);
//        } else if (str.startsWith("-")) {
//            del = str.substring(1);
//        } else {
//            return;
//        }
        if (!names.contains(str)) {
            if (str.matches("(^-[^-]{2,}$)") || !str.startsWith("-")) return;
            throw new OptionParseException("Unknown option: " + str);
        }
    }

    private static void initNames(Set<String> names, List<OptionGroup> groups, boolean isRecursion) {
        for (OptionGroup group : groups) {
            for (Option opt : group.options()) {
                names.add(opt.getPrefix() + opt.getDisplayName());
                if (opt instanceof SubCommandOption sub && sub.getChild() != null && isRecursion) {
                    initNames(names, sub.getChild().getGroups(), true);
                }
            }
        }
    }

    private static void parseSubCommand(List<OptionGroup> groups, OptionGroup group, String[] targets, Map<String, ParseResult> result, String prefix, Set<String> names) throws OptionParseException {
        int commandIndex = groups.indexOf(group);
        Optional<Option> match = group.options().stream()
                .filter(t -> t.getDisplayName().equals(targets[commandIndex]))
                .findFirst();
        if (match.isEmpty())
            throw new OptionParseException("SubCommand Group \"" + group.name() + "\" not found.");
        result.put(prefix + group.name(), ParseResult.builder()
                .rSubCommand(match.get().getDisplayName())
                .build());


        //子がある場合、子をパース
        if (match.get() instanceof SubCommandOption subOpt && subOpt.getChild() != null) {
            List<OptionGroup> grp = subOpt.getChild().getGroups();
            String[] targs = Arrays.copyOfRange(targets, commandIndex, targets.length);

            var res = parseImpl(grp, targs, result, names, match.get().getDisplayName() + ".");
            result.putAll(res);
        } else {
            //引数の再チェックを行う
            Set<String> unRecursionNames = new HashSet<>();
            initNames(unRecursionNames, groups, false);
            for (String s : targets) {
                checkContainsName(unRecursionNames, s);
            }
        }
    }

    private static boolean searchAndPut(String groupName, boolean isRequired, String[] targets, Map<String, ParseResult> result, Option opt, OptionGroup.Kind kind, Set<String> names) throws OptionParseException {
        //インデックスを探す必要があるため、あえてStream APIではなく、forループを使う。
        int matchIndex = -1; //-1の場合、マッチしなかったことを表す
        String value = null;
        for (int i = 0; i < targets.length; i++) {
            String str = targets[i];

            if (str.isEmpty()) continue;

            if (!str.startsWith("-") && !str.startsWith("/")) {
                continue;
            }
            MatchResult matchResult = match(opt, str, i, targets, groupName);

            value = matchResult.value();
            matchIndex = matchResult.matchIndex();
            if (matchResult.matchIndex() != -1) {
                break;
            }
        }
        if (matchIndex == -1 && isRequired && opt.getArgType() != NONE)
            throw new OptionParseException("No items matching displayName \"" + groupName + "\" were found.");
        else if (matchIndex == -1) {
            if (kind == OptionGroup.Kind.WRAP) {
                if (opt.getArgType() != NONE) {
                    result.put(groupName, ParseResult.builder().present(false).build());
                    return false;
                }
            } else if (kind == OptionGroup.Kind.EQUAL || kind == OptionGroup.Kind.WHICH) {
                result.put(groupName, ParseResult.builder().present(false).build());
                return false;
            }
        }

        put(opt, result, matchIndex, value, groupName);
        return true;
    }

    private  record MatchResult(String value, int matchIndex) {

    }

    private static MatchResult match(Option opt, String str, int i, String[] targets, String groupName) throws OptionParseException {
        if ((opt.getPrefix() + opt.getDisplayName()).equals(str)) {
            if (opt.getArgType() != NONE && i < targets.length - 1) {
                return new MatchResult(targets[i + 1], i);
            } else if (opt.getArgType() == NONE) {
                //不要な引数がある場合、エラーを出すようにする。
                if (i < targets.length - 1) {
                    String arg = targets[i + 1];

                    if (!arg.matches("(-|--).*")) {
                        throw new OptionParseException("Option " + opt.getPrefix() + opt.getDisplayName() + " does not accept arguments");
                    }
                }
                return new MatchResult(null, i);
            } else {
                throw new OptionParseException("Option argument not found. groupName=\"" + groupName + "\" argType=" + opt.getArgType());
            }
        }
        return new MatchResult(null, -1);
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
                .present(val)
                .build());
    }

    private static final String INTEGER_REGEX = "^[+-]?\\d+$";
    private static final String DOUBLE_REGEX = "^[+-]?(\\d+(\\.\\d+)?)|(\\d+(\\.\\d+)?([eE][+-]?\\d+))$";
    private static final String BOOLEAN_REGEX = "(?i)true|(?i)false";

    private static void putInteger(Map<String, ParseResult> res, String value, String groupName) throws OptionParseException {
        if (value != null && value.matches(INTEGER_REGEX)) {
            res.put(groupName, ParseResult.builder()
                    .rInt(Integer.parseInt(value))
                    .present(true)
                    .build());
        } else {
            throw new OptionParseException("The type is different: " + value + " groupName=" + groupName);
        }
    }

    private static void putString(Map<String, ParseResult> res, String value, String groupName) throws OptionParseException {
        if (value != null) {
            res.put(groupName, ParseResult.builder()
                    .rString(value)
                    .present(true)
                    .build());
        } else {
            throw new OptionParseException("The type is different: " + value + " groupName=" + groupName);
        }
    }

    private static void putDouble(Map<String, ParseResult> res, String value, String groupName) throws OptionParseException {
        if (value != null && value.matches(DOUBLE_REGEX)) {
            res.put(groupName, ParseResult.builder()
                    .rDouble(Double.parseDouble(value))
                    .present(true)
                    .build());
        } else {
            throw new OptionParseException("The type is different: " + value + " groupName=" + groupName);
        }
    }

    private static void putBoolean(Map<String, ParseResult> res, String value, String groupName) throws OptionParseException {
        if (value != null && value.matches(BOOLEAN_REGEX)) {
            res.put(groupName, ParseResult.builder()
                    .rBoolean(Boolean.parseBoolean(value))
                    .present(true)
                    .build());
        } else {
            throw new OptionParseException("The type is different: " + value + " groupName=" + groupName);
        }
    }
}
