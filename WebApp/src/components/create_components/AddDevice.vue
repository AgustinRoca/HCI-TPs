<template>
    <CreateOverlay title="Agregar Dispositivo" ref="createOverlay" @onCloseModal="" @onCreate="addDevice">
        <v-autocomplete
            v-model="device_name"
            label="Dispositivos"
            :items="existing_devices"
            ref="something"
        ></v-autocomplete>
    </CreateOverlay>
</template>

<script>
    import CreateOverlay from './CreateOverlay'
    const api = require('../../js/api')
    const utils = require('../../js/utils')

    export default {
        name: 'AddDevice',
        components: { CreateOverlay },
        props:{
            isRoutine:{
                type: Boolean,
                default: false
            }
        },
        methods: {
            displayAddDevice: function () {
                this.$refs.createOverlay.display()
            },
            clearAddDevice(){
                this.device_name = "";
            },
            addDevice : function () {
                // Consigue el dispositivo elegido
                let device = this.device_name_to_device[this.device_name];

                if (device !== undefined){
                    //api.createObject("devices", {'type':{'id':this.device_key_mappings[this.device.type]}, 'name':this.device.name, 'meta':{'fav':false}})
                    this.clearAddDevice();
                    this.$emit("onAddDevice", Object.assign({}, device));
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
                this.existing_devices = utils.dictionaryKeysToArray(this.device_name_to_device);
            }
        },
        device_name_to_device: {},
        device_name: "",
        data () {
            return {
                existing_devices: [],
                device_name_to_device: {},
                device_name: "",
            }

        },
        created () {
            this.getDevices();
        }
    }
</script>

<style scoped>

</style>
