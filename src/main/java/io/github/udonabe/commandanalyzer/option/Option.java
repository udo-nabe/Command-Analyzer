/*
 * Copyright (c) 2025 Command-Analyzer Contributors.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 */

package io.github.udonabe.commandanalyzer.option;

import lombok.Getter;

import java.util.List;
import java.util.Objects;

/**
 * コマンドのオプションを表すクラス。
 */
public final class Option {
    private final Kind kind;
    private final List<String> names;
    private final ArgType type;
    private final boolean required;
    private final String exclusiveGroup;
    private final String description;
    private final String managementName;

    private Option(Kind kind,
                  List<String> names,
                  ArgType type,
                  boolean required,
                  String exclusiveGroup,
                  String description,
                  String managementName) {
        this.kind = kind;
        this.names = names;
        this.type = type;
        this.required = required;
        this.exclusiveGroup = exclusiveGroup;
        this.description = description;
        this.managementName = managementName;
    }

    /**
     * 文字列が自身のプレフィックス+内容(namesの要素一つ一つ)と等価か調べる。
     *
     * @param in 比較対象。
     * @return 調べた結果。
     */
    public boolean matches(String in) {
        return names.stream().anyMatch(n -> (kind.getPrefix() + n).equals(in));
    }

    public static Option shortOption(String name, ArgType type, boolean required, String description, String managementName) {
        return new Option(Kind.SHORT_OPTION, List.of(name), type, required, null, description, managementName);
    }

    public static Option longOption(String name, ArgType type, boolean required, String description, String managementName) {
        return new Option(Kind.LONG_OPTION, List.of(name), type, required, null, description, managementName);
    }

    public static Option slashOption(String name, ArgType type, boolean required, String description, String managementName) {
        return new Option(Kind.SLASH_OPTION, List.of(name), type, required, null, description, managementName);
    }

    public Kind kind() {
        return kind;
    }

    public List<String> names() {
        return names;
    }

    public ArgType type() {
        return type;
    }

    public boolean required() {
        return required;
    }

    public String exclusiveGroup() {
        return exclusiveGroup;
    }

    public String description() {
        return description;
    }

    public String managementName() {
        return managementName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Option) obj;
        return Objects.equals(this.kind, that.kind) &&
               Objects.equals(this.names, that.names) &&
               Objects.equals(this.type, that.type) &&
               this.required == that.required &&
               Objects.equals(this.exclusiveGroup, that.exclusiveGroup) &&
               Objects.equals(this.description, that.description) &&
               Objects.equals(this.managementName, that.managementName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kind, names, type, required, exclusiveGroup, description, managementName);
    }

    @Override
    public String toString() {
        return "Option[" +
               "kind=" + kind + ", " +
               "names=" + names + ", " +
               "type=" + type + ", " +
               "required=" + required + ", " +
               "exclusiveGroup=" + exclusiveGroup + ", " +
               "description=" + description + ", " +
               "managementName=" + managementName + ']';
    }


    public enum Kind {
        SHORT_OPTION("-"),
        LONG_OPTION("--"),
        SLASH_OPTION("/"),
        SUBCOMMAND(""),
        ARGUMENT("");
        @Getter
        private final String prefix;

        Kind(String prefix) {
            this.prefix = prefix;
        }
    }

    public enum ArgType {
        NONE,
        STRING,
        INTEGER,
        DOUBLE,
        BOOLEAN,
    }
}
