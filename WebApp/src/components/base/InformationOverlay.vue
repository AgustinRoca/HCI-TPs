<template>
    <v-dialog scrollable v-model="visible" persistent max-width="800px">
        <v-card>
            <v-card-title>
                <v-row no-gutters>
                    <v-col cols="9">
                        <span v-if="!edit" class="headline">{{ title }}</span>
                        <v-text-field
                            v-else
                            v-model="title"
                            label="Solo"
                            placeholder="Nombre"
                            solo
                            outlined
                            :value="title"
                        ></v-text-field>
                    </v-col>
                    <v-col cols="1">
                        <v-btn
                            v-if="!add"
                            fab
                            small
                            color="white"
                            @click="addDevice()"
                            icon>
                            <v-icon color="black lighten-1">mdi-plus</v-icon>
                        </v-btn>
                    </v-col>
                    <v-col cols="1">
                        <v-btn
                            v-if="!edit"
                            fab
                            small
                            color="white"
                            @click="startEdit()"
                            icon>
                            <v-icon color="black lighten-1">mdi-settings</v-icon>
                        </v-btn>
                        <v-btn
                            v-else
                            fab
                            small
                            color="white"
                            @click="endEdit()"
                            icon>
                            <v-icon color="black lighten-1">mdi-content-save</v-icon>
                        </v-btn>
                    </v-col>
                    <v-col cols="1">
                        <v-btn
                            fab
                            small
                            color="white"
                            @click="deleteRoom()"
                            icon>
                            <v-icon color="black lighten-1">mdi-delete</v-icon>
                        </v-btn>
                    </v-col>
                    <span class="subtitle-1">{{ subtitle }}</span>
                </v-row>
            </v-card-title>
            <v-card-text>
                <slot></slot>
            </v-card-text>
            <v-card-actions>
                <div class="flex-grow-1"></div>
                <v-btn color="blue darken-1" text @click="hide()">Cerrar</v-btn>
                <v-btn v-if="add" color="blue darken-1" text @click="create()">Agregar</v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script>
    export default {
        name: 'InformationOverlay',
        props: ['title', 'subtitle'],
        methods: {
            display:function () {
                this.visible = true;
            },
            deleteRoom:function(){
                this.$emit("onDelete");
                this.visible = false;
            },
            hide:function(){
                this.$emit("onCloseModal");
                this.visible = false;
                this.add = false;
                this.edit = false;
            },
            addDevice:function(){
                this.$emit("onAddDevice");
                this.add = true;
            },
            deviceAdded:function(){
                this.add = false;
            },
            startEdit:function(){
                this.edit = true
            },
            endEdit:function(){
                this.edit = false
                this.$emit("onSave", this.title);
            },
            create:function(){
                this.$emit("onCreate")
                this.add = false;
                this.edit = false;
            }
        },
        data () {
            return {
                visible: false,
                edit: false,
                add: false
            }
        }
    }
</script>
