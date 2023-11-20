import React, { Component } from 'react';
import { faPencilAlt } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import '../styles/form.scss';
import '../styles/normalization.scss';
import Select from 'react-select';

import { Attribute, Attributes, DBObjectKit, Option, RequestAttributeData } from '../store/interfaces';

import {
  applyBlockRule,
  applyHideRule,
  calculateDetailMass,
  calculateKitStockLength,
  calculateKitStockMass,
  calculateKitStockThickness,
  calculateKitStockWidth,
  calculateMatConsumptionRate,
  calculateMaxDetailCount,
  calculateMaxGroupStockLength,
  calculateUsageCoeffic,
  calculateVUsageCoeff,
  editVUsageCoeff,
  getVContQuantityAttr,
  rawTypeCalculation,
  unitValuesByTypeCalculation,
  validateKitInstallAllowance,
  validateKitMaxGroupStockLength,
  validateKitStockWidth,
  validationKitStockLength
} from './normalizationRules';
import {
  AREA,
  ATTRIBUTE_KIT_ALLOWANCE,
  ATTRIBUTE_KIT_AREA_MASS,
  ATTRIBUTE_KIT_CUT_ALLOWANCE,
  ATTRIBUTE_KIT_INSTALL_ALLOWANCE,
  ATTRIBUTE_KIT_LINEAR_MASS,
  ATTRIBUTE_KIT_MAT_CONSUMPTION_RATE,
  ATTRIBUTE_KIT_MAX_GROUP_STOCK_LENGTH,
  ATTRIBUTE_KIT_MAX_GROUP_STOCK_LENGTH_CALC,
  ATTRIBUTE_KIT_MAX_NUMBER_OF_PARTS,
  ATTRIBUTE_KIT_NUMBER_OF_ADD_PARTS,
  ATTRIBUTE_KIT_NUMBER_OF_PARTS,
  ATTRIBUTE_KIT_PART_LENGTH,
  ATTRIBUTE_KIT_PART_MASS,
  ATTRIBUTE_KIT_PART_WIDTH,
  ATTRIBUTE_KIT_RAW_LENGTH,
  ATTRIBUTE_KIT_RAW_THICKNESS,
  ATTRIBUTE_KIT_RAW_TYPE,
  ATTRIBUTE_KIT_RAW_WEIGHT,
  ATTRIBUTE_KIT_RAW_WIDTH,
  ATTRIBUTE_KIT_RS_NAME,
  ATTRIBUTE_KIT_STOCK_HIGHT_DIAMETER,
  ATTRIBUTE_KIT_STOCK_LENGTH,
  ATTRIBUTE_KIT_STOCK_WEIGHT,
  ATTRIBUTE_KIT_STOCK_WIDTH,
  ATTRIBUTE_KIT_UNIT_VALUE,
  ATTRIBUTE_KIT_USAGE_COEFFIC,
  ATTRIBUTE_KIT_WEIGHT,
  ATTRIBUTE_V_DESCRIPTION,
  ATTRIBUTE_V_NAME,
  ATTRIBUTE_V_USAGE_CONT_COEFF,
  ATTRIBUTE_V_WCG_MASS,
  calculationMethodsOpts,
  DIM_MM,
  LENGTH,
  MASS,
  mfgBarAttributes,
  normalization_continuous_cooling_types,
  normalization_types,
  oem_part_standard_material_types,
  raw_material_types,
  TYPE_KIT_COOL_MIX,
  TYPE_KIT_MFG_CONT,
  TYPE_KIT_MFG_KIT
} from './consts';
import AttributeField from './Form/attributeField';
import StaticField from './Form/staticField';
import { convert, units } from './dimensionUnits';

interface IProps {
  data: DBObjectKit;
  onSaveInstance: (data: RequestAttributeData) => void;
}

interface IState {
  attributes: Attributes;
  isActiveEdit: boolean;
  calculationMethod: string;
  requestAttributesData: RequestAttributeData;
  forceUpdate: boolean;
  materialAttributes: Attributes;
  scopedAttributes: Attributes;
  disableEditingMaterial: boolean;
  disableEditingMaterialAdd: boolean;
  disableEditingScope: boolean;
  unitValuesDictionary: Option[];
  saveEnabled: boolean;
  materialType: string;
  uom: string;
}

class Normalization extends Component<IProps, IState> {
  constructor(props: IProps) {
    super(props);
    this.state = {
      attributes: {},
      isActiveEdit: false,
      calculationMethod: null,
      requestAttributesData: {},
      forceUpdate: false,
      materialAttributes: {},
      scopedAttributes: {},
      disableEditingMaterial: true,
      disableEditingMaterialAdd: true,
      disableEditingScope: true,
      unitValuesDictionary: [],
      saveEnabled: false,
      materialType: '',
      uom: null
    };
  }

  onEditCalculationMethod = (method: Option) => this.setState({ calculationMethod: method.value });

  onEditUnitValues = (option: Option) => {
    this.onEditAttribute(ATTRIBUTE_KIT_UNIT_VALUE, option.value, null, option.value, false);
  };

  onEditActivate = () => {
    if (this.props.data.basics.type.name === TYPE_KIT_MFG_KIT) {
      if (
        !this.state.disableEditingMaterial &&
        !this.state.disableEditingScope &&
        !this.state.disableEditingMaterialAdd
      ) {
        this.setState({
          isActiveEdit: !this.state.isActiveEdit
        });
      }
    } else {
      this.setState({
        isActiveEdit: !this.state.isActiveEdit
      });
    }
  };

  onEditStaticField = (value: string, fieldName: string, isCalculated?: boolean) => {
    const { requestAttributesData } = this.state;
    const attributes = JSON.parse(JSON.stringify(this.state.attributes));

    if (fieldName === ATTRIBUTE_V_USAGE_CONT_COEFF) {
      const transformedValue = editVUsageCoeff(
        value,
        this.props.data.attributes[ATTRIBUTE_V_USAGE_CONT_COEFF],
        this.state.attributes
      );
      attributes[fieldName].value = value;
      attributes[fieldName].info.inputvalue = transformedValue;
      attributes[fieldName].info.inputunit = this.state.uom;

      if (!isCalculated) {
        if (attributes[fieldName].value !== this.state.attributes[fieldName].value) {
          this.setState({
            saveEnabled: true
          });
        } else {
          this.setState({
            saveEnabled: false
          });
        }
      }
      requestAttributesData[fieldName] = String(transformedValue);

      this.setState({
        requestAttributesData,
        attributes
      });
    }
  };

  onEditAttribute = (
    attributeName: string,
    value: string,
    units?: string,
    rawText?: string,
    isCalculated?: boolean
  ) => {
    const attributes = JSON.parse(JSON.stringify(this.state.attributes));
    if (attributes[attributeName]) {
      attributes[attributeName].info.inputvalue = value;
      attributes[attributeName].value = rawText;
      if (!isCalculated) {
        if (attributes[attributeName].value !== this.state.attributes[attributeName].value) {
          this.setState({
            saveEnabled: true
          });
        } else {
          this.setState({
            saveEnabled: false
          });
        }
      }

      const { requestAttributesData } = this.state;
      if (units && units !== '') {
        requestAttributesData[attributeName] = `${value} ${units}`;
      } else {
        requestAttributesData[attributeName] = String(value);
      }

      this.setState({
        requestAttributesData,
        attributes
      });
    }
  };

  componentDidUpdate(prevProps: IProps, prevState: IState) {
    //
    if (prevProps.data !== this.props.data) {
      const { data } = this.props;

      if (data && data.basics && data.basics.type && data.basics.type.name) {
        const type = this.props.data.basics.type.name;
        const isTheSameObj = prevProps.data && prevProps.data.physicalid === this.props.data.physicalid;

        // update continousCoolingUnit
        const contQuantityAttr = getVContQuantityAttr(data.attributes);
        let uom = '';
        if (contQuantityAttr) {
          const clientUOM = contQuantityAttr.info.inputunit;
          uom = clientUOM;
          if (isTheSameObj) {
            uom = this.state.uom ? this.state.uom : clientUOM;
            data.attributes[contQuantityAttr.name].info.inputvalue = String(
              convert(Number(contQuantityAttr.value), clientUOM, uom)
            );
            data.attributes[contQuantityAttr.name].info.inputunit = uom;
          }
        }

        let calculationMethod = '';
        switch (type) {
          case TYPE_KIT_MFG_KIT:
            const materials = data.info.predecessors;
            const { scoped } = data.info;
            let materialType = '';
            let rawType = '';

            if (materials && materials.length > 0) {
              materialType = materials[0].basics.type.name;
              const materialAttributes = materials[0].attributes;
              if (raw_material_types.includes(materialType)) {
                if (materials[0].attributes[ATTRIBUTE_KIT_RS_NAME]) {
                  rawType = materials[0].attributes[ATTRIBUTE_KIT_RS_NAME].info.inputvalue;
                }
              }

              if (oem_part_standard_material_types.includes(materialType)) {
                rawType = 'ПКИ';
                materialAttributes[ATTRIBUTE_KIT_RAW_TYPE] = {
                  displayName: 'Профиль заготовки',
                  info: {
                    inputvalue: rawType,
                    type: 'text'
                  }
                };
              }
              // else {
              //   materialAttributes[ATTRIBUTE_KIT_RAW_TYPE] = {
              //     displayName: 'Профиль заготовки',
              //     info: {
              //       inputvalue: data.attributes[ATTRIBUTE_KIT_RAW_TYPE].value,
              //       type: 'text'
              //     }
              //   };
              // }

              this.setState({
                materialAttributes,
                disableEditingMaterial: false,
                isActiveEdit: false,
                materialType
              });
            } else {
              this.setState({
                disableEditingMaterial: true,
                isActiveEdit: false,
                attributes: data.attributes
              });
            }

            if (scoped && scoped.length > 0) {
              this.setState({
                scopedAttributes: scoped[0].attributes,
                disableEditingScope: false,
                isActiveEdit: false
              });
            } else {
              this.setState({
                disableEditingScope: true,
                isActiveEdit: false,
                attributes: data.attributes
              });
            }

            calculationMethod = rawTypeCalculation.find(item => item.rawType === rawType)
              ? rawTypeCalculation.find(item => item.rawType === rawType).calculationMethod
              : '';

            if (rawType === '' || calculationMethod === '') {
              this.setState({
                disableEditingMaterialAdd: true,
                isActiveEdit: false,
                attributes: data.attributes
              });
            } else {
              const unitValuesDictionary = unitValuesByTypeCalculation
                .filter((item: any) => item.type.includes(calculationMethod))
                .map((item: any) => item.value);

              this.setState(
                {
                  attributes: data.attributes,
                  requestAttributesData: this.prepareForSave(data.attributes),
                  calculationMethod,
                  unitValuesDictionary,
                  disableEditingMaterialAdd: false,
                  isActiveEdit: false
                },
                () => {
                  this.addDefaultAttributeValues(calculationMethod);
                }
              );
            }
            break;
          case TYPE_KIT_MFG_CONT:
            this.setState(
              {
                attributes: data.attributes,
                requestAttributesData: this.prepareForSave(data.attributes),
                disableEditingMaterialAdd: false,
                isActiveEdit: false,
                uom
              },
              () => {
                this.addDefaultAttributeValues(calculationMethod);
              }
            );
            break;
          case TYPE_KIT_COOL_MIX:
            this.setState(
              {
                attributes: data.attributes,
                requestAttributesData: this.prepareForSave(data.attributes),
                disableEditingMaterialAdd: false,
                isActiveEdit: false,
                uom
              },
              () => {
                this.addDefaultAttributeValues(calculationMethod);
              }
            );
            break;
          default:
            break;
        }
      } else {
        this.setState({
          attributes: {},
          isActiveEdit: false,
          calculationMethod: null,
          requestAttributesData: {},
          forceUpdate: false,
          materialAttributes: {},
          scopedAttributes: {},
          disableEditingMaterial: true,
          disableEditingMaterialAdd: true,
          disableEditingScope: true,
          unitValuesDictionary: []
        });
      }
    }
  }

  addDefaultAttributeValues = (calculationMethod: string) => {
    // добавляем сюда значения по умолчанию.
    switch (calculationMethod) {
      case LENGTH:
        break;
      case AREA:
        break;
      case MASS:
        if (
          this.state.attributes[ATTRIBUTE_KIT_UNIT_VALUE].value &&
          this.state.unitValuesDictionary.some(
            (unitValue: Option) => unitValue.value === this.state.attributes[ATTRIBUTE_KIT_UNIT_VALUE].info.inputvalue
          )
        ) {
          // do nothing
        } else {
          this.onEditAttribute(ATTRIBUTE_KIT_UNIT_VALUE, '2', null, '2', false); // устанавливаем значение кг для ЕВ при расчете по массе
        }
        break;
    }
  };

  prepareForSave = (attributes: Attributes): RequestAttributeData => {
    const preparedAttributes: RequestAttributeData = {};

    attributes &&
      Object.values(attributes).forEach((attribute: Attribute) => {
        if (mfgBarAttributes.includes(attribute.name)) {
          preparedAttributes[attribute.name] = `${attribute.info.inputvalue}${
            attribute.info.inputunit ? ` ${attribute.info.inputunit}` : ''
          }`;
        }
      });

    return preparedAttributes;
  };

  onSave = () => {
    if (Object.keys(this.state.requestAttributesData).length > 0) {
      this.props.onSaveInstance(this.state.requestAttributesData);
    }

    this.setState({
      saveEnabled: false
    });
  };

  onReset = () => {
    const { data } = this.props;
    const contQuantityAttr = getVContQuantityAttr(data.attributes);
    const uom = contQuantityAttr?.info?.inputunit ? contQuantityAttr.info.inputunit : null;
    this.setState({
      saveEnabled: false,
      attributes: data.attributes,
      requestAttributesData: this.prepareForSave(data.attributes),
      uom
    });
  };

  onEditUOM = (selected: Option) => {
    const attributes = JSON.parse(JSON.stringify(this.state.attributes));
    const uom = selected.value;
    const attr = getVContQuantityAttr(this.state.attributes);
    const fieldName = attr ? attr.name : '';

    const oldUOM = attributes[fieldName].info.inputunit;
    const oldValue = attributes[fieldName].info.inputvalue;
    attributes[fieldName].info.inputunit = uom;
    attributes[fieldName].info.inputvalue = convert(oldValue, oldUOM, uom);

    if (attributes[fieldName] !== this.state.attributes[fieldName]) {
      this.setState({
        saveEnabled: true
      });
    } else {
      this.setState({
        saveEnabled: false
      });
    }

    this.setState({
      attributes,
      uom
    });
  };

  getUnitValue: () => Option = () => {
    const unitInfo = this.state.attributes[ATTRIBUTE_KIT_UNIT_VALUE];
    const unitValue: Option = unitInfo ? this.getUnitFromDict(unitInfo.info.inputvalue) : this.recogniseAndGetUnit();
    const possibleUnits = this.getUnitValuesOptions().map(o => o.value);
    return possibleUnits.indexOf(unitValue?.value) >= 0 ? unitValue : null;
  };

  recogniseAndGetUnit: () => Option = () => null;

  getUnitFromDict = (unitInputValue: string) => {
    return this.state.unitValuesDictionary.find((unitValue: Option) => unitValue.value === unitInputValue);
  };

  /**
   *  Defines possible options for unit type depend on current material type.
   *  !Important! If type is one of oem_part_standard_material_types,
   *  only 'шт' unit is possible.
   */
  getUnitValuesOptions: () => Option[] = () => {
    return oem_part_standard_material_types.includes(this.state.materialType)
      ? [unitValuesByTypeCalculation[3].value]
      : this.state.unitValuesDictionary;
  };

  render() {
    if (
      this.props.data &&
      this.props.data.basics &&
      this.props.data.basics.type &&
      this.props.data.basics.type.name &&
      normalization_types.includes(this.props.data.basics.type.name)
    ) {
      return (
        <div className="normalization-container">
          <div className="buttons-container right">
            <button onClick={this.onSave} disabled={!(this.state.isActiveEdit && this.state.saveEnabled)}>
              Сохранить
            </button>
            <button onClick={this.onReset} disabled={!this.state.isActiveEdit}>
              Сбросить
            </button>
            <button
              className={`toggle-button${this.state.isActiveEdit ? ' on' : ' off'}`}
              onClick={this.onEditActivate}
            >
              <FontAwesomeIcon
                title={`${this.state.isActiveEdit ? 'Отключить редактирование' : 'Включить редактирование'}`}
                icon={faPencilAlt}
              />
            </button>
          </div>
          {this.props.data.basics.type.name === TYPE_KIT_MFG_KIT ? (
            <>
              {this.state.disableEditingMaterial ? (
                <div className="normalization-status-container">
                  <p className="warning">Редактирование невозможно. Необходимо назначить материал.</p>
                </div>
              ) : null}
              {this.state.disableEditingScope ? (
                <div className="normalization-status-container">
                  <p className="warning">Редактирование невозможно. Необходимо назначить деталь.</p>
                </div>
              ) : null}
              {this.state.disableEditingMaterialAdd ? (
                <div className="normalization-status-container">
                  <p className="warning">Редактирование невозможно. Необходимо проверить материал.</p>
                </div>
              ) : null}
              {this.getUnitValue() === null && (
                <div className="normalization-status-container">
                  <p className="warning">Редактирование невозможно. Необходимо выбрать единицу величины.</p>
                </div>
              )}
              <div className="normalization-form-container">
                <div className="fieldset-container">
                  <div className="fieldset-label">Параметры детали</div>
                  {/* длина детали */}
                  <AttributeField
                    hided={applyHideRule(ATTRIBUTE_KIT_PART_LENGTH, this.state.calculationMethod)}
                    disabled={
                      applyBlockRule(ATTRIBUTE_KIT_PART_LENGTH, this.state.calculationMethod) ||
                      !this.state.isActiveEdit
                    }
                    attribute={this.state.attributes[ATTRIBUTE_KIT_PART_LENGTH]}
                    customUnit={DIM_MM}
                    onEdit={this.onEditAttribute}
                    forceUpdate={this.state.forceUpdate}
                  />
                  {/* Ширина детали */}
                  <AttributeField
                    hided={applyHideRule(ATTRIBUTE_KIT_PART_WIDTH, this.state.calculationMethod)}
                    disabled={
                      applyBlockRule(ATTRIBUTE_KIT_PART_WIDTH, this.state.calculationMethod) || !this.state.isActiveEdit
                    }
                    attribute={this.state.attributes[ATTRIBUTE_KIT_PART_WIDTH]}
                    customUnit={DIM_MM}
                    onEdit={this.onEditAttribute}
                    forceUpdate={this.state.forceUpdate}
                  />
                </div>
                <div className="fieldset-container">
                  <div className="fieldset-label">Параметры заготовки</div>
                  {/* Профиль заготовки */}
                  <AttributeField
                    hided={false}
                    disabled={true}
                    attribute={this.state.materialAttributes[ATTRIBUTE_KIT_RAW_TYPE]}
                    onEdit={this.onEditAttribute}
                    forceUpdate={this.state.forceUpdate}
                    customLabel="Профиль заготовки"
                  />
                  {/* Тип расчета заготовки */}
                  <div className="field-container">
                    <div className="field-label">Тип расчета заготовки</div>
                    <div className="field-value">
                      <Select
                        className="select-item"
                        classNamePrefix="select"
                        value={calculationMethodsOpts.find(
                          (option: Option) => option.value === this.state.calculationMethod
                        )}
                        name="calculationMethod"
                        options={calculationMethodsOpts}
                        onChange={this.onEditCalculationMethod}
                        isDisabled={true}
                      />
                    </div>
                  </div>
                  {/* Длина единичной заготовки */}
                  <AttributeField
                    hided={applyHideRule(ATTRIBUTE_KIT_STOCK_LENGTH, this.state.calculationMethod)}
                    disabled={
                      applyBlockRule(ATTRIBUTE_KIT_STOCK_LENGTH, this.state.calculationMethod) ||
                      !this.state.isActiveEdit
                    }
                    attribute={this.state.attributes[ATTRIBUTE_KIT_STOCK_LENGTH]}
                    calculationMethod={this.state.calculationMethod}
                    calculationArgs={[
                      this.state.attributes[ATTRIBUTE_KIT_PART_LENGTH],
                      this.state.attributes[ATTRIBUTE_KIT_ALLOWANCE],
                      this.state.attributes[ATTRIBUTE_KIT_CUT_ALLOWANCE],
                      this.state.attributes[ATTRIBUTE_KIT_INSTALL_ALLOWANCE],
                      this.state.materialAttributes[ATTRIBUTE_KIT_RAW_LENGTH]
                    ]}
                    calculationFunction={calculateKitStockLength}
                    validationFunction={validationKitStockLength}
                    customUnit={DIM_MM}
                    onEdit={this.onEditAttribute}
                    forceUpdate={this.state.forceUpdate}
                  />
                  {/* Max длина групповой заготов */}
                  <AttributeField
                    hided={applyHideRule(ATTRIBUTE_KIT_MAX_GROUP_STOCK_LENGTH, this.state.calculationMethod)}
                    disabled={
                      applyBlockRule(ATTRIBUTE_KIT_MAX_GROUP_STOCK_LENGTH, this.state.calculationMethod) ||
                      !this.state.isActiveEdit
                    }
                    attribute={this.state.attributes[ATTRIBUTE_KIT_MAX_GROUP_STOCK_LENGTH]}
                    calculationArgs={[
                      this.state.attributes[ATTRIBUTE_KIT_MAX_GROUP_STOCK_LENGTH],
                      this.state.attributes[ATTRIBUTE_KIT_INSTALL_ALLOWANCE]
                    ]}
                    validationFunction={validateKitMaxGroupStockLength}
                    customUnit={DIM_MM}
                    onEdit={this.onEditAttribute}
                    forceUpdate={this.state.forceUpdate}
                  />
                  {/* Диаметр/Толщина единичной заготовки */}
                  <AttributeField
                    hided={applyHideRule(ATTRIBUTE_KIT_STOCK_HIGHT_DIAMETER, this.state.calculationMethod)}
                    disabled={
                      applyBlockRule(ATTRIBUTE_KIT_STOCK_HIGHT_DIAMETER, this.state.calculationMethod) ||
                      !this.state.isActiveEdit
                    }
                    attribute={this.state.attributes[ATTRIBUTE_KIT_STOCK_HIGHT_DIAMETER]}
                    calculationMethod={this.state.calculationMethod}
                    calculationArgs={[this.state.materialAttributes[ATTRIBUTE_KIT_RAW_THICKNESS]]}
                    calculationFunction={calculateKitStockThickness}
                    customUnit={DIM_MM}
                    onEdit={this.onEditAttribute}
                    forceUpdate={this.state.forceUpdate}
                    customLabel="Диаметр/Толщина заготовки"
                  />
                  {/* Ширина единичной заготовки */}
                  <AttributeField
                    hided={applyHideRule(ATTRIBUTE_KIT_STOCK_WIDTH, this.state.calculationMethod)}
                    disabled={
                      applyBlockRule(ATTRIBUTE_KIT_STOCK_WIDTH, this.state.calculationMethod) ||
                      !this.state.isActiveEdit
                    }
                    attribute={this.state.attributes[ATTRIBUTE_KIT_STOCK_WIDTH]}
                    customUnit={DIM_MM}
                    calculationMethod={this.state.calculationMethod}
                    calculationArgs={[
                      this.state.attributes[ATTRIBUTE_KIT_PART_WIDTH],
                      this.state.attributes[ATTRIBUTE_KIT_ALLOWANCE],
                      this.state.materialAttributes[ATTRIBUTE_KIT_RAW_WIDTH]
                    ]}
                    calculationFunction={calculateKitStockWidth}
                    validationFunction={validateKitStockWidth}
                    onEdit={this.onEditAttribute}
                    forceUpdate={this.state.forceUpdate}
                  />
                  {/* Припуск на размер */}
                  <AttributeField
                    hided={applyHideRule(ATTRIBUTE_KIT_ALLOWANCE, this.state.calculationMethod)}
                    disabled={
                      applyBlockRule(ATTRIBUTE_KIT_ALLOWANCE, this.state.calculationMethod) || !this.state.isActiveEdit
                    }
                    attribute={this.state.attributes[ATTRIBUTE_KIT_ALLOWANCE]}
                    customUnit={DIM_MM}
                    onEdit={this.onEditAttribute}
                    forceUpdate={this.state.forceUpdate}
                  />
                  {/* Припуск под отрезку */}
                  <AttributeField
                    hided={applyHideRule(ATTRIBUTE_KIT_CUT_ALLOWANCE, this.state.calculationMethod)}
                    disabled={
                      applyBlockRule(ATTRIBUTE_KIT_CUT_ALLOWANCE, this.state.calculationMethod) ||
                      !this.state.isActiveEdit
                    }
                    attribute={this.state.attributes[ATTRIBUTE_KIT_CUT_ALLOWANCE]}
                    customUnit={DIM_MM}
                    onEdit={this.onEditAttribute}
                    forceUpdate={this.state.forceUpdate}
                  />
                  {/* Припуск под установку */}
                  <AttributeField
                    hided={applyHideRule(ATTRIBUTE_KIT_INSTALL_ALLOWANCE, this.state.calculationMethod)}
                    disabled={
                      applyBlockRule(ATTRIBUTE_KIT_INSTALL_ALLOWANCE, this.state.calculationMethod) ||
                      !this.state.isActiveEdit
                    }
                    attribute={this.state.attributes[ATTRIBUTE_KIT_INSTALL_ALLOWANCE]}
                    calculationArgs={[
                      this.state.attributes[ATTRIBUTE_KIT_MAX_GROUP_STOCK_LENGTH],
                      this.state.attributes[ATTRIBUTE_KIT_INSTALL_ALLOWANCE]
                    ]}
                    validationFunction={validateKitInstallAllowance}
                    customUnit={DIM_MM}
                    onEdit={this.onEditAttribute}
                    forceUpdate={this.state.forceUpdate}
                  />
                  {/* Количество деталей из един */}
                  <AttributeField
                    hided={applyHideRule(ATTRIBUTE_KIT_NUMBER_OF_PARTS, this.state.calculationMethod)}
                    disabled={
                      applyBlockRule(ATTRIBUTE_KIT_NUMBER_OF_PARTS, this.state.calculationMethod) ||
                      !this.state.isActiveEdit
                    }
                    attribute={this.state.attributes[ATTRIBUTE_KIT_NUMBER_OF_PARTS]}
                    onEdit={this.onEditAttribute}
                    forceUpdate={this.state.forceUpdate}
                  />
                  {/* Количество дополнительных деталей */}
                  <AttributeField
                    hided={applyHideRule(ATTRIBUTE_KIT_NUMBER_OF_ADD_PARTS, this.state.calculationMethod)}
                    disabled={
                      applyBlockRule(ATTRIBUTE_KIT_NUMBER_OF_ADD_PARTS, this.state.calculationMethod) ||
                      !this.state.isActiveEdit
                    }
                    attribute={this.state.attributes[ATTRIBUTE_KIT_NUMBER_OF_ADD_PARTS]}
                    onEdit={this.onEditAttribute}
                    forceUpdate={this.state.forceUpdate}
                  />
                  {/* ЕВ - Единица величины */}
                  <div className="field-container">
                    <div className="field-label">ЕВ - Единица величины</div>
                    <div className="field-value">
                      <Select
                        className="select-item"
                        classNamePrefix="select"
                        value={this.getUnitValue()}
                        name="unitValues"
                        options={this.getUnitValuesOptions()}
                        onChange={this.onEditUnitValues}
                        isDisabled={!this.state.isActiveEdit}
                      />
                    </div>
                  </div>
                </div>
                <div className="fieldset-container">
                  <div className="fieldset-label">Расчетные параметры</div>
                  {/* Масса детали */}
                  <AttributeField
                    hided={applyHideRule(ATTRIBUTE_KIT_PART_MASS, this.state.calculationMethod)}
                    disabled={
                      applyBlockRule(ATTRIBUTE_KIT_PART_MASS, this.state.calculationMethod) || !this.state.isActiveEdit
                    }
                    attribute={this.state.attributes[ATTRIBUTE_KIT_PART_MASS]}
                    calculationMethod={this.state.calculationMethod}
                    calculationArgs={[this.state.scopedAttributes[ATTRIBUTE_V_WCG_MASS]]}
                    calculationFunction={calculateDetailMass}
                    onEdit={this.onEditAttribute}
                    forceUpdate={this.state.forceUpdate}
                  />
                  {/* Масса заготовки */}
                  <AttributeField
                    hided={applyHideRule(ATTRIBUTE_KIT_STOCK_WEIGHT, this.state.calculationMethod)}
                    disabled={
                      applyBlockRule(ATTRIBUTE_KIT_STOCK_WEIGHT, this.state.calculationMethod) ||
                      !this.state.isActiveEdit
                    }
                    attribute={this.state.attributes[ATTRIBUTE_KIT_STOCK_WEIGHT]}
                    calculationMethod={this.state.calculationMethod}
                    calculationArgs={[
                      this.state.attributes[ATTRIBUTE_KIT_STOCK_LENGTH],
                      this.state.materialAttributes[ATTRIBUTE_KIT_LINEAR_MASS],
                      this.state.materialAttributes[ATTRIBUTE_KIT_AREA_MASS],
                      this.state.attributes[ATTRIBUTE_KIT_STOCK_WIDTH],
                      oem_part_standard_material_types.includes(this.state.materialType)
                        ? this.state.materialAttributes[ATTRIBUTE_KIT_WEIGHT]
                        : this.state.materialAttributes[ATTRIBUTE_KIT_RAW_WEIGHT],
                      this.state.attributes[ATTRIBUTE_KIT_UNIT_VALUE],
                      this.state.attributes[ATTRIBUTE_KIT_MAT_CONSUMPTION_RATE]
                    ]}
                    calculationFunction={calculateKitStockMass}
                    onEdit={this.onEditAttribute}
                    forceUpdate={this.state.forceUpdate}
                  />
                  {/* Норма расхода материала */}
                  <AttributeField
                    hided={applyHideRule(ATTRIBUTE_KIT_UNIT_VALUE, this.state.calculationMethod)}
                    disabled={
                      (this.state.attributes[ATTRIBUTE_KIT_UNIT_VALUE] &&
                        this.state.attributes[ATTRIBUTE_KIT_UNIT_VALUE].info.inputvalue !== '4' &&
                        applyBlockRule(ATTRIBUTE_KIT_MAT_CONSUMPTION_RATE, this.state.calculationMethod)) ||
                      !this.state.isActiveEdit
                    }
                    attribute={this.state.attributes[ATTRIBUTE_KIT_MAT_CONSUMPTION_RATE]}
                    onEdit={this.onEditAttribute}
                    calculationMethod={this.state.calculationMethod}
                    calculationArgs={[
                      this.state.attributes[ATTRIBUTE_KIT_UNIT_VALUE],
                      this.state.attributes[ATTRIBUTE_KIT_STOCK_LENGTH],
                      this.state.materialAttributes[ATTRIBUTE_KIT_LINEAR_MASS],
                      this.state.attributes[ATTRIBUTE_KIT_STOCK_WIDTH],
                      this.state.materialAttributes[ATTRIBUTE_KIT_AREA_MASS],
                      this.state.attributes[ATTRIBUTE_KIT_STOCK_WEIGHT],
                      this.state.attributes[ATTRIBUTE_KIT_MAT_CONSUMPTION_RATE]
                    ]}
                    calculationFunction={calculateMatConsumptionRate}
                    forceUpdate={this.state.forceUpdate}
                  />
                  {/* КИМ */}
                  <AttributeField
                    hided={applyHideRule(ATTRIBUTE_KIT_USAGE_COEFFIC, this.state.calculationMethod)}
                    disabled={
                      applyBlockRule(ATTRIBUTE_KIT_USAGE_COEFFIC, this.state.calculationMethod) ||
                      !this.state.isActiveEdit
                    }
                    attribute={this.state.attributes[ATTRIBUTE_KIT_USAGE_COEFFIC]}
                    onEdit={this.onEditAttribute}
                    calculationMethod={this.state.calculationMethod}
                    calculationFunction={calculateUsageCoeffic}
                    calculationArgs={[
                      this.state.scopedAttributes[ATTRIBUTE_V_WCG_MASS],
                      this.state.attributes[ATTRIBUTE_KIT_STOCK_WEIGHT],
                      this.state.attributes[ATTRIBUTE_KIT_STOCK_LENGTH],
                      this.state.attributes[ATTRIBUTE_KIT_STOCK_WIDTH],
                      this.state.materialAttributes[ATTRIBUTE_KIT_LINEAR_MASS],
                      this.state.materialAttributes[ATTRIBUTE_KIT_AREA_MASS]
                    ]}
                    forceUpdate={this.state.forceUpdate}
                  />
                  {/* Max кол-во деталей из груп */}
                  <AttributeField
                    hided={applyHideRule(ATTRIBUTE_KIT_MAX_NUMBER_OF_PARTS, this.state.calculationMethod)}
                    disabled={
                      applyBlockRule(ATTRIBUTE_KIT_MAX_NUMBER_OF_PARTS, this.state.calculationMethod) ||
                      !this.state.isActiveEdit
                    }
                    attribute={this.state.attributes[ATTRIBUTE_KIT_MAX_NUMBER_OF_PARTS]}
                    onEdit={this.onEditAttribute}
                    calculationMethod={this.state.calculationMethod}
                    calculationFunction={calculateMaxDetailCount}
                    calculationArgs={[
                      this.state.attributes[ATTRIBUTE_KIT_MAX_GROUP_STOCK_LENGTH],
                      this.state.attributes[ATTRIBUTE_KIT_INSTALL_ALLOWANCE],
                      this.state.attributes[ATTRIBUTE_KIT_PART_LENGTH],
                      this.state.attributes[ATTRIBUTE_KIT_ALLOWANCE]
                    ]}
                    forceUpdate={this.state.forceUpdate}
                  />
                  {/* Расч. max длина групповой */}
                  <AttributeField
                    hided={applyHideRule(ATTRIBUTE_KIT_MAX_GROUP_STOCK_LENGTH_CALC, this.state.calculationMethod)}
                    disabled={
                      applyBlockRule(ATTRIBUTE_KIT_MAX_GROUP_STOCK_LENGTH_CALC, this.state.calculationMethod) ||
                      !this.state.isActiveEdit
                    }
                    attribute={this.state.attributes[ATTRIBUTE_KIT_MAX_GROUP_STOCK_LENGTH_CALC]}
                    customUnit={DIM_MM}
                    onEdit={this.onEditAttribute}
                    calculationMethod={this.state.calculationMethod}
                    calculationFunction={calculateMaxGroupStockLength}
                    calculationArgs={[
                      this.state.attributes[ATTRIBUTE_KIT_PART_LENGTH],
                      this.state.attributes[ATTRIBUTE_KIT_ALLOWANCE],
                      this.state.attributes[ATTRIBUTE_KIT_MAX_NUMBER_OF_PARTS],
                      this.state.attributes[ATTRIBUTE_KIT_INSTALL_ALLOWANCE]
                    ]}
                    forceUpdate={this.state.forceUpdate}
                  />
                </div>
              </div>
            </>
          ) : null}
          {normalization_continuous_cooling_types.includes(this.props.data.basics.type.name) ? (
            <>
              <StaticField
                disabled={true}
                label="Наименование"
                fieldName="name_description"
                value={`${this.props.data.attributes[ATTRIBUTE_V_NAME].value} ${this.props.data.attributes[ATTRIBUTE_V_DESCRIPTION].value}`}
                unit=""
                type="text"
                flex="flex2"
                onEdit={this.onEditStaticField}
              />
              <div className="field-container">
                <div className="field-label">Единица измерения</div>
                <div className="field-value">
                  <Select
                    className="select-item"
                    classNamePrefix="select"
                    value={
                      this.state.uom
                        ? {
                            label: units[this.state.uom] ? units[this.state.uom].label : this.state.uom,
                            value: this.state.uom
                          }
                        : null
                    }
                    name="unitValues"
                    options={Object.keys(units)
                      .filter(unitKey => {
                        const materialUnit = this.state.uom;
                        const unitCurrent = units[materialUnit];
                        if (unitCurrent && materialUnit) {
                          const unitType = unitCurrent.type;
                          return units[unitKey].type == unitType;
                        }
                        return false;
                      })
                      .map(unitKey => {
                        return { label: units[unitKey].label, value: unitKey };
                      })}
                    onChange={this.onEditUOM}
                    isDisabled={!this.state.isActiveEdit}
                  />
                </div>
              </div>

              <StaticField
                disabled={!this.state.isActiveEdit}
                label="Количество"
                fieldName={ATTRIBUTE_V_USAGE_CONT_COEFF}
                value={calculateVUsageCoeff(this.state.attributes)}
                unit={units[this.state.uom] ? units[this.state.uom].label : this.state.uom}
                type="number"
                onEdit={this.onEditStaticField}
              />
            </>
          ) : null}
        </div>
      );
    }
    return (
      <div className="normalization-placeholder">
        <span>Выберите объект Заготовка</span>
      </div>
    );
  }
}

export default Normalization;
