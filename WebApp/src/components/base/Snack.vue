<template>
    <v-snackbar
        v-model="snackbar"
        :color="snackcolor"
    >
        {{ snacktext }}
        <v-btn
            color="white"
            text
            @click="snackbar = false"
        >
            Cerrar
        </v-btn>
    </v-snackbar>
</template>

<script>
    export default {
        name: 'Snack',
        data: () => ({
            snackbar: false,
            snackcolor: 'red',
            snacktext: ''
        }),
        methods:{
            showSnack (text, color) {
                this.snackbar = true
                this.snacktext = text
                this.snackcolor = color
            },
        }
    }
</script>
