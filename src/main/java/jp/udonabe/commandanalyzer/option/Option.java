/*
 * Copyright (c) 2025 Command-Analyzer Contributors.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 */

package jp.udonabe.commandanalyzer.option;

import lombok.Getter;
import lombok.NonNull;

/**
 * コマンドのオプションを表すクラス。
 * 一般的には、このクラスを直接インスタンス化することはせず、
 * 子クラスをインスタンス化した方が意図が分かりやすくなります。
 * ただし、独自の接頭辞を使う場合は、このクラスをインスタンス化するか、
 * このクラスを拡張してください。
 */

@Getter
public class Option {
    /**
     * オプションの接頭辞。空にすることもできます。
     * 空にした場合は、{@link #required}をtrueに設定することを推奨します。
     * 接頭辞が「-」(ハイフン)の場合は、ショートオプションとみなされ、複数のオプションをまとめられるようになります。
     */
    private final String prefix;
    /**
     * オプションの名前。
     */
    private final String name;
    /**
     * 引数の種別。詳細は{@link ArgType}をご覧ください。
     */
    private final ArgType argType;
    /**
     * このオプションが必須かどうか。
     * 必須オプションの場合は、コマンドの解析時にこのオプションが無いとエラーになります。
     * {@link #argType}を{@link ArgType#NONE}に設定する際は、falseにすることを強く推奨します。
     */
    private final boolean required;

    /**
     * 標準のコンストラクタ。
     * @param prefix 接頭辞。({@link #prefix})
     * @param name オプションの名前。({@link #name})
     * @param argType 引数の種別。({@link #argType})
     * @param required 必須かどうか。({@link #required})
     */
    public Option(@NonNull String prefix, @NonNull String name, @NonNull ArgType argType, boolean required) {
        this.prefix = prefix;
        this.name = name;
        this.argType = argType;
        this.required = required;
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
