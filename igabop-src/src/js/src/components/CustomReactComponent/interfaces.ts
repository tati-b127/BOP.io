import { MenuItemEventHandler } from 'react-contexify/src/types/index';

interface IContextMenuItem {
  icon?: string;
  title: string;
  onClick: (args: MenuItemEventHandler) => any;
}

interface IContextMenu {
  value: string;
  items: IContextMenuItem[];
}

export interface ICustomReactComponentProps {
  url: string;
  scHeader?: string;
  contextMenuArray?: IContextMenu[];
  objectId?: string;
  componentId: string;
}

export interface ICustomReactComponentState {
  isLoading?: boolean;
}

export interface IFetchHeaderParams {
  'Content-Type'?: ContentTypeHeaderType;
  Accept?: TAcceptHeaderType;
}

export interface IFetchParams {
  data?: any;
  responseType?: string;
  timeout?: number;
  type?: string;
}

type ContentTypeHeaderType =
  | 'application/json'
  | 'application/x-www-form-urlencoded'
  | 'multipart/form-data'
  | 'multipart/mixed stream'
  | string;

export type TFetchMethodType = 'GET' | 'POST' | 'PUT' | 'DELETE';

type TAcceptHeaderType = 'application/octet-stream';

interface IPreferenceOption {
  label: string;
  value: string;
}

export interface IPreference {
  name: string;
  type: 'text' | 'boolean' | 'range' | 'list' | 'hidden';
  label: string;
  options?: IPreferenceOption[];
  defaultValue?: string;
  value?: string;
}

export interface IWidgetSystemSettings {
  [id: string]: string;
}

