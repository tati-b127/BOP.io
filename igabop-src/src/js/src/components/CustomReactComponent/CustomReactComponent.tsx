import React from 'react';

import { Item, Menu } from 'react-contexify';
import {
  ICustomReactComponentProps,
  ICustomReactComponentState,
  IFetchHeaderParams,
  IFetchParams,
  TFetchMethodType,
  IWidgetSystemSettings,
  IPreference
} from './interfaces';

/**
 * Base component CustomReactComponent that provides opportunity to fetch data from 3dspace
 */

export class CustomReactComponent<
  P extends ICustomReactComponentProps,
  S extends ICustomReactComponentState
> extends React.Component<P, S> {
  protected widget: any;

  protected objectId: string;

  constructor(props: P) {
    super(props);
    this.widget = window.widget;
    let objectId = null;

    if (this.widget.getValue('SelectedItem')) {
      objectId = this.widget.getValue('SelectedItem')[0].objectId;
    }

    this.objectId = objectId;
  }

  protected getObjectId() {
    return this.objectId;
  }

  protected Fetch(
    url: string,
    method: TFetchMethodType,
    params?: IFetchParams,
    headerParams?: IFetchHeaderParams
  ): Promise<unknown> {
    const headers: any = {
      'Content-Type': 'application/json'
    };
    if (this.props.scHeader) {
      headers.securitycontext = this.props.scHeader;
    }
    if (headerParams) {
      Object.entries(headerParams).forEach(parameter => {
        headers[parameter[0]] = parameter[1];
      });
    }
    this.setState({ isLoading: true });
    return new Promise((fulfill, reject) => {
      const promiseObject: any = {
        method,
        type: 'json',
        onComplete: (data: any) => {
          fulfill(data);
          this.setState({ isLoading: false });
        },
        onFailure: (error: any, message: any) => {
          reject({ error, message });
          this.setState({ isLoading: false });
        },
        headers,
        timeout: 300000
      };
      if (params) {
        Object.entries(params).forEach(parameter => {
          promiseObject[parameter[0]] = parameter[1];
        });
      }
      window.widget.WAFData.authenticatedRequest(encodeURI(this.props.url + url), promiseObject);
    });
  }

  protected FetchJSON(
    url: string,
    method: string,
    scHeader?: string,
    data?: any,
    responseType?: string,
    timeout?: number
  ) {
    let headers: {};

    if (this.props.scHeader) {
      headers = {
        'Content-Type': 'application/json',
        securitycontext: this.props.scHeader
      };
    } else {
      headers = {
        'Content-Type': 'application/json'
      };
    }

    return new Promise((fulfill, reject) => {
      const promiseObject: any = {
        method,
        type: 'json',
        onComplete: fulfill,
        onFailure: reject,
        headers,
        data,
        timeout: 300000
      };
      if (responseType) {
        promiseObject.responseType = responseType;
        promiseObject.onFailure = (error: any, response: any) => reject({ error, response });
      }
      window.widget.WAFData.authenticatedRequest(url, promiseObject);
    });
  }

  protected FetchBlob(
    url: string,
    method: string,
    scHeader?: string,
    data?: any,
    responseType?: string,
    timeout?: number
  ) {
    let headers: any;

    if (this.props.scHeader) {
      headers = {
        securitycontext: this.props.scHeader
      };
    }
    if (!timeout) timeout = 300000;

    return new Promise((fulfill, reject) => {
      window.widget.WAFData.authenticatedRequest(url, {
        method,
        responseType: responseType || 'blob',
        onComplete: fulfill,
        onFailure: reject,
        headers,
        data,
        timeout
      });
    });
  }

  protected FetchTypeAhead(type: string, search: string) {
    search = search == '*' ? '*' : `*${search}*`;

    return this.FetchJSON(`/resources/v2/e6w/service/ObjectSearch?searchStr=${search}&typeStr=${type}`, 'GET');
  }

  protected GetWidgetPreferences(): IPreference[] {
    const preferences: IPreference[] = this.widget.getPreferences();
    return preferences;
  }

  protected GetWidgetPreference(id: string): IPreference {
    const preference: IPreference = this.GetWidgetPreferences().find(
      (preference: IPreference) => preference.name === id
    );
    return preference;
  }

  protected SaveWidgetPreference(preference: IPreference) {
    this.widget.addPreference(preference);
    if (preference.value) {
      this.widget.setValue(preference.name, preference.value);
    }
  }

  protected RenderContextMenuArray() {
    if (this.props.contextMenuArray) {
      return this.props.contextMenuArray.map(contextMenu => {
        const items = contextMenu.items.map(item => {
          return (
            <Item className="context-menu-item" onClick={item.onClick}>
              {item.icon ? <img src={item.icon} className="context-icon" /> : null}
              {item.title}
            </Item>
          );
        });
        return <Menu id={contextMenu.value}>{items}</Menu>;
      });
    }
    return null;
  }
}
