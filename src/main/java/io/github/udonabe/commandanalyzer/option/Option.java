/*
 * Copyright (c) 2025 Command-Analyzer Contributors.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 */

package io.github.udonabe.commandanalyzer.option;

import lombok.NonNull;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * コマンドのオプションを表すクラス。
 */
public sealed class Option implements Cloneable {
    private final Set<OptionDisplay> displays;
    private final ArgType type;
    private final boolean required;
    private final String description;
    private final String managementName;

    private Option(@NonNull Set<OptionDisplay> displays,
                   @NonNull ArgType type,
                   boolean required,
                   String description,
                   @NonNull String managementName) {
        this.displays = displays;
        this.type = type;
        this.required = required;
        this.description = description;
        this.managementName = managementName;
    }

    public static Option subCommand(Set<String> displays, String description, String managementName) {
        Set<OptionDisplay> converted = displays.stream()
                .map(t -> new OptionDisplay(OptionDisplay.PrefixKind.SUBCOMMAND, t))
                .collect(Collectors.toUnmodifiableSet());
        return new Option(
                converted,
                Option.ArgType.NONE,
                true,
                description,
                managementName
        );
    }

    /**
     * 文字列が自身のプレフィックス+内容(namesの要素一つ一つ)と等価か調べる。
     *
     * @param in 比較対象。
     * @return 調べた結果。
     */
    public boolean matches(String in) {
        return displays.stream().anyMatch(n -> n.getFullDisplay().equals(in));
    }

    public Set<OptionDisplay> displays() {
        return Set.copyOf(displays);
    }

    public ArgType type() {
        return type;
    }

    public boolean required() {
        return required;
    }

    public String description() {
        return description;
    }

    public String managementName() {
        return managementName;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof Option option)) return false;

        return required == option.required && displays.equals(option.displays) && type == option.type && Objects.equals(description, option.description) && managementName.equals(option.managementName);
    }

    @Override
    public int hashCode() {
        int result = displays.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + Boolean.hashCode(required);
        result = 31 * result + Objects.hashCode(description);
        result = 31 * result + managementName.hashCode();
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Option{");
        sb.append("displays=").append(displays);
        sb.append(", type=").append(type);
        sb.append(", required=").append(required);
        sb.append(", description='").append(description).append('\'');
        sb.append(", managementName='").append(managementName).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public Option clone() {
        return new Option(Set.copyOf(displays), type, required, description, managementName);
    }

    public enum ArgType {
        NONE,
        STRING,
        INTEGER,
        DOUBLE,
        BOOLEAN,
    }

    /**
     * {@link Option}のテスト用クラス。コンストラクタが{@code private}で、テストできないため、テスト時にはこれを使う。
     */
    static final class TestOption extends Option {
        TestOption(@NonNull Set<OptionDisplay> displays,
                   @NonNull ArgType type,
                   boolean required,
                   String description,
                   @NonNull String managementName) {
            super(displays, type, required, description, managementName);
        }
    }
}
