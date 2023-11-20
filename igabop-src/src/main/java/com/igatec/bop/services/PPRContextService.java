package com.igatec.bop.services;

import com.igatec.bop.core.dao.FunctionDAO;
import com.igatec.bop.core.dao.PPRContextDAO;
import com.igatec.bop.core.dao.ProductDAO;
import com.igatec.bop.core.model.PPRContext;
import com.igatec.bop.core.model.Product;
import com.igatec.bop.utils.Util;
import com.igatec.utilsspring.utils.common.core.DBObject;
import com.igatec.utilsspring.utils.common.core.Selector;
import com.igatec.utilsspring.utils.services.ObjectDAO;
import com.matrixone.apps.domain.util.FrameworkException;
import lombok.SneakyThrows;
import matrix.util.StringList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.igatec.bop.utils.Constants.PARAM_INSTANCE_ID;
import static com.igatec.bop.utils.Constants.SELECT_TYPE;
import static com.igatec.bop.utils.Constants.TYPE_KIT_MFG_ASSEMBLY;
import static com.igatec.bop.utils.Constants.TYPE_KIT_MFG_PRODUCED_PART;
import static com.igatec.bop.utils.Constants.TYPE_MFG_PRODUCTION_PLANNING;
import static com.igatec.bop.utils.Constants.TYPE_PPR_CONTEXT;
import static com.igatec.bop.utils.Constants.TYPE_PPR_CONTEXT_PROCESS;
import static com.igatec.bop.utils.Constants.TYPE_PPR_CONTEXT_SYSTEM;

/**
 * Class represent methods to work with PPRContext object
 */
@Service
public class PPRContextService {

    @Autowired
    @ObjectDAO("PPRContext")
    PPRContextDAO pprContextDAO;
    @Autowired
    @ObjectDAO("Function")
    FunctionDAO functionDAO;
    @Autowired
    @ObjectDAO("Product")
    ProductDAO productDAO;

    /**
     * Get info about PPRContext objects
     *
     * @param where   filter MQL valid
     * @param selects list of selects
     * @return list of DBObject
     */
    public List<DBObject> get(String where, Selector selects) throws FrameworkException {
        return pprContextDAO.findObjects(where, selects);
    }

    public PPRContext getById(String id) throws FrameworkException {
        return pprContextDAO.get(id);
    }

    /**
     * Get info about Function
     *
     * @param id      Function object id
     * @param selects selector
     * @return list of DBObject
     */
    public List<DBObject> getInfo(String id, Selector selects) throws FrameworkException {
        List<DBObject> pprContexts = getRoot(id, selects);
        return pprContexts;
    }

    /**
     * Get info about Function
     *
     * @param id Function object id
     */
    public List<DBObject> getRoot(String id, Selector selector) throws FrameworkException {
        StringList objSelects = Util.getDefaultObjSelects();
        objSelects.addAll(selector.toStringList());
        DBObject obj = pprContextDAO.get(id, objSelects);
        List<DBObject> list = new ArrayList<>();
        list.add(obj);
        pprContextDAO.contextExtendData(list, selector);

        if(TYPE_PPR_CONTEXT.equals(obj.getBasicFieldName(SELECT_TYPE))) {
            list.addAll(getStructure((PPRContext) obj, id, selector, (short) 0));
        }else {
            list.addAll(productDAO.getStructure((Product) obj, id, selector, (short) 0));
        }
        return list;
    }

    /**
     * Get all connected to given PPRContext General System, MBOM and EBOM objects
     *
     * @param ppr            PPRContext object id
     * @param recurseToLevel level to expand. 0 - stands for all
     */
    @SneakyThrows
    public List<DBObject> getStructure(PPRContext ppr, String parentId, Selector selects, short recurseToLevel) {
        String sComposeeType = String.format("%s,%s", TYPE_PPR_CONTEXT_SYSTEM, TYPE_PPR_CONTEXT_PROCESS);
        List<DBObject> structure = pprContextDAO.getEBOM(ppr, parentId, selects, recurseToLevel);
        structure.addAll(pprContextDAO.getComposeeStructure(ppr, parentId, selects, sComposeeType, recurseToLevel));
        checkSetBop(ppr.getPhysicalid(), structure, selects);
        return structure;
    }

    @SneakyThrows
    public List<DBObject> checkSetBop(String contextId, List<DBObject> pprContexts, Selector selects) {
        List<String> producedPartFromOP = new ArrayList<>();

        List<DBObject> operations = pprContextDAO.getBOMOperations(pprContextDAO.get(contextId), contextId, selects, (short) 0);

        operations.forEach(dbObject -> {
            producedPartFromOP.addAll(
                    Util.getImplementedLinks(
                            pprContextDAO.getContext(),
                            (String) dbObject.getInfo().get(PARAM_INSTANCE_ID),
                            selects,
                            TYPE_MFG_PRODUCTION_PLANNING).stream()
                            .map(o -> (String) o.getInfo(PARAM_INSTANCE_ID)).collect(Collectors.toList()));
        });

        pprContexts.forEach(dbObject -> {
                    boolean indicationSetBop = false;
                    for (String instance : producedPartFromOP) {
                        if (dbObject.getInfo(PARAM_INSTANCE_ID) != null) {
                            if (instance.equals(dbObject.getInfo(PARAM_INSTANCE_ID))) {
                                indicationSetBop = true;
                                break;
                            }
                        }
                    }
                    if (dbObject.getBasicFieldName(SELECT_TYPE).equals(TYPE_KIT_MFG_ASSEMBLY) ||
                            dbObject.getBasicFieldName(SELECT_TYPE).equals(TYPE_KIT_MFG_PRODUCED_PART)) {
                        if (producedPartFromOP.contains(dbObject.getPhysicalid())) {
                            indicationSetBop = true;
                        }
                    }

                    dbObject.getInfo().put("indicationSetBop", indicationSetBop);
                }
        );

        return pprContexts;
    }

    /**
     * Get all connected to given PPRContext General System objects
     *
     * @param ppr      PPRContext object id
     * @param parentId object id
     * @param selects  selector
     * @return list of DBObject
     */
    public List<DBObject> getSystems(PPRContext ppr, String parentId, Selector selects) {
        return pprContextDAO.getSystems(ppr, parentId, selects);
    }

    /**
     * Get all connected to the given PPRContext template General System objects
     *
     * @param ppr     PPRContext object id
     * @param selects selector
     * @return list of DBObject
     */
    public List<DBObject> getCandidates(PPRContext ppr, Selector selects) {
        return pprContextDAO.getCandidates(ppr, selects);
    }

    /**
     * @param ppr
     * @param gsysId
     * @param isDuplicate
     * @param type
     * @param selects
     * @return
     */
    public List<DBObject> insertGeneralSystem(DBObject ppr, String gsysId, String isDuplicate, String type, Selector selects) {
        return pprContextDAO.insertGeneralSystem(ppr, gsysId, isDuplicate, type, selects);
    }
}
