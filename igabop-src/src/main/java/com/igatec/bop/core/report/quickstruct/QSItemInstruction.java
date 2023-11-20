package com.igatec.bop.core.report.quickstruct;

public class QSItemInstruction extends QSItem {

    //================================================

    public QSItemInstruction(QSDocument doc, String _xmlId, String _type, String _id, QSPath[] _paths) {
        super(doc, _xmlId, _type, _id, _paths);
    }

    //================================================

    protected String getValidChildTypes() {
        return "DELAsmProcessCanUseCnx,PLMDocConnection";
    }

    //================================================
}
