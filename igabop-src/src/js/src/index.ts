import React from 'react';
import ReactDom from 'react-dom';

import './styles/index.scss';
import App from './components/app';
import { IConnection, IPreference, IPreferenceOption } from './store/interfaces';
import { BOP_PATH } from './components/consts';

class BOPWidget {
  protected widget: any;

  protected WAFData: any;

  protected connection: IConnection;

  protected prevHeader: string;

  constructor() {
    this.widget = window.widget;
  }

  init() {
    window.widget.setTitle('Bill of Process');
    window.widget.setBody('<div id="root"></div>');
    window.widget.i3DXCompassServices.getServiceUrl({
      serviceName: '3DSpace',
      platformId: window.widget.getValue('x3dPlatformId'),
      onComplete: (url: string) => {
        window.widget.setValue('3DSpace', url);
        this.WAFData = window.widget.WAFData;
        this.PopulatePreferences(url, () => this.Render());
      },
      onFailure: (err: any) => console.log(err)
    });
  }

  protected PopulatePreferences(url: string, callback: () => void) {
    this.connection = { url, callback };
    this.RetrievePreferredSC(url);
  }

  protected RetrievePreferredSC(URL: string) {
    let pathWS = `${URL}/resources/modeler/pno/person?current=true`;
    pathWS += '&select=preferredcredentials&select=collabspaces';
    this.WAFData.authenticatedRequest(pathWS, {
      method: 'GET',
      type: 'json',
      onComplete: (e: any) => this.ComputeList(e)
    });
  }

  protected ComputeList(e: any) {
    const TheCollabSpacesArray = e.collabspaces;
    const preferenceDictionary: Record<string, string>[] = [];
    if (TheCollabSpacesArray && TheCollabSpacesArray.length > 0) {
      for (let i = 0; i < TheCollabSpacesArray.length; i++) {
        const TheCurrentCSJson = TheCollabSpacesArray[i];
        const TheCurrentCS = TheCurrentCSJson.name;
        const TheCouples = TheCurrentCSJson.couples;
        for (let j = 0; j < TheCouples.length; j++) {
          const TheCurrentCoupleJson = TheCouples[j];
          const TheOrganization = TheCurrentCoupleJson.organization;
          const TheRole = TheCurrentCoupleJson.role;
          const TheCurrentOrg = TheOrganization.name;
          const TheCurrentRole = TheRole.name;
          const TheCurrentRoleNLS = TheRole.nls;
          const SCCurrent = `${TheCurrentRole}.${TheCurrentOrg}.${TheCurrentCS}`;
          const SCCurrent_NLS = `${TheCurrentRoleNLS} ● ${TheCurrentOrg} ● ${TheCurrentCS}`;
          preferenceDictionary.push({ SCCurrent_NLS, SCCurrent });
        }
      }
    }
    const options: IPreferenceOption[] = [];
    preferenceDictionary.forEach(item => {
      options.push({ value: item.SCCurrent, label: item.SCCurrent_NLS });
    });
    let defaultValue = window.widget.getValue('scHeader');
    if (!defaultValue) {
      defaultValue = this.GetPreferredValue(e.preferredcredentials);
    }
    const scHeaderPref: IPreference = {
      name: 'scHeader',
      type: 'list',
      label: 'Credentials'
    };
    if (options.length) {
      scHeaderPref.options = options;
    }
    if (defaultValue !== '') {
      scHeaderPref.defaultValue = defaultValue;
      window.widget.setValue('scHeader', defaultValue);
    }
    window.widget.addPreference(scHeaderPref);
    const cBack = this.connection.callback;
    this.connection.callback = null;
    cBack();
  }

  protected GetPreferredValue(preferredJson: any): string {
    if (preferredJson.collabspace && preferredJson.role && preferredJson.organization) {
      const TheCBPreferredJson = preferredJson.collabspace;
      const TheRolePreferredJson = preferredJson.role;
      const TheOrgPreferredJson = preferredJson.organization;
      const TheCBPreferred = TheCBPreferredJson.name;
      const TheRolePreferred = TheRolePreferredJson.name;
      const TheOrgPreferred = TheOrgPreferredJson.name;
      return `${TheRolePreferred}.${TheOrgPreferred}.${TheCBPreferred}`;
    }
    return '';
  }

  Fetch2(url: string) {
    const headers = { 'Content-Type': 'application/json' };
    return new Promise((fulfill, reject) => {
      const promiseObject = {
        method: 'GET',
        type: 'json',
        onComplete: (data: any) => {
          fulfill(data);
        },
        onFailure: (error: any, message: any) => {
          reject({ error, message });
        },
        headers,
        timeout: 300000
      };
      window.widget.WAFData.authenticatedRequest(encodeURI(url), promiseObject);
    });
  }

  protected Render() {
    const url_passport: string = window.widget.data['3DPassport'];
    const url_passport_without_path = url_passport.replace(/^(.*)\/3dpassport$/, '$1');
    const url_dashboard_without_path = url_passport_without_path.replace('3dpassport', '3ddashboard');
    console.log('render(), 3dpassport url is ', url_passport);
    console.log('render(), 3dpassport url without path is ', url_passport_without_path);
    console.log('render(), 3ddashboard url without path is ', url_dashboard_without_path);

    this.Fetch2(`${url_passport}/login?service=${url_dashboard_without_path}${BOP_PATH}`)
      .then((data: any) => {
        const ticket = `ticket=${data.access_token}`;
        const where = `where=name smatch 1`; // avoid heavy responses
        this.Fetch2(`${url_dashboard_without_path}${BOP_PATH}/pprcontexts?${ticket}&${where}`)
          .then((data: any) => {
            ReactDom.render(
              React.createElement(App, {
                url: url_dashboard_without_path,
                scHeader: window.widget.getValue('scHeader'),
                componentId: 'IGABOP_root',
                droppable: window.widget.DnD.droppable,
                draggable: window.widget.DnD.draggable,
                clean: window.widget.DnD.clean,
                widgetId: window.widget.id
              }),
              document.getElementById('root')
            );
          })
          .catch(error => console.log(error));
      })
      .catch(error => console.log(error));
  }
}

const bopWidget = new BOPWidget();
window.widgetInstance = bopWidget;
