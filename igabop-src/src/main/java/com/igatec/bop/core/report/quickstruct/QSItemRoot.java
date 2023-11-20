package com.igatec.bop.core.report.quickstruct;

public class QSItemRoot extends QSItem {

    String rootSystemId = null;

    //================================================

    public QSItemRoot(QSDocument doc, String _xmlId, String _type, String _id, String _rootSystemId ) {
        super(doc, _xmlId, _type, _id, null );
        rootSystemId = _rootSystemId;
    }

    //================================================

    protected String getValidChildTypes() {
        return "PPRContextProcessCnxDisc,PPRContextSystemCnxDisc,VPMReference";
    }

    //================================================

    public QSInstance[] getSystems() {
        //return getChildrenByClass( QSItemSystem.class );
        return getChildrenById( rootSystemId );
    }

    //================================================
}
