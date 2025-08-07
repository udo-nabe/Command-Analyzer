/*
 * Copyright (c) 2025 Command-Analyzer Contributors.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 */

package jp.udonabe.commandanalyzer.option;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OptionTest {
    /**
     * Optionのインスタンス化を行うテストです。
     * This is a test to instantiate Option.
     */
    @Test
    void instantiate() {
        //正常系:正しくインスタンス化できるか
        Option option = new Option("-", "e", "example", Option.ArgType.NONE);

        //接頭辞の確認
        assertEquals("-", option.getPrefix());

        //オプション名の確認
        assertEquals("e", option.getName());

        //引数タイプの確認
        assertEquals(Option.ArgType.NONE, option.getArgType());

        //表示名の確認
        assertEquals("example", option.getDisplayName());

        //異常系:引数に誤りがある場合、正しく例外を送出するか
        assertThrows(IllegalArgumentException.class, () -> new Option("-", "", "aaa", Option.ArgType.NONE));
        assertThrows(IllegalArgumentException.class, () -> new Option("-", "aaa", "", Option.ArgType.NONE));
    }
}
