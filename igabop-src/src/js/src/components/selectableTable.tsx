import React, {Component} from 'react';
import ReactTable from 'react-table';
import "react-table/react-table.css";
import selectTableHOC from "react-table/lib/hoc/selectTable";

interface ISelectableTableProps{
	data: any[],
	columns: any[],
	onSelectRows?: any,
	selectedRows?: any[],
	selectedTab?: string
}

interface ISelectableTableState{
	selectAll: boolean;
	selectedItems: String[];
}

class SelectableTable extends Component<ISelectableTableProps, ISelectableTableState> {
	constructor(props: ISelectableTableProps){
		super(props);
		this.state = {
			selectAll: false,
			selectedItems: props.selectedRows ? props.selectedRows : []
		}
	}

	componentDidUpdate(prevProps: ISelectableTableProps){
		if(prevProps.data!==this.props.data || prevProps.columns!==this.props.columns){
			this.deselectAll();
		}
		if(prevProps.selectedRows !== this.props.selectedRows){
			if(this.props.selectedRows.length === 0){
				this.deselectAll();
			}
			this.setState({
				selectedItems: this.props.selectedRows
			})
		}
	}


	public toggleAllSelection(){
		let selectedItems = this.state.selectedItems;
		if(this.state.selectAll){
			selectedItems = [];
		}else{
			selectedItems = this.props.data.map((item: any) => {
				let idKey = this.props.selectedTab === 'consumed' ? 'id' : 'physicalid';
				return item[idKey];
			});
		}

		this.setState({
			selectAll: !this.state.selectAll,
			selectedItems
		}, () => {
			this.props.onSelectRows(selectedItems);
		});
	}

	protected selectAllComponent(element: any){
		return (
			<input
				className={''}
				type={element.selectType}
				checked={this.state.selectAll}
				onClick={e => element.onClick()}
			/>
		);
	}

	render() {
		const SelectTable = selectTableHOC(ReactTable);
		return (
			<SelectTable
				data={this.props.data}
				columns={this.props.columns}
				showPagination={false}
				pageSize={this.props.data.length}
				className="-striped -highlight"
				keyField={this.props.selectedTab === 'consumed' ? 'id' : 'physicalid'}
				selectAll={this.state.selectAll}
				SelectInputComponent={(element: any) => this.selectComponent(element)}
				SelectAllInputComponent={(element: any) => this.selectAllComponent(element)}
				toggleAll={() => this.toggleAllSelection()}
				toggleSelection={(id: string) => this.toggleSelection(id)}
			/>
		);
	}

	public deselectAll(){
		this.setState({
			selectAll: false,
			selectedItems: []
		});
	}

	protected toggleSelection(id: string){
		const alreadySelectedId = this.state.selectedItems.some((selectedItemId: string) => selectedItemId==id);
		let selectedItems = this.state.selectedItems;

		if(alreadySelectedId){
			selectedItems = selectedItems.filter((selectedItemId: string) => selectedItemId!=id);
		}else{
			selectedItems.push(id);
		}

		this.setState({
			selectedItems
		}, () => {
			this.checkSelection();
			this.props.onSelectRows(selectedItems);
		});
	}

	protected checkSelection() {
		let selectAll = this.state.selectAll;

		if (this.state.selectedItems.length==this.props.data.length) {
			selectAll = !selectAll;
		}
		this.setState({
			selectAll: selectAll
		});
	}

	protected selectComponent(element: any) {
		const id = this.props.selectedTab === 'consumed' ? element.row.id : element.row.physicalid;
		const selectedItem = this.state.selectedItems.some((selectedItemId: string) => selectedItemId==id);
		return (
			<input
				className={''}
				type={element.selectType}
				checked={selectedItem}
				onClick={e => element.onClick(id)}
			/>
		);
	}
}

export default SelectableTable;
