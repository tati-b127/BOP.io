package com.igatec.bop.core.report.quickstruct;

public class QSItemOperation extends QSItem {

    //================================================

    public QSItemOperation(QSDocument doc, String _xmlId, String _type, String _id, QSPath[] _paths) {
        super(doc, _xmlId, _type, _id, _paths);
    }

    //================================================

    protected String getValidChildTypes() {
        return "DELWkiInstructionReference,DELWkiAlertReference,DELAsmProcessCanUseCnx,PLMDocConnection";
    }

    //================================================
}
