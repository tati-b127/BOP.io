import { IPreference, IWidgetUserSettings } from '../store/interfaces';

const userSettingsVariable = 'userSettings';

export const getUserSettings = (componentId: string): any => {
  let value: any = null;
  const userSettingsPreference = GetWidgetPreference(userSettingsVariable);
  if (userSettingsPreference) {
    const userSettings: IWidgetUserSettings = JSON.parse(userSettingsPreference.value);
    if (userSettings) {
      value = userSettings[componentId];
    }
  }
  return value;
};

export const saveUserSettings = (componentId: string, value: any): any => {
  let userSettingsPreference: IPreference = GetWidgetPreference(userSettingsVariable);
  let shouldCreateNewSettings = false;
  if (userSettingsPreference) {
    const userSettingsPreferenceValue = userSettingsPreference.value;
    if (userSettingsPreferenceValue) {
      const userSettings: IWidgetUserSettings = JSON.parse(userSettingsPreferenceValue);
      userSettings[componentId] = value;
      userSettingsPreference.value = JSON.stringify(userSettings);
    } else {
      shouldCreateNewSettings = true;
    }
  } else {
    shouldCreateNewSettings = true;
  }
  if (shouldCreateNewSettings) {
    const newUserSettings: IWidgetUserSettings = {};
    newUserSettings[componentId] = value;
    userSettingsPreference = {
      name: userSettingsVariable,
      label: 'userSettings',
      type: 'hidden',
      value: JSON.stringify(newUserSettings)
    };
  }
  SaveWidgetPreference(userSettingsPreference);
};

export function GetWidgetPreferences(): IPreference[] {
  const { widget } = window;
  return widget.getPreferences();
}

export function GetWidgetValue(name: string): unknown {
  const { widget } = window;
  return widget.getValue(name);
}

export function GetWidgetPreference(id: string): IPreference | null {
  const { widget } = window;
  if (!widget) return null;

  return widget.getPreferences().find((preference: IPreference) => preference.name === id);
}

export function SaveWidgetPreference(preference: IPreference): void {
  const { widget } = window;
  if (!widget) return;

  widget.addPreference(preference);
  if (preference.value) {
    widget.setValue(preference.name, preference.value);
  }
}
