package com.igatec.bop.core.report.quickstruct;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QSItemSystemImplementLink extends QSItem {

    public List<QSInstance> opRef = new ArrayList<QSInstance>();
    public List<QSInstance> tranRef = new ArrayList<QSInstance>();
    //================================================

    public QSItemSystemImplementLink(QSDocument doc, String _xmlId, String _type, String _id, QSPath[] _paths) {
        super(doc, _xmlId, _type, _id, _paths);
        document.registerSil( this );
    }

    //================================================

    private static String[] typesOpSys = {"DELLmiGeneralSystemInstance","DELLmiHeaderOperationInstance"};

    public void postFillInstances( Map<String, QSInstance> instMap ) {

        for( QSPath path : paths ) {
            List<QSInstance> listToFill = path.hasAnyOfTypes(typesOpSys) ? opRef : tranRef;
            for(QSPathElement el: path.getElements() ) {
                QSInstance inst = instMap.get(el.getId());
                if( inst != null ) {
                    listToFill.add( inst );
                }
            }
        }
    }

    //================================================
}
