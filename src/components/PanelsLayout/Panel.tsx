import * as React from 'react';
import SVG from 'react-inlinesvg';
import { IViewProps, IPanelProps } from './interfaces';

// eslint-disable-next-line @typescript-eslint/no-var-requires
const arrowLeftIcon = require('../../images/chevron-left-solid.svg');
// eslint-disable-next-line @typescript-eslint/no-var-requires
const arrowRightIcon = require('../../images/chevron-right-solid.svg');

export const Panel: React.FC<IPanelProps> = ({
  children,
  direction,
  collapsed,
  width,
  resizable,
  onResize,
  componentId
}: IPanelProps) => {
  const savedPanelSize = React.useRef(width);
  const currentPanelCollapse = React.useRef(collapsed);

  const [panelSize, setPanelSize] = React.useState(width);
  const [panelCollapsed, setCollapsed] = React.useState(collapsed);
  const [isResizing, setIsResizing] = React.useState(false);

  const collapsePanel = React.useCallback(() => {
    setCollapsed(prevState => !prevState);
    setPanelSize(savedPanelSize.current);
  }, []);

  React.useEffect(() => {
    setPanelSize(width);
    savedPanelSize.current = width;
  }, []);

  // check resize actions
  const onMouseDownHandler = React.useCallback(
    (e: React.MouseEvent<HTMLDivElement, MouseEvent>) => {
      document.getElementsByTagName('body')[0].classList.add('moving');
      let resizerClientX: number = e.clientX;
      const lastPanelSize: number = savedPanelSize.current; // fix width value before resizing
      setIsResizing(true);

      const onMouseMoveHandler = (ev: MouseEvent) => {
        let changeValue = 0;
        if (direction === 'left') {
          changeValue = ev.clientX - resizerClientX;
        } else {
          changeValue = -(ev.clientX - resizerClientX);
        }

        if (!currentPanelCollapse.current && Math.abs(changeValue) > 2) {
          resizerClientX = ev.clientX;
          if (savedPanelSize.current < 25) {
            setPanelSize(lastPanelSize);
            setCollapsed(true);
          } else {
            setPanelSize(prevSize => prevSize + changeValue);
          }
        }
      };

      const onMouseUpHandler = () => {
        document.removeEventListener('mousemove', onMouseMoveHandler);
        document.removeEventListener('mouseup', onMouseUpHandler);
        setIsResizing(false);
        if (lastPanelSize !== savedPanelSize.current && onResize) {
          onResize(savedPanelSize.current); // if collapsed by moving than previous width
        }
        document.getElementsByTagName('body')[0].classList.remove('moving');
      };

      document.addEventListener('mousemove', onMouseMoveHandler);
      document.addEventListener('mouseup', onMouseUpHandler);
    },
    [direction, onResize]
  );

  React.useEffect(() => {
    savedPanelSize.current = panelSize;
  }, [panelSize]);

  React.useEffect(() => {
    currentPanelCollapse.current = panelCollapsed;
  }, [panelCollapsed]);

  return (
    <div
      className={`panel ${direction}${panelCollapsed ? ' collapsed' : ''}${isResizing ? ' resizing' : ''}`}
      style={{ width: `${!panelCollapsed ? panelSize : '0'}px` }}
    >
      <div className="panel-body">{children}</div>
      {!panelCollapsed && resizable ? (
        <div className={`resizer ${direction}`} onMouseDown={onMouseDownHandler} />
      ) : null}
      <div className={`minify-button ${direction}`} onClick={collapsePanel}>
        {panelCollapsed ? (
          direction === 'left' ? (
            <SVG src={arrowRightIcon} />
          ) : (
            <SVG src={arrowLeftIcon} />
          )
        ) : direction === 'left' ? (
          <SVG src={arrowLeftIcon} />
        ) : (
          <SVG src={arrowRightIcon} />
        )}
      </div>
    </div>
  );
};

Panel.defaultProps = {
  onResize: null,
  resizable: true
};

export const View: React.FC<IViewProps> = ({ children }: IViewProps) => {
  return <div className="view">{children}</div>;
};

View.defaultProps = {
  direction: 'center'
};
