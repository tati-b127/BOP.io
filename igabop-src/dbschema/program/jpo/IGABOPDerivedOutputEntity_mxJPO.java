import com.dassault_systemes.platform.model.CommonWebException;
import com.dassault_systemes.platform.model.Oxid;
import com.dassault_systemes.platform.model.PathHolder;
import com.dassault_systemes.platform.model.SRHolder;
import com.dassault_systemes.platform.model.implement.TreeOrderServices;
import com.dassault_systemes.platform.model.itf.IOxidService;
import com.dassault_systemes.platform.model.services.IdentificationServicesProvider;
import com.dassault_systemes.platform.model.services.MdIDBaseServices;
import com.matrixone.apps.common.Document;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.*;
import com.matrixone.plmql.cmd.PLMID;
import matrix.db.*;
import matrix.util.StringList;
import matrix.util.UUID;

import java.util.Set;
import java.util.*;
import java.util.stream.Collectors;

public class IGABOPDerivedOutputEntity_mxJPO implements DomainConstants {
    //params
    public static final String PARAM_COMPOSEE = "composee";
    //basic Selects
    private static final String SELECT_PHYSICALID = "physicalid";
    private static final String SELECT_LOGICALID = "logicalid";
    private static final String SELECT_MAJORID = "majorid";
    private static final String SELECT_PATHS_SR_EL_0_PHYSICALID = "paths[SemanticRelation].path.element[0].physicalid";
    private static final String SELECT_PATHS_SR_EL_0_TYPE = "paths[SemanticRelation].path.element[0].type";
    //Vaults
    private static final String VAULT_ESERVICE_PRODUCTION = "eService Production";
    //Relationships
    private static final String RELATIONSHIP_PLM_CONNECTION = "VPLMrel/PLMConnection/V_Owner";
    private static final String RELATIONSHIP_DERIVED_OUTPUT_RELATIONSHIP = "DerivedOutputRelationship";
    private static final String RELATIONSHIP_XCAD_BASE_DEPENDENCY = "XCADBaseDependency";
    //Types
    private static final String TYPE_PLM_CONNECTION = "PLMConnection";
    private static final String TYPE_PLM_DOC_CONNECTION = "PLMDocConnection";
    private static final String TYPE_DOCUMENT = "Document";
    private static final String TYPE_PROCESS_CAN_USE_CNX = "DELAsmProcessCanUseCnx";
    //Policies
    private static final String POLICY_VPLM_SMB_CX_DEFINITION = "VPLM_SMB_CX_Definition";
    private static final String POLICY_DOCUMENT_RELEASE = "Document Release";
    //Interfaces
    private static final String INTERFACE_PLM_CORE_STREAM_STORAGE = "PLMCoreStreamStorage";
    //Attributes
    private static final String ATTRIBUTE_STREAM = String.format("%s.%s", INTERFACE_PLM_CORE_STREAM_STORAGE, "Stream");
    private static final String ATTRIBUTE_ID_REL = "IDRel";
    private static final String ATTRIBUTE_APP_INDEX = "AppIndex";
    private static final String ATTRIBUTE_PRIVATE_DATA = "PrivateData";
    private static final String ATTRIBUTE_V_CNX_TREE_ORDER = String.format("%s.%s", TYPE_PLM_CONNECTION, "V_CnxTreeOrder");
    private static final String ATTRIBUTE_V_RESOURCES_QUANTITY = String.format("%s.%s", TYPE_PROCESS_CAN_USE_CNX, "V_ResourcesQuantity");
    //Default attribute values
    private static final String DEFAULT_STREAM_DOC = "Ib4AAAB4XvP3dQoONtRlYw4uSE0ulvcP9m1tavrN75KfXJqbmlcSlFoQlJr2p/G5QlPT38Z/2pw+mXnZIYlJOam6oswBPr7O+bkFOakglYlFlS6JJYncjMpazi6MDCCgy8MMUu6RmJeSk1ocCJTx8WAHy0DkIQDERuaDxBwhyjAAWB0LTFhREQBZDCX6";//todo
    private static final String DEFAULT_PRIVATE_DATA = "@IypQRAAGAAAAAAAAADr/ei8AAAB4AWNkYmQLi/dLzE0Vc0/NSy1KzFEIriwuSc01AAFDYxMmjqLUsszizPw8Zkc9QwA3kg1eAAAAAQAAAQ==";//todo see MFN API to serach how to generate this value
    private static final String DEFAULT_APP_INDEX = "0";
    private static final String DEFAULT_ID_REL = "1";
    //for Semantics and Role attributes of SR
    private static final String SEMANTICS_REFERENCE = "Reference";
    private static final String ROLE_DMT_DOCUMENT = "DmtDocument";
    //delimiters
    private static final String DELIMITER_BELL = "\u0007";
    private static final String DELIMITER_COMMA = ",";

    //obj - multiple value from map (for example, for key "from.to.id") it can be String or StringList
    private static List<String> normalizeToList(Object obj) {
        List<String> list = new ArrayList<>();

        // 1) DomainObject.findObjects() return String with "BELL" char as delimiter: "id1id2..."
        if (obj instanceof String) {
            String str = (String) obj;
            if (str.contains(DELIMITER_BELL))
                list = Arrays.asList(str.split(DELIMITER_BELL));
            else
                list.add(str);
        } else if (obj instanceof StringList) {
            StringList sl = (StringList) obj;
            list = sl.toList();
        }

        return list;
    }

    /**
     * Trigger is invoked when checkin file into bo of DerivedOutputEntity type.
     * Creates new document and connect to VPMReference (VPMReference -> Drawing -> DerivedOutputEntity)
     */
    public int triggerCheckin(Context context, String args[]) {
        int result = 0;
        try {
            ContextUtil.startTransaction(context, true);
            String id = args[0];
            String fileName = args[1];
            String format = args[2];
            String dir = context.createWorkspace();

            if ("PDF".equals(format)) {
                MqlUtil.mqlCommand(context, "checkout bus $1 server format $2 file $3 $4", id, format, fileName, dir);

                String sSelectPID = String.format("to[%s].from.from[%s].to.%s", RELATIONSHIP_DERIVED_OUTPUT_RELATIONSHIP, RELATIONSHIP_XCAD_BASE_DEPENDENCY, SELECT_PHYSICALID);
                String productPID = MqlUtil.mqlCommand(context, "print bus $1 select $2 dump", id, sSelectPID);

                if (!productPID.equals("")) {
                    String sDocPLMID = createDocument(context, fileName, dir);

                    Set<String> idsToDelete = new HashSet<>();
                    String[] productPIDs = productPID.split(DELIMITER_COMMA);
                    for (String pid : productPIDs) {
                        List<Map> docs = getDocuments(context, pid);
                        List<Map> docsFiltered = docs.stream().filter(map -> fileName.equals(map.get(SELECT_ATTRIBUTE_TITLE))).collect(Collectors.toList());
                        docsFiltered.forEach(map -> {
                                String sId = (String) map.get(SELECT_PHYSICALID);
                                String sComposeeId = (String) map.get(PARAM_COMPOSEE);
                                idsToDelete.add(sId);
                                idsToDelete.add(sComposeeId);
                        });
                        String sComposeePLMID = createDocComposee(context, pid);
                        createSR(context, sComposeePLMID, new String[]{sDocPLMID}, SEMANTICS_REFERENCE, ROLE_DMT_DOCUMENT, DEFAULT_ID_REL);
                    }

                    if(!idsToDelete.isEmpty())
                        DomainObject.deleteObjects(context, idsToDelete.toArray(new String[]{}));
                }
            }

            ContextUtil.commitTransaction(context);
            return result;
        } catch (Exception ex) {
            ContextUtil.abortTransaction(context);
            ex.printStackTrace();
            return 1;
        }
    }

    /**
     * Creates Document and checks given file to it
     *
     * @param fileName name of the file
     * @param filePath file path on server
     */
    public String createDocument(Context context, String fileName, String filePath) throws Exception {
        String policyItemAlias = FrameworkUtil.getAliasForAdmin(context, "policy", POLICY_DOCUMENT_RELEASE, true);
        String typeItemAlias = FrameworkUtil.getAliasForAdmin(context, "type", TYPE_DOCUMENT, true);
        String sID = FrameworkUtil.autoName(context, typeItemAlias, null, policyItemAlias, VAULT_ESERVICE_PRODUCTION);
        Document document = new Document(sID);
        String sPID = document.getInfo(context, SELECT_PHYSICALID);

        String[] attrs = {sPID, ATTRIBUTE_TITLE, fileName};
        MqlUtil.mqlCommand(context, "mod bus $1 $2 $3", attrs);

        document.lock(context);
        document.checkinFile(context, true, false, context.getClientHost(), FORMAT_GENERIC, fileName, filePath);

        return getPLMID(context, sPID);
    }

    /**
     * Connect Document to the Instruction
     *
     * @param pid BOP entity physicalid
     */
    private String createDocComposee(Context context, String pid) throws Exception {
        String sComposeePLMID = createComposee(context, TYPE_PLM_DOC_CONNECTION, POLICY_VPLM_SMB_CX_DEFINITION, pid, RELATIONSHIP_PLM_CONNECTION);
        String sComposeePID = MdIDBaseServices.getPhysicalIDfromPLMId(sComposeePLMID);
        //post process
        //add interface
        MqlUtil.mqlCommand(context, "mod bus $1 add interface $2", sComposeePID, INTERFACE_PLM_CORE_STREAM_STORAGE);
        MqlUtil.mqlCommand(context, "mod bus $1 $2 $3", sComposeePID, ATTRIBUTE_STREAM, DEFAULT_STREAM_DOC);

        return sComposeePLMID;
    }

    private String createComposee(Context context, String sType, String sPolicy, String sPIDComposer, String sComposition) throws FrameworkException, CommonWebException {
        String sName = UUID.getNewUUIDHEXString();
        String sRev = "";
        String addCmd = "add bus $1 $2 $3 policy $4 composer $5 composition $6 select $7 dump";
        String sComposeePID = MqlUtil.mqlCommand(context, addCmd, sType, sName, sRev, sPolicy, sPIDComposer, sComposition, SELECT_PHYSICALID);

        //POST PROCESS
        //set pid as name
        MqlUtil.mqlCommand(context, "mod bus $1 name $2", sComposeePID, sComposeePID);
        //mod attributes
        DomainObject domObj = DomainObject.newInstance(context, sComposeePID);
        Map<String, String> attrMap = new HashMap<>();
        attrMap.put(ATTRIBUTE_V_CNX_TREE_ORDER, Double.toString(TreeOrderServices.createDefaultTreeOrderValue()));
        domObj.setAttributeValues(context, attrMap);

        return getPLMID(context, sComposeePID);
    }

    private Path createSR(Context context, String sOwnerPLMID, String[] elementIds, String semantics, String role, String sIDRel) throws Exception {
        //alternate variant
        IOxidService iOxidService = IdentificationServicesProvider.getOxidService();
        Oxid oxid = iOxidService.getOxidFromPLMId(context, sOwnerPLMID);
        //String[] elementIds = new String[]{sElementPID};
        PathHolder pathHolder = new PathHolder(elementIds);
        List<PathHolder> lPH = new ArrayList<>();
        lPH.add(pathHolder);
        SRHolder srHolder = new SRHolder(lPH, semantics, role);
        IGABOPSRService_mxJPO srService = new IGABOPSRService_mxJPO();
        List<String> pathIds = srService.createSR(context, oxid, srHolder);//contains list of path ids

        //POST PROCESS
        AttributeList attrList = new AttributeList();
        attrList.addElement(new Attribute(new AttributeType(ATTRIBUTE_APP_INDEX), DEFAULT_APP_INDEX));
        attrList.addElement(new Attribute(new AttributeType(ATTRIBUTE_PRIVATE_DATA), DEFAULT_PRIVATE_DATA));
        attrList.addElement(new Attribute(new AttributeType(ATTRIBUTE_ID_REL), sIDRel));

        //expect working with only one path instance
        Path path = new Path(pathIds.get(0));
        path.setAttributeValues(context, attrList);

        return path;
    }

    private String getPLMID(Context context, String pid) throws FrameworkException {
        String result = MqlUtil.mqlCommand(context, "print bus $1 select $2 $3 $4 $5 dump",
                pid, SELECT_TYPE, SELECT_PHYSICALID, SELECT_LOGICALID, SELECT_MAJORID);
        String[] data = result.split(DELIMITER_COMMA);
        return new PLMID(data[0], pid, data[2], data[3]).toString();
    }

    /**
     * Get all connected to given Header Operation (or Instruction) Document objects
     */
    private List<Map> getDocuments(Context context, String pid) throws Exception {
        StringList objSelects = new StringList();
        objSelects.add(SELECT_PHYSICALID);
        objSelects.add(SELECT_PATHS_SR_EL_0_PHYSICALID);
        objSelects.add(SELECT_PATHS_SR_EL_0_TYPE);
        StringList relSelects = new StringList();

        DomainObject givenObj = DomainObject.newInstance(context, pid);
        MapList mapList = givenObj.getRelatedObjects(context, RELATIONSHIP_PLM_CONNECTION, TYPE_PLM_DOC_CONNECTION,
                objSelects, relSelects, false, true, (short) 1, null, null, 0);

        List<String> sDocPIDs = new ArrayList<>();//collect Resource id for further getting info
        Map<String, String> mIdToComposee = new HashMap<>();//serve to match Res pid against its composee pid (for edit/delete)
        for (Map map : (List<Map>)mapList) {
            List<String> slPids = normalizeToList(map.get(SELECT_PATHS_SR_EL_0_PHYSICALID));
            String docPID = slPids.get(0);
            mIdToComposee.put(docPID, (String) map.get(SELECT_PHYSICALID));
            sDocPIDs.add(docPID);
        }

        //collects doc info
        objSelects.clear();
        objSelects.add(SELECT_PHYSICALID);
        objSelects.add(SELECT_ATTRIBUTE_TITLE);
        MapList results = DomainObject.getInfo(context, sDocPIDs.toArray(new String[]{}), objSelects);

        for (Map map : (List<Map>)results) {
            String sPid = (String) map.get(SELECT_PHYSICALID);
            map.put(PARAM_COMPOSEE, mIdToComposee.get(sPid));
        }

        return results;
    }
}