<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">
    <v-container class="ma-0 pa-0" >
        <snack ref="snackComp"></snack>
        <dispositivos-component ref="dispositivosComponentModal" v-bind:is-favs="false" v-bind:is-room="false"></dispositivos-component>
        <CreateDevice ref="createDeviceModal" @onCreateDevice="deviceAdded"></CreateDevice>
        <v-btn bottom dark x-large fab fixed right elevation=5 @click="$refs.createDeviceModal.displayCreateDevice()">
            <v-icon>mdi-plus</v-icon>
        </v-btn>
    </v-container>
</template>

<script>
    import dispositivosComponent from '../components/base/DispositivosComponent'
    import snack from '../components/base/Snack'
    import CreateDevice from '../components/create_components/CreateDevice'

    export default {
        components: { dispositivosComponent, CreateDevice, snack },
        name: 'Dispositivos',
        data: () => ({
            newName: '',
        }),
        computed: {
        },
        methods: {
            deviceAdded(device){
                this.$refs.snackComp.showSnack("Se agregó el dispositivo " + device.name + " con éxito", "indigo darken-1");
                this.$refs.dispositivosComponentModal.getDevices();
            },
            sendFilter(filter){
                this.$refs.dispositivosComponentModal.sendFilter(filter);
            }
        },
        created () {

        }
    }

</script>

