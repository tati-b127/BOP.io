package com.igatec.bop.core.report.quickstruct;

import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.apps.domain.util.eMatrixDateFormat;
import com.sun.org.apache.xerces.internal.dom.ElementImpl;

import java.text.SimpleDateFormat;
import java.util.*;

public class QSItemFactory extends QSItemSystem {

    RouteItem[] routeItems;

    //================================================

    public QSItemFactory(QSDocument doc, String _xmlId, String _type, String _id, QSPath[] _paths) {
        super(doc, _xmlId, _type, _id, _paths);
    }

    //================================================

    public void expand() throws Exception {

        super.expand();

        String res = MqlUtil.mqlCommand( document.context, "expand bus $1 from relationship $2 type $3 " +
                        "select bus where $4 " +
                        "select bus physicalid dump $5 ",
                id, "Object Route", "Route",
                " current==\"In Process\" ",
                "," );

        if( res.length() > 0 ) {
            String[] lines = res.split("\n" );
            String[] fields = (lines[0]+",dummy").split( "," );
            String routeId = fields[6];
            loadRoute( routeId );
        }
    }

    //================================================

    public ElementImpl makeRefNode(String nodeName ) {

        ElementImpl node = super.makeRefNode( nodeName );

        if( routeItems != null ) {
            ElementImpl routeNode = document.appendChildNode( node, "Route" );
            for( RouteItem ri : routeItems ) {
                ElementImpl routeItemNode = makeRouteItemNode( ri );
                routeNode.appendChild( routeItemNode );
            }
        }

        document.appendAttributes( node, this );

        return node;
    }

    //================================================

    private ElementImpl makeRouteItemNode(RouteItem ri) {
        ElementImpl node = new ElementImpl(document.xmldoc,"RouteItem" );

        node.setAttribute( "index", ""+ri.position );
        document.appendAttrNode( node, "Role", ri.role );
        document.appendAttrNode( node, "Date", ri.date );
        document.appendAttrNode( node, "LastName",  ri.lastName );
        document.appendAttrNode( node, "FirstName", ri.firstName );
        document.appendAttrNode( node, "Signature", ri.signature );

        return node;
    }

    //================================================

    private void loadRoute( String routeId ) throws FrameworkException {
        routeItems = loadRouteItems( routeId );
        sortRouteItems( routeItems );
    }

    //================================================

    private class RouteItem {
        int position;
        String role;
        String routeNodeId;
        String date;
        String lastName;
        String firstName;
        String email;
        String signature;
    }

    private RouteItem[] loadRouteItems( String routeId ) throws FrameworkException {

        String res = MqlUtil.mqlCommand( document.context, "expand bus $1 type $2 select bus $3 $4 $5 $6 $7 $8 $9 dump $10",
                routeId, "Inbox Task",
                "attribute[IGADrwApprover]", "attribute[Route Node ID]", "state[Complete].start",
                "from[Project Task].to.attribute[Last Name]", "from[Project Task].to.attribute[First Name]",
                "from[Project Task].to.attribute[Email Address]", "from[Project Task].to.attribute[IGAPersonSignature]",
                "|"
        );

        if( res.length() > 0 ) {
            String[] lines = res.split("\n" );

            List<RouteItem> listItems = new ArrayList<RouteItem>();

            for( String line : lines ) {
                String[] fields = (line+"|dummy").split("\\|");
                if( fields.length >= 13 ) {
                    RouteItem ri = new RouteItem();
                    ri.role        = fields[6];
                    ri.routeNodeId = fields[7];
                    ri.date        = convertDate( fields[8] );
                    ri.lastName = fields[9];
                    ri.firstName = fields[10];
                    ri.email = fields[11];
                    ri.signature = fields[12];
                    listItems.add( ri );
                }
            }
            return listItems.toArray( new RouteItem[0] );
        }

        return new RouteItem[0];
    }

    //================================================

    private String convertDate(String rawDate ) {
        try {
            Date d = eMatrixDateFormat.getJavaDate(rawDate);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.format(d);
        } catch ( Exception e ) {
            return null;
        }
    }

    //================================================

    private class RiComp implements Comparator<RouteItem> {
        public int compare(RouteItem a,RouteItem b) {
            return a.position - b.position;
        }
    }

    private void sortRouteItems( RouteItem[] items ) throws FrameworkException {
        StringBuilder sb = new StringBuilder();

        Map<String,RouteItem> map = new HashMap<String,RouteItem>();

        for( RouteItem ri : items ) {
            if( sb.length() > 0 ) {
                sb.append(",");
            }
            sb.append(ri.routeNodeId);
            map.put(ri.routeNodeId,ri);
        }

        String ids = sb.toString();

        String res = MqlUtil.mqlCommand( document.context, "query connection where $1 select physicalid $2 $3 dump $4",
                "physicalid matchlist '" + ids + "' ','",
                "physicalid", "attribute[Route Sequence]", ","
        );

        if( res.length() > 0 ) {
            String[] lines = res.split("\n");

            for( String line : lines ) {
                String[] fields = (line+",dummy").split(",");
                String routeNodeId = fields[1];
                String position = fields[2];
                RouteItem ri = (RouteItem)map.get(routeNodeId);
                if( ri != null ) {
                    ri.position = Integer.parseInt(position);
                }
            }
        }

        Arrays.sort( items, new RiComp() );
    }

    //================================================
}
