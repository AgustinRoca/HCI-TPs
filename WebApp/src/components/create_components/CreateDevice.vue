<template>
	<CreateOverlay title="Crear Dispositivo" ref="createOverlay" @onCloseModal="clearCreateDevice" @onCreate="createDevice">
        <snack ref="snackComp"></snack>
        <v-text-field label="Nombre" v-model="device.name" required></v-text-field>
		<v-overflow-btn class="my-2" v-model="device.type" :items="device_types" label="Tipo de Dispositivo" target="#dropdown-example-1"></v-overflow-btn>
	</CreateOverlay>
</template>

<script>
    import CreateOverlay from './CreateOverlay'
    import Snack from '../../components/base/Snack'
    const api = require('../../js/api')
    const utils = require('../../js/utils')

    export default {
        name: 'CreateDevice',
        components: { CreateOverlay, Snack },
        methods: {
            displayCreateDevice : function () {
                this.$refs.createOverlay.display();
            },
            clearCreateDevice:function () {
                this.device.name = ""
                this.device.type = ""
            },
			async createDevice() {
                if (this.device.name.length > 3 && this.device.name.length < 60){
                    const response = await api.createObject("devices", {'type':{'id':this.device_key_mappings[this.device.type]}, 'name':this.device.name, 'meta':{'fav':false}})
                    if (!response.hasOwnProperty("error")){
                        let device = Object.assign({}, response.result);
                        // Le pasa el resultado, que es el objeto con el ID
                        this.$emit("onCreateDevice", device);
                        this.clearCreateDevice()
                        this.$refs.snackComp.showSnack("Se creó con éxito", "indigo darken-1");
                    } else {
                        let snack = utils.handleResponse(response);
                        this.$refs.snackComp.showSnack(snack.text, snack.color);
                    }
                } else {
                    this.$refs.snackComp.showSnack("Error: El nombre tiene que tener entre 3 y 60 caracteres", "red darken-2");
                }
            },
            async getDeviceTypes(){
                const types = (await api.getObject("devicetypes")).result;
                let filteredTypes = utils.typesToPairs(types);
                this.device_types = utils.typesToNames(filteredTypes);
                this.device_key_mappings = utils.typesToDic(filteredTypes);
            }
        },
        props:{},
        device : {
            name: "",
            type: ""
        },
        data(){
            return{
                device : {
                    name: "",
                    type: ""
                },
                items: [],
                device_types: [],
                device_key_mappings: {}
            }
        },
        created () {
            this.getDeviceTypes();
        }
    }
</script>

<style scoped>

</style>
