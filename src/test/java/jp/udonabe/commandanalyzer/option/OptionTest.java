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
        //正常系:Linuxタイプのオプション
        Option option = new Option("-", "example", Option.ArgType.NONE, true);

        //接頭辞の確認
        assertEquals("-", option.getPrefix());

        //オプション名の確認
        assertEquals("e", option.getName());

        //引数タイプの確認
        assertEquals(Option.ArgType.NONE, option.getArgType());

        //必須かどうかの確認
        assertTrue(option.isRequired());
    }
}
