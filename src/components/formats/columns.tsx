import React from 'react';
import { Column, RowInfo } from 'react-table';

import * as styled from './columns.style';
import '../../styles/treegrid.scss';

import { ColumnShape } from 'react-base-table';

import { ATTRIBUTE_V_WIINSTRUCTION_TEXT, ATTRIBUTE_KIT_PREP_TIME, ATTRIBUTE_KIT_TPCS, mbom_types } from '../consts';
import { TreeObj } from '../../store/interfaces';

export const consumedResourcesColumns: Array<Column> = [
  {
    Header: 'Заголовок',
    accessor: 'name',
    Cell: (rowInfo: RowInfo) => {
      let typeIconURL = '';
      const icon = rowInfo.original.info['type.property[IPML.IconName].value'];
      if (icon) typeIconURL = require(`../../images/icons/${icon}.png`);
      const title = rowInfo.original.attributes['PLMEntity.V_Name'].value;
      const desc = rowInfo.original.attributes['PLMEntity.V_description'].value;
      return (
        <div className="res-cell">
          <img src={typeIconURL} />
          <div>{`${title} ${desc}`}</div>
        </div>
      );
    }
  },
  {
    Header: 'Рев.',
    accessor: 'revision',
    Cell: (rowInfo: RowInfo) => {
      return <div>{rowInfo.original.basics.revision}</div>;
    },
    width: 50
  },
  {
    Header: 'Статус',
    accessor: 'status',
    Cell: (rowInfo: RowInfo) => {
      return <div>{rowInfo.original.basics.current.displayName}</div>;
    },
    width: 70
  }
];

export const capableResourcesColumns: Array<Column> = [
  {
    Header: 'Название ресурса',
    accessor: 'name',
    Cell: (rowInfo: RowInfo) => {
      let typeIconURL = '';
      const icon = rowInfo.original.info['type.property[IPML.IconName].value'];
      if (icon) typeIconURL = require(`../../images/icons/${icon}.png`);
      return (
        <div className="res-cell">
          <img src={typeIconURL} />
          <span>
            {`${rowInfo.original.attributes['PLMEntity.V_Name'].value}${
              rowInfo.original.attributes['PLMEntity.V_description'].value
                ? ` ${rowInfo.original.attributes['PLMEntity.V_description'].value}`
                : ''
            }`}
          </span>
        </div>
      );
    }
  },
  {
    Header: 'Доступное количество',
    accessor: 'id',
    Cell: (rowInfo: RowInfo) => {
      return <div>{rowInfo.original.attributes['DELAsmProcessCanUseCnx.V_ResourcesQuantity'].value}</div>;
    }
  }
];

export const attachmentsResourcesColumns: Array<Column> = [
  {
    Header: 'Заголовок',
    accessor: 'fileName',
    Cell: (rowInfo: RowInfo) => {
      let typeIconURL = '';
      const icon = rowInfo.original.info['type.property[IPML.IconName].value'];
      if (icon) typeIconURL = require(`../../images/icons/${icon}.png`);
      return (
        <div className="res-cell">
          <img src={typeIconURL} />
          <div>{rowInfo.original.attributes.Title.value}</div>
        </div>
      );
    }
  },
  {
    Header: 'Название',
    accessor: 'name',
    Cell: (rowInfo: RowInfo) => {
      return <div>{rowInfo.original.basics.name}</div>;
    }
  }
];

export const bopColumns: ColumnShape[] = [
  {
    key: 'current',
    dataKey: 'current.displayName',
    title: 'Статус',
    resizable: true,
    frozen: false,
    width: 60,
    minWidth: 20,
    hidden: false
  },
  {
    key: 'reservedby',
    dataKey: 'reservedby',
    title: 'Блокировка',
    resizable: true,
    frozen: false,
    width: 70,
    minWidth: 20,
    hidden: false
  },
  {
    key: 'revision',
    dataKey: 'revision',
    title: 'Рев.',
    resizable: true,
    frozen: false,
    width: 30,
    minWidth: 20,
    hidden: false
  },
  {
    key: 'assignmentStatus',
    dataKey: 'assignmentStatus',
    title: 'Статус назначения',
    resizable: true,
    frozen: false,
    width: 30,
    minWidth: 20,
    hidden: false,
    format: 'attachment',
    cellRenderer: (data: any) => {
      const { rowData } = data;
      if (mbom_types.includes(rowData?.type)) {
        return (
          <styled.AssignmentIndicatorCell>
            {Boolean(rowData?.info?.indicationSetBop) && <styled.AssignmentIndicator />}
          </styled.AssignmentIndicatorCell>
        );
      }
      return null;
    }
  },
  {
    key: 'title',
    dataKey: 'title',
    title: 'Наименование',
    resizable: true,
    frozen: false,
    width: 500,
    minWidth: 20,
    hidden: false,
    format: 'title',
    attribute: ATTRIBUTE_V_WIINSTRUCTION_TEXT
  },
  {
    key: 'attachment',
    dataKey: 'docs',
    title: 'Вложение',
    resizable: true,
    frozen: false,
    width: 156,
    minWidth: 20,
    hidden: false,
    format: 'attachment'
  },
  {
    key: 'routes',
    dataKey: 'routes',
    title: 'Маршрут',
    resizable: true,
    frozen: false,
    width: 96,
    minWidth: 20,
    hidden: false,
    format: 'routes'
  },
  {
    key: 'routesOwner',
    dataKey: 'routes',
    title: 'Владелец Маршрута',
    resizable: true,
    frozen: false,
    width: 70,
    minWidth: 20,
    hidden: false,
    format: 'routesOwner'
  },
  {
    key: 'routesStatus',
    dataKey: 'routes',
    title: 'Статус Маршрута',
    resizable: true,
    frozen: false,
    width: 70,
    minWidth: 20,
    hidden: false,
    format: 'routesStatus'
  },
  {
    key: 'owner',
    dataKey: 'owner',
    title: 'Разработал',
    resizable: true,
    frozen: false,
    width: 70,
    minWidth: 20,
    hidden: false
  },
  {
    key: 'Kit_PrepTime',
    dataKey: 'Kit_PrepTime',
    title: 'Тпз',
    resizable: true,
    frozen: false,
    width: 40,
    minWidth: 20,
    hidden: false,
    format: 'attribute',
    attribute: ATTRIBUTE_KIT_PREP_TIME
  },
  {
    key: 'Kit_Tpcs',
    dataKey: 'Kit_Tpcs',
    title: 'Тшт',
    resizable: true,
    frozen: false,
    width: 40,
    minWidth: 20,
    hidden: false,
    format: 'attribute',
    attribute: ATTRIBUTE_KIT_TPCS
  },
  {
    key: 'originated',
    dataKey: 'originated',
    title: 'Дата создания',
    resizable: true,
    frozen: false,
    width: 75,
    minWidth: 20,
    hidden: false,
    format: 'date'
  },
  {
    key: 'modified',
    dataKey: 'modified',
    title: 'Дата модификации',
    resizable: true,
    frozen: false,
    width: 75,
    minWidth: 20,
    hidden: false,
    format: 'date'
  },
  {
    key: 'name',
    dataKey: 'name',
    title: 'Имя',
    resizable: true,
    frozen: false,
    width: 75,
    minWidth: 20,
    hidden: false
  },
  {
    key: 'type',
    dataKey: 'type',
    title: 'Тип',
    resizable: true,
    frozen: false,
    width: 75,
    minWidth: 20,
    hidden: false
  },
  {
    key: 'physicalid',
    dataKey: 'physicalid',
    title: 'Идентификатор',
    resizable: true,
    frozen: false,
    width: 75,
    minWidth: 20,
    hidden: false
  }
];
