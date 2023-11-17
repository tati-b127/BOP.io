export type panelDirection = 'right' | 'left';

export interface IPanel {
  componentId?: string;
  children: React.ReactNode;
}
export interface IPanelProps extends IPanel {
  direction: panelDirection;
  collapsed: boolean;
  width: number;
  resizable?: boolean;
  onResize?: (width: number) => void;
}

export interface IViewProps extends IPanel {
  direction?: string;
}

export interface IPanelsLayoutProps {
  children: React.ReactNode;
  componentId?: string;
}
