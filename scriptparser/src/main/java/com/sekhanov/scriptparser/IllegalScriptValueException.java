package com.sekhanov.scriptparser;

import java.io.Serializable;

/**
 * Исключение выбрасывается при неправильном синтаксисе команды скрипта
 */
public class IllegalScriptValueException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = -6867957279563938412L;

    public IllegalScriptValueException(String message) {
        super(message);
    }

}
