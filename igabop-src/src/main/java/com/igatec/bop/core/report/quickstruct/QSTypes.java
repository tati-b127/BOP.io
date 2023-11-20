package com.igatec.bop.core.report.quickstruct;

public class QSTypes {

    static String[] typesOpInst = { "Kit_MainOp", "DELLmiHeaderOperationReference", "DELWkiInstructionReference" };

    public static boolean isOperationOrInstruction( String type ) {
        return inArray( type, typesOpInst );
    }

    public static boolean inArray( String value, String[] array ) {
        for( String s: array ) {
            if( value.equals(s) ) {
                return true;
            }
        }
        return false;
    }
}
