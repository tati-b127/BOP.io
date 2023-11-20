import React, {Component} from 'react';
import Toolbar from './toolbar';
import Dropdown from './dropdown';
import {IToolbarMenuTab} from "@raos/widget-utils/lib/sharedStore/interfaces";
import {IToolbarMenuItem} from "../store/interfaces";

const expandIcon = require('../images/CATFmtContributingParts_32x32.png');
const upIcon = require('../images/I_ADM_ArrowUp.png');
const downIcon = require('../images/I_ADM_ArrowDown.png');
const setNumOperationIcon = require('../images/I_CXPSetAsStartup.png');
const refreshIcon = require('../images/I_DfuRefreshOn.png');
const deleteIcon = require('../images/I_VCX_Panel_Scenario_RemoveScenario@2x.png');
const promoteIcon = require('../images/I_Promote.png');
const demoteIcon = require('../images/I_Demote.png');
const reserveIcon = require('../images/CATFmtFreeze_32x32.png');
const unreserveIcon = require('../images/CATFmtUnfreeze_32x32.png');
const prepareIcon = require('../images/I_ChangeCS.png');
const customizeColumnsIcon = require('../images/customizeColumnsIcon.svg');
const reportIcon = require('../images/I_Report.png');

interface IBopToolbarProps{
	onAction: Function;
	selectedRows: any;
}

interface IBopToolbarState{

}

class BopToolbar extends Component<IBopToolbarProps, IBopToolbarState> {

	constructor(props: IBopToolbarProps){
		super(props);
		this.handleCreateItem = this.handleCreateItem.bind(this);
		this.handleInsertItem = this.handleInsertItem.bind(this);
		this.handleDocument = this.handleDocument.bind(this);
		this.handleSTO = this.handleSTO.bind(this);
		this.handleAssign = this.handleAssign.bind(this);
		this.handleReport = this.handleReport.bind(this);
	}

	protected handleAssign(){

	}

	protected handleReport(){
		//alert('handleReport');
		this.props.onAction('report', '')
	}

	protected handleCreateItem(objectType: string){
		this.props.onAction('create', objectType)
	}

	protected handleInsertItem(objectType: string){

	}

	protected handleDocument(objectType: string){

	}

	protected handleSTO(objectType: string){

	}

	render() {
		let operationMenuItems: IToolbarMenuItem[] = [
			{
				id: 'expand',
				title: 'Раскрыть структуру',
				onClickFunction: () => this.handleExpand(),
				image: expandIcon
			},
			{
				id: 'up',
				title: 'Переместить вверх',
				onClickFunction: () => this.handleUp(),
				image: upIcon
			},
			{
				id: 'down',
				title: 'Переместить вниз',
				onClickFunction: () => this.handleDown(),
				image: downIcon
			},
			{
				id: 'setNumOperation',
				title: 'Установить номера Операций/Переходов',
				onClickFunction: () => this.handleSetNumOperation(),
				image: setNumOperationIcon
			},
			{
				id: 'refresh',
				title: 'Обновить',
				onClickFunction: () => this.handleRefresh(),
				image: refreshIcon
			},
			{
				id: 'delete',
				title: 'Удалить',
				onClickFunction: () => this.handleDelete(),
				image: deleteIcon
			},
			{
				id: 'reserve',
				title: 'Заблокировать',
				onClickFunction: () => this.handleReserve(),
				image: reserveIcon
			},
			{
				id: 'unreserve',
				title: 'Разблокировать',
				onClickFunction: () => this.handleUnreserve(),
				image: unreserveIcon
			},
			{
				id: 'promote',
				title: 'Повысить состояние',
				onClickFunction: () => this.handlePromote(),
				image: promoteIcon
			},
			{
				id: 'demote',
				title: 'Понизить состояние',
				onClickFunction: () => this.handleDemote(),
				image: demoteIcon
			},
			{
				id: 'prepare',
				title: 'Подготовить к согласованию',
				onClickFunction: () => this.handlePrepare(),
				image: prepareIcon
			},
			{
				id: 'customize',
				title: 'Настройка аттрибутов',
				onClickFunction: () => this.handleCustomize(),
				image: customizeColumnsIcon
			},
		];

		let createMenuItems = [
			{
				id: 'create',
				title: 'Create',
				element: <Dropdown items={[
					{id: 'item1', name: 'Предприятие', enabled: this.isAnyRowSelected()},
					{id: 'item2', name: 'Цех', enabled: this.isAnyRowSelected()},
					{id: 'item3', name: 'Участок', enabled: this.isAnyRowSelected()},
					{id: 'item4', name: 'Операция', enabled: this.isAnyRowSelected()},
					{id: 'item5', name: 'Переход', enabled: this.isAnyRowSelected()}
				]} label="Создать" onSelect={this.handleCreateItem}/>
			},
			{
				id: 'insert',
				title: 'Insert',
				element: <Dropdown items={[
					{id: 'item1', name: 'Цех', enabled: this.isAnyRowSelected()},
					{id: 'item2', name: 'Участок', enabled: this.isAnyRowSelected()},
					{id: 'item3', name: 'Операция', enabled: this.isAnyRowSelected()},
					{id: 'item4', name: 'Переход', enabled: this.isAnyRowSelected()}
				]} label="Вставить сущ" onSelect={this.handleInsertItem}/>
			},
			{
				id: 'document',
				title: 'Document',
				element: <Dropdown items={[
					{id: 'item1', name: 'Создать новый', enabled: this.isAnyRowSelected()},
					{id: 'item2', name: 'Прикрепить существующий', enabled: this.isAnyRowSelected()},
				]} label="Документ" onSelect={this.handleDocument}/>
			},
			{
				id: 'sto',
				title: 'STO',
				element: <Dropdown items={[
					{id: 'item1', name: 'Оборудование', enabled: this.isAnyRowSelected()},
					{id: 'item2', name: 'Профессия', enabled: this.isAnyRowSelected()},
					{id: 'item3', name: 'Приспособление', enabled: this.isAnyRowSelected()},
					{id: 'item4', name: 'Слесарно-монтажный инстр', enabled: this.isAnyRowSelected()},
					{id: 'item5', name: 'Режущ инстр', enabled: this.isAnyRowSelected()},
					{id: 'item6', name: 'Вспом инстр', enabled: this.isAnyRowSelected()},
					{id: 'item7', name: 'Средства измер', enabled: this.isAnyRowSelected()},
					{id: 'item8', name: 'Средства защ', enabled: this.isAnyRowSelected()},
					{id: 'item9', name: 'Проектир оснас', enabled: this.isAnyRowSelected()},
				]} label="СТО" onSelect={this.handleSTO}/>
			},
		];

		const otherMenuTabs: IToolbarMenuItem[] = [
			/*{
				id: 'assign',
				title: 'Assign',
				element: <button onClick={this.handleAssign}>Назначить компл и матер</button>
			},*/
			{
				id: 'report',
				title: 'Отчет',
				onClickFunction: () => this.handleReport(),
				image: reportIcon
			},
		]



		let toolbarTabs: IToolbarMenuTab[] = [
			{
				title: 'Operations',
				groups: [{ items: operationMenuItems }]
			},
			/*{
				title: 'Create',
				groups: [{ items: createMenuItems }]
			},*/
			{
				title: 'Other',
				groups: [{ items: otherMenuTabs }]
			}
		];

		return (
			<Toolbar toolbarTabs={toolbarTabs} />
		);
	}

	protected handleDelete(){
		this.props.onAction('delete', '')
	}

	protected handleRefresh(){
		this.props.onAction('refresh', '')
	}

	protected handleReserve(){
		this.props.onAction('reserve', '')
	}

	protected handleUnreserve() {
		this.props.onAction('unreserve', '')
	}

	protected handlePromote(){
		this.props.onAction('promote', '')
	}

	protected isAnyRowSelected(){
		return this.props.selectedRows.length>0;
	}

	protected handleSetNumOperation(){
		this.props.onAction('setNumOperation', '')
	}

	protected handleUp(){
		this.props.onAction('up', '')
	}

	protected handleDown(){
		this.props.onAction('down', '')
	}

	protected handleExpand(){
		this.props.onAction('expand', '')
	}

	protected handleDemote(){
		this.props.onAction('demote', '')
	}

	protected handlePrepare(){
		this.props.onAction('prepare', '')
	}

	protected handleCustomize(){
		this.props.onAction('customize', '')
	}
}

export default BopToolbar;