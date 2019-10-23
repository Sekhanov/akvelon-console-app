package com.sekhanov.scriptparser;

import java.io.Serializable;

/**
 * Исключение, выбрасываемое в случае, если при анализе строки не удалось
 * определить тип команды
 */
public class IllegalScriptValueException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = -6867957279563938412L;

    public IllegalScriptValueException(String message) {
        super(message);
    }

}
