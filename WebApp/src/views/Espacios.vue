<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">
    <v-container
        fluid
    >
        <snack ref="snackComp"></snack>

        <v-row
            align="center"
            justify="center"
        >
            <room-information ref="roomInfoModal" @onUpdate="getRooms"></room-information>


            <template v-for="room in filteredRooms">

                <v-flex xs12 md4 sm6 lg3 class="pa-1" :key="room.id">
                    <v-fade-transition>
                    <v-hover v-slot:default="{ hover }">

                        <v-card
                            v-on:click="$refs.roomInfoModal.displayRoomInfo(room.id, room.name)"
                            class="card ma-1"
                            color="white"
                            :elevation="hover ? 12 : 2"
                        >
                            <v-img
                                src="../assets/defaultRoomImage.jpg"
                                class="rounded-img"
                                :elevation="hover ? 24 : 12"
                                height="200px"
                            ></v-img>

                            <v-card-title primary-title class="justify-center centered-title">
                                {{room.name}}
                            </v-card-title>

                        </v-card>
                    </v-hover>
                    </v-fade-transition>
                </v-flex>

            </template>
            <CreateRoom ref="createRoomModal" @onCreateRoom="getRooms"></CreateRoom>
            <v-btn bottom dark x-large fab fixed right elevation=5 @click="$refs.createRoomModal.displayCreateRoom()">
                <v-icon>mdi-plus</v-icon>
            </v-btn>
        </v-row>

    </v-container>
</template>
<style src="../css/cardEspacios.css"></style>
<script>
    import menuComponent from '../components/base/menuComponent.vue'
    import roomInformation from '../components/base/RoomInformation'
    import dispositivosComponent from '../components/base/DispositivosComponent'
    import Snack from '../components/base/Snack'
    import CreateRoom from '../components/create_components/CreateRoom'

    const api = require('../js/api')
    const utils = require('../js/utils')

    export default {
        components: { menuComponent, dispositivosComponent, CreateRoom, roomInformation, Snack },
        name: 'Espacios',
        data: () => ({
            rooms: [],
            filteredRooms: [],
            activeFilter: ""
        }),
        computed: {
            menuVisibility: function () {
                let dic = {}
                for (let i = 0; i < this.rooms.length; i++) {
                    dic[this.rooms[i].id] = false
                }
                return dic
            },
        },
        methods: {
            async getRooms () {
                const result = await api.getObject('rooms')
                this.rooms = utils.observerArrayToArray(result['result'])
                this.filteredRooms = this.rooms;
                console.log("GET")
            },
            async deleteObj (id) {
                this.hideMenu(id)
                let res = await api.deleteObj('rooms', id)
                let snack = utils.handleResponse(res);
                this.$refs.snackComp.showSnack(snack.text, snack.color);
                this.getRooms()
            },
            hideMenu (id) {
                this.menuVisibility[id] = false
            },
            sendFilter(filter){
                if (filter === ""){
                    this.filteredRooms = this.rooms;
                } else {
                    let aux = [];
                    if (filter.length > this.activeFilter.length){
                        for (let i = 0; i < this.filteredRooms.length; i++){
                            if (this.filteredRooms[i].name.toLowerCase().includes(filter)){
                                aux.push(this.filteredRooms[i])
                            }
                        }
                    } else {
                        for (let i = 0; i < this.rooms.length; i++){
                            if (this.rooms[i].name.toLowerCase().includes(filter)){
                                aux.push(this.rooms[i])
                            }
                        }
                    }
                    this.filteredRooms = aux;
                }
                this.activeFilter = filter;
            }
        },
        created () {
            this.getRooms()
        },
    }
</script>
