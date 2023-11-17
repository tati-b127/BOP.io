import { Attribute, AttributeNames, Attributes, Validation } from '../store/interfaces';
import { multiply, sum } from '../utils/mathUtils';

import {
  AREA,
  ATTRIBUTE_KIT_ALLOWANCE,
  ATTRIBUTE_KIT_CUT_ALLOWANCE,
  ATTRIBUTE_KIT_INSTALL_ALLOWANCE,
  ATTRIBUTE_KIT_MAT_CONSUMPTION_RATE,
  ATTRIBUTE_KIT_MAX_GROUP_STOCK_LENGTH,
  ATTRIBUTE_KIT_MAX_GROUP_STOCK_LENGTH_CALC,
  ATTRIBUTE_KIT_MAX_NUMBER_OF_PARTS,
  ATTRIBUTE_KIT_NUMBER_OF_PARTS,
  ATTRIBUTE_KIT_PART_LENGTH,
  ATTRIBUTE_KIT_PART_MASS,
  ATTRIBUTE_KIT_PART_WIDTH,
  ATTRIBUTE_KIT_STOCK_HIGHT_DIAMETER,
  ATTRIBUTE_KIT_STOCK_LENGTH,
  ATTRIBUTE_KIT_STOCK_WEIGHT,
  ATTRIBUTE_KIT_STOCK_WIDTH,
  ATTRIBUTE_KIT_USAGE_COEFFIC,
  ATTRIBUTE_V_CONT_QUANTITY_AREA,
  ATTRIBUTE_V_CONT_QUANTITY_LENGTH,
  ATTRIBUTE_V_CONT_QUANTITY_MASS,
  ATTRIBUTE_V_CONT_QUANTITY_VOLUME,
  ATTRIBUTE_V_USAGE_CONT_COEFF,
  DIM_METER,
  LENGTH,
  MASS,
  unitValues
} from './consts';
import { convert } from './dimensionUnits';

export const rawTypeCalculation = [
  { rawType: 'Круг', calculationMethod: LENGTH },
  { rawType: 'Шток', calculationMethod: LENGTH },
  { rawType: 'Шпилька', calculationMethod: LENGTH },
  { rawType: 'Труба', calculationMethod: LENGTH },
  { rawType: 'Квадрат', calculationMethod: LENGTH },
  { rawType: 'Шестигранник', calculationMethod: LENGTH },
  { rawType: 'Швеллер', calculationMethod: LENGTH },
  { rawType: 'Двутавр', calculationMethod: LENGTH },
  { rawType: 'Профиль', calculationMethod: LENGTH },
  { rawType: 'Уголок', calculationMethod: LENGTH },
  { rawType: 'Плита', calculationMethod: AREA },
  { rawType: 'Лист', calculationMethod: AREA },
  { rawType: 'Пластина', calculationMethod: AREA },
  { rawType: 'Стекло', calculationMethod: AREA },
  { rawType: 'Поролон', calculationMethod: AREA },
  { rawType: 'Отливка', calculationMethod: MASS },
  { rawType: 'Поковка', calculationMethod: MASS },
  { rawType: 'Пруток', calculationMethod: LENGTH },
  { rawType: 'ПКИ', calculationMethod: MASS }
];

export const unitValuesByTypeCalculation = [
  { value: unitValues[0], type: [AREA] },
  { value: unitValues[1], type: [LENGTH, AREA, MASS] },
  { value: unitValues[2], type: [LENGTH] },
  { value: unitValues[3], type: [LENGTH, AREA, MASS] }
];

export const blockFieldRule = {
  [ATTRIBUTE_KIT_PART_LENGTH]: [MASS],
  [ATTRIBUTE_KIT_PART_WIDTH]: [MASS, LENGTH],
  [ATTRIBUTE_KIT_STOCK_LENGTH]: [LENGTH, MASS, AREA],
  [ATTRIBUTE_KIT_STOCK_HIGHT_DIAMETER]: [AREA, LENGTH, MASS],
  [ATTRIBUTE_KIT_STOCK_WIDTH]: [MASS, LENGTH, AREA],
  [ATTRIBUTE_KIT_ALLOWANCE]: [MASS],
  [ATTRIBUTE_KIT_CUT_ALLOWANCE]: [AREA, MASS],
  [ATTRIBUTE_KIT_INSTALL_ALLOWANCE]: [AREA, MASS],
  [ATTRIBUTE_KIT_PART_MASS]: [LENGTH, AREA, MASS],
  [ATTRIBUTE_KIT_STOCK_WEIGHT]: [LENGTH, AREA, MASS],
  [ATTRIBUTE_KIT_MAT_CONSUMPTION_RATE]: [LENGTH, AREA, MASS],
  [ATTRIBUTE_KIT_USAGE_COEFFIC]: [LENGTH, AREA, MASS],
  [ATTRIBUTE_KIT_MAX_NUMBER_OF_PARTS]: [LENGTH, AREA, MASS],
  [ATTRIBUTE_KIT_MAX_GROUP_STOCK_LENGTH_CALC]: [LENGTH, AREA, MASS],
  [ATTRIBUTE_KIT_MAX_GROUP_STOCK_LENGTH]: [AREA, MASS],
  [ATTRIBUTE_KIT_NUMBER_OF_PARTS]: [AREA]
};

export const hideFieldRule = {
  [ATTRIBUTE_KIT_MAX_GROUP_STOCK_LENGTH]: [AREA, MASS],
  [ATTRIBUTE_KIT_CUT_ALLOWANCE]: [AREA, MASS],
  [ATTRIBUTE_KIT_ALLOWANCE]: [MASS],
  [ATTRIBUTE_KIT_INSTALL_ALLOWANCE]: [AREA, MASS],
  [ATTRIBUTE_KIT_NUMBER_OF_PARTS]: [AREA],
  [ATTRIBUTE_KIT_MAX_NUMBER_OF_PARTS]: [AREA, MASS],
  [ATTRIBUTE_KIT_MAX_GROUP_STOCK_LENGTH_CALC]: [AREA, MASS],
  [ATTRIBUTE_KIT_STOCK_WIDTH]: [LENGTH, MASS],
  [ATTRIBUTE_KIT_PART_WIDTH]: [LENGTH, MASS],
  [ATTRIBUTE_KIT_PART_LENGTH]: [MASS],
  [ATTRIBUTE_KIT_STOCK_LENGTH]: [MASS],
  [ATTRIBUTE_KIT_STOCK_HIGHT_DIAMETER]: [MASS]
};

export const applyBlockRule = (attributeName: AttributeNames, calculationMethod: string): boolean => {
  // @ts-ignore
  if (blockFieldRule[attributeName]) {
    // @ts-ignore
    return blockFieldRule[attributeName].includes(calculationMethod);
  }
  return false;
};

export const applyHideRule = (attributeName: AttributeNames, calculationMethod: string): boolean => {
  // @ts-ignore
  return hideFieldRule[attributeName] ? hideFieldRule[attributeName].includes(calculationMethod) : false;
};

export const validationKitStockLength = (
  attr1: Attribute,
  attr2: Attribute,
  attr3: Attribute,
  attr4: Attribute,
  attr5: Attribute,
  calculationMethod?: string
): Validation => {
  const validation = {
    statusOK: true,
    message: ''
  };

  if (attr1 && attr2 && attr3 && attr4 && attr5) {
    const rawLength = parseFloat(attr5.info.inputvalue);
    let result = 0;
    if (calculationMethod == LENGTH) {
      result = sum(
        sum(attr1.info.inputvalue, attr2.info.inputvalue),
        sum(attr3.info.inputvalue, attr4.info.inputvalue)
      );
    }
    if (calculationMethod == AREA) {
      result = sum(attr1.info.inputvalue, attr2.info.inputvalue);
    }
    if (result > rawLength) {
      validation.statusOK = false;
      validation.message = 'Длина заготовки не может быть больше длины в поставке!';
    }
  }
  return validation;
};

// Слайд 26
export const calculateKitStockLength = (
  attr1: Attribute,
  attr2: Attribute,
  attr3: Attribute,
  attr4: Attribute,
  attr5: Attribute,
  calculationMethod?: string
): number => {
  // Расчет атрибута «Длина единичной заготовки» =​
  // Длина детали + Припуск на размер + Припуск под установку + Припуск под отрезку, мм​
  if (attr1 && attr2 && attr3 && attr4 && attr5) {
    const rawLength = parseFloat(attr5.info.inputvalue);
    if (calculationMethod == LENGTH) {
      const result = sum(
        sum(attr1.info.inputvalue, attr2.info.inputvalue),
        sum(attr3.info.inputvalue, attr4.info.inputvalue)
      );
      return result;
    }
    if (calculationMethod == AREA) {
      const result = sum(attr1.info.inputvalue, attr2.info.inputvalue);
      return result;
    }
    return 0;
  }
  return 0;
};

export const validateKitStockWidth = (
  attr1: Attribute,
  attr2: Attribute,
  attr3: Attribute,
  calculationMethod?: string
): Validation => {
  const validation = {
    statusOK: true,
    message: ''
  };
  // Расчет атрибута: «Ширина единичной заготовки» =​
  // «Ширина детали» + «Припуск на размер»​
  if (attr1 && attr2 && attr3) {
    if (calculationMethod == AREA) {
      const result = sum(attr1.info.inputvalue, attr2.info.inputvalue);
      const rawWidth = parseFloat(attr3.info.inputvalue);
      if (result > rawWidth) {
        validation.statusOK = false;
        validation.message = 'Ширина заготовки не может быть больше ширины материала!';
      }
    }
  }
  return validation;
};

// слайд 40
export const calculateKitStockWidth = (
  attr1: Attribute,
  attr2: Attribute,
  attr3: Attribute,
  calculationMethod?: string
): number => {
  // Расчет атрибута: «Ширина единичной заготовки» =​
  // «Ширина детали» + «Припуск на размер»​
  if (attr1 && attr2 && attr3) {
    if (calculationMethod == AREA) {
      const result = sum(attr1.info.inputvalue, attr2.info.inputvalue);
      const rawWidth = parseFloat(attr3.info.inputvalue);
      return result;
    }
    return 0;
  }
  return 0;
};

// слайд 28
export const calculateKitStockThickness = (attr1: Attribute, calculationMethod?: string): number => {
  if (attr1) {
    const value = parseFloat(attr1.info.inputvalue);
    if (isNaN(value)) {
      return 0;
    }
    return parseFloat(attr1.info.inputvalue);
  }
  return 0;
};

// слайд 30
export const calculateKitStockMass = (
  attr1: Attribute,
  attr2: Attribute,
  attr3: Attribute,
  attr4: Attribute,
  attr5: Attribute,
  attr6: Attribute,
  attr7: Attribute,
  calculationMethod?: string
): number => {
  /* TYPE_KIT_STOCK_LENGTH,
    TYPE_MATERIAL_LINEAR_MASS,
    TYPE_MATERIAL_AREA_MASS,
    TYPE_KIT_STOCK_WIDTH,
    TYPE_MATERIAL_DETAIL_MASS,
    TYPE_KIT_UNIT_VALUE */
  /*
    { value: '1', label: 'м2' },
    { value: '2', label: 'кг' },
    { value: '3', label: 'м.п.' },
    { value: '4', label: 'шт.' }
    */

  if (calculationMethod == LENGTH) {
    if (attr1 && attr2 && attr5 && attr6 && attr7) {
      const unitValue = attr6.info.inputvalue;
      let result = 0;
      if (unitValue === '4') {
        result = multiply(attr5.info.inputvalue, attr7.info.inputvalue);
      } else {
        // Расчет атрибута: «Масса заготовки» =​
        // (Длина единичной заготовки / 1000 (для перевода в метры))* Масса единицы длины (атрибут объекта «Материал»)
        result = multiply(
          convert(parseFloat(attr1.info.inputvalue), attr1.info.inputunit, DIM_METER),
          attr2.info.inputvalue
        );
        // ​Результат округляется до 3-его знака после запятой​
      }
      result = parseFloat(result.toFixed(3));
      return result;
    }
  }
  if (calculationMethod == AREA) {
    if (attr1 && attr3 && attr4 && attr5 && attr6 && attr7) {
      const unitValue = attr6.info.inputvalue;
      let result = 0;
      if (unitValue === '4') {
        result = multiply(attr5.info.inputvalue, attr7.info.inputvalue);
      } else {
        // Расчет атрибута: «Масса заготовки» =​
        // ((Длина единичной заготовки*Ширина единичной заготовки) / 1 000 000 (для перевода в метры2))* Масса единицы площади (атрибут объекта Материал)​
        // Результат округлить до 3-его знака после запятой​
        result = multiply(multiply(attr1.info.inputvalue, attr3.info.inputvalue), attr4.info.inputvalue);
      }
      result = parseFloat(result.toFixed(3)); // Math.round(result*1000)/1000;
      return result;
    }
  }
  if (calculationMethod == MASS) {
    if (attr5 && attr6 && attr7) {
      const unitValue = attr6.info.inputvalue;
      const result = 0;
      if (unitValue === '2') {
        const mass = parseFloat(attr5.info.inputvalue);
        return parseFloat(mass.toFixed(3)); // Math.round(mass*1000)/1000;
      }
      const mass = multiply(attr5.info.inputvalue, attr7.info.inputvalue);
      return parseFloat(mass.toFixed(3)); // Math.round(mass*1000)/1000;
    }
  }
  return 0;
};

// слайд 31
export const calculateMatConsumptionRate = (
  attrUnitValue: Attribute,
  attr2: Attribute,
  attr3: Attribute,
  attr4: Attribute,
  attr5: Attribute,
  attr6: Attribute,
  attr7: Attribute,
  calculationMethod?: string
): number => {
  /*
    { value: '1', label: 'м2' },
    { value: '2', label: 'кг' },
    { value: '3', label: 'м.п.' },
    { value: '4', label: 'шт.' }
    */
  if (calculationMethod == LENGTH) {
    if (attrUnitValue && attr2 && attr3 && attr7) {
      if (attrUnitValue.info.inputvalue == '1') {
        return 0;
      }
      if (attrUnitValue.info.inputvalue == '2') {
        const result = multiply(attr2.info.inputvalue, attr3.info.inputvalue);
        return parseFloat(result.toFixed(3));
      }
      if (attrUnitValue.info.inputvalue == '3') {
        const value2 = parseFloat(attr2.info.inputvalue);
        const unit = attr2.info.inputunit;
        const result = convert(value2, unit, DIM_METER);
        if (!isNaN(value2)) return parseFloat(result.toFixed(3));
        return 0;
      }
      if (attrUnitValue.info.inputvalue == '4') {
        const value = parseFloat(attr7.info.inputvalue);
        if (!isNaN(value)) return parseFloat(value.toFixed(3));
        return null;
      }
    }
  }
  if (calculationMethod == AREA) {
    if (attrUnitValue && attr2 && attr4 && attr7) {
      if (attrUnitValue.info.inputvalue == '3') {
        return 0;
      }
      if (attrUnitValue.info.inputvalue == '2') {
        const area = multiply(attr2.info.inputvalue, attr4.info.inputvalue);
        const result = multiply(area, attr5.info.inputvalue);
        return parseFloat(result.toFixed(3));
      }
      if (attrUnitValue.info.inputvalue == '1') {
        const area = multiply(attr2.info.inputvalue, attr4.info.inputvalue);
        return parseFloat(area.toFixed(3));
      }
      if (attrUnitValue.info.inputvalue == '4') {
        const value = parseFloat(attr7.info.inputvalue);
        if (!isNaN(value)) return parseFloat(value.toFixed(3));
        return null;
      }
    }
  }
  if (calculationMethod == MASS) {
    if (attrUnitValue && attr6 && attr7) {
      if (attrUnitValue.info.inputvalue == '2') {
        const result = parseFloat(attr6.info.inputvalue);
        if (!isNaN(result)) return parseFloat(result.toFixed(3));
        return 0;
      }
      if (attrUnitValue.info.inputvalue == '4') {
        const value = parseFloat(attr7.info.inputvalue);
        if (!isNaN(value)) return parseFloat(value.toFixed(3));
        return null;
      }
    }
  }
  return 0;
};

// слайд 32
export const calculateUsageCoeffic = (
  attr1: Attribute,
  attr2: Attribute,
  attr3: Attribute,
  attr4: Attribute,
  attr5: Attribute,
  attr6: Attribute,
  calculationMethod?: string
): number => {
  if (attr1 && attr2) {
    const detailWeight = parseFloat(attr1.info.inputvalue);
    const stockWeight = parseFloat(attr2.info.inputvalue);
    let result = 0;
    if (stockWeight == 0) return 0;
    switch (calculationMethod) {
      case LENGTH:
        result = detailWeight / stockWeight;
        break;
      case AREA:
        result = detailWeight / stockWeight;
        break;
      case MASS:
        result = detailWeight / stockWeight;
        break;
      default:
        return 0;
    }

    if (!isNaN(result)) return parseFloat(result.toFixed(3));
    return 0;
  }
  return 0;
};

// слайд 29
export const calculateDetailMass = (attr1: Attribute, calculationMethod?: string): number => {
  if (attr1) {
    const result = parseFloat(attr1.info.inputvalue);
    if (!isNaN(result)) return parseFloat(result.toFixed(3)); // Math.round(result*1000)/1000;
  }
  return 0;
};

export const validateKitInstallAllowance = (
  attr1: Attribute,
  attr2: Attribute,
  calculationMethod?: string
): Validation => {
  const validation = {
    statusOK: true,
    message: ''
  };
  if (attr1 && attr2) {
    const maxGroupStockLength = parseFloat(attr1.info.inputvalue);
    const installAllowance = parseFloat(attr2.info.inputvalue);
    if (isNaN(maxGroupStockLength)) return validation;
    if (isNaN(installAllowance)) return validation;

    if (installAllowance >= maxGroupStockLength && maxGroupStockLength > 0) {
      validation.statusOK = false;
      validation.message = 'Припуск на установку больше чем длина групповой заготовки!';
    }
  }
  return validation;
};

export const validateKitMaxGroupStockLength = (
  attr1: Attribute,
  attr2: Attribute,
  calculationMethod?: string
): Validation => {
  const validation = {
    statusOK: true,
    message: ''
  };
  if (attr1 && attr2) {
    const maxGroupStockLength = parseFloat(attr1.info.inputvalue);
    const installAllowance = parseFloat(attr2.info.inputvalue);
    if (isNaN(maxGroupStockLength)) return validation;
    if (isNaN(installAllowance)) return validation;

    if (installAllowance >= maxGroupStockLength && maxGroupStockLength > 0) {
      validation.statusOK = false;
      validation.message = 'Припуск на установку больше чем длина групповой заготовки!';
    }
  }
  return validation;
};

// слайд 33
export const calculateMaxDetailCount = (
  attr1: Attribute,
  attr2: Attribute,
  attr3: Attribute,
  attr4: Attribute,
  calculationMethod?: string
): number => {
  // Расчет атрибута «Max кол-во деталей из групповой ​заготовки»
  // ​=(Max длина групповой заготовки – Припуск под ​установку​)/(Длина детали + Припуск на ​размер),​
  // округляем в меньшую сторону до целого числа.​
  if (attr1 && attr2 && attr3 && attr4) {
    const maxGroupLength = parseFloat(attr1.info.inputvalue);
    const installAllowance = parseFloat(attr2.info.inputvalue);
    const length = parseFloat(attr3.info.inputvalue);
    const allowance = parseFloat(attr4.info.inputvalue);
    if (maxGroupLength == 0) return 1;
    let firstSum = sum(maxGroupLength, multiply(-1, installAllowance));
    if (firstSum < 0) firstSum = 0;
    const secondSum = sum(length, allowance);
    if (secondSum == 0) return 0;
    return Math.floor(firstSum / secondSum);
  }
  return 0;
};

export const calculateMaxGroupStockLength = (
  attr1: Attribute,
  attr2: Attribute,
  attr3: Attribute,
  attr4: Attribute,
  calculationMethod?: string
): number => {
  // Расчет атрибута «Расч. max длина групповой ​заготовки» ​
  //= (Длина детали + Припуск на размер)* ​
  // Max кол-во деталей из групповой заготовки + ​Припуск под установку
  if (attr1 && attr2 && attr3 && attr4) {
    const sumFirst = sum(attr1.info.inputvalue, attr2.info.inputvalue);
    const mult = multiply(sumFirst, attr3.info.inputvalue);
    const sumSecond = sum(mult, attr4.info.inputvalue);
    return sumSecond;
  }
  return 0;
};

export const calculateVUsageCoeff = (attrs: Attributes): number => {
  const attrQuantity = getVContQuantityAttr(attrs);
  const attrUsageCoeff = attrs[ATTRIBUTE_V_USAGE_CONT_COEFF];
  if (attrUsageCoeff && attrQuantity) {
    return multiply(attrUsageCoeff.info.inputvalue, attrQuantity.info.inputvalue);
  }
  return 0;
};

export const editVUsageCoeff = (value: string, attrUsageCoeff: Attribute, attrs: Attributes): string => {
  const attrQuantity = getVContQuantityAttr(attrs);
  if (attrUsageCoeff && attrQuantity) {
    const result = parseFloat(value) / parseFloat(attrQuantity.info.inputvalue);
    return String(result);
  }
  return '';
};

export const getUnitsVContQuantity = (attrs: Attributes): string => {
  const attrQuantity = getVContQuantityAttr(attrs);
  if (attrQuantity?.info?.inputunit) {
    return attrQuantity.info.inputunit;
  }
  return '';
};

export const getVContQuantityAttr = (attrs: Attributes): Attribute => {
  if (attrs[ATTRIBUTE_V_CONT_QUANTITY_LENGTH]) return attrs[ATTRIBUTE_V_CONT_QUANTITY_LENGTH];
  if (attrs[ATTRIBUTE_V_CONT_QUANTITY_AREA]) return attrs[ATTRIBUTE_V_CONT_QUANTITY_AREA];
  if (attrs[ATTRIBUTE_V_CONT_QUANTITY_MASS]) return attrs[ATTRIBUTE_V_CONT_QUANTITY_MASS];
  if (attrs[ATTRIBUTE_V_CONT_QUANTITY_VOLUME]) return attrs[ATTRIBUTE_V_CONT_QUANTITY_VOLUME];
};
