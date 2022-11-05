<template>
	<v-dialog scrollable v-model="visible" persistent max-width="600px">
		<!-- <template v-slot:activator="{ on }">
		</template>
		-->
		<v-card>
			<v-card-title>
				<span class="headline">{{ title }}</span>
			</v-card-title>
			<v-card-text>
				<slot></slot>
			</v-card-text>
			<v-card-actions>
				<div class="flex-grow-1"></div>
				<v-btn color="blue darken-1" text @click="hide()">Cerrar</v-btn>
				<v-btn v-if="isVisible" color="blue darken-1" text @click="create()">Agregar</v-btn>
			</v-card-actions>
		</v-card>
	</v-dialog>
</template>

<script>
    export default {
        name: 'CreateOverlay',
        props: ['title'],
        methods: {
            display:function () {
                this.visible = true;
            },
            hide:function(){
                this.$emit("onCloseModal");
                this.visible = false;
            },
			create:function(){
                this.$emit("onCreate")
                this.visible = false;
			},
            setCreateVisibility:function(isVisible){
                this.isVisible = isVisible;
            }
        },
        data () {
            return {
                visible: false,
                isVisible: true,
            }
        }
    }
</script>

<style scoped>

</style>
