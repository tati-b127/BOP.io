import React, { Component } from 'react';
import '../styles/dropdown.css';
import SVG from "react-inlinesvg";

const arrowIcon = require('../images/chevron-circle-up-solid.svg');

interface IDropdownProps{
	label: string;
	items: any[];
	onSelect?: Function
}

interface IDropdownState{
	opened: boolean
}

class Dropdown extends Component<IDropdownProps, IDropdownState> {
	protected dropDownRef: any;
	constructor(props: IDropdownProps){
		super(props);
		this.state = {
			opened: false
		}
		this.dropDownRef = React.createRef();
	}

	componentDidMount(){
		window.addEventListener('mousedown', (e: any) => {
			if(this.dropDownRef.current && !this.dropDownRef.current.contains(e.target)){
				this.setState({
					opened: false
				});
			}
		})
	}

	protected handleItem(item: any){
		this.setState({
			opened: false
		});

		this.props.onSelect(item.id);
	}

	protected handleOpener(){
		this.setState({
			opened: !this.state.opened
		});
	}

	render() {
		return (
			<div className="dropdown" ref={this.dropDownRef}>
				<div className="dropdown-container" title={this.props.label}>
					<div className="dropdown-label">{this.props.label}</div>
					<div className="dropdown-open-button" onClick={() => this.handleOpener()}>
						<img className="dropdown-open-icon" src={arrowIcon}></img>
					</div>
				</div>
				<div className={`dropdown-list-container${this.state.opened?' opened':' closed'}`}>
					<div className="dropdown-list">
						{this.props.items.map((item: any) => {
							let enabled = true; //make item default enabled
							
							// check if item is not enabled by props
							if(item.enabled==false){ 
								enabled = false;
							}

							return (
								<div onClick={() => this.handleItem(item.id)} className={`dropdown-list-item${enabled?'':' disabled'}`} key={item.id}>
									{item.name}
								</div>
							)
						})}
					</div>
				</div>
			</div>
		);
	}
}

export default Dropdown;
