/*
 * Copyright (c) 2025 Command-Analyzer Contributors.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 */

package jp.udonabe.commandanalyzer.option;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

/**
 * コマンドのオプションを表すクラス。
 * 一般的には、このクラスを直接インスタンス化することはせず、
 * 子クラスをインスタンス化した方が意図が分かりやすくなります。
 * ただし、独自の接頭辞を使う場合は、このクラスをインスタンス化するか、
 * このクラスを拡張してください。
 */

@Getter
@EqualsAndHashCode
@ToString
public class Option {
    /**
     * オプションの接頭辞。空にすることもできます。
     * 注意: このクラスを直接インスタンス化しても、子クラス特有の機能(ショートオプションをまとめられるなど)は付与されません。
     */
    private final String prefix;
    /**
     * オプションの表示名。
     * 表示名は空にすることができます。
     * 空にした場合、ArgTypeをNONEに設定することはできません。
     */
    private final String displayName;
    /**
     * 引数の種別。詳細は{@link ArgType}をご覧ください。
     */
    private final ArgType argType;

    /**
     * 標準のコンストラクタ。
     * @param prefix 接頭辞。({@link #prefix})
     * @param argType 引数の種別。({@link #argType})
     */
    public Option(@NonNull String prefix, String displayName, @NonNull ArgType argType) {
        //引数のチェック
        if (displayName == null || displayName.isEmpty()) {
            if (!(this instanceof SubCommandOption || this instanceof ArgumentOption)) {
                throw new IllegalArgumentException("Argument displayName cannot be null or empty");
            }
        }
        if (displayName != null) {
            if (!(prefix.equals("-") || prefix.equals("--") || prefix.isEmpty())) {
                throw new IllegalArgumentException("Argument prefix must be \"-\" or \"--\" or empty.");
            }
            if (displayName.startsWith("-") || displayName.startsWith("--")) {
                throw new IllegalArgumentException("Argument displayName cannot be \"-\" or \"--\"");
            }
        }

        this.prefix = prefix;
        this.displayName = displayName;
        this.argType = argType;
    }

    /**
     * 引数の種別。
     */
    public enum ArgType {
        /**
         * このオプションは、整数を引数として受け取るということを意味します。
         */
        INTEGER,
        /**
         * このオプションは、真偽値を引数として受け取るということを意味します。
         * @deprecated 代わりに、{@link #NONE}を使用することを推奨します。
         */
        @Deprecated BOOLEAN,
        /**
         * このオプションは、倍精度浮動小数点数を引数として受け取るということを意味します。
         */
        DOUBLE,
        /**
         * このオプションは、文字列を引数として受け取るということを意味します。
         */
        STRING,
        /**
         * このオプションは、引数としては何も受け取らず、存在するかどうか自体が真偽値として受け取られます。
         */
        NONE,
    }
}
