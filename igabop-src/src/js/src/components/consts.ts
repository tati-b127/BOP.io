/* eslint camelcase: "error" */
export const BOP_PATH = '/bop/api';

export const TYPE_KIT_FACTORY = 'Kit_Factory';
export const TYPE_KIT_SHOP_FLOOR = 'Kit_ShopFloor';
export const TYPE_KIT_WORK_CELL = 'Kit_WorkCell';
export const TYPE_KIT_MAIN_OP = 'Kit_MainOp';
export const TYPE_KIT_PRODUCT = 'Kit_Product';
export const TYPE_KIT_PART = 'Kit_Part';
export const TYPE_KIT_MFG_KIT = 'Kit_MfgBar';
export const TYPE_KIT_MFG_CONT = 'Kit_MfgContinuousPrvd';
export const TYPE_KIT_MFG_PRODUCED_PART = 'Kit_MfgProducedPart';
export const TYPE_KIT_MFG_ASSEMBLY = 'Kit_MfgAssembly';
export const TYPE_KIT_COOL_MIX = 'Kit_CoolingMixture';
export const TYPE_KIT_OEM_COMPONENT = 'Kit_MfgOEMComponent';
export const TYPE_KIT_MATERIAL_RAW = 'Kit_MfgRawMaterial';
export const TYPE_KIT_MATERIAL_PART = 'Kit_MfgKitPart';
export const TYPE_KIT_MATERIAL_STANDARD = 'Kit_MfgStandardComponent';
export const TYPE_VPM_REFERENCE = 'VPMReference';
export const TYPE_ERGO_HUMAN = 'ErgoHuman';
export const TYPE_INSTRUCTION_REFERENCE = 'DELWkiInstructionReference';
export const TYPE_HEADER_OPERATION_REFERENCE = 'DELLmiHeaderOperationReference';
export const TYPE_GENERAL_SYSTEM_REFERENCE = 'DELLmiGeneralSystemReference';
export const TYPE_KIT_FINAL_PRODUCT = 'Kit_FinalProduct';
export const TYPE_KIT_CONFIG_ITEM = 'Kit_ConfigItem';
export const TYPE_KIT_DRAWING = 'Kit_Drawing';
export const TYPE_KIT_MATERIAL = 'Kit_Material';
export const TYPE_KIT_CORE_MATERIAL = 'Kit_CoreMaterial';
export const TYPE_KIT_STANDARD_PRODUCT = 'Kit_StandardProduct';
export const TYPE_KIT_OEM_PRODUCT = 'Kit_OEMProduct';
export const TYPE_KIT_SET_PRODUCT = 'Kit_SetProduct';

export const ATTRIBUTE_TITLE = 'Title';
export const ATTRIBUTE_V_NAME = 'PLMEntity.V_Name'; // title
export const ATTRIBUTE_V_DESCRIPTION = 'PLMEntity.V_description'; // description
export const ATTRIBUTE_V_WIINSTRUCTION_TEXT = 'DELWkiInstructionReference.V_WIInstruction_Text';
export const ATTRIBUTE_KIT_PREP_TIME = 'Kit_MainOp.Kit_PrepTime';
export const ATTRIBUTE_KIT_TPCS = 'Kit_MainOp.Kit_Tpcs';
export const ATTRIBUTE_KIT_NUM_OPERATION = 'Kit_MainOp.Kit_NumOperation';
export const ATTRIBUTE_KIT_TRANSITION_NUMBER = 'Kit_Transition.Kit_TransitionNumber';
export const ATTRIBUTE_KIT_STOCK_LENGTH = 'Kit_MfgBar.Kit_StockLength';
export const ATTRIBUTE_KIT_MAX_GROUP_STOCK_LENGTH = 'Kit_MfgBar.Kit_MaxGroupStockLength';
export const ATTRIBUTE_KIT_MAX_GROUP_STOCK_LENGTH_CALC = 'Kit_MfgBar.Kit_MaxGroupStockLengthCalc';
export const ATTRIBUTE_KIT_PART_LENGTH = 'Kit_MfgBar.Kit_PartLength';
export const ATTRIBUTE_KIT_PART_WIDTH = 'Kit_MfgBar.Kit_PartWidth';
export const ATTRIBUTE_KIT_PART_MASS = 'Kit_MfgBar.Kit_PartMass';
export const ATTRIBUTE_KIT_ALLOWANCE = 'Kit_MfgBar.Kit_Allowance';
export const ATTRIBUTE_KIT_CUT_ALLOWANCE = 'Kit_MfgBar.Kit_CutAllowance';
export const ATTRIBUTE_KIT_INSTALL_ALLOWANCE = 'Kit_MfgBar.Kit_InstallAllowance';
export const ATTRIBUTE_KIT_STOCK_HIGHT_DIAMETER = 'Kit_MfgBar.Kit_StockHightDiameter';
export const ATTRIBUTE_KIT_STOCK_WIDTH = 'Kit_MfgBar.Kit_StockWidth';
export const ATTRIBUTE_KIT_STOCK_WEIGHT = 'Kit_MfgBar.Kit_StockWeight';
export const ATTRIBUTE_KIT_USAGE_COEFFIC = 'Kit_MfgBar.Kit_UsageCoeffic';
export const ATTRIBUTE_KIT_NUMBER_OF_PARTS = 'Kit_MfgBar.Kit_NumberOfParts';
export const ATTRIBUTE_KIT_NUMBER_OF_ADD_PARTS = 'Kit_MfgBar.Kit_NumberOfAddParts';
export const ATTRIBUTE_KIT_MAX_NUMBER_OF_PARTS = 'Kit_MfgBar.Kit_MaxNumberOfParts';
export const ATTRIBUTE_KIT_RAW_TYPE = 'Kit_MfgBar.Kit_RawType';
export const ATTRIBUTE_KIT_STOCK_TYPE = 'Kit_MfgBar.Kit_StockType';
export const ATTRIBUTE_KIT_MAT_CONSUMPTION_RATE = 'Kit_MfgBar.Kit_MatConsumptionRate';
export const ATTRIBUTE_KIT_STANDARD_UNIT = 'Kit_MfgBar.Kit_StandardUnit';
export const ATTRIBUTE_KIT_BAR_CODE = 'Kit_MfgBar.Kit_BarCode';
export const ATTRIBUTE_KIT_UNIT_VALUE = 'Kit_MfgBar.Kit_UnitValue';
export const ATTRIBUTE_KIT_CUT_OUT_COEFF = 'Kit_MfgBar.Kit_CutOutCoeff';
export const ATTRIBUTE_KIT_M_PART_NUMBER = 'Kit_MfgBar.Kit_M_PartNumber';
export const ATTRIBUTE_KIT_RAW_LENGTH = 'Kit_MfgRawMaterial.Kit_RawLength';
export const ATTRIBUTE_KIT_RAW_WIDTH = 'Kit_MfgRawMaterial.Kit_RawWidth';
export const ATTRIBUTE_KIT_LINEAR_MASS = 'Kit_WeightExt.Kit_LinearMass';
export const ATTRIBUTE_KIT_RAW_THICKNESS = 'Kit_MfgRawMaterial.Kit_RawThickness';
export const ATTRIBUTE_KIT_RAW_WEIGHT = 'Kit_MfgRawMaterial.Kit_RawWeight';
export const ATTRIBUTE_KIT_AREA_MASS = 'Kit_WeightExt.Kit_AreaMass';
export const ATTRIBUTE_KIT_RS_NAME = 'Kit_MatExt.Kit_RSName';
export const ATTRIBUTE_KIT_WEIGHT = 'Kit_MfgWeightExt.Kit_Weight';
export const ATTRIBUTE_V_WCG_MASS = 'WCGEquivalentComputedExt.V_WCG_Mass';
export const ATTRIBUTE_V_USAGE_CONT_COEFF = 'ProcessInstanceContinuous.V_UsageContCoeff';
export const ATTRIBUTE_V_CONT_QUANTITY_AREA = 'DELFmiContQuantity_Area.V_ContQuantity';
export const ATTRIBUTE_V_CONT_QUANTITY_LENGTH = 'DELFmiContQuantity_Length.V_ContQuantity';
export const ATTRIBUTE_V_CONT_QUANTITY_MASS = 'DELFmiContQuantity_Mass.V_ContQuantity';
export const ATTRIBUTE_V_CONT_QUANTITY_VOLUME = 'DELFmiContQuantity_Volume.V_ContQuantity';

export const DIM_METER = 'METER';
export const DIM_MM = 'MM';

export const DIM_KILOGRAM = 'KILOGRAM';
export const DIM_GRAM = 'GRAM';
export const DIM_MG = 'MG';

export const DIM_METER2 = 'METER2';
export const DIM_MM2 = 'MM2';

export const DIM_METER3 = 'METER3';
export const DIM_MM3 = 'MM3';
export const DIM_LITER = 'LITER';

export const selects = [
  'from.to.paths.path.element.type',
  'from.to',
  'from[Object Route].to.owner',
  'from[Object Route].to.attribute[Route Status]',
  'from[Object Route].to.attribute[Route Completion Action]',
  'from[Object Route].to.physicalid',
  `attribute[${ATTRIBUTE_TITLE}].value`,
  `attribute[${ATTRIBUTE_V_NAME}].value`,
  `attribute[${ATTRIBUTE_V_DESCRIPTION}].value`,
  `attribute[${ATTRIBUTE_V_WIINSTRUCTION_TEXT}].value`,
  `attribute[${ATTRIBUTE_KIT_PREP_TIME}].value`,
  `attribute[${ATTRIBUTE_KIT_TPCS}].value`,
  `attribute[${ATTRIBUTE_KIT_NUM_OPERATION}].value`,
  `attribute[${ATTRIBUTE_KIT_TRANSITION_NUMBER}].value`,
  'revision',
  'owner',
  'originated',
  'modified',
  'reservedby',
  'type.property[IPML.IconName].value'
].join(',');
/* eslint camelcase: "error" */
export const op_types = [TYPE_HEADER_OPERATION_REFERENCE, TYPE_KIT_MAIN_OP];

export const sys_types = [TYPE_GENERAL_SYSTEM_REFERENCE, TYPE_KIT_FACTORY, TYPE_KIT_SHOP_FLOOR, TYPE_KIT_WORK_CELL];

export const bop_types = op_types.concat(sys_types);

export const product_types = [TYPE_VPM_REFERENCE, TYPE_KIT_PRODUCT, TYPE_KIT_PART];

export const ebom_types = [
  TYPE_KIT_FINAL_PRODUCT,
  TYPE_KIT_CONFIG_ITEM,
  TYPE_KIT_PRODUCT,
  TYPE_KIT_PART,
  TYPE_KIT_DRAWING,
  TYPE_KIT_MATERIAL,
  TYPE_KIT_CORE_MATERIAL,
  TYPE_KIT_STANDARD_PRODUCT,
  TYPE_KIT_OEM_PRODUCT,
  TYPE_KIT_SET_PRODUCT
];

export const resource_types = [TYPE_VPM_REFERENCE, TYPE_ERGO_HUMAN];

export const instruction_types = [TYPE_INSTRUCTION_REFERENCE];

export const mbom_types = [
  'CreateAssembly',
  'DELFmiFunctionPPRCreateReference',
  'DELFmiFunctionPPRDiscreteReference',
  'DELFmiFunctionReference',
  TYPE_KIT_MFG_CONT,
  TYPE_KIT_MFG_ASSEMBLY,
  'Kit_MfgBar',
  'Kit_MfgConfigItem',
  'Kit_MfgContinuousMaterial',
  TYPE_KIT_MFG_CONT,
  'Kit_MfgFinalProduct',
  'Kit_MfgKit',
  TYPE_KIT_MATERIAL_PART,
  'Kit_MfgManufacturedPart',
  TYPE_KIT_OEM_COMPONENT,
  'Kit_MfgOEMKit',
  TYPE_KIT_MFG_PRODUCED_PART,
  TYPE_KIT_MATERIAL_RAW,
  TYPE_KIT_MATERIAL_STANDARD,
  'Kit_MfgTransform',
  'Provide',
  'Transform'
];

export const raw_material_types = [TYPE_KIT_MATERIAL_RAW];

export const oem_part_standard_material_types = [
  TYPE_KIT_OEM_COMPONENT,
  TYPE_KIT_MATERIAL_PART,
  TYPE_KIT_MATERIAL_STANDARD
];

export const material_types = [
  TYPE_KIT_OEM_COMPONENT,
  TYPE_KIT_MATERIAL_PART,
  TYPE_KIT_MATERIAL_RAW,
  TYPE_KIT_MATERIAL_STANDARD
];

export const material2_types = [TYPE_KIT_MFG_CONT, TYPE_KIT_MFG_CONT];

export const doc_types = ['Document', 'ARCHDocument'];

export const normalization_types = [TYPE_KIT_MFG_KIT, TYPE_KIT_MFG_CONT, TYPE_KIT_COOL_MIX];

export const normalization_continuous_cooling_types = [TYPE_KIT_MFG_CONT, TYPE_KIT_COOL_MIX];

export const specialCharacters = [
  '&#126;',
  '&#163;',
  '&#164;',
  '&#165;',
  '&#166;',
  '&#167;',
  '&#168;',
  '&#169;',
  '&#170;',
  '&#171;',
  '&#172;',
  '&#173;',
  '&#174;',
  '&#175;',
  '&#176;',
  '&#177;',
  '&#178;',
  '&#179;',
  '&#180;',
  '&#181;',
  '&#182;',
  '&#183;',
  '&#184;',
  '&#185;',
  '&#186;',
  '&#187;',
  '&#188;',
  '&#189;',
  '&#190;',
  '&#191;',
  '&#192;',
  '&#193;',
  '&#194;',
  '&#195;',
  '&#196;',
  '&#197;',
  '&#198;',
  '&#199;',
  '&#200;',
  '&#201;',
  '&#202;',
  '&#203;',
  '&#204;',
  '&#205;',
  '&#206;',
  '&#207;',
  '&#208;',
  '&#209;',
  '&#210;',
  '&#211;',
  '&#212;',
  '&#213;',
  '&#214;',
  '&#215;',
  '&#216;',
  '&#217;',
  '&#218;',
  '&#219;',
  '&#220;',
  '&#221;',
  '&#222;',
  '&#223;',
  '&#224;',
  '&#225;',
  '&#226;',
  '&#227;',
  '&#228;',
  '&#229;',
  '&#230;',
  '&#231;',
  '&#232;',
  '&#233;',
  '&#234;',
  '&#235;',
  '&#236;',
  '&#237;',
  '&#238;',
  '&#239;',
  '&#240;',
  '&#241;',
  '&#242;',
  '&#243;',
  '&#245;',
  '&#246;',
  '&#244;'
];

export const startOpts = [
  { value: 'immediately', label: 'Немедленно' },
  { value: 'manually', label: 'Вручную' }
];

export const ownershipOpts = [
  { value: 'no', label: 'Нет' },
  { value: 'yes', label: 'Да' }
];

export const calculationMethodsOpts = [
  { value: '1', label: '1 - По длине' },
  { value: '2', label: '2 - По площади' },
  { value: '3', label: '3 - По массе' }
];

export const unitValues = [
  { value: '1', label: 'м2' },
  { value: '2', label: 'кг' },
  { value: '3', label: 'м.п.' },
  { value: '4', label: 'шт.' }
];

export const mfgBarAttributes = [
  ATTRIBUTE_KIT_STOCK_LENGTH,
  ATTRIBUTE_KIT_MAX_GROUP_STOCK_LENGTH,
  ATTRIBUTE_KIT_MAX_GROUP_STOCK_LENGTH_CALC,
  ATTRIBUTE_KIT_PART_LENGTH,
  ATTRIBUTE_KIT_PART_WIDTH,
  ATTRIBUTE_KIT_PART_MASS,
  ATTRIBUTE_KIT_ALLOWANCE,
  ATTRIBUTE_KIT_CUT_ALLOWANCE,
  ATTRIBUTE_KIT_INSTALL_ALLOWANCE,
  ATTRIBUTE_KIT_STOCK_HIGHT_DIAMETER,
  ATTRIBUTE_KIT_STOCK_WIDTH,
  ATTRIBUTE_KIT_STOCK_WEIGHT,
  ATTRIBUTE_KIT_USAGE_COEFFIC,
  ATTRIBUTE_KIT_NUMBER_OF_PARTS,
  ATTRIBUTE_KIT_NUMBER_OF_ADD_PARTS,
  ATTRIBUTE_KIT_MAX_NUMBER_OF_PARTS,
  ATTRIBUTE_KIT_RAW_TYPE,
  ATTRIBUTE_KIT_STOCK_TYPE,
  ATTRIBUTE_KIT_MAT_CONSUMPTION_RATE,
  ATTRIBUTE_KIT_STANDARD_UNIT,
  ATTRIBUTE_KIT_BAR_CODE,
  ATTRIBUTE_KIT_UNIT_VALUE,
  ATTRIBUTE_KIT_CUT_OUT_COEFF,
  ATTRIBUTE_KIT_M_PART_NUMBER
];

export const LENGTH = '1';
export const AREA = '2';
export const MASS = '3';
export const VOLUME = '4';
