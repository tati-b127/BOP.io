package com.igatec.bop.core.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.igatec.bop.core.model.GeneralSystem;
import com.igatec.bop.core.model.Instruction;
import com.igatec.bop.utils.Util;
import com.igatec.utilsspring.annotations.MatrixTransactional;
import com.igatec.utilsspring.utils.common.core.DBObject;
import com.igatec.utilsspring.utils.common.core.Selector;
import com.igatec.utilsspring.utils.common.dao.DBObjectDAO;
import com.igatec.utilsspring.utils.services.ObjectDAO;
import com.matrixone.apps.common.Person;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.ContextUtil;
import lombok.SneakyThrows;
import matrix.db.Context;
import matrix.util.StringList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.igatec.bop.utils.Constants.ATTRIBUTE_KIT_PREP_TIME;
import static com.igatec.bop.utils.Constants.ATTRIBUTE_KIT_TPCS;
import static com.igatec.bop.utils.Constants.ATTRIBUTE_V_ESTIMATED_TIME;
import static com.igatec.bop.utils.Constants.ATTRIBUTE_V_TREE_ORDER;
import static com.igatec.bop.utils.Constants.DELIMITER_COMMA;
import static com.igatec.bop.utils.Constants.RELATIONSHIP_GENERAL_SYSTEM_INSTANCE;
import static com.igatec.bop.utils.Constants.RELATIONSHIP_HEADER_OPERATION_INSTANCE;
import static com.igatec.bop.utils.Constants.RELATIONSHIP_INSTRUCTION_INSTANCE;
import static com.igatec.bop.utils.Constants.ROLE_TIME_ANALYST;
import static com.igatec.bop.utils.Constants.SELECT_ATTRIBUTE_VALUE;
import static com.igatec.bop.utils.Constants.SELECT_INTERFACE;
import static com.igatec.bop.utils.Constants.SELECT_PATH;
import static com.igatec.bop.utils.Constants.TYPE_GENERAL_SYSTEM;
import static com.igatec.bop.utils.Constants.TYPE_HEADER_OPERATION_REFERENCE;
import static com.igatec.bop.utils.Constants.TYPE_INSTRUCTION_REFERENCE;
import static com.igatec.bop.utils.Constants.TYPE_MFG_PRODUCTION_PLANNING;

@Repository
@MatrixTransactional
@ObjectDAO("Instruction")
public class InstructionDAO extends DBObjectDAO<Instruction> {
    /**
     * Modify Work Instruction attributes values
     *
     * @param instr   Work Instruction object id
     * @param selects comma separated list with MQL object selects for response data
     */
    public List<DBObject> modify(DBObject instr, String pBody, Selector selects) throws Exception {
        //todo it's wa
        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(pBody);
        Map<String, Object> attrMap = mapper.convertValue(json, new TypeReference<Map<String, Object>>() {
        });

        Context context = getContext();
        Person p = Person.getPerson(context);
        boolean isTimeAnalyst = p.hasRole(context, ROLE_TIME_ANALYST);
        boolean isTimeAttr = attrMap.containsKey(ATTRIBUTE_KIT_PREP_TIME) || attrMap.containsKey(ATTRIBUTE_KIT_TPCS);
        if (isTimeAttr)
            attrMap.put(ATTRIBUTE_V_ESTIMATED_TIME, attrMap.get(ATTRIBUTE_KIT_TPCS));

        try {
            if (isTimeAnalyst && isTimeAttr)
                ContextUtil.pushContext(context);

            DomainObject domObj = DomainObject.newInstance(context, instr.getPhysicalid());
            domObj.setAttributeValues(context, attrMap);
        } finally {
            if (isTimeAnalyst && isTimeAttr)
                ContextUtil.popContext(context);
        }

        List<String> ids = new ArrayList<>();
        ids.add(instr.getPhysicalid());
        return Util.extendDataPro(getContext(), ids, null, null, selects);
    }

    /**
     * Creates Document and checks given file in it
     *
     * @param obj   Work Instruction or Operation object id
     * @param mFile File
     */
    public List<DBObject> createDocument(DBObject obj, MultipartFile mFile, Selector selects) throws Exception {
        Context context = getContext();
        String filePath = context.createWorkspace();//todo for server running
//        String filePath = System.getProperty("user.dir");//todo for local running

        byte[] bytes = mFile.getBytes();
        File file = new File(filePath + File.separator + mFile.getOriginalFilename());
        BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(file));
        stream.write(bytes);
        stream.close();

        String sDocPID = Util.attachDocument(context, obj, mFile.getOriginalFilename(), filePath);

        List<String> ids = new ArrayList<>();
        ids.add(obj.getPhysicalid());
        return Util.extendDataPro(getContext(), ids, null, null, selects);
    }

    /**
     * Connect Documents to the Instruction
     *
     * @param obj  Work Instruction or Operation object id
     * @param docs list of docs
     */
    public void attachDocuments(DBObject obj, List<DBObject> docs) throws Exception {
        Context context = getContext();
        Util.attachDocuments(context, obj, docs);
    }

    /**
     * Get all connected to given Header Operation (or Instruction) Document objects
     *
     * @param dbObject Header Operation (Instruction) object id
     */
    @SneakyThrows
    public List<DBObject> getDocuments(DBObject dbObject, Selector selects) {
        return Util.getDocuments(getContext(), dbObject, selects);
    }

    /**
     * Deletes Instruction and/or its Composee. In case of composee it means deleting link
     *
     * @param obj Instruction object
     * @param id  comma separated ids to delete
     */
    @SneakyThrows
    public boolean delete(DBObject obj, @NotNull String id, Selector selects, String bopPath) {
        Context context = getContext();
        Util.delete(context, id, true);
        return true;
    }

    public List<DBObject> getMboms(String bopInstId, Selector selects) {
       return Util.getImplementedLinks(getContext(), bopInstId, selects, TYPE_MFG_PRODUCTION_PLANNING);
    }

    //todo remove it as it exact copy of systemDAO getStructure
    /**
     * Get given General System's structure including General System, Header Operation and Instruction objects
     *
     * @param gsys           General System object id
     * @param selects        Comma separated list with object selects
     * @param recurseToLevel recurse level for expand
     */
    @SneakyThrows
    public List<DBObject> getSystemStructure(GeneralSystem gsys, String parentId, Selector selects, short recurseToLevel) {
        String[] rels = new String[]{RELATIONSHIP_GENERAL_SYSTEM_INSTANCE, RELATIONSHIP_HEADER_OPERATION_INSTANCE, RELATIONSHIP_INSTRUCTION_INSTANCE};
        String[] types = new String[]{TYPE_GENERAL_SYSTEM, TYPE_HEADER_OPERATION_REFERENCE, TYPE_INSTRUCTION_REFERENCE};
        String relPattern = String.join(DELIMITER_COMMA, rels);
        String typePattern = String.join(DELIMITER_COMMA, types);
        StringList relSelects = Util.getDefaultRelSelects();
        relSelects.add(SELECT_ATTRIBUTE_VALUE);
        relSelects.add(SELECT_INTERFACE);
        StringList objSelects = Util.getDefaultObjSelects();
        objSelects.addAll(selects.toStringList());
        Util.appendByScopedSelectForBOP(objSelects);
        List<DBObject> list = getRelatedObjects(gsys, relPattern, typePattern,
                objSelects, relSelects, false, recurseToLevel, null, null, 0);

        list = Util.extendData(getContext(), list, parentId, relPattern, selects);

        String sTreeOrder = String.format("from[%s].attribute[%s]", relPattern, ATTRIBUTE_V_TREE_ORDER);
        return Util.sortByTreeOrder(list, sTreeOrder);
    }

    /**
     * Clone structure process
     */
    @SneakyThrows
    String cloneStructure(DBObject instr) {
        Context context = getContext();
        String newPID = Util.clone(context, instr.getPhysicalid(), false);

        //copy links to docs
        DBObject newObj = getInfo(new String[]{newPID}).get(0);
        Util.copyLinksToDocs(context, instr, newObj);

        return newPID;
    }
}
