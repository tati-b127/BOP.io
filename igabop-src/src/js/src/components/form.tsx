import React from 'react';
import AsyncSelect from 'react-select/async';
import CustomReactComponent from '@raos/widget-utils/lib/sharedComponents/customReactComponent';

import { ownershipOpts, selects, startOpts } from './consts';

import Select from 'react-select';
import '../styles/form.scss';

import { Option, Props, Route, State } from '../store/interfaces';

const person_search_service = 'resources/v2/e6w/service/PersonSearch';
const object_search_service = 'resources/v2/e6w/service/ObjectSearch';

const formatOption = (data: any) => {
  return data.map((i: any) => {
    return {
      value: i.physicalid,
      // label: i.attributes['Scheduled Completion Date'].value
      label: i.basics.name
    };
  });
};

const formatOptionDS = (data: any) => {
  return data.data.map((i: any) => {
    const el = i.dataelements;
    return {
      value: i.id,
      label: el.name
    };
  });
};

const formatOptionDSPerson = (data: any) => {
  return data.data.map((i: any) => {
    const el = i.dataelements;
    return {
      value: el.name,
      label: `${el.firstname} ${el.lastname}`
    };
  });
};

const filter = (data: Option[], inputValue: string) => {
  return data.filter(i => i.label.toLowerCase().includes(inputValue.toLowerCase()));
};

export default class Form extends CustomReactComponent<Props, State> {
  state = {
    inputValue: '',
    isClearable: true,
    isLoading: false,
    tasksOpts: [] as Option[],
    rtsOpts: [] as Option[],
    personsOpts: [] as Option[],
    task: {} as Option,
    rt: {} as Option,
    person: {} as Option,
    start: startOpts[0],
    ownership: ownershipOpts[1],
    url3dspace: this.widget.getValue('3DSpace'),
    isCreateDisabled: true,
    isAddContentDisabled: true,
    isDeleteContentDisabled: true
  };

  handleInputChange = (newValue: string) => {
    const inputValue = newValue;
    this.setState({ inputValue });
    return inputValue;
  };

  handleTaskChange = (value: Option) => this.setState({ task: value });

  handleRTChange = (value: Option) => this.setState({ rt: value });

  handlePersonChange = (value: Option) => this.setState({ person: value });

  handleStartChange = (value: Option) => this.setState({ start: value });

  handleOwnershipChange = (value: Option) => this.setState({ ownership: value });

  loadOptions = (inputValue: string, callback: Function) => {
    // this.Fetch(`${this.props.api}/tasks/my?selects=${selects}`, 'GET')
    this.Fetch2(`${this.state.url3dspace}/${object_search_service}?searchStr=*${inputValue}*&typeStr=Inbox Task`)
      .then((data: any) => {
        const tasksOpts = formatOptionDS(data);
        this.setState({ tasksOpts });
        callback(filter(this.state.tasksOpts, inputValue));
      })
      .catch(error => console.log(error));
  };

  loadRTOptions = (inputValue: string, callback: Function) => {
    this.Fetch(`${this.props.api}/tasks/routeTemplates?selects=${selects}`, 'GET')
      // this.Fetch2(`${this.state.url3dspace}/${object_search_service}?searchStr=*${inputValue}*&typeStr=Route Template`)
      .then((data: any) => {
        const rtsOpts = formatOption(data);
        this.setState({ rtsOpts });
        callback(filter(this.state.rtsOpts, inputValue));
      })
      .catch(error => console.log(error));
  };

  loadPersonOptions = (inputValue: string, callback: Function) => {
    this.Fetch2(`${this.state.url3dspace}/${person_search_service}?searchStr=*${inputValue}*`)
      .then((data: any) => {
        const personsOpts = formatOptionDSPerson(data);
        this.setState({ personsOpts });
        callback(filter(this.state.personsOpts, inputValue));
      })
      .catch(error => console.log(error));
  };

  onCreateRoute = () => {
    const url = `${this.props.api}/routes/createRoute?selects=${selects}`;
    const { selected } = this.props;
    const { task } = this.state;
    const { rt } = this.state;
    const { person } = this.state;
    const { start } = this.state;
    const { ownership } = this.state;
    const data = { selected, task, rt, person, start, ownership };

    if (!rt.value || !person.value) alert('You have to select Route Template and assignee to create Route');
    else {
      this.props.setIsLoading(true);
      this.Fetch(url, 'PUT', { data: JSON.stringify(data) })
        .then((data: any) => this.props.updateTableData(data, true))
        .catch(error => console.log(error))
        .finally(() => this.props.setIsLoading(false));
    }
  };

  onAddContent = () => {
    const url = `${this.props.api}/routes/addContent?selects=${selects}`;
    const selected = this.props.selected.filter(row => row.routes.length === 0);
    const states = selected.map(row => row.current.name);
    const route = this.props.selected.find(row => row.routes.length === 1).routes[0];
    const data = { selected, states, route };

    if (states.includes('RELEASED')) alert('Remove released objects from selection');
    else {
      this.props.setIsLoading(true);
      this.Fetch(url, 'PUT', { data: JSON.stringify(data) })
        .then((data: any) => this.props.updateTableData(data, true))
        .catch(error => console.log(error))
        .finally(() => this.props.setIsLoading(false));
    }
  };

  onDeleteContent = () => {
    const url = `${this.props.api}/routes/deleteContent?selects=${selects}`;
    const routes: string[] = [];
    const selectedWithRoute = this.props.selected.filter(row => row.routes.length !== 0);
    selectedWithRoute.forEach(obj => {
      if (!routes.find(routeId => routeId === obj.routes[0].physicalid)) {
        routes.push(obj.routes[0].physicalid);
      }
    });
    const data: { route: string; content: string[] }[] = [];
    routes.forEach(routeId => {
      const selected = selectedWithRoute
        .filter(row => row.routes[0].physicalid === routeId)
        .map(object => object.physicalid);
      data.push({ route: routeId, content: selected });
    });

    this.props.setIsLoading(true);
    this.Fetch(url, 'DELETE', { data: JSON.stringify(data) })
      .then((data: any) => this.props.updateTableData(data, true))
      .catch(error => console.log(error))
      .finally(() => this.props.setIsLoading(false));
  };

  componentDidUpdate(prevProps: Readonly<Props>, prevState: Readonly<State>, snapshot?: any): void {
    if (prevProps.selected !== this.props.selected) {
      const routes = this.props.selected.map(row => row.routes).filter(routes => routes.length > 0);
      const isCreateDisabled = !(this.props.selected.length > 0 && routes.length === 0);
      const isAddContentDisabled = !(this.props.selected.length > 1 && routes.length === 1);
      const isDeleteContentDisabled = !(this.props.selected.length >= 1 && routes.length >= 1);
      this.setState({ isCreateDisabled, isAddContentDisabled, isDeleteContentDisabled });
    }
  }

  render() {
    const {
      isClearable,
      isLoading,
      isCreateDisabled,
      isAddContentDisabled,
      isDeleteContentDisabled,
      start,
      ownership
    } = this.state;
    return (
      <div className="form-container">
        <div className="buttons-container">
          <button onClick={this.onCreateRoute} disabled={isCreateDisabled}>
            Создать маршрут
          </button>
          <button onClick={this.onAddContent} disabled={isAddContentDisabled}>
            Добавить контент
          </button>
          <button onClick={this.onDeleteContent} disabled={isDeleteContentDisabled}>
            Удалить контент
          </button>
        </div>
        <div className="fieldset-container">
          <div className="fieldset-label">Задача</div>
          <AsyncSelect
            cacheOptions
            loadOptions={this.loadOptions}
            onInputChange={this.handleInputChange}
            onChange={this.handleTaskChange}
            isClearable={isClearable}
          />
        </div>
        <div className="fieldset-container">
          <div className="fieldset-label">Шаблон маршрута</div>
          <AsyncSelect
            cacheOptions
            loadOptions={this.loadRTOptions}
            onInputChange={this.handleInputChange}
            onChange={this.handleRTChange}
            isClearable={isClearable}
          />
        </div>
        <div className="fieldset-container">
          <div className="fieldset-label">Исполнитель первой задачи</div>
          <AsyncSelect
            cacheOptions
            loadOptions={this.loadPersonOptions}
            onInputChange={this.handleInputChange}
            onChange={this.handlePersonChange}
            isClearable={isClearable}
          />
        </div>
        <div className="fieldset-container">
          <div className="fieldset-label">Старт</div>
          <Select
            className="select-item"
            classNamePrefix="select"
            defaultValue={start}
            name="start"
            options={startOpts}
            onChange={this.handleStartChange}
          />
        </div>
        <div className="fieldset-container">
          <div className="fieldset-label">Передать права на маршрут</div>
          <Select
            className="select-item"
            classNamePrefix="select"
            defaultValue={ownership}
            name="ownership"
            options={ownershipOpts}
            onChange={this.handleOwnershipChange}
          />
        </div>
      </div>
    );
  }

  // todo remove it
  Fetch2(url: string) {
    const headers = { 'Content-Type': 'application/json' };
    this.setState({ isLoading: true });
    return new Promise((fulfill, reject) => {
      const promiseObject = {
        method: 'GET',
        type: 'json',
        onComplete: (data: any) => {
          fulfill(data);
          this.setState({ isLoading: false });
        },
        onFailure: (error: any, message: any) => {
          reject({ error, message });
          this.setState({ isLoading: false });
        },
        headers,
        timeout: 300000
      };
      window.widget.WAFData.authenticatedRequest(encodeURI(url), promiseObject);
    });
  }
}
