import * as React from 'react';
import {
  CustomReactComponent
} from '../CustomReactComponent';
//import { ModalDialogComponent } from '@components/ModalDialogComponent';
import ReactTable, { Column, RowInfo } from 'react-table';
import selectTableHOC from 'react-table/lib/hoc/selectTable';
import {
  DragDropContext,
  Draggable,
  DraggableProvided,
  DraggableStateSnapshot,
  Droppable,
  DroppableProvided,
  DropResult
} from 'react-beautiful-dnd';
import { getUserSettings, saveUserSettings } from '../../utils/widgetUtils';
import 'react-table/react-table.css';
import './styles.css';

import { ICustomizeColumnsTableItem, IDroppableStateSnapshot, ICustomizeColumnsComponentProps, ICustomizeColumnsComponentState } from './interfaces';

const settingsIcon = require('./images/settingsIcon.svg');
const deleteIcon = require('./images/deleteIcon.svg');
const saveIcon = require('./images/saveIcon.svg');
const cancelIcon = require('./images/cancelIcon.svg');

/**
 *
 * Component for rendering table to customize table columns using checkboxes to disable/enable columns and drag and drop feature to reorder columns
 *
 */
export class CustomizeColumnsComponent extends CustomReactComponent<
  ICustomizeColumnsComponentProps,
  ICustomizeColumnsComponentState
> {
  protected widget: any;

  protected tableRef: HTMLDivElement;

  protected tBodyRef: HTMLDivElement;

  protected customizeContainerRef: HTMLDivElement;

  protected scroll = 0;

  constructor(props: ICustomizeColumnsComponentProps) {
    super(props);
    this.widget = window.widget;
    const tableSettings = getUserSettings(props.componentId) || {};
    let columnSettings = [];
		if(tableSettings){
			const settings = JSON.parse(tableSettings);
      // @ts-ignore
          columnSettings = settings.columns.filter(column => props.columns.some(col => col.key === column.id)).map(column => {
				const tableColumn = props.columns.find(col => col.key === column.id);
				return {
          id: column.id,
          title: tableColumn.title,
          hidden: column.hidden,
          width: column.width
        }
      });
    }else{
      columnSettings = props.columns.map(column => {
        return {
          id: column.key,
          title: column.title,
          hidden: column.hidden,
          width: column.width
        }
      })
    }



    this.state = {
      // @ts-ignore
      selectAll: !columnSettings.some(column => column.hidden === true),
      tableData: columnSettings,
      columns: [
        { id: 'id', show: false, accessor: 'id', Header: '' },
        {
          id: 'title',
          show: true,
          accessor: 'title',
          Header: 'Название столбца'
        }
      ],
      isConfirmModalOpen: false,
      isLoading: false
    };
  }

  getSnapshotBeforeUpdate(prevProps: any, prevState: ICustomizeColumnsComponentState) {
    return {
      bodyScrollTop: this.tBodyRef.scrollTop,
      bodyScrollLeft: this.tBodyRef.scrollLeft,
      tableScrollTop: this.tableRef.scrollTop,
      tableScrollLeft: this.tableRef.scrollLeft
    };
  }

  componentDidUpdate(prevProps: any, prevState: ICustomizeColumnsComponentState, snapshot: any) {
    this.tBodyRef.scrollTo(snapshot.bodyScrollLeft, snapshot.bodyScrollTop);
    this.tableRef.scrollTo(snapshot.tableScrollLeft, snapshot.tableScrollTop);
  }

  render() {
    const SelectTable = selectTableHOC(ReactTable);
    return (
      <div className="customize-columns-container" ref={ref => (this.customizeContainerRef = ref)}>
        <DragDropContext onDragEnd={this.OnDragEnd}>
          <SelectTable
            data={[...this.state.tableData]}
            columns={[...this.state.columns]}
            keyField="id"
            resizable={false}
            sortable={false}
            showPagination={false}
            minRows={0}
            pageSize={this.state.tableData.length}
            getTrProps={this.RowProps}
            getTrGroupProps={this.RowProps}
            isSelected={this.IsSelected}
            selectAll={this.state.selectAll}
            SelectInputComponent={(element: any) => this.SelectComponent(element)}
            SelectAllInputComponent={(element: any) => this.SelectAllComponent(element)}
            TableComponent={(element: any) => this.TableComponent(element)}
            TrGroupComponent={(element: any) => this.TrGroupComponent(element)}
            TbodyComponent={(element: any) => this.TBodyComponent(element)}
          />
        </DragDropContext>
        <div className="bottom-panel">
          <div className="buttons-list">
            <button onClick={this.SaveOptions} disabled={false}>
              Сохранить
            </button>
            <button onClick={this.props.onClose} disabled={false}>
              Отменить
            </button>
          </div>
        </div>
      </div>
    );
  }

  protected SaveOptions = () => {
    const columns: ICustomizeColumnsTableItem[] = this.state.tableData;
    const rawSettings = JSON.stringify({columns: columns});
    saveUserSettings(this.props.componentId, rawSettings);
    if(this.props.onSave) this.props.onSave(columns);
  }

  protected Reorder(array: ICustomizeColumnsTableItem[], startIndex: number, endIndex: number) {
    const result = Array.from(array);
    const [removed] = result.splice(startIndex, 1);
    result.splice(endIndex, 0, removed);

    return result;
  }

  protected RowProps(state: any, rowInfo: RowInfo, column: Column, instance: any) {
    return {
      style: {
        maxHeight: '40px'
      },
      rowInfo
    };
  }

  protected OnDragEnd = (result: DropResult): void => {
    if (!result.destination) {
      return;
    }
    const tableData = this.Reorder(this.state.tableData, result.source.index, result.destination.index);
    this.setState({ tableData });
  }

  protected IsSelected = (key: string): boolean => {
    if (this.state.tableData) {
      const column = this.state.tableData.find(item => item.id === key);
      if (column) {
        return !column.hidden;
      }
      return false;
    }
    return false;
  }

  protected ToggleSelection = (row: any) => {
    const { tableData } = this.state;
    const hidden = tableData.find(column => column.id === row.id).hidden;
    tableData.find(column => column.id === row.id).hidden = !hidden;
    this.setState({
      tableData
    });
    this.CheckSelection(tableData);
  }

  protected CheckSelection = (tableData: ICustomizeColumnsTableItem[]) => {
    let selectAll = false;
    const object = tableData.find(key => key.hidden === true);
    if (!object) {
      selectAll = true;
    }
    this.setState({
      selectAll
    });
  }

  protected ToggleAllSelection = () => {
    let { tableData } = this.state;
    const selectAll = !this.state.selectAll;
    tableData = tableData.map(column => {
      column.hidden = !selectAll;
      return column;
    });
    this.setState({
      selectAll,
      tableData
    });
  }

  protected SelectComponent(element: any) {
    return (
      <input type="checkbox" checked={!element.row.hidden} onChange={e => {
        this.ToggleSelection(element.row);
      }}></input>
    );
  }

  protected TableComponent(element: any) {
    return (
      <div className="rt-table" ref={ref => (this.tableRef = ref)} role="grid">
        {element.children}
      </div>
    );
  }

  protected TBodyComponent(element: any) {
    return (
      <div id="customize-columns-body" className="rt-tbody" ref={ref => (this.tBodyRef = ref)}>
        <Droppable droppableId="1" ignoreContainerClipping={true}>
          {(provided: DroppableProvided, snapshot: IDroppableStateSnapshot) => {
            return (
              <div
                ref={provided.innerRef}
                {...provided.droppableProps}
                className="droppable-container"
                style={{
                  paddingBottom: snapshot.isUsingPlaceholder ? 50 : 0
                }}
              >
                {element.children}
              </div>
            );
          }}
        </Droppable>
      </div>
    );
  }

  protected TrGroupComponent(element: any) {
    let isDnDDisabled = false;
    if (!element.rowInfo) {
      isDnDDisabled = true;
    }
    if (element.rowInfo && element.rowInfo.original.fixed === true) {
      isDnDDisabled = true;
    }
    return (
      <Draggable
        key={element.rowInfo ? element.rowInfo.original.id : 0}
        draggableId={element.rowInfo ? element.rowInfo.original.id : 0}
        index={element.rowInfo ? this.state.tableData.findIndex(item => item.id === element.rowInfo.original.id) : -1}
        isDragDisabled={isDnDDisabled}
      >
        {(providedDraggable: DraggableProvided, snapshotDraggable: DraggableStateSnapshot) => (
          <div
            ref={providedDraggable.innerRef}
            {...providedDraggable.draggableProps}
            {...providedDraggable.dragHandleProps}
            style={this.GetItemStyle(providedDraggable.draggableProps.style, snapshotDraggable.isDragging)}
          >
            <div className="rt-tr-group">{element.children}</div>
          </div>
        )}
      </Draggable>
    );
  }

  protected GetItemStyle(draggableStyle: any, isDragging: boolean) {
    return {
      userSelect: 'none',
      display: 'flex',
      background: isDragging ? 'lightgreen' : null,
      ...draggableStyle
    };
  }

  protected SelectAllComponent(element: any) {
    return (
      <input type="checkbox" checked={this.state.tableData.every(item => item.hidden === false)} onChange={value => {
        this.ToggleAllSelection();
      }}></input>
    );
  }
}
