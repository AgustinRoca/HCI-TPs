<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">
	<div>
		<CreateOverlay title="Crear Rutina" ref="createOverlay" @onCloseModal="cleanRoutine" @onCreate="saveRoutine">
			<v-text-field label="Nombre" v-model="routine.name" required></v-text-field>

			<v-banner>
				<h3>Agregar Acciones</h3>
				<template v-slot:actions>
					<v-btn light large icon v-on="on" @click="$refs.addActionModal.displayOverlay()">
						<v-icon>mdi-plus</v-icon>
					</v-btn>
				</template>
			</v-banner>

			<v-list two-lines style="overflow-y:auto" max-height="600px" scrollable>
				<template v-for="(item, index) in routine.devices_config">
					<v-list-item
							:key="item.name"
					>
                        <v-btn
                            fab
                            small
                            color="white"
                            @click="removeAction(item.count)"
                            icon>
                            <v-icon color="black lighten-1">mdi-minus</v-icon>
                        </v-btn>
						<v-list-item-content>
							<v-list-item-title>
                                {{item.name}} - {{item.action}} - {{item.data[0]}}

                            </v-list-item-title>
						</v-list-item-content>
					</v-list-item>
					<v-divider></v-divider>
				</template>
			</v-list>

		</CreateOverlay>

<!--
		<AddDevice ref="addDeviceModal" v-bind:is-routine="true" @onAddDevice="addDevice"></AddDevice>
-->
		<AddAction ref="addActionModal"  @onAddConfig="addConfig"></AddAction>
	</div>
</template>

<script>
    import CreateOverlay from './CreateOverlay'
    import AddDevice from './AddDevice'
    import AddAction from './AddAction'
    const api = require('../../js/api')
    const utils = require('../../js/utils')

    export default {
        name: 'CreateRutine',
        components:{ AddDevice, CreateOverlay, AddAction},
        methods:{
            displayCreateRutine : function () {
                this.cleanRoutine();
                this.$refs.createOverlay.display();
            },
            addDevice: function(device){
                this.routine.devices_config.push(device);
                this.routine.devices_config = utils.getUniqueDevicesFromArray(this.routine.devices_config);
            },
            async saveRoutine(){
                if (this.routine.name.length > 3 && this.routine.name.length <= 60 && this.routine.devices_config.length > 0){
                    let routineToSave = this.buildRoutine();

                    console.log(JSON.stringify(routineToSave))
                    await api.createObject("routines", routineToSave)
                    this.$emit("onCreateRoutine");
                }
                else{
                    this.$emit("onFailedRoutine");
                }
            },
            checkboxClick: function(e){
                e.cancelBubble = true;
                if(this.hasActivationTime)
                    this.isTimePanelOpen = [0];
                else
                    this.isTimePanelOpen = [];
            },
            cleanRoutine(){
                this.routine.devices_config = [];
                this.routine.name = "";
                this.routine.hasActivationTime = false;
                this.routine.activation_time = null;
                this.isTimePanelOpen = [];
            },
            addConfig(id, name, action, data){
                let config = {};
                config["id"] = id;
                config["name"] = name;
                config["action"] = action;
                config["data"] = data;
                config["count"] = this.actionCount;
                this.routine.devices_config.push(config)
                this.actionCount++;
            },
            buildRoutine(){
                let result = {};
                result.name = this.routine.name;
                result.actions = [];
                if (this.routine.hasActivationTime){
                    result.meta.time = this.routine.activation_time;
                }
                for (let i = 0; i < this.routine.devices_config.length; i++){
                    let device = this.routine.devices_config[i];
                    let action = {};

                    action["device"] = {};
                    action.device["id"] = device.id;
                    action["actionName"] = device.action;
                    action["params"] = device.data;

                    result.actions.push(action)
                }

                return result;
            },
            removeAction(id){
                let aux = []
                for (let i = 0; i < this.routine.devices_config.length; i++){
                    if (this.routine.devices_config[i].count !== id){
                        aux.push(this.routine.devices_config[i]);
                    }
                }
                this.routine.devices_config = aux;
            }
        },
        data(){
            return {
                routine:{
                    name: "",
                    hasActivationTime: false,
                    activation_time: null,
                    devices_config: [],
                },
                actionCount: 0,
                isTimePanelOpen: [],
            }
        }
    }
</script>
