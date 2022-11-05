<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">
	<div>
		<CreateOverlay title="Crear Espacio" ref="createOverlay" @onCloseModal="clearNewRoom" @onCreate="createRoom">
			<v-text-field label="Nombre" v-model="room.name" required></v-text-field>
			<v-banner>
				<h3>Agregar Dispositivos</h3>
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

			<v-list two-lines style="overflow-y:auto" max-height="600px" scrollable>
				<template v-for="(item, index) in room.devices">
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
		</CreateOverlay>


		<AddDevice ref="addDeviceModal" @onAddDevice="addDeviceToList"></AddDevice>
		<CreateDevice ref="createDeviceModal" @onCreateDevice="addDeviceToList"/>
	</div>
</template>

<script>
    import CreateOverlay from './CreateOverlay'
    import CreateDevice from './CreateDevice'
	import AddDevice from './AddDevice'
    const api = require('../../js/api')
    const utils = require('../../js/utils')

    export default {
        name: 'CreateRoom',
        components: { CreateDevice, CreateOverlay, AddDevice },
        methods: {
            displayCreateRoom : function () {
                this.$refs.createOverlay.display();
            },
            createNewDevice : function() {
                this.$refs.createDeviceModal.displayCreateDevice();
            },
            clearNewRoom : function(){
                this.room.name = "";
                this.room.devices = [];
            },
            chooseExistingDevice : function(){
                this.$refs.addDeviceModal.displayAddDevice();
            },
			async createRoom() {
                // Crea el room
                let response = await api.createObject("rooms", { 'name':this.room.name});
                if (!response.hasOwnProperty("error")){
                    let roomId = response.result.id;
                    let uniqueDevices = utils.getUniqueDevicesFromArray(this.room.devices);
                    for (let i = 0; i < uniqueDevices.length; i++){
                        let device = uniqueDevices[i];
                        let res = await api.createObjectWithoutData("rooms/" + roomId + "/devices/" +  device.id);
                    }
                    this.$refs.addDeviceModal.getDevices();
                    this.clearNewRoom();
                    this.$emit("onCreateRoom", this.room);
                } else {
                    console.log(error)
                }
            },
			addDeviceToList : function(device) {
                this.room.devices.push(device);
                console.log("ADD")
            },
            removeDevice(id){
                this.room.devices = utils.removeById(this.room.devices, id);
            }
        },
        data(){
            return{
                room:{
                    name:"",
                    devices:[]
                },
                display:false,
                menu_items: [
                    { title: 'Dispositivo Nuevo', method : this.createNewDevice },
                    { title: 'Dispositivo Existente', method: this.chooseExistingDevice }
                ],
                existingDeviceModal: false
            }
        }
    }
</script>

<style scoped>
</style>
