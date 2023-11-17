import React, { Component } from 'react';

import InputSuffix from '../inputSuffix';
import { convert, units } from '../dimensionUnits';
import { Attribute, Units, Validation } from '../../store/interfaces';

interface IAttributeFieldProps {
  hided: boolean;
  disabled: boolean;
  attribute: Attribute;
  customUnit?: Units;
  onEdit?: (
    attributeName: string,
    value: string,
    unConvertValue?: string,
    rawText?: string,
    isCalculated?: boolean
  ) => void;
  calculationMethod?: string; // LENGTH, AREA or MASS calculation methods
  calculationFunction?: Function; // Function that is applied to displayed input value
  calculationArgs?: Attribute[]; // arguments that are used in calculation, validation and edit functions
  editFunction?: Function; // Function which is applied to user entered value
  forceUpdate?: boolean;
  customLabel?: string;
  validationFunction?: Function; // Function that is used to validate user entered values
}

interface IAttributeFieldState {
  value: string | number;
  unitsSuffix: string;
  isValid: boolean;
  validationMessage: string;
}

export default class AttributeField extends Component<IAttributeFieldProps, IAttributeFieldState> {
  constructor(props: IAttributeFieldProps) {
    super(props);

    this.state = {
      value: '',
      unitsSuffix: '',
      isValid: true,
      validationMessage: ''
    };
  }

  componentDidMount() {
    if (this.props.attribute) {
      this.formatAttributeValue(this.props.attribute.info.inputvalue);
    }
  }

  formatAttributeValue = (inputvalue: string): void => {
    let value = '';
    let unitsSuffix = '';
    if (this.props.customUnit && this.props.attribute.info.inputunit !== '' && !isNaN(parseFloat(inputvalue))) {
      value = String(convert(parseFloat(inputvalue), this.props.attribute.info.inputunit, this.props.customUnit));
    } else {
      value = inputvalue;
    }

    if (this.props.customUnit) {
      if (units[this.props.customUnit]) {
        unitsSuffix = units[this.props.customUnit].label;
      }
    } else if (units[this.props.attribute.info.inputunit] && this.props.attribute.info.inputunit !== '') {
      unitsSuffix = units[this.props.attribute.info.inputunit].label;
    }

    if (this.props.validationFunction) {
      const validation: Validation = this.props.validationFunction(
        ...this.props.calculationArgs,
        this.props.calculationMethod
      );

      this.setState({
        value,
        unitsSuffix,
        validationMessage: validation.message,
        isValid: validation.statusOK
      });
    } else {
      this.setState({
        value,
        unitsSuffix
      });
    }
  };

  componentDidUpdate(prevProps: IAttributeFieldProps) {
    let isCalculated = false;
    if (
      this.props.calculationFunction &&
      this.props.calculationArgs &&
      prevProps.calculationArgs &&
      prevProps.calculationArgs !== this.props.calculationArgs &&
      /* prevProps.calculationArgs.every(
				(attribute: Attribute) => attribute !== undefined
			) && */
      this.props.attribute
    ) {
      const calculatedValue = this.props.calculationFunction(
        ...this.props.calculationArgs,
        this.props.calculationMethod
      );
      this.formatAttributeValue(calculatedValue);

      isCalculated = true;
      if (this.props.attribute.info.inputvalue !== calculatedValue) {
        this.props.onEdit(
          this.props.attribute.name,
          calculatedValue,
          this.props.attribute.info.inputunit,
          calculatedValue,
          true
        );
      }
    }

    if (prevProps.attribute !== this.props.attribute && this.props.attribute && !isCalculated) {
      if (!prevProps.attribute) {
        this.formatAttributeValue(this.props.attribute.info.inputvalue);
      } else if (prevProps.attribute.info.inputvalue !== this.props.attribute.info.inputvalue) {
        this.formatAttributeValue(this.props.attribute.info.inputvalue);
      } else if (prevProps.attribute.value !== this.props.attribute.value) {
        // TODO: check if fails here
        this.formatAttributeValue(this.props.attribute.info.inputvalue);
        /* this.setState({
						value: this.props.attribute.value
					}); */
      }
    }

    if (prevProps.forceUpdate !== this.props.forceUpdate && this.props.attribute) {
      this.formatAttributeValue(this.props.attribute.info.inputvalue);
    }
  }

  onEditAttribute = (value: string) => {
    let convertValue = value;
    if (this.props.customUnit && this.props.attribute.info.inputunit !== '' && !isNaN(parseFloat(value))) {
      convertValue = String(convert(parseFloat(value), this.props.customUnit, this.props.attribute.info.inputunit));
    }

    if (this.props.editFunction) {
      convertValue = this.props.editFunction(value, ...this.props.calculationArgs, this.props.calculationMethod);
    }

    this.props.onEdit(this.props.attribute.name, convertValue, this.props.attribute.info.inputunit, value, false);

    this.setState({
      value
    });
  };

  render() {
    if (this.props.attribute) {
      return (
        !this.props.hided && (
          <div className="field-container">
            <div className="field-label" title={this.props.attribute.displayName}>
              {this.props.customLabel ? this.props.customLabel : this.props.attribute.displayName}
            </div>
            <div className="field-value">
              <InputSuffix
                onEdit={this.onEditAttribute}
                type={this.props.attribute.info.type}
                value={this.state.value}
                suffix={this.state.unitsSuffix}
                disabled={this.props.disabled}
                addClassName={!this.state.isValid ? 'error' : ''}
              />
              {!this.state.isValid ? <div className="validation error">{this.state.validationMessage}</div> : null}
            </div>
          </div>
        )
      );
    }
    return null;
  }
}
