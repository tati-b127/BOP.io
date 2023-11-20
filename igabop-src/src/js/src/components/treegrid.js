import React from 'react';

import { CustomReactComponent } from './CustomReactComponent/CustomReactComponent';

import AutoResizer from 'react-base-table/lib/AutoResizer';
import BaseTable, { unflatten } from 'react-base-table';
import 'react-base-table/styles.css';
import '../styles/treegrid.scss';
import '../styles/drag.css';
import { faClipboardList, faDotCircle, faFile, faIndustry, faSearch } from '@fortawesome/free-solid-svg-icons';
import { sortableContainer, sortableElement, sortableHandle } from 'react-sortable-hoc';

import { getUserSettings, saveUserSettings } from '../utils/widgetUtils';
import { dragData } from '../utils/utils';

import {
  ATTRIBUTE_KIT_PREP_TIME,
  ATTRIBUTE_KIT_TPCS,
  instruction_types,
  mbom_types,
  op_types,
  product_types,
  sys_types
} from './consts';
import { RowLoader } from './loader';
import InlineEditor from './inlineEditor';
import { bopColumns } from './formats/columns';

// const { sortableContainer, sortableElement, sortableHandle } = ReactSortableHoc
const DraggableContainer = sortableContainer(({ children }) => children);
const DraggableElement = sortableElement(({ children }) => children);
const DraggableHandle = sortableHandle(({ children }) => children);

class Treegrid extends CustomReactComponent {
  constructor(props) {
    super(props);
    this.cellRenderer = this.cellRenderer.bind(this);
    this.stringRenderer = this.stringRenderer.bind(this);
    this.dateRenderer = this.dateRenderer.bind(this);
    this.titleRenderer = this.titleRenderer.bind(this);
    this.attachmentRenderer = this.attachmentRenderer.bind(this);
    this.attributeRenderer = this.attributeRenderer.bind(this);
    this.routesRenderer = this.routesRenderer.bind(this);
    this.routesStatusRenderer = this.routesStatusRenderer.bind(this);
    this.routesOwnerRenderer = this.routesOwnerRenderer.bind(this);

    this.rowClassName = this.rowClassName.bind(this);
    this.onRowDragOver = this.onRowDragOver.bind(this);
    this.onRowDragLeave = this.onRowDragLeave.bind(this);
    this.onRowDrop = this.onRowDrop.bind(this);
    this.onRowClick = this.onRowClick.bind(this);
    this.onChangeCell = this.onChangeCell.bind(this);
    this.onContextMenu = this.onContextMenu.bind(this);
    this.onSaveCellContent = this.onSaveCellContent.bind(this);
    this.renderers = {
      string: this.stringRenderer,
      date: this.dateRenderer,
      title: this.titleRenderer,
      loading: this.loadingRenderer,
      attachment: this.attachmentRenderer,
      attribute: this.attributeRenderer,
      routes: this.routesRenderer,
      routesStatus: this.routesStatusRenderer,
      routesOwner: this.routesOwnerRenderer
    };

    this.components = {
      TableCell: this.cellRenderer
    };

    let columns = bopColumns;

    let tableSettings = getUserSettings(props.componentId);

    if (tableSettings && JSON.parse(tableSettings).columns.length == columns.length) {
      const settings = JSON.parse(tableSettings);
      const customizedColumns = settings.columns
        .filter(column => !column.hidden && columns.some(col => col.key === column.id))
        .map(column => {
          const tableColumn = columns.find(col => col.key === column.id);
          if (column && column.width) {
            tableColumn.width = column.width;
          }
          return tableColumn;
        });
      columns = customizedColumns;
      tableSettings = settings;
    } else {
      const columnsSettings = columns.map(column => {
        return {
          id: column.key,
          hidden: column.hidden,
          width: column.width
        };
      });
      tableSettings = {};
      tableSettings.columns = columnsSettings;
      const rawSettings = JSON.stringify({ columns: columnsSettings });
      saveUserSettings(props.componentId, rawSettings);
    }

    columns.push({
      key: 'emptyId',
      dataKey: '',
      title: '',
      resizable: false,
      frozen: false,
      hidden: false
    }); // add empty column to fix behavior at the right border of the bop table

    this.state = {
      isLoading: false,
      rowDragOverId: null,
      tableData: [],
      linearizedTree: [],
      expandedRowKeys: props.expandedRowKeys,
      columns,
      selectedRows: [],
      columnSettings: tableSettings.columns
    };

    this.rowEventHandlers = {
      onClick: this.onRowClick,
      onDragEnter: this.onRowDragOver,
      onDragLeave: this.onRowDragLeave,
      onDrop: this.onRowDrop,
      onContextMenu: this.onContextMenu
    };

    this.tableRef = null;
    this.rowRefs = {};
    this.currentSelection = null;
    this.previousSelection = null;
  }

  linearizeTree(node, lineicArray, expandedRowKeys) {
    lineicArray.push({
      id: node.id,
      node
    });
    if (node.children) {
      if (expandedRowKeys.includes(node.id)) {
        node.children.forEach(child => {
          this.linearizeTree(child, lineicArray, expandedRowKeys);
        });
      }
    }
    return lineicArray;
  }

  onRowClick(data) {
    const { ctrlKey } = data.event;
    const { shiftKey } = data.event;
    const { rowKey } = data;
    this.previousSelection = this.currentSelection;
    this.currentSelection = rowKey;
    let { selectedRows } = this.state;
    if (this.props.multiselect && ctrlKey) {
      if (selectedRows.some(selection => selection.rowKey === rowKey))
        selectedRows = selectedRows.filter(selection => selection.rowKey !== rowKey);
      else selectedRows.push(data);
    } else if (this.props.multiselect && shiftKey) {
      if (selectedRows.some(selection => selection.rowKey === rowKey)) {
        selectedRows = selectedRows.filter(selection => selection.rowKey !== rowKey);
      } else {
        let prevIndex = this.state.linearizedTree.map(item => item.id).indexOf(this.previousSelection);
        let currIndex = this.state.linearizedTree.map(item => item.id).indexOf(this.currentSelection);
        if (prevIndex > -1 && currIndex > -1) {
          if (prevIndex > currIndex) {
            const tempValue = currIndex;
            currIndex = prevIndex;
            prevIndex = tempValue;
          }
          for (let index = prevIndex + 1; index < currIndex; index++) {
            selectedRows.push({
              rowIndex: index,
              rowKey: this.state.linearizedTree[index].id,
              rowData: this.state.linearizedTree[index].node
            });
          }
        }
        selectedRows.push(data);
      }
    } else {
      selectedRows = [data];
    }
    if (
      this.state.selectedRows.length === 0 ||
      this.state.selectedRows.some(selection => selection.rowKey !== rowKey)
    ) {
      this.setState({ selectedRows });
    }
    this.props.onSelect(selectedRows);
  }

  onRowDragOver(data) {
    if (data.rowKey !== this.state.rowDragOverId) {
      this.setState({
        rowDragOverId: data.rowKey
      });
    }
  }

  onRowDragLeave(data) {
    setTimeout(() => {
      this.setState({
        rowDragOverId: null
      });
    }, 5000);
  }

  onRowDrop(e) {
    console.log(e);
  }

  onContextMenu(row) {
    this.props.onContextMenu(row);
  }

  rowClassName(rowProps) {
    const rowClasses = [];
    // changes row class if it is selected
    if (this.state.selectedRows.some(selection => selection.rowKey === rowProps.rowData.key)) {
      rowClasses.push('tree-selected-row');
    }
    if (this.state.rowDragOverId === rowProps.rowData.key) {
      rowClasses.push('tree-row-dragging');
    }
    if (rowProps.rowData && instruction_types.includes(rowProps.rowData.type)) {
      // rowClasses.push('row-instruction');
    } else {
      // rowClasses.push('row-border-top');
    }

    return rowClasses.join(' ');
  }

  rowRenderer({ physicalid, key, index, children, ...rest }) {
    return (
      <DraggableElement key={key} index={index}>
        <div {...rest}>
          <DraggableHandle>
            <div className="drag-handler" />
          </DraggableHandle>
          {children}
        </div>
      </DraggableElement>
    );
  }

  cellRenderer(cellProps) {
    let format = cellProps.column.format || 'string';
    if (this.props.loadingRows.some(loadingRow => cellProps.rowData.id === loadingRow)) {
      format = 'loading';
    }

    const renderer = this.renderers[format] || this.renderers.string;
    return renderer(cellProps);
  }

  stringRenderer(cell) {
    return (
      <div className={cell.className} title={cell.cellData}>
        {cell.cellData}
      </div>
    );
  }

  dateRenderer(cell) {
    let formattedDate = '';
    if (cell.cellData) {
      const date = new Date(cell.cellData);
      formattedDate = date.toLocaleDateString('ru-RU');

      let hour = date.getHours();
      let min = date.getMinutes();
      let sec = date.getSeconds();
      hour = hour < 10 ? `0${hour}` : hour;
      min = min < 10 ? `0${min}` : min;
      sec = sec < 10 ? `0${sec}` : sec;

      formattedDate = `${formattedDate} ${hour}:${min}:${sec}`;
    }

    return (
      <div className={cell.className} title={formattedDate}>
        {formattedDate}
      </div>
    );
  }

  attributeRenderer(cell) {
    const { type } = cell.rowData;
    let data = '';
    if (cell.cellData) {
      let val = parseFloat(cell.cellData) / 60;
      val = Math.round(val * 10000 + Number.EPSILON) / 10000;
      data = val.toString();
    }

    return (
      <div className="cell">
        {op_types.includes(type) ? (
          <InlineEditor
            useControls={false}
            saveTimeout={1000}
            onChange={content => this.onChangeCell(cell, content)}
            content={data}
            forceUpdate={this.state.forceUpdate}
            onSaveContent={content => this.onSaveCellContent(cell, content)}
            type="real"
          />
        ) : (
          data
        )}
      </div>
    );
  }

  attachmentRenderer(cell) {
    let docs = '';
    if (cell.cellData)
      docs = cell.cellData.map(doc => {
        let typeIconURL = '';
        if (doc.icon) typeIconURL = require(`../images/icons/${doc.icon}.png`);
        const title = doc.file;
        const pid = doc.physicalid;
        const type = doc.typeId;
        return (
          <div
            className="cell"
            ref={ref => {
              if (ref) {
                this.rowRefs[pid] = { ref };
                const { widgetId } = this.props;
                const data = dragData(widgetId, [
                  {
                    id: pid,
                    objectType: type,
                    objectTitle: title
                  }
                ]);

                this.props.clean(this.rowRefs[pid].ref);
                this.props.draggable(this.rowRefs[pid].ref, {
                  data: JSON.stringify(data)
                });
              }
            }}
          >
            <img className="cell-icon-attachment" src={typeIconURL} />
            &nbsp;
            {title}
          </div>
        );
      });

    return <div>{docs}</div>;
  }

  attachmentRenderer2(cell) {
    let docs = '';
    if (cell.cellData) docs = cell.cellData.split(',').map(file => <div>{file}</div>);

    return <div>{docs}</div>;
  }

  routesRenderer(cell) {
    let routes = '';
    if (cell.cellData) {
      if (cell.cellData.length === 1) routes = cell.cellData[0].name;
      else if (cell.cellData.length > 1) routes = '> 1 route';
    }
    return <div>{routes}</div>;
  }

  routesStatusRenderer(cell) {
    let routes = '';
    if (cell.cellData) {
      if (cell.cellData.length === 1) routes = cell.cellData[0].status;
    }
    return <div>{routes}</div>;
  }

  routesOwnerRenderer(cell) {
    let routes = '';
    if (cell.cellData) {
      if (cell.cellData.length === 1) routes = cell.cellData[0].owner;
    }
    return <div>{routes}</div>;
  }

  titleRenderer(cell) {
    const pid = cell.rowData.physicalid;
    const { path } = cell.rowData;
    const { type } = cell.rowData;
    const { title } = cell.rowData;
    const desc = cell.rowData.description;
    const text = cell.rowData.WItext;
    const { icon } = cell.rowData;
    const number = cell.rowData.NumOperation;

    const first = this.getIconHtml(cell);

    let second = <span className="title">{title}</span>;
    if (instruction_types.includes(type) || op_types.includes(type)) second = <span className="title">{number}</span>;

    let third = '';
    if (instruction_types.includes(type)) third = text;
    else if (op_types.includes(type)) third = title;
    else if (mbom_types.includes(type) || product_types.includes(type)) third = desc;

    return (
      <div
        className="cell"
        ref={ref => {
          if (ref) {
            this.rowRefs[pid] = { ref };
            const { widgetId } = this.props;
            const data = dragData(widgetId, [
              {
                id: pid,
                objectType: type,
                objectTitle: title,
                path
              }
            ]);

            this.props.clean(this.rowRefs[pid].ref);
            this.props.draggable(this.rowRefs[pid].ref, {
              data: JSON.stringify(data)
            });
          }
        }}
      >
        {first}
        <div className="cell-item">{second}</div>
        <div className="cell-editor-container">
          {instruction_types.includes(type) ? (
            <InlineEditor
              useControls={false}
              saveTimeout={1000}
              onChange={content => this.onChangeCell(cell, content)}
              content={third}
              forceUpdate={this.state.forceUpdate}
              onSaveContent={content => this.onSaveCellContent(cell, content)}
            />
          ) : (
            third
          )}
        </div>
      </div>
    );
  }

  getIconHtml(cell) {
    const { type } = cell.rowData;
    const { icon } = cell.rowData;
    const { scoped } = cell.rowData;

    let scopedIconURL = '';
    let typeIconURL = '';
    try {
      if (scoped && scoped.length > 0) {
        const scopedInSession = this.state.tableData.find(node => scoped.includes(node.physicalid));

        if (mbom_types.includes(type)) {
          if (scopedInSession) scopedIconURL = require(`../images/icons/I_PPRProcessHasScope.png`);
          else scopedIconURL = require(`../images/icons/I_PPRProcessHasBrokenScope.png`);
        } else if (sys_types.includes(type)) {
          if (scopedInSession) scopedIconURL = require(`../images/icons/I_PPRSystemHasScope.png`);
          else scopedIconURL = require(`../images/icons/I_PPRSystemHasBrokenScope.png`);
        }
      }

      if (icon && !instruction_types.includes(type)) typeIconURL = require(`../images/icons/${icon}.png`);
    } catch (ex) {
      typeIconURL = require(`../images/icons/I_VPMNavProduct.png`); // default
    }

    return (
      <div className="icon-div">
        <img className="cell-icon" src={typeIconURL} />
        <img className="cell-icon-scoped" src={scopedIconURL} />
      </div>
    );
  }

  loadingRenderer(cell) {
    return cell.cellData ? (
      <div className="cell">
        <RowLoader />
        <div className={cell.className} title={cell.cellData}>
          {cell.cellData}
        </div>
      </div>
    ) : null;
  }

  onChangeCell(cell, content) {
    this.forceUpdate();
  }

  onSaveCellContent(cell, content) {
    if (ATTRIBUTE_KIT_TPCS === cell.column.attribute || ATTRIBUTE_KIT_PREP_TIME === cell.column.attribute)
      content = `${parseFloat(content)} MINUTE`;
    this.props.onSaveCellContent(cell, content);
  }

  expandIconProps({ rowData }) {
    return {
      expanding: !rowData.children || rowData.children.length === 0
    };
  }

  handleTableClick(event) {
    console.log(event);
  }

  addTableListeners() {
    if (this.tableRef) {
      this.props.droppable(this.tableRef.getDOMNode(), {
        drop: data => {
          try {
            const { objectId, objectType, objectTitle, path } = JSON.parse(data).data.items[0];
            const dropZoneRow = this.state.tableData.find(row => row.key === this.state.rowDragOverId);
            this.props.onDrop(objectId, objectType, path, dropZoneRow);
          } catch (e) {
            console.log(e);
          }
        }
      });
    }
  }

  componentDidMount() {
    if (this.props.treeData) {
      let linearizedTree = [];
      if (unflatten(this.props.treeData).length > 0) {
        linearizedTree = this.linearizeTree(unflatten(this.props.treeData)[0], [], this.props.expandedRowKeys);
      }

      this.setState({
        tableData: this.props.treeData,
        linearizedTree
      });
      this.addTableListeners();
    }

    if (this.props.selectedRows) {
      this.setState({ selectedRows: this.props.selectedRows });
    }

    if (this.props.expandedRowKeys) {
      let linearizedTree = [];
      if (unflatten(this.props.treeData).length > 0) {
        linearizedTree = this.linearizeTree(unflatten(this.props.treeData)[0], [], this.props.expandedRowKeys);
      }
      this.setState({
        expandedRowKeys: this.props.expandedRowKeys,
        linearizedTree
      });
    }
  }

  componentDidUpdate(prevProps) {
    if (prevProps.treeData !== this.props.treeData) {
      let linearizedTree = [];
      if (unflatten(this.props.treeData).length > 0) {
        linearizedTree = this.linearizeTree(unflatten(this.props.treeData)[0], [], this.props.expandedRowKeys);
      }

      this.setState({
        tableData: this.props.treeData,
        linearizedTree
      });
      this.addTableListeners();
    }

    if (prevProps.selectedRows !== this.props.selectedRows) {
      this.setState({ selectedRows: this.props.selectedRows });
    }

    if (prevProps.expandedRowKeys !== this.props.expandedRowKeys) {
      let linearizedTree = [];
      if (unflatten(this.props.treeData).length > 0) {
        linearizedTree = this.linearizeTree(unflatten(this.props.treeData)[0], [], this.props.expandedRowKeys);
      }
      this.setState({
        expandedRowKeys: this.props.expandedRowKeys,
        linearizedTree
      });
    }

    if (prevProps.forceUpdate !== this.props.forceUpdate) {
      // very bad decision to force update table.
      this.tableRef.forceUpdateTable();
      this.setState(
        {
          isLoading: true,
          forceUpdate: !this.state.forceUpdate
        },
        () => {
          this.setState({
            isLoading: false
          });
        }
      );
    }
  }

  onRowExpand(row) {
    this.props.onExpand(row);
  }

  rowProps(args) {
    const extraProps = this.props.rowProps ? this.props.rowProps : {};
    return {
      ...extraProps,
      tagName: ({ key, index, children, ...rest }) =>
        this.rowRenderer({ physicalid: args.rowData.physicalid, key, index, children, ...rest }),
      index: args.rowIndex
    };
  }

  handleSortEnd({ oldIndex, newIndex }) {
    const data = [...this.state.tableData];
    const [removed] = data.splice(oldIndex, 1);
    data.splice(newIndex, 0, removed);
    this.setState({
      tableData: data
    });
  }

  onResize(e, onEnd) {
    const columnKey = e.column.key;
    const { width } = e;
    this.state.columnSettings.find(column => column.id == columnKey).width = width;
    if (onEnd) {
      const rawSettings = JSON.stringify({ columns: this.state.columnSettings });
      saveUserSettings(this.props.componentId, rawSettings);
    }
  }

  render() {
    return (
      <AutoResizer>
        {({ width, height }) => (
          <BaseTable
            ref={ref => {
              if (ref) {
                this.tableRef = ref;
              }
            }}
            disabled={this.state.isLoading}
            data={unflatten(this.state.tableData)}
            columns={this.state.columns}
            expandColumnKey="title"
            fixed
            selectable
            width={width}
            height={height - 50}
            onRowExpand={row => this.onRowExpand(row)}
            expandedRowKeys={this.state.expandedRowKeys}
            components={this.components}
            rowEventHandlers={this.rowEventHandlers}
            rowClassName={this.rowClassName}
            onColumnResize={e => this.onResize(e, false)}
            onColumnResizeEnd={e => this.onResize(e, true)}
            rowKey="key"
            estimatedRowHeight={30}
          />
        )}
      </AutoResizer>
    );
  }
}

export const IconTypeDictionary = {
  PPRContext: faClipboardList,
  DELLmiGeneralSystemReference: faDotCircle,
  DELLmiHeaderOperationReference: faSearch,
  Object: faIndustry,
  Document: faFile
};

export default Treegrid;
