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
 * Class represents Instruction object
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class Instruction extends DBObject {
    public static String MQL_TYPE = TYPE_INSTRUCTION_REFERENCE;

    public static List<String> derivatives = new ArrayList<>();

    protected Instruction(String id) {
        super(id);
    }

    public Instruction() {
    }

    /**
     * Constructs Instruction from data map
     *
     * @param data Map with data
     * @return Constructed Instruction object
     */
    public static Instruction fromMap(Map data) {
        String id = (String) data.get(SELECT_PHYSICALID);
        Instruction dbObj = new Instruction(id);
        dbObj.setInfo(data);
        return dbObj;
    }

    @Override
    protected String getDefaultPolicy() {
        return POLICY_VPLM_SMB_DEFINITION;
    }

    @Override
    protected String getDefaultType() {
        return TYPE_INSTRUCTION_REFERENCE;
    }

    @Override
    protected void throwIfTypeNotCorrect(String type) {
        if (TYPE_INSTRUCTION_REFERENCE.equals(type)) {
            return;
        }
        for (String derivative : derivatives) {
            if (derivative.equals(type)) {
                return;
            }
        }

        throw new IllegalArgumentException(String.format("Type for bookmark object must be %s, or one of its derivatives, but found %s", TYPE_INSTRUCTION_REFERENCE, type));
    }
}
