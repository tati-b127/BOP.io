import {
    ATTRIBUTE_KIT_ALLOWANCE,
    ATTRIBUTE_KIT_CUT_ALLOWANCE,
    ATTRIBUTE_KIT_INSTALL_ALLOWANCE,
    ATTRIBUTE_KIT_M_PART_NUMBER,
    ATTRIBUTE_KIT_MAT_CONSUMPTION_RATE,
    ATTRIBUTE_KIT_MAX_GROUP_STOCK_LENGTH,
    ATTRIBUTE_KIT_MAX_GROUP_STOCK_LENGTH_CALC,
    ATTRIBUTE_KIT_MAX_NUMBER_OF_PARTS,
    ATTRIBUTE_KIT_NUMBER_OF_ADD_PARTS,
    ATTRIBUTE_KIT_NUMBER_OF_PARTS,
    ATTRIBUTE_KIT_PART_LENGTH,
    ATTRIBUTE_KIT_PART_MASS,
    ATTRIBUTE_KIT_PART_WIDTH,
    ATTRIBUTE_KIT_RAW_TYPE,
    ATTRIBUTE_KIT_STOCK_HIGHT_DIAMETER,
    ATTRIBUTE_KIT_STOCK_LENGTH,
    ATTRIBUTE_KIT_STOCK_WEIGHT,
    ATTRIBUTE_KIT_STOCK_WIDTH,
    ATTRIBUTE_KIT_UNIT_VALUE,
    ATTRIBUTE_KIT_USAGE_COEFFIC,
    DIM_GRAM,
    DIM_KILOGRAM,
    DIM_METER,
    DIM_MG,
    DIM_MM,
} from "../components/consts";

export type Option = {
  label: string;
  value: string;
};

export type Route = {
  name: string;
  status: string;
  action: string;
  owner: string;
  physicalid: string;
};

type Current = {
  name: string;
  displayName: string;
};

export type TreeObj = {
  id: string;
  physicalid: string;
  name: string;
  title: string;
  NumOperation?: string;
  WItext?: string;
  typeId: string;
  type: string;
  icon: string;
  current: Current;
  revision: string;
  instanceId: string;
  leaf: boolean;
  key: string;
  Kit_PrepTime?: string;
  Kit_Tpcs?: string;
  originated: number;
  modified: number;
  owner: string;
  reservedby: string;
  description: string;
  file?: string;
  parentId: string;
  routes: Route[];
  children?: TreeObj[];
  docs?: TreeObj[];
  path: string[];
  scoped?: string[];
  info: any;
};

export type Props = {
  url: string;
  api: string;
  selected: TreeObj[];
  updateTableData: Function;
  setIsLoading?: Function;
};

export type State = {
  inputValue: string;
  isLoading?: boolean;
  isClearable: boolean;
  isCreateDisabled: boolean;
  isAddContentDisabled: boolean;
  isDeleteContentDisabled: boolean;
  tasksOpts: Option[];
  rtsOpts: Option[];
  personsOpts: Option[];
  task: Option;
  rt: Option;
  person: Option;
  start: Option;
  ownership: Option;
};

type Info = {
  inputunit?: string;
  inputvalue: string;
  type?: string;
};

export type Attribute = {
  displayName: string;
  info: Info;
  mandatory?: string;
  name?: string;
  value?: string;
  writeAccess?: string;
};

export type Attributes = {
  [key: string]: Attribute;
};

export type RequestAttributeData = {
  [key: string]: string;
};

export type ConversionUnit = {
  label: string;
  multiplier: number;
  offset: number;
  type: string;
};

export type ConversionUnits = {
  [key: string]: ConversionUnit;
};

export type Units =
  typeof DIM_METER
  | typeof DIM_MM
  | typeof DIM_GRAM
  | typeof DIM_KILOGRAM
  | typeof DIM_MG

export type AttributeNames =
  | typeof ATTRIBUTE_KIT_ALLOWANCE
  | typeof ATTRIBUTE_KIT_CUT_ALLOWANCE
  | typeof ATTRIBUTE_KIT_INSTALL_ALLOWANCE
  | typeof ATTRIBUTE_KIT_MAT_CONSUMPTION_RATE
  | typeof ATTRIBUTE_KIT_MAX_GROUP_STOCK_LENGTH_CALC
  | typeof ATTRIBUTE_KIT_MAX_GROUP_STOCK_LENGTH
  | typeof ATTRIBUTE_KIT_MAX_NUMBER_OF_PARTS
  | typeof ATTRIBUTE_KIT_M_PART_NUMBER
  | typeof ATTRIBUTE_KIT_NUMBER_OF_ADD_PARTS
  | typeof ATTRIBUTE_KIT_NUMBER_OF_PARTS
  | typeof ATTRIBUTE_KIT_PART_LENGTH
  | typeof ATTRIBUTE_KIT_PART_MASS
  | typeof ATTRIBUTE_KIT_PART_WIDTH
  | typeof ATTRIBUTE_KIT_RAW_TYPE
  | typeof ATTRIBUTE_KIT_STOCK_LENGTH
  | typeof ATTRIBUTE_KIT_STOCK_HIGHT_DIAMETER
  | typeof ATTRIBUTE_KIT_STOCK_WEIGHT
  | typeof ATTRIBUTE_KIT_STOCK_WIDTH
  | typeof ATTRIBUTE_KIT_UNIT_VALUE
  | typeof ATTRIBUTE_KIT_USAGE_COEFFIC;

type DBObject = {
  attributes: Attributes;
  basics: any;
  id: string;
  physicalid: string;
  info: any;
};

export type DBObjectKit = {
  attributes: Attributes;
  basics: any;
  id: string;
  physicalid: string;
  info: {
    predecessors: DBObject[];
    scoped: DBObject[];
  };
};

export type Validation = {
  statusOK: boolean;
  message: string;
};

declare global {
  interface Window {
    widgetInstance: any;
    widget: IWidget;
  }
}

export interface IWidget {
  id: string;
  setValue: (id: string, value: any) => void;
  getValue: (id: string) => any;
  getPreferences: () => IPreference[];
  addPreference: (pref: IPreference) => void;
  [id: string]: any;
}

/**
 * Toolbar component menu tab
 *
 * @property id - tab id
 * @property title - tab title
 * @property groups - tab inner item groups
 *
 */

export interface IToolbarMenuTab {
  id: string;
  title: string;
  groups: IToolbarMenuGroup[];
}

/**
 * Toolbar component menu item group
 *
 * @property items - toolbar group inner items
 *
 */

export interface IToolbarMenuGroup {
  items: IToolbarMenuItem[];
}

/**
 * Toolbar component menu item
 *
 * @property id - menu item id
 * @property title - menu item title
 * @property onClickFunction - function raised on click
 * @property image - link to menu item image
 * @property isLoading - is toolbar item loading property
 * @property subItems - toolbar item subitems
 * @property isHover - is toolbar menu item hovered state
 *
 */

export interface IToolbarMenuItem {
  id: string;
  title: string;
  onClickFunction: Function;
  image?: string;
  isLoading?: boolean;
  subItems?: IToolbarMenuItem[];
  isHover?: boolean;
  disabled?: boolean;
}

/**
 * Interface for establishing connection with system
 *
 * @property callback - Callback after calculating the Security Context
 * @property url - 3DSpace URL
 *
 */

export interface IConnection {
  callback: () => void;
  url: string;
  userId?: string;
}

/**
 * Interface for constructing dynamic user preferences
 *
 * @property name - preference name
 * @property type - preference type
 * @property label - preference label
 * @property options - preference isSelectable options
 * @property defaultValue - preference default chosen value
 *
 */

export interface IPreference {
  name: string;
  type: "text" | "boolean" | "range" | "list" | "hidden";
  label: string;
  options?: IPreferenceOption[];
  defaultValue?: string;
  value?: string;
}

/**
 * Interface for constructing user preference option
 *
 * @property label - preference displayed name
 * @property value - preference hidden value
 *
 */

export interface IPreferenceOption {
  label: string;
  value: string;
}

export interface IWidgetUserSettings {
  [id: string]: any;
}

export interface IWidgetSystemSettings {
  [id: string]: string;
}

export interface IObjectHistoryRecord {
  id: string;
  title: string;
}

export interface ISelectedItem {
  objectId: string;
  displayName?: string;
}
