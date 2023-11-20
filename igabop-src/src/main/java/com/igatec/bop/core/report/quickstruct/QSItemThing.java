package com.igatec.bop.core.report.quickstruct;

import com.sun.org.apache.xerces.internal.dom.ElementImpl;

public class QSItemThing extends QSItem {

    //================================================

    public QSItemThing(QSDocument doc, String _xmlId, String _type, String _id, QSPath[] _paths) {
        super(doc, _xmlId, _type, _id, _paths);
    }

    //================================================

    protected String getValidChildTypes() {
        return "VPMReference,DELFmiProcessImplementCnx,DELFmiProcessPrerequisiteCnxCust";
    }

    //================================================

    public ElementImpl makeRefNode(String nodeName ) {

        ElementImpl node = super.makeRefNode( nodeName );

        QSInstance[] pis = getChildrenByRel("ProductImplementMetaInst" );

        if( pis.length > 0 ) {
            QSInstance pi = pis[0];

            document.appendItemOnce( document.psNode, "Product", pi.itemTo );
            //document.appendInstanceOnce( document.psNode, "ProductInst", pi );

            document.appendAttrNode( node, "ProductImplementScopeRef", pi.itemTo.xmlId );

            //ElementImpl refNode = document.appendChildNode( node, "ProductImplementLinkRef" );
            //document.addPathItemNode( refNode, pi.xmlId );
        }

        return node;
    }

    //================================================
}
