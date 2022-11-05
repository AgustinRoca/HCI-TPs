const typesNotIncluded = ["speaker", "alarm"]
const actionNameMap = {};

export function getActionNameMap(){
    actionNameMap['setVolume'] = 'Setear Volumen';
    actionNameMap['play'] = 'Reproducir';
    actionNameMap['stop'] = 'Detener';
    actionNameMap['pause'] = 'Pausar';
    actionNameMap['resume'] = 'Resumir';
    actionNameMap['nextSong'] = 'Siguiente Cancion';
    actionNameMap['previousSong'] = 'Cancion Anterior';
    actionNameMap['setGenre'] = 'Cambiar Genero';
    actionNameMap['getPlaylist'] = 'Obtener Playlist';
    actionNameMap['open'] = 'Abrir';
    actionNameMap['close'] = 'Cerrar';
    actionNameMap['turnOn'] = 'Encender';
    actionNameMap['turnOff'] = 'Apagar';
    actionNameMap['setColor'] = 'Cambiar Color';
    actionNameMap['setBrightness'] = 'Cambiar Brillo';
    actionNameMap['setTemperature'] = 'Cambiar Temperatura';
    actionNameMap['setHeat'] = 'Cambiar Calor';
    actionNameMap['setGrill'] = 'Cambiar Grill';
    actionNameMap['setConvection'] = 'Cambiar Conveccion';
    actionNameMap['setMode'] = 'Cambiar Modo';
    actionNameMap['setVerticalSwing'] = 'Cambiar Swing Vertical';
    actionNameMap['setHorizontalSwing'] = 'Cambiar Swing Horizontal';
    actionNameMap['setFanSpeed'] = 'Cambiar Velocidad';
    actionNameMap['lock'] = 'Bloquear';
    actionNameMap['unlock'] = 'Desbloquear';
    actionNameMap['dock'] = 'Atracar';
    actionNameMap['start'] = 'Empezar';
    actionNameMap['setLocation'] = 'Establecer localizacion';
    actionNameMap['setFreezerTemperature'] = 'Cambiar Temperatura de Freezer';

    return actionNameMap;
}

export function getErrorByCode(code){
    if (code === 1){
        return "Los datos son inválidos";
    } else if (code === 2){
        return "El dato está restringido";
    } else if (code === 3){
        return "Error en la base de datos"
    } else if (code === 4){
        return "Error inseperado";
    } else {
        return "Error desconocido";
    }
}

export function handleResponse(response){
    let snack = {};
    if (response.hasOwnProperty('error')) {
        if (response.error.hasOwnProperty('code')) {
            snack.text = getErrorByCode(response.error.code);
            snack.color = "red darken-2";
        } else {
            snack.text = "Ocurrió un error en la operación";
            snack.color = "red darken-2";
        }
    } else {
        snack.text = "Operación exitosa";
        snack.color = "indigo darken-1";
    }
    return snack;
}

export function observerArrayToArray(obs){
    let arr = [];
    for (let i = 0; i < obs.length; i++){
        arr.push(obs[i]);
    }
    return arr;
}

export function getUnsupportedDevices(){
    return typesNotIncluded;
}

export function devicesToDic(devices){
    let dic = {};
    for (let i = 0; i < devices.length; i++){
        dic[devices[i].id] = devices[i];
    }
    return dic;
}

export function typesToPairs(types){
    let result = [];
    for (let i = 0; i < types.length; i++){
        if (!typesNotIncluded.includes(types[i].name)){
            result.push({'id':types[i].id, 'name':types[i].name})
        }
    }
    return result
}

export function typesToNames(types){
    let result = [];
    for (let i = 0; i < types.length; i++){
        result.push(types[i].name)
    }
    return result
}

export function typesToDic(types){
    let result = {};
    for (let i = 0; i < types.length; i++){
        result[types[i].name] = types[i].id
    }
    return result
}

export function cleanDictionary(dic){
    let result = {}
    for (let key in dic){
        if (dic.hasOwnProperty(key) && typeof dic[key] !== 'undefined'){
            result[key] = dic[key];
        }
    }
    return result;
}

export function dictionaryValuesToArray(dic){
    let result = [];
    for (let key in dic){
        if (dic.hasOwnProperty(key)){
            result.push(dic[key]);
        }
    }
    return result;
}

export function deviceArrayToNamedDictionary(arr){
    let result = {};
    for (let i = 0; i < arr.length; i++){
        result[arr[i].name] = arr[i];
    }
    return result;
}

export function dictionaryKeysToArray(dic){
    let result = [];
    for (let key in dic){
        result.push(key)
    }
    return result
}

export function getUniqueDevicesFromArray(arr){
    let aux = {};
    let result = [];
    for (let i = 0; i < arr.length; i++){
        aux[arr[i].name] = arr[i];
    }
    for (let key in aux){
        if (aux.hasOwnProperty(key)){
            result.push(aux[key])
        }
    }
    return result;
}

export function removeById(arr, id){
    let result = [];
    for (let i = 0; i < arr.length; i++){
        if (arr[i].id !== id){
            result.push(arr[i])
        }
    }
    return result;
}
