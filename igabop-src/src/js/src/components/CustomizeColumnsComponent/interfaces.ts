import { ICustomReactComponentProps, ICustomReactComponentState } from '../CustomReactComponent';
import { Column } from 'react-table';
import { DraggableId } from 'react-beautiful-dnd';

/** Customize Columns Component props */
export interface ICustomizeColumnsComponentProps extends ICustomReactComponentProps {
  /** Columns to handle in table */
  columns: ICustomizeColumnsTableItem[];
  /** A callback function when the customization columns table is closed without saving
   * The handler is of type `() => any`
   */
  onClose: () => any;
  /** A callback function when the customization columns table is closed with saving
   * The handler is of type `(columns) => any`
   */
  onSave?: (columns: ICustomizeColumnsTableItem[]) => any;
}

/** Customize Columns Component state */
export interface ICustomizeColumnsComponentState extends ICustomReactComponentState {
  /** Current columns */
  columns: Column[];
  /** Table data */
  tableData: ICustomizeColumnsTableItem[];
  /** Are all columns selected */
  selectAll: boolean;
  /** Is modal to confirm saving open */
  isConfirmModalOpen: boolean;
}

export interface ICustomizeColumnsTableItem {
  id?: string; // used in react table
  key?: string; // used in basetable
  title: string;
  width: number;
  hidden: boolean;
}

export interface IDroppableStateSnapshot {
  isDraggingOver: boolean;
  draggingOverWith?: DraggableId;
  draggingFromThisWith?: DraggableId;
  isUsingPlaceholder?: boolean;
}

export interface IComponentColumn {
  id: string;
  width: number;
  hidden: boolean;
}

export interface IUserComponentConfig {
  columns: IComponentColumn[];
}

