package com.igatec.bop.core.model;

import com.igatec.utilsspring.utils.common.core.DBObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.igatec.bop.utils.Constants.*;

/**
 * Class represents Header Operation object
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class HeaderOperation extends DBObject {
    public static String MQL_TYPE = TYPE_HEADER_OPERATION_REFERENCE;

    public static List<String> derivatives = new ArrayList<>();

    public HeaderOperation(String id) {
        super(id);
    }

    public HeaderOperation() {
    }

    /**
     * Constructs Header Operation from data map
     *
     * @param data Map with data
     * @return Constructed Header Operation object
     */
    public static HeaderOperation fromMap(Map data) {
        String id = (String) data.get(SELECT_PHYSICALID);
        HeaderOperation dbObj = new HeaderOperation(id);
        dbObj.setInfo(data);
        return dbObj;
    }

    @Override
    protected String getDefaultPolicy() {
        return POLICY_VPLM_SMB_DEFINITION;
    }

    @Override
    protected String getDefaultType() {
        return TYPE_HEADER_OPERATION_REFERENCE;
    }

    @Override
    protected void throwIfTypeNotCorrect(String type) {
        if (TYPE_HEADER_OPERATION_REFERENCE.equals(type)) {
            return;
        }
        for (String derivative : derivatives) {
            if (derivative.equals(type)) {
                return;
            }
        }

        throw new IllegalArgumentException(String.format("Type for bookmark object must be %s, or one of its derivatives, but found %s", TYPE_HEADER_OPERATION_REFERENCE, type));
    }
}
