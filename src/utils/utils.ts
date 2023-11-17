export const dragData = (
	widgetId: string,
	itemArray: {
		id: string;
		objectType: string;
		objectTitle: string;
		parentId: string;
		parentConnectionId: string;
		path: string;
	}[]
) => {
	let items: any[] = [];
	itemArray.forEach(item => {
		items.push({
			objectId: item.id,
			objectType: item.objectType,
			serviceId: '3DSpace',
			envId: 'OnPremise',
			contextId: '',
			displayName: item.objectTitle,
			displayType: item.objectType,
			parentId: item.parentId,
			parentConnectionId: item.parentConnectionId,
			path: item.path,
		});
	});
	return {
		protocol: '3DXContent',
		version: '1.1',
		source: widgetId,
		widgetId: widgetId,
		data: {
			items: items
		}
	};
};

export const GetLocaleDate = (language: string, date: Date, param: boolean) => {
	let day = '';
	let dateDay = date.getDate();
	if (dateDay < 10) {
		day = `0${dateDay}`;
	} else {
		day = `${dateDay}`;
	}
	let month = '';
	let dateMonth = date.getMonth() + 1;
	if (dateMonth < 10) {
		month = `0${dateMonth}`;
	} else {
		month = `${dateMonth}`;
	}
	const year = date.getFullYear();
	if (language === 'ru') {
		return `${dateDay}.${dateMonth}.${year}`;
	} else {
		return `${month}/${day}/${year}`;
	}
};
