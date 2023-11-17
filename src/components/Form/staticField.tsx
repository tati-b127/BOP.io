import React, { Component } from "react";
import InputSuffix from "../inputSuffix";

interface IStaticFieldProps{
    label: string;
    type: string;
    unit: string;
    disabled: boolean;
    onEdit: Function;
    value: number|string;
    fieldName: string;
    flex?: string
}

interface IStaticFieldState{
    isValid: boolean;
    value: string | number;
    validationMessage: string;
}
export default class StaticField extends Component<IStaticFieldProps, IStaticFieldState>{
	constructor(props:IStaticFieldProps){
		super(props);
		this.state = {
            isValid: true,
            value: '',
            validationMessage: ''
		};
    }

    componentDidMount(){
        this.setState({
            value: this.props.value
        });
    }

    componentDidUpdate(prevProps: IStaticFieldProps){
        if(prevProps.value!==this.props.value){
            this.setState({
                value: this.props.value
            });
            this.props.onEdit(this.props.value, this.props.fieldName, true);
        }
    }
    
    onEditAttribute = (value: string) => {
        this.setState({
            value
        });
        this.props.onEdit(value, this.props.fieldName, false);
    }

	render(){
		return(
			<div className="field-container">
				<div
					className="field-label"
					title={this.props.label}
				>
					{this.props.label}
				</div>
				<div className={`field-value ${this.props.flex}`}>
					<InputSuffix
						onEdit={this.onEditAttribute}
						type={this.props.type}
						value={this.state.value}
						suffix={this.props.unit}
						disabled={this.props.disabled}
						addClassName={!this.state.isValid ? "error" : ""}
					/>
					{!this.state.isValid ? (
						<div className="validation error">
							{this.state.validationMessage}
						</div>
					) : null}
				</div>
			</div>
		)
	}
}