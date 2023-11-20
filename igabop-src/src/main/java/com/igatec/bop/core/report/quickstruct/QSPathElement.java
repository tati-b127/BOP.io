package com.igatec.bop.core.report.quickstruct;

public class QSPathElement {

    String kind;
    String type;
    String id;

    //================================================

    public QSPathElement( String source ) {
        String[] sourceItems = source.split("," );
        kind = sourceItems[0];
        type = sourceItems[1];
        id   = sourceItems[2];
    }

    //================================================

    public String toString() {
        return kind.substring(0,3)+":"+type+":"+id;
    }

    public String getId() {
        return  id;
    }

    //================================================
}
