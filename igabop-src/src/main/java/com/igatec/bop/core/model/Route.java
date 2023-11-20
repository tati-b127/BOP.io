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
public class Route extends DBObject {
    public static String MQL_TYPE = TYPE_ROUTE;

    public static List<String> derivatives = new ArrayList<>();

    public Route(String id) {
        super(id);
    }

    public Route() {
    }

    /**
     * Constructs Enovia obj from data map
     *
     * @param data Map with data
     * @return Constructed Enovia object
     */
    public static Route fromMap(Map data) {
        String id = (String) data.get(SELECT_PHYSICALID);
        Route dbObj = new Route(id);
        dbObj.setInfo(data);
        return dbObj;
    }

    @Override
    protected String getDefaultPolicy() {
        return POLICY_ROUTE;
    }

    @Override
    protected String getDefaultType() {
        return TYPE_ROUTE;
    }

    @Override
    protected void throwIfTypeNotCorrect(String type) {
        if (TYPE_ROUTE.equals(type)) {
            return;
        }
        for (String derivative : derivatives) {
            if (derivative.equals(type)) {
                return;
            }
        }

        throw new IllegalArgumentException(String.format("Type for bookmark object must be %s, or one of its derivatives, but found %s", TYPE_ROUTE, type));
    }
}
