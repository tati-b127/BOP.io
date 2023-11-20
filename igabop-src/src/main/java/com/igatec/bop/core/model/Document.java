package com.igatec.bop.core.model;

import com.igatec.utilsspring.utils.common.core.DBObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.igatec.bop.utils.Constants.*;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class Document extends DBObject {
    public static String MQL_TYPE = TYPE_DOCUMENT;

    public static List<String> derivatives = new ArrayList<>();

    protected Document(String id) {
        super(id);
    }

    public Document() {
    }

    /**
     * Constructs Document from data map
     *
     * @param data Map with data
     * @return Constructed Document object
     */
    public static Document fromMap(Map data) {
        String id = (String) data.get(SELECT_PHYSICALID);
        Document dbObj = new Document(id);
        dbObj.setInfo(data);
        return dbObj;
    }

    @Override
    protected String getDefaultPolicy() {
        return POLICY_DOCUMENT_RELEASE;
    }

    @Override
    protected String getDefaultType() {
        return TYPE_DOCUMENT;
    }

    @Override
    protected void throwIfTypeNotCorrect(String type) {
        if (TYPE_DOCUMENT.equals(type)) {
            return;
        }
        for (String derivative : derivatives) {
            if (derivative.equals(type)) {
                return;
            }
        }

        throw new IllegalArgumentException(String.format("Type for bookmark object must be %s, or one of its derivatives, but found %s", TYPE_DOCUMENT, type));
    }
}
