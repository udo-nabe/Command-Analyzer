/*
 * Copyright (c) 2025 Command-Analyzer Contributors.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 */

package jp.udonabe.commandanalyzer.commnad;

import jp.udonabe.commandanalyzer.OptionParseException;
import jp.udonabe.commandanalyzer.ParseResult;
import jp.udonabe.commandanalyzer.option.ArgumentOption;
import jp.udonabe.commandanalyzer.option.Option;
import jp.udonabe.commandanalyzer.option.OptionGroup;
import jp.udonabe.commandanalyzer.option.SubCommandOption;
import jp.udonabe.commandanalyzer.parser.Parser;
import lombok.Getter;
import lombok.NonNull;

import java.util.*;
import java.util.stream.Stream;

/**
 * コマンドのオプションをまとめるクラス。
 * <b>このライブラリを使うときは、最初にこのクラスのインスタンスをBuilderを使って生成し、
 * parseしてください。</b>
 */
@Getter
public class CommandOptions {
    private final List<OptionGroup> groups;

    private CommandOptions(List<OptionGroup> groups) {
        assert groups != null : "Command-Analyzer Internal Error: The argument 'groups' cannot be null.";
        this.groups = groups;
    }

    public Map<String, ParseResult> parse(String[] args) throws OptionParseException {
        return Parser.parse(groups, args);
    }

    /**
     * CommandOptionsのビルダー。
     */
    public static class Builder {
        private final List<OptionGroup> groups;

        public Builder() {
            this.groups = new ArrayList<>();
        }

        /**
         * 新たにオプションを追加する。
         *
         * @param opt 追加するオプション。
         * @return 自分自身。
         */
        public Builder option(String name, boolean required, Option opt) {
            checkNonAdded(name);
            checkOptionNonAdded(opt.getDisplayName());
            groups.add(new OptionGroup(name, OptionGroup.Kind.WRAP, required, opt));
            return this;
        }

        /**
         * オプションをどれが入力されても同じ動作をするものとして追加する。
         *
         * @param options 追加するOption。全てのインスタンスで、displayNameとprefix以外の属性が同じである必要があります。
         * @return 自分自身。
         */
        public Builder equal(String name, boolean required, @NonNull Option... options) {
            checkNonAdded(name);
            for (Option o : options) {
                checkOptionNonAdded(o.getDisplayName());
            }
            //optionsが空ならばエラーを出す
            if (options.length == 0) {
                throw new IllegalArgumentException("The argument 'options' cannot be null or empty.");
            }
            //displayNameとprefix以外が等価か判定する
            if (Arrays.stream(options).map(Option::getArgType)
                        .distinct().limit(2).count() != 1) {
                throw new IllegalArgumentException("All instances must have equivalent fields except for displayName and prefix.");
            }

            groups.add(new OptionGroup(name, OptionGroup.Kind.EQUAL, required, options));
            return this;
        }

        /**
         * 複数のオプションを選択するグループを追加する。
         * @param name グループの管理名。
         * @param required このグループが必須かどうか。
         * @param options 追加するオプション。nameとdisplayNameは異なる必要がある。
         * @return 自分自身。
         */
        public Builder which(String name, boolean required, @NonNull Option... options) {
            checkNonAdded(name);
            for (Option o : options) {
                checkOptionNonAdded(o.getDisplayName());
            }
            //optionsが空ならばエラーを出す
            if (options.length == 0) {
                throw new IllegalArgumentException("The argument 'options' cannot be null or empty.");
            }
            //nameかdisplayNameが一つでも重複した場合、エラーを出す。
            Set<String> displaySeen = new HashSet<>();

            if (Arrays.stream(options).map(Option::getDisplayName)
                        .anyMatch(d -> !displaySeen.add(d))) {
                throw new IllegalArgumentException("All instances must have unique fields 'name' and 'displayName'.");
            }

            groups.add(new OptionGroup(name, OptionGroup.Kind.WHICH, required, options));
            return this;
        }

        public Builder subCommand(@NonNull String name, @NonNull String... subCommands) {
            checkNonAdded(name);
            for (String s : subCommands) {
                checkOptionNonAdded(s);
            }

            if (subCommands.length == 0) {
                throw new IllegalArgumentException("The argument 'subCommands' cannot be null or empty.");
            }

            //一つでも重複した場合、エラーを出す。
            Set<String> displaySeen = new HashSet<>();

            if (Arrays.stream(subCommands)
                    .anyMatch(d -> !displaySeen.add(d))) {
                throw new IllegalArgumentException("All strings must be unique.");
            }

            List<Option> opts = new ArrayList<>();
            for (String s : subCommands) {
                opts.add(new SubCommandOption(s));
            }
            groups.add(new OptionGroup(name, OptionGroup.Kind.SUBCOMMAND, true, opts));
            return this;
        }

        public Builder argument(@NonNull String name, @NonNull Option.ArgType type) {
            checkNonAdded(name);
            checkOptionNonAdded(name);

            groups.add(new OptionGroup(name, OptionGroup.Kind.SUBCOMMAND, true, new ArgumentOption(type)));
            return this;
        }

        /**
         * 自身の内容からCommandOptionsを生成する。
         *
         * @return 生成したCommandOptionsインスタンス。
         */
        public CommandOptions build() {
            return new CommandOptions(groups);
        }

        private void checkNonAdded(String name) {
            if (groups.stream()
                    .anyMatch(t -> t.name().equals(name))) {
                throw new IllegalArgumentException("All groups are must have unique field 'name'.");
            }
        }

        private void checkOptionNonAdded(String name) {
            if (groups.stream()
                    .anyMatch(t -> t.options().stream()
                            .anyMatch(t1 -> t1.getDisplayName().equals(name)))) {
                throw new IllegalArgumentException("All options are must have unique field 'name'.");
            }
        }
    }
}
