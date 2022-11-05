let mainRoute = "http://localhost:8080/api/";

/**
 * Getter de la lista de objetos a partir del parametro de ruta especificado.
 * @param obj --> Parametro para completar la ruta, "devices", "devicetypes", "homes", "rooms", "routines"
 * @returns {Promise<any>}
 */
export async function getObject(obj){
    const objs = await fetch(mainRoute +  obj, {
        method: 'GET',
        headers: {
            'content-type': 'application/json; charset=utf-8',
        },
        responseType: "json"
    }).then(res => res.json()).catch(e => console.log(e));
    return objs;
}

export async function createObject(obj, data){
    const objs = await fetch(mainRoute +  obj, {
        method: 'POST',
        headers: {
            'content-type': 'application/json; charset=utf-8',
        },
     responseType: "json",
        body: JSON.stringify(data)
    }).then(res => res.json()).catch(e => console.log(e));
    return objs;
}

export async function createObjectWithoutData(obj){
    const objs = await fetch(mainRoute +  obj, {
        method: 'POST',
        headers: {
            'content-type': 'application/json; charset=utf-8',
        },
        responseType: "json",
    }).then(res => res.json()).catch(e => console.log(e));
    return objs;
}

/**
 * Getter de un objeto por el id y la ruta
 * @param obj es la ruta que se usa
 * @param id del objeto
 * @returns {Promise<any | void>}
 */
export async function getObjectById(obj, id){
    const objs = await fetch(mainRoute +  obj + "/"  + id, {
        method: 'GET',
        headers: {
            'content-type': 'application/json; charset=utf-8',
        },
        responseType: "json"
    }).then(res => res.json()).catch(e => console.log(e));
    return objs;
}

/**
 * Getter de todos los rooms de una casa.
 * @param id de la casa
 * @returns {Promise<any>}
 */
export async function getRoomsForHouse(id){
    const objs = await fetch(mainRoute + "homes/" +  id + "/rooms", {
        method: 'GET',
        headers: {
            'content-type': 'application/json; charset=utf-8',
        },
        responseType: "json"
    }).then(res => res.json());
    return objs
}

/**
 * Getter para todos los devices favoritos
 * @returns {Promise<Array>}
 */
export async function getFavoriteDevices(){
    const objs = (await getObject("devices"))["devices"];

    let filteredObjs = []

    for (let i = 0; i < objs.length; i++){
        if (objs[i].meta.fav === true){
            filteredObjs.push(objs[i])
        }
    }
    return filteredObjs
}

/**
 * Getter de todos los devices de un room.
 * @param id del room
 * @returns {Promise<any>}
 */
export async function getDevicesForRoom(id){
    const objs = await fetch(mainRoute + "rooms/" +  id + "/devices", {
        method: 'GET',
        headers: {
            'content-type': 'application/json; charset=utf-8',
        },
        responseType: "json"
    }).then(res => res.json());
    return objs;
}

/**
 * Hace una actualizacion en la BD para una accion
 * @param id del device
 * @param action nombre de la accion
 * @returns {Promise<string>}
 */
export async function performAction(id, action){
    fetch(mainRoute + "devices/" +  id + "/" + action, {
        method: 'PUT',
        headers: {
            'content-type': 'application/json; charset=utf-8',
        },
        responseType: "json"
    });
    return '';
}

/**
 * Hace una actualizacion en la BD para una acción que tiene parametros por valor
 * @param id del device
 * @param action nombre de la accion
 * @param value de la accion
 * @returns {Promise<string>}
 */
export async function performActionWithValue(id, action, value){
    fetch(mainRoute + "devices/" +  id + "/" + action, {
        method: 'PUT',
        headers: {
            'content-type': 'application/json; charset=utf-8',
        },
        responseType: "json",
        body: JSON.stringify(value)
    });
    return '';
}

/**
 * Actualiza informacion del device
 * @param obj ruta del objeto
 * @param id del device
 * @param data a actualizar
 * @returns {Promise<string>}
 */
export async function updateObjInfo(obj, id, data){
    await fetch(mainRoute + obj +  id , {
        method: 'PUT',
        headers: {
            'content-type': 'application/json; charset=utf-8',
        },
        responseType: "json",
        body: JSON.stringify(data)
    });
    return '';
}

export async function removeDeviceFromRoom(id){
    await fetch(mainRoute + "rooms/devices/" +  id , {
        method: 'DELETE',
        headers: {
            'content-type': 'application/json; charset=utf-8',
        },
        responseType: "json"
    });
    return '';
}

export async function deleteObj(obj, id){
    await fetch(mainRoute + obj +  id , {
        method: 'DELETE',
        headers: {
            'content-type': 'application/json; charset=utf-8',
        },
        responseType: "json"
    });
    return '';
}

/**
 * Ejecuta una rutina en base al id
 * @param id de la rutina
 * @returns {Promise<string>}
 */
export async function performRoutine(id){
    const result = fetch(mainRoute + "routines/" +  id + "/execute", {
        method: 'PUT',
        headers: {
            'content-type': 'application/json; charset=utf-8',
        },
        responseType: "json"
    }).then(res => res.json()).catch(e => console.log(e));
    return result;
}
