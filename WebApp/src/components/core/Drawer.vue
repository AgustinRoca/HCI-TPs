<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">
    <v-navigation-drawer
        id="app-drawer"
        v-model="inputValue"
        :src="image"
        app
        color="grey darken-2"
        dark
        floating
        mobile-break-point="991"
        persistent
        width="16%"
    >
        <template v-slot:img="attrs">
            <v-img
                gradient="to top, rgba(0, 0, 0, .7), rgba(0, 0, 0, .7)"
                v-bind="attrs"></v-img>
        </template>

        <v-list-item two-line>
            <v-list-item-title class="title pa-2"
            >
                HOUSEITBA
            </v-list-item-title>
        </v-list-item>

        <v-divider class="mx-3 mb-3"/>

        <v-list nav>
            <!-- Bug in Vuetify for first child of v-list not receiving proper border-radius -->
            <div/>

            <v-list-item
                v-for="(link, i) in links"
                :key="i"
                :to="link.to"
                active-class="primary white--text"
            >
                <v-list-item-action>
                    <v-icon>{{ link.icon }}</v-icon>
                </v-list-item-action>

                <v-list-item-title v-text="link.text"/>
            </v-list-item>
        </v-list>

        <template v-slot:append>
            <v-list nav>
                <v-row
                    class="justify-space-around"
                >

                    <v-btn
                        icon
                        href="http://www.facebook.com"
                        target="_blank"
                    >
                        <v-icon color="tertiary">
                            mdi-facebook
                        </v-icon>
                    </v-btn>

                    <v-btn
                        icon
                        href="http://www.twitter.com"
                        target="_blank"
                    >
                        <v-icon color="tertiary">
                            mdi-twitter
                        </v-icon>
                    </v-btn>

                    <v-btn
                        icon
                        href="http://www.instagram.com"
                        target="_blank"
                    >
                        <v-icon color="tertiary">
                            mdi-instagram
                        </v-icon>
                    </v-btn>
                </v-row>
            </v-list>
        </template>
    </v-navigation-drawer>
</template>

<script>
    // Utilities
    import {
        mapMutations,
        mapState,
    } from 'vuex'

    export default {
        props: {
            opened: {
                type: Boolean,
                default: false,
            },
        },
        data: () => ({
            links: [
                {
                    to: '/',
                    icon: 'mdi-home',
                    text: 'Espacios',
                },
                {
                    to: '/Dispositivos',
                    icon: 'mdi-cellphone',
                    text: 'Dispositivos',
                },
                {
                    to: '/Rutinas',
                    icon: 'mdi-alarm',
                    text: 'Rutinas',
                },
                {
                    to: '/Favoritos',
                    icon: 'mdi-heart',
                    text: 'Favoritos',
                },
            ],
        }),

        computed: {
            ...mapState('app', ['image', 'color']),
            inputValue: {
                get () {
                    return this.$store.state.app.drawer
                },
                set (val) {
                    this.setDrawer(val)
                },
            },
        },

        methods: {
            ...mapMutations('app', ['setDrawer', 'toggleDrawer']),
        },
    }
</script>
