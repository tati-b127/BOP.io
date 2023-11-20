package com.igatec.bop.services;

import com.igatec.bop.core.dao.PPRContextDAO;
import com.igatec.bop.utils.Util;
import com.igatec.bop.core.dao.FunctionDAO;
import com.igatec.bop.core.model.Function;
import com.igatec.utilsspring.utils.common.core.DBObject;
import com.igatec.utilsspring.utils.common.core.Selector;
import com.igatec.utilsspring.utils.services.ObjectDAO;
import com.matrixone.apps.domain.util.FrameworkException;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.igatec.bop.utils.Constants.DELIMITER_COMMA;
import static com.igatec.bop.utils.Constants.PARAM_INSTANCE_ID;
import static com.igatec.bop.utils.Constants.TYPE_KIT_MFG_PRODUCED_PART;
import static com.igatec.bop.utils.Constants.TYPE_MFG_PRODUCTION_PLANNING;

/**
 * Class represents methods to work with Systems
 */
@Service
public class FunctionService {

    @Autowired
    @ObjectDAO("Function")
    FunctionDAO functionDAO;

    @Autowired
    @ObjectDAO("PPRContext")
    PPRContextDAO pprContextDAO;

    /**
     * Get info about Function objects
     *
     * @param where   filter MQL valid
     * @param selects list of selects
     * @return list of DBObject
     */
    public List<DBObject> get(String where, Selector selects) throws FrameworkException {
        return functionDAO.findObjects(where, selects);
    }

    /**
     * Modify MBOM obj
     *
     * @param func        MBOM object id
     * @param instancePID instance pid to given MBOM object in the structure
     * @return Function DBObject
     */
    public Function getInfo(Function func, String instancePID) {
        return functionDAO.get(func, instancePID);
    }

    public DBObject modify(Function func, String pBody, String instancePID) throws Exception {
        return functionDAO.modify(func, pBody, instancePID);
    }

    /**
     * Modify MBOM instance obj
     *
     * @param func        MBOM object id
     * @param instancePID instance pid to given MBOM object in the structure
     * @return DBObject
     */
    public DBObject modifyInstance(Function func, String pBody, String instancePID) throws Exception {
        return functionDAO.modifyInstance(func, pBody, instancePID);
    }

    /**
     * Get all connected to given Function Functions objects
     *
     * @param product        Function object id
     * @param parentId       object id
     * @param selects        Comma separated list with object selects
     * @param recurseToLevel recurse level for expand
     * @return list of DBObject
     */
    @SneakyThrows
    public List<DBObject> getStructure(Function product,
                                       String parentId,
                                       String pprContextId,
                                       Selector selects,
                                       short recurseToLevel) {
        List<DBObject> functionObjects = functionDAO.getStructure(product, parentId, selects, recurseToLevel);

        List<DBObject> pprContexts = pprContextDAO.getBOMOperations(pprContextDAO.get(pprContextId), pprContextId, selects, (short) 0);
        List<String> producedPartFromOP = new ArrayList<>();

        pprContexts.forEach(dbObject -> {
            producedPartFromOP.addAll(
                    Util.getImplementedLinks(
                            pprContextDAO.getContext(),
                            (String) dbObject.getInfo().get(PARAM_INSTANCE_ID),
                            selects,
                            TYPE_MFG_PRODUCTION_PLANNING).stream()
                            .map(o -> (String) o.getInfo(PARAM_INSTANCE_ID)).collect(Collectors.toList()));
        });

        functionObjects.forEach(dbObject -> {
                    boolean indicationSetBop = false;
                    for (String instance : producedPartFromOP) {
                        if (dbObject.getInfo(PARAM_INSTANCE_ID) != null) {
                            if (instance.equals(dbObject.getInfo(PARAM_INSTANCE_ID))) {
                                indicationSetBop = true;
                                break;
                            }
                        }
                    }
                    dbObject.getInfo().put("indicationSetBop", indicationSetBop);
                }
        );

        return functionObjects;
    }

    /**
     * Insert Predecessor for MBOM object
     * (copy of 'Insert Predecessor' function in Manufactured Item Definition app)
     *
     * @param func           MBOM object
     * @param instancePID    instance pid to given MBOM object in the structure
     * @param parentId       id of parent MBOM for given MBOM object in the structure (used to correctly prepare response)
     * @param predecessorPID pid of MBOM object which stands for Material
     * @param selects        Comma separated list with object selects
     * @return list of DBObject
     */
    public List<DBObject> insertPredecessor(Function func,
                                            String instancePID,
                                            String parentId,
                                            String predecessorPID,
                                            Selector selects) {
        return functionDAO.insertPredecessor(func, instancePID, parentId, predecessorPID, selects);
    }

    /**
     * Insert Predecessor for MBOM object in case of additional materials
     * (copy of 'Insert Predecessor' function in Manufactured Item Definition app)
     *
     * @param func           MBOM object
     * @param parentId       id of parent MBOM for given MBOM object in the structure (used to correctly prepare response)
     * @param predecessorPID pid of MBOM object which stands for Material
     * @param selects        Comma separated list with object selects
     * @return list of DBObject
     */
    public List<DBObject> insertPredecessor2(Function func,
                                             String parentId,
                                             String predecessorPID,
                                             Selector selects) {
        return functionDAO.insertPredecessor2(func, parentId, predecessorPID, selects);
    }
}
