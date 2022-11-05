<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">
    <InformationOverlay v-bind:title="this.name" subtitle="Dispositivos" ref="infoOverlay" @onCloseModal="clearData" @onSave="updateInfo"
                        @onAddDevice="addDevice" @onDelete="deleteObj" @onCreate="addDevicesToRoom">
        <DispositivosComponent
            ref="dispositivosComponentModal"
            v-bind:is-favs="false"
            v-bind:is-room="true"
            v-bind:room-id="this.roomId">
        </DispositivosComponent>

        <v-banner
            v-if="addVisible"
        >
            <h3>Agregar Dispositivo</h3>
            <template v-slot:actions>


                <div class="text-center">
                    <v-menu bottom left transition="slide-y-transition">
                        <template v-slot:activator="{ on }">
                            <v-btn light large icon v-on="on">
                                <v-icon>mdi-plus</v-icon>
                            </v-btn>
                        </template>
                        <v-list>
                            <v-list-item
                                v-for="(item, index) in menu_items"
                                :key="index"
                                @click="item.method()"
                            >
                                <v-list-item-title>{{ item.title }}</v-list-item-title>
                            </v-list-item>
                        </v-list>
                    </v-menu>
                </div>


            </template>
        </v-banner>
        <v-list v-if="addVisible" two-lines style="overflow-y:auto" max-height="600px" scrollable>
            <template v-for="(item, index) in devicesToAdd">
                <v-list-item
                    :key="item.name"
                >
                    <v-btn
                        fab
                        small
                        color="white"
                        @click="removeDevice(item.id)"
                        icon>
                        <v-icon color="black lighten-1">mdi-minus</v-icon>
                    </v-btn>

                    <v-list-item-content>
                        <v-list-item-title v-html="item.name"></v-list-item-title>
                    </v-list-item-content>
                </v-list-item>
                <v-divider></v-divider>
            </template>
        </v-list>
        <AddDevice ref="addDeviceModal" @onAddDevice="addDeviceToList"></AddDevice>
        <CreateDevice ref="createDeviceModal" @onCreateDevice="addDeviceToList"/>
    </InformationOverlay>
</template>

<script>
    import InformationOverlay from './InformationOverlay'
    import DispositivosComponent from './DispositivosComponent'
    import CreateDevice from '../create_components/CreateDevice'
    import AddDevice from '../create_components/AddDevice'

    const api = require('../../js/api')
    const utils = require('../../js/utils')

    export default {
        name: 'CreateRoom',
        components: { InformationOverlay, DispositivosComponent, CreateDevice, AddDevice },
        methods: {
            displayRoomInfo: function (id, name) {
                this.name = name
                this.roomId = id
                this.$refs.infoOverlay.display()
                this.$refs.dispositivosComponentModal.loadData(id)
            },
            createNewDevice: function () {
                //this.$refs.createDeviceModal.displayCreateDevice();
            },
            addDevice: function () {
                this.addVisible = true
            },
            clearData: function () {
                this.name = ''
                this.roomId = null
                this.addVisible = false
                this.devicesToAdd = [];
            },
            chooseExistingDevice: function () {
                //this.$refs.addDeviceModal.displayAddDevice();
            },
            async deleteObj () {
                // Recupero los dispositivos para ese room
                const res = (await api.getDevicesForRoom(this.roomId)).result;

                // Borra los dispositivos del cuarto
                for (let i = 0; i < res.length; i++){
                    await api.deleteObj("rooms/devices/", res[i].id);
                }

                // Borra el cuarto
                await api.deleteObj('rooms/', this.roomId)
                this.$emit('onUpdate')
            },
            async updateInfo (name) {
                if (name.length > 3) {
                    await api.updateObjInfo('rooms/', this.roomId, { 'name': name })
                    this.$emit('onUpdate')
                }
            },
            async createRoom () {
                // Crea el room
                let response = await api.createObject('rooms', { 'name': this.room.name })
                if (!response.hasOwnProperty('error')) {
                    let roomId = response.result.id
                    let uniqueDevices = utils.getUniqueDevicesFromArray(this.room.devices)
                    for (let i = 0; i < uniqueDevices.length; i++) {
                        let device = uniqueDevices[i]
                        let res = await api.createObjectWithoutData('rooms/' + roomId + '/devices/' + device.id)
                    }
                    // CREATE ROOM
                    this.$emit('onCreateRoom', this.room)
                    this.$refs.addDeviceModal.getDevices()
                    this.clearNewRoom()
                } else {
                    console.log(error)
                }
            },
            addDeviceToList: function (device) {
                this.devicesToAdd.push(device)
            },
            createNewDevice: function () {
                this.$refs.createDeviceModal.displayCreateDevice()
            },
            chooseExistingDevice: function () {
                this.$refs.addDeviceModal.displayAddDevice()
            },
            async createRoom () {
                // Crea el room
                let response = await api.createObject('rooms', { 'name': this.room.name })
                if (!response.hasOwnProperty('error')) {
                    let roomId = response.result.id
                    let uniqueDevices = utils.getUniqueDevicesFromArray(this.room.devices)
                    for (let i = 0; i < uniqueDevices.length; i++) {
                        let device = uniqueDevices[i]
                        let res = await api.createObjectWithoutData('rooms/' + roomId + '/devices/' + device.id)
                    }
                    // CREATE ROOM
                    this.$refs.addDeviceModal.getDevices()
                    this.clearNewRoom()
                    this.$emit('onCreateRoom', this.room)
                } else {
                    console.log(error)
                }
            },
            async addDevicesToRoom () {
                let uniqueDevices = utils.getUniqueDevicesFromArray(this.devicesToAdd)
                for (let i = 0; i < uniqueDevices.length; i++) {
                    let device = uniqueDevices[i]
                    let res = await api.createObjectWithoutData('rooms/' + this.roomId + '/devices/' + device.id)
                }
                this.devicesToAdd = []
                this.addVisible = false
                this.$refs.dispositivosComponentModal.loadData(this.roomId)
            },
            removeDevice(id){
                this.devicesToAdd = utils.removeById(this.devicesToAdd, id);
            }
        },
        data () {
            return {
                devicesToAdd: [],
                name: '',
                roomId: null,
                display: false,
                addVisible: false,
                menu_items: [
                    { title: 'Dispositivo Nuevo', method: this.createNewDevice },
                    { title: 'Dispositivo Existente', method: this.chooseExistingDevice },
                ],
                existingDeviceModal: false,
            }
        },
    }
</script>
