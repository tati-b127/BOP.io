import * as React from 'react';
import { ITabProps } from '..';

/**
 * Tab tag for wrapping content
 * @param name Name of the tab
 * @param tabKey Key of the tab
 * @param active? Defines currently opened tab by default. If active=true in several tabs, the active tab will be last. By default active is not set.
 * @param disabled? Disables the tab. The tab is not hided but cannot be selected. By default disabled is not set.
 * @param icon? Icon element as image source. can be used image or svg file. by default is not set.
 * @param title? Tooltip title
 */

export const Tab: React.FC<ITabProps> = ({ children, active }) => {
  return <div className={`tab-container ${active ? 'active' : ''}`}>{children}</div>;
};

export { Tab as default };
