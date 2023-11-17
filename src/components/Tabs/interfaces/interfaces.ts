import React from "react";

export interface ITabProps {
  name: string;
  active?: boolean;
  disabled?: boolean;
  icon?: string;
  tabKey: string;
  title?: string;
  children?: React.ReactNode;
}

export interface ITabsProps {
  onChangeTab?: Function;
  centered?: boolean;
  children?: React.ReactNode;
}

export interface ITabHeader {
  tabKey: string;
  name: string;
  isActive: boolean;
  children?: React.ReactNode;
  icon?: string;
  title?: string;
  inMenu: boolean;
  disabled?: boolean;
  onClick: (itemName: string) => void;
}
