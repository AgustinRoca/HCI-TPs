<template>
    <v-dialog scrollable v-model="visible" max-width="700px">
         <v-card>
                <v-container>
                    <v-row no-gutters>
                        <v-col cols="8">
                            <v-card-title>{{routine.name}}</v-card-title>
                        </v-col>
                        <v-col cols="2">
                            <v-btn medium class="play-button-inside"
                                color="white"
                                @click="executeRoutine()">

                                <v-icon color="light-blue">mdi-play</v-icon>
                            </v-btn>
                        </v-col>
                        <v-col cols="1">
                            <v-btn
                                large
                                color="white"
                                @click="deleteRoutine()"
                                class="delete-button"
                                icon>
                                <v-icon color="black lighten-1">mdi-delete</v-icon>
                            </v-btn>
                        </v-col>
                    </v-row>
                    <v-divider></v-divider>

                    <v-card flat v-for="action in routine.actions" :key="action.actionName" >
                        <v-container>
                            <v-layout row wrap class ="px-3 py-2 int-actions">
                                <v-flex xs4>
                                    <div class="caption grey--text">Dispositivo</div>
                                    <div>{{deviceIdMap[action.device.id]}}</div>
                                </v-flex>
                                <v-flex xs5>
                                    <div class="caption grey--text">Accion</div>
                                    <div>{{actionNameMap[action.actionName]}}</div>
                                </v-flex>
                                <v-flex xs3 v-if="action.params.length" >
                                    <div class="caption grey--text">Valor</div>
                                    <div>{{action.params[0]}}</div>
                                </v-flex>
                            </v-layout>
                        </v-container>
                    </v-card>
                </v-container>
            </v-card>


    </v-dialog>
</template>

<script>
    export default {
        name: 'RoutinesComponent',
         data () {
            return {
                routine:'',
                deviceIdMap:'',
                visible: false,
            }
        },
        methods: {

            display:function (rut,actionMap,deviceMap) {
                this.routine = rut;
                this.actionNameMap = actionMap;
                this.deviceIdMap = deviceMap;
                this.visible = true;
            },
            deleteRoutine:function(){
                this.$emit("onDelete");
                this.visible = false;
            },
            executeRoutine: function(){
                this.$emit("onExecuteRoutine")
            }
        }
    }

</script>