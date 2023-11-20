package com.igatec.bop.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.igatec.utilsspring.utils.common.SelectExpressionBuilder.builder;

public class Constants {
    //constants
    public static final String IOT = "иот";

    //Route
    public static final String PARAM_TASK = "task";
    public static final String PARAM_RT = "rt";
    public static final String PARAM_PERSON = "person";
    public static final String PARAM_START = "start";
    public static final String PARAM_OWNERSHIP = "ownership";
    public static final String PARAM_VALUE = "value";
    public static final String PARAM_ROUTE = "route";
    public static final String START_IMMEDIATELY = "immediately";
    public static final String START_MANUALLY = "manually";
    public static final String OWNERSHIP_YES = "yes";
    public static final String OWNERSHIP_NO = "no";

    //Roles
    public static final String ROLE_TIME_ANALYST = "IGABOPTimeAnalyst";

    //common
    public static final String QUERY_WILDCARD = "*";
    public static final String KIND_CONNECTION = "connection";
    public static final String KIND_BUSINESSOBJECT = "businessobject";

    //basic Selects
    public static final String SELECT_ID = "id";
    public static final String SELECT_PHYSICALID = "physicalid";
    public static final String SELECT_VERSIONID = "versionid";
    public static final String SELECT_TYPE = "type";
    public static final String SELECT_NAME = "name";
    public static final String SELECT_POLICY = "policy";
    public static final String SELECT_ATTRIBUTE_VALUE = "attribute.value";
    public static final String SELECT_INTERFACE = "interface";
    public static final String SELECT_RELATIONSHIP_ID = "id[connection]";
    public static final String SELECT_PATHS_SR_ID = "paths[SemanticRelation].path.id";
    public static final String SELECT_PATHS_SR_EL_PHYSICALID = "paths[SemanticRelation].path.element.physicalid";
    public static final String SELECT_PATHS_SR_EL_TYPE = "paths[SemanticRelation].path.element.type";
    public static final String SELECT_PATHS_SR_EL_KIND = "paths[SemanticRelation].path.element.kind";
    public static final String SELECT_PATHS_SR_EL_ORDER = "paths[SemanticRelation].path.element.order";
    public static final String SELECT_PATHS_SR_EL_0_PHYSICALID = "paths[SemanticRelation].path.element[0].physicalid";
    public static final String SELECT_PATHS_SR_EL_0_TYPE = "paths[SemanticRelation].path.element[0].type";
    public static final String SELECT_PATHS_SR_EL_1_PHYSICALID = "paths[SemanticRelation].path.element[1].physicalid";
    public static final String SELECT_ICON_NAME = "type.property[IPML.IconName].value";
    public static final String SELECT_ELEMENT_PHYSICALID = "element.physicalid";
    public static final String SELECT_ELEMENT_TYPE = "element.type";
    public static final String SELECT_ELEMENT_KIND = "element.kind";
    public static final String SELECT_ELEMENT_ORDER = "element.order";
    public static final String SELECT_OWNER_PHYSICALID = "owner.physicalid";
    public static final String SELECT_OWNER_NAME = "owner.name";
    public static final String SELECT_OWNER_TYPE = "owner.type";
    public static final String SELECT_TO_PHYSICALID = "to.physicalid";
    public static final String SELECT_FROM_PHYSICALID = "from.physicalid";
    public static final String SELECT_FORMAT_FILE = "format.file";
    public static final String SELECT_DOCS = "docs";
    public static final String SELECT_PATH = "path";
    public static final String SELECT_CURRENT = "current";

    //Vaults
    public static final String VAULT_VPLM = "vplm";
    public static final String VAULT_ESERVICE_PRODUCTION = "eService Production";

    //Relationships
    public static final String RELATIONSHIP_PLM_INSTANCE = "PLMInstance";
    public static final String RELATIONSHIP_VPM_INSTANCE = "VPMInstance";
    public static final String RELATIONSHIP_VPM_REP_INSTANCE = "VPMRepInstance";
    public static final String RELATIONSHIP_PLM_CONNECTION = "VPLMrel/PLMConnection/V_Owner";
    public static final String RELATIONSHIP_PLM_PORT = "VPLMrel/PLMPort/V_Owner";
    public static final String RELATIONSHIP_HEADER_OPERATION_INSTANCE = "DELLmiHeaderOperationInstance";
    public static final String RELATIONSHIP_GENERAL_SYSTEM_INSTANCE = "DELLmiGeneralSystemInstance";
    public static final String RELATIONSHIP_INSTRUCTION_INSTANCE = "DELWkiInstructionInstance";
    public static final String RELATIONSHIP_FUNCTION_INSTANCE = "DELFmiFunctionInstance";
    public static final String RELATIONSHIP_IDENTIFIED_FUNCTION_INSTANCE = "DELFmiFunctionIdentifiedInstance";
    public static final String RELATIONSHIP_OBJECT_ROUTE = "Object Route";
    public static final String RELATIONSHIP_PROJECT_TASK = "Project Task";
    public static final String RELATIONSHIP_PROCESS_INSTANCE_CONTINUOUS = "ProcessInstanceContinuous";

    //Pathtypes
    public static final String SEMANTIC_RELATION = "SemanticRelation";

    //Types
    public static final String TYPE_PLM_ENTITY = "PLMEntity";
    public static final String TYPE_PLM_PORT = "PLMPort";
    public static final String TYPE_PLM_REFERENCE = "PLMReference";
    public static final String TYPE_VPM_REFERENCE = "VPMReference";
    public static final String TYPE_FUNCTION_REFERENCE = "DELFmiFunctionReference";
    public static final String TYPE_PLM_CONNECTION = "PLMConnection";
    public static final String TYPE_PPR_CONTEXT = "PPRContext";
    public static final String TYPE_PPR_CONTEXT_SYSTEM = "PPRContextSystemCnxDisc";
    public static final String TYPE_PPR_CONTEXT_PROCESS = "PPRContextProcessCnxDisc";
    public static final String TYPE_GENERAL_SYSTEM = "DELLmiGeneralSystemReference";
    public static final String TYPE_PROD_SYSTEM_IO_PORT = "DELLmiProdSystemIOPort";
    public static final String TYPE_PROCESS_PREREQUISITE_PORT = "DELFmiProcessPrerequisitePort";
    public static final String TYPE_PROCESS_CAN_USE_CNX = "DELAsmProcessCanUseCnx";
    public static final String TYPE_TIME_CONSTRAINT_CNX = "DELLmiTimeConstraintCnx";
    public static final String TYPE_PLM_DOC_CONNECTION = "PLMDocConnection";
    public static final String TYPE_MFG_PRODUCTION_PLANNING = "MfgProductionPlanning";
    public static final String TYPE_HEADER_OPERATION_REFERENCE = "DELLmiHeaderOperationReference";
    public static final String TYPE_INSTRUCTION_REFERENCE = "DELWkiInstructionReference";
    public static final String TYPE_ERGO_HUMAN = "ErgoHuman";
    public static final String TYPE_DOCUMENT = "Document";
    public static final String TYPE_IMPLEMENT_LINKS_DISCIPLINE = "DELSysImplementLinksDiscipline";
    public static final String TYPE_ROUTE = "Route";
    public static final String TYPE_INBOX_TASK = "Inbox Task";
    public static final String TYPE_ROUTE_TEMPLATE = "Route Template";
    public static final String TYPE_TRANSFORM = "Transform";
    public static final String TYPE_PROVIDE = "Provide";
    public static final String TYPE_CREATE_ASSEMBLY = "CreateAssembly";
    public static final String TYPE_FUNCTION_PPR_DISCRETE_REFERENCE = "DELFmiFunctionPPRDiscreteReference";
    public static final String TYPE_FUNCTION_PPR_CREATE_REFERENCE = "DELFmiFunctionPPRCreateReference";
    public static final String TYPE_PROCESS_PREREQUISITE_CNX_CUST = "DELFmiProcessPrerequisiteCnxCust";
    public static final String TYPE_OPERATION_REFERENCE = "DELLmiOperationReference";
    public static final String TYPE_PROCESS_IMPLEMENT_CNX = "DELFmiProcessImplementCnx";

    //Custom types
    public static final String TYPE_KIT_ABS_DESIGN_PRODUCT = "Kit_AbsDesignProduct";
    public static final String TYPE_KIT_ABS_REFERENCE = "Kit_AbsReference";
    public static final String TYPE_KIT_CONFIG_ITEM = "Kit_ConfigItem";
    public static final String TYPE_KIT_COOLING_MIXTURE = "Kit_CoolingMixture";
    public static final String TYPE_KIT_CORE_MATERIAL = "Kit_CoreMaterial";
    public static final String TYPE_KIT_DRAWING = "Kit_Drawing";
    public static final String TYPE_KIT_FACTORY = "Kit_Factory";
    public static final String TYPE_KIT_FINAL_PRODUCT = "Kit_FinalProduct";
    public static final String TYPE_KIT_MAIN_OP = "Kit_MainOp";
    public static final String TYPE_KIT_MATERIAL = "Kit_Material";
    public static final String TYPE_KIT_MFG_ASSEMBLY = "Kit_MfgAssembly";
    public static final String TYPE_KIT_MFG_BAR = "Kit_MfgBar";
    public static final String TYPE_KIT_MFG_CONFIG_ITEM = "Kit_MfgConfigItem";
    public static final String TYPE_KIT_MFG_CONTINUOUS_PRVD = "Kit_MfgContinuousPrvd";
    public static final String TYPE_KIT_MFG_FINAL_PRODUCT = "Kit_MfgFinalProduct";
    public static final String TYPE_KIT_MFG_KIT = "Kit_MfgKit";
    public static final String TYPE_KIT_MFG_KIT_PART = "Kit_MfgKitPart";
    public static final String TYPE_KIT_MFG_MANUFACTURED_PART = "Kit_MfgManufacturedPart";
    public static final String TYPE_KIT_MFG_OEM_COMPONENT = "Kit_MfgOEMComponent";
    public static final String TYPE_KIT_MFG_PRODUCED_PART = "Kit_MfgProducedPart";
    public static final String TYPE_KIT_MFG_RAW_MATERIAL = "Kit_MfgRawMaterial";
    public static final String TYPE_KIT_MFG_STANDARD_COMPONENT = "Kit_MfgStandardComponent";
    public static final String TYPE_KIT_MFG_TRANSFORM = "Kit_MfgTransform";
    public static final String TYPE_KIT_OEM_PRODUCT = "Kit_OEMProduct";
    public static final String TYPE_KIT_PART = "Kit_Part";
    public static final String TYPE_KIT_PRODUCT = "Kit_Product";
    public static final String TYPE_KIT_SET_PRODUCT = "Kit_SetProduct";
    public static final String TYPE_KIT_SHOP_FLOOR = "Kit_ShopFloor";
    public static final String TYPE_KIT_STANDARD_PRODUCT = "Kit_StandardProduct";
    public static final String TYPE_KIT_WORK_CELL = "Kit_WorkCell";

    //
    public static final List<String> TYPES_MBOM = Arrays.asList(
            TYPE_KIT_MFG_PRODUCED_PART,
            TYPE_KIT_MFG_ASSEMBLY
    );
    public static final List<String> TYPES_EBOM = Arrays.asList(
            TYPE_KIT_PRODUCT,
            TYPE_KIT_PART,
            TYPE_VPM_REFERENCE
    );

    //Policies
    public static final String POLICY_VPLM_SMB_DEFINITION = "VPLM_SMB_Definition";
    public static final String POLICY_VPLM_SMB_EVALUATION = "VPLM_SMB_Evaluation";
    public static final String POLICY_VPLM_SMB_CX_DEFINITION = "VPLM_SMB_CX_Definition";
    public static final String POLICY_VPLM_SMB_PORT_DEFINITION = "VPLM_SMB_PORT_Definition";
    public static final String POLICY_DOCUMENT_RELEASE = "Document Release";
    public static final String POLICY_ROUTE = "Route";
    public static final String POLICY_INBOX_TASK = "Inbox Task";
    public static final String POLICY_ROUTE_TEMPLATE = "Route Template";

    //States
    public static final String STATE_PRIVATE = "PRIVATE";
    public static final String STATE_IN_WORK = "IN_WORK";
    public static final String STATE_FROZEN = "FROZEN";
    public static final String STATE_RELEASED = "RELEASED";
    public static final String STATE_OBSOLETE = "OBSOLETE";
    public static final String STATE_ACTIVE = "Active";

    public static final List<String> STATES_ALL = Arrays.asList(
            STATE_PRIVATE,
            STATE_IN_WORK,
            STATE_FROZEN,
            STATE_RELEASED
    );

    //Signatures
    public static final String SIGN_SHARE_WITHIN_PROJECT = "ShareWithinProject";
    public static final String SIGN_ISOLATE = "Isolate";
    public static final String SIGN_TO_FREEZE = "ToFreeze";
    public static final String SIGN_TO_RELEASE = "ToRelease";
    public static final String SIGN_UN_FREEZE = "UnFreeze";
    public static final String SIGN_OUT_DATED = "OutDated";
    //Messages
    public static final String MSG_OK = "Ok";
    //State signature mapping
    public static Map<String,String> PROMOTE_STATE_SIGN = new HashMap<>();
    public static Map<String,String> DEMOTE_STATE_SIGN = new HashMap<>();

    static {
        PROMOTE_STATE_SIGN.put(STATE_PRIVATE, SIGN_SHARE_WITHIN_PROJECT);
        PROMOTE_STATE_SIGN.put(STATE_IN_WORK, SIGN_TO_FREEZE);
        //PROMOTE_STATE_SIGN.put(STATE_FROZEN, SIGN_TO_RELEASE);
        DEMOTE_STATE_SIGN.put(STATE_IN_WORK, SIGN_ISOLATE);
        DEMOTE_STATE_SIGN.put(STATE_FROZEN, SIGN_UN_FREEZE);
    }

    //Interfaces
    public static final String INTERFACE_PLM_CORE_STREAM_STORAGE = "PLMCoreStreamStorage";
    public static final String INTERFACE_INDUSTRIAL_MACHINE = "IndustrialMachine";
    public static final String INTERFACE_KIT_AUXIL_TOOL = "Kit_AuxilTool";
    public static final String INTERFACE_KIT_BENCH_TOOL = "Kit_BenchTool";
    public static final String INTERFACE_KIT_CUTTING_TOOL = "Kit_CuttingTool";
    public static final String INTERFACE_KIT_FIXTURE = "Kit_Fixture";
    public static final String INTERFACE_KIT_INDUSTRIAL_MACHINE = "Kit_IndustrialMachine";
    public static final String INTERFACE_KIT_MEASURE_TOOL = "Kit_MeasureTool";
    public static final String INTERFACE_KIT_PROTECTOR = "Kit_Protector";
    public static final String INTERFACE_KIT_RESOURCES = "Kit_Resources";
    public static final String INTERFACE_KIT_TECHNOLOGICAL_TOOLING = "Kit_TechnologicalTooling";
    public static final String INTERFACE_KIT_WORKER = "Kit_Worker";
    public static final String INTERFACE_KIT_TRANSITION = "Kit_Transition";
    public static final String INTERFACE_KIT_PN_EXT = "Kit_PNExt";
    public static final String INTERFACE_TOOL_DEVICE = "ToolDevice";
    public static final String INTERFACE_WORKER = "Worker";
    public static final String INTERFACE_DATA_REQUIREMENT_EXT = "DELAsmDataRequirementExt";
    public static final String INTERFACE_IGABOP_ROUTE = "IGABOPRoute";

    //Attributes
    public static final String ATTRIBUTE_C_UPDATESTAMP = String.format("%s.%s", TYPE_PLM_ENTITY, "C_updatestamp");
    public static final String ATTRIBUTE_REL_C_UPDATESTAMP = String.format("%s.%s", RELATIONSHIP_PLM_INSTANCE, "C_updatestamp");
    public static final String ATTRIBUTE_PLM_EXTERNAL_ID = String.format("%s.%s", TYPE_PLM_ENTITY, "PLM_ExternalID");
    public static final String ATTRIBUTE_REPO_PRIVILEGE = String.format("%s.%s", TYPE_PLM_ENTITY, "RepoPrivilege");
    public static final String ATTRIBUTE_STREAM_DESCRIPTORS = String.format("%s.%s", TYPE_PLM_ENTITY, "StreamDescriptors");
    public static final String ATTRIBUTE_V_CUSTO_DISCIPLINE = String.format("%s.%s", TYPE_PLM_ENTITY, "V_CustoDiscipline");
    public static final String ATTRIBUTE_V_NAME = String.format("%s.%s", TYPE_PLM_ENTITY, "V_Name");
    public static final String ATTRIBUTE_V_DESCRIPTION = String.format("%s.%s", TYPE_PLM_ENTITY, "V_description");
    public static final String ATTRIBUTE_V_DISCIPLINE = String.format("%s.%s", TYPE_PLM_ENTITY, "V_discipline");
    public static final String ATTRIBUTE_REL_V_DISCIPLINE = String.format("%s.%s", RELATIONSHIP_PLM_INSTANCE, "V_discipline");
    public static final String ATTRIBUTE_V_IS_UPTODATE = String.format("%s.%s", TYPE_PLM_ENTITY, "V_isUptodate");
    public static final String ATTRIBUTE_V_NATURE = String.format("%s.%s", TYPE_PLM_ENTITY, "V_nature");
    public static final String ATTRIBUTE_V_SEC_LEVEL = String.format("%s.%s", TYPE_PLM_ENTITY, "V_sec_level");
    public static final String ATTRIBUTE_REL_V_SEC_LEVEL = String.format("%s.%s", RELATIONSHIP_PLM_INSTANCE, "V_sec_level");
    public static final String ATTRIBUTE_V_USAGE = String.format("%s.%s", TYPE_PLM_ENTITY, "V_usage");
    public static final String ATTRIBUTE_WORKSPACE_ID = String.format("%s.%s", TYPE_PLM_ENTITY, "workspaceid");
    public static final String ATTRIBUTE_V_APPLICABILITY_DATE = String.format("%s.%s", TYPE_PLM_REFERENCE, "V_ApplicabilityDate");
    public static final String ATTRIBUTE_V_AUTHORITY_CONTROL = String.format("%s.%s", TYPE_PLM_REFERENCE, "V_AuthorityControl");
    public static final String ATTRIBUTE_V_DERIVED_FROM = String.format("%s.%s", TYPE_PLM_REFERENCE, "V_DerivedFrom");
    public static final String ATTRIBUTE_V_EFFECTIVITY_KIND = String.format("%s.%s", TYPE_PLM_REFERENCE, "V_EffectivityKind");
    public static final String ATTRIBUTE_V_PROJECT_APPLICABILITY = String.format("%s.%s", TYPE_PLM_REFERENCE, "V_ProjectApplicability");
    public static final String ATTRIBUTE_V_VERSION_ID = String.format("%s.%s", TYPE_PLM_REFERENCE, "V_VersionID");
    public static final String ATTRIBUTE_V_FROM_EXTERNAL_ID = String.format("%s.%s", TYPE_PLM_REFERENCE, "V_fromExternalID");
    public static final String ATTRIBUTE_V_HAS_CONFIG_CONTEXT = String.format("%s.%s", TYPE_PLM_REFERENCE, "V_hasConfigContext");
    public static final String ATTRIBUTE_V_IS_LAST_MINOR_VERSION = String.format("%s.%s", TYPE_PLM_REFERENCE, "V_isLastMinorVersion");
    public static final String ATTRIBUTE_V_IS_LAST_VERSION = String.format("%s.%s", TYPE_PLM_REFERENCE, "V_isLastVersion");
    public static final String ATTRIBUTE_V_IS_VPLM_CONTROLLED = String.format("%s.%s", TYPE_PLM_REFERENCE, "V_isVPLMControlled");
    public static final String ATTRIBUTE_V_ORDER = String.format("%s.%s", TYPE_PLM_REFERENCE, "V_order");
    public static final String ATTRIBUTE_V_VERSION_COMMENT = String.format("%s.%s", TYPE_PLM_REFERENCE, "V_versionComment");
    public static final String ATTRIBUTE_STREAM = String.format("%s.%s", INTERFACE_PLM_CORE_STREAM_STORAGE, "Stream");
    public static final String ATTRIBUTE_V_IO_TYPE = String.format("%s.%s", TYPE_PROCESS_PREREQUISITE_PORT, "V_IOType");
    public static final String ATTRIBUTE_LAST_PID_AND_ROLE = "LastPIDAndRole";
    public static final String ATTRIBUTE_ID_REL = "IDRel";
    public static final String ATTRIBUTE_ROLE = "Role";
    public static final String ATTRIBUTE_SEMANTICS = "Semantics";
    public static final String ATTRIBUTE_ROLE_SEMANTICS = "RoleSemantics";
    public static final String ATTRIBUTE_APP_INDEX = "AppIndex";
    public static final String ATTRIBUTE_PRIVATE_DATA = "PrivateData";
    public static final String ATTRIBUTE_OUT_OF_SCOPES = "OutOfScopes";
    public static final String ATTRIBUTE_ROUTE_STATUS = "Route Status";
    public static final String ATTRIBUTE_V_WIINSTRUCTION_TEXT = String.format("%s.%s", TYPE_INSTRUCTION_REFERENCE, "V_WIInstruction_Text");
    public static final String ATTRIBUTE_V_RESOURCES_QUANTITY = String.format("%s.%s", TYPE_PROCESS_CAN_USE_CNX, "V_ResourcesQuantity");
    public static final String ATTRIBUTE_V_TREE_ORDER = String.format("%s.%s", RELATIONSHIP_PLM_INSTANCE, "V_TreeOrder");
    public static final String ATTRIBUTE_V_CNX_TREE_ORDER = String.format("%s.%s", TYPE_PLM_CONNECTION, "V_CnxTreeOrder");
    public static final String ATTRIBUTE_V_PORT_TREE_ORDER = String.format("%s.%s", TYPE_PLM_PORT, "V_PortTreeOrder");
    public static final String ATTRIBUTE_V_DELAY_MODE = String.format("%s.%s", TYPE_TIME_CONSTRAINT_CNX, "V_DelayMode");
    public static final String ATTRIBUTE_V_IS_ACTIVE = String.format("%s.%s", TYPE_TIME_CONSTRAINT_CNX, "V_IsActive");
    public static final String ATTRIBUTE_V_RESOURCE_CONSTRAINT = String.format("%s.%s", TYPE_TIME_CONSTRAINT_CNX, "V_ResourceConstraint");
    public static final String ATTRIBUTE_V_CONTIGUITY = String.format("%s.%s", TYPE_TIME_CONSTRAINT_CNX, "V_Contiguity");
    public static final String ATTRIBUTE_V_MATERIAL_NEED = String.format("%s.%s", TYPE_TIME_CONSTRAINT_CNX, "V_MaterialNeed");
    public static final String ATTRIBUTE_V_DELAY = String.format("%s.%s", TYPE_TIME_CONSTRAINT_CNX, "V_Delay");
    public static final String ATTRIBUTE_V_DEPENDENCY_TYPE = String.format("%s.%s", TYPE_TIME_CONSTRAINT_CNX, "V_DependencyType");
    public static final String ATTRIBUTE_V_OPTIONAL_TIME_CONSTRAINT = String.format("%s.%s", TYPE_TIME_CONSTRAINT_CNX, "V_OptionalTimeConstraint");
    public static final String ATTRIBUTE_V_IS_TIME_PROPORTIONAL_TO_QTY = String.format("%s.%s", TYPE_OPERATION_REFERENCE, "V_IsTimeProportionalToQty");
    public static final String ATTRIBUTE_V_TRACK_TIME = String.format("%s.%s", TYPE_OPERATION_REFERENCE, "V_TrackTime");
    public static final String ATTRIBUTE_V_ESTIMATED_TIME = String.format("%s.%s", TYPE_OPERATION_REFERENCE, "V_EstimatedTime");
    public static final String ATTRIBUTE_V_MEASURED_TIME = String.format("%s.%s", TYPE_OPERATION_REFERENCE, "V_MeasuredTime");
    public static final String ATTRIBUTE_V_TIME_MODE = String.format("%s.%s", TYPE_OPERATION_REFERENCE, "V_TimeMode");
    public static final String ATTRIBUTE_V_ESTIMATED_TIME_ADDED_VALUE_RATIO = String.format("%s.%s", TYPE_OPERATION_REFERENCE, "V_EstimatedTime_AddedValueRatio");
    public static final String ATTRIBUTE_SCHEDULED_COMPLETION_DATE = "Scheduled Completion Date";
    public static final String ATTRIBUTE_ROUTE_SEQUENCE = "Route Sequence";
    public static final String ATTRIBUTE_ROUTE_COMPLETION_ACTION = "Route Completion Action";
    public static final String RANGE_PROMOTE_CONNECTED_OBJECT = "Promote Connected Object";
    //custom attrs
    public static final String ATTRIBUTE_KIT_M_PART_NUMBER = String.format("%s.%s", INTERFACE_KIT_PN_EXT, "Kit_M_PartNumber");
    public static final String ATTRIBUTE_KIT_NUM_OPERATION = String.format("%s.%s", TYPE_KIT_MAIN_OP, "Kit_NumOperation");
    public static final String ATTRIBUTE_KIT_PREP_TIME = String.format("%s.%s", TYPE_KIT_MAIN_OP, "Kit_PrepTime");
    public static final String ATTRIBUTE_KIT_TPCS = String.format("%s.%s", TYPE_KIT_MAIN_OP, "Kit_Tpcs");
    public static final String ATTRIBUTE_KIT_TRANSITION_NUMBER = String.format("%s.%s", INTERFACE_KIT_TRANSITION, "Kit_TransitionNumber");
    public static final String ATTRIBUTE_KIT_ALLOWANCE = String.format("%s.%s", TYPE_KIT_MFG_BAR, "Kit_Allowance");
    public static final String ATTRIBUTE_KIT_BARCODE = String.format("%s.%s", TYPE_KIT_MFG_BAR, "Kit_BarCode");
    public static final String ATTRIBUTE_KIT_CUT_ALLOWANCE = String.format("%s.%s", TYPE_KIT_MFG_BAR, "Kit_CutAllowance");
    public static final String ATTRIBUTE_KIT_CUT_OUT_COEFF = String.format("%s.%s", TYPE_KIT_MFG_BAR, "Kit_CutOutCoeff");
    public static final String ATTRIBUTE_KIT_INSTALL_ALLOWANCE = String.format("%s.%s", TYPE_KIT_MFG_BAR, "Kit_InstallAllowance");
    public static final String ATTRIBUTE_KIT_MAT_CONSUMPTION_RATE = String.format("%s.%s", TYPE_KIT_MFG_BAR, "Kit_MatConsumptionRate");
    public static final String ATTRIBUTE_KIT_MAX_GROUP_STOCK_LENGTH = String.format("%s.%s", TYPE_KIT_MFG_BAR, "Kit_MaxGroupStockLength");
    public static final String ATTRIBUTE_KIT_MAX_GROUP_STOCK_LENGTH_CALC = String.format("%s.%s", TYPE_KIT_MFG_BAR, "Kit_MaxGroupStockLengthCalc");
    public static final String ATTRIBUTE_KIT_MAX_NUMBER_OF_PARTS = String.format("%s.%s", TYPE_KIT_MFG_BAR, "Kit_MaxNumberOfParts");
    public static final String ATTRIBUTE_KIT_NUMBER_OF_ADD_PARTS = String.format("%s.%s", TYPE_KIT_MFG_BAR, "Kit_NumberOfAddParts");
    public static final String ATTRIBUTE_KIT_NUMBER_OF_PARTS = String.format("%s.%s", TYPE_KIT_MFG_BAR, "Kit_NumberOfParts");
    public static final String ATTRIBUTE_KIT_PART_LENGTH = String.format("%s.%s", TYPE_KIT_MFG_BAR, "Kit_PartLength");
    public static final String ATTRIBUTE_KIT_PART_MASS = String.format("%s.%s", TYPE_KIT_MFG_BAR, "Kit_PartMass");
    public static final String ATTRIBUTE_KIT_PART_WIDTH = String.format("%s.%s", TYPE_KIT_MFG_BAR, "Kit_PartWidth");
    public static final String ATTRIBUTE_KIT_RAW_TYPE = String.format("%s.%s", TYPE_KIT_MFG_BAR, "Kit_RawType");
    public static final String ATTRIBUTE_KIT_STANDARD_UNIT = String.format("%s.%s", TYPE_KIT_MFG_BAR, "Kit_StandardUnit");
    public static final String ATTRIBUTE_KIT_STOCK_HIGHT_DIAMETER = String.format("%s.%s", TYPE_KIT_MFG_BAR, "Kit_StockHightDiameter");
    public static final String ATTRIBUTE_KIT_STOCK_LENGTH = String.format("%s.%s", TYPE_KIT_MFG_BAR, "Kit_StockLength");
    public static final String ATTRIBUTE_KIT_STOCK_TYPE = String.format("%s.%s", TYPE_KIT_MFG_BAR, "Kit_StockType");
    public static final String ATTRIBUTE_KIT_STOCK_WEIGHT = String.format("%s.%s", TYPE_KIT_MFG_BAR, "Kit_StockWeight");
    public static final String ATTRIBUTE_KIT_STOCK_WIDTH = String.format("%s.%s", TYPE_KIT_MFG_BAR, "Kit_StockWidth");
    public static final String ATTRIBUTE_KIT_UNIT_VALUE = String.format("%s.%s", TYPE_KIT_MFG_BAR, "Kit_UnitValue");
    public static final String ATTRIBUTE_KIT_USAGE_COEFFIC = String.format("%s.%s", TYPE_KIT_MFG_BAR, "Kit_UsageCoeffic");
    public static final String ATTRIBUTE_KIT_RS_NAME = "Kit_MatExt.Kit_RSName";

    //hideFieldRule

    public static final List<String> RAW_MATERIAL_TYPES_LST = Arrays.asList(TYPE_KIT_MFG_RAW_MATERIAL);
    public static final List<String> OEM_MATERIAL_TYPES_LST = Arrays.asList(TYPE_KIT_MFG_OEM_COMPONENT, TYPE_KIT_MFG_KIT_PART, TYPE_KIT_MFG_STANDARD_COMPONENT);
    public static enum CALCULATE_METHOD {LENGTH, AREA, MASS};
    public static final Map<String, CALCULATE_METHOD> CALCULATION_METHOD_BY_TYPE = new HashMap<>();
    static {
        CALCULATION_METHOD_BY_TYPE.put("Круг",CALCULATE_METHOD.LENGTH);
        CALCULATION_METHOD_BY_TYPE.put("Шток", CALCULATE_METHOD.LENGTH);
        CALCULATION_METHOD_BY_TYPE.put("Шпилька", CALCULATE_METHOD.LENGTH);
        CALCULATION_METHOD_BY_TYPE.put("Труба", CALCULATE_METHOD.LENGTH);
        CALCULATION_METHOD_BY_TYPE.put("Квадрат", CALCULATE_METHOD.LENGTH);
        CALCULATION_METHOD_BY_TYPE.put("Шестигранник", CALCULATE_METHOD.LENGTH);
        CALCULATION_METHOD_BY_TYPE.put("Швеллер", CALCULATE_METHOD.LENGTH);
        CALCULATION_METHOD_BY_TYPE.put("Двутавр", CALCULATE_METHOD.LENGTH);
        CALCULATION_METHOD_BY_TYPE.put("Профиль", CALCULATE_METHOD.LENGTH);
        CALCULATION_METHOD_BY_TYPE.put("Уголок", CALCULATE_METHOD.LENGTH);
        CALCULATION_METHOD_BY_TYPE.put("Плита", CALCULATE_METHOD.AREA);
        CALCULATION_METHOD_BY_TYPE.put("Лист", CALCULATE_METHOD.AREA);
        CALCULATION_METHOD_BY_TYPE.put("Пластина", CALCULATE_METHOD.AREA);
        CALCULATION_METHOD_BY_TYPE.put("Стекло", CALCULATE_METHOD.AREA);
        CALCULATION_METHOD_BY_TYPE.put("Поролон", CALCULATE_METHOD.AREA);
        CALCULATION_METHOD_BY_TYPE.put("Отливка",CALCULATE_METHOD.MASS);
        CALCULATION_METHOD_BY_TYPE.put("Поковка",CALCULATE_METHOD.MASS);
        CALCULATION_METHOD_BY_TYPE.put("Пруток",CALCULATE_METHOD.LENGTH);
        CALCULATION_METHOD_BY_TYPE.put("ПКИ", CALCULATE_METHOD.MASS);
        CALCULATION_METHOD_BY_TYPE.put("ОЦ", CALCULATE_METHOD.AREA);
        CALCULATION_METHOD_BY_TYPE.put("Сэндвич-панель", CALCULATE_METHOD.AREA);
    }

    public static Map<CALCULATE_METHOD, List<String>> HIDE_RULES_MAP = new HashMap<>();
    static {
        HIDE_RULES_MAP.put(CALCULATE_METHOD.LENGTH, Arrays.asList(ATTRIBUTE_KIT_STOCK_WIDTH, ATTRIBUTE_KIT_PART_WIDTH));
        HIDE_RULES_MAP.put(CALCULATE_METHOD.AREA, Arrays.asList(ATTRIBUTE_KIT_MAX_GROUP_STOCK_LENGTH,
                ATTRIBUTE_KIT_CUT_ALLOWANCE, ATTRIBUTE_KIT_INSTALL_ALLOWANCE,
                ATTRIBUTE_KIT_NUMBER_OF_PARTS, ATTRIBUTE_KIT_MAX_NUMBER_OF_PARTS,
                ATTRIBUTE_KIT_MAX_GROUP_STOCK_LENGTH_CALC));
        HIDE_RULES_MAP.put(CALCULATE_METHOD.MASS, Arrays.asList(ATTRIBUTE_KIT_MAX_GROUP_STOCK_LENGTH,
                ATTRIBUTE_KIT_CUT_ALLOWANCE, ATTRIBUTE_KIT_ALLOWANCE, ATTRIBUTE_KIT_INSTALL_ALLOWANCE,
                ATTRIBUTE_KIT_MAX_NUMBER_OF_PARTS,
                ATTRIBUTE_KIT_MAX_GROUP_STOCK_LENGTH_CALC,ATTRIBUTE_KIT_STOCK_WIDTH, ATTRIBUTE_KIT_PART_WIDTH,
                ATTRIBUTE_KIT_PART_LENGTH, ATTRIBUTE_KIT_STOCK_LENGTH, ATTRIBUTE_KIT_STOCK_HIGHT_DIAMETER));
    }

    public static Map<String,String> MFG_BAR_ATTRIBUTES = new HashMap<>();
    static {
        MFG_BAR_ATTRIBUTES.put(ATTRIBUTE_KIT_ALLOWANCE, "0");
        MFG_BAR_ATTRIBUTES.put(ATTRIBUTE_KIT_CUT_ALLOWANCE, "0");
        MFG_BAR_ATTRIBUTES.put(ATTRIBUTE_KIT_INSTALL_ALLOWANCE, "0");
        MFG_BAR_ATTRIBUTES.put(ATTRIBUTE_KIT_MAX_GROUP_STOCK_LENGTH, "0");
        MFG_BAR_ATTRIBUTES.put(ATTRIBUTE_KIT_MAX_GROUP_STOCK_LENGTH_CALC, "0");
        MFG_BAR_ATTRIBUTES.put(ATTRIBUTE_KIT_MAX_NUMBER_OF_PARTS, "1");
        MFG_BAR_ATTRIBUTES.put(ATTRIBUTE_KIT_NUMBER_OF_PARTS, "1");
        MFG_BAR_ATTRIBUTES.put(ATTRIBUTE_KIT_PART_LENGTH, "0");
        MFG_BAR_ATTRIBUTES.put(ATTRIBUTE_KIT_PART_WIDTH, "0");
        MFG_BAR_ATTRIBUTES.put(ATTRIBUTE_KIT_STOCK_HIGHT_DIAMETER, "0");
        MFG_BAR_ATTRIBUTES.put(ATTRIBUTE_KIT_STOCK_LENGTH, "0");
        MFG_BAR_ATTRIBUTES.put(ATTRIBUTE_KIT_STOCK_WIDTH, "0");
    }

    //attribute types mapping
    public static Map<Short,String> ATTRIBUTE_TYPES = new HashMap<>();

    static {
        ATTRIBUTE_TYPES.put((short)1, "string");
        ATTRIBUTE_TYPES.put((short)2, "integer");
        ATTRIBUTE_TYPES.put((short)3, "real");
        ATTRIBUTE_TYPES.put((short)5, "boolean");
    }

    //attribute selects
    public static final String SELECT_ATTRIBUTE_V_TREE_ORDER = String.format("attribute[%s]", ATTRIBUTE_V_TREE_ORDER);
    public static final String SELECT_ATTRIBUTE_V_CNX_TREE_ORDER = String.format("attribute[%s]", ATTRIBUTE_V_CNX_TREE_ORDER);
    public static final String SELECT_ATTRIBUTE_KIT_NUM_OPERATION = String.format("attribute[%s]", ATTRIBUTE_KIT_NUM_OPERATION);
    public static final String SELECT_ATTRIBUTE_V_NAME = String.format("attribute[%s]", ATTRIBUTE_V_NAME);
    public static final String SELECT_ATTRIBUTE_V_DERIVED_FROM = String.format("attribute[%s]", ATTRIBUTE_V_DERIVED_FROM);
    public static final String SELECT_ATTRIBUTE_V_VERSION_COMMENT = String.format("attribute[%s]", ATTRIBUTE_V_VERSION_COMMENT);
    public static final String SELECT_ATTRIBUTE_V_RESOURCES_QUANTITY = String.format("attribute[%s]", ATTRIBUTE_V_RESOURCES_QUANTITY);
    public static final String SELECT_ATTRIBUTE_SCHEDULED_COMPLETION_DATE = String.format("attribute[%s]", ATTRIBUTE_SCHEDULED_COMPLETION_DATE);
    public static final String SELECT_ATTRIBUTE_ROUTE_SEQUENCE = String.format("attribute[%s]", ATTRIBUTE_ROUTE_SEQUENCE);

    //Default attribute values
    public static final String DEFAULT_STREAM = "IccAAAB4XvP3dQoONtRlYw4uSE0uVvUP9m1tavot7uLq45OZl+1WlJ8bEBAUkh9cWVySmvun8TlD099/2pwguZDEpJxUXVHmAB9f5/zcgpzU3NS8ksSiSpfEkkRuRmUtZxdGBhDQ5WEGKfdIzEvJSS0OAcr4eLCDZSDyEABiI/NBYs5MTEzsl0NTTzIwGHPELTygzcDEXINQzsAC06yoCACyuiya";//todo
    public static final String DEFAULT_STREAM_DOC = "Ib4AAAB4XvP3dQoONtRlYw4uSE0ulvcP9m1tavrN75KfXJqbmlcSlFoQlJr2p/G5QlPT38Z/2pw+mXnZIYlJOam6oswBPr7O+bkFOakglYlFlS6JJYncjMpazi6MDCCgy8MMUu6RmJeSk1ocCJTx8WAHy0DkIQDERuaDxBwhyjAAWB0LTFhREQBZDCX6";//todo
    public static final String DEFAULT_PRIVATE_DATA = "@IypQRAAGAAAAAAAAADr/ei8AAAB4AWNkYmQLi/dLzE0Vc0/NSy1KzFEIriwuSc01AAFDYxMmjqLUsszizPw8Zkc9QwA3kg1eAAAAAQAAAQ==";//todo see MFN API to serach how to generate this value
    public static final String DEFAULT_APP_INDEX = "0";
    public static final String DEFAULT_V_SEC_LEVEL = "0";
    public static final String DEFAULT_C_UPDATESTAMP = "-1";
    public static final String DEFAULT_V_ORDER = "1";
    public static final String DEFAULT_ID_REL = "1";

    //Attribute selects
    public static final String SELECT_ATTRIBUTE_STREAM = builder().attr(ATTRIBUTE_STREAM).build();

    //for Semantics and Role attributes of SR
    public static final String SEMANTICS_REFERENCE = "Reference";
    public static final String SEMANTICS_REFERENCE_3 = "Reference3";
    public static final String SEMANTICS_REFERENCE_4 = "Reference4";
    public static final String SEMANTICS_REFERENCE_5 = "Reference5";
    public static final String ROLE_DMT_DOCUMENT = "DmtDocument";
    public static final String ROLE_PLM_PPRCONTEXTLINK_SYSTEM = "PLM_PPRContextLink_System";
    public static final String ROLE_DEL_PCU_OWNER = "DEL_PCU_Owner";
    public static final String ROLE_DEL_PCU_RESULT = "DEL_PCU_Result";
    public static final String ROLE_PLM_IMPLEMENTLINK_SOURCE = "PLM_ImplementLink_Source";
    public static final String ROLE_PLM_IMPLEMENTLINK_TARGET = "PLM_ImplementLink_Target";
    public static final String ROLE_DEL_LINK_TO_CONSTRAINING_OBJECT = "DEL_LinkToConstrainingObject";
    public static final String ROLE_DEL_LINK_TO_CONSTRAINED_OBJECT = "DEL_LinkToConstrainedObject";
    public static final String ROLE_DELFMI_PREREQUISITECST_SOURCE = "DELFmi_PrerequisiteCst_Source";
    public static final String ROLE_DELFMI_PREREQUISITECST_TARGET = "DELFmi_PrerequisiteCst_Target";

    //Default revision
    public static final String DEFAULT_REVISION = "A.1";

    //request params
    public static final String PARAM_ID = "id";
    public static final String PARAM_TYPE = "type";
    public static final String PARAM_DIRECTION = "direction";
    public static final String PARAM_SELECTS = "selects";
    public static final String PARAM_IDS = "ids";
    public static final String PARAM_STATES = "states";
    public static final String PARAM_RECURSE_TO_LEVEL = "recurseToLevel";
    public static final String PARAM_GSYS = "gsys";
    public static final String PARAM_DUPLICATE = "duplicate";
    public static final String PARAM_OPERATION = "operation";
    public static final String PARAM_REL_ID = "relId";
    public static final String PARAM_MBOM = "mbom";
    public static final String PARAM_PATH = "path";
    public static final String PARAM_BOP_PATH = "bopPath";
    public static final String PARAM_MBOM_PATH = "mbomPath";
    public static final String PARAM_BOP_ROOT_PID = "bopRootPID";
    public static final String PARAM_PREDECESSOR = "predecessor";
    public static final String PARAM_RESOURCE = "resource";
    public static final String PARAM_WI = "wi";
    public static final String PARAM_COMPOSEE = "composee";
    public static final String PARAM_PARENT_ID = "parentId";
    public static final String PARAM_PARENT_PID = "parentPID";
    public static final String PARAM_INSTANCE_ID = "instanceId";
    public static final String PARAM_PREDECESSORS = "predecessors";
    public static final String PARAM_SCOPED = "scoped";
    public static final String PARAM_WHERE = "where";
    public static final String PARAM_ROUTES = "routes";
    public static final String PARAM_SELECTED = "selected";
    public static final String PARAM_CURRENT = "current";
    public static final String PARAM_NAME = "name";
    public static final String PARAM_PHYSICALID = "physicalid";
    public static final String PARAM_RESERVED_BY = "reservedby";
    public static final String PARAM_TYPE_ID = "typeId";
    public static final String PARAM_TITLE = "title";
    public static final String PARAM_CHILDREN = "children";
    public static final String PARAM_CONNECTION = "connection";
    public static final String PARAM_ATTRIBUTES = "attributes";
    public static final String PARAM_COPY_PID = "copyPID";
    public static final String DEFAULT_VAL = "DEFAULT";

    //delimiters
    public static final String DELIMITER_BELL = "\u0007";
    public static final String DELIMITER_COMMA = ",";
}
