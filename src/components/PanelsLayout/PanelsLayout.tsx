import React, { useState, useEffect } from 'react';
import { IPanelsLayoutProps } from '.';
import './styles.scss';

export const PanelsLayout: React.FC<IPanelsLayoutProps> = ({ children, componentId }: IPanelsLayoutProps) => {
  const [panels, setPanels] = useState<React.ReactNode[]>([]);

  useEffect(() => {
    let leftPanel: React.ReactElement = null;
    let rightPanel: React.ReactElement = null;
    let viewPanel: React.ReactElement = null;
    React.Children.forEach(children, child => {
      if (!React.isValidElement(child)) return;
      const currentChild = child as React.ReactElement;
      if (currentChild.props.direction === 'left') {
        leftPanel = React.cloneElement(child, { componentId: componentId.concat('.left') });
      } else if (currentChild.props.direction === 'right') {
        rightPanel = React.cloneElement(child, { componentId: componentId.concat('.right') });
      } else if (currentChild.props.direction === 'center') {
        viewPanel = child;
      }
    });
    if (viewPanel) {
      setPanels([leftPanel, viewPanel, rightPanel]);
    }
  }, [children, componentId]);
  return <div className="panels-layout">{panels}</div>;
};
export { PanelsLayout as default };
