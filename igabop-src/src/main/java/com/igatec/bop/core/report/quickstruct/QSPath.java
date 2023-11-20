package com.igatec.bop.core.report.quickstruct;

public class QSPath {

    String type;
    public QSPathElement[] elements;

    //================================================

    public QSPath( String source ) {

        String[] sourceItems = source.split("\\^");

        type = sourceItems[0];

        int nElements = sourceItems.length-1;
        elements = new QSPathElement[nElements];
        for( int i=0; i<nElements; i++ ) {
            elements[i] = new QSPathElement(sourceItems[i+1]);
        }
    }

    //================================================

    public String toString() {
        StringBuilder sb = new StringBuilder();

        for( int i=0; i<elements.length; i++ ) {
            if( i > 0 ) {
                sb.append(", ");
            }
            sb.append( elements[i].toString() );
        }
        return sb.toString();
    }

    //================================================

    public boolean hasAnyOfTypes( String[] types ) {
        for( QSPathElement el : elements ) {
            for( String type : types ) {
                if( el.type.equals( type ) ) {
                    return true;
                }
            }
        }
        return false;
    }

    public QSPathElement[] getElements() {
        return elements;
    }

    //================================================
}
