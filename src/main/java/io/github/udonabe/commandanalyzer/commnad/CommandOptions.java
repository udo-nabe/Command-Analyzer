/*
 * Copyright (c) 2025 Command-Analyzer Contributors.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 */

package io.github.udonabe.commandanalyzer.commnad;

import io.github.udonabe.commandanalyzer.OptionParseException;
import io.github.udonabe.commandanalyzer.ParseResult;
import io.github.udonabe.commandanalyzer.option.ArgumentOption;
import io.github.udonabe.commandanalyzer.option.Option;
import io.github.udonabe.commandanalyzer.option.OptionGroup;
import io.github.udonabe.commandanalyzer.option.SubCommandOption;
import io.github.udonabe.commandanalyzer.parser.InnerParser;
import lombok.Getter;
import lombok.NonNull;

import java.util.*;

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
        return InnerParser.parse(groups, args);
    }

    /**
     * CommandOptionsのビルダー。
     */
    public static class Builder {
        private final List<OptionGroup> groups;
        private final Set<String> groupNames = new HashSet<>(), optionNames = new HashSet<>();

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
            checkValidType(opt);
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
            checkValidType(options);
            for (Option o : options) {
                checkOptionNonAdded(o.getDisplayName());
            }
            //optionsが空ならばエラーを出す
            if (options.length == 0) {
                throw new IllegalArgumentException("The argument 'options' cannot be null or empty.");
            }
            Option.ArgType firstType = options[0].getArgType();
            for (int i = 1; i < options.length; i++) {
                if (options[i].getArgType() != firstType) {
                    throw new IllegalArgumentException(
                            "All instances must have equivalent fields except for displayName and prefix."
                    );
                }
            }

            groups.add(new OptionGroup(name, OptionGroup.Kind.EQUAL, required, options));
            return this;
        }

        /**
         * 複数のオプションを選択するグループを追加する。
         *
         * @param name     グループの管理名。
         * @param required このグループが必須かどうか。
         * @param options  追加するオプション。nameとdisplayNameは異なる必要がある。
         * @return 自分自身。
         */
        public Builder which(String name, boolean required, @NonNull Option... options) {
            checkNonAdded(name);
            checkValidType(options);
            for (Option o : options) {
                checkOptionNonAdded(o.getDisplayName());
            }
            //optionsが空ならばエラーを出す
            if (options.length == 0) {
                throw new IllegalArgumentException("The argument 'options' cannot be null or empty.");
            }
            //nameかdisplayNameが一つでも重複した場合、エラーを出す。
            Set<String> displaySeen = new HashSet<>();

            for (Option s : options) {
                if (!displaySeen.add(s.getDisplayName())) {
                    throw new IllegalArgumentException("All instances must have unique fields 'displayName'.");
                }
            }

            groups.add(new OptionGroup(name, OptionGroup.Kind.WHICH, required, options));
            return this;
        }


        /**
         * サブコマンドを追加する。
         * @param name サブコマンドの管理名。
         * @param subCommands 追加するサブコマンド。
         * @return 自分自身。
         */
        public Builder subCommand(@NonNull String name, @NonNull SubCommandOption... subCommands) {
            checkNonAdded(name);
            for (String s : Arrays.stream(subCommands).map(Option::getDisplayName).toList()) {
                checkOptionNonAdded(s);
            }

            if (subCommands.length == 0) {
                throw new IllegalArgumentException("The argument 'subCommands' cannot be null or empty.");
            }

            if (!groups.isEmpty()) {
                throw new IllegalArgumentException("The subcommand must come first.");
            }

            //一つでも重複した場合、エラーを出す。
            Set<String> displaySeen = new HashSet<>();
            for (SubCommandOption s : subCommands) {
                if (!displaySeen.add(s.getDisplayName())) {
                    throw new IllegalArgumentException("All strings must be unique.");
                }
            }

            groups.add(new OptionGroup(name, OptionGroup.Kind.SUBCOMMAND, true, subCommands));
            return this;
        }

        /**
         * サブコマンドを追加する。このメソッドで追加したサブコマンドは、子要素(child)が必ず{@code null}になります。
         * @param name サブコマンドの管理名。
         * @param subCommands 各サブコマンドの名前。
         * @return 自分自身。
         * @deprecated このメソッドは、{@link #subCommand(String, SubCommandOption...)}に移行しました。
         * こちらの方が子要素を指定できて便利なので、それをお使いください。なお、このメソッドは2.0.0(パーサーコンビネーターの実装予定バージョン)
         * で削除予定です。
         */
        @Deprecated(forRemoval = true, since = "1.2.0")
        public Builder subCommand(String name, String... subCommands) {
            return subCommand(name, Arrays.stream(subCommands).map(t -> new SubCommandOption(t, null))
                    .toArray(SubCommandOption[]::new));
        }

        /**
         * 引数を追加する。
         * 例:
         * java <b>com.example.Main</b>
         * com.example.Mainの部分が、引数に当たる。
         * @param name 引数の名前、管理名であって、表示名ではない。
         * @param type 引数の型。
         * @return 自分自身
         */
        public Builder argument(@NonNull String name, @NonNull Option.ArgType type) {
            checkNonAdded(name);
            checkOptionNonAdded(name);

            //これより前に追加されたオプションの中で、required=falseがある場合、エラーにする
//            for (OptionGroup grp : groups) {
//                if (!grp.required()) throw new IllegalStateException("All options before the argument must be required.");
//            }

            groups.add(new OptionGroup(name, OptionGroup.Kind.ARGUMENT, true, new ArgumentOption(type)));
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
            if (!groupNames.add(name)) {
                throw new IllegalArgumentException("All groups are must have unique field 'name'.");
            }
        }

        private void checkOptionNonAdded(String name) {
            if (!optionNames.add(name)) {
                throw new IllegalArgumentException("All groups are must have unique field 'name'.");
            }
        }

        private void checkValidType(Option... opts) {
            for (Option opt : opts) {
                if (opt instanceof ArgumentOption || opt instanceof SubCommandOption) {
                    throw new IllegalArgumentException("Add ArgumentOption and SubCommandOption with argument() and subCommand() respectively.");
                }
            }
        }
    }
}
