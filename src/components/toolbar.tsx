import * as React from "react";
import { IToolbarComponentProps, IToolbarComponentState, IToolbarMenuItem } from '@raos/widget-utils/lib/sharedStore/interfaces';
import '@raos/widget-utils/lib/sharedStyles/toolbarComponent.css';
import '../styles/toolbar.css'

const chevronArrowIcon = require('@raos/widget-utils/lib/sharedImages/chevronArrowIcon.svg');
const upIcon = require('@raos/widget-utils/lib/sharedImages/upArrow.svg');

import {Loader} from '@raos/widget-utils/lib/sharedComponents/utilComponents';

interface ICustomToolbarComponent extends IToolbarMenuItem{
	element: any;
	disabled: boolean;
}

export class Toolbar extends React.Component<IToolbarComponentProps,
  IToolbarComponentState> {
  protected menuContainerRef = React.createRef<HTMLDivElement>();

	constructor(props: IToolbarComponentProps) {
		super(props);
		this.HandleClickOutside = this.HandleClickOutside.bind(this);
		this.HandleResize = this.HandleResize.bind(this);
		this.HandleMouseLeave = this.HandleMouseLeave.bind(this);

		this.state = {
			showToolbar: false,
			toolbarTabs: props.toolbarTabs ? props.toolbarTabs : [],
			showTooltip: false,
			tooltipX: 0,
			tooltipY: 0,
			tooltipMsg: '',
			currentTabIndex: 0,
			subitemMenu: null,
			subitemsX: 0,
			subitemsY: 0
		};
	}

	componentDidMount() {
		document.addEventListener('mousedown', this.HandleClickOutside);
		window.addEventListener('resize', this.HandleResize);
	}

	componentDidUpdate() {}

	componentWillReceiveProps(nextprops: IToolbarComponentProps) {
		this.setState({ toolbarTabs: nextprops.toolbarTabs });
		if (this.state.subitemMenu) {
			nextprops.toolbarTabs.forEach(tab => {
				tab.groups.forEach(group => {
					const item = group.items.find(
						item => item.id === this.state.subitemMenu.id
					);
					if (item) {
						this.setState({
							subitemMenu: { id: item.id, items: item.subItems }
						});
					}
				});
			});
		}
	}

	componentWillUnmount() {
		document.removeEventListener('mousedown', this.HandleClickOutside);
		window.removeEventListener('resize', this.HandleResize);
	}

	render() {
		let commands: React.ReactElement[] = [];
		let tabs: React.ReactElement[] = [];
		let subitems: React.ReactElement[] = [];

		if (this.state.subitemMenu) {
			this.state.subitemMenu.items.forEach(item => {
				subitems.push(
					<div
						className={`subitem-container${item.isLoading ? ' disabled' : ''}`}
						onClick={e => (item.isLoading ? null : item.onClickFunction(e))}
					>
						{item.isLoading ? Loader(22, 22) : null}
						{item.image && (
							<img className="subitem-icon" src={item.image}></img>
						)}
						<div
							className={`subitem-label${
								item.isHover ? ' subitem-label--hover' : ''
							}`}
						>
							{item.title}
						</div>
					</div>
				);
			});
		}

		const showTabs = this.state.toolbarTabs.length > 1;

		if (showTabs) {
			tabs = this.state.toolbarTabs.map((tab, index) => {
				return (
					<div
						className={`tab-item${
							index === this.state.currentTabIndex ? ' active' : ''
						}`}
						onClick={e =>
							index !== this.state.currentTabIndex && this.SwitchTab(index)
						}
					>
						{tab.title}
					</div>
				);
			});
		}

		const currentTab = this.state.toolbarTabs[this.state.currentTabIndex];
		if (currentTab) {
			currentTab.groups.forEach((group, index) => {
				let groupCommands = group.items.map((item: ICustomToolbarComponent) => {
					if(item.element){
						return (
							<div className={`command-item${item.isLoading || item.disabled ? ' disabled' : ''}`}>
								{item.element}
							</div>
						)
					}else{
						return (
							<div
								className={`command-item${
									item.isLoading || item.disabled ? ' disabled' : ''
								}`}
								onMouseEnter={e => {
									const boundingClientRect = e.currentTarget.getBoundingClientRect();
									this.ShowTooltip(
										boundingClientRect.left - 5,
										boundingClientRect.top - boundingClientRect.height,
										item.title
									);
								}}
								onMouseLeave={e => this.HideTooltip()}
							>
								{item.isLoading ? Loader(22, 22) : null}
								<img
									src={item.image}
									onClick={() =>
										item.isLoading || item.disabled
											? null
											: item.onClickFunction()
									}
									className="command-icon"
								/>
								{item.subItems &&
								item.subItems.length > 0 &&
								!item.isLoading &&
								!item.disabled ? (
									<div
										className={`command-expand-container${
											this.state.subitemMenu &&
											item.id === this.state.subitemMenu.id
												? ' selected'
												: ''
										}`}
										onClick={e => {
											const boundingClientRect = e.currentTarget.getBoundingClientRect();
											this.setState({
												subitemMenu: { id: item.id, items: item.subItems },
												subitemsX: boundingClientRect.left - 36,
												subitemsY:
													boundingClientRect.bottom - boundingClientRect.top
											});
										}}
									>
										<img className="command-expand-icon" src={upIcon} />
									</div>
								) : null}
							</div>
						);
					}
				});
				if (currentTab.groups[index + 1]) {
					groupCommands.push(<div className="command-item-separator" />);
				}
				commands = [...commands, ...groupCommands];
			});
		}

		return [
			<div
				className="toolbar-container"
				toolbar-opened={this.state.showToolbar ? 'true' : 'false'}
			>
				<span
					className="chevron-button"
					onClick={() => this.ChangeToolbarState()}
				>
					<img
						className={`chevron-icon${this.state.showToolbar ? ' opened' : ''}`}
						src={chevronArrowIcon}
					/>
				</span>
				<div className="menu-container">
					{showTabs ? (
						<div className="tabs-placeholder">
							<div className="tabs-container">{tabs}</div>
						</div>
					) : null}
					<div className="workbench-container">
						<div className="command-container">{commands}</div>
					</div>
				</div>
			</div>,
			this.state.showTooltip ? (
				<div
					className="command-tooltip"
					style={{ left: this.state.tooltipX, top: this.state.tooltipY }}
				>
					<div className="tooltip-text">{this.state.tooltipMsg}</div>
				</div>
			) : null,
			this.state.subitemMenu ? (
				<div
					className="subitems-menu-container"
					ref={this.menuContainerRef}
					style={{ left: this.state.subitemsX, bottom: this.state.subitemsY }}
					onMouseLeave={e => this.HandleMouseLeave()}
					onMouseEnter={e => clearTimeout(this.timeoutOnMouseLeave)}
				>
					{subitems}
				</div>
			) : null
		];
	}

	protected ChangeToolbarState() {
		const showToolbar = this.state.showToolbar;
		this.setState({
			showToolbar: !showToolbar,
			showTooltip: false,
			tooltipX: 0,
			tooltipY: 0,
			tooltipMsg: ''
		});
	}

	protected SwitchTab(index: number) {
		this.setState({
			currentTabIndex: index
		});
	}

	protected ShowTooltip(x: number, y: number, message: string) {
		this.setState({
			showTooltip: true,
			tooltipX: x,
			tooltipY: y,
			tooltipMsg: message
		});
	}

	protected HideTooltip() {
		this.setState({
			showTooltip: false,
			tooltipX: 0,
			tooltipY: 0,
			tooltipMsg: ''
		});
	}

	protected HandleClickOutside(event: any) {
		const target = event.target;
		if (
			this.menuContainerRef &&
			this.menuContainerRef.current &&
			this.menuContainerRef.current.contains(target)
		) {
			return;
		}
		this.setState({ subitemMenu: null, subitemsX: 0, subitemsY: 0 });
	}

	protected timeoutOnMouseLeave: NodeJS.Timeout = null;

	protected HandleMouseLeave() {
		if (this.timeoutOnMouseLeave) {
			clearTimeout(this.timeoutOnMouseLeave);
		}

		this.timeoutOnMouseLeave = setTimeout(() => {
			if (this.state.subitemMenu) {
				this.setState({ subitemMenu: null, subitemsX: 0, subitemsY: 0 });
			}
		}, 500);
	}

	protected HandleResize(event: any) {
		if (this.state.subitemMenu) {
			this.setState({ subitemMenu: null, subitemsX: 0, subitemsY: 0 });
		}
	}
}

export default Toolbar;
