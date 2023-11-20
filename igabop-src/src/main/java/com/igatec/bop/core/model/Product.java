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
public class Product extends DBObject {
    public static String MQL_TYPE = TYPE_VPM_REFERENCE;

    public static List<String> derivatives = new ArrayList<>();

    public Product(String id) {
        super(id);
    }

    public Product() {
    }

    /**
     * Constructs Product from data map
     *
     * @param data Map with data
     * @return Constructed Product object
     */
    public static Product fromMap(Map data) {
        String id = (String) data.get(SELECT_PHYSICALID);
        Product dbObj = new Product(id);
        dbObj.setInfo(data);
        return dbObj;
    }

    @Override
    protected String getDefaultPolicy() {
        return POLICY_VPLM_SMB_EVALUATION;
    }

    @Override
    protected String getDefaultType() {
        return TYPE_VPM_REFERENCE;
    }

    @Override
    protected void throwIfTypeNotCorrect(String type) {
        if (TYPE_VPM_REFERENCE.equals(type)) {
            return;
        }
        for (String derivative : derivatives) {
            if (derivative.equals(type)) {
                return;
            }
        }

        throw new IllegalArgumentException(String.format("Type for bookmark object must be %s, or one of its derivatives, but found %s", TYPE_VPM_REFERENCE, type));
    }
}
