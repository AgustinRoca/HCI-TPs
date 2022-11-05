<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">
    <v-container class="ma-0 pa-0" id="dispositivos-component">
        <snack ref="snackComp"></snack>
        <v-layout row align-content-center>

            <v-expansion-panels>
                <v-expansion-panel id="dispositivosRepeater" v-for="dispositivo in filteredObjs"
                                   :key="dispositivo.id">
                    <v-expansion-panel-header>
                        <v-col cols="1" v-if="isRoom">
                            <v-btn
                                fab
                                small
                                color="white"
                                @click.native.stop="removeDeviceFromRoom(dispositivo.id)"
                                v-on="on"
                                icon>
                                <v-icon color="black lighten-1">mdi-close-circle</v-icon>
                            </v-btn>
                        </v-col>
                        <v-col cols="1">
                            <v-checkbox
                                fab
                                v-model="dispositivo.meta.fav"
                                on-icon="mdi-heart"
                                off-icon="mdi-heart"
                                color="red darken-1"
                                @click.native.stop="updateDeviceInfo(dispositivo.id, {'name': dispositivo.name, 'meta':{'fav':dispositivo.meta.fav}})">
                            </v-checkbox>
                        </v-col>
                        <v-col cols="10">
                            <div class="device-name my-2"> {{ dispositivo.name }} </div>
                            <div class="grey--text"> Status: {{ dispositivo.state.status}} </div>
                        </v-col>
                        <v-col cols="1" v-if="!isRoom">
                            <v-menu
                                v-model="menuVisibility[dispositivo.id]"
                                :close-on-content-click="false"
                                :nudge-width="200"
                                offset-x>
                                <template v-slot:activator="{ on }">
                                    <v-btn
                                        fab
                                        large
                                        color="white"
                                        @click.native.stop="setNewNameDefault(dispositivo.name)"
                                        v-on="on"
                                        icon>
                                        <v-icon color="black lighten-1">mdi-settings</v-icon>
                                    </v-btn>
                                </template>

                                <v-card center>
                                    <v-list>
                                        <v-list-item>

                                            <v-list-item-content>
                                                <v-list-item-title>{{dispositivo.name}}</v-list-item-title>
                                                <v-list-item-subtitle>Configuración</v-list-item-subtitle>
                                            </v-list-item-content>

                                            <v-list-item-action>
                                                <v-btn icon v-on:click="deleteDevice(dispositivo.id)">
                                                    <v-icon>mdi-delete</v-icon>
                                                </v-btn>
                                            </v-list-item-action>
                                        </v-list-item>
                                    </v-list>

                                    <v-divider></v-divider>

                                    <v-list>

                                        <v-list-item>
                                            <v-list-item-content>
                                                <v-list-item-title>Nombre</v-list-item-title>
                                                <v-text-field
                                                    v-model="newName"
                                                    label="Solo"
                                                    placeholder="Nombre"
                                                    solo
                                                    outlined
                                                    :value="dispositivo.name"
                                                ></v-text-field>
                                            </v-list-item-content>
                                        </v-list-item>
                                    </v-list>

                                    <v-card-actions>
                                        <div class="flex-grow-1"></div>

<!--
                                        <v-btn text v-on:click="hideMenu(dispositivo.id)">Cancel</v-btn>
-->
                                        <v-btn color="primary" text
                                               @click="saveInfoFromMenu(dispositivo.id, { 'name':newName, 'meta':{ 'fav':dispositivo.meta.fav} })">
                                            Save
                                        </v-btn>
                                    </v-card-actions>
                                </v-card>
                            </v-menu>

                        </v-col>
                    </v-expansion-panel-header>
                    <v-expansion-panel-content>
                        <template v-if="dispositivo.type.name === 'lamp'">
                            Estado
                            <v-row no-gutters>
                                <v-btn-toggle v-model="dispositivo.state.status" mandatory>
                                    <v-btn value="on" text v-on:click="doSomething(dispositivo.id, 'turnOn')">
                                        Prendido
                                    </v-btn>
                                    <v-btn value="off" text v-on:click="doSomething(dispositivo.id, 'turnOff')">
                                        Apagado
                                    </v-btn>
                                </v-btn-toggle>
                            </v-row>
                            <v-divider></v-divider>
                            Brillo
                            <v-row no-gutters>
                                <v-slider v-model="dispositivo.state.brightness" class="align-center" max="100" min="0"
                                          hide-details
                                          v-on:end="doSomethingWithValue(dispositivo.id, 'setBrightness' , [dispositivo.state.brightness])">
                                    <template v-slot:append>
                                        <v-text-field
                                            v-model="dispositivo.state.brightness"
                                            class="mt-0 pt-0"
                                            hide-details
                                            single-line
                                            type="number"
                                            style="width: 60px"
                                            v-on:blur="doSomethingWithValue(dispositivo.id, 'setBrightness' , [dispositivo.state.brightness])"
                                        ></v-text-field>
                                    </template>
                                </v-slider>
                            </v-row>
                            <v-divider></v-divider>
                            Color
                            <v-row no-gutters>
                                <v-color-picker v-model="dispositivo.color"
                                                :value="dispositivo.color"
                                                v-on:input="doSomethingWithValue(dispositivo.id, 'setColor' , [dispositivo.state.color])"></v-color-picker>
                            </v-row>
                        </template>
                        <template v-else-if="dispositivo.type.name === 'blinds'">
                            Estado
                            <v-btn-toggle v-model="dispositivo.state.status" mandatory>
                                <v-btn value="opened" text v-on:click="doSomething(dispositivo.id, 'open')">
                                    Abierto
                                </v-btn>
                                <v-btn value="closed" text v-on:click="doSomething(dispositivo.id, 'close')">
                                    Cerrado
                                </v-btn>
                            </v-btn-toggle>
                        </template>
                        <template v-else-if="dispositivo.type.name === 'speaker'">
                            Control
                            <v-row no-gutters>
                                <v-col>
                                    <v-btn v-if="dispositivo.state.status === 'stopped'" class="" icon>
                                        <v-icon color="black lighten-1">mdi-skip-previous</v-icon>
                                    </v-btn>
                                </v-col>
                                <v-col>
                                    <v-btn v-if="dispositivo.state.status === 'stopped'" class="" icon>
                                        <v-icon color="black lighten-1">mdi-play</v-icon>
                                    </v-btn>
                                    <v-btn v-else class="" icon>
                                        <v-icon color="black lighten-1">mdi-pause</v-icon>
                                    </v-btn>
                                </v-col>
                                <v-col>
                                    <v-btn v-if="dispositivo.state.status === 'stopped'" class="" icon>
                                        <v-icon color="black lighten-1">mdi-skip-next</v-icon>
                                    </v-btn>
                                </v-col>
                            </v-row>
                            <v-divider></v-divider>
                            Volumen
                            <v-slider v-model="dispositivo.state.volume" step="1" :tick-labels="ticksLabels" min="0" color="black"
                                      max="10" ticks="always" tick-size="3"></v-slider>
                            <v-divider></v-divider>
                            Genre
                            <v-select
                                :items="items"
                                label="Outlined style"
                                outlined
                            ></v-select>
                        </template>
                        <template v-else-if="dispositivo.type.name === 'oven'">
                            <v-row no-gutters>

                                <v-col cols="6" md="6">
                                    Estado
                                    <v-btn-toggle v-model="dispositivo.state.status" mandatory>
                                        <v-btn value="on" text v-on:click="doSomething(dispositivo.id, 'turnOn')">
                                            Prendido
                                        </v-btn>
                                        <v-btn value="off" text v-on:click="doSomething(dispositivo.id, 'turnOff')">
                                            Apagado
                                        </v-btn>
                                    </v-btn-toggle>
                                </v-col>


                                <v-col cols="6" md="6">
                                    Grill
                                    <v-btn-toggle v-model="dispositivo.state.grill" mandatory
                                                  v-on:change="doSomethingWithValue(dispositivo.id, 'setGrill', [dispositivo.state.grill])">
                                        <v-btn value="large" text>
                                            Large
                                        </v-btn>
                                        <v-btn value="eco" text>
                                            Eco
                                        </v-btn>
                                        <v-btn value="off" text>
                                            Apagado
                                        </v-btn>
                                    </v-btn-toggle>
                                </v-col>
                            </v-row>
                            <v-divider></v-divider>
                            <v-row no-gutters>
                                <v-col cols="6" md="6">
                                    Calor
                                    <v-btn-toggle v-model="dispositivo.state.heat" mandatory
                                                  v-on:change="doSomethingWithValue(dispositivo.id, 'setHeat', [dispositivo.state.heat])">
                                        <v-btn value="conventional" text>
                                            Convencional
                                        </v-btn>
                                        <v-btn value="bottom" text>
                                            Abajo
                                        </v-btn>
                                        <v-btn value="top" text>
                                            Arriba
                                        </v-btn>
                                    </v-btn-toggle>
                                </v-col>
                                <v-col cols="6" md="6">
                                    Convección
                                    <v-btn-toggle v-model="dispositivo.state.convection" mandatory
                                                  v-on:change="doSomethingWithValue(dispositivo.id, 'setConvection', [dispositivo.state.convection])">
                                        <v-btn value="normal" text>
                                            Normal
                                        </v-btn>
                                        <v-btn value="eco" text>
                                            Eco
                                        </v-btn>
                                        <v-btn value="off" text>
                                            Apagado
                                        </v-btn>
                                    </v-btn-toggle>
                                </v-col>
                            </v-row>
                            <v-divider></v-divider>
                            Temperatura
                            <v-row no-gutters>
                                <v-slider v-model="dispositivo.state.temperature" class="align-center" max="230"
                                          min="90" color="black"
                                          v-on:end="doSomethingWithValue(dispositivo.id, 'setTemperature' , [dispositivo.state.temperature])">
                                    <template v-slot:append>
                                        <v-text-field
                                            v-model="dispositivo.state.temperature"
                                            class="mt-0 pt-0"
                                            hide-details
                                            single-line
                                            type="number"
                                            style="width: 60px"
                                            v-on:blur="doSomethingWithValue(dispositivo.id, 'setTemperature' , [dispositivo.state.temperature])"
                                        ></v-text-field>
                                    </template>
                                </v-slider>
                            </v-row>
                        </template>
                        <template v-else-if="dispositivo.type.name === 'ac'">
                            <v-row no-gutters class="pa-2">

                                <v-col cols="6" md="6">
                                    Estado
                                    <v-btn-toggle v-model="dispositivo.state.status" mandatory>
                                        <v-btn value="on" text v-on:click="doSomething(dispositivo.id, 'turnOn')">
                                            Prendido
                                        </v-btn>
                                        <v-btn value="off" text v-on:click="doSomething(dispositivo.id, 'turnOff')">
                                            Apagado
                                        </v-btn>
                                    </v-btn-toggle>
                                </v-col>


                                <v-col cols="6" md="6">
                                    Modo
                                    <v-btn-toggle v-model="dispositivo.state.mode" mandatory
                                                  v-on:change="doSomethingWithValue(dispositivo.id, 'setMode' , [dispositivo.state.mode])">
                                        <v-btn value="cool" text>
                                            Cool
                                        </v-btn>
                                        <v-btn value="fan" text>
                                            Fan
                                        </v-btn>
                                        <v-btn value="heat" text>
                                            Heat
                                        </v-btn>
                                    </v-btn-toggle>
                                </v-col>
                            </v-row>
                            <v-divider></v-divider>
                            <v-row no-gutters class="pa-2">
                                <v-col cols="6" md="6">
                                    Movimiento Vertical
                                    <v-btn-toggle v-model="dispositivo.state.verticalSwing" mandatory
                                                  v-on:change="doSomethingWithValue(dispositivo.id, 'setVerticalSwing' , [dispositivo.state.verticalSwing])">
                                        <v-btn value="auto" text>
                                            Auto
                                        </v-btn>
                                        <v-btn value="22" text>
                                            22
                                        </v-btn>
                                        <v-btn value="45" text>
                                            45
                                        </v-btn>
                                        <v-btn value="67" text>
                                            67
                                        </v-btn>
                                        <v-btn value="90" text>
                                            90
                                        </v-btn>
                                    </v-btn-toggle>
                                </v-col>
                                <v-col cols="6" md="6">
                                    Movimiento Horizontal
                                    <v-btn-toggle v-model="dispositivo.state.horizontalSwing" mandatory
                                                  v-on:click="doSomethingWithValue(dispositivo.id, 'setHorizontalSwing' , [dispositivo.state.horizontalSwing])">
                                        <v-btn value="auto" text>
                                            Auto
                                        </v-btn>
                                        <v-btn value="-90" text>
                                            -90
                                        </v-btn>
                                        <v-btn value="-45" text>
                                            -45
                                        </v-btn>
                                        <v-btn value="0" text>
                                            0
                                        </v-btn>
                                        <v-btn value="45" text>
                                            45
                                        </v-btn>
                                        <v-btn value="90" text>
                                            90
                                        </v-btn>
                                    </v-btn-toggle>
                                </v-col>
                            </v-row>
                            <v-divider></v-divider>
                            <v-row no-gutters class="pa-2">
                                <v-col cols="6" md="6">
                                    Velocidad
                                    <v-btn-toggle v-model="dispositivo.state.fanSpeed" mandatory
                                                  v-on:change="doSomethingWithValue(dispositivo.id, 'setFanSpeed' , [dispositivo.state.fanSpeed])">
                                        <v-btn value="auto" text>
                                            Auto
                                        </v-btn>
                                        <v-btn value="25" text>
                                            25
                                        </v-btn>
                                        <v-btn value="50" text>
                                            50
                                        </v-btn>
                                        <v-btn value="75" text>
                                            75
                                        </v-btn>
                                        <v-btn value="100" text>
                                            100
                                        </v-btn>
                                    </v-btn-toggle>
                                </v-col>
                            </v-row>
                            <v-divider></v-divider>
                            Temperatura
                            <v-row no-gutters class="pa-2">
                                <v-slider v-model="dispositivo.state.temperature" class="align-center" max="38" min="18"
                                          color="black"
                                          v-on:end="doSomethingWithValue(dispositivo.id, 'setTemperature' , [dispositivo.state.temperature])">
                                    <template v-slot:append>
                                        <v-text-field
                                            v-model="dispositivo.state.temperature"
                                            class="mt-0 pt-0"
                                            hide-details
                                            single-line
                                            type="number"
                                            style="width: 60px"
                                            v-on:blur="doSomethingWithValue(dispositivo.id, 'setTemperature' , [dispositivo.state.temperature])"
                                        ></v-text-field>
                                    </template>
                                </v-slider>
                            </v-row>
                        </template>
                        <template v-else-if="dispositivo.type.name === 'door'">
                            <v-row no-gutters class="pa-2">

                                <v-col cols="6" md="6">
                                    Estado
                                    <v-btn-toggle v-model="dispositivo.state.status" mandatory>
                                        <v-btn value="opened" text v-on:click="doSomething(dispositivo.id, 'open')">
                                            Abierta
                                        </v-btn>
                                        <v-btn value="closed" text v-on:click="doSomething(dispositivo.id, 'close')">
                                            Cerrada
                                        </v-btn>
                                    </v-btn-toggle>
                                </v-col>


                                <v-col cols="6" md="6">
                                    Bloqueo
                                    <v-btn-toggle v-model="dispositivo.state.lock" mandatory>
                                        <v-btn value="locked" text v-on:click="doSomething(dispositivo.id, 'lock')">
                                            Bloqueada
                                        </v-btn>
                                        <v-btn value="unlocked" text v-on:click="doSomething(dispositivo.id, 'unlock')">
                                            Desbloqueada
                                        </v-btn>
                                    </v-btn-toggle>
                                </v-col>
                            </v-row>
                        </template>
                        <template v-else-if="dispositivo.type.name === 'vacuum'">
                            Control
                            <v-row no-gutters>
                                <v-col>
                                    <v-btn v-if="dispositivo.state.status === 'inactive'" class="" icon
                                           v-on:click="doSomething(dispositivo.id, 'start')">
                                        <v-icon large color="black lighten-1">mdi-play</v-icon>
                                    </v-btn>
                                    <v-btn v-else class="ma-1" icon v-on:click="doSomething(dispositivo.id, 'pause')">
                                        <v-icon large color="black lighten-1">mdi-stop</v-icon>
                                    </v-btn>
                                    <v-btn class="mx-3" icon v-on:click="doSomething(dispositivo.id, 'dock')">
                                        <v-icon color="black lighten-1">mdi-battery-charging</v-icon>
                                    </v-btn>
                                </v-col>
                            </v-row>
                            <v-divider></v-divider>
                            <v-row no-gutters class="pa-2">

                                <v-col cols="3" md="3">
                                    <div>Estado</div>
                                    <v-btn-toggle v-model="dispositivo.state.mode" mandatory
                                                  v-on:change="doSomethingWithValue(dispositivo.id, 'setMode', [dispositivo.state.mode])">
                                        <v-btn value="vacuum" text>
                                            Aspirar
                                        </v-btn>
                                        <v-btn value="mop">
                                            Limpiar
                                        </v-btn>
                                    </v-btn-toggle>


                                </v-col>

                                <v-col cols="2" >
                                    <div>Carga</div>
                                    <div>{{dispositivo.state.batteryLevel}} %</div>
                                </v-col>


                                <v-col cols="7" md="7">
                                    Localizacion
                                    <v-select
                                        v-model="dispositivo.state.location"
                                        :items="rooms"
                                        label="Localizacion"
                                        outlined
                                        v-on:blur="doSomethingWithValue(dispositivo.id, 'setLocation', [roomNamesToIds[dispositivo.state.location]])"
                                    ></v-select>
                                </v-col>
                            </v-row>
                        </template>
                        <template v-else-if="dispositivo.type.name === 'refrigerator'">
                            Modo
                            <v-row no-gutters class="pa-2">
                                <v-btn-toggle v-model="dispositivo.state.mode" mandatory
                                              v-on:change="doSomethingWithValue(dispositivo.id, 'setMode', [dispositivo.state.mode])">
                                    <v-btn value="default" text>
                                        Predeterminado
                                    </v-btn>
                                    <v-btn value="vacation" text>
                                        Vacaciones
                                    </v-btn>
                                    <v-btn value="party" text>
                                        Fiesta
                                    </v-btn>
                                </v-btn-toggle>
                            </v-row>

                            <v-divider></v-divider>
                            Temperatura
                            <v-row no-gutters class="pa-2">
                                <v-slider v-model="dispositivo.state.temperature"
                                          class="align-center" step="1" max="8" min="2"
                                          thumb-label="always"
                                          color="black"
                                          tick-size="3"
                                          ticks="always"
                                          v-on:end="doSomethingWithValue(dispositivo.id, 'setTemperature', [dispositivo.state.temperature])">
                                </v-slider>
                            </v-row>
                            Temperatura Freezer
                            <v-row no-gutters class="pa-2">
                                <v-slider v-model="dispositivo.state.freezerTemperature" class="align-center" max="-8" step="1"  thumb-label="always"
                                          min="-20"
                                          color="black"
                                          tick-size="3"
                                          ticks="always"
                                          v-on:end="doSomethingWithValue(dispositivo.id, 'setFreezerTemperature', [dispositivo.state.freezerTemperature])">
                                </v-slider>
                            </v-row>
                        </template>
                    </v-expansion-panel-content>

                </v-expansion-panel>
            </v-expansion-panels>
        </v-layout>
    </v-container>
</template>

<style src="../../css/dispositivos.css"></style>
<script>
    const api = require('../../js/api')
    const utils = require('../../js/utils')
    import snack from './Snack'

    export default {
        name: 'dispositivos-component',
        components: {snack },
        props:{
            isFavs:{
                type: Boolean,
                default: false
            },
            isRoom:{
                type: Boolean,
                default: false
            },
            roomId: {
                type: String
            }
        },
        ticksLabels: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10],
        rooms: [],
        roomNamesToIds: {},
        roomIdsToNames: {},
        objs: [],
        data: () => ({
            newName: '',
            activeFilter: "",
            objs: [],
            filteredObjs: [],
            isFavs: false,
            isRoom: false,
            roomId: null,
            ticksLabels: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10],
            rooms: [],
            roomNamesToIds: {},
            roomIdsToNames: {},
        }),
        computed: {
            menuVisibility: function () {
                let dic = {}
                for (let i = 0; i < this.objs.length; i++) {
                    dic[this.objs[i].id] = false
                }
                return dic
            },
        },
        methods: {
            // Getter de los devices, redirecciona al getter adecuado
            getDevices(){
                if (this.isFavs){
                    this.getDevicesFavs();
                } else if (this.isRoom){
                    this.getDevicesForRoom(this.roomId)
                } else {
                    this.getDevicesDefault()
                }
            },
            // Getter para todos los devices
            async getDevicesDefault(){
                const devices = await api.getObject('devices')
                this.objs = devices['devices'];
                this.filteredObjs = this.objs;
            },
            // Getter para los devices de un solo cuarto
            async getDevicesForRoom(id){
                const res = (await api.getDevicesForRoom(id)).result;
                this.objs = res;
                this.filteredObjs = this.objs;
            },
            // Getter de los devices favoritos
            async getDevicesFavs () {
                const devices = await api.getFavoriteDevices()
                this.objs = devices;
                this.filteredObjs = this.objs;
            },
            async getRooms () {
                const result = await api.getObject('rooms')
                let roomsAux = utils.observerArrayToArray(result['result'])
                for (let i = 0; i < roomsAux.length; i++){
                    this.rooms.push(roomsAux[i].name)
                }
                this.roomNamesToIds = this.roomNamesToIdsFun(roomsAux);
                this.roomIdsToNames = this.roomIdsToNamesFun(roomsAux);
            },
            roomNamesToIdsFun(rooms){
                let dic = {}
                for (let i = 0; i < rooms.length; i++){
                    dic[rooms[i].name] = rooms[i].id;
                }
                return dic;
            },
            roomIdsToNamesFun(rooms){
                let dic = {}
                for (let i = 0; i < rooms.length; i++){
                    dic[rooms[i].id] = rooms[i].name;
                }
                return dic;
            },
            async removeDeviceFromRoom(id){
                await api.removeDeviceFromRoom(id);
                this.getDevices();
            },
            async doSomething (id, action) {
                await api.performAction(id, action)
            },
            async doSomethingWithValue (id, action, value) {
                console.log(value)
                await api.performActionWithValue(id, action, value)
            },
            async updateDeviceInfo (id, data) {
                await api.updateObjInfo("devices/", id, data)
                this.getDevices();
            },
            saveInfoFromMenu (id, data) {
                if (data.name.length > 3 && data.name.length < 60) {
                    this.hideMenu(id);
                    this.updateDeviceInfo(id, data);
                    this.getDevices();
                } else {
                    this.$refs.snackComp.showSnack("Error: El nombre tiene que tener entre 3 y 60 caracteres", "red darken-2");
                }
            },
            async deleteDevice (id) {
                this.hideMenu(id);

                for (let i = 0; i < this.objs.length; i ++){
                    if (this.objs[i].id === id){
                        if (this.objs[i].hasOwnProperty("room")){
                            let res = await api.deleteObj("rooms/devices/", id);
                        }
                    }
                }
                let res = await api.deleteObj("devices/", id);
                let snack = utils.handleResponse(res);
                this.$refs.snackComp.showSnack(snack.text, snack.color);
                this.getDevices();

                //Eliminamos la/s rutina/s que tenia dicho dispositvo
                let routines = await api.getObject('routines')
                routines = routines['result']

                for(var i=0;i<routines.length;i++){
                    for(var j=0;j<routines[i].actions.length;i++){
                            if(routines[i].actions[j].device.id === id){
                                res = await api.deleteObj('routines/', routines[i].id);
                                break;
                            }
                    }
                }

            },
            setNewNameDefault (name) {
                this.newName = name;
            },
            hideMenu (id) {
                this.menuVisibility[id] = false;
            },
            loadData(id){
                this.roomId = id
                // Esto tiene que ser en este orden, porque sino va a devolver vacio
                // Primero hace el getter de los rooms y los dispositivos asociados a cada uno
                this.getRooms();
                // Despues hace el getter de los devices
                this.getDevices();
            },
            sendFilter(filter){
                if (filter === ""){
                    this.filteredObjs = this.objs;
                } else {
                    let aux = [];
                    if (filter.length > this.activeFilter.length){
                        for (let i = 0; i < this.filteredObjs.length; i++){
                            if (this.filteredObjs[i].name.toLowerCase().includes(filter)){
                                aux.push(this.filteredObjs[i])
                            }
                        }
                    } else {
                        for (let i = 0; i < this.objs.length; i++){
                            if (this.objs[i].name.toLowerCase().includes(filter)){
                                aux.push(this.objs[i])
                            }
                        }
                    }
                    this.filteredObjs = aux;
                }
                this.activeFilter = filter;
            }
        },
        created () {
            console.log("CREATED")
            console.log(this.roomId)
            // Esto tiene que ser en este orden, porque sino va a devolver vacio
            // Primero hace el getter de los rooms y los dispositivos asociados a cada uno
            this.getRooms();
            // Despues hace el getter de los devices
            this.getDevices();
        },
    }
</script>
