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
 * Class represents General System object
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class GeneralSystem extends DBObject {
    public static String MQL_TYPE = TYPE_GENERAL_SYSTEM;

    public static List<String> derivatives = new ArrayList<>();

    public GeneralSystem(String id) {
        super(id);
    }

    public GeneralSystem() {
    }

    /**
     * Constructs DELLmiGeneralSystemReference from data map
     *
     * @param data Map with data
     * @return Constructed General System object
     */
    public static GeneralSystem fromMap(Map data) {
        String id = (String) data.get(SELECT_PHYSICALID);
        GeneralSystem dbObj = new GeneralSystem(id);
        dbObj.setInfo(data);
        return dbObj;
    }

    @Override
    protected String getDefaultPolicy() {
        return POLICY_VPLM_SMB_DEFINITION;
    }

    @Override
    protected String getDefaultType() {
        return TYPE_GENERAL_SYSTEM;
    }

    @Override
    protected void throwIfTypeNotCorrect(String type) {
        if (TYPE_GENERAL_SYSTEM.equals(type)) {
            return;
        }
        for (String derivative : derivatives) {
            if (derivative.equals(type)) {
                return;
            }
        }

        throw new IllegalArgumentException(String.format("Type for bookmark object must be %s, or one of its derivatives, but found %s", TYPE_GENERAL_SYSTEM, type));
    }
}
