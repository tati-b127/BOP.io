import com.dassault_systemes.catrgn.connector.plm.services.PLMConnectorServices;
import com.dassault_systemes.platform.model.CommonWebException;
import com.dassault_systemes.platform.model.Oxid;
import com.dassault_systemes.platform.model.PathHolder;
import com.dassault_systemes.platform.model.SRHolder;
import com.dassault_systemes.platform.model.implement.SRService;
import com.dassault_systemes.platform.model.itf.IOxidService;
import com.dassault_systemes.platform.model.services.IdentificationServicesProvider;
import com.dassault_systemes.platform.model.services.MdIDBaseServices;
import com.dassault_systemes.requirements.UnifiedAutonamingServices;
import com.matrixone.apps.common.Document;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MqlUtil;
import matrix.db.*;
import matrix.util.StringList;
import matrix.util.UUID;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IGAService_mxJPO implements DomainConstants {
  public void mxMain(Context context, String[] args) throws Exception {
/*    System.out.println();
    System.out.println("Check MdIDBaseServices.getPhysicalIDfromPLMId(plmid)");
    String s_plmid = "PLMIDv2(D35565C9000033085EA1C02B0002037D,D35565C9000033085EA1C02B0002037D,D35565C9000033085EA1C02B0002037D,DELLmiProductionGeneralSystem/DELLmiGeneralSystemReference,PLM1)";
    String pid = MdIDBaseServices.getPhysicalIDfromPLMId(s_plmid);
    System.out.println(pid);
    System.out.println();
    //
    System.out.println("Check new PLMID()");
    PLMID plmid = new PLMID(s_plmid);
    System.out.println(plmid);
    System.out.println();
    //random physicalid logicalid majorid updatestmp
    System.out.println("Check UUID.getNewUUIDHEXString()");
    System.out.println(UUID.getNewUUIDHEXString());*/

/*    String sPPR_PID = "D35565C900003B645E9FE0DF0000A4D8";
    BusinessObject boPPR = new BusinessObject(sPPR_PID);
    BusinessObject boOwner = BusinessObjectEditabilityServices.getOwner(context, boPPR, "VPLMrel/PLMConnection/V_Owner");
    boolean isDeletable = BusinessObjectEditabilityServices.isDeletable(context, boOwner, "Class/PPRContextSystemCnxDisc");
    boolean isEditable = BusinessObjectEditabilityServices.isEditable(context, boOwner);
    System.out.println(String.format("Owner is %s, isDeletable=%s, isEditable=%s", boPPR.getName(), isDeletable, isEditable));
    System.out.println();*/

/*    IPLMDictionaryEntityAdapter iplmDEA = IPLMDicoServices.PLMType2DicoType(context, "Class/PPRContextSystemCnxDisc");
    System.out.println(String.format("plmType=%s, coretype=%s, m1type=%s, name=%s", iplmDEA.getPLMType(context), iplmDEA.getCoreType(context), iplmDEA.getM1Type(context), iplmDEA.getName(context)));
    System.out.println(String.format("isPLMPort=%s, isPLMConnection=%s", iplmDEA.isPLMPort(context), iplmDEA.isPLMConnection(context)));
    System.out.println();*/

    //
/*    UIParameterForm uiParameterForm = new UIParameterForm();
    HashMap mp = new HashMap<>();
    MapList fields = new MapList();
    HashMap nameField = new HashMap<>();
    HashMap settings = new HashMap<>();
    mp.put("Name", UUID.getNewUUIDHEXString());
    mp.put("autoNameCheck", "false");
    mp.put("parentOID", "D35565C900003B645E9FE0DF0000A4D8");
    mp.put("relationship", "VPLMrel/PLMConnection/V_Owner");
    mp.put("direction", "from");
//    mp.put("TypeActual", "VPLMtyp/PPRContextProcessCnxDisc");
    mp.put("TypeActual", "PPRContextProcessCnxDisc");
    mp.put("fields", fields);
    fields.add(nameField);
    nameField.put("name", "Name");
    nameField.put("settings", settings);
    settings.put("nameMode", "autoName");
    String sid = uiParameterForm.createObject(context, mp);
    System.out.println(sid);
    System.out.println();*/

/*    Double d = TreeOrderServices.createDefaultTreeOrderValue();
    System.out.println(d);*/

    try {
      ContextUtil.startTransaction(context, true);
//      ContextUtil.pushContext(context, "vnalimov", "vnalimov", null);

      //copy of Insert System function in Planning Structure
      String sPPRContextPID = "D35565C900003B645E9FE0DF0000A4D8";//PPRContext  ppr-80422873-00000007 A.1
      String sGenSysPID = insertGeneralSystem(context, sPPRContextPID);

      //copy of Create a Scope in Planning Structure
      String sMBOMPID = "D35565C900003B645E9FE0DF0000A449";//Kit_MfgProducedPart cmt-80422873-00000037 A.1
      createScope(context, sGenSysPID, sMBOMPID);

      //copy of Insert Header Operation function in Planning Structure
      String sOperationPID = insertHeaderOperation(context, sGenSysPID);

      //copy of Create link Operation-Capable Resource function in Planning Structure
      String sResourcePID = "D35565C900003B645E9FE0DF0000A3AF";//ErgoHuman rwk-80422873-00000049 A.1
      createLinkToResource(context, sOperationPID, sResourcePID);

      //copy of Create Text Instruction function in Work Instructions app
      String sInstrPID = createWorkInstruction(context, sOperationPID);

      //copy of AttachDocument function in Work Instructions app
      String sDocPID = attachDocument(context, sInstrPID);

//      ContextUtil.popContext(context);
      ContextUtil.commitTransaction(context);
    } catch (Exception ex) {
      ContextUtil.abortTransaction(context);
      ex.printStackTrace();
    }

  }

  private String attachDocument(Context context, String sParentPID) throws Exception {
    String sType = "PLMDocConnection";
    String sPolicy = "VPLM_SMB_CX_Definition";
    String sComposition = "VPLMrel/PLMConnection/V_Owner";
    String sComposeePLMID = createComposee(context, sType, sPolicy, sParentPID, sComposition);
    String sComposeePID = MdIDBaseServices.getPhysicalIDfromPLMId(sComposeePLMID);
    //post process
    //add interface
    String sStreamVal = "Ib4AAAB4XvP3dQoONtRlYw4uSE0ulvcP9m1tavrN75KfXJqbmlcSlFoQlJr2p/G5QlPT38Z/2pw+mXnZIYlJOam6oswBPr7O+bkFOakglYlFlS6JJYncjMpazi6MDCCgy8MMUu6RmJeSk1ocCJTx8WAHy0DkIQDERuaDxBwhyjAAWB0LTFhREQBZDCX6";//todo
    MqlUtil.mqlCommand(context, "mod bus $1 add interface $2", sComposeePID, "PLMCoreStreamStorage");
    MqlUtil.mqlCommand(context, "mod bus $1 $2 $3", sComposeePID, "PLMCoreStreamStorage.Stream", sStreamVal);

    String sDocPLMID = createDocument(context);

    String sAttrSemantics = "Reference";
    String sAttrRole = "DmtDocument";
    createSR(context, sComposeePLMID, sDocPLMID, sAttrSemantics, sAttrRole);

    return MdIDBaseServices.getPhysicalIDfromPLMId(sDocPLMID);
  }

  private String createDocument(Context context) throws Exception {
/*    Document doc = new Document();
    StringList slFileNames = new StringList();
    slFileNames.add("testSearch.js");
    String sName = null;
    String sTitle = null;
    String sDesc = null;
    String sStore = null;//if null calculated from policy
    boolean append = true;
    String sFormat = null;//generic by default
    boolean lockUnlock = true;
    String sLanguage = null;
    doc = doc.checkinCreate(context, slFileNames, sName, sTitle, sDesc, sStore, append, sFormat, lockUnlock, sLanguage);
    String sPID = doc.getInfo(context, "physicalid");*/

    String sType = "Document";
    String sPolicy = "Document Release";
    String sVault = "eService Production";
    String policyItemAlias = FrameworkUtil.getAliasForAdmin(context, "policy", sPolicy, true);
    String typeItemAlias = FrameworkUtil.getAliasForAdmin(context, "type", sType, true);
    String sID = FrameworkUtil.autoName(context, typeItemAlias, null, policyItemAlias, sVault);
    Document document = new Document(sID);
//    document.setAttributeValue(context, ATTRIBUTE_FILE_VERSION, "0");
    String sPID = document.getInfo(context, "physicalid");
    System.out.println("del bus " + sPID + ";");

    String sFileName = "VersionString.txt";
    String sFilePath = "/usr/PLM/3DSpace";
//    java.io.File file = new java.io.File(String.format("%s%s%s", sFilePath, File.separator, sFileName));
//    document.checkin(context, file, true, false);
    document.lock(context);
    document.checkinFile(context, true, false, context.getClientHost(), FORMAT_GENERIC, sFileName, sFilePath);

    //POST PROCESS
    //change owner, set project and org
    MqlUtil.mqlCommand(context, "mod bus $1 owner $2 project $3 organization $4", sPID, "vnalimov", "Common Space", "Company Name");

    boolean isRelationship = false;
    return PLMConnectorServices.getPLMID(context, sPID, isRelationship);
  }

  private String createWorkInstruction(Context context, String sPIDParent) throws FrameworkException {
    String sType = "DELWkiInstructionReference";
    String sName = UnifiedAutonamingServices.autoname(context, sType);//use Requirement service
    String sPolicy = "VPLM_SMB_Definition";
    String sVault = "vplm";
    String sRev = "A.1";
    DomainObject instr = DomainObject.newInstance(context);
    instr.createObject(context, sType, sName, sRev, sPolicy, sVault);

    StringList objSelcets = new StringList();
    objSelcets.add("physicalid");
    objSelcets.add("versionid");
    Map info = instr.getInfo(context, objSelcets);
    String sPID = (String) info.get("physicalid");
    String sVID = (String) info.get("versionid");
    System.out.println("del bus " + sPID + ";");

    //POST PROCESS
    //connect to Operation
    DomainRelationship.connect(context, sPIDParent, "DELWkiInstructionInstance", sPID, true);
    //change owner, set project and org
    MqlUtil.mqlCommand(context, "mod bus $1 owner $2 project $3 organization $4", sPID, "vnalimov", "Common Space", "Company Name");
    //mod attributes (not required)
    Map<String,String> attrMap = new HashMap<>();
    attrMap.put("PLMEntity.V_Name", "VADInstruction");
    attrMap.put("PLMEntity.C_updatestamp", "-1");
    attrMap.put("PLMEntity.V_sec_level", "0");
    attrMap.put("PLMReference.V_order", "1");
    attrMap.put("PLMEntity.PLM_ExternalID", sName);
    attrMap.put("PLMReference.V_VersionID", sVID);
    instr.setAttributeValues(context, attrMap);

    return sPID;
  }

  private void createLinkToResource(Context context, String sOperationPID, String sResourcePID) throws Exception {
    String sType = "DELAsmProcessCanUseCnx";
    String sPolicy = "VPLM_SMB_CX_Definition";
    String sComposition = "VPLMrel/PLMConnection/V_Owner";
    String sComposeePLMID = createComposee(context, sType, sPolicy, sOperationPID, sComposition);

    createSR(context, sComposeePLMID, sOperationPID, "Reference3", "DEL_PCU_Owner");
    createSR(context, sComposeePLMID, sResourcePID, "Reference4", "DEL_PCU_Result");
  }

  private void createScope(Context context, String sPIDComposer, String sMBOMPID) throws Exception {
    String sType = "MfgProductionPlanning";
    String sPolicy = "VPLM_SMB_CX_Definition";
    String sComposition = "VPLMrel/PLMConnection/V_Owner";
    String sComposeePLMID = createComposee(context, sType, sPolicy, sPIDComposer, sComposition);

    createSR(context, sComposeePLMID, sPIDComposer, "Reference5", "PLM_ImplementLink_Source");
    createSR(context, sComposeePLMID, sMBOMPID, "Reference3", "PLM_ImplementLink_Target");
  }

  private String insertGeneralSystem(Context context, String sPIDComposer) throws Exception {
    String sType = "PPRContextSystemCnxDisc";
    String sPolicy = "VPLM_SMB_CX_Definition";
    String sComposition = "VPLMrel/PLMConnection/V_Owner";
    String sComposeePLMID = createComposee(context, sType, sPolicy, sPIDComposer, sComposition);
    String sComposeePID = MdIDBaseServices.getPhysicalIDfromPLMId(sComposeePLMID);
    //post process
    //add interface
    String sStreamVal = "IccAAAB4XvP3dQoONtRlYw4uSE0uVvUP9m1tavot7uLq45OZl+1WlJ8bEBAUkh9cWVySmvun8TlD099/2pwguZDEpJxUXVHmAB9f5/zcgpzU3NS8ksSiSpfEkkRuRmUtZxdGBhDQ5WEGKfdIzEvJSS0OAcr4eLCDZSDyEABiI/NBYs5MTEzsl0NTTzIwGHPELTygzcDEXINQzsAC06yoCACyuiya";//todo
    MqlUtil.mqlCommand(context, "mod bus $1 add interface $2", sComposeePID, "PLMCoreStreamStorage");
    MqlUtil.mqlCommand(context, "mod bus $1 $2 $3", sComposeePID, "PLMCoreStreamStorage.Stream", sStreamVal);

    String sGSType = "DELLmiGeneralSystemReference";
    String sGenSysPLMID = createGenSys(context, sGSType);
    String sGenSysPID = MdIDBaseServices.getPhysicalIDfromPLMId(sGenSysPLMID);

    String sPortType = "DELLmiProdSystemIOPort";
    String sPortPolicy = "VPLM_SMB_PORT_Definition";
    String sPortComposition = "VPLMrel/PLMPort/V_Owner";
    String sPortPLMID1 = createComposee(context, sPortType, sPortPolicy, sGenSysPID, sPortComposition);
    String sPortPLMID2 = createComposee(context, sPortType, sPortPolicy, sGenSysPID, sPortComposition);

    String sAttrSemantics = "Reference4";//in semanticsByNames map Reference4 = 6 (like existing instances)
    String sAttrRole = "PLM_PPRContextLink_System";//in rolesByNames map PLM_PPRContextLink_System = 246 (like existing instances)
    createSR(context, sComposeePLMID, sGenSysPLMID, sAttrSemantics, sAttrRole);

    return sGenSysPID;
  }

  private String insertHeaderOperation(Context context, String sPIDParent) throws Exception {
    String sType = "DELLmiHeaderOperationReference";
    String sName = UnifiedAutonamingServices.autoname(context, sType);//use Requirement service
    String sPolicy = "VPLM_SMB_Definition";
    String sVault = "vplm";
    String sRev = "A.1";
    DomainObject operation = DomainObject.newInstance(context);
    operation.createObject(context, sType, sName, sRev, sPolicy, sVault);

    StringList objSelcets = new StringList();
    objSelcets.add("physicalid");
    objSelcets.add("versionid");
    Map info = operation.getInfo(context, objSelcets);
    String sPID = (String) info.get("physicalid");
    String sVID = (String) info.get("versionid");
    System.out.println("del bus " + sPID + ";");

    //POST PROCESS
    //connect to General System
    DomainRelationship.connect(context, sPIDParent, "DELLmiHeaderOperationInstance", sPID, true);
    //change owner, set project and org
    MqlUtil.mqlCommand(context, "mod bus $1 owner $2 project $3 organization $4", sPID, "vnalimov", "Common Space", "Company Name");
    //mod attributes
    Map<String,String> attrMap = new HashMap<>();
    attrMap.put("PLMEntity.V_Name", "VADOperation");
    attrMap.put("PLMEntity.C_updatestamp", "-1");
    attrMap.put("PLMEntity.V_sec_level", "0");
    attrMap.put("PLMReference.V_order", "1");
    attrMap.put("PLMEntity.PLM_ExternalID", sName);
    attrMap.put("PLMReference.V_VersionID", sVID);
    operation.setAttributeValues(context, attrMap);

    return sPID;
  }

  private void createSR(Context context, String sOwnerPLMID, String sElementPLMID, String semantics, String role) throws Exception {
    //ProcessPIDServices processPIDServices = new ProcessPIDServices();
    //processPIDServices.createSR(context, sOwnerPLMID, sElementPLMID, semantics, role);

    //alternate variant
    IOxidService iOxidService = IdentificationServicesProvider.getOxidService();
    Oxid oxid = iOxidService.getOxidFromPLMId(context, sOwnerPLMID);
    String[] elementIds = new String[]{sElementPLMID};
    PathHolder pathHolder = new PathHolder(elementIds);
    List<PathHolder> lPH = new ArrayList<>();
    lPH.add(pathHolder);
    SRHolder srHolder = new SRHolder(lPH, semantics, role);
    SRService srService = new SRService();
    List<String> pathIds = srService.createSR(context, oxid, srHolder);//contains list of path ids

    //POST PROCESS
    AttributeList attrList = new AttributeList();
    String privateData = "@IypQRAAGAAAAAAAAADr/ei8AAAB4AWNkYmQLi/dLzE0Vc0/NSy1KzFEIriwuSc01AAFDYxMmjqLUsszizPw8Zkc9QwA3kg1eAAAAAQAAAQ==";//todo see MFN API to serach how to generate this value
    String appIndex = "0";
    attrList.addElement(new Attribute(new AttributeType("AppIndex"), appIndex));
    attrList.addElement(new Attribute(new AttributeType("PrivateData"), privateData));

    for (String pathId : pathIds) {
      System.out.println("del path " + pathId + ";");
      Path path = new Path(pathId);
      path.setAttributeValues(context, attrList);
    }
  }

  private String createGenSys(Context context, String sGSType) throws FrameworkException, CommonWebException {
    String sGSName = UnifiedAutonamingServices.autoname(context, sGSType);//use Requirement service
    String sGSPolicy = "VPLM_SMB_Definition";
    String sGSVault = "vplm";
    String sGSRev = "A.1";
    DomainObject gsys = DomainObject.newInstance(context);
    gsys.createObject(context, sGSType, sGSName, sGSRev, sGSPolicy, sGSVault);

    StringList objSelcets = new StringList();
    objSelcets.add("physicalid");
    objSelcets.add("versionid");
    Map info = gsys.getInfo(context, objSelcets);
    String sGSPID = (String) info.get("physicalid");
    String sVID = (String) info.get("versionid");
    System.out.println("del bus " + sGSPID + ";");

    //POST PROCESS
    //change owner, set project and org
    MqlUtil.mqlCommand(context, "mod bus $1 owner $2 project $3 organization $4", sGSPID, "vnalimov", "Common Space", "Company Name");
    //mod attributes
    Map<String,String> attrMap = new HashMap<>();
    attrMap.put("PLMEntity.V_Name", "VADGenSys");
    attrMap.put("PLMEntity.C_updatestamp", "-1");
    attrMap.put("PLMEntity.V_sec_level", "0");
    attrMap.put("PLMReference.V_order", "1");
    attrMap.put("PLMEntity.PLM_ExternalID", sGSName);
    attrMap.put("PLMReference.V_VersionID", sVID);
    gsys.setAttributeValues(context, attrMap);

    boolean isRelationship = false;
    return PLMConnectorServices.getPLMID(context, sGSPID, isRelationship);
  }

  private String createComposee(Context context, String sType, String sPolicy, String sPIDComposer, String sComposition) throws FrameworkException {
    String sName = UUID.getNewUUIDHEXString();
    String sRev = "";
/*    BusinessObject bo = new BusinessObject(sType, sName, sRev, null);
    bo.createAsComposee(context, sPolicy, sComposition, sPIDComposer);
    System.out.println(bo.getObjectId(context));*/
    String addCmd = "add bus $1 $2 $3 policy $4 composer $5 composition $6 select $7 dump";
    String sComposeePID = MqlUtil.mqlCommand(context, addCmd, sType, sName, sRev, sPolicy, sPIDComposer, sComposition, "physicalid");
    System.out.println("del bus " + sComposeePID + ";");

    //POST PROCESS
    //set pid as name
    MqlUtil.mqlCommand(context, "mod bus $1 name $2", sComposeePID, sComposeePID);
    //change owner, set project and org
    MqlUtil.mqlCommand(context, "mod bus $1 owner $2 project $3 organization $4", sComposeePID, "vnalimov", "Common Space", "Company Name");
    //mod attributes
    if(!"DELAsmProcessCanUseCnx".equals(sType) && !"PLMDocConnection".equals(sType)){
      DomainObject domObj = DomainObject.newInstance(context, sComposeePID);
      Map<String,String> attrMap = new HashMap<>();
      attrMap.put("PLMEntity.C_updatestamp", "-1");
      attrMap.put("PLMEntity.V_sec_level", "0");

      attrMap.put("PLMEntity.V_discipline", sType);//always equals type
      domObj.setAttributeValues(context, attrMap);
    }

    boolean isRelationship = false;
    return PLMConnectorServices.getPLMID(context, sComposeePID, isRelationship);
  }
}