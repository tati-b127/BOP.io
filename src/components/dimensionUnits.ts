import {
    AREA,
    DIM_GRAM,
    DIM_KILOGRAM,
    DIM_LITER,
    DIM_METER,
    DIM_METER2,
    DIM_METER3,
    DIM_MG,
    DIM_MM,
    DIM_MM2,
    LENGTH,
    MASS,
    VOLUME
} from './consts';

import {multiply, sum} from '../utils/mathUtils';
import {ConversionUnits} from '../store/interfaces';

export const units: ConversionUnits = {
    [DIM_METER]: {
        label: "m",
        multiplier: 1.0,
        offset: 0.0,
        type: LENGTH
    },
    [DIM_MM]: {
        label: "mm",
        multiplier: 0.001,
        offset: 0.0,
        type: LENGTH
    },
    [DIM_GRAM]: {
        label: "g",
        multiplier: 0.001,
        offset: 0.0,
        type: MASS
    },
    [DIM_KILOGRAM]: {
        label: "kg",
        multiplier: 1.0,
        offset: 0.0,
        type: MASS
    },
    [DIM_MG]: {
        label: "mg",
        multiplier: 0.000001,
        offset: 0.0,
        type: MASS
    },
    [DIM_METER2]: {
        label: "m2",
        multiplier: 1,
        offset: 0.0,
        type: AREA
    },
    [DIM_MM2]: {
        label: "mm2",
        multiplier: 0.000001,
        offset: 0.0,
        type: AREA
    },
    [DIM_METER3]: {
        label: "m3",
        multiplier: 1.0,
        offset: 0.0,
        type: VOLUME
    },
    // [DIM_MM3]: {
    //     label: "mm3",
    //     multiplier: 0.000000001,
    //     offset: 0.0,
    //     type: VOLUME
    // },
    [DIM_LITER]: {
        label: "l",
        multiplier: 0.001,
        offset: 0.0,
        type: VOLUME
    }
}

export const convert = (value: number, fromUnit: string, toUnit: string): number => {
    if(fromUnit==='' || toUnit==='' || fromUnit===undefined || toUnit===undefined){
        return value;
    }
    const fromMultiplier = units[fromUnit].multiplier;
    const fromOffset = units[fromUnit].offset;
    const toMultiplier = units[toUnit].multiplier;
    const toOffset = units[toUnit].offset;

    const valueAddFromMultiplier = multiply(value, fromMultiplier);
    const valueAddFromOffset = sum(valueAddFromMultiplier, fromOffset);
    const valueDivideToMultiplier = multiply(valueAddFromOffset, 1/toMultiplier);
    const valueAddToOffset = sum(valueDivideToMultiplier, toOffset);
    return valueAddToOffset;
}