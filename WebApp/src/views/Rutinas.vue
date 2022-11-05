<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">
    <v-container
        fluid
    >

        <routinesComponent  @onExecuteRoutine="executeRoutine(currentRoutine)"
                                @onDelete="deleteRoutine(currentRoutine.id)"
                                ref="routinesComponentModal"
                                > 

        </routinesComponent>

        <v-snackbar
            v-model="snack.snackbar"
            :color="snack.snackcolor"
        >
            {{ snack.snacktext }}
            <v-btn
                color="white"
                text
                @click="snack.snackbar = false"
            >
                Cerrar
            </v-btn>
        </v-snackbar>

        <v-row 
            align="center"
            justify="center"    
        >

            <v-flex xs12 sm6 md4 lg4 class="pa-3" v-for="rut in filteredRoutines" :key="rut.id">
                <v-hover v-slot:default="{ hover }">
                    <v-card
                        v-on:click="currentRoutine=rut; $refs.routinesComponentModal.display(rut,actionNameMap,deviceIdMap)"
                        class="ma-3"
                        height="250px"
                        width="360px"
                        color = "white"
                        :elevation="hover ? 12 : 2"
                    >
                        <v-img
                            class="rounded-img"
                            height="200px"
                            src="../assets/defaultRoutineImage.jpg"
                        >

                        </v-img>

                        <v-row no-gutters>
                            <v-col cols="9">
                                <v-card-text primary-title>
                                    <div class="left-text">{{rut.name}}</div>
                                </v-card-text>
                            </v-col>
                            <v-col cols="3">
                                <v-btn medium class="play-button-outside"
                                    color="light-grey"
                                    @click.native.stop="executeRoutine(rut)">
                                    <v-icon  color="light-blue">mdi-play</v-icon>
                                </v-btn>
                            </v-col>
                        </v-row>

                    </v-card>
                </v-hover>
            </v-flex>
        </v-row>

        <CreateRutine ref="createRutineModal" @onCreateRoutine="routineCreated" @onFailedRoutine="routineFailed"></CreateRutine>
            <v-btn bottom dark x-large fab fixed right elevation=5 @click="$refs.createRutineModal.displayCreateRutine()">
                <v-icon>mdi-plus</v-icon>
            </v-btn>

        </v-row>

  </v-container>

</template>

<style src="../css/cardRutinas.css"></style>

<script>
    import CreateRutine from '../components/create_components/CreateRutine'
    import routinesComponent from '../components/base/RoutinesComponent'
import { getObjectById } from '../js/api'


    const api = require('../js/api')
    const utils = require('../js/utils')

    export default {
        components: {CreateRutine, routinesComponent },
        name: 'Rutinas',
        routines: [],
        dispositivos: [],
        data: () => ({
            snack: {
                snackbar: false,
                snackcolor: 'red',
                snacktext: '',
            },
            routines: [],
            filteredRoutines: [],
            activeFilter: "",
            deviceIdMap: {},
            actionNameMap:{},
            currentRoutine:'',
        }),
        computed: {
            menuVisibility: function () {
                var dic = {}
                for (var i = 0; i < this.routines.length; i++) {
                    dic[this.routines[i].id] = false
                }
                return dic
            },

        },
        methods: {
            async getRoutines () {
                let result = await api.getObject('routines')
                this.routines = result['result']
                this.filteredRoutines = this.routines;
            },
            async getDispositivos () {
                const devices = await api.getObject('devices')
                this.dispositivos = devices['devices']


                await this.createDeviceIdMap()
                await this.createActionNameMap()

                await this.getRoutines()
            },
            async deleteRoutine (id) {
                let res = await api.deleteObj('routines/', id)
                let snack = utils.handleResponse(res);
                this.showSnack(snack.text, snack.color);
                this.getRoutines()
            },
            async doSomething (id, action) {
                await api.performAction(id, action)
            },
            async doSomethingWithValue (id, action, value) {
                await api.performActionWithValue(id, action, value)
            },
            updateDeviceInfo (id, data) {
                api.updateDeviceInfo(id, data)
                this.getRoutines()
            },
            async routineCreated () {
                this.showSnack('Se creo la nueva rutina ', 'green')
                this.getRoutines()
            },
            routineFailed(){
                this.showSnack('Fallo al crear la rutina','red');
            }, 
            async executeRoutine (routine) {
                let res = await api.performRoutine(routine.id);
                console.log(res);
                if(res.hasOwnProperty('error')){
                    this.showSnack('Error al ejecutar la rutina', 'red')
                }
                else{
                    this.showSnack('Rutina:  \'' + routine.name + '\' ejecutándose', 'indigo darken-2')
                }
            },
            createDeviceIdMap () {
                for (var i = 0; i < this.dispositivos.length; i++) {
                    this.deviceIdMap[this.dispositivos[i].id] = this.dispositivos[i].name
                }
            },
            getDeviceName (name) {
                return this.deviceIdMap[name]
            },
            createActionNameMap () {
                this.actionNameMap = utils.getActionNameMap()
            },
            showSnack (text, color) {
                this.snack.snackbar = true
                this.snack.snacktext = text
                this.snack.snackcolor = color
            },
            getDeviceName(name){
                return this.deviceIdMap[name];
            },
            createActionNameMap(){
                this.actionNameMap = utils.getActionNameMap();
            },
            sendFilter(filter){
                if (filter === ""){
                    this.filteredRoutines = this.routines;
                } else {
                    let aux = [];
                    if (filter.length > this.activeFilter.length){
                        for (let i = 0; i < this.filteredRoutines.length; i++){
                            if (this.filteredRoutines[i].name.toLowerCase().includes(filter)){
                                aux.push(this.filteredRoutines[i])
                            }
                        }
                    } else {
                        for (let i = 0; i < this.routines.length; i++){
                            if (this.routines[i].name.toLowerCase().includes(filter)){
                                aux.push(this.routines[i])
                            }
                        }
                    }
                    this.filteredRoutines = aux;
                }
                this.activeFilter = filter;
            }
        },
        created () {
            this.getDispositivos() //&routines

        },

    }

</script>
