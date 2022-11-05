<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">
    <CreateOverlay title="Agregar Accion" ref="createOverlay" @onCloseModal="cleanAction" @onCreate="addAction">
        <!--Autocomplete para el dispositivo-->
        <h2>Elegir Dispositivo</h2>
        <v-autocomplete
            v-model="selectedDeviceName"
            label="Dispositivos"
            :items="existingDevicesNames"
            ref="dispositivos"
            v-on:change="deviceChanged"
        ></v-autocomplete>
        <h2>Elegir Accion</h2>
        <v-autocomplete
            v-model="selectedActionName"
            label="Acciones"
            :items="actionsForDevice"
            ref="acciones"
            v-on:change="actionChanged"
        ></v-autocomplete>
        <h2 v-if="action.needValue">Elegir Valor</h2>
        <v-autocomplete
            v-if="action.hasOptions && action.needValue"
            v-model="action.value"
            label="Valor"
            :items="action.supportedValues"
            ref="something"
            v-on:change="valueChanged"
        ></v-autocomplete>
        <v-slider
            v-else-if="action.hasValue && action.needValue"
            v-model="action.value"
            class="align-center"
            :min="action.min"
            :max="action.max"
            hide-details
            style="width: 100%"
            v-on:change="valueChanged">
            <template v-slot:append>
                <v-text-field
                    v-model="action.value"
                    class="mt-0 pt-0"
                    hide-details
                    single-line
                    type="number"
                    style="width: 5%"
                    v-on:input="valueChanged"
                ></v-text-field>
            </template>
        </v-slider>
        <v-color-picker
            v-else-if="this.action.hasColor && action.needValue"
            v-model="this.action.value"
            style="width: 100%"
            v-on:input="valueChanged">
        </v-color-picker>
        <v-autocomplete
            v-if="action.hasRoom && action.needValue"
            v-model="action.value"
            label="Valor"
            :items="rooms"
            ref="something"
            v-on:change="valueChanged"
        ></v-autocomplete>

    </CreateOverlay>
</template>

<script>
    import CreateOverlay from './CreateOverlay'
    const api = require('../../js/api')
    const utils = require('../../js/utils')

    export default {
        name: 'AddAction',
        components: { CreateOverlay },
        props: {

        },
        methods: {
            displayOverlay: function () {
                this.getAllData();
                this.cleanAction();
                this.$refs.createOverlay.display()
                this.$refs.createOverlay.setCreateVisibility(false);
            },
            async getAllData() {
                // Getter de todos los dispositivos
                this.existingDevices = (await api.getObject("devices")).devices;
                // Lo pasa a los nombres solo
                for (let i = 0; i < this.existingDevices.length; i++){
                    this.existingDevicesNames.push(this.existingDevices[i].name)
                }
                // Getter de todos los tipos de dispositivos
                let devicetypes = (await api.getObject("devicetypes")).result;
                // Getter de los dispositivos no soportados
                let unsupportedDevices = utils.getUnsupportedDevices();
                // Recupera las acciones de los dispositivos soportados
                for (let i = 0; i < devicetypes.length; i++){
                    if (!unsupportedDevices.includes(devicetypes[i].name)){
                        this.actions_per_device_type[devicetypes[i].id] = devicetypes[i].actions;
                    }
                }
                // Getter de todos los cuartos
                let auxRooms = (await api.getObject("rooms")).result;
                for (let i = 0; i < auxRooms.length; i++){
                    this.rooms.push(auxRooms[i].name);
                    this.roomNamesToIds[auxRooms[i].name] = auxRooms[i].id;
                }
            },
            deviceChanged(){
                this.selectedDevice = this.getDevice(this.selectedDeviceName);
                this.actionsForDevice = [];
                this.selectedActionName = "";
                this.selectedAction = {};
                this.action.needValue = false;
                for (let i = 0; i < this.actions_per_device_type[this.selectedDevice.type.id].length; i++){
                    let action = (this.actions_per_device_type[this.selectedDevice.type.id])[i];
                    this.actionsForDevice.push(action.name);
                }
                this.$refs.createOverlay.setCreateVisibility(false);
            },
            valueChanged(){
                this.$refs.createOverlay.setCreateVisibility(true);
            },
            getDevice(name){
                for (let i = 0; i < this.existingDevices.length; i++){
                    if (this.existingDevices[i].name === name){
                        return this.existingDevices[i];
                    }
                }
                return null;
            },
            actionChanged(){
                this.selectedAction = this.getAction(this.selectedActionName);

                if (this.selectedAction.params[0] !== undefined){
                    if (this.selectedAction.name.includes("Location")){
                        this.action.hasValue = false;
                        this.action.needValue = true;
                        this.action.hasOptions = false;
                        this.action.hasColor = false;
                        this.action.hasRoom = true;
                        this.action.value = "";
                    } else if (this.selectedAction.params[0].hasOwnProperty("supportedValues")){
                        this.action.supportedValues = this.selectedAction.params[0].supportedValues;
                        this.action.hasValue = false;
                        this.action.needValue = true;
                        this.action.hasOptions = true;
                        this.action.hasColor = false;
                        this.action.hasRoom = false;
                        this.action.value = "";
                    } else if (this.selectedAction.params[0].hasOwnProperty("type")){
                        if (this.selectedAction.params[0].type === "integer" || this.selectedAction.params[0].type === "number"){
                            this.action.min = this.selectedAction.params[0].minValue;
                            this.action.max = this.selectedAction.params[0].maxValue;
                            this.action.hasValue = true;
                            this.action.hasOptions = false;
                            this.action.needValue = true;
                            this.action.hasRoom = false;
                            this.action.hasColor = false;
                            this.action.value = "";
                        } else if (this.selectedAction.params[0].type === "string"){
                            this.action.hasColor = true;
                            this.action.hasOptions = false;
                            this.action.hasRoom = false;
                            this.action.hasValue = false;
                            this.action.needValue = true;
                            this.action.value = "";
                        }
                    }  else if (this.selectedAction.params[0].hasOwnProperty("minValue")){
                        this.action.min = this.selectedAction.params[0].minValue;
                        this.action.max = this.selectedAction.params[0].maxValue;
                        this.action.hasValue = true;
                        this.action.hasOptions = false;
                        this.action.hasRoom = false;
                        this.action.needValue = true;
                        this.action.hasColor = false;
                        this.action.value = "";
                    } else {
                        this.action.needValue = false;
                        this.action.hasOptions = false;
                        this.action.hasRoom = false;
                        this.action.hasValue = false;
                        this.action.hasColor = false;
                        this.action.value = "";
                    }
                } else {
                    this.action.needValue = false;
                    this.action.hasOptions = false;
                    this.action.hasValue = false;
                    this.action.hasRoom = false;
                    this.action.hasColor = false;
                    this.action.value = "";
                }

                this.$refs.createOverlay.setCreateVisibility(!this.action.needValue);
            },
            getAction(name){
                for (let i = 0; i < this.actions_per_device_type[this.selectedDevice.type.id].length; i++){
                    let action = (this.actions_per_device_type[this.selectedDevice.type.id])[i];
                    if (action.name === name){
                        return action;
                    }
                }
                return null;
            },
            cleanAction(){
                this.$refs.createOverlay.setCreateVisibility(false);
                this.action.needValue = false;
                this.selectedDeviceName = "";
                this.selectedActionName = "";
                this.selectedDevice = {};
                this.selectedAction = {};
                this.action.value = "";
            },
            addAction : function () {
                if (this.action.hasRoom && this.action.needValue){
                    this.action.value = this.roomNamesToIds[this.action.value];
                }
                if (this.action.value === ""){
                    this.$emit("onAddConfig", this.selectedDevice.id, this.selectedDevice.name, this.selectedAction.name, []);
                } else {
                    this.$emit("onAddConfig", this.selectedDevice.id, this.selectedDevice.name, this.selectedAction.name, [this.action.value]);
                }
            },
            async getDevices(){
                // Recupera los dispositivos de la API
                let devices = (await api.getObject("devices")).devices;
                // Los pasa a un diccionario
                let deviceDic = utils.devicesToDic(devices);

                if (!this.isRoutine){
                    // Recupera los cuartos de la API
                    const rooms = (await api.getObject("rooms")).result;

                    // Itera por los cuartos consiguiendo los devices que estan en cada uno y marca como undefined
                    for (let i = 0; i < rooms.length; i++){
                        let devicesInRoom = (await api.getDevicesForRoom(rooms[i].id)).result;
                        for (let j = 0; j < devicesInRoom.length; j++){
                            deviceDic[devicesInRoom[j].id] = undefined;
                        }
                    }

                    // Limpia los undefined que se pusieron para que sea mas performante, en vez de hacer delete
                    deviceDic = utils.cleanDictionary(deviceDic);
                }

                // Lleva el diccionario a un array de devices disponibles
                devices = utils.dictionaryValuesToArray(deviceDic);
                // Lleva el array a un diccionario indexado por el nombre del dispositivo
                this.device_name_to_device = utils.deviceArrayToNamedDictionary(devices);
                // Lleva el diccionario de nombres a un array de nombres
                this.existingDevices = utils.dictionaryKeysToArray(this.device_name_to_device);
            }
        },
        device_name_to_device: {},
        data () {
            return {
                existingDevices: [],
                existingDevicesNames: [],
                actionsForDevice: [],
                actions_per_device_type: {},
                selectedDeviceName: "",
                selectedActionName: "",
                selectedAction: {},
                selectedDevice: {},
                device_name_to_device: {},
                rooms:[],
                roomNamesToIds: [],
                action: {
                    needValue: false,
                    hasOptions: false,
                    hasValue: false,
                    hasRoom: false,
                    hasColor: false,
                    supportedValues: [],
                    min: 0,
                    max: 0,
                    value: null
                }
            }

        },
        created () {
            //this.getAllData();
            //this.getDevices();
        }
    }
</script>

<style scoped>

</style>
