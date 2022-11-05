<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">
    <v-container class="ma-0 pa-0" id="dispositivos-component">

        <!--
        <v-layout row class="my-5">
            <div class="tituloDispostivos">Dispositivos</div>
            <v-btn small class="filtroDispositivos" @click="ordenarPor()">
                <v-icon left small>mdi-folder</v-icon>
                <span class="class caption upperrcase">Filtrar por nombre</span>
            </v-btn>
        </v-layout>
        -->

        <dispositivos-component ref="dispositivosComponentModal" v-bind:is-favs="true" v-bind:is-room="false"></dispositivos-component>
    </v-container>
</template>


<script>
    const api = require('../js/api')
    import dispositivosComponent from '../components/base/DispositivosComponent'

    export default {
        name: 'Favorites',
        components: { dispositivosComponent },
        dispositivos: [],
        ticksLabels: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10],
        data: () => ({
            newName: '',
            dispositivos: [],
            ticksLabels: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10],
            rooms: [],
        }),
        computed: {
            menuVisibility: function () {
                console.log('computed')
                var dic = {}
                for (var i = 0; i < this.dispositivos.length; i++) {
                    dic[this.dispositivos[i].id] = false
                }
                return dic
            },
        },
        methods: {
            async getDispositivos () {
                const devices = await api.getFavoriteDevices()
                this.dispositivos = devices;
                return this.dispositivos;
            },
            doSomething (id, action) {
                api.performAction(id, action)
            },
            doSomethingWithValue (id, action, value) {
                api.performActionWithValue(id, action, value)
            },
            updateDeviceInfo (id, data) {
                api.updateDeviceInfo(id, data)
                this.getDispositivos();
            },
            saveInfoFromMenu (id, data) {
                if (data.name.length > 3) {
                    this.hideMenu(id)
                    this.updateDeviceInfo(id, data)
                }
                this.getDispositivos();
            },
            deleteDevice (id) {
                this.hideMenu(id);
                api.deleteDevice(id);
                this.getDispositivos();
            },
            setNewNameDefault (name) {
                this.newName = name;
            },
            hideMenu (id) {
                this.menuVisibility[id] = false
            },
            sendFilter(filter){
                this.$refs.dispositivosComponentModal.sendFilter(filter);
            }
        },
        created () {
            this.getDispositivos();
        },
    }

</script>

