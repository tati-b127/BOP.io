import * as React from 'react';
import ReactResizeDetector from 'react-resize-detector';
import SVG from 'react-inlinesvg';
import { ITabProps, ITabsProps, ITabHeader } from '../interfaces/interfaces';
import '../styles.scss';
import Tab from './Tab';

// eslint-disable-next-line @typescript-eslint/no-var-requires, import/no-webpack-loader-syntax
//const contractIcon = require('!svg-inline-loader!!../images/contractIcon.svg');

/**
 * A Tabs tags for wrapping separate tabs
 * @param centered defines tabs headers alignment, default false.
 * @param children Use Tab tags as children.
 */

export const Tabs: React.FC<ITabsProps> = ({ onChangeTab, centered, children }) => {
  const tabHeaders = React.useRef<ITabHeader[]>([]);
  const tabHeadersSize = React.useRef<number[]>([]);
  const tabSizeChecker = React.useRef<{ needCheck: boolean; prevSize: number }>({ needCheck: false, prevSize: 0 });
  const tabsNavigationPanel = React.useRef<HTMLDivElement>();

  const [activeTab, setActiveTab] = React.useState<string | null>(null);
  const [menuItems, setMenuItems] = React.useState<ITabHeader[]>([]);
  const [menuItemsVisible, setMenuItemsVisible] = React.useState(false);

  const onTabItemClickHandler = React.useCallback((tabKey: string) => {
    onChangeTab(tabKey);
    setActiveTab(tabKey);
  }, []);

  const onResizeTabsHandler = React.useCallback(
    (width: number) => {
      if (width < tabsNavigationPanel.current.clientWidth) {
        if (tabHeaders.current.length > 0) {
          tabSizeChecker.current = { needCheck: true, prevSize: tabsNavigationPanel.current.clientWidth };
          const lastTabHeader = tabHeaders.current.pop();
          if (!menuItems.find(item => item.tabKey == lastTabHeader.tabKey)) {
            setMenuItems(prevArr => [...prevArr, lastTabHeader]);
          }
        }
      } else if (
        menuItems.length > 0 &&
        menuItems.length === tabHeadersSize.current.length &&
        width > tabsNavigationPanel.current.clientWidth + tabHeadersSize.current[tabHeadersSize.current.length - 1]
      ) {
        const items = [...menuItems];
        tabHeaders.current.push(items.splice(0, 1)[0]);
        tabHeadersSize.current.pop();
        setMenuItems(items);
      }
    },
    [menuItems]
  );

  const onMenuButtonClickHandler = React.useCallback(() => {
    setMenuItemsVisible(prevState => !prevState);
  }, []);

  const dropMenuHandler = React.useCallback(() => {
    setMenuItemsVisible(false);
  }, []);

  // tabs & headers preparation
  React.useEffect(() => {
    const tabs: ITabHeader[] = [];
    let openedTab: string = null;
    React.Children.forEach(children, child => {
      if (React.isValidElement<ITabProps>(child)) {
        const elementChild: React.ReactElement<ITabProps> = child;
        const header: ITabHeader = {
          name: elementChild.props.name,
          disabled: elementChild.props.disabled,
          tabKey: elementChild.props.tabKey,
          onClick: onTabItemClickHandler,
          inMenu: false,
          isActive: elementChild.props.active,
          icon: elementChild.props.icon,
          title: elementChild.props.title
        };
        tabs.push(header);
        if (!openedTab && elementChild.props.active && !elementChild.props.disabled) {
          openedTab = tabs[tabs.length - 1].tabKey;
        }
      }
    });
    tabHeaders.current = tabs;
    setActiveTab(activeTab || openedTab || tabHeaders.current[0].tabKey);
  }, [activeTab, children, onTabItemClickHandler]);

  // add/remove document event listener on show/hide menu
  React.useEffect(() => {
    if (menuItemsVisible) {
      document.addEventListener('click', dropMenuHandler);
    } else {
      document.removeEventListener('click', dropMenuHandler);
    }
  }, [dropMenuHandler, menuItemsVisible]);

  // save width of lasted tab item pushed to menu
  React.useEffect(() => {
    if (tabSizeChecker.current.needCheck) {
      const width = tabSizeChecker.current.prevSize - tabsNavigationPanel.current.clientWidth;
      tabHeadersSize.current.push(width);
      tabSizeChecker.current.needCheck = false;
      tabSizeChecker.current.prevSize = 0;
    }
  }, [menuItems]);

  return (
    <ReactResizeDetector handleWidth>
      <div className="tabs-component">
        {menuItemsVisible && (
          <div className="hided-items-menu">
            {menuItems.map(tab => {
              return (
                <TabHeader
                  tabKey={tab.tabKey}
                  key={tab.tabKey}
                  name={tab.name}
                  isActive={tab.tabKey === activeTab}
                  inMenu={true}
                  onClick={tab.onClick}
                  disabled={tab.disabled}
                  icon={tab.icon}
                  title={tab.title}
                >
                  {tab.name}
                </TabHeader>
              );
            })}
          </div>
        )}

        <div className="tabs-navigator-placeholder">
          <div className={`tabs-navigator${centered ? ' centered' : ''}`} ref={tabsNavigationPanel}>
            {tabHeaders.current.map(tab => {
              return (
                <TabHeader
                  tabKey={tab.tabKey}
                  key={tab.tabKey}
                  name={tab.name}
                  isActive={tab.tabKey === activeTab}
                  inMenu={false}
                  onClick={tab.onClick}
                  disabled={tab.disabled}
                  icon={tab.icon}
                  title={tab.title}
                >
                  {tab.name}
                </TabHeader>
              );
            })}
          </div>
        </div>
        <div className="tab-content">
          {React.Children.map(children, child => {
            if (React.isValidElement<ITabProps>(child)) {
              const elementChild: React.ReactElement<ITabProps> = child;
              return (
                <Tab
                  name={elementChild.props.name}
                  tabKey={elementChild.props.tabKey}
                  active={elementChild.props.tabKey === activeTab}
                >
                  {child.props.children}
                </Tab>
              );
            }
          })}
        </div>
      </div>
    </ReactResizeDetector>
  );
};

const TabHeader: React.FC<ITabHeader> = ({ children, tabKey, name, isActive, inMenu, disabled, onClick, icon, title }) => {
  const [iconElement, setIconElement] = React.useState(null);
  const onClickHandler = React.useCallback(() => {
    if (onClick && !disabled) onClick(tabKey);
  }, [disabled, tabKey, onClick]);

  React.useEffect(() => {
    if (icon) {
      const isSvg = icon.indexOf('<svg') === 0;
      if (isSvg) {
        setIconElement(<SVG className="icon" src={icon} />);
      } else {
        setIconElement(<img className="icon" src={icon} alt={name} />);
      }
    }
  }, [icon, name]);

  return (
    <div
      className={`tab-header${isActive ? ' active' : ''}${inMenu ? ' menu-item' : ''}${disabled ? ' disabled' : ''}`}
      onClick={onClickHandler}
      onKeyPress={onClickHandler}
      tabIndex={0}
      role="button"
      title={title}
    >
      {icon && <div className="icon-container">{iconElement}</div>}
      <div>{children}</div>
    </div>
  );
};

Tabs.defaultProps = {
  centered: false,
  children: []
};

export { Tabs as default };
